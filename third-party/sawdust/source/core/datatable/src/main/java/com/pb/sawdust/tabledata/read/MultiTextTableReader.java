package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.io.IterableReader;
import com.pb.sawdust.util.collections.iterators.ParallelIterator;
import com.pb.sawdust.util.collections.iterators.FilteredIterator;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.Typer;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;

/**
 * The {@code MultiTextTableReader} class provides a {@code TextFileTableReader} implementation wherein multiple data
 * tables can be read from a single file. It essentially runs multiple {@code TextFileTableReader} instances through
 * the same file. The advantage of this class, which is especially important if the input file is large, is that it
 * only reads the file once. If the readers contained by this class are supposed to act on distinct parts of the file
 * (<i>i.e.</i> some lines belong to one table, the others to another), then the readers should have line filters set
 * to ignore the non-relevant parts of the file.
 * <p>
 * When reading in data tables from a file, this class allocates the processing of file lines to each reader in parallel.
 * Because of this, one reader's line processing may be quicker than anothers, allowing it to "get ahead" of the other(s),
 * which could cause some memory issues. In the worst case scenario, the entire file might be held in memory (the "fast"
 * reader has finished processing and the "slow" reader is still working on the first line). To offer some control over
 * this, this class allows the specification of a maximum file line queue size - that is, the maximum number of file
 * lines that can be held in memory at any one time.
 * <p>
 * There are a couple of restrictions on the readers contained by an instance of this class. Specifically, each reader
 * must be set up to read in the same file, and each reader must produce a data table with a different name. Since
 * each reader must read the same file, this implies that one cannot allow more than one reader to use a default data
 * table name (which is based one the file name).
 *
 * @author crf <br/>
 *         Started: Nov 3, 2008 9:53:09 PM
 */
public class MultiTextTableReader<S,R extends TextFileTableReader<S>> {
    private ParallelIterator<S> parallelIterator;
    private Map<String,SingleTextTableReader> readerMap;
    private final IterableReader<S> iteratingReader;

    /**
     * Constructor specifying the readers and the maximum number of file lines that can be held in memory.
     *
     * @param readers
     *        The readers which will process the input file(s).
     *
     * @param parallelIteratorQueueSize
     *        The maximum number of file lines that can be held in memory at any one time.
     *
     * @throws IllegalArgumentException if the number of readers is less than two
     * @throws IllegalStateException if any two readers create tables with the same name, or if the readers do not all
     *                               read the same file.
     */
    public MultiTextTableReader(R[] readers, final int parallelIteratorQueueSize) {
        if (readers.length < 2)
            throw new IllegalArgumentException("MultiTextTableReader requires at least two base readers.");
        File tableFile = readers[0].tableFile;
        readerMap = new HashMap<String,SingleTextTableReader>();
        for (R reader : readers) {
            String tableName = reader.getTableName();
            if (readerMap.containsKey(tableName))
                throw new IllegalStateException("Table name can only be used once: " + tableName);
            if (!reader.tableFile.equals(tableFile))
                throw new IllegalStateException("All readers must use specified table file, found\n\t" + reader.tableFile + "\nexpected\n\t" + tableFile);
            readerMap.put(tableName,new SingleTextTableReader(reader));
        }
        iteratingReader = readers[0].getReader();
        iteratingReader.setCloseAtIterationEnd(false);
        parallelIterator = new ParallelIterator<S>(iteratingReader.iterator(),readers.length) {
            protected Queue<S> getIteratorQueue() {
                return new LinkedBlockingQueue<S>(parallelIteratorQueueSize);
            }
        };
    }

    /**
     * Constructor specifying the readers. The maximum number of file lines allowed in memory at any one time is set to
     * {@code 50,000}.
     *
     * @param readers
     *        The readers which will process the input file(s).
     *
     * @throws IllegalArgumentException if the number of readers is less than two
     * @throws IllegalStateException if any two readers create tables with the same name, or if the readers do not all
     *                               read the same file.
     */
    public MultiTextTableReader(R[] readers) {
        this(readers,50000);
    }

    /**
     * Get the reader contained by this instance that will produce a data table with a specified name.
     *
     * @param tableName
     *        The table name in question.
     *
     * @return the reader that creates a data table named {@code tableName}.
     *
     * @throws IllegalArgumentException if no reader in this instance creates a data table named {@code tableName}.
     */
    public TextFileTableReader<S> getReader(String tableName) {
        if (!readerMap.containsKey(tableName))
            throw new IllegalArgumentException("Table reader not found for table name: " + tableName);
        return readerMap.get(tableName);
    }

    /**
     * Get a collection of the readers contained by this instance. The returned readers will be wrapped in such a way
     * that they are not modifiable (they can be used to read data, but their internal state cannot be changed).
     *
     * @return the readers contained by this instance.
     */
    public Collection<? extends TextFileTableReader<S>> getReaders() {
        return Collections.unmodifiableCollection(readerMap.values());
    }

    private void loadData() {
        Set<Thread> threads = new HashSet<Thread>();
        for (final SingleTextTableReader reader : readerMap.values()) {
                threads.add(
                    new Thread(
                        new Runnable() {
                            public void run() {
                                reader.loadDataOnce();
                            }
                        }
                    )
                );
        }
        for (Thread thread : threads)
            thread.start();
        try {
            for (Thread thread : threads)
                thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeWrappingException(e);
        } finally {
            iteratingReader.close();
        }
    }

    private class SingleTextTableReader extends TextFileTableReader<S> {
        private final Filter<S> filter;
        private final TextFileTableReader<S> wrappedReader;
        private Object[][] data = null;

        private SingleTextTableReader(R wrappedReader) {
            super(wrappedReader.tableFile.getPath(),wrappedReader.getTableName());
            this.filter = wrappedReader.getLineFilter();
            this.wrappedReader = wrappedReader;
            this.columnsToKeep = wrappedReader.columnsToKeep;
            this.fileHasHeader = wrappedReader.fileHasHeader;
            Filter<Object[]> rowDataFilter = wrappedReader.getRowFilter();
            if (rowDataFilter != null)
                super.setRowFilter(wrappedReader.getRowFilter());
            Filter<Integer> rowIndexFilter = wrappedReader.getRowsToKeepFilter();
            if (rowIndexFilter != null)
                super.setRowsToKeep(rowIndexFilter);
        }

        public String[] getAllColumnNames() {
            return wrappedReader.getAllColumnNames();
        }

        public DataType[] getAllColumnTypes() {
            return wrappedReader.getAllColumnTypes();
        }

        public DataType[] getColumnTypes() {
            return wrappedReader.getColumnTypes();
        }

        public String[] getColumnNames() {
            return wrappedReader.getColumnNames();
        }

        public Object[][] getData() {
            synchronized(MultiTextTableReader.this) {
                if (data == null) {
                    loadData();
                }
            }
            return data;
        }

        private void loadDataOnce() {
            data = super.getData();
        }

        public String getTableName() {
            return wrappedReader.getTableName();
        }

        protected String[] getRowData(S row) {
            return wrappedReader.getRowData(row);
        }

        public void resetColumnsToKeep() {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void resetRowsToKeep() {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setAllColumnNames(String ... allColumnNames) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setColumnNames(String ... columnNames) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setColumnsToKeep(int ... columns) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setColumnsToKeep(String ... columns) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setColumnTypes(DataType ... columnTypes) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setFileHasHeader(boolean fileHasHeader) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setMaxTypingDepth(int maxDepth) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setRowFilter(Filter<Object[]> rowFilter) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setRowsToKeep(Filter<Integer> rowFilter) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        public void setTyper(Typer typer) {
            throw new UnsupportedOperationException("Cannot change state of reader from a MultiTextTableReader.");
        }

        protected IterableReader<S> getReader() {
            return new IterableReader<S>() {

                public void setCloseAtIterationEnd(boolean closeAtIterationEnd) {
                    //ignore
                }

                public Iterator<S> iterator() {
                    return filter == null ? parallelIterator.getIterator() :
                            new FilteredIterator<S>(parallelIterator.getIterator(),filter);
                }

                public void close() {
                    //not needed - will be closed by main iterator
                }
            };
        }
    }
}
