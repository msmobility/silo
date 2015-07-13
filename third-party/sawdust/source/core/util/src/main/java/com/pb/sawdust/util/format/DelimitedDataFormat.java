package com.pb.sawdust.util.format;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.calculator.Function1;

import java.util.Arrays;


/**
 * The {@code DelimitedDataFormat} class allows delimited text data to be produced in a systematic fashion. Each
 * instance is tied to a specific delimiter {@code char} or {@code String}, and arrays of data passed into the {@code format(...)}
 * methods will return strings containing that data delimited by the instance's delimiter. The formatting rules are as
 * follows:
 * <ul>
 *     <li>Except for the first item, each item immediately follows the delimiter.</li>
 *     <li>If an item contains the delimiter, the item will be encased in double quote symbols (<tt>"</tt>).</li>
 *     <li>If an item contains double quotes, the item will be encased in double quote symbols amd its double quotes
 *         are replaced with "double-double quotes" ({@code ""}).</li>
 * </ul>
 * These rules are the producer-equivalent version of those described in {@link com.pb.sawdust.util.parsing.DelimitedDataParser}.
 * This class also allows the formatting of the individual data items to be controlled through the use of formatters.
 * @author crf <br/>
 *         Started: Aug 1, 2008 10:09:24 AM
 */
public class DelimitedDataFormat {
    private final String delimiter;

    /**
     * Constructor specifying the delimiter character.
     *
     * @param delimiter
     *        The character to use as the text delimiter.
     */
    public DelimitedDataFormat(char delimiter) {
        this(String.valueOf(delimiter));
    }

    /**
     * Constructor specifying the delimiter string.
     *
     * @param delimiter
     *        The string to use as the text delimiter.
     */
    public DelimitedDataFormat(String delimiter) {
        this.delimiter = delimiter;
    }

    private static final Function1<TextFormat,String> formatToString =
        new Function1<TextFormat,String>() {
            public String apply(TextFormat value) {
                return value.getFormat();
            }
        };

    /**
     * Format a series of data items into a delimited data string using specified formats for each data item.
     *
     * @param formats
     *        The formats to use to convert each item in {@code data} into a string. Each item in this arry corresponds
     *        to the item in {@code data} with the same index.
     *
     * @param data
     *        The data which will be formatted into a delimited text string.
     *
     * @return {@code data} as delimited text.
     *
     * @throws IllegalArgumentException if <code>formats.length != data.length</code> or if a given item in {@code data}
     *                                  cannot be formatted with the corresponding format in {@code formats}.
     */
    public String format(TextFormat[] formats, Object ... data) {
        for (int i : range(formats.length))
            if (!formats[i].isValidArgument(data[i]))
                throw new IllegalArgumentException("Data item cannot be used with format " + formats[i].getFormat() + ": " + data[i]);
        return format(ArrayUtil.apply(formats,formatToString,new String[formats.length]),data);
    }


    /**
     * Format a series of data items into a delimited data string using specified formats for each data item.
     *
     * @param formats
     *        The formats to use to convert each item in {@code data} into a string. Each item in this arry corresponds
     *        to the item in {@code data} with the same index. Each format must be a valid format for a single argument;
     *        <i>i.e.</i>, it must contain only one '<tt>%</tt>' character (not including double percents ("<tt>%%</tt>')
     *        used for literal percent signs).
     *
     * @param data
     *        The data which will be formatted into a delimited text string.
     *
     * @return {@code data} as delimited text.
     *
     * @throws IllegalArgumentException if <code>formats.length != data.length</code>
     * @throws java.util.IllegalFormatException if one of the formats in {@code format} have invalid syntax or cannot
     *                                          be used with the corresponding {@code data} item.
     */
    public String format(String[] formats, Object ... data) {
        if (formats.length != data.length)
            throw new IllegalArgumentException("Data and formats must be same length.");
        String[] formattedData = new String[data.length];
        for (int i : range(formats.length))
            formattedData[i] = String.format(formats[i],data[i]);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String element : formattedData) {
            if (first)
                first = false;
            else
                sb.append(delimiter);
            if (element.indexOf(delimiter) > -1 || element.indexOf("\"") > -1)
                sb.append("\"").append(element.replace("\"","\"\"")).append("\"");
            else
                sb.append(element);
        }
        return sb.toString();
    }

    /**
     * Format a series of data items into a delimited data string. The most general {@code TextFormat}
     * (<code>new TextFormat(TextFormat.Conversion.STRING)</code>) will be used to format each data item.
     *
     * @param data
     *        The data which will be formatted into a delimited text string.
     *
     * @return {@code data} as delimited text.
     */
    public String format(Object ... data) {
        TextFormat[] formats = new TextFormat[data.length];
        Arrays.fill(formats,new TextFormat(TextFormat.Conversion.STRING));
        return format(formats,data);
    }

    /**
     * Format a series of {@code byte}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(byte ... data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }   

    /**
     * Format a series of {@code short}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(short ... data) {
        StringBuilder sb = new StringBuilder();
        for (short b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code int}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(int ... data) {
        StringBuilder sb = new StringBuilder();
        for (int b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code long}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(long ... data) {
        StringBuilder sb = new StringBuilder();
        for (long b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code float}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(float ... data) {
        StringBuilder sb = new StringBuilder();
        for (float b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code double}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(double ... data) {
        StringBuilder sb = new StringBuilder();
        for (double b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code char}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(char ... data) {
        StringBuilder sb = new StringBuilder();
        for (char b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Format a series of {@code boolean}s into a delimited data string. The default JVM conversion will be used to convert
     * the data.
     * 
     * @param data
     *        The data to format.
     *        
     * @return {@code data} as delimited text.
     */
    public String format(boolean ... data) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : data)
            sb.append(delimiter).append(b);
        return data.length == 0 ? "" : sb.substring(delimiter.length()); //drop first delimiter
    }

    /**
     * Get a format string for delimited data using the specified formats. The returned string can be used as the format
     * argument for a string format call (<i>e.g.</i> {@code String.format(String,Object...)}. There will be one numbered
     * argument for each format passed to this method.
     * <p>
     * This method assumes that data formatted with the returned formatting string does not include the delimiter nor
     * double quotes, or that they are already correctly escaped. To format data that does not satisfy this condition,
     * use {@link #format(TextFormat[], Object...)}.
     *
     * @param formats
     *        The formats for the data.
     *
     * @return a delimited formatting string using {@code formats}.
     */
    public String getFormatString(TextFormat ... formats) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i : range(formats.length)) {
            if (first)
                first = false;
            else
                sb.append(delimiter);
            sb.append(formats[i].getFormat(i+1));
        }
        return sb.toString();
    }
}
