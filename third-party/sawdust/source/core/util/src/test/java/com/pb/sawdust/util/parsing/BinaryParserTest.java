package com.pb.sawdust.util.parsing;

import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.util.test.TestBase;

/**
 * @author crf <br/>
 *         Started: Sep 3, 2008 4:32:11 PM
 */
public class BinaryParserTest extends ArrayParserTest<byte[],byte[]> {

    public static void main(String ... args) {
        TestBase.main();
    }

    ArrayParser<byte[], byte[]> getArrayParser(int[] widths) {
        return new BinaryParser(widths);
    }

    ArrayParser<byte[], byte[]> getArrayParser(int[] positions, int length) {
        return new BinaryParser(positions,length);
    }

    byte[] getSampleInput(int length) {
        byte[] input = new byte[length];
        randomGenerator.nextBytes(input);
        return input;
    }

    @Test
    public void testFormOutputElement() {
        byte[] input = getSampleInput(widths.length);
        assertArrayEquals(input,arrayParser.formOutputElement(input));
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
        byte[] input = new byte[] {1,4,7,8};
        byte[][] output = new byte[3][];
        output[0] = new byte[] {1};
        output[1] = new byte[] {4,7};
        output[2] = new byte[] {8};

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
        byte[] sampleInput = getSampleInput(arrayParser.getMinLength() + finalElementSize);
        byte[] finalElement = new byte[finalElementSize];
        System.arraycopy(sampleInput,arrayParser.getMinLength(),finalElement,0,finalElementSize);
        assertArrayEquals(finalElement,arrayParser.parse(sampleInput)[widths.length-1]);
    }
}
