package com.pb.sawdust.tabledata.write;

import java.io.Writer;
import java.io.OutputStream;
import java.io.File;

/**
 * The {@code CsvTableWriter} class provides a {@code TableWriter} implementation for writing data tables to comma-separated
 * value (csv) files. This class just extends {@link DelimitedTextTableWriter} using a comma '<code>,</code>' as the delimiter.
 *
 * @author crf <br/>
 *         Started: Aug 1, 2008 10:07:01 AM
 */
public class CsvTableWriter extends DelimitedTextTableWriter {

    /**
     * Constructor specifying the path to the output file.
     *
     * @param filePath
     *        The path to the file to which the data table will be written.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code file} is not found or cannot be created.
     */
    public CsvTableWriter(String filePath) {
        super(',',filePath);
    }

    /**
     * Cosntructor specifying the file to which the table data will be written.
     *
     * @param file
     *        The file to which the data table will be written.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code file} is not found or cannot be created.
     */
    public CsvTableWriter(File file) {
        super(',',file);
    }

    /**
     * Constructor specifying the writer to which the table data will be written.
     *
     * @param writer
     *        The writer which will write the table data.
     */
    public CsvTableWriter(Writer writer) {
        super(',',writer);
    }

    /**
     * Constructor specifying the output stream to which the table data will be sent.
     *
     * @param outStream
     *        The output stream to which the table data will be sent.
     */
    public CsvTableWriter(OutputStream outStream) {
        super(',',outStream);
    }
}
