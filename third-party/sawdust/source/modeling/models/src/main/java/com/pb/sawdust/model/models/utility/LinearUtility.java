package com.pb.sawdust.model.models.utility;

import com.pb.sawdust.util.collections.SetList;

/**
 * The {@code LinearUtility} interface is used to represent linear utility functions.  That is, for a give set of
 * variables, <code>v1,v2,...,vN</code>, and corresponding coefficients, <code>c1,c2,...,cN</code>, the utility function
 * is given by:
 * <br>
 * <pre><code>
 *     U = v1*c1 + v2*c2 + ... + vN*cN
 * </code></pre>
 * <p>
 * Because of this linearity, there is no need to have duplicated variables in the utility variable list.  For example,
 * if a linear utility has two coefficients, <code>c1</code> and <code>c2</code>, on variable <code>v1</code>, then in
 * the utility those coefficients can be combined:
 * <br>
 * <pre><code>
 *     U = v1*c1 + v1*c2 + ...
 *       = v1*(c1+c2) + ...
 * </code></pre>
 * <br>
 * This characteristic of linear models allows the requirement that the utility varaibles are only represented once
 * in the result of the {@code getVariables()} method.
 *
 * @author crf <br/>
 *         Started Sep 15, 2010 12:53:00 PM
 */
public interface LinearUtility extends Utility {

    /**
     * {@inheritDoc}
     *
     * As described in the main documentation of this interface, there is no need to have duplicated variables in the
     * list returned by this method (which is what the {@code SetList} enforces). Duplicated variables can be reduced
     * by summing their coefficient values into a single coefficient.
     */
    SetList<String> getVariables();

    /**
     * Get the coefficient for a particular variable.
     *
     * @param variable
     *        The variable name.
     *
     * @return the coefficient corresponding to {@code variable}.
     *
     * @throws IllegalArgumentException if {@code variable} is not a variable in this utility.
     */
    double getCoefficient(String variable);
}
