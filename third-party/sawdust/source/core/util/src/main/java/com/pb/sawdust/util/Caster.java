package com.pb.sawdust.util;

/**
 * The {@code Caster} class provides convenience methods for casting between various types, primarily primitives.
 *
 * @author crf <br/>
 *         Started: Jun 12, 2009 1:51:24 PM
 */
public class Caster {

    private Caster(){}

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(byte value) {
        return value != 0;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(short value) {
        return value != 0;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(int value) {
        return value != 0;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(long value) {
        return value != 0L;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(float value) {
        return value != 0.0f;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the value is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(double value) {
        return value != 0.0d;
    }

    /**
     * Cast a value to a {@code boolean}. Unless the char point is zero, this method returns {@code true}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code true} if {@code value} is numerically true, {@code false} otherwise.
     */
    public static boolean castToBoolean(char value) {
        return value != (char) 0;
    }

    /**
     * Cast a value to a {@code byte}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static byte castToByte(boolean value) {
        return (byte) (value ? 1 : 0);
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(short value) {
        return (byte) value;
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(int value) {
        return (byte) value;
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(long value) {
        return (byte) value;
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(float value) {
        return (byte) value;
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(double value) {
        return (byte) value;
    }

    /**
     * Cast a value to a {@code byte}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code byte}.
     */
    public static byte castToByte(char value) {
        return (byte) value;
    }   

    /**
     * Cast a value to a {@code short}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static short castToShort(boolean value) {
        return (short) (value ? 1 : 0);
    }

    /**
     * Cast a value to a {@code short}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(byte value) {
        return (short) value;
    }

    /**
     * Cast a value to a {@code short}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(int value) {
        return (short) value;
    }

    /**
     * Cast a value to a {@code short}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(long value) {
        return (short) value;
    }

    /**
     * Cast a value to a {@code short}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(float value) {
        return (short) value;
    }

    /**
     * Cast a value to a {@code short}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(double value) {
        return (short) value;
    }

    /**
     * Cast a value to a {@code short}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code short}.
     */
    public static short castToShort(char value) {
        return (short) value;
    }    

    /**
     * Cast a value to an {@code int}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static int castToInt(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Cast a value to an {@code int}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(byte value) {
        return (int) value;
    }

    /**
     * Cast a value to an {@code int}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(short value) {
        return (int) value;
    }

    /**
     * Cast a value to an {@code int}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(long value) {
        return (int) value;
    }

    /**
     * Cast a value to an {@code int}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(float value) {
        return (int) value;
    }

    /**
     * Cast a value to an {@code int}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(double value) {
        return (int) value;
    }

    /**
     * Cast a value to an {@code int}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as an {@code int}.
     */
    public static int castToInt(char value) {
        return (int) value;
    }   

    /**
     * Cast a value to a {@code long}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static long castToLong(boolean value) {
        return value ? 1L : 0L;
    }  

    /**
     * Cast a value to a {@code long}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(byte value) {
        return (long) value;
    }

    /**
     * Cast a value to a {@code long}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(short value) {
        return (long) value;
    }

    /**
     * Cast a value to a {@code long}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(int value) {
        return (long) value;
    }

    /**
     * Cast a value to a {@code long}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(float value) {
        return (long) value;
    }

    /**
     * Cast a value to a {@code long}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(double value) {
        return (long) value;
    }

    /**
     * Cast a value to a {@code long}.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code long}.
     */
    public static long castToLong(char value) {
        return (long) value;
    }        

    /**
     * Cast a value to a {@code float}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static float castToFloat(boolean value) {
        return value ? 1.0f : 0.0f;
    }  

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(byte value) {
        return (float) value;
    }

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(short value) {
        return (float) value;
    }

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(int value) {
        return (float) value;
    }

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(long value) {
        return (float) value;
    }

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(double value) {
        return (float) value;
    }

    /**
     * Cast a value to a {@code float}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code float}.
     */
    public static float castToFloat(char value) {
        return (float) value;
    }      

    /**
     * Cast a value to a {@code double}.
     *
     * @param value
     *        The value to cast.
     *
     * @return zero if {@code value} is {@code false}, one otherwise.
     */
    public static double castToDouble(boolean value) {
        return value ? 1.0d : 0.0d;
    }  

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(byte value) {
        return (double) value;
    }

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(short value) {
        return (double) value;
    }

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(int value) {
        return (double) value;
    }

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(long value) {
        return (double) value;
    }

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(float value) {
        return (double) value;
    }

    /**
     * Cast a value to a {@code double}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code double}.
     */
    public static double castToDouble(char value) {
        return (double) value;
    }     

    /**
     * Cast a value to a {@code char}.
     *
     * @param value
     *        The value to cast.
     *
     * @return char point zero if {@code value} is {@code false}, char point one otherwise.
     */
    public static char castToChar(boolean value) {
        return (char) (value ? 1 : 0);
    }  

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(byte value) {
        return (char) value;
    }

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(short value) {
        return (char) value;
    }

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(int value) {
        return (char) value;
    }

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(long value) {
        return (char) value;
    }

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(float value) {
        return (char) value;
    }

    /**
     * Cast a value to a {@code char}. Some loss of precision may occur.
     *
     * @param value
     *        The value to cast.
     *
     * @return {@code value} as a {@code char}.
     */
    public static char castToChar(double value) {
        return (char) value;
    }
}
