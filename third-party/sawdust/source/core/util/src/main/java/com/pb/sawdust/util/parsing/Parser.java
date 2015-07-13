package com.pb.sawdust.util.parsing;

/**
 * The {@code Parser} interface is intended for use by classes which can take an input and transform (parse) it into
 * an array of outputs. There is no requirement (nor expectation) that a given {@code Parser<I,O>} can parse e=<i>every</i>
 * given input {@code I}; the method {@code isParsable(I)} is specified to give users access to the parsability of a
 * given input.
 *
 * @param <I>
 *        The type of input to be parsed.
 *
 * @param <O>
 *        The output type of the parser.
 *
 * @author crf <br/>
 *         Started: Jul 8, 2008 1:35:43 PM
 */
public interface Parser<I,O> {

    /**
     * Determine whether the specified input can be parsed by this parser.
     *
     * @param input
     *        The input in question.
     *
     * @return {@code true} if {@code input} can be parsed, {@code false} if it cannot.
     */
    boolean isParsable(I input);

    /**
     * Parse an input into an array of outputs.
     *
     * @param input
     *        The input to parse.
     *
     * @return an output array.
     *
     * @throws IllegalArgumentException if the input is not parsable, <i>i.e.</i>, if <code>isParsable(input) == false</code>.
     */
    O[] parse(I input);
}
