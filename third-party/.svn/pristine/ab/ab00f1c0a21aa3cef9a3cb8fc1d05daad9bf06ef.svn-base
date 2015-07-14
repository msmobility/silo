package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.provider.hub.PolyDataProvider;
import com.pb.sawdust.tabledata.DataTable;

import java.util.*;

/**
 * The {@code PolyDataIndexProvider} ...
 *
 * @author crf
 *         Started 4/9/12 12:46 PM
 */
public class PolyDataIndexProvider implements IndexProvider {
    public static final String CHOICE_VARIABLE_NAME = "__%CHOICE%__";

    private final IndexProvider baseProvider;
    final int[] dimensionMapping;
    private final int polyLocation;

    private PolyDataIndexProvider(IndexProvider baseProvider, int[] dimensionMapping, int polyLocation) {
        this.baseProvider = baseProvider;
        this.dimensionMapping = dimensionMapping;
        this.polyLocation = polyLocation;
    }

    public PolyDataIndexProvider getSpecifiedProvider(int polyLocation) {
        return new PolyDataIndexProvider(baseProvider,dimensionMapping,polyLocation);
    }

    @Override
    public int getDimensionCount() {
        return dimensionMapping.length;
    }

    @Override
    public int getIndex(int dimension, int location) {
        int d = dimensionMapping[dimension];
        if (d == -1)
            return polyLocation;
        return baseProvider.getIndex(d,location);
    }

    @Override
    public int[] getIndices(int dimension, int start, int end) {
        int d = dimensionMapping[dimension];
        if (d == -1) {
            int[] indices = new int[end-start];
            Arrays.fill(indices,polyLocation);
            return indices;
        }
        return baseProvider.getIndices(d,start,end);
    }

    @Override
    public int getIndexLength() {
        return baseProvider.getIndexLength();
    }

    public static PolyDataIndexProvider polyDataIndexProvider(DataTable data, List<String> dimensionVariables, Map<Integer,List<?>> idMapping) {
        List<String> providerVariables = new LinkedList<>();
        int[] dimensionMapping = new int[dimensionVariables.size()];
        int counter = 0;
        int subCounter = 0;
        for (String variable : dimensionVariables) {
            if (variable.equals(CHOICE_VARIABLE_NAME)) {
                dimensionMapping[counter++] = -1;
            } else {
                dimensionMapping[counter++] = subCounter++;
                providerVariables.add(variable);
            }
        }
        @SuppressWarnings("unchecked")
        IndexProvider baseProvider = new DataTableIndexProvider(data,providerVariables,idMapping);
        return new PolyDataIndexProvider(baseProvider,dimensionMapping,-1);
    }

    public static PolyDataIndexProvider polyDataIndexProvider(DataTable data, List<String> dimensionVariables) {
        return polyDataIndexProvider(data,dimensionVariables,new HashMap<Integer,List<?>>());
    }

    public static PolyDataIndexProvider polyDataIndexProvider(DataProvider data, List<String> dimensionVariables, Map<Integer,List<Integer>> idMapping) {
        List<String> providerVariables = new LinkedList<>();
        int[] dimensionMapping = new int[dimensionVariables.size()];
        int counter = 0;
        int subCounter = 0;
        for (String variable : dimensionVariables) {
            if (variable.equals(CHOICE_VARIABLE_NAME)) {
                dimensionMapping[counter++] = -1;
            } else {
                dimensionMapping[counter++] = subCounter++;
                providerVariables.add(variable);
            }
        }
        IndexProvider baseProvider = new DataProviderIndexProvider(data,providerVariables,idMapping);
        return new PolyDataIndexProvider(baseProvider,dimensionMapping,-1);
    }

    public static PolyDataIndexProvider polyDataIndexProvider(DataProvider data, List<String> dimensionVariables) {
        return polyDataIndexProvider(data,dimensionVariables,new HashMap<Integer,List<Integer>>());
    }

    public static <K> PolyDataIndexProvider polyDataIndexProvider(final DataProviderHub<K> data, List<String> dimensionVariables, final List<K> orderedProviderKeys, Map<Integer,List<Integer>> idMapping) {
        final List<String> providerVariables = new LinkedList<>();
        int[] dimensionMapping = new int[dimensionVariables.size()];
        int counter = 0;
        int subCounter = 0;
        for (String variable : dimensionVariables) {
            if (variable.equals(CHOICE_VARIABLE_NAME)) {
                dimensionMapping[counter++] = -1;
            } else {
                dimensionMapping[counter++] = subCounter++;
                providerVariables.add(variable);
            }
        }
        final Map<Integer,List<Integer>> finalIdMapping = new HashMap<>(idMapping);
        return new PolyDataIndexProvider(null,dimensionMapping,-1) {

            public PolyDataIndexProvider getSpecifiedProvider(int polyLocation) {
                return new PolyDataIndexProvider(new DataProviderIndexProvider(data.getProvider(orderedProviderKeys.get(polyLocation)),providerVariables,finalIdMapping),this.dimensionMapping,polyLocation);
            }

            @Override
            public int getIndexLength() {
                return data.getDataLength();
            }
        };
    }

    public static <K> PolyDataIndexProvider polyDataIndexProvider(final PolyDataProvider<K> data, List<String> dimensionVariables, final List<K> orderedProviderKeys, Map<Integer,List<Integer>> idMapping) {
        final List<String> providerVariables = new LinkedList<>();
        int[] dimensionMapping = new int[dimensionVariables.size()];
        int counter = 0;
        int subCounter = 0;
        for (String variable : dimensionVariables) {
            if (variable.equals(CHOICE_VARIABLE_NAME)) {
                dimensionMapping[counter++] = -1;
            } else {
                dimensionMapping[counter++] = subCounter++;
                providerVariables.add(variable);
            }
        }
        final Map<Integer,List<Integer>> finalIdMapping = new HashMap<>(idMapping);
        return new PolyDataIndexProvider(null,dimensionMapping,-1) {

            public PolyDataIndexProvider getSpecifiedProvider(int polyLocation) {
                return new PolyDataIndexProvider(new DataProviderIndexProvider(data.getFullProvider(orderedProviderKeys.get(polyLocation)),providerVariables,finalIdMapping),this.dimensionMapping,polyLocation);
            }

            @Override
            public int getIndexLength() {
                return data.getDataLength();
            }
        };
    }

    public static <K> PolyDataIndexProvider polyDataIndexProvider(final PolyDataProvider<K> data, List<String> dimensionVariables, final List<K> orderedProviderKeys) {
        return polyDataIndexProvider(data,dimensionVariables,orderedProviderKeys,new HashMap<Integer,List<Integer>>());
    }
}
