package com.pb.sawdust.model.models.trace;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.format.TextFormat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code CalculationTrace} class provides a structure for storing information about a calculation and its result.
 * The intention is to allow the component parts of a complex calculation to be stored in an object so that it can be
 * analyzed later, separate from the model object/data.
 * <p>
 * A calculation trace is comprised of four main parts:
 * <p>
 * <ul>
 *     <li>A {@code String} <b>symbolic trace</b>, which shows the calculation represented in symbols.</li>
 *     <li>A {@code String} <b>resolved trace</b>, which shows the calculation with its symbols resolved to values.</li>
 *     <li>A {@code double} <b>result</b>, which is the result of the calculation.</li>
 *     <li>A list of {@code CalculationTrace} <b>trace elements</b>, which are the calculation traces for the component
 *         parts of the calculation.</li>
 * </ul>
 * <p>
 * For example, say you had the following formulas:
 * <p>
 * <pre><code>
 *     x = y + z
 *     y = a * b
 *     a = 2
 *     b = 3
 *     z = 1
 * </code></pre>
 * <p>
 * The calculation trace for <code>x</code> would look like the following:
 * <ul>
 *     <li>Symbolic trace: <code>y + z</code></li>
 *     <li>Resolved trace: <code>6 + 1</code></li>
 *     <li>Result: 7</li>
 *     <li>Trace elements:
 *         <ol>
 *             <li>a trace for the calculation of <code>y</code> (which would capture <code>a * b</code></li>
 *             <li>a trace for the plus symbol (<code>+</code>)</li>
 *             <li>a trace of the constant <code>z</code></li>
 *         </ol>
 *     </li>
 * </ul>
 * <p>
 * When adding trace elements, it is important to keep in mind whether a particular element will have component trace
 * elements itself or not.  If it will not, then generally instances of the {@code ConstantTrace} or {@code LabelTrace}
 * class should be used (for constant values/symbols or non-numeric symbols, respectively).  Maintaining this convention
 * can help calculation trace analyzers to present the trace information in a cleaner and more informative manner.
 *
 * @see com.pb.sawdust.model.models.trace.ConstantTrace
 * @see com.pb.sawdust.model.models.trace.LabelTrace
 *
 * @author crf <br/>
 *         Started Dec 8, 2010 8:39:33 AM
 */
public class CalculationTrace {
    private final String symbolicTrace;
    private final String resolvedTrace;
    private final List<CalculationTrace> traceElements;
    private final Double result;

    private CalculationTrace(String symbolicTrace, String resolvedTrace, Double result) {
        this.symbolicTrace = symbolicTrace;
        this.resolvedTrace = resolvedTrace;
        this.result = result;
        traceElements = new LinkedList<CalculationTrace>();
    }

    /**
     * Constructor specifying the symbolic trace, resolved trace, and result.
     *
     * @param symbolicTrace
     *        The symbolic trace of the calculation.
     *
     * @param resolvedTrace
     *        The resolved trace of the calculation.
     *
     * @param result
     *        The result of the calculation.
     */
    public CalculationTrace(String symbolicTrace, String resolvedTrace, double result) {
        this(symbolicTrace,resolvedTrace,Double.valueOf(result));
    }

    /**
     * Constructor specifying the symbolic trace and result. The resolved trace will just be the string representation of
     * the result. This constructor is useful for specifying constants, though generally {@link ConstantTrace} should be
     * used where possible.
     *
     * @param symbolicTrace
     *        The symbolic trace of the calculation.
     *
     * @param result
     *        The result of the calculation.
     */
    public CalculationTrace(String symbolicTrace, double result) {
        this(symbolicTrace,Double.toString(result),result);
    }

    /**
     * Constructor specifying the symbolic trace. This constructor is useful for specifying traces which only consist
     * of symbols (such as the plus sign (<code>+</code>)), though generally {@link LabelTrace} should be used where
     * possible.  The resolved trace will be the same as the symbolic trace, and no result will be set for this trace.
     *
     * @param symbolicTrace
     *        The symbolic trace of the calculation.
     */
    public CalculationTrace(String symbolicTrace) {
        this(symbolicTrace,symbolicTrace,null);
    }

    /**
     * Add a trace element to this calculation trace. The trace element will go after all of the previously added elements.
     *
     * @param trace
     *        The trace element to add to this calculation trace.
     */
    public void addTraceElement(CalculationTrace trace) {
        traceElements.add(trace);
    }

    /**
     * Convenience method to add a trace element to this calculation trace, along with a "connector" to the previous trace
     * element.  This method is equivalent to calling:
     * <pre><code>
     *     addTraceElement(new LabelTrace(connector));
     *     addTraceElement(trace);
     * </code</pre>
     *
     * @param connector
     *        The label which connects the trace element to the previously added trace element in this calculation trace.
     *
     * @param trace
     *        The trace element to add to this calculation trace.
     */
    public void addTraceElement(String connector, CalculationTrace trace) {
        traceElements.add(new LabelTrace(connector));
        traceElements.add(trace);
    }

//    public void setResult(double result) {
//        if (this.result != null)
//            throw new IllegalStateException("Cannot set the trace result more than once.");
//        this.result = result;
//    }

    /**
     * Get this calculation trace's symbolic trace.
     *
     * @return the symbolic trace for this calculation trace.
     */
    public String getSymbolicTrace() {
        return symbolicTrace;
    }

    /**
     * Get this calculation trace's resolved trace.
     *
     * @return the resolved trace for this calculation trace.
     */
    public String getResolvedTrace() {
        return resolvedTrace;
    }

    /**
     * Get the list of calculation trace elements which make up the component parts of this calculation. If the calculation
     * is not composed of multiple parts (such as with a label or constant), then the list returned by this method should
     * be empty.
     *
     * @return this calculation trace's constituent trace elements.
     */
    public List<CalculationTrace> getTraceElements() {
        return Collections.unmodifiableList(traceElements);
    }

    /**
     * Get this calculation trace's result. This will return {@code null} if no result was specified.
     *
     * @return the result for this calculation trace.
     */
    public Double getResult() {
        return result;
    }

    public String toString() {
        return SimpleCalculationTracePrinter.traceToString(this);
    }
}
