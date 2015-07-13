package com.pb.sawdust.util.format;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.pb.sawdust.util.test.TestBase;

import java.util.*;

/**
 * @author crf <br/>
 *         Started: Sep 18, 2008 10:21:23 AM
 */
public class DelimitedDataFormatTest extends TestBase {
    public static final String DELIMITER_KEY = "delimiter";
    protected static char[] availableDelimiters = new char[] {',',';','.','\'',' '};

    protected char delimiter;
    protected DelimitedDataFormat formatter;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (char delimiter : availableDelimiters)
            context.add(buildContext(DELIMITER_KEY,delimiter));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    @Before
    public void beforeTest() {
        delimiter = (Character) getTestData(DELIMITER_KEY);
        formatter = new DelimitedDataFormat(delimiter);
    }
    
    @Test
    public void testSimpleFormat() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        String finalResult = "" + d1 + delimiter + d2 + delimiter + d3;
        assertEquals(finalResult,formatter.format(d1,d2,d3));
    }

    @Test
    public void testFormatWithDelimiter() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg" + delimiter + "hfghfj";
        Object d3 = true;
        String finalResult = "" + d1 + delimiter + "\"" + d2 + "\"" + delimiter + d3;
        assertEquals(finalResult,formatter.format(d1,d2,d3));
    }

    @Test
    public void testFormatWithQuote() {
        Object d1 = random.nextInt();
        Object d2a = "ghgkgkhg";
        Object d2b = "hgkjgkhg";
        Object d2 = d2a + "\"" + d2b;
        Object d3 = true;
        String finalResult = "" + d1 + delimiter + "\"" + d2a + "\"\"" + d2b + "\"" + delimiter + d3;
        assertEquals(finalResult,formatter.format(d1,d2,d3));
    }

    @Test
    public void testFormatWithTextFormats() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat f3 = new TextFormat(TextFormat.Conversion.BOOLEAN);
        TextFormat[] formats = new TextFormat[] {f1,f2,f3};
        String finalResult = "" + f1.format(d1) + delimiter + f2.format(d2) + delimiter + f3.format(d3);
        assertEquals(finalResult,formatter.format(formats,d1,d2,d3));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testFormatWithTextFormatsSizeFailure() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat[] formats = new TextFormat[] {f1,f2};
        formatter.format(formats,d1,d2,d3);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testFormatWithTextFormatsArgumentFailure() {
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat f3 = new TextFormat(TextFormat.Conversion.BOOLEAN);
        TextFormat[] formats = new TextFormat[] {f1,f2,f3};
        formatter.format(formats,d2,d2,d3);
    }

    @Test
    public void testFormatWithFormats() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat f3 = new TextFormat(TextFormat.Conversion.BOOLEAN);
        String[] formats = new String[] {f1.getFormat(),f2.getFormat(),f3.getFormat()};
        String finalResult = "" + f1.format(d1) + delimiter + f2.format(d2) + delimiter + f3.format(d3);
        assertEquals(finalResult,formatter.format(formats,d1,d2,d3));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testFormatWithFormatsSizeFailure() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        String[] formats = new String[] {f1.getFormat(),f2.getFormat()};
        formatter.format(formats,d1,d2,d3);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testFormatWithFormatsArgumentFailure() {
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f1 = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat f3 = new TextFormat(TextFormat.Conversion.BOOLEAN);
        String[] formats = new String[] {f1.getFormat(),f2.getFormat(),f3.getFormat()};
        formatter.format(formats,d2,d2,d3);
    }

    @Test(expected= IllegalFormatException.class)
    public void testFormatWithFormatsFormatFailure() {
        Object d1 = random.nextInt();
        Object d2 = "ghgkgkhg";
        Object d3 = true;
        TextFormat f2 = new TextFormat(TextFormat.Conversion.STRING_UPPER_CASE);
        TextFormat f3 = new TextFormat(TextFormat.Conversion.BOOLEAN);
        String f1 = "%jklj";
        String[] formats = new String[] {f1,f2.getFormat(),f3.getFormat()};
        formatter.format(formats,d1,d2,d3);
    }
}
