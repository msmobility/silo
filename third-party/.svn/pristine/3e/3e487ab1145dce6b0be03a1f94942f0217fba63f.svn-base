package com.pb.sawdust.tabledata.write;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.util.format.TextFormat;
import com.pb.sawdust.util.Range;

import java.util.EnumSet;
import java.util.Formatter;
import java.util.Set;
import java.util.HashSet;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * The {@code AsciiTableWriter} provides a {@code TableWriter} implementation which writes out data tables as "nicely"
 * formatted text tables. The ascii tablewill look something like the following:
 * <pre><tt>
 *     +----------+----------+
 *     | column 1 | column 2 |
 *     +----------+----------+
 *     |  cell 1a |  cell 2a |
 *     |  cell 1b |  cell 2b |
 *     +----------+----------+
 * </tt></pre>
 * for a 2 column, 2 row table. The formats for each column can be specified, giving some control over the output.
 * <p>
 * It is noted that despite its name, there is no requirement that the writer use the <code>ASCII</code> character
 * set.  If another character set is desired, then a {@code java.io.PrintStream} specifying a different character
 * encoding can be used. 
 *
 * @author crf <br/>
 *         Started: Jul 29, 2008 9:38:53 AM
 */
public class AsciiTableWriter extends TextTableWriter {
    private final Formatter formatter;
    private String tableEdge;
    private String headerFormat;
    private String rowFormat;

    /**
     * Constructor specifying the writer to which the table data will be written.
     *
     * @param writer
     *        The writer which will write the table data.
     */
    public AsciiTableWriter(Writer writer) {
        super(writer);
        this.formatter = new Formatter(this.writer);
    }

    /**
     * Constructor specifying the output stream to which the table data will be sent.
     *
     * @param outStream
     *        The output stream to which the table data will be sent.
     */
    public AsciiTableWriter(OutputStream outStream) {
        super(outStream);
        this.formatter = new Formatter(this.writer);
    }

    /**
     * Constructor for writing to {@code System.out}.
     */
    public AsciiTableWriter() {
        this(new PrintWriter(System.out));
    }
    
    protected void writeHeader(DataTable table) {
        writer.println(tableEdge);
        formatter.format(headerFormat,(Object[]) table.getColumnLabels());
        writer.println(tableEdge);
    }

    protected void writeRow(Object[] dataRow, TextFormat[] format) {
        formatter.format(rowFormat,dataRow);
    }

    protected void writeFooter(DataTable table) {
        writer.println(tableEdge);
    }

    private void setFormats(DataTable table,TextFormat[] formats) {
        //remove widths from formats
        TextFormat[] widthlessFormats = new TextFormat[formats.length];
        int counter = 0;
        Set<TextFormat.Conversion> allowPrecision = EnumSet.noneOf(TextFormat.Conversion.class);//  new HashSet<TextFormat.Conversion>();
        allowPrecision.add(TextFormat.Conversion.SCIENTIFIC);
        allowPrecision.add(TextFormat.Conversion.SCIENTIFIC_UPPER_CASE);
        allowPrecision.add(TextFormat.Conversion.FLOATING_POINT);
        allowPrecision.add(TextFormat.Conversion.FLOATING_POINT_HEXADECIMAL);
        allowPrecision.add(TextFormat.Conversion.FLOATING_POINT_HEXADECIMAL_UPPER_CASE);
        allowPrecision.add(TextFormat.Conversion.FLOATING_POINT_OR_SCIENTIFIC);
        allowPrecision.add(TextFormat.Conversion.FLOATING_POINT_OR_SCIENTIFIC_UPPER_CASE);
        for (TextFormat format : formats)
            if (allowPrecision.contains(format.getConversion()))
                widthlessFormats[counter++] = TextFormat.noMinimumWidthFormat(format);
            else
                widthlessFormats[counter++] = TextFormat.noMinimumWidthFormat(TextFormat.noPrecisionFormat(format));
        //loop over the table once to check for column widths, then again to create table string
        int columnCount = table.getColumnCount();
        Range columnRange = new Range(columnCount);
        int[] columnWidths = new int[columnCount];
        String[] columnLabels = table.getColumnLabels();
        for (int i : columnRange)
            columnWidths[i] = columnLabels[i].length();
        for (DataRow row : table) {
            for (int i : columnRange) {
                int cellWidth = new Formatter().format(widthlessFormats[i].getFormat(),row.getCell(i)).out().toString().length();
                if (columnWidths[i] < cellWidth)
                    columnWidths[i] = cellWidth;
            }
        }
        StringBuilder rowString = new StringBuilder("|");
        StringBuilder headerString = new StringBuilder("|");
        StringBuilder edge = new StringBuilder("+");
        for (int i : columnRange) {
            rowString.append(" ").append(TextFormat.getMinimumWidthFormat(widthlessFormats[i],columnWidths[i])).append(" |");
            headerString.append(" ").append(new TextFormat(TextFormat.Conversion.STRING,columnWidths[i])).append(" |");
            for (int j : Range.range(columnWidths[i]+2))
                edge.append("-");
            edge.append("+");
        }
        rowString.append(TextFormat.NEW_LINE_FORMAT);
        headerString.append(TextFormat.NEW_LINE_FORMAT);
        headerFormat = headerString.toString();
        rowFormat = rowString.toString();
        tableEdge = edge.toString();
    }

    public void writeTable(DataTable table,TextFormat ... formats) {
        if (formats.length != table.getColumnCount())
            throw new IllegalArgumentException("Format count must match table column count; expected " + table.getColumnCount() + ", found " + formats.length);
        setFormats(table,formats);
        super.writeTable(table,formats);
        writer.close();
    }
}
