package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.model.builder.DataProviderBuilder;
import com.pb.sawdust.model.builder.DataTableSource;
import com.pb.sawdust.model.builder.IndexProviderSource;
import com.pb.sawdust.model.builder.TensorSource;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.DataTableDataProvider;
import com.pb.sawdust.model.models.provider.DataTableReferenceDataProvider;
import com.pb.sawdust.model.models.provider.VariableRenamingDataProvider;
import com.pb.sawdust.model.models.provider.tensor.DataProviderIndexProvider;
import com.pb.sawdust.model.models.provider.tensor.IndexProvider;
import com.pb.sawdust.model.models.provider.tensor.TensorDataProvider;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.factory.TensorFactory;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.property.PropertyDeluxe;

import java.util.*;

/**
 * The {@code DataLinkTable} ...
 *
 * @author crf
 *         Started 5/29/12 9:46 AM
 */
public class DataLinkTable {
    public static final String DATA_TYPE_COLUMN_NAME = "type";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String DESCRIPTION_COLUMN_NAME = "description";
    public static final String SOURCE_COLUMN_NAME = "source";
    public static final String SOURCE_REFERENCE_COLUMN_NAME = "source_reference";
    public static final String PROVIDER_REFERENCE_COLUMN_NAME = "provider_reference";
    public static final String OPTIONS_COLUMN_NAME = "options";
    public static final String REFERENCE_TABLE_MISSING_VALUE_OPTION_KEY = "missing_value";

    private final Map<String,DataProviderBuilder> dataProviderBuilders = new HashMap<>();
    private final Map<String,String> requiredSources = new HashMap<>();

    public DataLinkTable(DataTable table, Map<String,DataTableSource> baseTableSources, Map<String,TensorSource> baseTensorSources, DataProvider baseProvider, TensorFactory factory, PropertyDeluxe properties) {
        buildProvidersBuilders(table,baseTableSources,baseTensorSources,baseProvider,factory,properties);
    }

    public Set<String> getRequiredSources(Set<String> dataLinkNames) {
        Set<String> sources = new HashSet<>();
        for (String dataLinkName : dataLinkNames) {
            if (!requiredSources.containsKey(dataLinkName))
                throw new IllegalArgumentException("Data link (table) name not found: " + dataLinkName);
            sources.add(requiredSources.get(dataLinkName));
        }
        return sources;
    }

    public Map<String,DataProviderBuilder> getProviderBuilders() {
        return Collections.unmodifiableMap(dataProviderBuilders);
    }

    private void buildProvidersBuilders(DataTable table, Map<String,DataTableSource> baseTableSources, Map<String,TensorSource> baseTensorSources, final DataProvider baseProvider, TensorFactory factory, PropertyDeluxe properties) {
        for (String tableName : baseTableSources.keySet()) {
            if (dataProviderBuilders.containsKey(tableName))
                throw new IllegalStateException("Table name cannot be repeated: " + tableName);
            dataProviderBuilders.put(tableName,new DataTableProviderBuilder(baseTableSources.get(tableName),factory));
        }
        for (DataRow row : table) {
            row = new DefTableUtil.PoundPropertyDataRow(row,properties);
            DataType type = DataType.getDataSourceDataType(row.getCellAsString(DATA_TYPE_COLUMN_NAME));
            String name = row.getCellAsString(NAME_COLUMN_NAME);
            String source = row.getCellAsString(SOURCE_COLUMN_NAME);
            String sourceReference = row.getCellAsString(SOURCE_REFERENCE_COLUMN_NAME);
            String providerReference = row.getCellAsString(PROVIDER_REFERENCE_COLUMN_NAME);
            Map<String,String> options = DefTableUtil.parseOptions(row.getCellAsString(OPTIONS_COLUMN_NAME));

            if (dataProviderBuilders.containsKey(name))
                throw new IllegalStateException("(Reference) table name cannot be repeated: " + name);
            switch (type) {
                case DATATABLE : {
                    if (!baseTableSources.containsKey(source))
                        throw new IllegalArgumentException("(Reference) table not found in data sources: " + source);
                    dataProviderBuilders.put(name,new DataTableReferenceProviderBuilder(name,baseTableSources.get(source),factory,sourceReference,providerReference,baseProvider,options));
                    requiredSources.put(name,source);
                } break;
                case TENSOR : {
                    if (!baseTensorSources.containsKey(source))
                        throw new IllegalArgumentException("(Reference) tensor not found in data sources: " + source);
                    String[] sourceLookup = sourceReference.trim().split(" ");
                    final List<String> indexVariables = Arrays.asList(providerReference.trim().split(" "));
                    if (sourceLookup.length != indexVariables.size())
                        throw new IllegalArgumentException(SOURCE_REFERENCE_COLUMN_NAME + " and " + PROVIDER_REFERENCE_COLUMN_NAME + " index count lengths must be the same: " + name);
                    final TensorSource tensorSource = baseTensorSources.get(source);
//                    final Map<Integer,List<Integer>> idMap = new HashMap<>();
                    final Set<Integer> idDimensions = new HashSet<>();
                    final Integer[] missingValues = new Integer[sourceLookup.length];
                    String[] mv = options.containsKey("missing_value") ? options.get("missing_value").trim().split(" ") : null;
                    if (mv!= null && sourceLookup.length != mv.length)
                        throw new IllegalArgumentException(String.format("Incorrect dimension count for 'missing_value' option in %s: found %d, should be %d",name,mv.length,sourceLookup.length));
                    for (int i : range(sourceLookup.length)) {
                        if (sourceLookup[i].toLowerCase().equals("id"))
                            //idMap.put(i,(List<Integer>) tensorSource.getTensor().getIndex().getIndexIds().get(i));
                            idDimensions.add(i);
                        if (mv != null && !mv[i].toLowerCase().equals("null"))
                            missingValues[i] = Integer.parseInt(mv[i]);
                    }
                    IndexProviderSource indexSource = new IndexProviderSource() {
                        @Override
                        public IndexProvider getIndexProvider() {
                            Map<Integer,List<Integer>> idMap = new HashMap<>();
                            for (int i : idDimensions) {
                                @SuppressWarnings("unchecked") //cast is correct, because we know the index is integers
                                List<Integer> ids = (List<Integer>) tensorSource.getTensor().getIndex().getIndexIds().get(i);
                                idMap.put(i,ids);
                            }
                            DataProviderIndexProvider indexProvider = new DataProviderIndexProvider(baseProvider,indexVariables,idMap);
                            //register missing values using options
                            for (int i : range(missingValues.length)) {
                                if (missingValues[i] != null) {
                                    if (idDimensions.contains(i))
                                        indexProvider.registerMissingId(i,missingValues[i]);
                                    else
                                        indexProvider.registerMissingIndex(i,missingValues[i]);
                                }
                            }
                            return indexProvider;
                        }
                    };
                    dataProviderBuilders.put(name,new TensorProviderBuilder(name,tensorSource,DefTableUtil.getTensorFactory(options,factory),indexSource,options));
                } break;
            }
        }
    }

    private class DataTableProviderBuilder implements DataProviderBuilder {
        private final DataTableSource dataTableSource;
        private final TensorFactory factory;

        private DataTableProviderBuilder(DataTableSource dataTableSource, TensorFactory factory) {
            this.dataTableSource = dataTableSource;
            this.factory = factory;
        }

        @Override
        public DataProvider getProvider() {
            return new DataTableDataProvider(dataTableSource.getDataTable(),factory);
        }
    }

    private class DataTableReferenceProviderBuilder implements DataProviderBuilder  {
        private final String name;
        private final DataTableSource dataTableSource;
        private final TensorFactory factory;
        private final String sourceReference;
        private final String providerReference;
        private final DataProvider baseProvider;
        private final Map<String,String> options;

        private DataTableReferenceProviderBuilder(String name, DataTableSource dataTableSource, TensorFactory factory, String sourceReference, String providerReference, DataProvider baseProvider, Map<String,String> options) {
            this.name = name;
            this.dataTableSource = dataTableSource;
            this.factory = factory;
            this.sourceReference = sourceReference;
            this.providerReference = providerReference;
            this.baseProvider = baseProvider;
            this.options = options;
        }

        @Override
        public DataProvider getProvider() {
            DataTableReferenceDataProvider provider = (options.containsKey(REFERENCE_TABLE_MISSING_VALUE_OPTION_KEY)) ?
                new DataTableReferenceDataProvider(factory,dataTableSource.getDataTable(),sourceReference,baseProvider,providerReference,Double.parseDouble(options.get(REFERENCE_TABLE_MISSING_VALUE_OPTION_KEY))) :
                new DataTableReferenceDataProvider(factory,dataTableSource.getDataTable(),sourceReference,baseProvider,providerReference);
            return VariableRenamingDataProvider.getStandardVariableRenamedProvider(provider,name);
        }
    }

    private class TensorProviderBuilder implements DataProviderBuilder  {
        private final String name;
        private final TensorSource tensorSource;
        private final TensorFactory factory;
        private final IndexProviderSource indexSource;
        private final Map<String,String> options;

        private TensorProviderBuilder(String name, TensorSource tensorSource, TensorFactory factory, IndexProviderSource indexSource, Map<String,String> options) {
            this.name = name;
            this.tensorSource = tensorSource;
            this.factory = DefTableUtil.getTensorFactory(options,factory);
            this.indexSource = indexSource;
            this.options = options;
        }

        @Override
        public DataProvider getProvider() {
            return new TensorDataProvider(name,tensorSource.getTensor(),indexSource.getIndexProvider(),factory);
        }
    }



    public static enum DataType {
        DATATABLE("datatable"),
        TENSOR("tensor","matrix");

        private final List<String> identifiers;

        private DataType(String ... identifiers) {
            this.identifiers = Arrays.asList(identifiers);
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        public static DataType getDataSourceDataType(String identifier) {
            for (DataType dt : values())
                for (String dtIdentifier : dt.getIdentifiers())
                    if (dtIdentifier.equalsIgnoreCase(identifier))
                        return dt;
            throw new IllegalArgumentException("DataType for identifier " + identifier + " not found");
        }
    }
}
