package com.pb.sawdust.calculator;

import com.pb.sawdust.util.format.TextFormat;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code NumericFunctionN} abstract class is used to specify functions which take one or more numbers as an
 * argument and return a number.
 */
public abstract class NumericFunctionN {
    /**
     * Get the number of arguments this function takes.
     *
     * @return this functions argument count.
     */
    abstract public int getArgumentCount();

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected byte applyUncheckedByte(int start,  byte ... values) {
        return applyUncheckedByte(start,new NumericValuesProvider.ByteArrayValuesProvider(values));
    }

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments..
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected short applyUncheckedShort(int start,  short ... values) {
        return applyUncheckedShort(start,new NumericValuesProvider.ShortArrayValuesProvider(values));
    }

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected int applyUncheckedInt(int start,  int ... values) {
        return applyUncheckedInt(start,new NumericValuesProvider.IntArrayValuesProvider(values));
    }

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected long applyUncheckedLong(int start,  long ... values) {
        return applyUncheckedLong(start,new NumericValuesProvider.LongArrayValuesProvider(values));
    }

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected float applyUncheckedFloat(int start,  float ... values) {
        return applyUncheckedFloat(start,new NumericValuesProvider.FloatArrayValuesProvider(values));
    }

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values[start]} through {@code values[start+getArgumentCount()-1]}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    protected double applyUncheckedDouble(int start,  double ... values) {
        return applyUncheckedDouble(start,new NumericValuesProvider.DoubleArrayValuesProvider(values));
    }
    
    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the 
     * function should be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected byte applyUncheckedByte(int start, NumericValuesProvider values);

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments..
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected short applyUncheckedShort(int start,  NumericValuesProvider values);

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected int applyUncheckedInt(int start, NumericValuesProvider values);

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected long applyUncheckedLong(int start, NumericValuesProvider values);

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected float applyUncheckedFloat(int start, NumericValuesProvider values);

    /**
     * Apply the function without checking the bounds of the inputs. That is, this function should assume that values
     * exist for {@code values.getValue(start)} through {@code values.getValue(start+getArgumentCount()-1)}, and that the function should
     * be applied on these values.
     *
     * @param start
     *        The starting point in the values array for the functions arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of the function when applied to the values in {@code values}, starting at {@code start}.
     */
    abstract protected double applyUncheckedDouble(int start, NumericValuesProvider values);

    /**
     * Apply this function to a series of byte arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public byte applyByte(byte ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedByte(0,values);
    }

    /**
     * Apply this function to a series of short arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public short applyShort(short ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedShort(0,values);
    }

    /**
     * Apply this function to a series of int arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public int applyInt(int ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedInt(0,values);
    }

    /**
     * Apply this function to a series of long arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public long applyLong(long ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedLong(0,values);
    }

    /**
     * Apply this function to a series of float arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public float applyFloat(float ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedFloat(0,values);
    }

    /**
     * Apply this function to a series of double arguments.
     *
     * @param values
     *        The arguments for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public double applyDouble(double ... values) {
        if (values.length != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.length);
        return applyUncheckedDouble(0, values);
    }
    
    /**
     * Apply this function to a series of byte arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public byte applyByte(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedByte(0,values);
    }

    /**
     * Apply this function to a series of short arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public short applyShort(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedShort(0,values);
    }

    /**
     * Apply this function to a series of int arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public int applyInt(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedInt(0,values);
    }

    /**
     * Apply this function to a series of long arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public long applyLong(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedLong(0,values);
    }

    /**
     * Apply this function to a series of float arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public float applyFloat(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedFloat(0,values);
    }

    /**
     * Apply this function to a series of double arguments.
     *
     * @param values
     *        The argument provider for the function.
     *
     * @return the value of this function when applied to {@code values}.
     *
     * @throws IllegalArgumentException if the number of values in {@code values} does not equal this function's
     *                                  argument count.
     */
    public double applyDouble(NumericValuesProvider values) {
        if (values.getLength() != getArgumentCount())
            throw new IllegalArgumentException("Incorrect argument count; expected " + getArgumentCount() + ", found " + values.getLength());
        return applyUncheckedDouble(0,values);
    }
    
    public String toString() {
        return "NumericFunction";
    }

    public String getSymbolicFormat(TextFormat argumentFormat) {
        StringBuilder format = new StringBuilder(toString()).append("(");
        for (int i : range(getArgumentCount())) {
            if (i > 0)
                format.append(",");
            format.append(argumentFormat.getFormat(i+1));
        }
        return format.append(")").toString();
    }

    public String getSymbolicFormat() {
        return getSymbolicFormat(new TextFormat(TextFormat.Conversion.STRING));
    }  

    /**
     * The {@code FlexFunctionN} provides a skeletal implementation of {@code NumericFunctionN}. The only remaining
     * methods that need to be implemented are the {@code apply} methods acting on {@code long}s and {@code double}s.
     * All {@code apply} calls using integer (in the generic sense) arguments will (by default) use the method acting on
     * {@code long}s, and all calls using decimal arguments will use the method acting on {@code double}s.
     */
    public static abstract class FlexFunctionN extends NumericFunctionN {
        private final String name;
        private final int argumentCount;

        /**
         * Default constructor specifying the argument count for the function.
         * 
         * @param argumentCount
         *        The number of arguments for the function.
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.    
         */
        public FlexFunctionN(int argumentCount) {
            this(argumentCount,null);
        }

        /**
         * Constructor specifying the name and argument count for the function. This name will be used to give a useful 
         * output for the {@code toString()} method.
         * 
         * @param argumentCount
         *        The number of arguments for the funcion.
         *
         * @param name
         *        The name for the function.                                        
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.
         */
        public FlexFunctionN(int argumentCount, String name) {
            if (argumentCount < 0)
                throw new IllegalArgumentException("Argument count cannot be negative: " + argumentCount);
            this.argumentCount = argumentCount;
            this.name = name;
        }

        @Override
        public int getArgumentCount() {
            return argumentCount;
        }

        @Override
        protected byte applyUncheckedByte(int start,  byte ... values) {
            return (byte) applyUncheckedLong(start,new NumericValuesProvider.ByteArrayValuesProvider(values));
        }

        @Override
        protected short applyUncheckedShort(int start,  short ... values) {
            return (short) applyUncheckedLong(start,new NumericValuesProvider.ShortArrayValuesProvider(values));
        }

        @Override
        protected int applyUncheckedInt(int start,  int ... values) {
            return (int) applyUncheckedLong(start,new NumericValuesProvider.IntArrayValuesProvider(values));
        }

        @Override
        protected float applyUncheckedFloat(int start,  float ... values) {
            return (float) applyUncheckedDouble(start,new NumericValuesProvider.FloatArrayValuesProvider(values));
        }    

        @Override
        protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
            return (byte) applyUncheckedLong(0,values);
        }

        @Override
        protected short applyUncheckedShort(int start, NumericValuesProvider values) {
            return (short) applyUncheckedLong(0,values);
        }

        @Override
        protected int applyUncheckedInt(int start, NumericValuesProvider values) {
            return (int) applyUncheckedLong(0,values);
        }

        @Override
        protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
            return (float) applyUncheckedDouble(0,values);
        }

        public String getName() {
            return name;
        }
    
        public String toString() {
            return name;
        }
    }                   

    /**
     * The {@code DecimalFunctionN} class provides a {@code FlexFunctionN} which acts on decimals. That is, by default,
     * all {@code apply} calls will use {@code apply(double[])}.
     */
    public static abstract class DecimalFunctionN extends FlexFunctionN {
           
        /**
         * Constructor specifying the name and argument count for the function. 
         * 
         * @param argumentCount
         *        The number of arguments for the function.                                  
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.
         */
        public DecimalFunctionN(int argumentCount) {
            super(argumentCount);
        }

        /**
         * Constructor specifying the name and argument count for the function. This name will be used to give a useful 
         * output for the {@code toString()} method.
         * 
         * @param argumentCount
         *        The number of arguments for the function.
         *
         * @param name
         *        The name for the function.                                        
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.
         */
        public DecimalFunctionN(int argumentCount, String name) {
            super(argumentCount, name);
        }   

        @Override
        protected byte applyUncheckedByte(int start,  byte ... values) {
            return (byte) applyUncheckedDouble(start,new NumericValuesProvider.ByteArrayValuesProvider(values));
        } 

        @Override
        protected short applyUncheckedShort(int start,  short ... values) {
            return (short) applyUncheckedDouble(start,new NumericValuesProvider.ShortArrayValuesProvider(values));
        } 

        @Override
        protected int applyUncheckedInt(int start,  int ... values) {
            return (int) applyUncheckedDouble(start,new NumericValuesProvider.IntArrayValuesProvider(values));
        } 

        @Override
        protected long applyUncheckedLong(int start,  long ... values) {
            return (long) applyUncheckedDouble(start,new NumericValuesProvider.LongArrayValuesProvider(values));
        }   

        @Override
        protected byte applyUncheckedByte(int start, NumericValuesProvider values) {
            return (byte) applyUncheckedDouble(0,values);
        }

        @Override
        protected short applyUncheckedShort(int start, NumericValuesProvider values) {
            return (short) applyUncheckedDouble(0,values);
        }

        @Override
        protected int applyUncheckedInt(int start, NumericValuesProvider values) {
            return (int) applyUncheckedDouble(0,values);
        } 

        @Override
        protected long applyUncheckedLong(int start, NumericValuesProvider values) {
            return (long) applyUncheckedDouble(0,values);
        }
    }                    

    /**
     * The {@code IntegralFunctionN} class provides a {@code FlexFunctionN} which acts on integers. That is, by default,
     * all {@code apply} calls will use {@code apply(long[])}.
     */
    public static abstract class IntegralFunctionN extends FlexFunctionN {
           
        /**
         * Constructor specifying the name and argument count for the function. 
         * 
         * @param argumentCount
         *        The number of arguments for the function.                                  
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.
         */
        public IntegralFunctionN(int argumentCount) {
            super(argumentCount);
        }

        /**
         * Constructor specifying the name and argument count for the function. This name will be used to give a useful 
         * output for the {@code toString()} method.
         * 
         * @param argumentCount
         *        The number of arguments for the function.
         *
         * @param name
         *        The name for the function.                                        
         *        
         * @throws IllegalArgumentException if {@code argumentCount} is less than 0.
         */
        public IntegralFunctionN(int argumentCount, String name) {
            super(argumentCount, name);
        }   

        @Override
        protected float applyUncheckedFloat(int start,  float ... values) {
            return (float) applyUncheckedLong(start,new NumericValuesProvider.FloatArrayValuesProvider(values));
        }

        @Override
        protected double applyUncheckedDouble(int start,  double ... values) {
            return (double) applyUncheckedLong(start,new NumericValuesProvider.DoubleArrayValuesProvider(values));
        }

        @Override
        protected float applyUncheckedFloat(int start, NumericValuesProvider values) {
            return (float) applyUncheckedLong(0,values);
        }

        @Override
        protected double applyUncheckedDouble(int start, NumericValuesProvider values) {
            return (double) applyUncheckedLong(0,values);
        }
    }
}
