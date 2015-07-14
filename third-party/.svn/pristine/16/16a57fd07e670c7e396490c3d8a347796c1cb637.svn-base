package com.pb.sawdust.model.builder.parser.yoube;

import com.pb.sawdust.calculator.*;
import com.pb.sawdust.model.builder.parser.ParsedFunctionBuilder;
import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.range;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import java.util.*;

/**
 * The {@code YoubeParsedFunctionBuilder} ...
 *
 * @author crf <br/>
 *         Started 4/18/11 12:23 PM
 */
public class YoubeParsedFunctionBuilder extends ParsedFunctionBuilder {
    public static final String FUNCTION_IDENTIFIER = "FUNCTION___";

    public YoubeParsedFunctionBuilder(Map<String,NumberParser> numberParseMap, Map<String,NumericFunctionN> functionMap) {
        super(new YoubeNumberMap(numberParseMap),new YoubeFunctionMap(functionMap));
    }

    @SuppressWarnings("unchecked") //these empty maps are ignored, so no issues
    public YoubeParsedFunctionBuilder() {
        this(Collections.EMPTY_MAP,Collections.EMPTY_MAP);
    }

    @Override
    protected com.pb.sawdust.calculator.ParameterizedNumericFunction buildFunction(List<String> formula) {
        Deque<Object> stack = getFormulaStack(formula);

        //next up, collapse functions, if possible
        stack = collapseFormulaStack(stack);

        List<NumericFunctionN> rpf = new LinkedList<NumericFunctionN>();
        List<Number> values = new LinkedList<Number>();
        List<Integer> missingDimensions = new LinkedList<Integer>();
        List<String> arguments = new LinkedList<String>();
        JavaType dominatingType = JavaType.LONG;
        int counter = 0;
        for (Object o : stack) {
            if (o instanceof NumericFunctionN) {
                rpf.add((NumericFunctionN) o);
            } else {
                rpf.add(NumericFunctions.PARAMETER);
                if (o instanceof Number) {
                    Number n = (Number) o;
                    values.add(n);
                    if (dominatingType == JavaType.LONG && (n instanceof Double || n instanceof Float))
                        dominatingType = JavaType.DOUBLE;
                } else {
                    values.add(-1);
                    missingDimensions.add(counter);
                    arguments.add((String) o);
                }
                counter++;
            }
        }
        NumericValuesProvider provider;
        if (dominatingType == JavaType.LONG) {
            long[] v = new long[values.size()];
            counter = 0;
            for (Number n : values)
                v[counter++] = n.longValue();
            provider = new NumericValuesProvider.LongArrayValuesProvider(v);
        } else { //double
            double[] v = new double[values.size()];
            counter = 0;
            for (Number n : values)
                v[counter++] = n.doubleValue();
            provider = new NumericValuesProvider.DoubleArrayValuesProvider(v);
        }
        NumericFunctionN function =  new PartialNumericFunctionN(NumericFunctions.compositeNumericFunction(rpf),
                                                                 ArrayUtil.toPrimitive(missingDimensions.toArray(new Integer[missingDimensions.size()])),
                                                                 provider);
        return new BasicParameterizedNumericFunction(function,arguments);
    }

    private Deque<Object> getFormulaStack(List<String> formula) {
        Deque<Object> stack = new LinkedList<Object>();
        for (String f : formula) {
            if (numberParseMap.containsKey(f)) {
                stack.addLast(numberParseMap.get(f).parse((String) stack.removeLast()));
            } else if (f.equals(FUNCTION_IDENTIFIER)) {
                Object function = stack.getLast();
                if (function instanceof String)
                    throw new IllegalStateException("Function not found: " + function);
            } else if (functionMap.containsKey(f)) {
                stack.addLast(functionMap.get(f));
            } else {
                stack.addLast(f);
            }
        }
        return stack;
    }

    private Deque<Object> collapseFormulaStack(Deque<Object> stack) {
        //todo: this
        return stack;
    }

    @Override
    protected List<String> parseToReversePolish(String formula) {
        YoubeParser yp = new YoubeParser(new TokenRewriteStream(new YoubeLexer(new ANTLRStringStream(formula))));
        yp.setTreeAdaptor(ADAPTOR);
        try {
            return toReversePolish((CommonTree) yp.formula().getTree());
        } catch (RecognitionException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    private static final TreeAdaptor ADAPTOR = new CommonTreeAdaptor() {
        public Object create(Token payload) {
            return new CommonTree(payload);
        }
    };

    private List<String> toReversePolish(Tree tree) {
        List<String> rbBranches = new LinkedList<String>();
        String trunk = tree.getText();
        for (int i : range(tree.getChildCount()))
            rbBranches.addAll(toReversePolish(tree.getChild(i)));
        rbBranches.add(trunk);
        return rbBranches;
    }

    public static void main(String ... args) throws Exception {
        //make a simple formula
        String test = "(-6.0+9)*min(8,f)+9.0";
//        test = "(-6.0+9)*min(8,f)+9.0";
        test = "true && 6 > f";
        test = "6 > f";
        test = " !(true && 6 < f) || G";
//        test = "true && (6 > f)";

        YoubeParsedFunctionBuilder builder = new YoubeParsedFunctionBuilder();
        System.out.println(test);
//        System.out.println(builder.parseToReversePolish(test));
//        System.out.println(builder.getFormulaStack(builder.parseToReversePolish(test)));

        com.pb.sawdust.calculator.ParameterizedNumericFunction f =  builder.buildFunction(test);
        System.out.println(f.getFunction() + " " + f.getArgumentNames() + " " + f.getFunction().getArgumentCount());
        System.out.println(f.getFunction().getSymbolicFormat());
        double v = 1.0;
        System.out.println("f = " + v);
//        System.out.println(f.getFunction().applyDouble(2.0));
        System.out.println(f.getFunction().applyDouble(v,7.4));
    }
}
