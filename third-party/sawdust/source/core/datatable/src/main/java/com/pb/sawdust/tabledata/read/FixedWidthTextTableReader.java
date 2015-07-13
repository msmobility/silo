package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.io.IterableFileReader;
import com.pb.sawdust.io.IterableReader;
import com.pb.sawdust.util.parsing.FixedWidthStringParser;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The {@code FixedWidthTextTableReader} is used to read text files with fixed width columns. The constructors require
 * either the individual column widths to be specified or he column (start) positions and line length.
 *
 * @author crf <br/>
 *         Started: Jul 2, 2008 3:19:20 PM
 */
public class FixedWidthTextTableReader extends TextFileTableReader<String> {
    private final FixedWidthStringParser parser;

    /**
     * Constructor specifying the file and table name, as well as column widths, for the fixed width data.
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @param widths
     *        The widths (in characters) for each column in the table file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     */
    public FixedWidthTextTableReader(String filePath, String tableName, int[] widths) {
        super(filePath, tableName);
        parser = new FixedWidthStringParser(widths);
    }

    /**
     * Constructor specifying the file name, as well as column widths, for the fixed width data. The file name (excluding
     * directories) will be used as the table name.
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @param widths
     *        The widths (in characters) for each column in the table file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     */
    public FixedWidthTextTableReader(String filePath, int[] widths) {
        super(filePath);
        parser = new FixedWidthStringParser(widths);
    }

    /**
     * Constructor specifying the file and table name, as well as column positions and line length, for the fixed width
     * data.
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @param positions
     *        The (0-based) line positions of the start of each column.
     *
     * @param lineLength
     *        The length of each line.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     * @throws IllegalArgumentException if {@code positions} does not start at 0, if the elements of {@code positions}
     *                                  are not in ascending order, or if the last position in {@code positions} is
     *                                  not less than {@code lineLength}.
     */
    public FixedWidthTextTableReader(String filePath, String tableName, int[] positions, int lineLength) {
        super(filePath, tableName);
        parser = new FixedWidthStringParser(positions,lineLength);
    }

    /**
     * Constructor specifying the file name, as well as column positions and line length, for the fixed width data.
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @param positions
     *        The (0-based) line positions of the start of each column
     *
     * @param lineLength
     *        The length of each line.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     * @throws IllegalArgumentException if {@code positions} does not start at 0, if the elements of {@code positions}
     *                                  are not in ascending order, or if the last position in {@code positions} is
     *                                  not less than {@code lineLength}.
     */
    public FixedWidthTextTableReader(String filePath, int[] positions, int lineLength) {
        super(filePath);
        parser = new FixedWidthStringParser(positions,lineLength);
    }

    /**
     * Constructor specifying the file and table name in which an attempt will be made to infer the column widths. This
     * inference is very simple and will probably only work on the simplest of tables. In a broad sense, it follows these
     * rules:
     * <ul>
     *     <li>
     *          Spaces are assumed to be padding in the front of columns.
     *     </li>
     *     <li>
     *          Numbers are held together as single columns - so no numeric text is inferred.
     *     </li>
     *     <li>
     *          The smallest line length determines the width used column parser.
     *     </li>
     *     <li>
     *          Column widths for all rows are inferred, and these inferred widths are merged (the merging retains all
     *          discovered column delimitation points).
     *     </li>
     *     <li>
     *          The smallest line length determines the width used column parser.
     *     </li>
     * </ul>
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     */
    public FixedWidthTextTableReader(String filePath, String tableName) {
        super(filePath,tableName);
        int[] points = guessLinePoints();
        int[] newPoints = new int[points.length - 1];
        System.arraycopy(points,0,newPoints,0,newPoints.length);
        parser = new FixedWidthStringParser(newPoints,points[points.length-1]);
    }

    /**
     * Constructor specifying the file name in which an attempt will be made to infer the column widths. The file name
     * (excluding directories) will be used as the table name.This inference is very simple and will probably only work
     * on the simplest of tables. It is described in {@link #FixedWidthTextTableReader(String, String)}.
     *
     * @param filePath
     *        The path to the csv data file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     */
    public FixedWidthTextTableReader(String filePath) {
        super(filePath);
        int[] points = guessLinePoints();
        int[] newPoints = new int[points.length - 1];
        System.arraycopy(points,0,newPoints,0,newPoints.length);
        parser = new FixedWidthStringParser(newPoints,points[points.length-1]);
    }

    protected IterableReader<String> getReader() {
        return IterableFileReader.getLineIterableFile(tableFile);
    }

    protected String[] getRowData(String row) {
        char[] charLine = row.toCharArray();
        if (!parser.isParsable(charLine))
            throw new IllegalStateException("Fixed width line too short for specified columns - expected >=" + parser.getMinLength() + ", found " + row.length() + ":\n\t" + row);
        return parser.parse(charLine);
    }

    private int[] guessLinePoints() {
        int[] points = new int[0];
        for (String line : getReader())
            points = mergePoints(guessLinePointsForOneLine(line),points);
        return points;
    }

    private int[] mergePoints(int[] w1, int[] w2) {
            List<Integer> i = new LinkedList<Integer>();
            @SuppressWarnings("unchecked") //ok, primitive iterator'll get autoboxed
            Iterator<Integer> it1 = ArrayUtil.getIterator(w1);
            @SuppressWarnings("unchecked") //ok, primitive iterator'll get autoboxed
            Iterator<Integer> it2 = ArrayUtil.getIterator(w2);
            boolean f1;
            boolean f2;
            int v1 = !(f1 = !it1.hasNext()) ? it1.next() : 0;
            int v2 = !(f2 = !it2.hasNext()) ? it2.next() : 0;
            while(!f1 || !f2) {
                if (f1) {
                    if (v1 != 0)
                        break;
                    i.add(v2);
                    if (it2.hasNext())
                        v2 = it2.next();
                    else
                        f2 = true;
                    continue;
                } else if (f2) {
                    if (v2 != 0)
                        break;
                    i.add(v1);
                    if (it1.hasNext())
                        v1 = it1.next();
                    else
                        f1 = true;
                    continue;
                }

                if (v1 < v2) {
                    i.add(v1);
                    if (it1.hasNext())
                        v1 = it1.next();
                    else
                        f1 = true;
                } else if (v2 < v1) {
                    i.add(v2);
                    if (it2.hasNext())
                        v2 = it2.next();
                    else
                        f2 = true;
                } else {
                    i.add(v2);
                    if (it2.hasNext())
                        v2 = it2.next();
                    else
                        f2 = true;
                    if (it1.hasNext())
                        v1 = it1.next();
                    else
                        f1 = true;
                }
            }
            return ArrayUtil.toPrimitive(i.toArray(new Integer[i.size()]));
        }

        private int[] guessLinePointsForOneLine(String line) {
            List<Integer> points = new LinkedList<Integer>();
            boolean inText = false;
            boolean inColumnPadding = true;
            char[] lineChars = line.toCharArray();
            for (int i : range(lineChars.length)) {
                switch (lineChars[i]) {
                    case ' ' : {
                        if (!inColumnPadding) {
                            inColumnPadding = true;
                            inText = false;
                            points.add(i);
                        }
                        break;
                    }
                    case '0' :
                    case '1' :
                    case '2' :
                    case '3' :
                    case '4' :
                    case '5' :
                    case '6' :
                    case '7' :
                    case '8' :
                    case '9' : {
                        if (inText) {
                            inText = false;
                            points.add(i);
                        }
                        inColumnPadding = false;
                        break;
                    }
                    case '+' :
                    case '-' : break; //could be text/number - treat as padding, but at expense of lone '-'/'+' cannot be entry
                    case '.' :
                    case 'E' : {
                        inColumnPadding = false;
                        break; //can be number or letter, I guess
                    }
                    default : {
                        inText = true;
                        if (!inText && !inColumnPadding) {
                            inText = true;
                            points.add(i);
                        }
                        inColumnPadding = false;
                    }
                }
            }
            points.add(lineChars.length);
            return ArrayUtil.toPrimitive(points.toArray(new Integer[points.size()]));
        }
}
