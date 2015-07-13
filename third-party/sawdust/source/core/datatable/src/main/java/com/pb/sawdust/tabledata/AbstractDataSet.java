package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.TableSchema;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * The {@code AbstractDataSet} provides a skeletal implementation of {@code DataSet} which lowers the implementation bar
 * for developers. It actually implements all methods in {@code DataSet}, adding a single abstract method for
 * transferring input data tables to data tables of the type held by this data set.
 * </p>
 * <p>
 * For implementation convenience, the data tables are held as objects in an internal map collection. In some instances
 * (such as {@code SqlDataSet}), this object is not a convenient or correct way to hold and access the data tables; in
 * such a case, the map can be set to {@code null} and the methods {@link #addTable(DataTable)}, {@link #dropTable(String)},
 * and {@link #getTable(String)}, which use the map, overridden to use whatever custom data structure is being used to
 * hold/access the data tables. 
 * </p>
 *
 * @param <T>
 *        The type of data table this data set holds.
 *
 * @author crf <br/>
 *         Started: May 27, 2008 12:00:12 PM
 */
public abstract class AbstractDataSet<T extends DataTable> implements DataSet<T> {
    
    /**
     * Mapping from table label to table schema. Calls to {@link #addTable(DataTable)}, {@link #addTable(com.pb.sawdust.tabledata.metadata.TableSchema)},
     * and {@link #dropTable(String)} should update this map appropriately.
     */
    protected Map<String,TableSchema> tableLabelToSchema = new HashMap<String,TableSchema>();

    /**
     * Mapping from table label to actual data table. Used by {@link #addTable(DataTable)}, {@link #dropTable(String)},
     * and {@link #getTable(String)}. This object may be set to {@code null} if those methods are overridden to use
     * some other custom data structure.
     */
    protected Map<String,T> tableLabelToTable = new HashMap<String,T>();

    /**
     * Transfer a given data table to the type specified by this data set. This allows arbitrary data tables to be added
     * to this data set.
     *
     * @param table
     *        The data table to be transferred.
     *
     * @return a copy of {@code table} transferred to this data set's table type.
     */
    protected abstract T transferTable(DataTable table);

    public boolean hasTable(String tableLabel) {
        return tableLabelToSchema.containsKey(tableLabel);
    }

    public Set<String> getTableLabels() {
        return tableLabelToSchema.keySet();
    }

    public DataTable addTable(DataTable table) {
        String tableLabel = table.getLabel();
        if (hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_ALREADY_EXISTS,tableLabel);
        tableLabelToTable.put(tableLabel,transferTable(table));
        tableLabelToSchema.put(tableLabel,table.getSchema());
        return table;
    }

    public DataTable addTable(TableSchema schema) {
        throw new UnsupportedOperationException("addTable(TableSchema) not supported by AbstractDataSet");
    }

    public DataTable dropTable(String tableLabel) {
        if (!hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_NOT_FOUND,tableLabel);
        tableLabelToSchema.remove(tableLabel);
        return tableLabelToTable.remove(tableLabel);
    }

    public T getTable(String tableLabel) {
        if (!hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_NOT_FOUND,tableLabel);
        return tableLabelToTable.get(tableLabel);
    }

    public TableSchema getTableSchema(String tableLabel) {
        if (!hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_NOT_FOUND,tableLabel);
        return tableLabelToSchema.get(tableLabel);
    }

    public boolean verifySchemas() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
