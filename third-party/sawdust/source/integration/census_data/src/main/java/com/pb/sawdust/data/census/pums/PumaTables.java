package com.pb.sawdust.data.census.pums;

import com.pb.sawdust.data.census.pums.spec.*;
import com.pb.sawdust.data.census.pums.transform.PumaDataGroupTransformation;
import com.pb.sawdust.model.builder.parser.yoube.YoubeParsedFunctionBuilder;
import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.basic.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.CsvTableReader;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.tabledata.transform.row.ContextDataRow;
import com.pb.sawdust.tabledata.transform.row.RowWiseDataTableTransformation;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code PumaTables} provides a structure which allows coordinated access to pair of PUMA household and person data tables.
 *
 * @author crf
 *         Started 1/20/12 10:26 AM
 */
public class PumaTables implements Iterable<PumaDataGroup> {
    private final DataTable householdTable;
    private final DataTable personTable;
    private final PumaDataDictionary<?,?> dataDictionary;

    /**
     * Constructor specifying the household and person tables, as well as the data dictionary they are associated with.
     *
     * @param householdTable
     *        The household data table.
     *
     * @param personTable
     *        The person data table.
     *
     * @param dataDictionary
     *        The data dictionary.
     */
    public PumaTables(DataTable householdTable, DataTable personTable, PumaDataDictionary<?,?> dataDictionary) {
        this.householdTable = householdTable;
        this.personTable = personTable;
        this.dataDictionary = dataDictionary;
    }

    /**
     * Get the data dictionary corresponding to these PUMA data tables.
     *
     * @return the data dictionary.
     */
    public PumaDataDictionary<?,?> getDataDictionary() {
        return dataDictionary;
    }

    /**
     * Get the household data table.
     *
     * @return the household data table.
     */
    public DataTable getHouseholdTable() {
        return householdTable;
    }

    /**
     * Get the person data table.
     *
     * @return the person data table.
     */
    public DataTable getPersonTable() {
        return personTable;
    }

    /**
     * Get an iterator over the rows in the household table. The rows will provide access to the {@link PumaDataGroup}
     * corresponding to the household row.
     *
     * @return an iterator over the rows in the household data table.
     */
    public Iterator<PumaDataRow> getHouseholdPumaDataRowIterator() {
        return getPumaDataRowIterator(this,true);
    }

    /**
     * Get an iterator over the rows in the person table. The rows will provide access to the {@link PumaDataGroup}
     * corresponding to the person row.
     *
     * @return an iterator over the rows in the person data table.
     */
    public Iterator<PumaDataRow> getPersonPumaDataRowIterator() {
        return getPumaDataRowIterator(this,false);
    }

    /**
     * Set the data coercion for the tables held by this instance.
     *
     * @param dataCoercion
     *        If {@code true}, then data in the tables will be coerced, otherwise it will not.
     */
    public void setDataCoercion(boolean dataCoercion) {
        householdTable.setDataCoersion(dataCoercion);
        personTable.setDataCoersion(dataCoercion);
    }

    private Iterator<PumaDataRow> getPumaDataRowIterator(PumaTables tables, final boolean householdIterator) {
        final Iterator<PumaDataGroup> groupIterator = tables.iterator();
        return new Iterator<PumaDataRow>() {
            private PumaDataGroup currentGroup = null;
            private DataRow next = null;
            private boolean done = true;
            private Iterator<DataRow> rowIterator;

            @Override
            public boolean hasNext() {
                if (next != null)
                    return true;
                if (done) {
                    if (groupIterator.hasNext())
                        currentGroup = groupIterator.next();
                    else
                        return false;
                    if (householdIterator) {
                        next = currentGroup.getHouseholdRow();
                        return true;
                    } else {
                        rowIterator = currentGroup.getPersonRows().iterator();
                        done = false;
                    }
                }
                if (rowIterator.hasNext()) {
                    next = rowIterator.next();
                    return true;
                } else {
                    done = true;
                    return hasNext();
                }
            }

            @Override
            public PumaDataRow next() {
                if (hasNext()) {
                    PumaDataRow pdr = new PumaDataRow(next,currentGroup);
                    next = null;
                    return pdr;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    @Override
    public Iterator<PumaDataGroup> iterator() {
        return new PumaDataGroupIterator(householdTable,personTable,dataDictionary.getHouseholdSerialIdField().getColumnName(),dataDictionary.getPersonSerialIdField().getColumnName());
    }

    /**
     * The {@code PumaDataRow} class is a data row for PUMA data tables which provides access to the row's corresponding
     * {@code PumaDataGroup}.
     */
    public static class PumaDataRow extends AbstractDataRow implements ContextDataRow<PumaDataGroup> {
        private final DataRow row;
        private final PumaDataGroup dataGroup;

        /**
         * Constructor specifying the data row and group.
         *
         * @param row
         *        The data row.
         *
         * @param dataGroup
         *        The PUMA data group corresponding to {@code row}.
         */
        public PumaDataRow(DataRow row, PumaDataGroup dataGroup) {
            super(row.willCoerceData());
            this.row = row;
            this.dataGroup = dataGroup;
        }

        @Override
        public PumaDataGroup getContext() {
            return dataGroup;
        }

        @Override
        public String[] getColumnLabels() {
            return row.getColumnLabels();
        }

        @Override
        public DataType[] getColumnTypes() {
            return row.getColumnTypes();
        }

        @Override
        public Object[] getData() {
            return row.getData();
        }
    }

    private class PumaDataGroupIterator implements Iterator<PumaDataGroup> {
        private final DataTable personTable;
        private final String hhSerialIdColumn;
        private final TableIndex<Long> personTableIndex;
        private final Iterator<DataRow> householdIterator;

        private PumaDataGroupIterator(DataTable householdTable, DataTable personTable, String hhSerialIdColumn, String personSerialIdColumn) {
            Map<Integer,DataType> hhMapping = new HashMap<>();
            Map<Integer,DataType> personMapping = new HashMap<>();
            hhMapping.put(householdTable.getColumnNumber(hhSerialIdColumn),DataType.LONG);
            personMapping.put(personTable.getColumnNumber(personSerialIdColumn),DataType.LONG);
            this.personTable = new CoercedDataTable(personTable,personMapping);
            this.hhSerialIdColumn = hhSerialIdColumn;
            personTableIndex = new SimpleTableIndex<>(this.personTable,personSerialIdColumn);
            personTableIndex.buildIndex();
            householdIterator = new CoercedDataTable(householdTable,hhMapping).iterator();
        }

        @Override
        public boolean hasNext() {
            return householdIterator.hasNext();
        }

        @Override
        public PumaDataGroup next() {
            DataRow row = householdIterator.next();
            int[] rowNumbers = null;
            try {
                rowNumbers = ArrayUtil.toIntArray(personTableIndex.getRowNumbers(row.getCellAsLong(hhSerialIdColumn)));
            } catch (TableDataException e) {
                //no person rows
            }
            DataTable personSubTable = rowNumbers == null ? new ListDataTable(personTable.getSchema()) : new PieceWiseDataTable(personTable,rowNumbers);
            return new PumaDataGroup(row,personSubTable);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Build a {@code PumaTables} instance from the information provided in a {@code PumaDataReaderSpec}.
     *
     * @param readerSpec
     *        The reader spec.
     *
     * @param <H>
     *        The type of the household fields of the returned tables.
     *
     * @param <P>
     *        The type of the person fields of the returned tables.
     *
     * @return a {@code PumaTables} instance built from the specifications held in {@code readerSpec}.
     */
    public static <H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
                   P extends Enum<P> & PumaDataField.PumaDataPersonField> PumaTables readGroup(PumaDataReaderSpec<H,P> readerSpec) {
        PumaDataReader<?,H,P> reader = readerSpec.getReader();
        reader.specifyHouseholdColumns(readerSpec.getHouseholdFields());
        reader.specifyPersonColumns(readerSpec.getPersonFields());
        PumaTables tables = new PumaTables(readerSpec.getTable(reader.getHouseholdTableReader()),readerSpec.getTable(reader.getPersonTableReader()),readerSpec.getDictionary());
        tables.setDataCoercion(true);
        tables = readerSpec.getTransformation().transformTables(tables);
        return tables;
    }

//    public static <H extends Enum<H> & PumaDataField.PumaDataHouseholdField,P extends Enum<P> & PumaDataField.PumaDataPersonField,D extends PumaDataDictionary<H,P>> PumaTables readGroup(D dataDictionary,PumaDataReader<?,H,P> reader, Class<? extends DataTable> tableClass) {
//        try {
//            Constructor<? extends DataTable> constructor = tableClass.getConstructor(tableClass);
//            DataTable hhTable = constructor.newInstance(reader.getHouseholdTableReader());
//            DataTable personTable = constructor.newInstance(reader.getPersonTableReader());
//            return new PumaTables(hhTable,personTable,dataDictionary);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalArgumentException("Table class must have constructor taking a TableReader as its only argument.");
//        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
//            throw new RuntimeWrappingException(e);
//        }
//    }

    public static <T extends RowWiseDataTableTransformation & PumaDataGroupTransformation> void main(String ... args) {
//        AcsDataDictionary<AcsDataDictionary2005_2009.HouseholdField,AcsDataDictionary2005_2009.PersonField> dictionary = AcsDataDictionary2005_2009.getDictionary();
//        Map<String,String> files = new HashMap<>();
//        files.put("D:/dump/downloads/reno_pums/ss05_09hnv.csv","D:/dump/downloads/reno_pums/ss05_09pnv.csv");
//        PumaDataReader reader = new AcsDataDictionary2005_2009.AcsDataReader2005_2009(files); //need to use raw type here to let household columns through...
////        reader.addAllowedPumas(getAvailablePumas());
//        reader.specifyHouseholdColumns(new HashSet<>(Arrays.asList(AcsDataDictionary2005_2009.HouseholdField.RT,
//                                           AcsDataDictionary2005_2009.HouseholdField.SERIALNO,
//                                           AcsDataDictionary2005_2009.HouseholdField.ST,
//                                           AcsDataDictionary2005_2009.HouseholdField.TYPE,
//                                           AcsDataDictionary2005_2009.HouseholdField.PUMA,
//                                           AcsDataDictionary2005_2009.HouseholdField.NP,
//                                           AcsDataDictionary2005_2009.HouseholdField.HINCP,
//                                           AcsDataDictionary2005_2009.HouseholdField.WGTP)));
//        reader.specifyPersonColumns( new HashSet<>(Arrays.asList(AcsDataDictionary2005_2009.PersonField.RT,
//                                           AcsDataDictionary2005_2009.PersonField.SERIALNO,
//                                           AcsDataDictionary2005_2009.PersonField.PWGTP,
//                                           AcsDataDictionary2005_2009.PersonField.AGEP,
//                                           AcsDataDictionary2005_2009.PersonField.ESR,
//                                           AcsDataDictionary2005_2009.PersonField.PERNP,
//                                           AcsDataDictionary2005_2009.PersonField.PINCP,
//                                           AcsDataDictionary2005_2009.PersonField.SCH)));
//
//        DataTable hhTable = new RowDataTable(new ColumnDataTable(reader.getHouseholdTableReader()));
//        DataTable personTable = new RowDataTable(new RowDataTable(reader.getPersonTableReader()));
//
//        PumaTables tables = new PumaTables(hhTable,personTable,dictionary);
//        tables.setDataCoercion(true);
//
////        PumaFunctionRowWiseColumnTransformation<Integer> transformation1 = new PumaFunctionRowWiseColumnTransformation<>(
////            AcsDataDictionary2005_2009.HouseholdField.HINCP,
////            DataType.INT,
////            new Function2<Integer,PumaDataGroup,Integer>() {
////                @Override
////                public Integer apply(Integer integer, PumaDataGroup pumaDataGroup) {
////                    int type = pumaDataGroup.getHouseholdRow().getCellAsInt(AcsDataDictionary2005_2009.HouseholdField.TYPE.getColumnName());
////                    if (type == 2 || type == 3)
////                        return -99999;
////                    return pumaDataGroup.getHouseholdRow().getCellAsInt(AcsDataDictionary2005_2009.HouseholdField.HINCP.getColumnName());
////                }
////            },dictionary);
////
////        PumaFunctionRowWiseColumnTransformation<Integer> transformation2 = new PumaFunctionRowWiseColumnTransformation<>(
////            AcsDataDictionary2005_2009.HouseholdField.NP,
////            DataType.INT,
////            new Function2<Integer,PumaDataGroup,Integer>() {
////                @Override
////                public Integer apply(Integer integer, PumaDataGroup pumaDataGroup) {
////                    int type = pumaDataGroup.getHouseholdRow().getCellAsInt(AcsDataDictionary2005_2009.HouseholdField.TYPE.getColumnName());
////                    if (type == 2 || type == 3)
////                        return -1;
////                    return pumaDataGroup.getHouseholdRow().getCellAsInt(AcsDataDictionary2005_2009.HouseholdField.HINCP.getColumnName());
////                }
////            },dictionary);
////        T t1 = SpecTransformationFunction.getTransformation(dictionary,AcsDataDictionary2005_2009.HouseholdField.HINCP,"-99999 + TYPE*0","TYPE == 2 || TYPE == 3");
//        T t1 = SpecTransformationFunction.getTransformation(dictionary,AcsDataDictionary2005_2009.HouseholdField.HINCP,"-99999","TYPE == 2 || TYPE == 3");
//        T t2 = SpecTransformationFunction.getTransformation(dictionary,AcsDataDictionary2005_2009.HouseholdField.NP,"-1","(TYPE == 2) || (TYPE == 3)");
////        T t2 = SpecTransformationFunction.getTransformation(dictionary,AcsDataDictionary2005_2009.HouseholdField.NP,"TYPE == 2 || TYPE == 3",null);
//
//
//
//        tables = new PumaTablesTransformation(Arrays.asList(new CompositePumaRowWiseDataTransformation(Arrays.asList(t1,t2)))).transformTables(tables);
//
////        tables = new PumaTablesTransformation(Arrays.asList(new CompositePumaRowWiseDataTransformation(Arrays.asList(transformation1,transformation2)))).transformTables(tables);
//
////    protected void transformPumaTables(DataTable hhTable, DataTable personTable) {
////        super.transformPumaTables(hhTable,personTable);
////        int counter = 0;
////        for (DataRow row : hhTable) {
////            int type = row.getCellAsInt(HouseholdField.TYPE.getColumnName());
////            if (type == 2 || type == 3) {
////                hhTable.setCellValue(counter,HouseholdField.HINCP.getColumnName(),-99999);
////                hhTable.setCellValue(counter,HouseholdField.NP.getColumnName(),-1);
////            }
////            counter++;
////        }
////    }

        DataTable specTable = new RowDataTable(new CsvTableReader("D:\\dump\\test_pums_reader_spec2.csv"));
        TablePumaDataReaderSpecReader reader = new TablePumaDataReaderSpecReader(new YoubeParsedFunctionBuilder(),specTable) {
            @Override
            protected DataTable getTable(TableReader reader) {
                return new RowDataTable(reader);
            }

            @Override
            protected PumaTables getJoinablePumaTables(PumaTables tables) {
                return new PumaTables(new RowDataTable(tables.getHouseholdTable()),new RowDataTable(tables.getPersonTable()),tables.getDataDictionary());
            }
        };
        PumaTables tables = reader.getPumaTables("nevada_puma");


        for (PumaDataGroup group : tables)
            System.out.println(group.semiDescriptiveToString());

    }
}
