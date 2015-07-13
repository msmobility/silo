package com.pb.sawdust.model.builder.parser.yoube;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.model.builder.parser.ParsedFunctionBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code YoubeNumberMap} ...
 *
 * @author crf <br/>
 *         Started 7/15/11 2:51 PM
 */
public class YoubeNumberMap extends HashMap<String,ParsedFunctionBuilder.NumberParser> {
    private static final long serialVersionUID = -2885085193345417386L;

    public YoubeNumberMap(Map<String,ParsedFunctionBuilder.NumberParser> extraParsers) {
        loadDefaultNumberClasses();
        putAll(extraParsers);
    }

    private void loadDefaultNumberClasses() {
        put("POS_INT___",new ParsedFunctionBuilder.LongParser());
        put("NEG_INT___",new ParsedFunctionBuilder.LongParser() {
            public Long parse(String number) {
                return super.parse("-" + number);
            }
        });
        put("POS_DEC___",new ParsedFunctionBuilder.DoubleParser());
        put("NEG_DEC___",new ParsedFunctionBuilder.DoubleParser() {
            public Double parse(String number) {
                return super.parse("-" + number);
            }
        });
        put("TRUE___",new ParsedFunctionBuilder.DoubleParser() {
            public Double parse(String number) {
                return 1.0;
            }
        });
        put("FALSE___",new ParsedFunctionBuilder.DoubleParser() {
            public Double parse(String number) {
                return 0.0;
            }
        });
    }
}
