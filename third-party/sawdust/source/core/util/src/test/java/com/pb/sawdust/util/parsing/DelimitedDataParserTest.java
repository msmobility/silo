package com.pb.sawdust.util.parsing;

import com.pb.sawdust.util.Range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.calculator.Function1;

/**
 * @author crf <br/>
 *         Started: Sep 4, 2008 8:56:36 AM
 */
public class DelimitedDataParserTest extends TestBase{

    public static final String DELIMITER_TYPE_KEY = "delimiter type";
    public static enum DELIMITER_TYPE {
        CHAR,
        STRING,
        MULTISTRING
    }

    public static void main(String ... args) {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (DELIMITER_TYPE type : DELIMITER_TYPE.values())
            if (type != DelimitedDataParserTest.DELIMITER_TYPE.MULTISTRING)
                context.add(buildContext(DELIMITER_TYPE_KEY,type));
        addClassRunContext(DelimitedDataParserTest.class,context);
        TestBase.main();
    }

    private Random randomGenerator = new Random();
    private DelimitedDataParser parser;
    private List<String> delimiterStrings;

    @Before
    public void beforeTest() {
        switch ((DELIMITER_TYPE) getTestData(DELIMITER_TYPE_KEY)) {
            case CHAR : {
                char delimiter = getDelimiterChar();
                parser = new DelimitedDataParser(delimiter);
                delimiterStrings = Arrays.asList("" + delimiter);
                break;
            }
            case STRING : {
                String delimiter = "";
                for (int i : Range.range(random.nextInt(2,8)))
                    delimiter += getDelimiterChar();
                parser = new DelimitedDataParser(delimiter);
                delimiterStrings = Arrays.asList(delimiter);
                break;
            }
            case MULTISTRING : {
                delimiterStrings = new LinkedList<String>();
                for (int d : Range.range(random.nextInt(2,8))) {
                    String delimiter = "";
                    for (int i : Range.range(random.nextInt(1,8)))
                        delimiter += getDelimiterChar();
                    delimiterStrings.add(delimiter);
                }
                //parser = new DelimitedDataParser(delimiter); //todo: deal with this constructor
                break;
            }
        }
    }

    @Test
    public void testIsParsable() {
        int quotes = randomGenerator.nextInt(20)+1;
        if (quotes % 2 == 0)
            quotes++;
        assertTrue(parser.isParsable(fillString("\"",getRandomStringArray(quotes,"\""))));
    }

    @Test
    public void testIsNotParsable() {
        int quotes = randomGenerator.nextInt(20)+1;
        if (quotes % 2 != 0)
            quotes++;
        assertFalse(parser.isParsable(fillString("\"",getRandomStringArray(quotes,"\""))));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testParseFailure() {
        String[] random = getDefaultRandomStringArray(randomGenerator.nextInt(10)+2);
        random[1] = "\"" + random[1];
        parser.parse(fillString(random));
    }

    @Test
    public void testParseNoQuotes() {
        String[] random = getDefaultRandomStringArray(randomGenerator.nextInt(10)+2);
        assertArrayEquals(random,parser.parse(fillString(random)));
    }

    @Test
    public void testParseEdgeQuotes() {
        String[] randomStrings = getDefaultRandomStringArray(randomGenerator.nextInt(10)+2);
        String[] randomReference = ArrayUtil.copyArray(randomStrings);
        // r1 --> """r1,""r1", which means [parsed r1] = "r1,"r1
        randomStrings[1] = "\"" + "\"\"" + randomStrings[1] + random.getRandomValue(delimiterStrings) + "\"\"" +  randomStrings[1] + "\"";
        randomReference[1] = "\"" + randomReference[1] + random.getRandomValue(delimiterStrings) + "\"" +  randomReference[1];
        assertArrayEquals(randomReference,parser.parse(fillString(randomStrings)));
    }


    @Test
    public void testParseAllDelimiters() {
        int randomSize = randomGenerator.nextInt(20)+5;
        StringBuilder delimiters = new StringBuilder();
        String[] result = new String[randomSize];
        result[0] = "";
        for (int i = 1; i < randomSize; i++) {
            result[i] = "";
            delimiters.append(random.getRandomValue(delimiterStrings));
        }
        assertArrayEquals(result,parser.parse(delimiters.toString()));
    }



    private static final char[] DELIMITERS = {' ',',',';','\t','?','\\','|'};

    private char getDelimiterChar() {
        return random.getRandomValue(DELIMITERS);
    }

    private String getRandomString(int length,String ... skipStrings) {
        String s = "";
        while (s.length() < length) {
            s += random.nextAsciiString(length - s.length());
            for (String skip : skipStrings)
                s = s.replace(skip,"");
        }
        return s;
    }

    private String[] getDefaultRandomStringArray(int size) {
        String[] skipStrings = new String[delimiterStrings.size()+1];
        int i = 0;
        for (String skipString : delimiterStrings)
            skipStrings[i++] = skipString;
        skipStrings[i] = "\"";
        return getRandomStringArray(size,skipStrings);
    }

    private String[] getRandomStringArray(int size, final String ... skipStrings) {
        return ArrayUtil.apply(new Void[size],
                new Function1<Void,String>() {
                public String apply(Void value) {
                    return getRandomString(randomGenerator.nextInt(10),skipStrings);
                }
            });
    }

    private String fillString(List<String> separators, String ... fill) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String f : fill) {
            if (first)
                first = false;
            else
                sb.append(random.getRandomValue(separators));
            sb.append(f);
        }
        return sb.toString();
    }

    private String fillString(String separator, String ... fill) {
        return fillString(Arrays.asList(separator),fill);
    }

    private String fillString(String ... fill) {
        return fillString(delimiterStrings,fill);
    }

}
