package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.basic.BasicTableKey;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;

import java.util.Map;
import java.util.Collections;

/**
 * <p>
 * The {@code FrozenTableKey} is an immutable {@code TableKey}. Once instantiated, the key cannot be rebuilt, and should
 * only be used for table data objects which are themselves immutable.
 * </p>
 *
 * @param <K>
 *        The type of the table key.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 11:47:31 AM
 */
public class FrozenTableKey<K> extends AbstractTableKey<K> {

    private final Map<K,Integer> index;

    /**
     * Constructor which builds a frozen table key from a specified data table and one of its columns. This will create
     * a table key "frozen" at the current state of the data table.
     *
     * @param table
     *        The data table upon which the key is to be built.
     *
     * @param columnLabel
     *        The data table's column which this key will refer to.
     *
     * @throws TableDataException if the column does not exist in the data table, or if the column does not contain
     *                            unique entries.
     */
    public FrozenTableKey(DataTable table, String columnLabel) {
        //perhaps slightly innefficient (not building key in this class), but show me where that matters
        this(new BasicTableKey<K>(table,columnLabel));
    }

    /**
     * Constructor taking an already existing table key and "freezing" its current state.
     *
     * @param tableKey
     *        The table key whose current state is to be held in this frozen key.
     */
    public FrozenTableKey(TableKey<K> tableKey) {
        super(tableKey.getKeyColumnLabel(),tableKey.getKeyColumnType());
        //injective map probably not necessary because we are already a key, but it never hurts to be sure
        InjectiveMap<K,Integer> transitionIndex = new InjectiveHashMap<K,Integer>();
        for (K key : tableKey.getUniqueKeys())
            transitionIndex.put(key,tableKey.getRowNumber(key));
        index = Collections.unmodifiableMap(transitionIndex);
        buildFrozenIndex();
    }

    protected InjectiveMap<K,Integer> getKeyIndex() {
        return new InjectiveHashMap<K,Integer>(index);
    }

    private void buildFrozenIndex() {
        super.buildIndex();
    }

    /**
     * Does nothing in this class, as the index is frozen.
     */
    public void buildIndex() {
        //do nothing, we are frozen
    }
}
