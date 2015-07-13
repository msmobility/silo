package com.pb.sawdust.tabledata;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;
import static com.pb.sawdust.util.Range.*;

/**
 * @author crf <br/>
 *         Started: Sep 27, 2008 4:01:06 PM
 */
abstract public class AbstractTableKeyTest<K> extends TableKeyTest<K> {
    protected AbstractTableKey<K> abstractKey;

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractKey = (AbstractTableKey<K>) key;
    }

    @Test
    @SuppressWarnings("unchecked")  //ok because key column well defined and typesafe 
    public void testGetKeyIndex() {
        String keyColumn = getKeyColumnLabel();
        InjectiveMap<K,Integer> keyMap = new InjectiveHashMap<K,Integer>();
        for (int i : range(dataTable.getRowCount()))
            keyMap.put((K) dataTable.getCellValue(i,keyColumn),i);
        assertEquals(keyMap,abstractKey.getKeyIndex());
    }

}
