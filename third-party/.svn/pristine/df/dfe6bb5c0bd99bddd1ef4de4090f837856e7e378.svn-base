package com.pb.sawdust.io;

import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.test.TestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.*;

/**
 * The {@code IterableFileReaderTest} ...
 *
 * @author crf <br/>
 *         Started Mar 23, 2010 11:39:04 AM
 */
public class IterableFileReaderTest extends TestBase {
    public static final String CHARSET_KEY = "charset";
    public static final String EOL_KEY = "eol";

    private static enum EOL {
        UNIX("\n"),
        WINDOWS("\r\n"),
        MAC("\r");

        private final String eol;
        private final String eolString;

        private EOL(String eol) {
            this.eol = eol;
            eolString = eol.replace("\n","\\n").replace("\r","\\r");
        }

        public String toString() {
            return eolString;
        }
    }

    private Charset charset;
    private String eol;
    IterableFileReader.LineIterableReader reader;

    private List<String> lines;
    private String comment = "// this is comment";

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        String[] charsets = new String[] {"US-ASCII","UTF-8","UTF-16"};
        List<Map<String,Object>> contexts = new LinkedList<Map<String,Object>>();
        for (String charset : charsets) {
            for (EOL eol : EOL.values()) {
                Map<String,Object> c = buildContext(CHARSET_KEY, Charset.forName(charset));
                c.put(EOL_KEY,eol);
                contexts.add(c);
            }
        }
        addClassRunContext(this.getClass(),contexts);
        return super.getAdditionalTestClasses();
    }

    private File getTempFile() {
        return getTemporaryFile(DelimitedDataReaderTest.class,"temp" + Thread.currentThread().getId() + ".txt");
    }

    @Before
    public void beforeTest() {
        charset = (Charset) getTestData(CHARSET_KEY);
        eol = ((EOL) getTestData(EOL_KEY)).eol;
        lines = Arrays.asList(random.nextAsciiString(random.nextInt(4,5)),
                              random.nextAsciiString(random.nextInt(4,5)),
                              random.nextAsciiString(random.nextInt(4,5)),
                              comment,
                              random.nextAsciiString(random.nextInt(4,5)));
        TextFile tf = new TextFile(getTempFile(),eol,charset);
        tf.writeLines(lines);
    }

    @After
    public void afterTest() {
        if (reader != null)
            reader.close();
        File f = getTempFile();
        if (f.exists())
            f.delete();
    }

    @Test
    public void testIterator() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertEquals(lines,readLines);
    }

    @Test
    public void testIteratorWithFilter() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset,new Filter<String>() {
            public boolean filter(String input) {
                return !input.startsWith("//");
            }
        });
        List<String> newLines = new LinkedList<String>();
        for (String line : lines)
            if (!line.equals(comment))
                newLines.add(line);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertEquals(newLines,readLines);
    }

    @Test
    public void testReaderClosed() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertTrue(reader.isClosed());
    }

    @Test
    public void testReaderNotClosed() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        reader.setCloseAtIterationEnd(false);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertFalse(reader.isClosed());
    }

    @Test
    public void testCloseIsClosed() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        Iterator<String> it = reader.iterator();
        it.next();
        assertFalse(reader.isClosed());
        reader.close();
        assertTrue(reader.isClosed());
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFail() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        Iterator<String> it = reader.iterator();
        it.next();
        reader.close();
        it.next();
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFail2() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        reader.close();
        reader.iterator();
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFail3() {
        reader = IterableFileReader.getLineIterableFile(getTempFile(),charset);
        reader.iterator();
        reader.iterator();
    }

    @Test
    public void testIteratorLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        List<String> newLines = new LinkedList<String>();
        for (String line : lines)
            newLines.add(line + eol);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertEquals(newLines,readLines);
    }

    @Test
    public void testIteratorWithFilterLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset,new Filter<String>() {
            public boolean filter(String input) {
                return !input.startsWith("//");
            }
        });
        List<String> newLines = new LinkedList<String>();
        for (String line : lines)
            if (!line.equals(comment))
                newLines.add(line + eol);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertEquals(newLines,readLines);
    }

    @Test
    public void testReaderClosedLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertTrue(reader.isClosed());
    }

    @Test
    public void testReaderNotClosedLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        reader.setCloseAtIterationEnd(false);
        List<String> readLines = new LinkedList<String>();
        for (String line : reader)
            readLines.add(line);
        assertFalse(reader.isClosed());
    }

    @Test
    public void testCloseIsClosedLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        Iterator<String> it = reader.iterator();
        it.next();
        assertFalse(reader.isClosed());
        reader.close();
        assertTrue(reader.isClosed());
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFailLineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        Iterator<String> it = reader.iterator();
        it.next();
        reader.close();
        it.next();
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFail2LineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        reader.close();
        reader.iterator();
    }

    @Test(expected=IllegalStateException.class)
    public void testCloseIteratorFail3LineTerminator() {
        reader = IterableFileReader.getLineIterableFileWithLineTerminator(getTempFile(),charset);
        reader.iterator();
        reader.iterator();
    }

}
