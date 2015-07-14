package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.model.builder.DataTableSource;
import com.pb.sawdust.model.builder.DataTableSources;
import com.pb.sawdust.model.builder.TensorSource;
import com.pb.sawdust.model.builder.TensorSources;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;

import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.property.PropertyDeluxe;

import java.nio.file.Paths;
import java.util.*;

/**
 * The {@code DataSourceTable} ...
 *
 * @author crf
 *         Started 5/29/12 5:57 AM
 */
public class DataSourceTable {
    public static final String DATA_TYPE_COLUMN_NAME = "data_type";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String DESCRIPTION_COLUMN_NAME = "description";
    public static final String SOURCE_COLUMN_NAME = "source";
    public static final String SOURCE_TYPE_COLUMN_NAME = "source_type";
    public static final String OPTIONS_COLUMN_NAME = "options";

    private static final Map<DataSourceType,TensorSourceBuilder> tensorSourceBuilderMap = new EnumMap<>(DataSourceType.class);
    private static final Map<DataSourceType,DataTableSourceBuilder> dataTableSourceBuilderMap = new EnumMap<>(DataSourceType.class);

    public static interface TensorSourceBuilder {
        TensorSource buildTensorSource(String dataType, String sourceType, String name, String source, Map<String,String> options);
    }

    public static interface DataTableSourceBuilder {
        DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options);
    }

    static {
        registerDataTableSourceBuilder(DataSourceType.CSV_TABLE,new DataTableSourceBuilder() {
            public DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
                return new DataTableSources.CsvDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
            }
        });
        registerDataTableSourceBuilder(DataSourceType.DBF_TABLE,new DataTableSourceBuilder() {
            public DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
                return new DataTableSources.DbfDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
            }
        });
        registerDataTableSourceBuilder(DataSourceType.EXCEL_TABLE,new DataTableSourceBuilder() {
            public DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
                return new DataTableSources.ExcelDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
            }
        });
        registerDataTableSourceBuilder(DataSourceType.TRANSCAD_TABLE,new DataTableSourceBuilder() {
            public DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
                return new DataTableSources.TranscadTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
            }
        });
        registerDataTableSourceBuilder(DataSourceType.JAVA_STATIC_TABLE,new JavaStaticDataTableSourceBuilder());

        registerTensorSourceBuilder(DataSourceType.CSV_TENSOR,new CsvTensorSourceBuilder());
        registerTensorSourceBuilder(DataSourceType.TRANSCAD_TENSOR,new TranscadTensorSourceBuilder());
        registerTensorSourceBuilder(DataSourceType.JAVA_STATIC_TENSOR,new JavaStaticTensorSourceBuilder());
    }

    public static void registerDataTableSourceBuilder(DataSourceType type, DataTableSourceBuilder builder) {
        if (type.getDataSourceDataType() != DataSourceDataType.DATATABLE)
            throw new IllegalArgumentException("DataTableSourceBuilder can only be registered for DATATABLE DataSourceType: " + type);
        dataTableSourceBuilderMap.put(type,builder);
    }

    public static void registerTensorSourceBuilder(DataSourceType type, TensorSourceBuilder builder) {
        if (type.getDataSourceDataType() != DataSourceDataType.TENSOR)
            throw new IllegalArgumentException("TensorSourceBuilder can only be registered for TENSOR DataSourceType: " + type);
        tensorSourceBuilderMap.put(type,builder);
    }

    private final Map<DataSourceDataType,Map<String,?>> dataSources = new EnumMap<>(DataSourceDataType.class);

    public DataSourceTable(DataTable dataSourceTable, PropertyDeluxe properties) {
        buildDataSourcesFromTable(dataSourceTable,properties);
    }

    @SuppressWarnings("unchecked") //? to DataTableSource cast is correct, as logic of class (should) enforce it
    public Map<String,DataTableSource> getDataTables() {
        return Collections.unmodifiableMap((Map<String,DataTableSource>) dataSources.get(DataSourceDataType.DATATABLE));
    }

    public Map<String,DataTableSource> getDataTables(List<String> tableNames) {
        Map<String,DataTableSource> tables = new HashMap<>();
        Map<String,DataTableSource> sourceTables = getDataTables();
        for (String tableName : tableNames) {
            if (!sourceTables.containsKey(tableName))
                throw new IllegalArgumentException("Table name not found: " + tableName);
            tables.put(tableName,sourceTables.get(tableName));
        }
        return tables;
    }

    @SuppressWarnings("unchecked") //? to TensorSource cast is correct, as logic of class (should) enforce it
    public Map<String,TensorSource> getTensors() {
        return Collections.unmodifiableMap((Map<String,TensorSource>) dataSources.get(DataSourceDataType.TENSOR));
    }

    public Map<String,TensorSource> getTensors(List<String> tensorNames) {
        Map<String,TensorSource> tables = new HashMap<>();
        Map<String,TensorSource> sourceTables = getTensors();
        for (String tensorName : tensorNames) {
            if (!sourceTables.containsKey(tensorName))
                throw new IllegalArgumentException("Tensor name not found: " + tensorName);
            tables.put(tensorName,sourceTables.get(tensorName));
        }
        return tables;
    }

    @SuppressWarnings("unchecked") //? to XX in builtSources cast is correct, as logic of class (should) enforce it
    private void buildDataSourcesFromTable(DataTable table, PropertyDeluxe properties) {
        for (DataRow row : table) {
            row = new DefTableUtil.PoundPropertyDataRow(row,properties);
            String name =row.getCellAsString(NAME_COLUMN_NAME);
            String dataType =row.getCellAsString(DATA_TYPE_COLUMN_NAME);
            String sourceType =row.getCellAsString(SOURCE_TYPE_COLUMN_NAME);
            String source =row.getCellAsString(SOURCE_COLUMN_NAME);
            DataSourceDataType dataSourceDataType = DataSourceDataType.getDataSourceDataType(dataType);
            DataSourceType dataSourceType = DataSourceType.getDataSourceType(dataSourceDataType,sourceType);
            Map<String,String> options = DefTableUtil.parseOptions(row.getCellAsString(OPTIONS_COLUMN_NAME));
            if (!dataSources.containsKey(dataSourceDataType))
                dataSources.put(dataSourceDataType,new HashMap<String,DataTableSource>());
            Map<String,?> builtSources = dataSources.get(dataSourceDataType);
            if (builtSources.containsKey(name))
                throw new IllegalStateException("Repeated data source name not allowed: " + name);
            switch (dataSourceDataType) {
                case DATATABLE : {
                    DataTableSourceBuilder builder = dataTableSourceBuilderMap.get(dataSourceType);
                    if (builder == null)
                        throw new IllegalStateException("DataTable data source builder not found for: " + dataSourceType);
                    ((Map<String,DataTableSource>) builtSources).put(name,builder.buildDataTableSource(dataType,sourceType,name,source,options));

//                    @SuppressWarnings("unchecked")
//                    Map<String,DataTableSource> sources = (Map<String,DataTableSource>) builtSources;
//                    DataTableSource sourceTable;
//                    switch (dataSourceType) {
//                        case CSV_TABLE : {
//                            sourceTable = new DataTableSources.CsvDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
//                        } break;
//                        case DBF_TABLE : {
//                            sourceTable = new DataTableSources.DbfDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
//                        } break;
//                        case EXCEL_TABLE : {
//                            sourceTable = new DataTableSources.ExcelDataTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
//                        } break;
//                        case TRANSCAD_TABLE : {
//                            sourceTable = new DataTableSources.TranscadTableSource(Paths.get(source),name,DefTableUtil.getTableBuilder(options));
//                        } break;
//                        case JAVA_STATIC_TABLE : {
//                            int splitPoint = source.lastIndexOf('.');
//                            if (splitPoint < 1)
//                                throw new IllegalStateException("Invalid class.method combination for static Java table: " + source);
//                            String className = source.substring(0,splitPoint);
//                            String methodName = source.substring(splitPoint + 1);
//                            sourceTable = new DataTableSources.JavaStaticDataTableSource(name,className,methodName);
//                        } break;
//                        default : throw new IllegalStateException("Should not be here: " + dataType + " " + sourceType + " " + name);
//                    }
//                    sources.put(name,sourceTable);
                } break;
                case TENSOR : {
                    TensorSourceBuilder builder = tensorSourceBuilderMap.get(dataSourceType);
                    if (builder == null)
                        throw new IllegalStateException("Tensor data source builder not found for: " + dataSourceType);
                    ((Map<String,TensorSource>) builtSources).put(name,builder.buildTensorSource(dataType,sourceType,name,source,options));

//                    TensorSource sourceTensor;
//                    switch (dataSourceType) {
//                        case CSV_TENSOR : {
//                            int[] dimensions = null;
//                            String tensorColumnName = null;
//                            for (Map.Entry<String,String> optionEntry : options.entrySet()) {
//                                String value = optionEntry.getValue().trim();
//                                switch (optionEntry.getKey().trim().toLowerCase()) {
//                                    case "dimensions" : {
//                                        try {
//                                            String[] dims = value.split(" ");
//                                            dimensions = new int[dims.length];
//                                            for (int i : range(dims.length))
//                                                dimensions[i] = Integer.parseInt(dims[i]);
//                                        } catch (Exception e) {
//                                            throw new RuntimeException("Invalid dimension specification for " + dataType + " " + sourceType + " " + name + ", must be spaced delimited list of integers: " + value,e);
//                                        }
//                                    } break;
//                                    case "tensor_column" : {
//                                        tensorColumnName = value;
//                                    } break;
//                                    case "tensor_factory" : break; //dealt with elsewhere
//                                    default : throw new IllegalStateException("Unknown option for " + dataType + " " + sourceType + " " + name + ": " + optionEntry.getKey());
//                                }
//                            }
//                            if (dimensions == null)
//                                throw new IllegalStateException("'dimensions' option for " + dataType + " " + sourceType + " " + name + " must be specified (space delimited list of integers)");
//                            if (tensorColumnName != null)
//                                sourceTensor = new TensorSources.CsvTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),dimensions,tensorColumnName);
//                            else
//                                sourceTensor = new TensorSources.CsvTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),dimensions);
//                        } break;
//                        case TRANSCAD_TENSOR : {
//                            String tensorNameInFile = null;
//                            int rowIndex = -1;
//                            int columnIndex = -1;
//                            for (Map.Entry<String,String> optionEntry : options.entrySet()) {
//                                String value = optionEntry.getValue().trim();
//                                switch (optionEntry.getKey().trim().toLowerCase()) {
//                                    case "row_index" : {
//                                        try {
//                                            rowIndex = Integer.parseInt(value);
//                                        } catch (NumberFormatException e) {
//                                            throw new RuntimeException("Invalid value for 'row_index': " + value,e);
//                                        }
//                                    } break;
//                                    case "column_index" : {
//                                        try {
//                                            columnIndex = Integer.parseInt(value);
//                                        } catch (NumberFormatException e) {
//                                            throw new RuntimeException("Invalid value for 'column_index': " + value,e);
//                                        }
//                                    } break;
//                                    case "tensor_name" : {
//                                        tensorNameInFile = value;
//                                    } break;
//                                    case "tensor_factory" : break; //dealt with elsewhere
//                                    default : throw new IllegalStateException("Unknown option for " + dataType + " " + sourceType + " " + name + ": " + optionEntry.getKey());
//                                }
//                            }
//                            boolean indexSpecified = rowIndex >= 0 && columnIndex >= 0;
//                            boolean tensorSpecified = tensorNameInFile != null;
//                            if (indexSpecified) {
//                                if (tensorSpecified) {
//                                    sourceTensor = new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),tensorNameInFile,rowIndex,columnIndex);
//                                } else {
//                                    sourceTensor = new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),name,rowIndex,columnIndex);
//                                }
//                            } else {
//                                if (tensorSpecified) {
//                                    sourceTensor = new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),tensorNameInFile);
//                                } else {
//                                    sourceTensor = new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options));
//                                }
//                            }
//                        } break;
//                        case JAVA_STATIC_TENSOR : {
//                            int splitPoint = source.lastIndexOf('.');
//                            if (splitPoint < 1)
//                                throw new IllegalStateException("Invalid class.method combination for static Java tensor: " + source);
//                            String className = source.substring(0,splitPoint);
//                            String methodName = source.substring(splitPoint + 1);
//                            sourceTensor = new TensorSources.JavaStaticTensorSource(name,className,methodName);
//                        } break;
//                        default : throw new IllegalStateException("Should not be here: " + dataType + " " + sourceType + " " + name);
//                    }
//                    sources.put(name,sourceTensor);
                } break;
            }
        }
    }

    public static enum DataSourceDataType {
        DATATABLE("datatable"),
        TENSOR("tensor","matrix");

        private final List<String> identifiers;

        private DataSourceDataType(String ... identifiers) {
            this.identifiers = Arrays.asList(identifiers);
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        public static DataSourceDataType getDataSourceDataType(String identifier) {
            for (DataSourceDataType dsdt : values())
                for (String dsdtIdentifier : dsdt.getIdentifiers())
                    if (dsdtIdentifier.equalsIgnoreCase(identifier))
                        return dsdt;
            throw new IllegalArgumentException("DataSourceDataType for identifier " + identifier + " not found");
        }
    }

    public static enum DataSourceType {
        CSV_TABLE(DataSourceDataType.DATATABLE,"csv"),
        DBF_TABLE(DataSourceDataType.DATATABLE,"dbf"),
        EXCEL_TABLE(DataSourceDataType.DATATABLE,"excel"),
        TRANSCAD_TABLE(DataSourceDataType.DATATABLE,"transcad"),
        JAVA_STATIC_TABLE(DataSourceDataType.DATATABLE,"java_static"),
        CSV_TENSOR(DataSourceDataType.TENSOR,"csv"),
        TRANSCAD_TENSOR(DataSourceDataType.TENSOR,"transcad"),
        JAVA_STATIC_TENSOR(DataSourceDataType.TENSOR,"java_static");

        private final DataSourceDataType dataSourceDataType;
        private final String identifier;

        private DataSourceType(DataSourceDataType dataSourceDataType, String identifier) {
            this.dataSourceDataType = dataSourceDataType;
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public DataSourceDataType getDataSourceDataType() {
            return dataSourceDataType;
        }

        public static DataSourceType getDataSourceType(DataSourceDataType dataSourceDataType,String identifier) {
            for (DataSourceType dst : values())
                if (dst.getDataSourceDataType() == dataSourceDataType && dst.getIdentifier().equalsIgnoreCase(identifier))
                    return dst;
            throw new IllegalArgumentException("DataSourceType for DataSourceDataType " + dataSourceDataType + " and identifier " + identifier + " not found");
        }
    }

    private static class CsvTensorSourceBuilder implements TensorSourceBuilder {
        @Override
        public TensorSource buildTensorSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
            int[] dimensions = null;
            String tensorColumnName = null;
            for (Map.Entry<String,String> optionEntry : options.entrySet()) {
                String value = optionEntry.getValue().trim();
                switch (optionEntry.getKey().trim().toLowerCase()) {
                    case "dimensions" : {
                        try {
                            String[] dims = value.split(" ");
                            dimensions = new int[dims.length];
                            for (int i : range(dims.length))
                                dimensions[i] = Integer.parseInt(dims[i]);
                        } catch (Exception e) {
                            throw new RuntimeException("Invalid dimension specification for " + dataType + " " + sourceType + " " + name + ", must be spaced delimited list of integers: " + value,e);
                        }
                    } break;
                    case "tensor_column" : {
                        tensorColumnName = value;
                    } break;
                    case "tensor_factory" : break; //dealt with elsewhere
                    default : throw new IllegalStateException("Unknown option for " + dataType + " " + sourceType + " " + name + ": " + optionEntry.getKey());
                }
            }
            if (dimensions == null)
                throw new IllegalStateException("'dimensions' option for " + dataType + " " + sourceType + " " + name + " must be specified (space delimited list of integers)");
            if (tensorColumnName != null)
                return new TensorSources.CsvTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),dimensions,tensorColumnName);
            else
                return new TensorSources.CsvTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),dimensions);
        }
    }


    private static class TranscadTensorSourceBuilder implements TensorSourceBuilder {
        @Override
        public TensorSource buildTensorSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
            String tensorNameInFile = null;
            int rowIndex = -1;
            int columnIndex = -1;
            for (Map.Entry<String,String> optionEntry : options.entrySet()) {
                String value = optionEntry.getValue().trim();
                switch (optionEntry.getKey().trim().toLowerCase()) {
                    case "row_index" : {
                        try {
                            rowIndex = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid value for 'row_index': " + value,e);
                        }
                    } break;
                    case "column_index" : {
                        try {
                            columnIndex = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid value for 'column_index': " + value,e);
                        }
                    } break;
                    case "tensor_name" : {
                        tensorNameInFile = value;
                    } break;
                    case "tensor_factory" : break; //dealt with elsewhere
                    default : throw new IllegalStateException("Unknown option for " + dataType + " " + sourceType + " " + name + ": " + optionEntry.getKey());
                }
            }
            boolean indexSpecified = rowIndex >= 0 && columnIndex >= 0;
            boolean tensorSpecified = tensorNameInFile != null;
            if (indexSpecified) {
                if (tensorSpecified) {
                    return new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),tensorNameInFile,rowIndex,columnIndex);
                } else {
                    return new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),name,rowIndex,columnIndex);
                }
            } else {
                if (tensorSpecified) {
                    return new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options),tensorNameInFile);
                } else {
                    return new TensorSources.TranscadNativeTensorSource(Paths.get(source),name,DefTableUtil.getTensorFactory(options));
                }
            }
        }
    }

    private static class JavaStaticTensorSourceBuilder implements TensorSourceBuilder {
        @Override
        public TensorSource buildTensorSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
            int splitPoint = source.lastIndexOf('.');
            if (splitPoint < 1)
                throw new IllegalStateException("Invalid class.method combination for static Java tensor: " + source);
            String className = source.substring(0,splitPoint);
            String methodName = source.substring(splitPoint + 1);
            return new TensorSources.JavaStaticTensorSource(name,className,methodName);
        }
    }

    private static class JavaStaticDataTableSourceBuilder implements DataTableSourceBuilder {
        @Override
        public DataTableSource buildDataTableSource(String dataType, String sourceType, String name, String source, Map<String,String> options) {
            int splitPoint = source.lastIndexOf('.');
            if (splitPoint < 1)
                throw new IllegalStateException("Invalid class.method combination for static Java table: " + source);
            String className = source.substring(0,splitPoint);
            String methodName = source.substring(splitPoint + 1);
            return new DataTableSources.JavaStaticDataTableSource(name,className,methodName);
        }
    }
}
