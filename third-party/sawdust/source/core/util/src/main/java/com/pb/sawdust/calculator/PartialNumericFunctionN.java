package com.pb.sawdust.calculator;

import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.format.TextFormat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code PartialNumericFunctionN} class provides a numeric function in which some arguments have be pre-specified.
 * That is, this class wraps a {@code NumericFunctionN} and fills some of its arguments with values from a {@link NumericValuesProvider}.
 * Thus, this class is similar to creating a curried function, and its argument count is the original function's minus
 * the number of pre-specified values. The pre-specified arguments (from the base data provider) are not captured until
 * the function is evaluated, so it is possible to build some dynamicsim into the "fixed" part of the evaluation of the
 * function, through the use of an appropriate, mutating {@code NumericValuesProvider}.
 * <p>
 * This class allows for the remaining arguments to be reordered as well, as it utilizes a mapping from its argument index
 * numbers to those of the original function which is flexible in it application.
 *
 * @author crf <br/>
 *         Started 7/15/11 4:48 PM
 */
public class PartialNumericFunctionN extends NumericFunctionN {
    private final NumericFunctionN baseFunction;
    private final int[] dimensionMap;
    private final int[] fullDimensionMap;
    private final NumericValuesProvider baseProvider;
    private volatile String symbolicFormat;

    /**
     * Constructor specifying the base function, argument (dimension) mapping, and base values provider. The dimension map
     * provides a mapping from the index of each argument of this function to the argument number of the base function.
     * For example, if a base function takes three arguments, and a partial function based on it pre-specifies the second
     * argument, then a possible dimension mapping would be
     * <pre>
     *     <code>
     *     [0,2]
     *     </code>
     * </pre>
     * which indicates that the first argument of the partial function maps to the first of the base function, and that
     * the second argument of the partial function maps to the base's third argument.
     * <p>
     * The base values provider must be of the same size as the number of arguments in the <i>base</i> function; the
     * arguments/values in the dimension map will be ignored, but must be included.
     *
     *
     * @param baseFunction
     *        The function this partial function is built from.
     *
     * @param dimensionMap
     *        The mapping from this function's arguments to the (remaining) base function arguments.
     *
     * @param baseProvider
     *        The values provider used to get the pre-specified arguments for the function.
     *
     * @throws IllegalArgumentException if the size of {@code baseFunction} and {@code baseProvider} are not equal, or if
     *                                  any of the indices in {@code dimensionMap} repeat or are out of bounds for the
     *                                  base function's arguments.
     */
    public PartialNumericFunctionN(NumericFunctionN baseFunction, int[] dimensionMap, NumericValuesProvider baseProvider) {
        if (baseProvider.getLength() != baseFunction.getArgumentCount())
            throw new IllegalArgumentException(String.format("Values provider length (%d) must equal function argument count (%d)",baseProvider.getLength(),baseFunction.getArgumentCount()));
        Set<Integer> indicesUsed = new HashSet<Integer>();
        for (int i : dimensionMap) {
            if (!indicesUsed.add(i))
                throw new IllegalArgumentException("Dimension map cannot have repeated index: " + i);
            if (i < 0 || i >= baseFunction.getArgumentCount())
                throw new IllegalArgumentException(String.format("Dimension map contains dimension index (%d) which is out of bounds for a base function with %d arguments.",i,baseFunction.getArgumentCount()));
        }
        this.baseFunction = baseFunction;
        this.dimensionMap = ArrayUtil.copyArray(dimensionMap);
        fullDimensionMap = new int[baseProvider.getLength()];
        Arrays.fill(fullDimensionMap, -1);
        for (int i = 0; i < dimensionMap.length; i++)
            fullDimensionMap[dimensionMap[i]] = i;
        this.baseProvider = baseProvider;
    }

    @Override
    public int getArgumentCount() {
        return dimensionMap.length;
    }

    @Override
    protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedByte(0,new PartialNumericValuesProvider(values,start));
    }

    @Override
    protected short applyUncheckedShort(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedShort(0,new PartialNumericValuesProvider(values,start));
    }

    @Override
    protected int applyUncheckedInt(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedInt(0,new PartialNumericValuesProvider(values,start));
    }

    @Override
    protected long applyUncheckedLong(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedLong(0,new PartialNumericValuesProvider(values,start));
    }

    @Override
    protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedFloat(0,new PartialNumericValuesProvider(values,start));
    }

    @Override
    protected double applyUncheckedDouble(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedDouble(0, new PartialNumericValuesProvider(values, start));
    }

    public String getSymbolicFormat(TextFormat argumentFormat) {
        String symbolicFormat = baseFunction.getSymbolicFormat(argumentFormat);
        int[] argmap = new int[dimensionMap.length];
        for (int i : range(fullDimensionMap.length)) {
            if (fullDimensionMap[i] == -1) { //fixed
                String format = argumentFormat.getFormat(i+1);
                String result = argumentFormat.format((argumentFormat.getConversion() == TextFormat.Conversion.INTEGER) ? baseProvider.getInt(i) : baseProvider.getDouble(i));
                symbolicFormat = symbolicFormat.replace(format,result);
            } else { //free
                argmap[fullDimensionMap[i]] = i;
            }
        }
        for (int i : range(argmap.length))
            symbolicFormat = symbolicFormat.replace(argumentFormat.getFormat(argmap[i]+1),argumentFormat.getFormat(i+1));
        return symbolicFormat;
    }

    private class PartialNumericValuesProvider extends NumericValuesProvider {
        private final NumericValuesProvider supplementalProvider;
        private final int start;

        private PartialNumericValuesProvider(NumericValuesProvider supplementalProvider, int start) {
            this.supplementalProvider = supplementalProvider;
            this.start = start;
        }

        @Override
        public int getLength() {
            return baseProvider.getLength();
        }

        @Override
        public byte getByte(int i) {
            int d = fullDimensionMap[i];
            return d == -1 ? baseProvider.getByte(i) : supplementalProvider.getByte(d+start);
        }

        @Override
        public short getShort(int i) {
            int d = fullDimensionMap[i];
            return d == -1 ? baseProvider.getShort(i) : supplementalProvider.getShort(d + start);
        }

        @Override
        public int getInt(int i) {
            int d = fullDimensionMap[i];
            return d == -1 ? baseProvider.getInt(i) : supplementalProvider.getInt(d + start);
        }

        @Override
        public long getLong(int i) {
            int d = fullDimensionMap[i];
            return d == -1 ? baseProvider.getLong(i) : supplementalProvider.getLong(d + start);
        }

        @Override
        public float getFloat(int i) {
            int d = fullDimensionMap[i];
            return d == -1 ? baseProvider.getFloat(i) : supplementalProvider.getFloat(d + start);
        }

        @Override
        public double getDouble(int i) {
            int d = fullDimensionMap[i];
            //return d == -1 ? baseProvider.getDouble(i) : supplementalProvider.getDouble(d + start);
            double dd =  d == -1 ? baseProvider.getDouble(i) : supplementalProvider.getDouble(d + start);
            return dd;
        }

        @Override
        public JavaType getValueTypePreference() {
            return baseProvider.getValueTypePreference();
        }
    }
}
