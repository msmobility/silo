package com.pb.sawdust.calculator;

import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.format.TextFormat;

import static java.lang.Math.round;

/**
 * The {@code NumericFunction2} class is used to specify functions which take two numbers as arguments and
 * return a number. It implements the standard {@code Function2} interface by specifying methods which act on primitives.
 * That is, implementations must be able to deal with any numeric argument (object or primitive).  For simplicity,
 * it does not specify {@code apply} functions mixing different primitive types; it is assumed that applications
 * will cast to the appropriate types as needed.
 */
public abstract class NumericFunction2 extends NumericFunctionN implements NumericResultFunction2<Number,Number> {
    /**
     * Get a two-argument numeric function which swaps the argument order for a specified two-argument function. That is,
     * if the input function is {@code f(x,y)}, then the mirrored function would be defined as <code>m(x,y) = f(y,x)</code>.
     *
     * @param function
     *        The input function.
     *
     * @return a function which swaps the argument order for {@code function}.
     */
    public static NumericFunction2 mirror(NumericFunction2 function) {
        return new NumericFunction2Mirror(function);
    }

    /**
     * Apply the function on {@code byte} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public byte apply(byte x, byte y);

    /**
     * Apply the function on {@code short} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public short apply(short x, short y);

    /**
     * Apply the function on {@code int} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public int apply(int x, int y);

    /**
     * Apply the function on {@code long} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public long apply(long x, long y);

    /**
     * Apply the function on {@code float} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public float apply(float x, float y);

    /**
     * Apply the function on {@code double} arguments.
     *
     * @param x
     *        The first argument.
     *
     * @param y
     *        The second argument.
     *
     * @return the result of the function applied on {@code x} and {@code y}.
     */
    abstract public double apply(double x, double y);

    public int getArgumentCount() {
        return 2;
    }

    public byte applyUncheckedByte(int start,  byte ... values) {
        return apply(values[start],values[start+1]);
    }

    public short applyUncheckedShort(int start,  short ... values) {
        return apply(values[start],values[start+1]);
    }

    public int applyUncheckedInt(int start,  int ... values) {
        return apply(values[start],values[start+1]);
    }

    public long applyUncheckedLong(int start,  long ... values) {
        return apply(values[start],values[start+1]);
    }

    public float applyUncheckedFloat(int start,  float ... values) {
        return apply(values[start],values[start+1]);
    }

    public double applyUncheckedDouble(int start,  double ... values) {
        return apply(values[start],values[start+1]);
    }

    public byte applyUncheckedByte(int start,  NumericValuesProvider values) {
        return apply(values.getByte(start),values.getByte(start+1));
    }

    public short applyUncheckedShort(int start,  NumericValuesProvider values) {
        return apply(values.getShort(start),values.getShort(start+1));
    }

    public int applyUncheckedInt(int start,  NumericValuesProvider values) {
        return apply(values.getInt(start),values.getInt(start+1));
    }

    public long applyUncheckedLong(int start,  NumericValuesProvider values) {
        return apply(values.getLong(start),values.getLong(start+1));
    }

    public float applyUncheckedFloat(int start,  NumericValuesProvider values) {
        return apply(values.getFloat(start),values.getFloat(start+1));
    }

    public double applyUncheckedDouble(int start,  NumericValuesProvider values) {
        return apply(values.getDouble(start),values.getDouble(start+1));
    }              
    
    public byte applyByte(Number x, Number y) {
        return apply(x.byteValue(),y.byteValue());
    }   
    
    public short applyShort(Number x, Number y) {
        return apply(x.shortValue(),y.shortValue());
    }
    
    public int applyInt(Number x, Number y) {
        return apply(x.intValue(),y.intValue());
    }
    
    public long applyLong(Number x, Number y) {
        return apply(x.longValue(),y.longValue());
    }
    
    public float applyFloat(Number x, Number y) {
        return apply(x.floatValue(),y.floatValue());
    }
    
    public double applyDouble(Number x, Number y) {
        return apply(x.doubleValue(),y.doubleValue());
    }

    private static class NumericFunction2Mirror extends NumericFunction2 {
        private final NumericFunction2 function;

        private NumericFunction2Mirror(NumericFunction2 function) {
            this.function = function;
        }

        public byte apply(byte x, byte y) {
            return function.apply(y,x);
        }

        public short apply(short x, short y) {
            return function.apply(y,x);
        }

        public int apply(int x, int y) {
            return function.apply(y,x);
        }

        public long apply(long x, long y) {
            return function.apply(y,x);
        }

        public float apply(float x, float y) {
            return function.apply(y,x);
        }

        public double apply(double x, double y) {
            return function.apply(y,x);
        }

        public Number apply(Number y, Number x) {
            return function.apply(y,x);
        }

        public String toString() {
            return "Mirror of " + function.toString();
        }

        public String getSymbolicFormat(TextFormat argumentFormat) {
            String regularFormat = super.getSymbolicFormat(argumentFormat);
            String arg1Format = argumentFormat.getFormat(1);
            String arg2Format = argumentFormat.getFormat(2);
            String arg3Format = argumentFormat.getFormat(3); //placeholder
            return regularFormat.replace(arg1Format,arg3Format).replace(arg2Format,arg1Format).replace(arg3Format,arg2Format);
        }
    }

    /**
     * The {@code FlexFunction1} provides a skeletal implementation of {@code NumericFunction2}. The only remaining
     * methods that need to be implemented are the {@code apply} methods acting on {@code long}s and {@code double}s.
     * All {@code apply} calls using integer (in the generic sense) arguments will (by default) use the method acting on
     * {@code long}s, and all calls using decimal arguments will use the method acting on {@code double}s.
     */
    public static abstract class FlexFunction2 extends NumericFunction2 {
        private final String name;

        /**
         * Default constructor.
         */
        public FlexFunction2() {
            this(null);
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public FlexFunction2(String name) {
            this.name = name;
        }

        public byte apply(byte x, byte y) {
            return (byte) apply((long) x, (long) y);
        }

        public short apply(short x, short y) {
            return (short) apply((long) x, (long) y);
        }

        public int apply(int x, int y) {
            return (int) apply((long) x, (long) y);
        }

        public float apply(float x, float y) {
            return (float) apply((double) x, (double) y);
        }

        @SuppressWarnings("unchecked") //as long as typing is observed, this should be ok
        public Number apply(Number x, Number y) {
            switch (JavaType.getPrimitiveJavaType(x.getClass())) {
                case BYTE : return apply(x.byteValue(),y.byteValue());
                case SHORT : return apply(x.shortValue(),y.shortValue());
                case INT : return apply(x.intValue(),y.intValue());
                case LONG : return apply(x.longValue(),y.longValue());
                case FLOAT : return apply(x.floatValue(),y.floatValue());
                case DOUBLE : return apply(x.doubleValue(),y.doubleValue());
                default : throw new ClassCastException("Numeric function type must extend Number.");
            }
        }

        public String toString() {
            return name == null ? super.toString() : name;
        }

        //todo: I think this is the same now as super call
        public String getSymbolicFormat(TextFormat argumentFormat) {
            return new StringBuilder(name == null ? super.toString() : name).append("(").append(argumentFormat.getFormat(1)).append(",").append(argumentFormat.getFormat(2)).append(")").toString();
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
     * The {@code DecimalFunction2} class provides a {@code FlexFunction2} which acts on decimals. That is, by default,
     * all {@code apply} calls will use {@code apply(double,double)}.
     */
    public static abstract class DecimalFunction2 extends FlexFunction2 {

        /**
         * Default constructor.
         */
        public DecimalFunction2() {
            super();
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public DecimalFunction2(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         *
         * This method will cast its arguments to {@code double}s and then round the result. That is, it returns
         * <code>Mathround(apply((double) x,(double) y))</code>.
         */
        public long apply(long x, long y) {
            return Math.round(apply((double) x, (double) y));
        }
    }

    /**
     * The {@code IntegralFunction2} class provides a {@code FlexFunction2} which acts on integers. That is, by default,
     * all {@code apply} calls will use {@code apply(long,long)}.
     */
    public static abstract class IntegralFunction2 extends FlexFunction2 {

        /**
         * Default constructor.
         */
        public IntegralFunction2() {
            super();
        }

        /**
         * Constructor specifying the name for the function. This name will be used to give a useful output for the
         * {@code toString()} method.
         *
         * @param name
         *        The name for the function.
         */
        public IntegralFunction2(String name) {
            super(name);
        }

        /**
         * {@inheritDoc}
         *
         * This method will round its arguments to {@code long}s and then cast the result. That is, it returns
         * <code>apply(Math.round(x), Math.round(y))</code>.
         */
        public double apply(double x, double y) {
            return apply(round(x),round(y));
        }
    }
}
