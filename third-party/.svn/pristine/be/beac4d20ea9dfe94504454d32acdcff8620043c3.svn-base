package com.pb.sawdust.util;

/**
 * The {@code MathUtil} provides utility methods for dealing with numbers and math.
 *
 * @author crf <br/>
 *         Started Aug 13, 2010 4:41:22 PM
 */
public class MathUtil {
    /**
     * The default delta used by the "almost equals" methods. Two non-zero values, <code>v1</code> and <code>v2</code>,
     * are almost equal if {@code ||v1 - v2|/v1| < delta}.
     */
    public static final double DEFAULT_ALMOST_EQUALS_DELTA = 0.00001;

    /**
     * The default epsilon used by the "almost equals" methods. A value, <code>v1</code> is almost equal to zero if
     * <code>|v1| < epsilon</code>.
     */
    public static final double DEFAULT_EQUALS_ZERO_EPSILON = 1.0e-10;

    /**
     * Determine if two float values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(float expected, float actual, float delta, float epsilon) {
        if (expected == actual)
            return true;
        else if (expected == 0.0)
            return Math.abs(actual) < epsilon;
        else if (actual == 0.0)
            return Math.abs(epsilon) < epsilon;
        else
            return Math.abs(Math.abs(expected - actual)/expected) < delta;
    }

     /**
     * Determine if two float values are almost equal within the default delta {@link #DEFAULT_ALMOST_EQUALS_DELTA} and
     * the defaul epsilon {@link #DEFAULT_EQUALS_ZERO_EPSILON}. Two non-zero values, <code>v1</code> and <code>v2</code>,
      * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(float expected, float actual) {
        return almostEquals(expected,actual,(float) DEFAULT_ALMOST_EQUALS_DELTA,(float) DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Determine if two double values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(double expected, double actual, double delta, double epsilon) {
        if (Double.compare(expected,actual) == 0)
            return true;
        else if (expected == 0.0)
            return Math.abs(actual) < epsilon;
        else if (actual == 0.0)
            return Math.abs(epsilon) < epsilon;
        else
            return Math.abs(Math.abs(expected - actual)/expected) < delta;
    }

     /**
     * Determine if two double values are almost equal within the default delta {@link #DEFAULT_ALMOST_EQUALS_DELTA} and
      * the defaul epsilon {@link #DEFAULT_EQUALS_ZERO_EPSILON}.  Two non-zero values, <code>v1</code> and <code>v2</code>,
      * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(double expected, double actual) {
        return almostEquals(expected,actual,DEFAULT_ALMOST_EQUALS_DELTA,DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Determine if two values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}. If the expected and actual values
     * are not both {@code Double} or both {@code Float} instances, then equality is determined using <code>expected.equals(actual)</code>.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(Object expected, Object actual, double delta, double epsilon) {
        if (expected != null) {
            if (actual != null) {
                if (expected.getClass() == Float.class && actual.getClass() == Float.class)
                    return almostEquals((float) (Float) expected,(float) (Float) actual,delta,epsilon);
                else if (expected.getClass() == Double.class && actual.getClass() == Double.class)
                    return almostEquals((double) (Double) expected,(double) (Double) actual,delta,epsilon);
            }
            return expected.equals(actual);
        }
        return actual == null;
    }

    /**
     * Determine if two values are almost equal within the default delta {@link #DEFAULT_ALMOST_EQUALS_DELTA} and
      * the defaul epsilon {@link #DEFAULT_EQUALS_ZERO_EPSILON}.  Two non-zero values, <code>v1</code> and <code>v2</code>,
      * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}. If the expected and
     * actual values are not both {@code Double} or both {@code Float} instances, then equality is determined using
     * <code>expected.equals(actual)</code>.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @return {@code true} if the values are almost equal, {@code false} if not.
     */
    public static boolean almostEquals(Object expected, Object actual) {
        return almostEquals(expected,actual,DEFAULT_ALMOST_EQUALS_DELTA,DEFAULT_EQUALS_ZERO_EPSILON);
    }
}
