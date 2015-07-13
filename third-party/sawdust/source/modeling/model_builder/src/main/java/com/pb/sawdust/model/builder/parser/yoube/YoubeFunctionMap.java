package com.pb.sawdust.model.builder.parser.yoube;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code YoubeFunctionMap} ...
 *
 * @author crf <br/>
 *         Started 7/14/11 5:32 PM
 */
public class YoubeFunctionMap extends HashMap<String,NumericFunctionN> {
    private static final long serialVersionUID = -2790468341227240750L;

    public YoubeFunctionMap(Map<String,NumericFunctionN> extraFunctions) {
        loadDefaultFunctions();
        putAll(extraFunctions);
    }

    private void loadDefaultFunctions() {
        put("^",NumericFunctions.POWER);
        put("*",NumericFunctions.MULTIPLY);
        put("/",NumericFunctions.DIVIDE);
        put("%",NumericFunctions.MODULO_DIVIDE);
        put("+",NumericFunctions.ADD);
        put("-",NumericFunctions.SUBTRACT);
        put("NEG___",NumericFunctions.NEGATE);
        put("POS___",NumericFunctions.PASS);
        put("abs",NumericFunctions.ABS);
        put("round",NumericFunctions.ROUND);
        put("ceil",NumericFunctions.CEIL);
        put("exp",NumericFunctions.EXP);
        put("floor",NumericFunctions.FLOOR);
        put("hypot",NumericFunctions.HYPOT);
        put("log",NumericFunctions.LOG);
        put("max",NumericFunctions.MAX);
        put("min",NumericFunctions.MIN);
        put("zdivide",NumericFunctions.ZERO_SAFE_DIVIDE);
        put("zlog",NumericFunctions.ZERO_SAFE_LOG);
        put("is_nan",NumericFunctions.IS_NAN);
        put("is_infinite",NumericFunctions.IS_INFINITE);
        put("nan_to_value",NumericFunctions.NAN_TO_VALUE);
        put("nan_to_zero",NumericFunctions.NAN_TO_ZERO);

        put("<>",NumericFunctions.NOT_EQUAL);
        put("!=",NumericFunctions.NOT_EQUAL);
        put("<=",NumericFunctions.LESS_THAN_OR_EQUAL);
        put(">=",NumericFunctions.GREATER_THAN_OR_EQUAL);
        put("&&",NumericFunctions.AND);
        put("&",NumericFunctions.AND);
        put("||",NumericFunctions.OR);
        put("|",NumericFunctions.OR);
        put("!",NumericFunctions.NOT);
        put("==",NumericFunctions.EQUAL);
        put("<",NumericFunctions.LESS_THAN);
        put(">",NumericFunctions.GREATER_THAN);
        put("ifelse",NumericFunctions.TERNARY);
        put("within",NumericFunctions.WITHIN_RANGE);
        put("lb_within",NumericFunctions.WITHIN_LOWER_BOUNDED_RANGE);
        put("ub_within",NumericFunctions.WITHIN_UPPER_BOUNDED_RANGE);
        put("b_within",NumericFunctions.WITHIN_BOUNDED_RANGE);

    }
}
