package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.io.IterableReader;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.Typer;
import com.pb.sawdust.tabledata.TableDataException;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.FilterChain;
import com.pb.sawdust.util.collections.iterators.FilteredIterator;

import java.util.*;

/**
 * The {@code TextFileTableReader} class is a base class for reading table data from text files. This class allows the
 * specification of whether or not the file has a header; it is assumed that the header in a text file contains the
 * (default) column names.  If the file has no header, then default column names will be specified as:
 * <pre>
 *     <code>
 *         DEFAULT_COLUMN_NAME_PREFIX + index
 *     </code>
 * </pre>
 * where {@code index} is the (0-based) index of the column. The default is to assume that the file has a header (even
 * if the column names are specified).
 * <p>
 * Data types are determined through a {@link com.pb.sawdust.tabledata.metadata.Typer} call. How deep the type
 * checker descends into the data is configurable by the user. Using this type information, the text data is internally
 * converted to the appropriate data types (via a {@code (Typer).coerceToType(String,DataType)} call, so user-specified 
 * types should process validly). These types The default {@code Typer} is a {@code Typer.StandardTyper}, though this
 * can be changed by calling {@code setTyper(Typer)}.
 *
 * @param <S>
 *        The type that each row entry will be read as (usually {@code String} or {@code String[]}.
 *
 * @author crf <br/>
 *         Started: Jul 2, 2008 8:17:30 AM
 */
public abstract class TextFileTableReader<S> extends FileTableReader {
    /**
     * The prefix for the default column names. Default names will be this string plus the (0-based) index of the column.
     */
    public static final String DEFAULT_COLUMN_NAME_PREFIX = "column";

    private int maxDepth = 0;
    private DataType[] allColumnTypes = null;
    private String[] allColumnNames = null;
    private final List<String[]> stringData = new LinkedList<String[]>();
    private Typer typer = new Typer.StandardTyper();
    private FilterChain<S> lineFilter = null;

    /**
     * Indicates whether the file has a header or not.
     */
    protected boolean fileHasHeader = true;

    /**
     * Constructor specifying the file and table name for the table data.
     *
     * @param tableFilePath
     *        The path to the text table data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code tableFilePath} was not found.
     */
    public TextFileTableReader(String tableFilePath, String tableName) {
        super(tableFilePath,tableName);
    }

    /**
     * Constructor specifying the file the table data. The file name (excluding directories) will be used as the
     * table name.
     *
     * @param tableFilePath
     *        The path to the table data file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code tableFilePath} was not found.
     */
    public TextFileTableReader(String tableFilePath) {
        super(tableFilePath);
    }

    /**
     * Get an iterable reader which will iterate over the rows in the table file. The actually object type
     * which is returned by the iterator is determined by the paramatrization of this class.
     *
     * @return a reader which iterates over the rows in the table file.
     */
    protected abstract IterableReader<S> getReader();

    /**
     * Get an array of row data from the input row. The input object type is determined by the paramatrization of this
     * class.
     *
     * @param row
     *        The row returned from each cycle of the reader returned by {@code getReader()}.
     *
     * @return an array of row data.
     */
    protected abstract String[] getRowData(S row);

    /**
     * Set whether this reader's file has a header or not. The default value is {@code true}.
     *
     * @param fileHasHeader
     *        {@code true} if the file has a header, {@code false} if not.
     */
    public void setFileHasHeader(boolean fileHasHeader) {
        this.fileHasHeader = fileHasHeader;
    }

    /**
     * Set the maximium depth the {@code Typer} should descend in the data to determine each column's type.
     *
     * @param maxDepth
     *        The type checkers maximum data depth.
     *
     * @throws IllegalArgumentException if {@code maxDepth < 1}.
     */
    public void setMaxTypingDepth(int maxDepth) {
        //if column types have already been set, this'll never be used, unless columnsToKeep changes.
        if (maxDepth < 1)
            throw new IllegalArgumentException("Max typing depth must be greater than 0.");
        this.maxDepth = maxDepth;
    }

    /**
     * Set the typer used to determine the data types of the text data.
     *
     * @param typer
     *        The typer to use to infer data types.
     */
    public void setTyper(Typer typer) {
        this.typer = typer;
    }

    public String[] getAllColumnNames() {
        if (allColumnNames == null) {
            String[] data;
            IterableReader<S> reader = getFilteredReader();
            try {
                data = getRowData(reader.iterator().next());
            } finally {
                reader.close();
            }
            if (fileHasHeader)
                allColumnNames = data;
            else
                allColumnNames = formDefaultColumnNames(data.length);
        }
        return allColumnNames;
    }

    private String[] formDefaultColumnNames(int columnCount) {
        String[] names = new String[columnCount];
        for (int i : range(columnCount))
            names[i] = DEFAULT_COLUMN_NAME_PREFIX + i;
        return names;
    }


    /**
     * Add a line filter to this reader. A line filter will filter lines before the data is parsed and typed.
     *
     * @param filter
     *        A filter which returns {@code true} if the line is to be parsed and added to the table, {@code false} otherwise.
     */
    public void addLineFilter(Filter<S> filter) {
        if (lineFilter == null)
            lineFilter = new FilterChain<S>();
        lineFilter.addFilter(filter);
    }

    /**
     * Clear any line filters held by this reader. This will not clear any row index or data filters.
     */
    public void clearLineFilters() {
        lineFilter = null;
    }

    /**
     * Get the line filter for this table reader.  The returned filter will incorporate all filters added through
     * {@code addLineFilter(Filter<S>)}. In subclasses, this method should be used in combination with {@code getReader()}
     * to form a reader which filters out lines from the file appropriately.
     *
     * @return the line filter used to filter out lines from the source table file.
     */
    protected Filter<S> getLineFilter() {
        return lineFilter;
    }

    private IterableReader<S> getFilteredReader() {
        return new IterableReader<S>() {
            private final IterableReader<S> reader = getReader();

            public void setCloseAtIterationEnd(boolean closeAtIterationEnd) {
                reader.setCloseAtIterationEnd(closeAtIterationEnd);
            }

            public Iterator<S> iterator() {
                return lineFilter == null ? reader.iterator() : new FilteredIterator<S>(reader.iterator(),lineFilter);
            }

            public void close() {
                reader.close();
            }
        };
    }

    public DataType[] getAllColumnTypes() {
        if (allColumnTypes == null) {
            boolean first = fileHasHeader;
            boolean limit = maxDepth != 0;
            int counter = 0;
            IterableReader<S> reader = getFilteredReader();
            try {
                for (S s : reader) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    stringData.add(getRowData(s));
                    if (limit && ++counter >= maxDepth)
                        break;
                }
            } finally {
                reader.close();
            }
            if (stringData.size() == 0)
                throw new TableDataException("No data in table, cannot infer data types");
            allColumnTypes = typer.inferTypesOnStringData(stringData.toArray(new String[stringData.size()][]));
        }
        return allColumnTypes;
    }

    public Iterator<Object[]> getDataIterator() {
        final DataType[] columnTypes = getColumnTypes();

        final List<String[]> stringData = this.stringData;
        final boolean fileHasHeader = this.fileHasHeader;
        if (maxDepth == 0 && stringData.size() > 0) {
            return new Iterator<Object[]>() {
                private final Iterator<String[]> stringIterator = stringData.iterator();

                public boolean hasNext() {
                    return stringIterator.hasNext();
                }
    
                public Object[] next() {
                    return castStringData(stringIterator.next(),columnTypes);
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return new Iterator<Object[]>() {
            private final Iterator<String[]> stringIterator = stringData.iterator();
            private final Iterator<S> lineIterator = getFilteredReader().iterator();
            private int counter = 0;
            private int alreadyRead = stringData.size();
            {
                if (fileHasHeader)
                    lineIterator.next();
            }

            public boolean hasNext() {
                return lineIterator.hasNext();
            }

            public Object[] next() {
                if (counter++ < alreadyRead) {
                    lineIterator.next();
                    return castStringData(stringIterator.next(),columnTypes);
                } else {
                    return castStringData(getRowData(lineIterator.next()),columnTypes);
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Object[] castStringData(String[] data, DataType[] types) {
        Object[] castData = new Object[data.length];
        if (columnsToKeep == null) {
            for (int i=0; i < data.length; i++) {
                castData[i] = typer.coerceToType(data[i],types[i]);
            }
        } else {
            for (int i=0; i < columnsToKeep.length; i++) {
                int column = columnsToKeep[i];
                castData[column] = typer.coerceToType(data[column],types[i]);
            }
        }
        return castData;
    }

}
