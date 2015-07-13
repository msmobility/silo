package com.pb.sawdust.tabledata.transform.column;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.calculator.NumericResultFunction1;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.transform.MutatingDataTableTransformation;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DataColumnAddition} class is a mutating data table transformation which appends columns to the input table.
 *
 * @author crf
 *         Started 1/24/12 9:56 AM
 */
public class DataColumnAddition extends ColumnWiseDataTableTransformation implements MutatingDataTableTransformation {
    private final Map<String,DataColumnAdditionSpecification> columnAdditions;

    /**
     * Constructor for an empty transformation. The transformation will do nothing until column additions are specified.
     */
    public DataColumnAddition() {
        this.columnAdditions = new LinkedHashMap<>();
    }

    /**
     * Convenience constructor initializing the transformation with a single column addition.
     *
     * @param name
     *        The name of the column to add.
     *
     * @param type
     *        The type of data held by the column to add.
     *
     * @param columnFunction
     *        The function identifying the initial values of the new column. This function takes as its sole argument the
     *        data row of the input table, before the column has been added.
     */
    public DataColumnAddition(String name, DataType type, Function1<DataRow,?> columnFunction) {
        this();
        addColumn(name,type,columnFunction);
    }

    /**
     * Specify column to add to the data table. This column will be added after all of those specified before this one
     * have been added (so the values of those additions should be available to this one).
     *
     * @param name
     *        The name of the column to add.
     *
     * @param type
     *        The type of data held by the column to add.
     *
     * @param columnFunction
     *        The function identifying the initial values of the new column. This function takes as its sole argument the
     *        data row of the input table, before the column has been added.
     */
    public void addColumn(String name, DataType type, Function1<DataRow,?> columnFunction) {
        columnAdditions.put(name,new DataColumnAdditionSpecification(type,columnFunction));
    }

    @Override
    public Set<String> getColumnsToTransform() {
        return columnAdditions.keySet();
    }

    @Override
    public DataTable transformColumns(Set<String> column, DataTable table) {
        for (String columnName : columnAdditions.keySet()) {
            DataColumnAdditionSpecification addition = columnAdditions.get(columnName);
            table.addColumn(columnName,addition.getColumnData(table),addition.type);
        }
        return table;
    }

    private class DataColumnAdditionSpecification {
        private final DataType type;
        private final Function1<DataRow,?> columnFunction;
        private final NumericResultFunction1<DataRow> numericColumnFunction;
        private final boolean isNumeric;

        @SuppressWarnings("unchecked") //instanceof check makes cast ok
        private DataColumnAdditionSpecification(DataType type, Function1<DataRow,?> columnFunction) {
            this.type = type;
            this.columnFunction = columnFunction;
            numericColumnFunction = columnFunction instanceof NumericResultFunction1 ? (NumericResultFunction1<DataRow>) columnFunction : null;
            isNumeric = numericColumnFunction != null;
        }

        private Object getColumnData(DataTable table) {
            Object array;
            switch (type) {
                case BOOLEAN : array = new boolean[table.getRowCount()]; break;
                case BYTE : array = new byte[table.getRowCount()]; break;
                case SHORT : array = new short[table.getRowCount()]; break;
                case INT : array = new int[table.getRowCount()]; break;
                case LONG : array = new long[table.getRowCount()]; break;
                case FLOAT : array = new float[table.getRowCount()]; break;
                case DOUBLE : array = new double[table.getRowCount()]; break;
                case STRING : array = new String[table.getRowCount()]; break;
                default : throw new IllegalStateException("Unsupported type for column addition: " + type);
            }
            int counter = 0;
            for (DataRow row : table) {
                if (isNumeric) {
                    switch (type) {
                        case BOOLEAN : Array.set(array,counter,numericColumnFunction.applyDouble(row) == 0.0); break;
                        case BYTE : Array.set(array,counter,numericColumnFunction.applyByte(row)); break;
                        case SHORT : Array.set(array,counter,numericColumnFunction.applyShort(row)); break;
                        case INT : Array.set(array,counter,numericColumnFunction.applyInt(row)); break;
                        case LONG : Array.set(array,counter,numericColumnFunction.applyLong(row)); break;
                        case FLOAT : Array.set(array,counter,numericColumnFunction.applyFloat(row)); break;
                        case DOUBLE : Array.set(array,counter,numericColumnFunction.applyDouble(row)); break;
                        case STRING : Array.set(array,counter,columnFunction.apply(row).toString()); break;
                    }
                } else {
                    switch (type) {
                        case BOOLEAN : Array.set(array,counter,(boolean) (Boolean) columnFunction.apply(row)); break;
                        case BYTE : Array.set(array,counter,(byte) (Byte) columnFunction.apply(row)); break;
                        case SHORT : Array.set(array,counter,(short) (Short) columnFunction.apply(row)); break;
                        case INT : Array.set(array,counter,(int) (Integer) columnFunction.apply(row)); break;
                        case LONG : Array.set(array,counter,(long) (Long) columnFunction.apply(row)); break;
                        case FLOAT : Array.set(array,counter,(float) (Float) columnFunction.apply(row)); break;
                        case DOUBLE : Array.set(array,counter,(double) (Double) columnFunction.apply(row)); break;
                        case STRING : Array.set(array,counter,(String) columnFunction.apply(row)); break;
                    }
                }
                counter++;
            }
            return array;
        }
    }
}
