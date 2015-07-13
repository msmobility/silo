package com.pb.sawdust.calculator;

import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;

/**
 * The {@code NumericValuesProvider} class provides a structure for getting primitive numeric argument values 
 * for use in numeric functions. Even if it naturally holds a different primitive, it will make the appropriate casting
 * (with possible loss of precision) to provide the desired value.
 *
 * @author crf <br/>
 *         Started 3/9/11 7:45 AM
 */
public abstract class NumericValuesProvider {
    /**
     * Get the length (number of argument values) of this provider.
     * 
     * @return the number of argument values this provider provides.
     */
    public abstract int getLength();

    /**
     * Get a {@code byte} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code byte} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */
    public abstract byte getByte(int i);     

    /**
     * Get a {@code short} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code short} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */    
    public abstract short getShort(int i);        

    /**
     * Get a {@code int} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code int} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */
    public abstract int getInt(int i);              

    /**
     * Get a {@code long} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code long} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */
    public abstract long getLong(int i);     

    /**
     * Get a {@code float} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code float} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */
    public abstract float getFloat(int i);       

    /**
     * Get a {@code double} value for the specified argument index.
     * 
     * @param i
     *        The argument index.
     *        
     * @return the {@code double} value for argument {@code i};
     * 
     * @throws IndexOutOfBoundsException if {@code i} is out of bounds for this provider.
     */
    public abstract double getDouble(int i);

    /**
     * Get the preferred value type for this provider. This method should not return {@link JavaType#OBJECT}, {@link JavaType#BOOLEAN},
     * or {@link JavaType#CHAR}, as these are non-numeric/non-primitive.
     * 
     * @return the natural (preferred) data type for this provider.
     */
    public abstract JavaType getValueTypePreference(); //used to automatically indicate what value type should be preferred

    /**
     * The {@code ByteValuesProvider} provides a structure for  a numeric values provider which naturally holds bytes.
     */
    public static abstract class  ByteValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code byte} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract byte getValue(int i);
        
        public byte getByte(int i) {
            return getValue(i);
        }                             
        
        public short getShort(int i) {
            return getValue(i);
        }
        
        public int getInt(int i) {
            return getValue(i);
        }
        
        public long getLong(int i) {
            return getValue(i);
        }
        
        public float getFloat(int i) {
            return getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.BYTE;
        }
    }
    
    /**
     * The {@code ShortValuesProvider} provides a structure for  a numeric values provider which naturally holds shorts.
     */
    public static abstract class  ShortValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code short} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract short getValue(int i);
        
        public byte getByte(int i) {
            return (byte) getValue(i);
        }                             
        
        public short getShort(int i) {
            return getValue(i);
        }
        
        public int getInt(int i) {
            return (int) getValue(i);
        }
        
        public long getLong(int i) {
            return (long) getValue(i);
        }
        
        public float getFloat(int i) {
            return (float) getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.SHORT;
        }
    }
    
    /**
     * The {@code IntValuesProvider} provides a structure for  a numeric values provider which naturally holds ints.
     */
    public static abstract class  IntValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code int} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract int getValue(int i);
        
        public byte getByte(int i) {
            return (byte) getValue(i);
        }                             
        
        public short getShort(int i) {
            return (short) getValue(i);
        }
        
        public int getInt(int i) {
            return getValue(i);
        }
        
        public long getLong(int i) {
            return (long) getValue(i);
        }
        
        public float getFloat(int i) {
            return (float) getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.INT;
        }
    }
    
    /**
     * The {@code LongValuesProvider} provides a structure for  a numeric values provider which naturally holds longs.
     */
    public static abstract class  LongValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code long} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract long getValue(int i);
        
        public byte getByte(int i) {
            return (byte) getValue(i);
        }                             
        
        public short getShort(int i) {
            return (short) getValue(i);
        }
        
        public int getInt(int i) {
            return (int) getValue(i);
        }
        
        public long getLong(int i) {
            return getValue(i);
        }
        
        public float getFloat(int i) {
            return (float) getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.LONG;
        }
    }
    
    /**
     * The {@code FloatValuesProvider} provides a structure for  a numeric values provider which naturally holds floats.
     */
    public static abstract class  FloatValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code float} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract float getValue(int i);
        
        public byte getByte(int i) {
            return (byte) getValue(i);
        }                             
        
        public short getShort(int i) {
            return (short) getValue(i);
        }
        
        public int getInt(int i) {
            return (int) getValue(i);
        }
        
        public long getLong(int i) {
            return (long) getValue(i);
        }
        
        public float getFloat(int i) {
            return getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.FLOAT;
        }
    }
    
    /**
     * The {@code DoubleValuesProvider} provides a structure for  a numeric values provider which naturally holds doubles.
     */
    public static abstract class  DoubleValuesProvider extends NumericValuesProvider {
        /**
         * Get the {@code double} value for the specified argument index.
         * 
         * @param i
         *        The (0-based) argument index.
         *        
         * @return the value at {@code i}.
         */
        public abstract double getValue(int i);
        
        public byte getByte(int i) {
            return (byte) getValue(i);
        }                             
        
        public short getShort(int i) {
            return (short) getValue(i);
        }
        
        public int getInt(int i) {
            return (int) getValue(i);
        }
        
        public long getLong(int i) {
            return (long) getValue(i);
        }
        
        public float getFloat(int i) {
            return (float) getValue(i);
        }
        
        public double getDouble(int i) {
            return getValue(i);
        }

        public JavaType getValueTypePreference() {
            return JavaType.DOUBLE;
        }
    }

    /**
     * The {@code ByteArrayValuesProvider} class provides a numeric values provider which holds its values in a {@code byte}
     * array.
     */
    public static class ByteArrayValuesProvider extends ByteValuesProvider {
        private final byte[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public ByteArrayValuesProvider(byte[] values) {
            this.values = ArrayUtil.copyArray(values);
        }
        
        public int getLength() {
            return values.length;
        }
        
        public byte getValue(int i) {
            return values[i];
        }
    }         
    
    /**
     * The {@code ShortArrayValuesProvider} class provides a numeric values provider which holds its values in a {@code short}
     * array.
     */
    public static class ShortArrayValuesProvider extends ShortValuesProvider {
        private final short[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public ShortArrayValuesProvider(short[] values) {
            this.values = ArrayUtil.copyArray(values);
        }
        
        public int getLength() {
            return values.length;
        }
        
        public short getValue(int i) {
            return values[i];
        }
    }
    
    /**
     * The {@code IntArrayValuesProvider} class provides a numeric values provider which holds its values in an {@code int}
     * array.
     */
    public static class IntArrayValuesProvider extends IntValuesProvider {
        private final int[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public IntArrayValuesProvider(int[] values) {
            this.values = ArrayUtil.copyArray(values);
        }  
        
        public int getLength() {
            return values.length;
        }
        
        public int getValue(int i) {
            return values[i];
        }
    }
    
    /**
     * The {@code LongArrayValuesProvider} class provides a numeric values provider which holds its values in a {@code long}
     * array.
     */
    public static class LongArrayValuesProvider extends LongValuesProvider {
        private final long[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public LongArrayValuesProvider(long[] values) {
            this.values = ArrayUtil.copyArray(values);
        }     
        
        public int getLength() {
            return values.length;
        }
        
        public long getValue(int i) {
            return values[i];
        }
    }
    
    /**
     * The {@code FloatArrayValuesProvider} class provides a numeric values provider which holds its values in a {@code float}
     * array.
     */
    public static class FloatArrayValuesProvider extends FloatValuesProvider {
        private final float[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public FloatArrayValuesProvider(float[] values) {
            this.values = ArrayUtil.copyArray(values);
        }     
        
        public int getLength() {
            return values.length;
        }
        
        public float getValue(int i) {
            return values[i];
        }
    }
    
    /**
     * The {@code DoubleArrayValuesProvider} class provides a numeric values provider which holds its values in a {@code double}
     * array.
     */
    public static class DoubleArrayValuesProvider extends DoubleValuesProvider {
        private final double[] values;

        /**
         * Constructor specifying the values held by this provider.
         * 
         * @param values
         *        The values to be held by this provider.
         */
        public DoubleArrayValuesProvider(double[] values) {
            this.values = ArrayUtil.copyArray(values);
        }    
        
        public int getLength() {
            return values.length;
        }
        
        public double getValue(int i) {
            return values[i];
        }
    }
}
