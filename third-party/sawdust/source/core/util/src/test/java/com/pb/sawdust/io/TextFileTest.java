package com.pb.sawdust.io;

import com.pb.sawdust.util.test.TestBase;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The {@code TextFileTest} ...
 *
 * @author crf <br/>
 *         Started Mar 21, 2010 1:49:24 PM
 */
public class TextFileTest extends TestBase {
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
    
    private TextFile tf;


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
    }

    private void writeTemporaryText(String ... text) {
        writeTemporaryText(charset,eol,text);
    }

    private void writeTemporaryText(Charset csn, String eol, String ... text) {
        LineSeparatorPrintWriter pw = LineSeparatorPrintWriter.lineSeparatorPrintWriter(getTempFile(),csn,eol);
        try {
            for (String t : text)
                pw.println(t);
        } finally {
            pw.close();
        }
    }

    private void deleteTempFile() {
        File temp = getTempFile();
        if (temp.exists())
            temp.delete();
    }

    private String getTempFileText() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(getTempFile()),charset));
            char[] buffer = new char[1024];
            StringBuilder sb = new StringBuilder();
            int len;
            while ((len = br.read(buffer)) > -1)
                sb.append(buffer,0,len);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {}
        }
    }

    private TextFile getTextFile() {
        return getTextFile(false);
    }

    private TextFile getTextFile(boolean replace) {
        return new TextFile(getTempFile(),eol,charset,replace);
    }

    @After
    public void afterTest() {
        if (tf != null)
            tf.close();
        deleteTempFile();
    }

    @Test
    public void testReadLine() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        assertEquals(lines[0],tf.readLine());
        tf.close();
    }

    @Test
    public void testReadLineAfterOpen() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        tf.openForReading();
        assertEquals(lines[0],tf.readLine());
        tf.close();
    }

    @Test
    public void testReadString() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        assertEquals(lines[0] + eol + lines[2].substring(0,2),tf.readString(lines[0].length() + 2 + eol.length()));
        tf.close();
    }

    @Test
    public void testReadStringAfterOpen() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        tf.openForReading();
        assertEquals(lines[0] + eol + lines[2].substring(0,2),tf.readString(lines[0].length() + 2 + eol.length()));
        tf.close();
    }

    @Test
    public void testSkipChars() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        tf.skipChars(lines[0].length() + eol.length());
        assertEquals(lines[1],tf.readLine());
        tf.close();
    }

    @Test
    public void testSkipCharsAfterOpen() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        tf.skipChars(lines[0].length() + eol.length());
        assertEquals(lines[1].substring(0,3),tf.readString(3));
        tf.close();
    }

    @Test
    public void testReadAll() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        assertEquals(lines[0] + eol + lines[1] + eol + lines[2] + eol,tf.readAll());
    }

    @Test
    public void testReadAllPartial() {
        String[] lines = new String[] {"line 1","line2","line3"};
        writeTemporaryText(lines);
        tf = getTextFile();
        tf.openForReading();
        tf.readString(lines[0].length());
        assertEquals(eol + lines[1] + eol + lines[2] + eol,tf.readAll());
    }

    @Test
    public void testIterator() {
        String[] lines = new String[] {"line 1","line2","line3"};
        List<String> lineList = Arrays.asList(lines);
        writeTemporaryText(lines);
        List<String> testList = new LinkedList<String>();
        for (String line : getTextFile())
            testList.add(line);
        assertEquals(lineList,testList);
    }

    @Test(expected=IllegalStateException.class)
    public void testMashingIterators() {
        writeTemporaryText("blah","blah");
        tf = getTextFile();
        for (String line : tf)
            for (String line2 : tf)
                line.substring(0);
    }

    @Test
    public void testAppend() {
        if (charset == Charset.forName("UTF-16"))
            return; //byte order marks eff this up
        String t1 = "blah";
        String t2 = "blahn";
        writeTemporaryText(t1);
        tf = getTextFile();
        tf.writeText(t2);
        assertEquals(t1 + eol + t2,getTempFileText());
    }

    @Test
    public void testNew() {
        if (charset == Charset.forName("UTF-16"))
            return; //byte order marks eff this up - unpredictably, no less
        String t1 = "blah";
        String t2 = "blahn";
        writeTemporaryText(t1);
        tf = getTextFile(true);
        tf.writeText(t2);
        assertEquals(t2,getTempFileText());
    }

    @Test
    public void testWriteText() {
        String t = "blah";
        tf = getTextFile();
        tf.writeText(t);
        assertEquals(t,getTempFileText());
    }

    @Test
    public void testWriteLine() {
        String t = "blah";
        tf = getTextFile();
        tf.writeLine(t);
        assertEquals(t + eol,getTempFileText());
    }

    @Test
    public void testWriteTexts() {
        List<String> texts = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.writeText(texts);
        String ref = "";
        for (String line : texts)
            ref += line;
        assertEquals(ref,getTempFileText());
    }

    @Test
    public void testWriteLines() {
        List<String> lines = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.writeLines(lines);
        String ref = "";
        for (String line : lines)
            ref += line + eol;
        assertEquals(ref,getTempFileText());
    }

    @Test
    public void testWriteTextNoAutoFlush() {
        String t = "blah";
        tf = getTextFile();
        tf.setAutoFlush(false);
        tf.writeText(t);
        tf.write();
        assertEquals(t,getTempFileText());
    }

    @Test
    public void testWriteTextNoAutoFlushNoWrite() {
        String t1 = "blah";
        String t2 = "kjhh";
        tf = getTextFile();
        tf.writeText(t1);
        tf.setAutoFlush(false);
        tf.writeText(t2);
        assertEquals(t1,getTempFileText());
    }

    @Test
    public void testWriteLineNoAutoFlush() {
        String t = "blah";
        tf = getTextFile();
        tf.setAutoFlush(false);
        tf.writeLine(t);
        tf.write();
        assertEquals(t + eol,getTempFileText());
    }

    @Test
    public void testWriteLineNoAutoFlushNoWrite() {
        String t1 = "blah";
        String t2 = "kjhh";
        tf = getTextFile();
        tf.writeLine(t1);
        tf.setAutoFlush(false);
        tf.writeLine(t2);
        assertEquals(t1 + eol,getTempFileText());
    }

    @Test
    public void testWriteTextsNoAutoFlush() {
        List<String> texts = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.setAutoFlush(false);
        tf.writeText(texts);
        tf.write();
        String ref = "";
        for (String line : texts)
            ref += line;
        assertEquals(ref,getTempFileText());
    }

    @Test
    public void testWriteTextsNoAutoFlushNoWrite() {
        List<String> texts = Arrays.asList("a","b","c");
        tf = getTextFile();
        String t = "bjl";
        tf.writeText(t);
        tf.setAutoFlush(false);
        tf.writeText(texts);
        assertEquals(t,getTempFileText());
    }

    @Test
    public void testWriteLinesNoAutoFlush() {
        List<String> lines = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.setAutoFlush(false);
        tf.writeLines(lines);
        tf.write();
        String ref = "";
        boolean first = true;
        for (String line : lines)
            ref += line + eol;
        assertEquals(ref,getTempFileText());
    }

    @Test
    public void testWriteLinesNoAutoFlushNoWrite() {
        List<String> lines = Arrays.asList("a","b","c");
        tf = getTextFile();
        String t = "bjl";
        tf.writeText(t);
        tf.setAutoFlush(false);
        tf.writeLines(lines);
        assertEquals(t,getTempFileText());
    }

    @Test
    public void testWriteTextOpen() {
        String t = "blah";
        tf = getTextFile();
        tf.openForWriting();
        tf.writeText(t);
        tf.close();
        assertEquals(t,getTempFileText());
    }

    @Test
    public void testWriteLineOpen() {
        String t = "blah";
        tf = getTextFile();
        tf.openForWriting();
        tf.writeLine(t);
        tf.close();
        assertEquals(t + eol,getTempFileText());
    }

    @Test
    public void testWriteTextsOpen() {
        List<String> texts = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.openForWriting();
        tf.writeText(texts);
        tf.close();
        String ref = "";
        for (String line : texts)
            ref += line;
        assertEquals(ref,getTempFileText());
    }

    @Test
    public void testWriteLinesOpen() {
        List<String> lines = Arrays.asList("a","b","c");
        tf = getTextFile();
        tf.openForWriting();
        tf.writeLines(lines);
        tf.close();
        String ref = "";
        for (String line : lines)
            ref += line + eol;
        assertEquals(ref,getTempFileText());
    }
}
