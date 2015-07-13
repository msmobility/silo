package com.pb.sawdust.model.models.provider.tensor;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code VariableIndexProvider} ...
 *
 * @author crf
 *         Started 4/10/12 5:57 AM
 */
public abstract class VariableIndexProvider<K> implements IndexProvider {
    private final String[] indexVariables;
    private final  Map<? extends K,Integer>[] indexMappings;
    private boolean[] useIndexMapping;
    private final int[] indexMappingIndices;
    private final boolean[] registeredMissing;
    private final K[] missingIds;
    private final int[] missingIndices;

    /**
     * Constructor specifying the variables to use for the index lookups, as well as the index ids.
     *
     * @param indexVariables
     *        The index variables, in order (of the dimensions they corresponding to).
     *
     * @param ids
     *        A mapping from the dimension to use id mapping to the mapping to use. Any dimensions not present in this
     *        map will not use id mapping.
     *
     * @throws IllegalArgumentException if any dimension key in {@code ids} is less than zero or greater than or equal to
     *                                  {@code indexVariables.size()}.
     */
    @SuppressWarnings("unchecked") //casting array to generic array is ok in spirit here, even if usually frowned upon
    public VariableIndexProvider(List<String> indexVariables, Map<Integer,List<? extends K>> ids) {
        this.indexVariables = indexVariables.toArray(new String[indexVariables.size()]);
        useIndexMapping = new boolean[this.indexVariables.length];
        indexMappings = (Map<? extends K,Integer>[]) new Map[this.indexVariables.length];
        List<Integer> indexMappingIndices = new LinkedList<>();
        for (int dimension : ids.keySet()) {
            checkDimension(dimension);
            useIndexMapping[dimension] = true;
            indexMappings[dimension] = buildIndexMapping(ids.get(dimension));
            indexMappingIndices.add(dimension);
        }
        this.indexMappingIndices = ArrayUtil.toIntArray(indexMappingIndices);
        registeredMissing = new boolean[useIndexMapping.length];
        Object[] tempMissingIds = new Object[registeredMissing.length];
        missingIds = (K[]) tempMissingIds;
        missingIndices = new int[registeredMissing.length];
    }

    private Map<? extends K,Integer> buildIndexMapping(List<? extends K> ids) {
        Map<K,Integer> indexMapping = new HashMap<>();
        int counter = 0;
        for (K i : ids)
            indexMapping.put(i,counter++);
        return indexMapping;
    }

    /**
     * Constructor specifying the variables to use for the index lookups. No index id mappings will be used.
     *
     * @param indexVariables
     *        The index variables, in order (of the dimensions they corresponding to).
     */
    public VariableIndexProvider(List<String> indexVariables) {
        this(indexVariables,new HashMap<Integer,List<? extends K>>());
    }

    private void checkDimension(int dimension) {
        if (dimension < 0 || dimension >= getDimensionCount())
            throw new IllegalArgumentException("Index out of bounds: " + dimension);
    }

    public void registerMissingIndex(int dimension, int index) {
        if (useIndexMapping[dimension])
            throw new IllegalArgumentException("Dimension uses index ids, not indices: " + dimension);
        if (registeredMissing[dimension])
            throw new IllegalArgumentException(String.format("Missing dimension index (%s) already registered for dimension %d",index,dimension));
        registeredMissing[dimension] = true;
        missingIndices[dimension] = index;
    }

    public void registerMissingId(int dimension, K id) {
        if (!useIndexMapping[dimension])
            throw new IllegalArgumentException("Dimension does not use index ids: " + dimension);
        if (registeredMissing[dimension])
            throw new IllegalArgumentException(String.format("Missing dimension id (%s) already registered for dimension %d",id,dimension));
        registeredMissing[dimension] = true;
        missingIds[dimension] = id;
    }

    @Override
    public int getDimensionCount() {
        return indexVariables.length;
    }

    abstract protected K getIndexId(String variable, int location);
    abstract protected K[] getIndexIds(String variable, int start, int end);
    abstract protected int getIndex(String variable, int location);
    abstract protected int[] getIndices(String variable, int start, int end);

    @Override
    public int getIndex(int dimension, int location) {
        checkDimension(dimension);
        if (useIndexMapping[dimension]) {
            K id = getIndexId(indexVariables[dimension],location);
            return registeredMissing[dimension] && missingIds[dimension].equals(id) ? 0 : indexMappings[dimension].get(id);
        } else {
            int index = getIndex(indexVariables[dimension],location);
            return registeredMissing[dimension] && index == missingIndices[dimension] ? 0 : index;
        }
    }

    @Override
    public int[] getIndices(int dimension, int start, int end) {
        checkDimension(dimension);
        int[] indices = useIndexMapping[dimension] ? new int[end-start] : getIndices(indexVariables[dimension],start,end);
        boolean checkMissing = registeredMissing[dimension];
        if (useIndexMapping[dimension]) {
            K missingValue = missingIds[dimension];
            Map<?,Integer> mapping = indexMappings[dimension];
            K[] indexIds = getIndexIds(indexVariables[dimension],start,end);
            for (int i : range(indices.length)) {
                K id = indexIds[i];
                indices[i] = checkMissing && missingValue.equals(id) ? 0 : mapping.get(id);
            }
        } else if (checkMissing) {
            int missingValue = missingIndices[dimension];
            for (int i : range(indices.length))
                if (indices[i] == missingValue)
                    indices[i] = 0;
        }
        return indices;
    }
}
