package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.metadata.ColumnSchema;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code SqlTableSchema} class provides a data table schema for tables held in SQL databases. In order to provide for
 * row access via row numbers (as required by the {@code DataTable} interface), a column holding the (0-based) row numbers
 * must be used as a "row number key column." If such a column does not exist in the table, then an extra column, an internal
 * row number key column, will be added to provide this functionality. This internal column is considered "non-visible"
 * because it is only used internally by the {@code SqlDataTable} implementations, and is hidden from users of the sql
 * data tables (see that class for a further discussion of this issue). "Visible" columns are all columns in the schema except
 * the internal row number key column.
 *
 * @author crf <br/>
 *         Started: May 14, 2008 7:57:59 PM
 *
 * @see SqlDataTable
 */
public class SqlTableSchema extends TableSchema {
    private String visibleColumnList;
    private ColumnSchema internalRowNumberKeyColumnSchema = null;
    private String rowNumberKeyColumnLabel = null;

    /**
     * Constructor for an empty data table, specifying whether an internal row number key column should be used. The resulting
     * schema will not contain any columns. If an internal row number key column will not be used, then the row number
     * key column should be specified (via {@link #setRowNumberKeyColumnLabel(String)}) after construction.
     *
     * @param tableName
     *        The name for the data table.
     *
     * @param useInternalRowNumberKeyColumn
     *        If {@code true}, an internal row number key column will be used.
     */
    public SqlTableSchema(String tableName, boolean useInternalRowNumberKeyColumn) {
        super(tableName);
        if (useInternalRowNumberKeyColumn)
            setRowNumberKeyColumnInternal();
    }
    /**
     * Constructor for an empty data table. The resulting schema will not contain any columns. An internal row number key
     * column will be used.
     *
     * @param tableName
     *        The name for the data table.
     */
    public SqlTableSchema(String tableName) {
        this(tableName, true);
    }

    /**
     * Constructor specifying the table name, column names and data types, and whether an internal row number key column
     * should be used. The equivalent entry locations in the column label and data type arrays identify a single column
     * (so the arrays must be of equal size). If an internal row number key column will not be used, then the row number
     * key column should be specified (via {@link #setRowNumberKeyColumnLabel(String)}) after construction.
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
     * @param useInternalRowNumberKeyColumn
     *        If {@code true}, an internal row number key column will be used.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if the sizes of {@code columnLabels} and {@code types} are not equal.
     */
    public SqlTableSchema(String tableName, String[] columnLabels, DataType[] types, boolean useInternalRowNumberKeyColumn) {
        super(tableName, columnLabels, types);
        if (useInternalRowNumberKeyColumn)
            setRowNumberKeyColumnInternal();
    }

    /**
     * Constructor specifying the table name, and column names and data types. The equivalent entry locations in the column
     * label and data type arrays identify a single column (so the arrays must be of equal size). An internal row number
     * key column will be used.
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
     * @throws com.pb.sawdust.tabledata.TableDataException if the sizes of {@code columnLabels} and {@code types} are not equal.
     */
    public SqlTableSchema(String tableName, String[] columnLabels, DataType[] types) {
        this(tableName, columnLabels, types, true);
    }

    /**
     * Constructor specifying the schema and whether an internal row number key column should be used. If an internal row
     * number key column will not be used, then the row number key column should be specified (via {@link #setRowNumberKeyColumnLabel(String)})
     * after construction.
     *
     * @param schema
     *        The schema to build the sql schema from.
     *
     * @param useInternalRowNumberKeyColumn
     *        If {@code true}, an internal row number key column will be used.
     */
    public SqlTableSchema(TableSchema schema, boolean useInternalRowNumberKeyColumn) {
        this(schema.getTableLabel(),schema.getColumnLabels(),schema.getColumnTypes(),useInternalRowNumberKeyColumn);
    }

    /**
     * Constructor specifying the schema. An internal row number key column will be used.
     *
     * @param schema
     *        The schema to build the sql schema from.
     */
    public SqlTableSchema(TableSchema schema) {
        this(schema,true);
    }

    /**
     * Get a list of visible columns from the schema correctly formatted for use in an SQL statement.
     *
     * @param identifierQuote
     *        The string placed on each side of an identifier to correctly "quote" them.
     *
     * @return a list of visible columns in this schema that can be used in an SQL statement.
     */
    public String getVisibleColumnList(String identifierQuote) {
        return String.format(visibleColumnList,identifierQuote);
    }

    /**
     * Set the name of the column to use for the row number key. This method does not verify that the column exists.
     *
     * @param rowNumberKeyColumnLabel
     *        The name of the column to use for the row number key.
     */
    public void setRowNumberKeyColumnLabel(String rowNumberKeyColumnLabel) {
        this.rowNumberKeyColumnLabel = rowNumberKeyColumnLabel;
        internalRowNumberKeyColumnSchema = null;
    }

    private void setRowNumberKeyColumnInternal() {
        setRowNumberKeyColumnLabel(SqlDataTable.INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL);
        internalRowNumberKeyColumnSchema = new ColumnSchema(rowNumberKeyColumnLabel,DataType.INT);
    }

    protected void setColumnLabels() {
        super.setColumnLabels();
        setVisibleColumnList();
    }

    protected void setColumnLabels(String[] columnLabels) {
        super.setColumnLabels(columnLabels);
        setVisibleColumnList();
    }

    private void setVisibleColumnList() {
        visibleColumnList = "";
        boolean first = true;
        for (String columnLabel : getColumnLabels()) {
            if (first)
                first = false;
            else
                visibleColumnList += ",";
            visibleColumnList += SqlTableDataUtil.formQuotedIdentifier(columnLabel,"%1$s");
        }
    }

    /**
     * Get an SQL statement which can be sent to an {@code SqlDataSet} to build the table specified by this schema.
     *
     * @param identifierQuote
     *        The string placed on each side of an identifier to correctly "quote" them.
     *
     * @param dataSet
     *        The sql data set which will be used to build the table. This is required to allow for variances in SQL syntax
     *        across vendors.
     *
     * @return a string SQL statement which can be used to build the table specified by this schema in {@code dataSet}.
     */
    public String getTableCreationSqlStatement(String identifierQuote, SqlDataSet dataSet) {
        String statement = "CREATE TABLE " + SqlTableDataUtil.formQuotedIdentifier(getTableLabel(),identifierQuote) + "(";
        boolean formedFirst = false;
        if (usesInternalRowNumberKey()) {
//            statement += SqlTableDataUtil.formQuotedIdentifier(SqlDataTable.INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL,identifierQuote) + " " + SqlTableDataUtil.getColumnDefinition(DataType.INT) + " PRIMARY KEY";
            statement += SqlTableDataUtil.formQuotedIdentifier(internalRowNumberKeyColumnSchema.getColumnLabel(),identifierQuote) + " " + dataSet.getColumnDefinition(internalRowNumberKeyColumnSchema.getType());
            formedFirst = true;
        }

        for (ColumnSchema columnSchema : this) {
            if (formedFirst)
                statement += ",";
            else
                formedFirst = true;
            //statement += SqlTableDataUtil.formQuotedIdentifier(columnSchema.getColumnLabel(),identifierQuote) + " " + SqlTableDataUtil.getColumnDefinition(columnSchema.getType());
            statement += SqlTableDataUtil.formQuotedIdentifier(columnSchema.getColumnLabel(),identifierQuote) + " " + dataSet.getColumnDefinition(columnSchema.getType());
        }

        if (usesInternalRowNumberKey()) {
            statement += ",CONSTRAINT " + SqlTableDataUtil.formQuotedIdentifier(getTableLabel() + "_" + internalRowNumberKeyColumnSchema.getColumnLabel() + "_key",identifierQuote) + " UNIQUE(" + SqlTableDataUtil.formQuotedIdentifier(internalRowNumberKeyColumnSchema.getColumnLabel(),identifierQuote) + ")";
        }
        statement += ")";
        return statement;
    }

    /**
     * Determine if the table specified by this schema uses an internal (non-visible) column as a row number key.
     *
     * @return {@code true} if this schema specifies the use of an internal column for the row number key.
     */
    public boolean usesInternalRowNumberKey() {
        return internalRowNumberKeyColumnSchema != null;
    }

    /**
     * Determine if the table specified by this schema uses a (visible) column to get its row numbers.
     *
     * @return {@code true} if this schema specifies a (visible) column for its row number, or {@code false} if not.
     */
    public boolean usesColumnAsRowNumberKey() {
        return rowNumberKeyColumnLabel != null;
    }

    /**
     * Get the label of the column (visible or not) specified by this schema to use as the row number key.
     *
     * @return the name or the row number key column specified by this schema.
     */
    public String getRowNumberKeyColumnLabel() {
        return usesInternalRowNumberKey() ? internalRowNumberKeyColumnSchema.getColumnLabel() : rowNumberKeyColumnLabel;
    }

    public SqlTableSchema copy(String tableLabel) {
        SqlTableSchema newSchema = new SqlTableSchema(tableLabel,usesInternalRowNumberKey());
        for (ColumnSchema col : this) {
            newSchema.addColumn(col.getColumnLabel(),col.getType());
        }
        return newSchema;
    }

    public SqlTableSchema copy() {
        return copy(getTableLabel());
    }

    protected boolean blindlyEquals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SqlTableSchema))
            return false;
        if (!super.blindlyEquals(o))
            return false;
        SqlTableSchema schema = (SqlTableSchema) o;
        return (visibleColumnList==null ? schema.visibleColumnList==null : visibleColumnList.equals(schema.visibleColumnList)) &&
                (internalRowNumberKeyColumnSchema==null ? schema.internalRowNumberKeyColumnSchema==null : internalRowNumberKeyColumnSchema.equals(schema.internalRowNumberKeyColumnSchema)) &&
                (rowNumberKeyColumnLabel==null ? schema.rowNumberKeyColumnLabel==null : rowNumberKeyColumnLabel.equals(rowNumberKeyColumnLabel));
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31*result + (visibleColumnList==null ? 0 : visibleColumnList.hashCode());
        result = 31*result + (internalRowNumberKeyColumnSchema==null ? 0 : internalRowNumberKeyColumnSchema.hashCode());
        result = 31*result + (rowNumberKeyColumnLabel==null ? 0 : rowNumberKeyColumnLabel.hashCode());
        return result;
    }

}
