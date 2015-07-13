package com.pb.sawdust.tabledata.metadata;

import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.collections.LinkedSetList;
import static com.pb.sawdust.util.Range.*;

import java.util.*;

/**
 * The {@code TableSchema} class provides a representation of a data table's schema, which provides information about the
 * data table and its columns. A schema defines the structure of the data table, but nothing about its actual contents
 * (including how many rows it contains).
 *
 * @author crf <br>
 *         Started: May 7, 2008 9:29:58 PM<br>
 */
public class TableSchema implements Iterable<ColumnSchema> {
    private String tableLabel;
    private SetList<ColumnSchema> columns;
    private Set<String> columnLabelSet;
    private String[] columnLabels;
    private DataType[] columnTypes;
    private String primaryKeyColumn = null;

    /**
     * Constructor for an empty data table. The resulting schema will not contain any columns.
     *
     * @param tableName
     *        The name for the data table.
     */
    public TableSchema(String tableName) {
        this.tableLabel = tableName;
        columns = new LinkedSetList<ColumnSchema>();
        columnLabelSet = new HashSet<String>();
        setColumnLabels();
        setColumnTypes();
    }

    /**
     * Constructor specifying the table name, and column names and data types. The equivalent entry locations in the column
     * label and data type arrays identify a single column (so the arrays must be of equal size).
     *
     * @param tableName
     *        The name for the data table.
     *
     * @param columnLabels
     *        The names for the columns.
     *
     * @param types
     *        The data types for the columns.
     *
     * @throws TableDataException if the sizes of {@code columnLabels} and {@code types} are not equal.
     */
    public TableSchema(String tableName, String[] columnLabels, DataType[] types) {
        this(tableName);
        if (columnLabels.length == types.length) {
            for (int i = 0; i < columnLabels.length; i++) {
                addColumnToSchema(columnLabels[i],types[i]);
            }
            setColumnLabels(columnLabels);
            setColumnTypes(types);
        } else {
            throw new TableDataException("Number of column names does not match number of column types:\n\t" + Arrays.toString(columnLabels) + "\n\t" + Arrays.toString(types));
        }
    }

    /**
     * Set the column to use as the primary key column for the data table.
     *
     * @param primaryKeyColumn
     *        The name of the primary key column.
     *
     * @throws TableDataException if {@code primaryKeyColumn} is not a column in this schema.
     */
    public void setPrimaryKeyColumn(String primaryKeyColumn) {
        if (!hasColumn(primaryKeyColumn))
            throw new TableDataException("Primary key column must exist in table schema.");
        this.primaryKeyColumn = primaryKeyColumn;
    }

    /**
     * Get the name of the primary key column specified in this schema. If a primary key has not been specified, then
     * this method will return {@code null}.
     *
     * @return the primary key column's name.
     */
    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    /**
     * Get the data table's name.
     *
     * @return the name of the data table.
     */
    public String getTableLabel() {
        return tableLabel;
    }

    /**
     * Get the name of a column at the specified position.
     *
     * @param columnIndex
     *        The (0-based) column position.
     *
     * @return the name of the column at {@code columnIndex}.
     *
     * @throws IndexOutOfBoundsException if {@code columnIndex} is less than zero or greater than/equal to the number of
     *                                   columns held by this schema.
     */
    public String getColumnLabel(int columnIndex) {
        return columns.get(columnIndex).getColumnLabel();
    }

    /**
     * Get the position of the specified column.
     *
     * @param columnLabel
     *        The name of the column in question.
     *
     * @return the (0-based) index of the position of column {@code columnLabel}.
     *
     * @throws TableDataException if {@code columnLabel} is not a column in this schema.
     */
    public int getColumnIndex(String columnLabel) {
        //todo: really innefficient
        for (int i : range(columnLabels.length))
            if (columnLabels[i].equals(columnLabel))
                return i;
        throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
    }

    /**
     * Get the schemas, in order, for the columns in this schema.
     *
     * @return this schema's column schemas.
     */
    public ColumnSchema[] getColumnSchemas() {
        return columns.toArray(new ColumnSchema[columns.size()]);
    }

    /**
     * Get the labels, in order, for the columns in this schema.
     *
     * @return this schema's column names.
     */
    public String[] getColumnLabels() {
        return columnLabels;
    }

    /**
     * Get the data types, in order, for the columns in this schema.
     *
     * @return the data types held by this schema's columns.
     */
    public DataType[] getColumnTypes() {
        return columnTypes;
    }

    /**
     * Determine if a specified column exists in this schema or not.
     *
     * @param columnLabel
     *        The name of the column in question.
     *
     * @return {@code true} if a column named {@code columnLabel} exists in this schema, {@code false} if not.
     */
    public boolean hasColumn(String columnLabel) {
        return columnLabelSet.contains(columnLabel);
    }

    /**
     * Add a column to this schema. the column will be appended to the end of the already existing columns.
     *
     * @param columnLabel
     *        The column label.
     *
     * @param type
     *        The data type held by the column.
     *
     * @return {@code true} if the column was added successfully.
     *
     * @throws TableDataException if a column with name {@code columnLabel} already exists.
     */
    public boolean addColumn(String columnLabel,DataType type) {
        addColumnToSchema(columnLabel,type);
        setColumnLabels();
        setColumnTypes();
        return true;
    }

    private void addColumnToSchema(String columnLabel, DataType type) {
        if (hasColumn(columnLabel))
            throw new TableDataException(TableDataException.COLUMN_ALREADY_EXISTS,columnLabel);
        columnLabelSet.add(columnLabel);
        columns.add(getColumnSchema(columnLabel,type));
    }

    /**
     * Drop a column from the schema.
     *
     * @param columnLabel
     *        The name of the column to drop.
     *
     * @return {@code true} if the column was removed successfully.
     *
     * @throws TableDataException if a column with name {@code columnLabel} does not exist.
     */
    public boolean dropColumn(String columnLabel) {
        dropColumnFromSchema(columnLabel);
        setColumnLabels();
        setColumnTypes();
        return true;
    }

    private void dropColumnFromSchema(String columnLabel) {
        if (!hasColumn(columnLabel))
            throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
        columnLabelSet.remove(columnLabel);
        int counter = 0;
        for (ColumnSchema column : columns) {
            if (column.getColumnLabel().equals(columnLabel))
                break;
            counter++;
        }
        columns.remove(counter);
    }

    /**
     * Construct a new column schema using the specified information. This method is used to build the column schemas held
     * by this schema when it is constructed, or when a new column is added.
     *
     * @param columnLabel
     *        The column label.
     *
     * @param type
     *        The data type held by the column.
     *
     * @return a column schema for a column named {@code columnLabel} and holding {@code type}s.
     */
    protected ColumnSchema getColumnSchema(String columnLabel,DataType type) {
        return new ColumnSchema(columnLabel,type);
    }

    /**
     * Set the labels for the columns held by this schema. This method rebuilds the internal column name list so that it
     * is consistent with the column schemas held by this class. This method is intended for use by extending schemas which
     * may need to modify the column schemas directly.
     */
    protected void setColumnLabels() {
        columnLabels = new String[columns.size()];
        int counter = 0;
        for (ColumnSchema columnSchema : columns) {
            columnLabels[counter++] = columnSchema.getColumnLabel();
        }
    }

    /**
     * Set the labels for the columns held by this schema. This method is intended for use by extending schemas which may
     * need to specify this array directly. No checks are made to ensure that the array is consistent with this schema, so
     * care must be taken when using this method to avoid invalid schema states.
     *
     * @param columnLabels
     *        The column names, in order.
     */
    protected void setColumnLabels(String[] columnLabels) {
        this.columnLabels = columnLabels;
    }

    /**
     * Set the column types held by this schema. This method rebuilds the internal column types list so that it is consistent
     * with the column schemas held by this class. This method is intended for use by extending schemas which may need to
     * modify the column schemas directly.
     */
    protected void setColumnTypes() {
        columnTypes = new DataType[columns.size()];
        int counter = 0;
        for (ColumnSchema columnSchema : columns) {
            columnTypes[counter++] = columnSchema.getType();
        }
    }

    /**
     * Set the column types held by this schema. This method is intended for use by extending schemas which may need to
     * specify this array directly. No checks are made to ensure that the array is consistent with this schema, so care
     * must be taken when using this method to avoid invalid schema states.
     *
     * @param columnTypes
     *        The column types, in order.
     */
    protected void setColumnTypes(DataType[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    /**
     * Get an iterator over the columns held by this schema, in the ordered they are specified.
     *
     * @return an iterator over this schema's column schemas.
     */
    public Iterator<ColumnSchema> iterator() {
        return columns.iterator();
    }

    /**
     * Copy this schema to a new one with a different data table name.
     *
     * @param tableLabel
     *        The data table name to use for the new schema.
     *
     * @return a copy of this schema, using {@code tableLabel} for the data table name.
     */
    public TableSchema copy(String tableLabel) {
        TableSchema newSchema = new TableSchema(tableLabel);
        for (ColumnSchema col : this) {
            newSchema.addColumn(col.getColumnLabel(),col.getType());
        }
        return newSchema;
    }

    /**
     * Get a copy of this data table schema.
     *
     * @return a copy of this schema.
     */
    public TableSchema copy() {
        return copy(getTableLabel());
    }

    public final boolean equals(Object o) {
        return this.blindlyEquals(o) && ((TableSchema) o).blindlyEquals(this);
    }

    /**
     * Determine if this object is equal to another. This method is intended to allow class inheritance without breaking
     * the {@code equals(Object)} contract. Specifically, when extending this class and adding (significant) internal
     * additions, this method should be overridden ({@code equals(Object)} cannot be overridden as it is declared final).
     * The idiom for overriding this method in an extending class {@code Extension} is as follows:
     * <code><pre>
     *     protected boolean blindlyEquals(Object o) {
     *         if (o == this)
     *             return true;
     *         if (!(o instanceof Extension))
     *             return false;
     *         if (!super.blindlyEquals(o))
     *             return false;
     *         ...//(equals checks specific to Extension)
     *     }
     * </pre></code>
     *
     * @param o
     *        The object to compare.
     *
     * @return {@code true} if {@code o} is equal to this object, {@code false} if not.
     */
    protected boolean blindlyEquals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TableSchema))
            return false;
        TableSchema schema = (TableSchema) o;
        return tableLabel.equals(schema.tableLabel) &&
                columns.equals(schema.columns) &&
                columnLabelSet.equals(schema.columnLabelSet) &&
                Arrays.equals(columnLabels,schema.columnLabels) &&
                Arrays.equals(columnTypes,schema.columnTypes) &&
                (primaryKeyColumn==null ? schema.primaryKeyColumn==null: primaryKeyColumn.equals(schema.primaryKeyColumn));
    }

    public int hashCode() {
        int result = 17;
        result = 31*result + columns.hashCode();
        result = 31*result + columnLabelSet.hashCode();
        result = 31*result + (columnLabels==null ? 0 : columnLabels.hashCode());
        result = 31*result + (columnTypes==null ? 0 : columnTypes.hashCode());
        result = 31*result + (primaryKeyColumn==null ? 0 : primaryKeyColumn.hashCode());
        return result;
    }
}
