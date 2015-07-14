package com.pb.sawdust.tabledata.util;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.ListDataTable;
import com.pb.sawdust.tabledata.basic.ReadOnlyWrappedDataTable;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.write.AsciiTableWriter;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.iterators.IterableIterator;
import com.pb.sawdust.util.format.TextFormat;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;


/**
 * The {@code TableDataUtil} class provides utility functions for working with table data objects.
 *
 * @author crf <br/>
 *         Started: Jul 1, 2008 9:25:25 AM
 */
public class TableDataUtil {

    private TableDataUtil() {}

    /**
     * Get a "nice" string representation of a {@code DataTable}. The representation takes the form of an ascii table,
     * described in {@link com.pb.sawdust.tabledata.write.AsciiTableWriter}. Because this will create a representation
     * of the entire table as text, it is not recommended to use this method on tables with large amounts of data.
     *
     * @param table
     *        The table.
     *
     * @return a string representation of {@code table}.
     */
    public static String toString(DataTable table) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        AsciiTableWriter writer = new AsciiTableWriter(outStream);
        writer.writeTable(table);
        return outStream.toString();
    }

    /**
     * Get a "nice" string representation of a {@code DataTable}. The representation takes the form of an ascii table,
     * described in {@link com.pb.sawdust.tabledata.write.AsciiTableWriter}. Because this will create a representation
     * of the entire table as text, it is not recommended to use this method on tables with large amounts of data.
     *
     * @param table
     *        The table.
     *
     * @param formats
     *        The formats to use for the columns in the table.
     *
     * @return a string representation of {@code table}.
     *
     * @throws IllegalArgumentException if the number of formats in {@code formats} does not equal the number of columns
     *                                  in {@code table}.
     */
    public static String toString(DataTable table, TextFormat ... formats) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        AsciiTableWriter writer = new AsciiTableWriter(outStream);
        writer.writeTable(table,formats);
        return outStream.toString();
    }

    private static DataTable toTable(Iterable<DataRow> rows) {
        DataTable table = null;
        for (DataRow row : rows) {
            if (table == null)
                 table = new ListDataTable(new TableSchema("",row.getColumnLabels(),row.getColumnTypes()));
            table.addRow(row);
        }
        if (table == null)
            throw new IllegalArgumentException("At least one row must be defined.");
        return table;

    }

    /**
     * Return a series of {@code DataRow}s as a {@code DataTable}. The returned table will be read only.
     *
     * @param rows
     *        The rows.
     *
     * @return a data table containing {@code rows}.
     *
     * @throws IllegalArgumentException if there are no rows in {@code rows}, or if any one of the rows in {@code rows} is
     *                                  incompatible with another (<i>i.e.</i> has a different number of columns, or has
     *                                  a different data type for one of the columns).
     */
    public static DataTable asTable(DataRow ... rows) {
        return asTable(new IterableIterator<>(ArrayUtil.getIterator(rows)));
    }

    /**
     * Return a series of {@code DataRow}s as a {@code DataTable}. The returned table will be read only.
     *
     * @param rows
     *        The rows.
     *
     * @return a data table containing {@code rows}.
     *
     * @throws IllegalArgumentException if there are no rows in {@code rows}, or if any one of the rows in {@code rows} is
     *                                  incompatible with another (<i>i.e.</i> has a different number of columns, or has
     *                                  a different data type for one of the columns).
     */
    public static DataTable asTable(Iterable<DataRow> rows) {
        return new ReadOnlyWrappedDataTable(toTable(rows));
    }

    /**
     * Get a "nice" string representation of a series of {@code DataRow}s. The representation takes the form of an ascii table,
     * described in {@link com.pb.sawdust.tabledata.write.AsciiTableWriter}. Because this will create a representation
     * of the rows as text, it is not recommended to use this method on large numbers of rows.
     *
     * @param rows
     *        The rows.
     *
     * @return a string representation of {@code table}.
     *
     * @throws IllegalArgumentException if there are no rows in {@code rows}, or if any one of the rows in {@code rows} is
     *                                  incompatible with another (<i>i.e.</i> has a different number of columns, or has
     *                                  an incompatible data type for one of the columns).
     */
    public static String toString(Iterable<DataRow> rows) {
        return toString(toTable(rows));
    }

    /**
     * Get a "nice" string representation of a series of {@code DataRow}s. The representation takes the form of an ascii table,
     * described in {@link com.pb.sawdust.tabledata.write.AsciiTableWriter}. Because this will create a representation
     * of the rows as text, it is not recommended to use this method on large numbers of rows
     *
     * @param rows
     *        The rows.
     *
     * @param formats
     *        The formats to use for the row entries
     *
     * @return a string representation of {@code table}.
     *
     * @throws IllegalArgumentException if the number of formats in {@code formats} does not equal the number of columns
     *                                  in each row in {@code rows}, if there are no row in {@code rows}, or if  any one
     *                                  of the rows in {@code rows} is incompatible with another (<i>i.e.</i> has a different
     *                                  number of columns, or has an incompatible data type for one of the columns).
     */
    public static String toString(Iterable<DataRow> rows, TextFormat ... formats) {
        return toString(toTable(rows),formats);
    }
}
