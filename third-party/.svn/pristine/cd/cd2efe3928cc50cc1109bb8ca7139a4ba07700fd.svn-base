package com.pb.sawdust.io;

import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.test.TestBase;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * The {@code DelimitedDataReaderTest} ...
 *
 * @author crf <br/>
 *         Started Mar 23, 2010 9:01:36 AM
 */
public class DelimitedDataReaderTest extends TestBase {
    private Charset charset = Charset.forName("UTF-8"); //alternative

    public static void main(String ... args) {
        TestBase.main();
    }

    private File getTempFile() {
        return getTemporaryFile(DelimitedDataReaderTest.class,"temp.txt");
    }

    @After
    public void afterTest() {
        File f = getTempFile();
        if (f.exists())
            f.delete();
    }

    @Test
    public void testGetData() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        String[][] expected = {l1,l2,l3};
        assertArrayEquals(expected,new DelimitedDataReader(',').getData(getTempFile()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDataBadData() {
        TextFile tf = new TextFile(getTempFile());
        tf.writeLines("a,b,\"c,\"\"");
        new DelimitedDataReader(',').getData(getTempFile());
    }

    @Test
    public void testGetIterator() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile());
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorBadData() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile());
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetIteratorWithFilter() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line2a = "//comment";
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line2a,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        });
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorWithFilterBadData() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"";
        String line2a = "//comment";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2a,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        });
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetDataMultiline() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        String[][] expected = {l1,l2,l3};
        assertArrayEquals(expected,new DelimitedDataReader(',',true).getData(getTempFile()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDataBadDataMultiline() {
        TextFile tf = new TextFile(getTempFile());
        tf.writeLines("a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv");
        new DelimitedDataReader(',',true).getData(getTempFile());
    }

    @Test
    public void testGetIteratorMultiLine() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile());
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorBadDataMultiline() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile());
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetIteratorWithFilterMultiline() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line2a = "//comment";
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line2a,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        });
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorWithFilterBadDataMultiline() {
        TextFile tf = new TextFile(getTempFile());
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv";
        String line2a = "//comment";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2a,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        });
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetDataCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        String[][] expected = {l1,l2,l3};
        assertArrayEquals(expected,new DelimitedDataReader(',').getData(getTempFile(),charset));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDataBadDataCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        tf.writeLines("a,b,\"c,\"\"");
        new DelimitedDataReader(',').getData(getTempFile(),charset);
    }

    @Test
    public void testGetIteratorCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),charset);
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorBadDataCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),charset);
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetIteratorWithFilterCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"\"";
        String[] l2 = {"a","b","c,\""};
        String line2a = "//comment";
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line2a,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        },charset);
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorWithFilterBadDataCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"";
        String line2a = "//comment";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2a,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',').getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        },charset);
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetDataMultilineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        String[][] expected = {l1,l2,l3};
        assertArrayEquals(expected,new DelimitedDataReader(',',true).getData(getTempFile(),charset));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDataBadDataMultilineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        tf.writeLines("a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv");
        new DelimitedDataReader(',',true).getData(getTempFile(),charset);
    }

    @Test
    public void testGetIteratorMultiLineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),charset);
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorBadDataMultilineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),charset);
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }

    @Test
    public void testGetIteratorWithFilterMultilineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String[] l1 = {"a","b","c"};
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afb\"";
        String[] l2 = {"a","b","c,\"" + FileUtil.getLineSeparator() + "afb"};
        String line2a = "//comment";
        String line3 = "d,e,f";
        String[] l3 = {"d","e","f"};
        tf.writeLines(line1,line2,line2a,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        },charset);
        try {
            Iterator<String[]> it = ir.iterator();
            assertArrayEquals(l1,it.next());
            assertArrayEquals(l2,it.next());
            assertArrayEquals(l3,it.next());
            assertFalse(it.hasNext());
        } finally {
            ir.close();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetIteratorWithFilterBadDataMultilineCharset() {
        TextFile tf = new TextFile(getTempFile(),charset);
        String line1 = "a,b,c";
        String line2 = "a,b,\"c,\"\"" + FileUtil.getLineSeparator() + "afv";
        String line2a = "//comment";
        String line3 = "d,e,f";
        tf.writeLines(line1,line2a,line2,line3);
        IterableReader<String[]> ir = new DelimitedDataReader(',',true).getLineIterator(getTempFile(),new Filter<String>() {
            public boolean filter(String data) {
                return !data.startsWith("//");
            }
        },charset);
        try {
            int a;
            for (String[] line : ir)
                a = 1;
        } finally {
            ir.close();
        }
    }
}
