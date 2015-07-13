package com.pb.sawdust.calculator;

import com.pb.sawdust.util.format.TextFormat;

/**
 * The {@code NumericFunction0} abstract class is used to specify zero-argument functions.
 */
public abstract class NumericFunction0 extends NumericFunctionN {
    public int getArgumentCount() {
        return 0;
    }

    public String getSymbolicFormat(TextFormat argumentFormat) {
        return "NumericFunction()";
    }

    /**
     * The {@code FlexFunction0} provides a skeletal implementation of {@code NumericFunction0}. The only methods that
     * need to be implemented are those which get the function value as a {@code long} and a {@code double}.
     * All {@code apply} calls using returning integers (in the generic sense) arguments will (by default) use the
     * {@code getLongValue()} method, and all calls returning decimal values will use the {@code getDoubleValue()} method.
     *
     */
    public static abstract class FlexFunction0 extends NumericFunction0 {
        private final String name;

        /**
         * Default constructor.
         */
        public FlexFunction0() {
            this(null);
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public FlexFunction0(String name) {
            this.name = name;
        }

        /**
         * Get the value of this function as a {@code long}.
         *
         * @return this function evaluated as a {@code long}.
         */
        protected abstract long getLongValue();

        /**
         * Get the value of this function as a {@code double}.
         *
         * @return this function evaluated as a {@code double}.
         */
        protected abstract double getDoubleValue();

        @Override
        public int getArgumentCount() {
            return 0;
        }

        @Override
        protected byte applyUncheckedByte(int start, byte ... values) {
            return (byte) getLongValue();
        }

        @Override
        protected short applyUncheckedShort(int start, short ... values) {
            return (short) getLongValue();
        }

        @Override
        protected int applyUncheckedInt(int start, int ... values) {
            return (int) getLongValue();
        }

        @Override
        protected long applyUncheckedLong(int start, long ... values) {
            return getLongValue();
        }

        @Override
        protected float applyUncheckedFloat(int start, float ... values) {
            return (float) getDoubleValue();
        }

        @Override
        protected double applyUncheckedDouble(int start, double ... values) {
            return getDoubleValue();
        } 

        @Override
        protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
            return (byte) getLongValue();
        }

        @Override
        protected short applyUncheckedShort(int start, NumericValuesProvider values) {
            return (short) getLongValue();
        }

        @Override
        protected int applyUncheckedInt(int start, NumericValuesProvider values) {
            return (int) getLongValue();
        }

        @Override
        protected long applyUncheckedLong(int start, NumericValuesProvider values) {
            return getLongValue();
        }

        @Override
        protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
            return (float) getDoubleValue();
        }

        @Override
        protected double applyUncheckedDouble(int start, NumericValuesProvider values) {
            return getDoubleValue();
        }

        public String toString() {
            return name == null ? super.toString() : name;
        }

        public String getSymbolicFormat(TextFormat argumentFormat) {
            return new StringBuilder(name == null ? super.toString() : name).append("()").toString();
        }

        /**
         * Get the name for this function.
         *
         * @return this function's name.
         */
        public String getName() {
            return name;
        }
    }
}
