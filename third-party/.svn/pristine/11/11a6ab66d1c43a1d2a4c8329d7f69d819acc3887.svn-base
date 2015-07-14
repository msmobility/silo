package com.pb.sawdust.tabledata.transform.column;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.CoercedDataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.transform.WrappingDataTableTransformation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DataColumnCoercion} is a wrapping data table transformation which modifies the types of the columns in a
 * data table. This transformation provides a view through to the underlying (input) data table through which access to
 * the specified column data is provided through data coercion. This class can be looked at as a structured way to specify
 * the construction of a {@link CoercedDataTable}.
 *
 * @author crf
 *         Started 1/19/12 4:08 PM
 */
public class DataColumnCoercion extends ColumnWiseDataTableTransformation implements WrappingDataTableTransformation {
    private final Map<String,DataType> coercionMapping;

    /**
     * Constructor for an empty transformation. The transformation will (effectively) do nothing until column coercions are specified.
     */
    public DataColumnCoercion() {
        this.coercionMapping = new LinkedHashMap<>();
    }

    /**
     * Constructor initializing the transformation with a single column coercion.
     *
     * @param column
     *        The name of the column to coerce.
     *
     * @param type
     *        The type to coerce the column to.
     */
    public DataColumnCoercion(String column, DataType type) {
        this();
        addColumnCoercion(column,type);
    }

    /**
     * Add a column coercion to this transformation.
     *
     * @param column
     *        The name of the column to coerce.
     *
     * @param type
     *        The type to coerce the column to.
     */
    public void addColumnCoercion(String column, DataType type) {
        coercionMapping.put(column,type);
    }

    @Override
    public Set<String> getColumnsToTransform() {
        return coercionMapping.keySet();
    }

    @Override
    public DataTable transformColumns(Set<String> columns, DataTable table) {
        Map<Integer,DataType> indexCoercionMapping = new HashMap<>();
        for (String column : getColumnsToTransform())
            indexCoercionMapping.put(table.getColumnNumber(column),coercionMapping.get(column));
        return new CoercedDataTable(table,indexCoercionMapping);
    }
}
