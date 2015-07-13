package com.pb.sawdust.model.models.trace;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.format.TextFormat;

import java.util.List;

import static com.pb.sawdust.util.Range.range;


/**
 * The {@code SimpleCalculationTracePrinter} ...
 *
 * @author crf <br/>
 *         Started Dec 8, 2010 1:00:33 AM
 */
public class SimpleCalculationTracePrinter {
    public static final String DEFAULT_LEVEL_SHIFT = "    ";
    public static final String SECTION_SEPARATOR =  " | ";

    public static String traceToString(CalculationTrace trace) {
        int[] lengths = new int[2];
        fillInBiggestLengths(0,lengths,trace.getTraceElements());
        return traceToString(trace,0,lengths[0],lengths[1]);
    }

    private static String repeat(CharSequence s, int amount) {
        StringBuilder sb = new StringBuilder();
        for (int i : range(amount))
            sb.append(s);
        return sb.toString();
    }

    private static void fillInBiggestLengths(int level, int[] currentLengths, List<CalculationTrace> traceElements) {
        for (CalculationTrace element : traceElements) {
            currentLengths[0] = Math.max(currentLengths[0],element.getSymbolicTrace().length()+(level+1)* DEFAULT_LEVEL_SHIFT.length());
            String rt = element.getResolvedTrace();
            if (rt != null)
                currentLengths[1] = Math.max(currentLengths[1],rt.length()+(level+1)* DEFAULT_LEVEL_SHIFT.length());
            fillInBiggestLengths(level+1,currentLengths,element.getTraceElements());
        }
    }

    private static String traceToString(CalculationTrace t, int level, int biggestLeft, int biggestMiddle) {
        TextFormat lFormat = new TextFormat(TextFormat.Conversion.STRING,biggestLeft+2,TextFormat.FormatFlag.LEFT_JUSTIFIED);
        TextFormat mFormat = new TextFormat(TextFormat.Conversion.STRING,biggestMiddle+2,TextFormat.FormatFlag.LEFT_JUSTIFIED);
        TextFormat rFormat = new TextFormat(TextFormat.Conversion.FLOATING_POINT,8,4);
        String depth = repeat(DEFAULT_LEVEL_SHIFT,level);
        String nameFormatString = lFormat.getFormat(1) + SECTION_SEPARATOR + mFormat.getFormat(2) + SECTION_SEPARATOR + rFormat.getFormat(3) + FileUtil.getLineSeparator();
        String noResultNameFormatString = lFormat.getFormat(1) + SECTION_SEPARATOR + mFormat.getFormat(2) + SECTION_SEPARATOR + new TextFormat(TextFormat.Conversion.STRING).getFormat(3) + FileUtil.getLineSeparator();
        StringBuilder sb = new StringBuilder(level == 0 ? "\n" : "");

        String symbolicTrace = t.getSymbolicTrace();
        String resolvedTrace = t.getResolvedTrace();
        Double result = t.getResult();
        List<CalculationTrace> traceElements = t.getTraceElements();

        boolean subs = false;
        //will print out subtraces *only* if subelements themselves have subelements (otherwise, they are redundant)
        for (CalculationTrace element : t.getTraceElements())
            if (traceElements.size() > 0)
                subs = true;
        String open = subs ? " {" : "  ";
        sb.append(result == null ?
                      //String.format(noResultNameFormatString,depth + symbolicTrace + open,depth + resolvedTrace + open,this instanceof LabelTrace ? "" : "<not set>") :
                      String.format(noResultNameFormatString,depth + symbolicTrace + open,depth + resolvedTrace + open,"") :
                      String.format(nameFormatString,depth + symbolicTrace + open,depth + resolvedTrace + open,result));
        if (subs) {
            for (CalculationTrace element : traceElements)
                sb.append(traceToString(element,level+1,biggestLeft,biggestMiddle));
            sb.append(String.format(noResultNameFormatString,depth + "} ",depth + "}",""));
        }
        return sb.toString();
    }
}
