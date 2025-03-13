package de.tum.bgu.msm.health.disease;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class LinearInterpolation {

    public static double findClosest(double[] arr, double target) {
        double closest = arr[0];
        for (double num : arr) {
            if (Math.abs(num - target) < Math.abs(closest - target)) {
                closest = num;
            }
        }
        return closest;
    }

    public static double interpolate(double[] x, double[] y, double xout) {
        // If xout is outside the range of x, return the closest y value
        if (xout <= x[0]) {
            return y[0];  // Return the first y value
        }
        if (xout >= x[x.length - 1]) {
            return y[y.length - 1];  // Return the last y value
        }

        // Find the interval where xout belongs
        for (int i = 0; i < x.length - 1; i++) {
            if (xout >= x[i] && xout <= x[i + 1]) {
                // Instead of linear interpolation, return the closest y value
                double closestX = findClosest(new double[]{x[i], x[i + 1]}, xout);
                return closestX == x[i] ? y[i] : y[i + 1];  // Return the corresponding y value
            }
        }

        throw new IllegalArgumentException("Unexpected error: xout is not handled.");
    }

    /*public static void main(String[] args) {
        // Known data points (x, y)
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 6, 8, 10};

        // Interpolate at points within the range
        double new_x = 2.5;
        double interpolatedValue = interpolate(x, y, new_x);

        System.out.println("Closest value to x = " + new_x + " is: " + interpolatedValue);
    }*/

}
