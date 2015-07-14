package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.data.census.pums.*;
import com.pb.sawdust.data.census.pums.transform.PumaDataTableTransformation;
import com.pb.sawdust.data.census.pums.transform.PumaTablesTransformation;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * The {@code SimplePumaDataReaderSpec} ...
 *
 * @author crf
 *         Started 1/24/12 10:30 AM
 */
public class SimplePumaDataReaderSpec<H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
                                      P extends Enum<P> & PumaDataField.PumaDataPersonField> implements PumaDataReaderSpec<H,P> {
    private final PumaDataReader<?,H,P> reader;
    private final PumaDataDictionary<H,P> dictionary;
    private final Set<H> householdFields;
    private final Set<P> personFields;
    private final Constructor<? extends DataTable> tableConstructor;
    private final PumaTablesTransformation transformation;


    public SimplePumaDataReaderSpec(PumaDataReader<?,H,P> reader,
                                    PumaDataDictionary<H,P> dictionary,
                                    Set<H> householdFields, //can be null
                                    Set<P> personFields, //can be null
                                    Class<? extends DataTable> tableClass, //can be null, but getTable method must be overidden
                                    Set<Puma> allowedPumas, //can be null
                                    List<? extends PumaTablesTransformation> tableTransformations, //can be null
                                    Map<PumaDataType,Filter<Object[]>> lineFilter) { //can be null
        this.reader = reader;
        this.dictionary = dictionary;
        this.householdFields = Collections.unmodifiableSet(new LinkedHashSet<>(householdFields == null ? Collections.<H>emptySet() : householdFields));
        this.personFields = Collections.unmodifiableSet(new LinkedHashSet<>(personFields == null ? Collections.<P>emptySet() : personFields));
        try {
            tableConstructor = tableClass == null ? null : tableClass.getConstructor(TableReader.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("DataTable class must have constructor taking a TableReader as its sole argument: " + tableClass);
        }
        PumaTablesTransformation transformation = new PumaTablesTransformation(Collections.<PumaDataTableTransformation>emptyList());
        if (tableTransformations != null)
            for (PumaTablesTransformation tablesTransformation : tableTransformations)
                transformation = transformation.composite(tablesTransformation);
        this.transformation = transformation;
        if (lineFilter != null) {
            for (PumaDataType type : lineFilter.keySet()) {
                switch (type) {
                    case HOUSEHOLD : reader.addHouseholdDataFilter(lineFilter.get(type)); break;
                    case PERSON : reader.addPersonDataFilter(lineFilter.get(type)); break;
                }
            }
        }
        if (allowedPumas != null && !allowedPumas.isEmpty())
            reader.addAllowedPumas(allowedPumas);
    }

    @Override
    public PumaDataReader<?,H,P> getReader() {
        return reader;
    }

    @Override
    public PumaDataDictionary<H,P> getDictionary() {
        return dictionary;
    }

    @Override
    public Set<H> getHouseholdFields() {
        return householdFields;
    }

    public @Override
    Set<P> getPersonFields() {
        return personFields;
    }

    @Override
    public DataTable getTable(TableReader reader) {
        if (tableConstructor == null)
            throw new IllegalStateException("No table class specified; getTable method must be overridden.");
        try {
            return tableConstructor.newInstance(reader);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    @Override
    public PumaTablesTransformation getTransformation() {
        return transformation;
    }
}
