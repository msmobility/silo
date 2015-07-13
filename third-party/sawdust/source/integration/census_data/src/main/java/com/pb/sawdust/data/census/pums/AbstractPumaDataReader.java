package com.pb.sawdust.data.census.pums;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.util.EmptyFilter;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.FilterChain;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * The {@code AbstractPumaDataReader} provides a skeletal implementation of the {@code PumaDataReader}. It takes care of
 * setting household and person fields, as well as developing and combining filters (line, data, PUMA, and 0-person household).
 * It only requires an implementing class to build the table readers, and provides access to the fields and filters via
 * {@code protected} "{@code get}" methods.
 *
 * @param <S>
 *       The type the line filters will act upon. This will usually be {@code String} or {@code String[]}, depending on
 *       the type of table reader used to build the tables.
 *
 * @param <H>
 *        The type of the household field this reader reads, as specified by the data dictionary type. This field must be
 *        an {@code enum}.
 *
 * @param <P>
 *        The type of the person field this reader reads, as specified by the data dictionary type. This field must be an
 *        {@code enum}.
 *
 * @param <D>
 *        The type of the data dictionary which defines the metadata about the files this reader reads.
 *
 * @author crf
 *         Started 10/13/11 6:05 PM
 */
public abstract class AbstractPumaDataReader<S,H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
                                               P extends Enum<P> & PumaDataField.PumaDataPersonField,
                                               D extends PumaDataDictionary<H,P>> implements PumaDataReader<S,H,P> {
    /**
     * A default name which can be used for the household tables read by implementing classes.
     */
    protected static final String HOUSEHOLD_TABLE_NAME = "Puma Data Household Table";

    /**
     * A default name which can be used for the person tables read by implementing classes.
     */
    protected static final String PERSON_TABLE_NAME = "Puma Data Person Table";

    private Set<H> householdFields = Collections.emptySet();
    private Set<P> personFields = Collections.emptySet();
    private final Set<H> requiredHouseholdFields;
    private final Set<P> requiredPersonFields;
    private FilterChain<S> householdLineFilter;
    private FilterChain<S> personLineFilter;
    private FilterChain<Object[]> householdDataFilter;
    private FilterChain<Object[]> personDataFilter;
    private final Set<Puma> allowedPumas = new HashSet<>();
    private FilterChain<Puma> pumaFilter;
    private Filter<Object[]> skipZeroPersonHouseholds;
    private TableReader householdReader;
    private TableReader personReader;
    private boolean modified = true;
    private final D dataDictionary;

    /**
     * Get the table readers built by this reader. The returned array should contain two elements: the household table
     * reader and the person table reader, in that order.
     *
     * @return an array holding the household and person table readers, in that order.
     */
    protected abstract TableReader[] getActualTableReaders();  //household,person

    /**
     * Constructor specifying the data dictionary corresponding to the files read by this instance.
     *
     * @param dataDictionary
     *        The data dictionary used by this reader.
     */
    public AbstractPumaDataReader(D dataDictionary) {
        clearPumaFilters();
        clearHouseholdFilters();
        clearPersonFilters();
        this.dataDictionary = dataDictionary;
        setSkipZeroPersonHouseholds(true);
        requiredHouseholdFields = EnumSet.noneOf(dataDictionary.getHouseholdFieldClass());
        requiredHouseholdFields.add(dataDictionary.getStateFipsField());
        requiredHouseholdFields.add(dataDictionary.getPumaField());
        requiredHouseholdFields.add(dataDictionary.getHouseholdSerialIdField());
        requiredHouseholdFields.add(dataDictionary.getPersonsField());
        requiredPersonFields = EnumSet.noneOf(dataDictionary.getPersonFieldClass());
        requiredPersonFields.add(dataDictionary.getPersonSerialIdField());
    }

    /**
     * Get te data dictionary used by this reader.
     *
     * @return this reader's data dictionary.
     */
    protected D getDataDictionary() {
        return dataDictionary;
    }

    /**
     * Get the set of household fields that will be included in household tables read by this reader.
     *
     * @return the set of selected household fields to be read.
     */
    protected Set<H> getHouseholdFields() {
        return householdFields;
    }

    /**
     * Get the set of person fields that will be included in person tables read by this reader.
     *
     * @return the set of selected person fields to be read.
     */
    protected Set<P> getPersonFields() {
        return personFields;
    }

    /**
     * Get the line filter which will be applied to lines in the file to identify accepted household file records.
     *
     * @return the line filter for the household table reader.
     */
    protected Filter<S> getHouseholdLineFilter() {
        return householdLineFilter;
    }

    /**
     * Get the line filter which will be applied to lines in the file to identify accepted person file records.
     *
     * @return the line filter for the person table reader.
     */
    protected Filter<S> getPersonLineFilter() {
        return personLineFilter;
    }

    private Filter<Object[]> buildHouseholdDataFilter(final Filter<Object[]> baseFilter) {
        return new Filter<Object[]>() {
            final int serialIdIndex = dataDictionary.getHouseholdSerialIdField().getColumnOrdinal();
            @Override
            public boolean filter(Object[] input) {
                boolean f = skipZeroPersonHouseholds.filter(input) && baseFilter.filter(input);
                long serialId = (Long) input[serialIdIndex];
                if (f)
                    validHouseholds.add(serialId);
                getHouseholdSerialIdBlock(serialId).countDown();
                return f;
            }
        };
    }

    /**
     * Get the data filter which will be applied to identify valid/accepted household table rows.
     *
     * @return the line filter for the household table reader.
     */
    protected Filter<Object[]> getHouseholdDataFilter() {
        final Filter<Puma> pumaFilter = getPumaFilter();
        if (pumaFilter == null)
            return buildHouseholdDataFilter(householdDataFilter);
        final int fipsIndex = dataDictionary.getStateFipsField().getColumnOrdinal();
        final int pumaIndex = dataDictionary.getPumaField().getColumnOrdinal();
        Filter<Object[]> hhPumaFilter = new Filter<Object[]>() {
            @Override
            public boolean filter(Object[] input) {
                return pumaFilter.filter(new Puma(((Number) input[fipsIndex]).intValue(),(String) input[pumaIndex]));
            }
        };
        FilterChain<Object[]> filter = new FilterChain<>(hhPumaFilter);
        filter.addFilter(householdDataFilter);
        return buildHouseholdDataFilter(filter);
    }

    private Filter<Object[]> buildPersonDataFilter(final Filter<Object[]> baseFilter) {
        return new Filter<Object[]>() {
            final int serialIdIndex = dataDictionary.getPersonSerialIdField().getColumnOrdinal();
            @Override
            public boolean filter(Object[] input) {
                long serialId = (Long) input[serialIdIndex];
                try {
                    getHouseholdSerialIdBlock(serialId).await();
                } catch (InterruptedException e) {
                    throw new RuntimeInterruptedException(e);
                }
                return validHouseholds.contains(serialId) && baseFilter.filter(input);
            }
        };
    }

    /**
     * Get the data filter which will be applied to identify valid/accepted person table rows.
     *
     * @return the line filter for the person table reader.
     */
    protected Filter<Object[]> getPersonDataFilter() {
        return buildPersonDataFilter(personDataFilter);
    }

    private Filter<Puma> getPumaFilter() {
        if (allowedPumas.size() == 0) {
            return pumaFilter;
        } else {
            FilterChain<Puma> f = new FilterChain<>(
                new Filter<Puma>() {
                    @Override
                    public boolean filter(Puma puma) {
                        return allowedPumas.contains(puma);
                    }
                }
            );
            if (pumaFilter != null)
                f.addFilter(pumaFilter);
            return f;
        }
    }

    @Override
    public void specifyHouseholdColumns(Set<H> columns) {
        householdFields = EnumSet.noneOf(dataDictionary.getHouseholdFieldClass());
        householdFields.addAll(columns);
        householdFields.addAll(requiredHouseholdFields);
        modified = true;
    }

    @Override
    public void specifyPersonColumns(Set<P> columns) {
        personFields = EnumSet.noneOf(dataDictionary.getPersonFieldClass());
        personFields.addAll(columns);
        personFields.addAll(requiredPersonFields);
        modified = true;
    }

    @Override
    public void addHouseholdLineFilter(Filter<S> filter) {
        householdLineFilter.addFilter(filter);
        modified = true;
    }

    @Override
    public void addHouseholdDataFilter(Filter<Object[]> filter) {
        householdDataFilter.addFilter(filter);
        modified = true;
    }

    @Override
    public void addPersonLineFilter(Filter<S> filter) {
        personLineFilter.addFilter(filter);
        modified = true;
    }

    @Override
    public void addPersonDataFilter(Filter<Object[]> filter) {
        personDataFilter.addFilter(filter);
        modified = true;
    }

    @Override
    public void clearHouseholdFilters() {
        householdLineFilter = new FilterChain<>();
        householdDataFilter = new FilterChain<>();
        modified = true;
    }

    @Override
    public void clearPersonFilters() {
        personLineFilter = new FilterChain<>();
        personDataFilter = new FilterChain<>();
        modified = true;
    }

    @Override
    public void addPumaFilter(Filter<Puma> filter) {
        if (pumaFilter == null)
            pumaFilter = new FilterChain<>();
        pumaFilter.addFilter(filter);
        modified = true;
    }

    @Override
    public void clearPumaFilters() {
        pumaFilter = null;
        modified = true;
    }

    @Override
    public void addAllowedPuma(Puma puma) {
        allowedPumas.add(puma);
        modified = true;
    }

    @Override
    public void addAllowedPumas(Collection<Puma> pumas) {
        allowedPumas.addAll(pumas);
        modified = true;
    }

    @Override
    public void setSkipZeroPersonHouseholds(boolean skipZeroPersonHouseholds) {
        if (skipZeroPersonHouseholds) {
            this.skipZeroPersonHouseholds = new Filter<Object[]>() {
                final int personsIndex = dataDictionary.getPersonsField().getColumnOrdinal();
                @Override
                public boolean filter(Object[] input) {
                    return (((Number) input[personsIndex]).intValue()) > 0;
                }
            };
        } else {
            this.skipZeroPersonHouseholds = new EmptyFilter<>();
        }
        modified = true;
    }

    private void updateReaders() {
        if (modified) {
            TableReader[] readers = getActualTableReaders();
            householdReader = readers[0];
            personReader = readers[1];
            modified = false;
        }
    }

    @Override
    public TableReader getHouseholdTableReader() {
        updateReaders();
        return householdReader;
    }

    @Override
    public TableReader getPersonTableReader() {
        updateReaders();
        return personReader;
    }

    private final Map<Long,CountDownLatch> householdSerialIdBlocks = new HashMap<>();
    private final Set<Long> validHouseholds = new HashSet<>();

    synchronized private CountDownLatch getHouseholdSerialIdBlock(Long serialId) {
        if (!householdSerialIdBlocks.containsKey(serialId))
            householdSerialIdBlocks.put(serialId,new CountDownLatch(1));
        return householdSerialIdBlocks.get(serialId);
    }

    /**
     * The {@code MultiFilePumsReader} class provides a simple {@code TableReader} which allows structurally identical tables
     * read by a series of {@code TableReader}s to be concatenated together into a single table. This can be useful for
     * constructing tables built from multiple PUMA/PUMS files.
     */
    protected class MultiFilePumsReader implements TableReader {
        private final TableReader[] readers;

        /**
         * Constructor identifying the readers which will read the tables which will be combined to form a single data
         * table by this class.
         *
         * @param readers
         *        The table readers for this reader.
         */
        public MultiFilePumsReader(Collection<TableReader> readers) {
            this(readers.toArray(new TableReader[readers.size()]));
        }

        private MultiFilePumsReader(TableReader ... readers) {
            this.readers = readers;
        }

        public String getTableName() {
            return readers[0].getTableName();
        }

        public String[] getColumnNames() {
            return readers[0].getColumnNames();
        }

        public DataType[] getColumnTypes() {
            return readers[0].getColumnTypes();
        }

        public Object[][] getData() {
            Object[][] masterData = new Object[0][];
            for (TableReader reader : readers) {
                Object[][] newData = reader.getData();
                Object[][] data = new Object[masterData.length + newData.length][];
                System.arraycopy(masterData,0,data,0,masterData.length);
                System.arraycopy(newData,0,data,masterData.length,newData.length);
                masterData = data;
            }
            return masterData;
        }
    }
}
