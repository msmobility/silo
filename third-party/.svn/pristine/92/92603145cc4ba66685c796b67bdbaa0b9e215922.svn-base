package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.collections.InjectiveMap;

import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Array;

/**
 * <p>
 * The {@code AbstractTableKey} class provides a skeletal version of {@code TableKey} which reduces the coding
 * burden on the implementing user. It actually implements all of the methods from {@code TableKey}, leaving it to
 * the implementing class to form the key lookup table.
 * </p>
 *
 * @param <K>
 *        The type of the table key.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 11:17:05 AM
 */
public abstract class AbstractTableKey<K> implements TableKey<K> {

    private String columnLabel;
    private DataType columnType;
    private Class colummnTypeClass;
    private Class colummnTypePrimitiveClass;
    private InjectiveMap<K,Integer> index;
    private K[] uniqueKeys;

    /**
     * Constructor specifying this key's data table column.
     *
     * @param columnLabel
     *        The label of the table data column used in this key.
     *
     * @param columnType
     *        The data type of the table data column used in this key.
     */
    public AbstractTableKey(String columnLabel, DataType columnType) {
        this.columnLabel = columnLabel;
        this.columnType = columnType;
        colummnTypeClass = columnType.getObjectClass();
        colummnTypePrimitiveClass = columnType.getPrimitiveClass();
    }

    /**
     * Build a lookup table used to map key values to table data rows. This key index should be constructed such that
     * {@code getRowNumber(key)} is identical to {@code getKeyIndex().get(key)}. This method should account for changes
     * made to the underlying/referring data table. That is, if a data table is altered in some manner, then
     * subsequent calls to this method will reflect these changes. This mapping should be unique, so that no
     * map value (row number) is repeated (matched to more than one key). The map's value set should form a complete
     * set of all row numbers in the underlying/referring data table.
     *
     * @return the mapping from key values to row numbers.
     */
    abstract protected InjectiveMap<K,Integer> getKeyIndex();

    /**
     * Get an array of type {@code K} and specified size. This method must be overridden in implementations
     * where the key type is explicitly set to something other than {@code Object}.
     *
     * @param size
     *        The size of the array to be returned.
     *
     * @return a new array of component type {@code K} and length {@code size}.
     */
    @SuppressWarnings("unchecked") //suppressed because doc states should be overridden when K is stated
    protected K[] getTypeArray(int size) {
        return (K[]) new Object[size];
    }

    public String getKeyColumnLabel() {
        return columnLabel;
    }

    public DataType getKeyColumnType() {
        return columnType;
    }

    public int getRowNumber(K keyValue) {
        Class keyValueClass = keyValue.getClass();
        if (keyValueClass != colummnTypeClass && keyValueClass != colummnTypePrimitiveClass)
            throw new TableDataException(TableDataException.INVALID_INDEX_VALUE_TYPE,keyValueClass,colummnTypeClass);
        if (!index.containsKey(keyValue)) {
            throw new TableDataException(TableDataException.INDEX_VALUE_NOT_FOUND,keyValue);
        }
        return index.get(keyValue);
    }

    public K getKey(int rowNumber) {
        K key = index.getKey(rowNumber);
        if (key == null)
            throw new TableDataException("Row number out of bounds: " + rowNumber);
        return key;
    }

    public String[] getIndexColumnLabels() {
        return new String[] {columnLabel};
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public Set<Integer> getRowNumbers(K ... indexValues) {
        Set<Integer> rowNumbers = new HashSet<Integer>();
        rowNumbers.add(getRowNumber(indexValues[0]));
        return rowNumbers;
    }

    public K[][] getUniqueValues() {
        @SuppressWarnings("unchecked") //supressed because getTypeArray is typesafe (by contract)
        K[][] uniqueValues = (K[][]) Array.newInstance(getTypeArray(0).getClass().getComponentType(),uniqueKeys.length,1);
        for (int i = 0; i < uniqueKeys.length; i++) {
            uniqueValues[i][0] = uniqueKeys[i];
        }
        return uniqueValues;
    }

    public K[] getUniqueKeys() {
        return uniqueKeys;
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public int getIndexCount(K ... indexValues) {
        return (indexValues.length > 0 && index.containsKey(indexValues[0])) ? 1 : 0;
    }

    public void buildIndex() {
        index = getKeyIndex();
        uniqueKeys = index.keySet().toArray(getTypeArray(index.size()));
    }

}
