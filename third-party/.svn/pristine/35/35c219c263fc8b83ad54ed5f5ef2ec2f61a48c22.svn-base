package com.pb.sawdust.calculator;

import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.format.TextFormat;

import static java.lang.Math.round;

/**
 * The {@code NumericFunction1} abstract class is used to specify functions which take one number as an argument and
 * return a number. It implements the standard {@code Function1} interface by specifying methods which act on primitives.
 * That is, implementations must be able to deal with any numeric argument (object or primitive).
 */
public abstract class NumericFunction1 extends NumericFunctionN implements NumericResultFunction1<Number> {
    /**
     * Apply the function to a {@code byte} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public byte apply(byte x);

    /**
     * Apply the function to a {@code short} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public short apply(short x);

    /**
     * Apply the function to an {@code int} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public int apply(int x);

    /**
     * Apply the function to a {@code long} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public long apply(long x);

    /**
     * Apply the function to a {@code float} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public float apply(float x);

    /**
     * Apply the function to a {@code double} input.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    abstract public double apply(double x);

    public int getArgumentCount() {
        return 1;
    }

    public byte applyUncheckedByte(int start,  byte ... values) {
        return apply(values[start]);
    }

    public short applyUncheckedShort(int start,  short ... values) {
        return apply(values[start]);
    }

    public int applyUncheckedInt(int start,  int ... values) {
        return apply(values[start]);
    }

    public long applyUncheckedLong(int start,  long ... values) {
        return apply(values[start]);
    }

    public float applyUncheckedFloat(int start,  float ... values) {
        return apply(values[start]);
    }

    public double applyUncheckedDouble(int start,  double ... values) {
        return apply(values[start]);
    }           

    public byte applyUncheckedByte(int start,  NumericValuesProvider values) {
        return apply(values.getByte(start));
    }

    public short applyUncheckedShort(int start,  NumericValuesProvider values) {
        return apply(values.getShort(start));
    }

    public int applyUncheckedInt(int start,  NumericValuesProvider values) {
        return apply(values.getInt(start));
    }

    public long applyUncheckedLong(int start,  NumericValuesProvider values) {
        return apply(values.getLong(start));
    }

    public float applyUncheckedFloat(int start,  NumericValuesProvider values) {
        return apply(values.getFloat(start));
    }

    public double applyUncheckedDouble(int start,  NumericValuesProvider values) {
        return apply(values.getDouble(start));
    }
    
    public byte applyByte(Number n) {
        return apply(n.byteValue());
    }

    public short applyShort(Number n) {
        return apply(n.shortValue());
    }

    public int applyInt(Number n) {
        return apply(n.intValue());
    }

    public long applyLong(Number n) {
        return apply(n.longValue());
    }

    public float applyFloat(Number n) {
        return apply(n.floatValue());
    }

    public double applyDouble(Number n) {
        return apply(n.doubleValue());
    }

    /**
     * The {@code FlexFunction1} provides a skeletal implementation of {@code NumericFunction1}. The only remaining
     * methods that need to be implemented are the {@code apply} methods acting on {@code long}s and {@code double}s.
     * All {@code apply} calls using integer (in the generic sense) arguments will (by default) use the method acting on
     * {@code long}s, and all calls using decimal arguments will use the method acting on {@code double}s.
     */
    public static abstract class FlexFunction1 extends NumericFunction1 {
        private final String name;

        /**
         * Default constructor.
         */
        public FlexFunction1() {
            this(null);
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public FlexFunction1(String name) {
            this.name = name;
        }

        public byte apply(byte x) {
            return (byte) apply((long) x);
        }

        public short apply(short x) {
            return (short) apply((long) x);
        }

        public int apply(int x) {
            return (int) apply((long) x);
        }

        public float apply(float x) {
            return (float) apply((double) x);
        }

        @SuppressWarnings("unchecked") //as long as typing is observed, this should be ok
        public Number apply(Number x) {
            switch (JavaType.getPrimitiveJavaType(x.getClass())) {
                case BYTE : return apply(x.byteValue());
                case SHORT : return apply(x.shortValue());
                case INT : return apply(x.intValue());
                case LONG : return apply(x.longValue());
                case FLOAT : return apply(x.floatValue());
                case DOUBLE : return apply(x.doubleValue());
                default : throw new ClassCastException("Numeric function type must extend Number.");
            }
        }

        public String toString() {
            return name == null ? super.toString() : name;
        }

        //todo: I think this is same as super call
        public String getSymbolicFormat(TextFormat argumentFormat) {
            return new StringBuilder(name == null ? super.toString() : name).append("(").append(argumentFormat.getFormat(1)).append(")").toString();
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

    /**
     * The {@code DecimalFunction1} class provides a {@code FlexFunction1} which acts on decimals. That is, by default,
     * all {@code apply} calls will use {@code apply(double)}.
     */
    public static abstract class DecimalFunction1 extends FlexFunction1 {

        /**
         * Default constructor.
         */
        public DecimalFunction1() {
            super();
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public DecimalFunction1(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         *
         * This method will cast its argument to a {@code double} and then round the result. That is, it returns
         * <code>Math.round(apply((double) x))</code>.
         */
        public long apply(long x) {
            return round(apply((double) x));
        }
    }

    /**
     * The {@code IntegralFunction1} class provides a {@code FlexFunction1} which acts on integers. That is, by default,
     * all {@code apply} calls will use {@code apply(long)}.
     */
    public static abstract class IntegralFunction1 extends FlexFunction1 {

        /**
         * Default constructor.
         */
        public IntegralFunction1() {
            super();
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public IntegralFunction1(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         *
         * This method will round its argument to a {@code long} and then cast the result. That is, it returns
         * <code>apply(Math.round((long) x))</code>.
         */
        public double apply(double x) {
            return apply(round((long) x));
        }
    }

}
