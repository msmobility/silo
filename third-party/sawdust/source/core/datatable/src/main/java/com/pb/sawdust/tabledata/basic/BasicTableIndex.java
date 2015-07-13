package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractTableIndex;
import com.pb.sawdust.tabledata.DataTable;

import java.util.*;

/**
 * The {@code BasicTable} class provides a simple implementation of the {@code TableIndex} interface.
 *
 * @param <I>
 *        The type of the table index components.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 12:23:49 AM
 */
public class BasicTableIndex<I> extends AbstractTableIndex<I,List<I>> {

    /**
     * Constructor specifying the table and columns on which to build the index.
     *
     * @param table
     *        The table the index will be based on.
     *
     * @param indexColumnLabels
     *        The labels of the table columns the index will be built on, in the order that they are to be referenced
     *        by the index' methods.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if any of {@code indexColumnLabels} are not contained in {@code table}.
     */
    public BasicTableIndex(DataTable table, String ... indexColumnLabels) {
        super(table,indexColumnLabels);
        buildIndex();
    }

    /**
     * Constructor specifying the table and columns on which to build the index.
     *
     * @param table
     *        The table the index will be based on.
     *
     * @param indexColumnLabels
     *        The labels of the table columns the index will be built on, in the order that they are to be referenced
     *        by the index' methods.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if any of {@code indexColumnLabels} are not contained in {@code table}.
     */
    public BasicTableIndex(DataTable table, List<String> indexColumnLabels) {
        this(table,indexColumnLabels.toArray(new String[indexColumnLabels.size()]));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    protected List<I> buildIndexKey(I ... indexValues) {
        return Arrays.asList(indexValues);
    }
}
