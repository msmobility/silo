package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractTableIndexTest;
import com.pb.sawdust.tabledata.TableIndex;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.util.test.TestBase;

import java.security.MessageDigest;

import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Mar 10, 2009 4:16:42 PM
 */
public class DigestTableIndexTest<I> extends AbstractTableIndexTest<I, DigestTableIndex.ByteDigest> {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected TableIndex<I> getTableIndex(String[] indexColumnLabels, DataTable table) {
        return new DigestTableIndex<I>(table,indexColumnLabels);
    }

    @SuppressWarnings("unchecked") //a valid warning, butframework will prevent errors
    public void testBuildIndexKey() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            Object[] a = new Object[] {"",7,false};
            for (Object ao : a)
                md.update(ao.toString().getBytes());
            assertEquals(new DigestTableIndex.ByteDigest(md.digest()),getBuildIndexKey((I[]) a));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
