package com.pb.sawdust.model.models.trace;

/**
 * The {@code LabelTrace} class is a {@code CalculationTrace} for labels.  Usually, this class will be used to represent
 * unresolvable symbols in a calculation, such as mathematical functions (<code>+</code>, <code>log</code>, <i>etc.</i>)
 * or parentheses. Label traces can contain no calculation trace elements, and any attempts to add them will cause an exception
 * to be thrown. 
 *
 * @author crf <br/>
 *         Started Dec 9, 2010 2:47:54 PM
 */
public class LabelTrace extends CalculationTrace {
    /**
     * Constructor specifying the label.
     *
     * @param label
     *        The calculation label.
     */
    public LabelTrace(String label) {
        super(label);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException as trace elements cannot be added to a label.
     */
    public void addTraceElement(String connector, CalculationTrace trace) {
        throw new UnsupportedOperationException("Label trace cannot have trace elements.");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException as trace elements cannot be added to a label.
     */
    public void addTraceElement(CalculationTrace trace) {
        throw new UnsupportedOperationException("Label trace cannot have trace elements.");
    }

//    public void setResult(double result) {
//        throw new UnsupportedOperationException("Label trace does not have a result.");
//    }

}
