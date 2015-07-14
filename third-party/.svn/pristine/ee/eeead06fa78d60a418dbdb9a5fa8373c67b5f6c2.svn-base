package com.pb.sawdust.calculator;

/**
 * The {@code CollapsibleFunction} interface specifies a function whose number of arguments may be reduced because some of
 * their values are known. The mechanism and algorithms for collapsing functions is left up to the implementation; this
 * interface essentially just specifies that it is possible.
 *
 * @author crf <br/>
 *         Started 7/15/11 10:54 AM
 */
public interface CollapsibleFunction {
    /**
     * Collapse a function by reducing its argument count. The numeric values provider passed to this method should have
     * the correct dimensions for applying the full function, and the implementation will determine how to extract the
     * correct values for the dimensions/arguments which are to be collapsed.
     *
     * @param provider
     *        The numeric values provider that may be used to find the values for the collapsible arguments.
     *
     * @return a collapsed version of the function, removing the need for passing in the collapsed (known) arguments.
     */
    NumericFunctionN collapseFunction(NumericValuesProvider provider);
}
