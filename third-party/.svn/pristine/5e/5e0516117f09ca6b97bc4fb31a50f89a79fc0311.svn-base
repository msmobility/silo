package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * <p>
 * The {@code AbstractDataRow} class provides a skeletal version of {@code DataRow} which implements as many
 * methods as possible, reducing the coding burden on the implementing user. No internal data structures are used in
 * this class, as it only references interface methods. Because of this, some method implementations, such as
 * {@code hasColumn(String)}, may be less efficient than if row information is accessed using class data structures. 
 * </p>
 * @author crf <br/>
 *         Started: May 12, 2008 10:32:31 AM
 */
public abstract class AbstractDataRow implements DataRow {
    private final boolean coercionOn;

    protected AbstractDataRow(boolean coercionOn) {
        this.coercionOn = coercionOn;
    }

    protected AbstractDataRow() {
        this(false);
    }
    
    public boolean hasColumn(String columnLabel) {
        for (String label : getColumnLabels()) {
            if (label.equals(columnLabel))
                return true;
        }
        return false;
    }

    public String getColumnLabel(int columnIndex) {
        try {
            return getColumnLabels()[columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnIndex);
        }
    }

    public Object[] getIndexValues(TableIndex index) {
        Object[] indexValues = new Object[index.getIndexColumnLabels().length];
        int counter = 0;
        for (String columnLabel : index.getIndexColumnLabels()) {
            if (!hasColumn(columnLabel)) {
                throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
            }
            indexValues[counter++] = getCell(columnLabel);
        }
        return indexValues;
    }

    @SuppressWarnings("unchecked") //suppressed because documentation states that T must match the column type
    public <T> T getCell(String columnLabel) {
        return (T) getCell(getColumnIndex(columnLabel));
    }

    @SuppressWarnings("unchecked") //suppressed because documentation states that T must match the column type
    public <T> T getCell(int columnIndex) {
        try {
            return (T) getData()[columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnIndex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public byte getCellAsByte(int columnIndex) {
        return coercionOn ? (Byte) DataType.BYTE.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).byteValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public byte getCellAsByte(String columnLabel) {
        return coercionOn ? (Byte) DataType.BYTE.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).byteValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public short getCellAsShort(int columnIndex) {
        return coercionOn ? (Short) DataType.SHORT.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).shortValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public short getCellAsShort(String columnLabel) {
        return coercionOn ? (Short) DataType.SHORT.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).shortValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public int getCellAsInt(int columnIndex) {
        try {
        return coercionOn ? (Integer) DataType.INT.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).intValue();
        } catch (ClassCastException e) {
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public int getCellAsInt(String columnLabel) {
        return coercionOn ? (Integer) DataType.INT.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).intValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public long getCellAsLong(int columnIndex) {
        return coercionOn ? (Long) DataType.LONG.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).longValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public long getCellAsLong(String columnLabel) {
        return coercionOn ? (Long) DataType.LONG.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).longValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public float getCellAsFloat(int columnIndex) {
        return coercionOn ? (Float) DataType.FLOAT.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).floatValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public float getCellAsFloat(String columnLabel) {
        return coercionOn ? (Float) DataType.FLOAT.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).floatValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public double getCellAsDouble(int columnIndex) {
        return coercionOn ? (Double) DataType.DOUBLE.coerce(getCell(columnIndex)) : this.<Number>getCell(columnIndex).doubleValue();
    } 

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code Number}, unless data coercion is turned on.
     */
    public double getCellAsDouble(String columnLabel) {
        return coercionOn ? (Double) DataType.DOUBLE.coerce(getCell(columnLabel)) : this.<Number>getCell(columnLabel).doubleValue();
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code DataType.BOOLEAN}, unless data coercion is turned on.
     */
    public boolean getCellAsBoolean(int columnIndex) {
        return coercionOn ? (Boolean) DataType.BOOLEAN.coerce(getCell(columnIndex)) : this.<Boolean>getCell(columnIndex);
    }

    /**
     * {@inheritDoc}
     *
     * This method assumes the cell value is of type {@code DataType.BOOLEAN}, unless data coercion is turned on.
     */
    public boolean getCellAsBoolean(String columnLabel) {
        return coercionOn ? (Boolean) DataType.BOOLEAN.coerce(getCell(columnLabel)) : this.<Boolean>getCell(columnLabel);
    }

    public String getCellAsString(int columnIndex) {
        return this.<Object>getCell(columnIndex).toString();
    }

    public String getCellAsString(String columnLabel) {
        return this.<Object>getCell(columnLabel).toString();
    }

    public int getColumnIndex(String columnLabel) {
        String[] columnLabels = getColumnLabels();
        for (int i = 0; i < columnLabels.length; i++) {
            if (columnLabels[i].equals(columnLabel))
                return i;
        }
        throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
    }

    public DataType getColumnType(int columnIndex) {
        try {
            return getColumnTypes()[columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnIndex);
        }
    }

    public DataType getColumnType(String columnLabel) {
        return getColumnType(getColumnIndex(columnLabel));
    }

    public boolean willCoerceData() {
        return coercionOn;
    }
}
