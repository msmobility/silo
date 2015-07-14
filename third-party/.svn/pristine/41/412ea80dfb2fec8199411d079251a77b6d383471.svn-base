package com.pb.sawdust.tabledata;

import java.util.*;
import java.lang.reflect.Array;

/**
 * The {@code AbstractTableIndex} class provides a skeletal version of {@code TableIndex} which reduces the coding
 * burden on the implementing user. It actually implements all of the methods from {@code TableIndex}, leaving it to
 * the implementing class to define how a unique key is built from a series of index values. This class contains two
 * type parameters, one, {@code K}, for the type of key that the {@code buildIndexKey(I ...)} returns, and a second,
 * {@code I}, which is equivalent to {@code TableIndex}'s type parameter, and indicates the types of the index columns.
 * A reference to the data table this index refers to is held by this class and is explicitly used with the
 * {@link #buildIndex()} method.
 *
 * @param <I>
 *        The type of the table index components.
 *
 * @param <K>
 *        The type of the key used to uniquely map a group of index components. 
 *
 * @author crf <br/>
 *         Started: May 21, 2008 8:47:20 PM
 */
public abstract class AbstractTableIndex<I,K> implements TableIndex<I> {
    private DataTable table;
    private String[] indexColumnLabels;
    private int indexColumnCount;
    private Map<K,Set<Integer>> index = new HashMap<K,Set<Integer>>();
    private I[][] uniqueValues = null;

    /**
     * Constructor specifying the data table and its columns to be used for this index. This does not build the index;
     * the user must call {@code buildIndex()} before using this instance.
     *
     * @param table
     *        The data table used to build this index.
     *
     * @param indexColumnLabels
     *        The column labels from {@code table} used for the index, in the order that they are to be referenced by
     *        the index's methods.
     *
     * @throws TableDataException if any of {@code indexColumnLabels} are not contained in {@code table}.
     */
    public AbstractTableIndex(DataTable table, String ... indexColumnLabels) {
        this.table = table;
        for (String columnLabel : indexColumnLabels)
            if (!table.hasColumn(columnLabel))
               throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
        this.indexColumnLabels = indexColumnLabels;
        indexColumnCount = indexColumnLabels.length;
    }

    /**
     * Build the key from the provided index values.  For each unique set of index values, the returned key must be
     * unique; identical keys should only be returned when all index values, in order, are identical. Equality is
     * determined in the manner that key matches are defined in the {@code java.lang.Map<K,V>} interface
     * (<code>key1</code> equals <code>key2<code> if <code>(key1==null ? key2==null : key1.equals(key2))</code>).
     * <p>
     * Efficiency gains can be achieved by developing a more efficient key-building algorithm, and if equality checks
     * (<i>i.e.</i> the <code>.equals(Object)</code> method and hash lookups) are as efficient as possible.
     *
     * @param indexValues
     *        The values to build the index key from.
     *
     * @return the index key.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    abstract protected K buildIndexKey(I ... indexValues);

    /**
     * Get an array of type {@code I} and specified size. This method must be overridden in implementations
     * where the index type is explicitly set to something other than {@code Object}.
     *
     * @param size
     *        The size of the array to be returned.
     *
     * @return a new array of component type {@code K} and length {@code size}.
     */
    @SuppressWarnings("unchecked") //suppressed because doc states should be overridden when I is stated
    protected I[] getTypeArray(int size) {
        return (I[]) new Object[size];
    }

    public void buildIndex() {
        index.clear();
        List<I[]> uniqueValueList = new LinkedList<I[]>(); 
        int counter = 0;
        for (DataRow row : table) {
            I[] values = getTypeArray(indexColumnLabels.length);
            for (int i = 0; i < values.length; i++) {
                @SuppressWarnings("unchecked") //supressed because doc states that generifying class indicates that all rows in
                                               //index will have type corresponding to I
                I cell = (I) row.getCell(indexColumnLabels[i]);
                values[i] = cell;
            }
            K key = buildIndexKey(values);
            if (!index.containsKey(key)) {
                index.put(key,new HashSet<Integer>());
                uniqueValueList.add(values);
            }
            index.get(key).add(counter++);
        }
        @SuppressWarnings("unchecked") //supressed because getTypeArray is typesafe (by contract)
        I[][] uniqueValues = uniqueValueList.toArray((I[][]) Array.newInstance(getTypeArray(0).getClass().getComponentType(),uniqueValueList.size(),indexColumnLabels.length));
        this.uniqueValues = uniqueValues;
    }

    public String[] getIndexColumnLabels() {
        return indexColumnLabels;
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Set<Integer> getRowNumbers(I ... indexValues) {
        checkInitialized();
        if (indexValues.length != indexColumnCount)
            throw new IllegalArgumentException("Incorrect index value count, expected " + indexColumnCount + ", found " + indexValues.length);
        K indexKey = buildIndexKey(indexValues);
        if (!index.containsKey(indexKey))
            throw new TableDataException(TableDataException.INDEX_VALUE_NOT_FOUND,indexKey);
        return index.get(indexKey);
    }

    public I[][] getUniqueValues() {
        checkInitialized();
        return uniqueValues;
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public int getIndexCount(I ... indexValues) {
        checkInitialized();
        K indexKey = buildIndexKey(indexValues);
        return (indexValues.length > 0 && index.containsKey(indexKey)) ? index.get(indexKey).size() : 0;
    }

    private void checkInitialized() {
        if (uniqueValues == null)
            throw new TableDataException(TableDataException.INDEX_NOT_INITIALIZED);
    }
}
