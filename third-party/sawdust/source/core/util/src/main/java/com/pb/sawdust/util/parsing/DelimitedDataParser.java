package com.pb.sawdust.util.parsing;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.LinkedList;

/**
 * The {@code DelimitedDataParser} is used to read delimited text data. Delimited data is structured such that a given
 * entry holds a series of data entries, each separated by a specified delimiter. The following rules are assumed for
 * delimited text:
 * <ul>
 *     <li>Quoted entries consist of an entry with a double quote ({@code "}) character immediately before and after the entry.</li>
 *     <li>Unless quoted, an entry is defined to be the "delimiterless" text between two delimiters, or between a
 *         delimiter and the end of a line.</li>
 *     <li>If quoted, an entry can contain a delimiter which will not be used to define text entries (it becomes part of
 *         the quoted entry's text).</li>
 *     <li>To include a double quote character inside of an entry, a double-double quote ({@code ""}) is used and the
 *         entry must be quoted.</li>
 * </ul>
 * The above implies that each entry must contain an even number of double quote characters; otherwise it is considered
 * unparsable.
 * <p>
 * The delimiter is instance specific; however, as this class uses regular expressions to parse entries, certain characters
 * may create unexpected behavior. This class attempts to deal with potential problem delimiters, but makes no guarantee
 * that all issues are handled.
 *
 * @author crf <br/>
 *         Started: Jul 8, 2008 1:37:33 PM
 */
public class DelimitedDataParser implements Parser<String,String> {
    private static final Pattern doubleQuotePattern = Pattern.compile("\"\"");
    private final Pattern delimiterPattern;
    private final Pattern simpleDelimiterPattern;
    private final String delimiter;
    private final int delimiterLength;

    /**
     * Constructor specifying the delimiter character. See the main comments in this class for caveats concerning
     * delimiter characters.
     *
     * @param delimiter
     *        The delimiter character.
     */
    public DelimitedDataParser(char delimiter) {
        this(String.valueOf(delimiter));
    }

    /**
     * Constructor specifying the delimiter string. See the main comments in this class for caveats concerning
     * delimiter characters.
     *
     * @param delimiter
     *        The delimiter string.
     */
    public DelimitedDataParser(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (char c : delimiter.toCharArray())
            sb.append(reMapDelimiter(c));
        String reDelimiter = sb.toString();
//        this.delimiterPattern = Pattern.compile("\\G(?:^|" + reDelimiter + ")(?:\"([^\"]*+(?:\"\"[^\"]*+)*+)\"|([^\"" + reDelimiter + "]*+))");
        this.delimiterPattern = Pattern.compile("\\G(?:^|" + reDelimiter + ")(?:\"([^\"]*+(?:\"\"[^\"]*+)*+)\"|([^\"]*?(?=" + reDelimiter + ")|[^\"]*))");

        this.delimiter = "" + delimiter;
        delimiterLength = delimiter.length();
        simpleDelimiterPattern = Pattern.compile(reDelimiter);
    }

    private String reMapDelimiter(char delimiter) {
        switch (delimiter) {
            case '\t' : return "\\t";
            case '\\' : return "\\\\";
            case '?'  : return "\\?";
            case '|'  : return "\\|";
            default : return ((Character) delimiter).toString();
        }
    }

    private int getQuoteCount(String input) {
        int q = 0;
        for (char c : input.toCharArray())
            if (c == '"')
                q++;
        return q;
    }

    private boolean isParsable(int quoteCount) {
        return  quoteCount % 2 == 0;
    }

    public boolean isParsable(String input) {
        //counts quotes to ensure we are valid - must be multiples of two
        return isParsable(getQuoteCount(input));
    }

    public String[] parse(String input) {
        int quoteCount = getQuoteCount(input);
        if (!isParsable(quoteCount))
            throw new IllegalArgumentException("Input not parsable: " + input);
        if (quoteCount == 0)
           return simpleDelimiterPattern.split(input,-1);
        List<String> elements = new LinkedList<String>();
        //if starts with delimiter, then add an empty string as first element and remove opening delimiter
        while (input.length() > 0 && input.substring(0,delimiterLength).equals(delimiter)) {
            input = input.substring(delimiterLength);
            elements.add("");
        }
        Matcher m = delimiterPattern.matcher(input);
        Matcher doubleQuote = doubleQuotePattern.matcher(""); //will reuse as needed
        while (m.find())
            if (m.start(2) >= 0)
                elements.add(m.group(2));
            else
                elements.add(doubleQuote.reset(m.group(1)).replaceAll("\""));
        return elements.toArray(new String[elements.size()]);
    }
}
