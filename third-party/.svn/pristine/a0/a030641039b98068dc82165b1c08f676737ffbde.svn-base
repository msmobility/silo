package com.pb.sawdust.calculator.tabledata.join;

import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.tabledata.metadata.ColumnSchema;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;

import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code JoinSchema} ...
 *
 * @author crf
 *         Started 2/1/12 8:30 AM
 */
public class JoinSchema { //extends TableSchema { //todo: work on this later...
//    private Map<String,TableSchema> schemas;
//
//    public JoinSchema(String tableName, Map<String,TableSchema> schemas) {
//        super(tableName);
//        this.schemas = new HashMap<>(schemas);
//    }
//
//    private TableSchema
//
//
//    @Override
//    public void setPrimaryKeyColumn(String primaryKeyColumn) {
//        if (!hasColumn(primaryKeyColumn))
//            throw new TableDataException("Primary key column must exist in table schema.");
//        this.primaryKeyColumn = primaryKeyColumn;
//    }
//
//    @Override
//    public String getPrimaryKeyColumn() {
//        return primaryKeyColumn;
//    }
//
//    @Override
//    public String getTableLabel() {
//        return tableLabel;
//    }
//
//    @Override
//    public String getColumnLabel(int columnIndex) {
//        return columns.get(columnIndex).getColumnLabel();
//    }
//
//    @Override
//    public int getColumnIndex(String columnLabel) {
//        //todo: really innefficient
//        for (int i : range(columnLabels.length))
//            if (columnLabels[i].equals(columnLabel))
//                return i;
//        throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
//    }
//
//    @Override
//    public ColumnSchema[] getColumnSchemas() {
//        return columns.toArray(new ColumnSchema[columns.size()]);
//    }
//
//    @Override
//    public String[] getColumnLabels() {
//        return columnLabels;
//    }
//
//    @Override
//    public DataType[] getColumnTypes() {
//        return columnTypes;
//    }
//
//    @Override
//    public boolean hasColumn(String columnLabel) {
//        return columnLabelSet.contains(columnLabel);
//    }
//
//    @Override
//    public boolean addColumn(String columnLabel,DataType type) {
//        addColumnToSchema(columnLabel,type);
//        setColumnLabels();
//        setColumnTypes();
//        return true;
//    }
//
//    @Override
//    public boolean dropColumn(String columnLabel) {
//        dropColumnFromSchema(columnLabel);
//        setColumnLabels();
//        setColumnTypes();
//        return true;
//    }
//
//    @Override
//    protected ColumnSchema getColumnSchema(String columnLabel,DataType type) {
//        return new ColumnSchema(columnLabel,type);
//    }
//
//    @Override
//    protected void setColumnLabels() {
//        columnLabels = new String[columns.size()];
//        int counter = 0;
//        for (ColumnSchema columnSchema : columns) {
//            columnLabels[counter++] = columnSchema.getColumnLabel();
//        }
//    }
//
//    @Override
//    protected void setColumnLabels(String[] columnLabels) {
//        this.columnLabels = columnLabels;
//    }
//
//    @Override
//    protected void setColumnTypes() {
//        columnTypes = new DataType[columns.size()];
//        int counter = 0;
//        for (ColumnSchema columnSchema : columns) {
//            columnTypes[counter++] = columnSchema.getType();
//        }
//    }
//
//    @Override
//    protected void setColumnTypes(DataType[] columnTypes) {
//        this.columnTypes = columnTypes;
//    }
//
//    @Override
//    public Iterator<ColumnSchema> iterator() {
//        return columns.iterator();
//    }
//
//    @Override
//    public TableSchema copy(String tableLabel) {
//        TableSchema newSchema = new TableSchema(tableLabel);
//        for (ColumnSchema col : this) {
//            newSchema.addColumn(col.getColumnLabel(),col.getType());
//        }
//        return newSchema;
//    }
//
//    @Override
//    public TableSchema copy() {
//        return copy(getTableLabel());
//    }
//
//    public final boolean equals(Object o) {
//        return this.blindlyEquals(o) && ((TableSchema) o).blindlyEquals(this);
//    }
//
//    /**
//     * Determine if this object is equal to another. This method is intended to allow class inheritance without breaking
//     * the {@code equals(Object)} contract. Specifically, when extending this class and adding (significant) internal
//     * additions, this method should be overridden ({@code equals(Object)} cannot be overridden as it is declared final).
//     * The idiom for overriding this method in an extending class {@code Extension} is as follows:
//     * <code><pre>
//     *     protected boolean blindlyEquals(Object o) {
//     *         if (o == this)
//     *             return true;
//     *         if (!(o instanceof Extension))
//     *             return false;
//     *         if (!super.blindlyEquals(o))
//     *             return false;
//     *         ...//(equals checks specific to Extension)
//     *     }
//     * </pre></code>
//     *
//     * @param o
//     *        The object to compare.
//     *
//     * @return {@code true} if {@code o} is equal to this object, {@code false} if not.
//     */
//    protected boolean blindlyEquals(Object o) {
//        if (o == this)
//            return true;
//        if (!(o instanceof TableSchema))
//            return false;
//        TableSchema schema = (TableSchema) o;
//        return tableLabel.equals(schema.tableLabel) &&
//                columns.equals(schema.columns) &&
//                columnLabelSet.equals(schema.columnLabelSet) &&
//                Arrays.equals(columnLabels,schema.columnLabels) &&
//                Arrays.equals(columnTypes,schema.columnTypes) &&
//                (primaryKeyColumn==null ? schema.primaryKeyColumn==null: primaryKeyColumn.equals(schema.primaryKeyColumn));
//    }
//
//    public int hashCode() {
//        int result = 17;
//        result = 31*result + columns.hashCode();
//        result = 31*result + columnLabelSet.hashCode();
//        result = 31*result + (columnLabels==null ? 0 : columnLabels.hashCode());
//        result = 31*result + (columnTypes==null ? 0 : columnTypes.hashCode());
//        result = 31*result + (primaryKeyColumn==null ? 0 : primaryKeyColumn.hashCode());
//        return result;
//    }
//

}
