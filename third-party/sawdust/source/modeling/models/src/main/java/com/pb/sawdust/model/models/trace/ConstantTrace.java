package com.pb.sawdust.model.models.trace;

/**
 * The {@code ConstantTrace} class is a {@code CalculationTrace} for constants. For example, a variable <code>x</code>
 * with a <i>defined</i> value of <code>7</code> could be correctly represented by an instance of this class.  Constant
 * traces can contain no calculation trace elements, and any attempts to add them will cause an exception to be thrown.
 *
 * @author crf <br/>
 *         Started Dec 8, 2010 9:56:47 AM
 */
public class ConstantTrace extends CalculationTrace {
    /**
     * Constructo specifying the symbolic trace, and the result.
     *
     * @param symbolicTrace
     *        The symbolic trace for the constant.
     *
     * @param result
     *        The result for the constant.
     */
    public ConstantTrace(String symbolicTrace, double result) {
        super(symbolicTrace,result);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException as trace elements cannot be added to a constant.
     */
    public void addTraceElement(String connector, CalculationTrace trace) {
        throw new UnsupportedOperationException("Constant trace cannot have trace elements.");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException as trace elements cannot be added to a constant.
     */
    public void addTraceElement(CalculationTrace trace) {
        throw new UnsupportedOperationException("Constant trace cannot have trace elements.");
    }
}
