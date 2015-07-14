package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractTableKey;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;

/**
 * The {@code BasicTableKey} class provides a simple implementation of the {@code TableKey} interface.
 *
 * @param <K>
 *        The type of the table key.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 8:20:21 AM
 */
public class BasicTableKey<K> extends AbstractTableKey<K> {

    private DataTable table;

    /**
     * Constructor specifying the table and column on which the key will be built.
     *
     * @param table
     *        The table the key will be based on.
     *
     * @param columnLabel
     *        The label of the column the key will be built on.
     */
    public BasicTableKey(DataTable table, String columnLabel) {
        super(columnLabel,table.getColumnDataType(columnLabel));
        this.table = table;
        buildIndex();
    }

    protected InjectiveMap<K, Integer> getKeyIndex() {
        InjectiveMap<K,Integer> index = new InjectiveHashMap<K,Integer>();
        int counter = 0;
        String columnLabel = getKeyColumnLabel();
        for (DataRow row : table) {
            @SuppressWarnings("unchecked") //suppressed because TableKey doc (contract) states that K must correspond
                                           //to the data type of the key column
            K keyValue = (K) row.getCell(columnLabel);
            if (index.containsKey(keyValue))
                throw new TableDataException(TableDataException.KEY_COLUMN_NOT_UNIQUE,columnLabel);
            index.put(keyValue,counter++);
        }
        return index;
    }
}
