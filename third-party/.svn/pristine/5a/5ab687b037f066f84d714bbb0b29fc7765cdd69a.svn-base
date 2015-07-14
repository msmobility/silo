package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.calculator.ParameterizedNumericFunction;
import com.pb.sawdust.calculator.tabledata.ParameterizedDataRowFunction;
import com.pb.sawdust.data.census.pums.*;
import com.pb.sawdust.data.census.pums.transform.PumaColumnWiseDataTableTransformation;
import com.pb.sawdust.data.census.pums.transform.PumaDataRowsRemoval;
import com.pb.sawdust.data.census.pums.transform.PumaDataTableTransformation;
import com.pb.sawdust.data.census.pums.transform.PumaTablesTransformation;
import com.pb.sawdust.model.builder.parser.FunctionBuilder;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.tabledata.transform.column.DataColumnAddition;
import com.pb.sawdust.tabledata.transform.column.DataColumnCoercion;
import com.pb.sawdust.tabledata.transform.column.DataColumnRemoval;
import com.pb.sawdust.util.Filter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code TablePumaDataReaderSpec} ...
 *
 * @author crf
 *         Started 1/26/12 7:40 AM
 */
public abstract class TablePumaDataReaderSpecReader<H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
                                                    P extends Enum<P> & PumaDataField.PumaDataPersonField> implements PumaTablesReader {
    public static final String TYPE_COLUMN_NAME = "type";
    public static final String ENTRY_COLUMN_NAME = "entry";
    public static final String FILTER_COLUMN_NAME = "filter";
    public static final String DEFINITION_COLUMN_NAME = "transformation";

    public static final String DEFAULT_TABLE_NAME = "puma_table";

    public static final String DELETE_TABLE_PREFIX = "%%DELETE_TABLE%%";
    private static final Object DELETE_TABLE_SENTINAL = new Object();
    //type	entry	filter	transformation

    private final FunctionBuilder functionBuilder;
    private final Map<String,Object> specs = new LinkedHashMap<>();
    private final Map<String,PumaTables> tables = new HashMap<>();

    public TablePumaDataReaderSpecReader(FunctionBuilder functionBuilder, DataTable specTable) {
        this.functionBuilder = functionBuilder;
        parseTable(specTable);
    }

    abstract protected DataTable getTable(TableReader reader);
    abstract protected PumaTables getJoinablePumaTables(PumaTables tables);

    protected String getFile(String fileEntry) {
        return fileEntry;
    }

    public synchronized PumaTables getPumaTables(String name) {
        if (!specs.containsKey(name))
            throw new IllegalArgumentException("Spec name not found: " + name);
        if (tables.size() == 0)
            loadTables();
        return tables.get(name);
    }

    @SuppressWarnings("unchecked") //strange cast here is correct, and internally enforced
    private void loadTables() {
        for (String t : specs.keySet()) {
            Object o = specs.get(t);
            if (o instanceof PumaDataReaderSpec) {
                tables.put(t,PumaTables.readGroup((PumaDataReaderSpec<H,P>) o));
            } else if (o == DELETE_TABLE_SENTINAL) {
                tables.remove(t.substring(DELETE_TABLE_PREFIX.length()));
            } else { //join
                String[] tableNames = (String[]) ((Object[]) o)[0];
                PumaTables pt = getJoinablePumaTables(tables.get(tableNames[0]));
                pt.setDataCoercion(true);
                DataTable hh = pt.getHouseholdTable();
                DataTable person = pt.getPersonTable();
                for (int i = 1; i < tableNames.length; i++) {
                    for (PumaDataGroup group : tables.get(tableNames[i])) {
                        hh.addRow(group.getHouseholdRow());
                        for (DataRow pRow : group.getPersonRows())
                            person.addRow(pRow);
                    }
                }
                Object transformation = ((Object[]) o)[1];
                if (transformation != null)
                    pt = ((PumaTablesTransformation) transformation).transformTables(pt);
                tables.put(t,pt);
            }
        }
    }

    private String parsePumaRowTableName(DataRow row) {
        String tableName = row.getCellAsString(ENTRY_COLUMN_NAME);
        if (tableName.equals(""))
            tableName = DEFAULT_TABLE_NAME;
        return tableName;
    }

    private PumaData parsePumaRowPumaData(DataRow row, PumaData currentPumaData, String currentTable) {
        String entry = row.getCellAsString(FILTER_COLUMN_NAME).toUpperCase();
        PumaData newPumaData = entry.equals("") ? currentPumaData : PumaData.valueOf(entry);
        if (currentPumaData != null && currentPumaData != newPumaData)
            throw new IllegalStateException(String.format("Definitions for a single puma table (%s) must all have the same puma data type: %s vs. %s",currentTable,currentPumaData,newPumaData));
        return newPumaData;
    }

    private Map<String,String> parsePumaRowFiles(DataRow row) {
        Map<String,String> fileMap = new HashMap<>();
        String files = row.getCellAsString(DEFINITION_COLUMN_NAME);
        if (!files.equals("")) {
            String[] filePair = files.split(";");
            String file1 = getFile(filePair[0]);
            switch (filePair.length) {
                case 1 : fileMap.put(file1,file1); break;
                case 2 : fileMap.put(file1,getFile(filePair[1])); break;
                default : throw new IllegalStateException("At most two files may be specified for a puma table line: " + Arrays.toString(filePair));
            }
        }
        return fileMap;
    }

    @SuppressWarnings("fallthrough") //the switch statements are complex and intended to fallthrough at certain points
    private void parseTable(DataTable table) {
        String currentTable = null;
        PumaData currentPumaData = null;
        Map<String,String> fileMap = new HashMap<>();
        Set<H> householdEntries = new LinkedHashSet<>();
        Set<P> personEntries = new LinkedHashSet<>();
        List<PumaDataTableTransformation> transformations = new LinkedList<>();
        Set<Puma> pumaSet = new HashSet<>();
        Map<PumaDataType,Filter<Object[]>> lineFilters = new EnumMap<>(PumaDataType.class);
        Map<String,PumaData> tablePumaDataMap = new HashMap<>();
        boolean inJoin = false;
        for (DataRow row : table) {
            PumaDataType transformationType = PumaDataType.HOUSEHOLD;
            switch (TypeType.getTypeTypeForName(row.getCellAsString(TYPE_COLUMN_NAME))) {
                case PUMA : {
                    switch (EntryType.getEntryTypeForName(row.getCellAsString(ENTRY_COLUMN_NAME))) {
                        case PUMA : {
                            int stateFips = Integer.parseInt(row.getCellAsString(FILTER_COLUMN_NAME).trim());
                            for (String s : row.getCellAsString(DEFINITION_COLUMN_NAME).split(";"))
                                pumaSet.add(new Puma(stateFips,Integer.parseInt(s.trim())));
                        } break;
                        case OTHER : {
                            String tableName = parsePumaRowTableName(row);
                            if (currentTable == null) {
                                currentTable = tableName;
                            } else if (!currentTable.equals(tableName)) {
                                if (inJoin) {
                                    ((Object[]) specs.get(currentTable))[1] = new PumaTablesTransformation(transformations);
                                    inJoin = false;
                                } else {
                                    addToSpecs(currentTable,new TablePumaDataReaderSpec(currentPumaData,householdEntries,personEntries,fileMap,transformations,pumaSet,lineFilters));
                                    tablePumaDataMap.put(currentTable,currentPumaData);
                                }
                                householdEntries.clear();
                                personEntries.clear();
                                fileMap.clear();
                                transformations.clear();
                                pumaSet.clear();
                                lineFilters.clear();
                                currentTable = tableName;
                                currentPumaData = null;
                            }
                            switch (FilterType.getFilterTypeForName(row.getCellAsString(FILTER_COLUMN_NAME))) {
                                case JOIN: {
                                    String[] tables = row.getCellAsString(DEFINITION_COLUMN_NAME).split(";");
                                    if (tables.length < 2)
                                        throw new IllegalStateException(String.format("Join (%s) must specify at least two tables: %s",currentTable,Arrays.toString(tables)));
                                    currentPumaData = null;
                                    for (String t : tables) {
                                        if (!specs.containsKey(t))
                                            throw new IllegalStateException("Table to join not found: " + t);
                                        else if (currentPumaData == null)
                                            currentPumaData = tablePumaDataMap.get(t);
                                        else if (currentPumaData != tablePumaDataMap.get(t))
                                            throw new IllegalStateException(String.format("All joined tables (for %s) must have identical PumaData types: %s vs %s",currentTable,currentPumaData,tablePumaDataMap.get(t)));
                                    }
                                    addToSpecs(currentTable,new Object[]{tables,null});
                                    tablePumaDataMap.put(currentTable,currentPumaData);
                                    inJoin = true;
                                } break;
                                case DROP : {
                                    if (!specs.containsKey(currentTable))
                                        throw new IllegalStateException("Table to drop not found: " + currentTable);
                                    addToSpecs(DELETE_TABLE_PREFIX + currentTable,DELETE_TABLE_SENTINAL);
                                    currentTable = null;
                                } break;
                                case TYPE :
                                case NEW_BYTE :
                                case NEW_SHORT :
                                case NEW_INT :
                                case NEW_LONG :
                                case NEW_FLOAT :
                                case NEW_DOUBLE :
                                case DELETE : throw new IllegalStateException("Invalid filter for puma entry: " + FilterType.getFilterTypeForName(row.getCellAsString(FILTER_COLUMN_NAME)));
                                case OTHER : {
                                    currentPumaData = parsePumaRowPumaData(row,currentPumaData,currentTable);
                                    fileMap.putAll(parsePumaRowFiles(row));
                                } break;
                            }
                        } break;
                    }
                } break;
                case PERSON : transformationType = PumaDataType.PERSON;
                case HOUSEHOLD : {
                    if (currentPumaData == null)
                        throw new IllegalStateException("Must define puma data type before performing table specifications: " + currentTable);
                    @SuppressWarnings("unchecked") //this should be ok
                    PumaDataDictionary<H,P> dictionary = (PumaDataDictionary<H,P>) currentPumaData.getDictionary();
                    String column = row.getCellAsString(ENTRY_COLUMN_NAME);
                    boolean isPumaColumn = true;
                    try {
                        switch (transformationType) {
                            case HOUSEHOLD :  {
                                H field = Enum.valueOf(dictionary.getHouseholdFieldClass(),column);
                                householdEntries.add(field);
                            } break;
                            case PERSON : {
                                P field = Enum.valueOf(dictionary.getPersonFieldClass(),column);
                                personEntries.add(field);
                            } break;
                        }
                    } catch (IllegalArgumentException e) {
                        isPumaColumn = false;
                    }
                    DataType newType = null;
                    switch (FilterType.getFilterTypeForName(row.getCellAsString(FILTER_COLUMN_NAME))) {
                        case TYPE : {
                            DataType type = DataType.valueOf(row.getCellAsString(DEFINITION_COLUMN_NAME).toUpperCase());
                            transformations.add(new PumaColumnWiseDataTableTransformation(new DataColumnCoercion(column,type),dictionary,transformationType));
                        } break;
                        //if statements avoid breaks so we can fall through to the bottom
                        case NEW_BYTE : if (newType == null) newType = DataType.BYTE;
                        case NEW_SHORT : if (newType == null) newType = DataType.SHORT;
                        case NEW_INT : if (newType == null) newType = DataType.INT;
                        case NEW_LONG : if (newType == null) newType = DataType.LONG;
                        case NEW_FLOAT : if (newType == null) newType = DataType.FLOAT;
                        case NEW_DOUBLE : if (newType == null) newType = DataType.DOUBLE;
                        {
                            if (isPumaColumn)
                                throw new IllegalStateException("Cannot add a new column which is already a puma column name: " + column);
                            String definition = row.getCellAsString(DEFINITION_COLUMN_NAME);
                            if (definition.trim().equals(""))
                                definition = "0.0";
                            ParameterizedNumericFunction function = functionBuilder.buildFunction(definition);
                            transformations.add(new PumaColumnWiseDataTableTransformation(new DataColumnAddition(column,newType,new ParameterizedDataRowFunction(function,newType)),dictionary,transformationType));
                        } break;
                        case FILTER_ROW : {
                            throw new IllegalStateException("filter_row not implemented at this time.");
//                            String definition = row.getCellAsString(DEFINITION_COLUMN_NAME);
//                            FunctionBuilder.ParameterizedNumericFunction function = functionBuilder.buildFunction(definition);
//                            Filter<Object[]> filter = null;
//                            //todo: map column names to object reference
//                            switch (transformationType) {
//                                case HOUSEHOLD :  {
//
//                                } break;
//                                case PERSON : {
//
//                                } break;
//                            }
//                            if (lineFilters.containsKey(transformationType))
//                                lineFilters.put(transformationType,new FilterChain<>(Arrays.asList(lineFilters.get(transformationType),filter)));
//                            else
//                                lineFilters.put(transformationType,filter);
                        }
                        case DELETE_ROW : {
                            String definition = row.getCellAsString(DEFINITION_COLUMN_NAME);
                            final ParameterizedDataRowFunction function = new ParameterizedDataRowFunction(functionBuilder.buildFunction(definition));
                            final PumaDataType tType = transformationType;
                            Filter<PumaTables.PumaDataRow> filter = new Filter<PumaTables.PumaDataRow>() {
                                private volatile PumaDataRowGenerator rowGenerator;
                                private final AtomicBoolean generatorBuilt = new AtomicBoolean(false);

                                private synchronized void buildGenerator(PumaTables.PumaDataRow row) {
                                    if (generatorBuilt.get())
                                        return;
                                    rowGenerator = new PumaDataRowGenerator(row,tType);
                                }

                                @Override
                                public boolean filter(PumaTables.PumaDataRow row) {
                                    if (!generatorBuilt.get())
                                        buildGenerator(row);
                                    return function.applyInt(rowGenerator.getRow(row)) != 0;
                                }
                            };
                            PumaDataRowsRemoval removal = new PumaDataRowsRemoval(dictionary,transformationType,filter);
                            transformations.add(removal);
                            if (transformationType == PumaDataType.HOUSEHOLD)
                                transformations.add(removal.getComplementaryRemoval()); //todo: should we do persons as well, by default?
                        } break;
                        case DELETE : {
                            transformations.add(new PumaColumnWiseDataTableTransformation(new DataColumnRemoval(column),dictionary,transformationType));
                        } break;
                        case JOIN :
                        case DROP : {
                            throw new IllegalStateException("Invalid filter entry for household/person row: " + row.getCellAsString(FILTER_COLUMN_NAME));
                        }
                        case OTHER: {
                            String transformation = row.getCellAsString(DEFINITION_COLUMN_NAME);
                            if (!transformation.trim().equals("")) {
                                String filter = row.getCellAsString(FILTER_COLUMN_NAME);
                                if (filter.trim().equals(""))
                                    filter = null;
                                transformations.add(SpecPumaTransformationFunction.getTransformation(dictionary,column,functionBuilder.buildFunction(transformation),filter == null ? null : functionBuilder.buildFunction(filter),transformationType));
                            }
                        } break;
                    }
                } break;
            }
        }
        if (currentTable != null)
            addToSpecs(currentTable,new TablePumaDataReaderSpec(currentPumaData,householdEntries,personEntries,fileMap,transformations,pumaSet,lineFilters));
    }

    private void addToSpecs(String name, Object o) {
        if (specs.containsKey(name))
            throw new IllegalArgumentException("Repeated table name: " + name);
        specs.put(name,o);
    }

    private class TablePumaDataReaderSpec extends SimplePumaDataReaderSpec<H,P> {

        @SuppressWarnings("unchecked") //these internal casts are correct
        private TablePumaDataReaderSpec(PumaData pumaData, Set<H> householdFields, Set<P> personFields, Map<String,String> files, List<PumaDataTableTransformation> transformations, Set<Puma> allowedPumas, Map<PumaDataType,Filter<Object[]>> lineFilter) {
            super((PumaDataReader<?,H,P>) pumaData.getReader(files),(PumaDataDictionary<H,P>) pumaData.getDictionary(),householdFields,personFields,null,allowedPumas,Arrays.asList(new PumaTablesTransformation(transformations)),lineFilter);
        }

        @Override
        public DataTable getTable(TableReader reader) {
            DataTable table = TablePumaDataReaderSpecReader.this.getTable(reader);
            table.setDataCoersion(true);
            return table;
        }
    }

//    private class TablePumaDataReaderSpec<H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
//                                           P extends Enum<P> & PumaDataField.PumaDataPersonField> implements PumaDataReaderSpec<H,P> {
//        private final PumaData pumaData;
//        private final Set<H> householdFields;
//        private final Set<P> personFields;
//        private final Map<String,String> files;
//        private final PumaTablesTransformation transformation;
//
//        private TablePumaDataReaderSpec(PumaData pumaData, Set<H> householdFields, Set<P> personFields, Map<String,String> files, List<PumaDataTableTransformation> transformations) {
//            this.pumaData = pumaData;
//            this.householdFields = new LinkedHashSet<>(householdFields);
//            this.personFields = new LinkedHashSet<>(personFields);
//            this.files = new HashMap<>(files);
//            this.transformation = new PumaTablesTransformation(transformations);
//        }
//
//        @Override
//        @SuppressWarnings("unchecked") //this is ok, we are being loose with generics here anyway
//        public PumaDataReader<?,H,P> getReader() {
//            return (PumaDataReader<?,H,P>) pumaData.getReader(files);
//        }
//
//        @Override
//        @SuppressWarnings("unchecked") //this is ok, we are being loose with generics here anyway
//        public PumaDataDictionary<H,P> getDictionary() {
//            return (PumaDataDictionary<H,P>) pumaData.getDictionary();
//        }
//
//        @Override
//        public Set<H> getHouseholdFields() {
//            return householdFields;
//        }
//
//        @Override
//        public Set<P> getPersonFields() {
//            return personFields;
//        }
//
//        @Override
//        public DataTable getTable(TableReader reader) {
//            return TablePumaDataReaderSpecReader.this.getTable(reader);
//        }
//
//        @Override
//        public PumaTablesTransformation getTransformation() {
//            return transformation;
//        }
//    }

    private static enum TypeType {
        PUMA("puma"),
        HOUSEHOLD("household","hh"),
        PERSON("person","p");

        private final Set<String> typeNames;

        private TypeType(String ... typeNames) {
            this.typeNames = new HashSet<>(Arrays.asList(typeNames));
        }

        private static Set<String> getAllPossibleTypeNames() {
            Set<String> names = new HashSet<>();
            for (TypeType type : TypeType.values())
                names.addAll(type.typeNames);
            return names;
        }

        private static TypeType getTypeTypeForName(String name) {
            name = name.toLowerCase();
            for (TypeType type : TypeType.values())
                for (String typeName : type.typeNames)
                    if (typeName.equals(name))
                        return type;
            throw new IllegalArgumentException("Type not found for name " + name + "; valid options are " + getAllPossibleTypeNames());
        }
    }
    private static enum EntryType {
        PUMA("puma"),
        OTHER();

        private final Set<String> typeNames;

        private EntryType(String ... typeNames) {
            this.typeNames = new HashSet<>(Arrays.asList(typeNames));
        }

        private static Set<String> getAllPossibleTypeNames() {
            Set<String> names = new HashSet<>();
            for (EntryType type : EntryType.values())
                names.addAll(type.typeNames);
            return names;
        }

        private static EntryType getEntryTypeForName(String name) {
            name = name.toLowerCase();
            for (EntryType type : EntryType.values())
                for (String typeName : type.typeNames)
                    if (typeName.equals(name))
                        return type;
            return OTHER;
        }
    }

    private static enum FilterType {
        TYPE("type"),
        NEW_BYTE("new_byte"),
        NEW_SHORT("new_short"),
        NEW_INT("new_int"),
        NEW_LONG("new_long"),
        NEW_FLOAT("new_float"),
        NEW_DOUBLE("new_double","new"),
        DELETE("delete"),
        FILTER_ROW("filter_row"), //not implemented: complicated because it acts on object[] data, not a data row (table not created yet...)
        DELETE_ROW("delete_row"),
        JOIN("join"),
        DROP("drop"),
        OTHER();

        private final Set<String> typeNames;

        private FilterType(String ... typeNames) {
            this.typeNames = new HashSet<>(Arrays.asList(typeNames));
        }

        private static Set<String> getAllPossibleTypeNames() {
            Set<String> names = new HashSet<>();
            for (FilterType type : FilterType.values())
                names.addAll(type.typeNames);
            return names;
        }

        private static FilterType getFilterTypeForName(String name) {
            name = name.toLowerCase();
            for (FilterType type : FilterType.values())
                for (String typeName : type.typeNames)
                    if (typeName.equals(name))
                        return type;
            return OTHER;
        }
    }
}
