package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;

import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * The {@code AbstractDataTable} class provides a skeletal version of {@code DataTable} which implements as many
 * methods as possible, reducing the coding burden on the implementing user.
 * </p>
 * @author crf <br/>
 *         Started: May 15, 2008 9:36:40 AM
 */
public abstract class AbstractDataTable implements DataTable {
    //this is to make the primary key threadsafe; this means getting the primary key is synchronized - slightly innefficient, but safe
    private final Lock primaryKeyLock = new ReentrantLock();
    private volatile TableKey<?> primaryKey = null;
    private volatile boolean primaryKeyFresh = false;
    private volatile boolean coercionOn = false;

    /**
     * Add a column to this data table. This method's specification is identical to {@code com.pb.sawdust.tabledata.DataTable#addColumn(String, Object, DataType)},
     * except that it should not check to ensure that {@code columnDataArray} is an array nor update the table schema.
     * That is, this class' implementation of the above specified method already carries out those tasks, leaving
     * the user to implement this method, which only adds the column to the data table. This method is optional and
     * need not be implemented (with anything more than a shell method), though in such a case, the {@code com.pb.sawdust.tabledata.DataTable#addColumn(String, Object, DataType)}
     * should be overridden to throw an {@code UnsupportedOperationException}.
     *
     * @param columnLabel
     *        The label for the new column.
     *
     * @param columnDataArray
     *        The new column data for the existing rows in the data table. This must be a java array (this is
     *        generified to allow for primitive and object arrays), with one entry for each row, matching the
     *        current row ordering of the data table.
     *
     * @param type
     *        The data type of the new column.
     *
     * @return {@code true} if the column was added successfully, {@code false} otherwise.
     *
     * @throws UnsupportedOperationException if this method is not implemented.
     *
     * @see DataTable#addColumn(String, Object, DataType)
     */
    abstract protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type);

    /**
     * Add a row of data to this data table, with the next row index specified. Specifying the next row index
     * explicitly, as opposed to calling {@code getRowCount()} internally, allows for efficiency gains in some
     * instances (such as when using {@code addDataByRow(Object[][])}). This method is intended to replace (or wrap)
     * the various {@code addRow} methods in the {@code DataTable} interface. This method is optional, and it need not
     * be implemented (with anything more than a shell method), though in such a case, the {@code addRow} methods should
     * be overridden to throw an {@code UnsupportedOperationException}.
     *
     * @param nextRowIndex
     *        The next row index in the data table. This value will be identical to a call to {@code getRowCount()}.
     *
     * @param rowData
     *        The row to add. Consistency checking (column names, types, etc. between row and this data table) may
     *        not be performed.
     *
     * @return {@code true} if the row was added successfully, {@code false} otherwise.
     *
     * @throws UnsupportedOperationException if this method is not implemented.
     */
    abstract protected boolean addRow(int nextRowIndex, Object ... rowData);

    /**
     * Delete the specified column from this table. This method should not check the input parameter for validity, and
     * should perform no other tasks on the table or its associated data.  This method is optional, and it need not
     * be implemented (with anything more than a shell method), though in such a case, the {@code deleteColumn} methods
     * should be overridden to throw an {@code UnsupportedOperationException}.
     *
     * @param columnNumber
     *        The (0-based) index of the column to delete.
     *
     * @throws UnsupportedOperationException if this method is not implemented.
     */
    abstract protected void deleteColumnFromData(int columnNumber);

    /**
     * Delete the specified row from this table. This method should not check the input parameter for validity, and
     * should perform no other tasks on the table or its associated data.  This method is optional, and it need not
     * be implemented (with anything more than a shell method), though in such a case, the {@code deleteRow} methods
     * should be overridden to throw an {@code UnsupportedOperationException}.
     *
     * @param rowNumber
     *        The (0-based) number of the row to delete.
     *
     * @throws UnsupportedOperationException if this method is not implemented.
     */
    abstract protected void deleteRowFromData(int rowNumber);

    /**
     * Set the value of a cell in this table. This method should not check the row and column number parameters for
     * validity, but it should throw an exception if the value does not match the column's specified data type. This
     * method is optional, and it need not be implemented (with anything more than a shell method), though in such a
     * case, the {@code setCellValue}, {@code setRowData}, and {@code setColumnData} methods should be overridden to
     * throw an {@code UnsupportedOperationException} (or to be implemented to not use this method).
     *
     * @param rowNumber
     *        The (0-based) row number of the cell to be changed.
     *
     * @param columnIndex
     *        The (0-based) column index of the cell to be changed.
     *
     * @param value
     *        The value to set in the cell.
     *
     * @return {@code true} if the cell was updated successfully, {@code false} otherwise.
     *
     * @throws TableDataException if {@code value}'s type does not match the data type of the column.
     */
    abstract protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value);

    /**
     * Sets whether this data table will attempt to coerce data to the correct types when setting or getting.
     *
     * @param coersionOn
     *        If {@code true}, then data will be coerced, otherwise it will not.
     */
    public void setDataCoersion(boolean coersionOn) {
        this.coercionOn = coersionOn;
    }

    /**
     * Indicates whether this data table will attempt to coerce data to the correct types when setting or getting.
     *
     * @return {@code true} if data will be coerced, {@code false} otherwise.
     */
    public boolean willCoerceData() {
        return coercionOn;
    }

    public String getLabel() {
        return getSchema().getTableLabel(); 
    }

    public String[] getColumnLabels() {
        return getSchema().getColumnLabels();
    }

    public boolean hasColumn(String columnLabel) {
        return getSchema().hasColumn(columnLabel);
    }

    public int getColumnNumber(String columnLabel) {
        int counter = 0;
        for (String label : getColumnLabels()) {
            if (columnLabel.equals(label))
                return counter;
            counter++;
        }
        throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
    }

    public String getColumnLabel(int columnNumber) {
        try {
            return getColumnLabels()[columnNumber];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnNumber);
        }
    }

    public DataType[] getColumnTypes() {
        return getSchema().getColumnTypes();
    }

    public DataType getColumnDataType(int columnNumber) {
        try {
            return getColumnTypes()[columnNumber];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnNumber);
        }
    }

    public DataType getColumnDataType(String columnLabel) {
        return getColumnDataType(getColumnNumber(columnLabel));
    }

    /**
     * Coerce row data into the valid types for this data table.  It can be assumed that the length of the data is
     * correct. This method will coerce the data <i>in place</i> (override this method to change this behavior).
     *
     * @param rowData
     *        The row data to coerce.
     *
     * @return {@code rowData}, holding data of the correct types for this data table's columns.
     *
     * @throws IllegalArgumentException if any of the values in {@code rowData} cannot be coerced.
     */
    protected Object[] coerceRowData(Object ... rowData) {
        DataType[] types = getSchema().getColumnTypes();
        for (int i = 0; i < rowData.length; i++) {
            Object rd = rowData[i];
            rowData[i] = types[i].coerce(rd,DataType.getDataType(rd));
        }
        return rowData;
    }

    /**
     * {@inheritDoc}
     *
     * If data coersion is enabled then this data will be coerced <i>in place</i>.  To change this behavior, override
     * the {@link #coerceRowData(Object...)} method.
     */
    public boolean addRow(Object ... rowData) {
        if (coercionOn)
            rowData = coerceRowData(rowData);
        if (addRow(getRowCount(),rowData)) {
            setPrimaryKeySpoiled();
            return true;
        }
        return false;
    }

    public boolean addRow(DataRow row) {
        return addRow(row.getData());
    }

    /**
     * Check whether a specified object is an array with primitive component type corresponding to a given data type. As
     * some data types do not have primitive equivalents (<i>e.g.</i> {@code DataType.STRING}), this method does
     * not guarantee the input array's component type is a java primitive.
     *
     * @param dataArray
     *        The object to check.
     *
     * @param type
     *        The data type to check against.
     *
     * @return {@code true} if {@code dataArray} is an array with component type matching {@code type}'s primitive type,
     *         {@code false} otherwise.
     *
     * @see com.pb.sawdust.tabledata.metadata.DataType#getPrimitiveClass()
     */
    protected boolean checkPrimitiveArrayType(Object dataArray, DataType type) {
        try {
            type.getPrimitiveArray(0).getClass().cast(dataArray);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Check whether a specified object is an array with object component type corresponding to a given data type.
     *
     * @param dataArray
     *        The object to check.
     *
     * @param type
     *        The data type to check against.
     *
     * @return {@code true} if {@code dataArray} is an array with component type matching {@code type}'s object type,
     *         {@code false} otherwise.
     *
     * @see com.pb.sawdust.tabledata.metadata.DataType#getObjectClass()
     */
    protected boolean checkObjectArrayType(Object dataArray, DataType type) {
        try {
            type.getObjectArray(0).getClass().cast(dataArray);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private Object getPrimitiveArray(DataType source, DataType result, Object sourceArray) {
        final boolean match = source == result;
        int arrayLength = Array.getLength(sourceArray);
        Object primitiveArray = result.getPrimitiveArray(arrayLength);
        for (int i = 0; i < arrayLength; i++)
            Array.set(primitiveArray,i,match ? Array.get(sourceArray,i) : source.coerce(Array.get(sourceArray,i),result));
        return primitiveArray;
    }

    /**
     * Get a "primitive" array of data from an input array. Primitive here means given data type's primitive class. If
     * the input array is already primitive, it will be passed through unchecked and untransformed, otherwise it will
     * be checked to ensure it matches the given data type, and will be transferred to a primitive array. This method
     * can be used to ensure that an input column data array is of the correct type for a given data type (no checking
     * is done for input columns which are already primitive, use {@code checkPrimitiveArrayType} to check such arrays).
     *
     * @param columnDataArray
     *        The input data array.
     *
     * @param type
     *        The data type the data array corresponds to.
     *
     * @return a primitive array corresponding to {@code type} filled with the same values as {@code columnDataArray}.
     *
     * @throws IllegalArgumentException if {@code columnDataArray} is not an array.
     * @throws TableDataException if {@code type} does not correspond to {@code columnDataArray}'s type. 
     */
    protected Object getPrimitiveColumn(Object columnDataArray, DataType type) {
        Class arrayClass = columnDataArray.getClass();
        if (!arrayClass.isArray())
            throw new IllegalArgumentException("columnDataArray in addColumn must be an array.");
        Class componentType = arrayClass.getComponentType();
        if (componentType.isPrimitive()) {
            if (type.getPrimitiveClass() != componentType) {
                if (coercionOn) {
                    DataType arrayType;
                    try {
                        arrayType = DataType.getDataType(componentType);
                    } catch (IllegalArgumentException e) {
                        throw new TableDataException("Cannot coerce colunn data from " + componentType + "[] to " + type.getPrimitiveClass() + "[]");
                    }
                    return getPrimitiveArray(type,arrayType,columnDataArray);
                } else
                    throw new TableDataException("Expected column type " + type.getPrimitiveClass() + "[], got " + componentType + "[] instead.");
            }
            return columnDataArray;
        }
        if (!checkObjectArrayType(columnDataArray,type)) {
            if (coercionOn) {
                DataType arrayType;
                try {
                    arrayType = DataType.getDataType(componentType);
                } catch (IllegalArgumentException e) {
                    throw new TableDataException("Cannot coerce colunn data from " + componentType + "[] to " + type.getPrimitiveClass() + "[]");
                }
                return getPrimitiveArray(type,arrayType,columnDataArray);
            } else
                throw new TableDataException("Expected column type " + type.getObjectClass() + "[], got " + componentType + "[] instead.");
        } else {
            return getPrimitiveArray(type,type,columnDataArray);
        }
    }

    public boolean addColumn(String columnLabel, Object columnDataArray, DataType type) {
        if (hasColumn(columnLabel))
            throw new TableDataException(TableDataException.COLUMN_ALREADY_EXISTS,columnLabel);
        columnDataArray = getPrimitiveColumn(columnDataArray,type);
        if (getColumnCount() > 0 && Array.getLength(columnDataArray) != getRowCount())
            throw new TableDataException("New column length not equal to the number of rows in data set!");
        boolean success = addColumnToData(columnLabel,columnDataArray,type);
        if (success)
            getSchema().addColumn(columnLabel,type);
        return success;
    }

    public boolean addColumn(DataColumn column) {
        return addColumn(column.getLabel(),column.getData(),column.getType());
    }

    public boolean addDataByColumn(Object[] data) {
        if (data.length != getColumnCount())
            throw new TableDataException("Column data length must be equal to the number of columns in the table; found " + data.length + ", expected " + getColumnCount());
        int rowIndex = getRowCount();
        DataType[] columnTypes = getColumnTypes();
        int newRowCount = 0;
        for (int i = 0; i < data.length; i++) {
            if (i == 0)
                newRowCount = Array.getLength(data[i]);
            else if (newRowCount != Array.getLength(data[i]))
                throw new IllegalArgumentException("Column data must be composed of eqaul length arrays.");
            if (!data[i].getClass().isArray())
                throw new IllegalArgumentException("Column data must be composed of arrays.");
            data[i] = getPrimitiveColumn(data[i],columnTypes[i]);
            if (!checkPrimitiveArrayType(data[i],columnTypes[i]))
                throw new TableDataException(String.format("Primitive column data must be of type %1$s",(columnTypes[i].getPrimitiveTypeString() + "[]")));
        }
        int rowCount = data.length == 0 ? 0 : Array.getLength(data[0]);
        for (int i=0; i < rowCount; i++) {
            Object[] rowData = new Object[data.length];
            for (int j = 0; j < data.length; j++) {
                rowData[j] = Array.get(data[j],i);
            }
            addRow(rowIndex+i,rowData);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * If data coersion is enabled then this data will be coerced <i>in place</i>.  To change this behavior, override
     * the {@link #coerceRowData(Object...)} method.
     */
    public boolean addDataByRow(Object[][] data) {
        int rowIndex = getRowCount();
        int counter = 0;
        for (Object[] rowData : data) {
            addRow(rowIndex+counter++,coercionOn ? coerceRowData(rowData) : rowData);
        }
        return true;
    }

    private void typeCheckData(int columnIndex, Object value) {
        if (value.getClass() != getColumnDataType(columnIndex).getObjectClass())
            throw new TableDataException(TableDataException.INVALID_DATA_TYPE,value.getClass().getName(),getColumnDataType(columnIndex).getObjectTypeString());
    }

    /**
     * Check to make sure data intended as a row in this data table is of the correct type and number of elements.
     *
     * @param rowData
     *        The row data in question.
     *
     * @throws TableDataException if {@code rowData} is not the correct length or its elements' types do not match those
     *                            required for its columns.
     */
    protected void boundAndTypeCheckRowData(Object ... rowData) {  
        if (rowData.length != getColumnCount())
            throw new TableDataException("Row data must be of length equal to number of columns in table");
        for (int i = 0; i < rowData.length; i++)
            typeCheckData(i,rowData[i]);
    }

    /**
     * {@inheritDoc}
     *
     * If data coersion is enabled then this data will be coerced <i>in place</i>.  To change this behavior, override
     * the {@link #coerceRowData(Object...)} method.
     */
    public boolean setRowData(int rowNumber, Object ... values) {
        if (rowNumber < 0 || rowNumber >= getRowCount())
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,rowNumber);
        if (values.length != getColumnCount())
            throw new TableDataException("Row data must have correct number of entries.");
        if (coercionOn)
            values = coerceRowData(values);
        boolean success = true;
        for (int i = 0; i < values.length; i++)
            success &= setCellValueInData(rowNumber,i,values[i]);
        setPrimaryKeySpoiled();
        return success;
    }

    public boolean setColumnData(int columnIndex, Object columnValues) {
        if (!columnValues.getClass().isArray())
            throw new IllegalArgumentException("Column values in setColumnData must be an array.");
        if (Array.getLength(columnValues) != getRowCount())
            throw new TableDataException("Column values array length should equal number of rows in table");
        DataType columnType = getColumnDataType(columnIndex);
        columnValues = getPrimitiveColumn(columnValues,columnType);
        if (!checkPrimitiveArrayType(columnValues,columnType))
            throw new TableDataException(TableDataException.INVALID_DATA_TYPE,columnValues.getClass().getComponentType(),columnType.getPrimitiveTypeString());
        Iterator columnDataIterator = ArrayUtil.getIterator(columnValues);
        int counter = 0;
        boolean success = true;
        while (columnDataIterator.hasNext())
            success &= setCellValue(counter++,columnIndex,columnDataIterator.next());
//        }
        return success;
    }

    public boolean setColumnData(String columnLabel, Object columnValues) {
        return setColumnData(getColumnNumber(columnLabel),columnValues);
    }

    /**
     * {@inheritDoc}
     *
     * If data coersion is enabled then this data will be coerced <i>in place</i>.  To change this behavior, override
     * the {@link #coerceRowData(Object...)} method.
     */
    public <K> boolean setRowDataByKey(K key, Object ... values) {
        return setRowData(getRowNumberByKey(key),values);
    }

    public boolean setCellValue(int rowNumber, int columnNumber, Object value) {
        if (rowNumber < 0 || rowNumber >= getRowCount())
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,rowNumber);
        if (columnNumber < 0 || columnNumber >= getColumnCount())
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnNumber);
        boolean success = setCellValueInData(rowNumber,columnNumber,coercionOn ? getColumnDataType(columnNumber).coerce(value) : value);
        if (getColumnLabel(columnNumber).equals(getPrimaryKey().getKeyColumnLabel()))
            setPrimaryKeySpoiled();
        return success;
    }

    public boolean setCellValue(int rowNumber, String columnLabel, Object value) {
        return setCellValue(rowNumber,getColumnNumber(columnLabel),value);
    }

    public <K> boolean setCellValueByKey(K key, int columnIndex, Object value) {
        return setCellValue(getRowNumberByKey(key),columnIndex,value);
    }

    public <K> boolean setCellValueByKey(K key, String columnLabel, Object value) {
        return setCellValueByKey(key,getColumnNumber(columnLabel),value);
    }

    public DataRow deleteRow(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= getRowCount())
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,rowNumber);
        DataRow deletedDataRow = getRow(rowNumber);
        deleteRowFromData(rowNumber);
        setPrimaryKeySpoiled();
        return deletedDataRow;
    }

    public <K> DataRow deleteRowByKey(K key) {
        return deleteRow(getRowNumberByKey(key));
    }

    public <T> DataColumn<T> deleteColumn(int columnNumber) {
        String columnLabel = getColumnLabel(columnNumber);
        if (columnNumber < 0 || columnNumber >= getColumnCount())
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnNumber);
        if (columnLabel.equals(getPrimaryKey().getKeyColumnLabel()))              
        if (getColumnLabel(columnNumber).equals(getPrimaryKey().getKeyColumnLabel()))
            throw new TableDataException(TableDataException.CANNOT_DELETE_PRIMARY_KEY_COLUMN,columnLabel);
        DataColumn<T> deletedDataColumn = getDataColumn(columnNumber,getColumnDataType(columnNumber));
        deleteColumnFromData(columnNumber);
        getSchema().dropColumn(columnLabel);
        return deletedDataColumn;
    }

    public <T> DataColumn<T> deleteColumn(String columnlabel) {
        return deleteColumn(getColumnNumber(columnlabel));
    }

    public <K> boolean setPrimaryKey(TableKey<K> primaryKey) {
        this.primaryKey = primaryKey;
        return true;
    }

    public boolean setPrimaryKey(String columnLabel) {
        return hasColumn(columnLabel) && setPrimaryKey(TableDataFactory.getTableKey(this, columnLabel));
        //        //because of erasure switch statement is unnecessary, but it makes me feel better
//        switch (getColumnDataType(columnLabel)) {
//            case BOOLEAN : return setPrimaryKey(TableDataFactory.<Boolean>getTableKey(this,columnLabel));
//            case BYTE : return setPrimaryKey(TableDataFactory.<Byte>getTableKey(this,columnLabel));
//            case SHORT : return setPrimaryKey(TableDataFactory.<Short>getTableKey(this,columnLabel));
//            case INT : return setPrimaryKey(TableDataFactory.<Integer>getTableKey(this,columnLabel));
//            case LONG : return setPrimaryKey(TableDataFactory.<Long>getTableKey(this,columnLabel));
//            case FLOAT : return setPrimaryKey(TableDataFactory.<Float>getTableKey(this,columnLabel));
//            case DOUBLE : return setPrimaryKey(TableDataFactory.<Double>getTableKey(this,columnLabel));
//            case STRING : return setPrimaryKey(TableDataFactory.<String>getTableKey(this,columnLabel));
//            default : return setPrimaryKey(TableDataFactory.<Object>getTableKey(this,columnLabel));
//        }
    }

    @SuppressWarnings("unchecked") //cannot ensure primaryKey will hold Ks, but if not it is a user definition/calling error, so suppressing
    public <K> TableKey<K> getPrimaryKey() {
        try {
            primaryKeyLock.lock();
            if (primaryKey == null)
                setPrimaryKey();
            if (!primaryKeyFresh) {
                primaryKey.buildIndex();
                primaryKeyFresh = true;
            }
        } finally {
            primaryKeyLock.unlock();
        }
        return (TableKey<K>) primaryKey;  //cannot guarantee that <K> will match primaryKey's type
    }

    @SuppressWarnings("unchecked") //coersion will be correct here
    private <K> K getKeyValue(K key) {
        return (K) (coercionOn ? getPrimaryKey().getKeyColumnType().coerce(key) : key);
    }

    private <K> int getRowNumberByKey(K key) {
        return getPrimaryKey().getRowNumber(getKeyValue(key));
    }

    private void setPrimaryKey() {
        String primaryKeyColumn = getSchema().getPrimaryKeyColumn();
        if (primaryKeyColumn != null)
            setPrimaryKey(primaryKeyColumn);
        else
            setPrimaryKey(getDefaultPrimaryKey());
    }

    /**
     * Set the primary key for this data table to be spoiled (as opposed to fresh).  This indicates that a change has
     * been made to the data table such that the primary key will no longer be valid without being rebuilt.
     * Implementations of this class which do not override any of its methods (<i>i.e.</i> just implement its abstract
     * methods) will not need to call this.  However, if any of the row-altering methods (add, delete, <i>etc.</i>) are
     * overridden, this method should be called within the overriding method's bodies to establish the fact that the
     * primary key is spoiled.
     */
    protected void setPrimaryKeySpoiled() {
        primaryKeyFresh = false;
    }

    /**
     * Get the default primary key to be used for this table. This default in this class just passes a given key through
     * as the row number.
     *
     * @return the default primary key.
     */
    protected TableKey<?> getDefaultPrimaryKey() {
        return new AbstractTableKey<Integer>(null,DataType.INT) {

            protected Integer[] getTypeArray(int size) {
                return new Integer[size];
            }

            protected InjectiveMap<Integer,Integer> getKeyIndex() {
                InjectiveMap<Integer,Integer> keyIndex = new InjectiveHashMap<Integer,Integer>();
                for (int i = 0; i < getRowCount(); i++)
                    keyIndex.put(i,i);
                return keyIndex;
            }
        };
    }

    public <K> DataRow getRowByKey(K key) {
        return getRow(getRowNumberByKey(key));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public <K> DataTable getIndexedRows(TableIndex<K> index, K ... indexValues) {
        DataTable table = TableDataFactory.getDataTable(getSchema());
        if (index.getClass().isAssignableFrom(TableKey.class)) {
            table.addRow(getRow(((TableKey<K>) index).getRowNumber(indexValues[0])));
        } else {
            for (int rowNumber : index.getRowNumbers(indexValues)) {
                table.addRow(getRow(rowNumber));
            }
        }
        return table;
    }

    /**
     * Get the column of data corresponding to a given ordinal index as a specified type. The column does not
     * necessarily have to be of the stated type, but an exception may be thrown if the implementation cannot convert
     * the data to correctly. This method is intended to wrap the various {@code get*Column(int)} methods
     * in the {@code DataTable} interface. If the type parameter {@code T} is specified, then it
     * must match the data column's type (the class of {@code T} must match {@code getColumnDataType(columnIndex).getObjectClass()}).
     *
     * @param columnIndex
     *        The cardinal index (0-based) of the column to return.
     *
     * @param type
     *        The data type of the column to return.
     *
     * @return the column corresponding to index {@code columnIndex}.
     */
    @SuppressWarnings("unchecked") //suppressed because documentation states that T must match the column type
    protected <T> DataColumn<T> getDataColumn(int columnIndex,DataType type) {
        String columnLabel = getColumnLabels()[columnIndex];
        DataType sourceType = getSchema().getColumnTypes()[columnIndex];
        T[] column = (T[]) type.getObjectArray(this.getRowCount());
        int counter = 0;
        for (DataRow row : this) {
            T cell = (T) row.getData()[columnIndex]; //unchecked, because can't guarantee that type corresponds to T
            column[counter++] = coercionOn ? (T) type.coerce(cell,sourceType) : cell;
        }
        return TableDataFactory.getDataColumn(column,columnLabel,type,getPrimaryKey());
//        List<Object> column = new LinkedList<Object>();
//        for (DataRow row : this)
//            column.add(row.getData()[columnIndex]);
//        return TableDataFactory.getDataColumn((T[]) column.toArray(type.getObjectArray(0)),columnLabel,type,getPrimaryKey());

//        switch (type) {
//            case BOOLEAN : return TableDataFactory.getDataColumn((T[]) column.toArray(new Boolean[0]),columnLabel,type,getPrimaryKey());
//            case BYTE : return TableDataFactory.getDataColumn((T[]) column.toArray(new Byte[0]),columnLabel,type,getPrimaryKey());
//            case SHORT : return TableDataFactory.getDataColumn((T[]) column.toArray(new Short[0]),columnLabel,type,getPrimaryKey());
//            case INT : return TableDataFactory.getDataColumn((T[]) column.toArray(new Integer[0]),columnLabel,type,getPrimaryKey());
//            case LONG : return TableDataFactory.getDataColumn((T[]) column.toArray(new Long[0]),columnLabel,type,getPrimaryKey());
//            case FLOAT : return TableDataFactory.getDataColumn((T[]) column.toArray(new Float[0]),columnLabel,type,getPrimaryKey());
//            case DOUBLE : return TableDataFactory.getDataColumn((T[]) column.toArray(new Double[0]),columnLabel,type,getPrimaryKey());
//            case STRING : return TableDataFactory.getDataColumn((T[]) column.toArray(new String[0]),columnLabel,type,getPrimaryKey());
//        }
//        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Note for implementors: all other {@code getCellValue}/{@code getCellValueByKey} methods will reflect back and
     * use this method. Thus, if overriding, only this method need be replaced.
     */
    public Object getCellValue(int rowIndex, int columnIndex) {
        try {
            return getRow(rowIndex).getData()[columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnIndex);
        }
    }

    public Object getCellValue(int rowIndex, String columnLabel) {
            return getCellValue(rowIndex,getColumnNumber(columnLabel));
    }

    public <K> Object getCellValueByKey(K key, int columnIndex) {
        return getCellValue(getRowNumberByKey(key),columnIndex);
    }

    public <K> Object getCellValueByKey(K key, String columnLabel) {
            return getCellValueByKey(key,getColumnNumber(columnLabel));
    }

    /**
     * Get the column of data corresponding to a given label as a specified type. The input column does not
     * necessarily have to be of the stated type, but an exception should be thrown if the implementation cannot convert
     * the data to correctly. This method is intended to wrap the various {@code get*Column(String)} methods in the
     * {@code DataTable} interface.
     *
     * @param columnLabel
     *        The label of the column to return.
     *
     * @param type
     *        The data type of the column to return.
     *
     * @return the column corresponding to label {@code columnLabel} whose type corresponds to {@code type}.
     */
    protected <T> DataColumn<T> getDataColumn(String columnLabel,DataType type) {
        return getDataColumn(getColumnNumber(columnLabel),type);
    }

    public <T> DataColumn<T> getColumn(int columnIndex) {
        return getDataColumn(columnIndex,getColumnDataType(columnIndex));
    }

    public <T> DataColumn<T> getColumn(String columnLabel) {
        return getDataColumn(columnLabel,getColumnDataType(columnLabel));
    }

    public BooleanDataColumn getBooleanColumn(int columnIndex) {
        return new BooleanDataColumn(this.<Boolean>getDataColumn(columnIndex,DataType.BOOLEAN));
    }

    public BooleanDataColumn getBooleanColumn(String columnLabel) {
        return new BooleanDataColumn(this.<Boolean>getDataColumn(columnLabel,DataType.BOOLEAN));
    }

    public ByteDataColumn getByteColumn(int columnIndex) {
        return new ByteDataColumn(this.<Byte>getDataColumn(columnIndex,DataType.BYTE));
    }

    public ByteDataColumn getByteColumn(String columnLabel) {
        return new ByteDataColumn(this.<Byte>getDataColumn(columnLabel,DataType.BYTE));
    }

    public ShortDataColumn getShortColumn(int columnIndex) {
        return new ShortDataColumn(this.<Short>getDataColumn(columnIndex,DataType.SHORT));
    }

    public ShortDataColumn getShortColumn(String columnLabel) {
        return new ShortDataColumn(this.<Short>getDataColumn(columnLabel,DataType.SHORT));
    }

    public IntegerDataColumn getIntColumn(int columnIndex) {
        return new IntegerDataColumn(this.<Integer>getDataColumn(columnIndex,DataType.INT));
    }

    public IntegerDataColumn getIntColumn(String columnLabel) {
        return new IntegerDataColumn(this.<Integer>getDataColumn(columnLabel,DataType.INT));
    }

    public LongDataColumn getLongColumn(int columnIndex) {
        return new LongDataColumn(this.<Long>getDataColumn(columnIndex,DataType.LONG));
    }

    public LongDataColumn getLongColumn(String columnLabel) {
        return new LongDataColumn(this.<Long>getDataColumn(columnLabel,DataType.LONG));
    }

    public FloatDataColumn getFloatColumn(int columnIndex) {
        return new FloatDataColumn(this.<Float>getDataColumn(columnIndex,DataType.FLOAT));
    }

    public FloatDataColumn getFloatColumn(String columnLabel) {
        return new FloatDataColumn(this.<Float>getDataColumn(columnLabel,DataType.FLOAT));
    }

    public DoubleDataColumn getDoubleColumn(int columnIndex) {
        return new DoubleDataColumn(this.<Double>getDataColumn(columnIndex,DataType.DOUBLE));
    }

    public DoubleDataColumn getDoubleColumn(String columnLabel) {
        return new DoubleDataColumn(this.<Double>getDataColumn(columnLabel,DataType.DOUBLE));
    }

    public StringDataColumn getStringColumn(int columnIndex) {
        return new StringDataColumn(this.<String>getDataColumn(columnIndex,DataType.STRING));
    }

    public StringDataColumn getStringColumn(String columnLabel) {
        return new StringDataColumn(this.<String>getDataColumn(columnLabel,DataType.STRING));
    }

    public Iterator<DataRow> iterator() {
        return iterator(0,getRowCount());
    }

    protected Iterator<DataRow> iterator(final int start, final int end) {
        return new Iterator<DataRow>() {
            private int place = start;

            public boolean hasNext() {
                return place < end;
            }

            public DataRow next() {
                if (hasNext())
                    return getRow(place++);
                else
                    throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public DataTable getTablePartition(int start, int end) {
        return new DataTablePartition(this,start,end,coercionOn);
    }

    /**
     * The {@code DataTablePartition} class provides a basic data table partition which can be used to fulfill the
     * {@code DataTable.getTablePartition(int,int)} method.
     * <p>
     * Instances of this class provide a view into the underlying table, so changes in it are reflected here.  As
     * required, instances of this class cannot modify the underlying table; however, the underlying table may be
     * changed after the partition instance is created, causing inconsistencies and/or errors.  Therefore, care must
     * be taken if a partition is created for a table which may be subsequently modified.
     */
    protected static class DataTablePartition extends AbstractDataTable {
        private final int start;
        private final int end;
        private final AbstractDataTable parentTable;

        /**
         * Constructor specifying the start (inclusive) and end (exclusive) of the table partition.
         *
         * @param parentTable
         *        The table to partition.
         *
         * @param start
         *        The (0-based) row number to start the partition at.
         *
         * @param end
         *        The (0-based) row number to end the partition at (exclusive).
         *
         * @param coersionOn
         *        If {@code true}, then data coersion is will be enabled in the table partition.
         *
         * @throws IllegalArgumentException if {@code start} or {@code end} are less than zero, greater than or equal to
         *                                  the number of rows in the underlying table, or if <code>start >= end</code>.
         *
         */
        protected DataTablePartition(AbstractDataTable parentTable, int start, int end, boolean coersionOn) {
            if (start >= end)
                throw new IllegalArgumentException(String.format("Table partition start (%d) must be strictly less than end (%d).",start,end));
            int rows = parentTable.getRowCount();
            if (start < 0 || end < 0 || end > rows)
                throw new IllegalArgumentException(String.format("Table partition (%d,%d) is out of bounds for table with %d rows.",start,end,rows));
            this.start = start;
            this.end = end;
            this.parentTable = parentTable;
            setPrimaryKey(parentTable.primaryKey); //inherit key by default
            setDataCoersion(coersionOn);
        }

        /**
         * {@inheritDoc}
         *
         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
         */
        protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
            throw new UnsupportedOperationException("Partition is unmodifiable view.");
        }

        /**
         * {@inheritDoc}
         *
         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
         */
        protected boolean addRow(int nextRowIndex, Object ... rowData) {
            throw new UnsupportedOperationException("Partition is unmodifiable view.");
        }

        /**
         * {@inheritDoc}
         *
         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
         */
        protected void deleteColumnFromData(int columnNumber) {
            throw new UnsupportedOperationException("Partition is unmodifiable view.");
        }

        /**
         * {@inheritDoc}
         *
         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
         */
        protected void deleteRowFromData(int rowNumber) {
            throw new UnsupportedOperationException("Partition is unmodifiable view.");
        }

        /**
         * {@inheritDoc}
         *
         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
         */
        protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
            throw new UnsupportedOperationException("Partition is unmodifiable view.");
        }

//        /**
//         * {@inheritDoc}
//         *
//         * @throws UnsupportedOperationException since the table partition cannot modify the underlying table.
//         */
//        public <K> boolean setPrimaryKey(TableKey<K> key) {
//            throw new UnsupportedOperationException("Key cannot be set in table partition.");
//        }

        public int getRowCount() {
            return end - start;
        }

        public int getColumnCount() {
            return parentTable.getColumnCount();
        }

        public TableSchema getSchema() {
            return parentTable.getSchema();
        }

        public DataRow getRow(int rowNumber) {
            int row = rowNumber+start;
            if (rowNumber < 0 || row >= end)
                throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,row);
            return parentTable.getRow(row);
        }

        public Iterator<DataRow> iterator() {
            return parentTable.iterator(start,end);
        }

        public DataTable getTablePartition(int start, int end) {
            if (start >= end)
                throw new IllegalArgumentException(String.format("Table partition start (%d) must be strictly less than end (%d).",start,end));
            int rows = getRowCount();
            if (start < 0 || end < 0 || end > rows)
                throw new IllegalArgumentException(String.format("Table partition (%d,%d) is out of bounds for table with %d rows.",start,end,rows));
            return parentTable.getTablePartition(start+this.start,end+this.start);
        }
    }

}
