package com.pb.sawdust.tabledata.write;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.util.format.DelimitedDataFormat;
import com.pb.sawdust.util.format.TextFormat;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;

/**
 * The {@code DelimitedTextTableWriter} class provides a {@code TableWriter} implementation for writing data tables to
 * single character delimited text files. The delimiter is specified when constructing the writer. The first line in the
 * output is a delimiter character-separated list of column names, and each subsequent line holds a delimiter character-separated
 * list of row data. More information about the specifics of the formatting can be found in the documenation for
 * {@link com.pb.sawdust.util.format.DelimitedDataFormat}.
 *
 * @author crf <br/>
 *         Started Mar 14, 2010 6:49:12 PM
 */
public class DelimitedTextTableWriter extends TextTableWriter {
    private static final TextFormat STRING_FORMAT = new TextFormat(TextFormat.Conversion.STRING);
    private final DelimitedDataFormat formatter;

    /**
     * Constructor the delimiter and specifying the path to the output file.
     *
     * @param delimiter
     *        The delimiter for the data.
     *
     * @param filePath
     *        The path to the file to which the data table will be written.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code file} is not found or cannot be created.
     */
    public DelimitedTextTableWriter(char delimiter, String filePath) {
        super(filePath);
        this.formatter = new DelimitedDataFormat(delimiter);
    }

    /**
     * Cosntructor specifying the delimiter and the file to which the table data will be written.
     *
     * @param delimiter
     *        The delimiter for the data.
     *
     * @param file
     *        The file to which the data table will be written.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code file} is not found or cannot be created.
     */
    public DelimitedTextTableWriter(char delimiter, File file) {
        super(file);
        this.formatter = new DelimitedDataFormat(delimiter);
    }

    /**
     * Constructor specifying the delimiter and the writer to which the table data will be written.
     *
     * @param delimiter
     *        The delimiter for the data.
     *
     * @param writer
     *        The writer which will write the table data.
     */
    public DelimitedTextTableWriter(char delimiter, Writer writer) {
        super(writer);
        this.formatter = new DelimitedDataFormat(delimiter);
    }

    /**
     * Constructor specifying the delimiter and output stream to which the table data will be sent.
     *
     * @param delimiter
     *        The delimiter for the data.
     *
     * @param outStream
     *        The output stream to which the table data will be sent.
     */
    public DelimitedTextTableWriter(char delimiter, OutputStream outStream) {
        super(outStream);
        this.formatter = new DelimitedDataFormat(delimiter);
    }

    protected void writeHeader(DataTable table) {
        TextFormat[] formats = new TextFormat[table.getColumnCount()];
        Arrays.fill(formats,STRING_FORMAT);
        writer.println(formatter.format(formats,(Object[]) table.getColumnLabels()));
    }

    protected void writeRow(Object[] dataRow, TextFormat[] format) {
        writer.println(formatter.format(format,dataRow));
    }

    protected void writeFooter(DataTable table) {
    }
}
