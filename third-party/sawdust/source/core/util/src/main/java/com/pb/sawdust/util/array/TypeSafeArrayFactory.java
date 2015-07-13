package com.pb.sawdust.util.array;

import com.pb.sawdust.util.JavaType;

import java.lang.reflect.Array;

/**
 * The {@code TypeSafeArrayFactory} holds static methods which provide {@code TypeSafeArray} implementations.
 *
 * @author crf <br/>
 *         Started: Jan 24, 2009 5:10:20 PM
 */
public class TypeSafeArrayFactory {

    private TypeSafeArrayFactory() {} //should not be instantiated

    /**
     * Get a type safe array holding the specified type.
     *
     * @param type
     *        The class of the type the array will hold.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @param <T>
     *        The type the returned array holds.
     *
     * @return a type safe array of size {@code dimensions} holding {@code T}.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static <T> TypeSafeArray<T> typeSafeArray(Class<T> type, int ... dimensions) {
        return new ObjectTypeSafeArray<T>(Array.newInstance(type,dimensions),type);
    }

    /**
     * Get a type safe array holding {@code boolean}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code boolean}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static BooleanTypeSafeArray booleanTypeSafeArray(int ... dimensions) {
        return new BooleanTypeSafeArray(Array.newInstance(boolean.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code byte}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code byte}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static ByteTypeSafeArray byteTypeSafeArray(int ... dimensions) {
        return new ByteTypeSafeArray(Array.newInstance(byte.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code short}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code short}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static ShortTypeSafeArray shortTypeSafeArray(int ... dimensions) {
        return new ShortTypeSafeArray(Array.newInstance(short.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code int}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code int}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static IntTypeSafeArray intTypeSafeArray(int ... dimensions) {
        return new IntTypeSafeArray(Array.newInstance(int.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code long}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code long}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static LongTypeSafeArray longTypeSafeArray(int ... dimensions) {
        return new LongTypeSafeArray(Array.newInstance(long.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code float}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code float}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static FloatTypeSafeArray floatTypeSafeArray(int ... dimensions) {
        return new FloatTypeSafeArray(Array.newInstance(float.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code double}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code double}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static DoubleTypeSafeArray doubleTypeSafeArray(int ... dimensions) {
        return new DoubleTypeSafeArray(Array.newInstance(double.class,dimensions));
    }

    /**
     * Get a type safe array holding {@code char}s.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding {@code char}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static CharTypeSafeArray charTypeSafeArray(int ... dimensions) {
        return new CharTypeSafeArray(Array.newInstance(char.class,dimensions));
    }

    /**
     * Get a type safe array holding the specified type.
     *
     * @param type
     *        The type the returned array will hold. If {@code type} is {@code JavaType.OBJECT}, then an {@code Object}
     *        array will be returned, otherwise the returned array will hold the specified primitive.
     *
     * @param dimensions
     *        The dimensionality of the returned array. Each value specifies the size of a dimension in the array, so
     *        {@code dimensions.length} indicates the number of dimensions in the array.
     *
     * @return a type safe array of size {@code dimensions} holding the type corresponding to {@code type}s.
     *
     * @throws NegativeArraySizeException if any of values in {@code dimensions} are less than zero.
     * @throws IllegalArgumentException if no dimensions are specified ({@code dimensions.length} == 0).
     */
    public static TypeSafeArray<?> typeSafeArray(JavaType type, int ... dimensions) {
        switch (type) {
            case BOOLEAN : return booleanTypeSafeArray(dimensions);
            case BYTE : return byteTypeSafeArray(dimensions);
            case CHAR : return charTypeSafeArray(dimensions);
            case DOUBLE : return doubleTypeSafeArray(dimensions);
            case FLOAT : return floatTypeSafeArray(dimensions);
            case INT : return intTypeSafeArray(dimensions);
            case LONG : return longTypeSafeArray(dimensions);
            case SHORT : return shortTypeSafeArray(dimensions);
            default : return typeSafeArray(Object.class,dimensions);
        }
    }
}
