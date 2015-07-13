package com.pb.sawdust.calculator;


import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.format.TextFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * The {@code SimpleCollapsibleFunction} class provides a simple, shell implementation of the {@code CollapsibleFunction}
 * interface. It sets up a collapsible function as a three-step process:
 * <ol>
 *     <li>
 *         When constructed, an instance will have its "collapsible" dimension/argument numbers specified.
 *     </li>
 *     <li>
 *         A numeric values provider of dimensionality/size equal to the full size (argument count) of the base function
 *         will be used to specify the collapsible argument/dimension values. That is, the values provider will have "slots"
 *         for all arguments in the base function, but only those in the collapsible arguments/dimensions spots will be
 *         used. These will be extracted to a numeric values provider which contains only the entries for the collapsible
 *         arguments/dimensions (it will be the same size as the {code collapsingDimensions}), which is then passed to
 *         the abstract {@link #collapseFunctionValuesCollapsed(NumericValuesProvider)} method to get the collapsed function.
 *     </li>
 *     <li>
 *         The collapsed function can then be used with arguments specified only for the uncollapsed arguments/dimensions.
 *     </li>
 * </ol>
 *
 * @author crf <br/>
 *         Started 7/15/11 10:56 AM
 */
public abstract class SimpleCollapsibleFunction extends NumericFunctionN implements CollapsibleFunction {
    private final NumericFunctionN baseFunction;
    private final int[] collapsingDimensions;
    private List<Integer> collapsingDimensionsList;

    /**
     * Constructor specifying the base function, and the dimensions that will be collapsed.
     *
     * @param baseFunction
     *        The base function.
     *
     * @param collapsingDimensions
     *        The dimensions that will be collapsed.
     */
    public SimpleCollapsibleFunction(NumericFunctionN baseFunction, int[] collapsingDimensions) {
        this.baseFunction = baseFunction;
        this.collapsingDimensions = ArrayUtil.copyArray(collapsingDimensions);
        collapsingDimensionsList = Collections.unmodifiableList(Arrays.asList(ArrayUtil.toIntegerArray(collapsingDimensions)));
    }

    public NumericFunctionN collapseFunction(final NumericValuesProvider provider) {
        final NumericFunctionN numericFunction =  collapseFunctionValuesCollapsed(new CollapsedNumericValuesProvider(provider));
        return new NumericFunctionN() {

            @Override
            public int getArgumentCount() {
                return numericFunction.getArgumentCount();
            }

            @Override
            protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedByte(start,values);
            }

            @Override
            protected short applyUncheckedShort(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedShort(start,values);
            }

            @Override
            protected int applyUncheckedInt(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedInt(start,values);
            }

            @Override
            protected long applyUncheckedLong(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedLong(start,values);
            }

            @Override
            protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedFloat(start,values);
            }

            @Override
            protected double applyUncheckedDouble(int start, NumericValuesProvider values) {
                return numericFunction.applyUncheckedDouble(start,values);
            }

            public String getSymbolicFormat(TextFormat argumentFormat) {
                String symbolicFormat = baseFunction.getSymbolicFormat();
                int counter = 0;
                for (int i : range(baseFunction.getArgumentCount())) {
                    if (counter < collapsingDimensions.length && i == collapsingDimensions[counter]) {
                        String format = argumentFormat.getFormat(i+1);
                        String result = argumentFormat.format((argumentFormat.getConversion() == TextFormat.Conversion.INTEGER) ? provider.getInt(i) : provider.getDouble(i));
                        symbolicFormat = symbolicFormat.replace(format,result);
                        counter++;
                    } else if (counter > 0) {
                        symbolicFormat = symbolicFormat.replace(argumentFormat.getFormat(i),argumentFormat.getFormat(i-counter));
                    }
                }
                return symbolicFormat;
            }

        };
    }

    public String getSymbolicFormat(TextFormat argumentFormat) {
        return baseFunction.getSymbolicFormat(argumentFormat);
    }

    /**
     * Get the collapsed function using the collapsible argument/dimension values from the specified provider. This method
     * is nearly the same as {@link #collapseFunction(NumericValuesProvider)}, except the specified provider's size will
     * be equal to that of the collapsible arguments/dimensions, not the entire base function.
     *
     * @param provider
     *        The provider providing the collapsed argument/dimension values.
     *
     * @return the collapsed function.
     */
    protected abstract NumericFunctionN collapseFunctionValuesCollapsed(NumericValuesProvider provider);


    /**
     * Get the (0-based) arguments/dimensions that will be collapsed by this function.
     *
     * @return the dimensions that this function collapses.
     */
    public List<Integer> getCollapsingDimensions() {
        return collapsingDimensionsList;
    }

    @Override
    public int getArgumentCount() {
        return baseFunction.getArgumentCount();
    }

    @Override
    protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedByte(start,values);
    }

    @Override
    protected short applyUncheckedShort(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedShort(start,values);
    }

    @Override
    protected int applyUncheckedInt(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedInt(start,values);
    }

    @Override
    protected long applyUncheckedLong(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedLong(start,values);
    }

    @Override
    protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedFloat(start,values);
    }

    @Override
    protected double applyUncheckedDouble(int start, NumericValuesProvider values) {
        return baseFunction.applyUncheckedDouble(start,values);
    }

    private class CollapsedNumericValuesProvider extends NumericValuesProvider {
        private final NumericValuesProvider provider;

        private CollapsedNumericValuesProvider(NumericValuesProvider provider) {
            this.provider = provider;
        }

        @Override
        public int getLength() {
            return collapsingDimensions.length;
        }

        @Override
        public byte getByte(int i) {
            return provider.getByte(collapsingDimensions[i]);
        }

        @Override
        public short getShort(int i) {
            return provider.getShort(collapsingDimensions[i]);
        }

        @Override
        public int getInt(int i) {
            return provider.getInt(collapsingDimensions[i]);
        }

        @Override
        public long getLong(int i) {
            return provider.getLong(collapsingDimensions[i]);
        }

        @Override
        public float getFloat(int i) {
            return provider.getFloat(collapsingDimensions[i]);
        }

        @Override
        public double getDouble(int i) {
            return provider.getDouble(collapsingDimensions[i]);
        }

        public JavaType getValueTypePreference() {
            return provider.getValueTypePreference();
        }
    }
}
