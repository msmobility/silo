package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.TableIndex;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code WrappedDataRow} class provides a {@code DataRow} implementation which wraps another data row. All methods
 * just (by default) pass through to the wrapped data row. This class is useful as a starting class for indirect {@code DataRow}
 * extensions which need to only modify select behavior from the source class.
 *
 *
 * @author crf
 *         Started 1/24/12 5:39 PM
 */
public class WrappedDataRow implements DataRow {
    /**
     * The wrapped data row.
     */
    protected final DataRow row;

    /**
     * Constructor specifying the data row to wrap.
     *
     * @param row
     *        The data row this instance will wrap.
     */
    public WrappedDataRow(DataRow row) {
        this.row = row;
    }

    @Override
    public String[] getColumnLabels() {
        return row.getColumnLabels();
    }

    @Override
    public String getColumnLabel(int columnIndex) {
        return row.getColumnLabel(columnIndex);
    }

    @Override
    public boolean hasColumn(String columnLabel) {
        return row.hasColumn(columnLabel);
    }

    @Override
    public int getColumnIndex(String columnLabel) {
        return row.getColumnIndex(columnLabel);
    }

    @Override
    public DataType[] getColumnTypes() {
        return row.getColumnTypes();
    }

    @Override
    public DataType getColumnType(int columnIndex) {
        return row.getColumnType(columnIndex);
    }

    @Override
    public DataType getColumnType(String columnLabel) {
        return row.getColumnType(columnLabel);
    }

    @Override
    public Object[] getData() {
        return row.getData();
    }

    @Override
    public <T> T getCell(int columnIndex) {
        return row.getCell(columnIndex);
    }

    @Override
    public <T> T getCell(String columnLabel) {
        return row.getCell(columnLabel);
    }

    @Override
    public byte getCellAsByte(int columnIndex) {
        return row.getCellAsByte(columnIndex);
    }

    @Override
    public byte getCellAsByte(String columnLabel) {
        return row.getCellAsByte(columnLabel);
    }

    @Override
    public short getCellAsShort(int columnIndex) {
        return row.getCellAsShort(columnIndex);
    }

    @Override
    public short getCellAsShort(String columnLabel) {
        return row.getCellAsShort(columnLabel);
    }

    @Override
    public int getCellAsInt(int columnIndex) {
        return row.getCellAsInt(columnIndex);
    }

    @Override
    public int getCellAsInt(String columnLabel) {
        return row.getCellAsInt(columnLabel);
    }

    @Override
    public long getCellAsLong(int columnIndex) {
        return row.getCellAsLong(columnIndex);
    }

    @Override
    public long getCellAsLong(String columnLabel) {
        return row.getCellAsLong(columnLabel);
    }

    @Override
    public float getCellAsFloat(int columnIndex) {
        return row.getCellAsFloat(columnIndex);
    }

    @Override
    public float getCellAsFloat(String columnLabel) {
        return row.getCellAsFloat(columnLabel);
    }

    @Override
    public double getCellAsDouble(int columnIndex) {
        return row.getCellAsDouble(columnIndex);
    }

    @Override
    public double getCellAsDouble(String columnLabel) {
        return row.getCellAsDouble(columnLabel);
    }

    @Override
    public boolean getCellAsBoolean(int columnIndex) {
        return row.getCellAsBoolean(columnIndex);
    }

    @Override
    public boolean getCellAsBoolean(String columnLabel) {
        return row.getCellAsBoolean(columnLabel);
    }

    @Override
    public String getCellAsString(int columnIndex) {
        return row.getCellAsString(columnIndex);
    }

    @Override
    public String getCellAsString(String columnLabel) {
        return row.getCellAsString(columnLabel);
    }

    @Override
    public Object[] getIndexValues(TableIndex index) {
        return row.getIndexValues(index);
    }

    @Override
    public boolean willCoerceData() {
        return row.willCoerceData();
    }
}
