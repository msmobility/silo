package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.io.IterableFileReader;
import com.pb.sawdust.io.IterableReader;

/**
 * The {@code WhitespaceDelimitedTableReader} is a table data reader for text files whose column entries are delimited
 * by whitespace. Here, whitespace refers to the (Java) regex character class <tt>[ \t\f\x0B]</tt>; that is, any
 * whitespace except line terminators. Any contiguous section of whitespace is treated as one delimiter, so as far
 * as reading data is considered, the following entries are equivalent:
 * <pre><tt>
 *    a b c
 *    a            b   c
 * </tt></pre>
 * This reader <i>is not</i> a general delimited data reader: it does not use a {@link com.pb.sawdust.util.parsing.DelimitedDataParser},
 * and thus does not allow any whitespace to be "ignored" by enclosing it in quotations.  As such, this class should
 * not be used as a <tt>[specific-whitespace-character]</tt> reader (<i>e.g.</i> tab-delimited text reader), as any
 * whitespaces it encounters will be treated as functionally the same as the <tt>[specific-whitespace-character]</tt> (for
 * example, spaces occuring in a tab-delimited file), resulting in incorrect/unexpected behavior.
 *
 * @author crf <br/>
 *         Started: Jul 11, 2008 10:27:26 AM
 */
public class WhitespaceDelimitedTextTableReader extends TextFileTableReader<String> {

    /**
     * Constructor specifying the file and table name for the whitespace-delimited data.
     *
     * @param filePath
     *        The path to the data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code fileName} was not found.
     */
    public WhitespaceDelimitedTextTableReader(String filePath, String tableName) {
        super(filePath, tableName);
    }

    /**
     * Constructor specifying the file the whitespace-delimited data. The file name (excluding directories) will be
     * used as the table name.
     *
     * @param filePath
     *        The path to the data file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code filePath} was not found.
     */
    public WhitespaceDelimitedTextTableReader(String filePath) {
        super(filePath);
    }

    protected IterableReader<String> getReader() {
        return IterableFileReader.getLineIterableFile(tableFile);
    }

    protected String[] getRowData(String row) {
        return row.split("\\s+");
    }
}
