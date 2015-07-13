package com.pb.sawdust.util.parsing;

import com.pb.sawdust.util.JavaType;

/**
 *
 * The {@code FixedWidthStringParser} class parses character arrays into Strings formed from character sub-arrays.
 *
 * @see ArrayParser
 * 
 * @author crf <br/>
 *         Started: Jul 8, 2008 2:06:53 PM
 */
public class FixedWidthStringParser extends ArrayParser<char[],String> {

    /**
     * Constructor specifying the widths of the subarrays used when parsing inputs. All widths must be greater than 0,
     * except the final width, which may also be {@link #OPEN_FINAL_SUBARRAY_WIDTH}, indicating an "open" (variable width)
     * final subarray. An open subarray allows the final subarray to be empty if the input array's length is equal to the
     * sum of positive widths.
     *
     * @param widths
     *        The widths of the subarrays resulting from the parsing of an input array.
     *
     * @throws IllegalArgumentException if element in {@code widths} is less than one (final element is allowed to be
     *                                  equal to {@link #OPEN_FINAL_SUBARRAY_WIDTH}).
     */
    public FixedWidthStringParser(int[] widths) {
        super(widths);
    }
    
    /**
     * Constructor specifying breakpoint positions and the input array length. Widths (as used in {@code ArrayParser(int[])}
     * constructor) are determined implicitly by the positions. If {@code positions[0] == 0}, then the size of the output
     * array will be {@code positions.length}, otherwise the size of the output array will be {@code positions.length + 1}
     * (the parsing algorithm assumes a start at input element 0). A positive {@code arrayLength} parameter determines the
     * width of the final element as {@code arrayLength - positions[positions.length-1]}. Alternatively, the {@code arrayLength}
     * parameter may be set to {@link #OPEN_FINAL_SUBARRAY_WIDTH}, which sets the final subarray to be open (equivalent
     * to setting the final width to {@code OPEN_FINAL_SUBARRAY_WIDTH}).
     *
     * @param positions
     *        The breakpoint positions for the subarrays resulting from the parsing of an input array.
     *
     * @param arrayLength
     *        The minimum length of an input array, or {@code OPEN_FINAL_SUBARRAY_WIDTH} for an open final subarray.
     *
     * @throws IllegalArgumentException if <code>arrayLength != OPEN_FINAL_SUBARRAY_WIDTH</code> and arrayLength is less
     *                                  than the sum of the elements in {@code positions}.
     */
    public FixedWidthStringParser(int[] positions, int arrayLength) {
       super(positions,arrayLength);
    }

    /**
     * Returns {@code JavaType.CHAR}, the component type of {@code char[]}.
     *
     * @return {@code JavaType.CHAR};
     */
    JavaType getJavaType() {
        return JavaType.CHAR;
    }

    String[] getOutputArray(int length, char[] inputArray) {
        return new String[length];
    }

    String formOutputElement(char[] inputSegment) {
        return new String(inputSegment);
    }

    /**
     * Parse a string using this parser. This call is equivalenr to calling {@code parse(input.toCharArray[])}.
     *
     * @param input
     *        The string to parse.
     *
     * @return an array of strings corresponding to the parsed {@code input}.
     */
    public String[] parse(String input) {
        return parse(input.toCharArray());
    }
}
