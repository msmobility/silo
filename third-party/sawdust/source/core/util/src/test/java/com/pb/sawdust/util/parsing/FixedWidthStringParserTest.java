package com.pb.sawdust.util.parsing;

import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.util.test.TestBase;
import static com.pb.sawdust.util.Range.*;

/**
 * @author crf <br/>
 *         Started: Sep 3, 2008 7:33:14 PM
 */
public class FixedWidthStringParserTest extends ArrayParserTest<char[],String> {

    public static void main(String ... args) {
        TestBase.main();
    }
    
    ArrayParser<char[], String> getArrayParser(int[] widths) {
        return new FixedWidthStringParser(widths);
    }

    ArrayParser<char[], String> getArrayParser(int[] positions, int length) {
        return new FixedWidthStringParser(positions,length);
    }

    char[] getSampleInput(int length) {
        return new char[length];
    }

    @Test
    public void testFormOutputElement() {
        char[] input = getSampleInput(widths.length);
        assertEquals(String.valueOf(input),arrayParser.formOutputElement(input));
    }

    @Test
    public void testParse() {
        testParseTest(true);
    }

    @Test
    public void testParsePositionsSize() {
        testParseTest(false);
    }

    private void testParseTest(boolean useWidths) {
        int[] widths = new int[] {1,2,1};
        int[] positions = new int[widths.length];
        int size = 0;
        for (int i = 0; i < widths.length; i++) {
            positions[i] = size;
            size += widths[i];
        }
        char[] input = new char[] {'a','b','c','d'};
        String[] output = new String[3];
        output[0] = "a";
        output[1] = "bc";
        output[2] = "d";

        if (useWidths)
            arrayParser = getArrayParser(widths);
        else
            arrayParser = getArrayParser(positions,size);
        assertArrayEquals(output,arrayParser.parse(input));
    }

    @Test
    public void testOpenParseFinalElement() {
        setOpenParser();
        int finalElementSize = randomGenerator.nextInt(10) + 1;
        char[] sampleInput = getSampleInput(arrayParser.getMinLength() + finalElementSize);
        for (int i : range(sampleInput.length))
            sampleInput[i] = (char) randomGenerator.nextInt(155);
        char[] finalElement = new char[finalElementSize];
        System.arraycopy(sampleInput,arrayParser.getMinLength(),finalElement,0,finalElementSize);
        assertEquals(new String(finalElement),arrayParser.parse(sampleInput)[widths.length-1]);
    }
}
