package com.pb.sawdust.tabledata;

import java.util.Set;

/**
 * <p>
 * A {@code TableIndex} object holds a mapping from the value(s) of one or more columns to the rows in a data table
 * which contain those values. No uniqueness constraints are enforced, so a set of column values may map to more than
 * one data row. For a unique mapping using one column (which is more efficient), see {@link TableKey}. An
 * index is made up of a series of one or more values, each corresponding to a column in a data table. The order
 * that those columns are specified in the index determines the order that they must be referenced when using the
 * index; there is no requirement that the index columns be in the same order that they exist in the parent table.
 * </p>
 * <p>
 * The type of the index ({@code <I>}) may be specified to indicate that all of the index columns are of the same type, offering
 * some compile-time type safety. In the case where the index values are not of the same type, {@code I} = {@code Object}
 * may be used or, alternatively, the type parameter may be omitted (which, because of erasure, is equivalent). 
 * </p>
 * <p>
 * The index's reference to rows is made through row numbers, which are ordinal based on the row ordering at the time
 * that the index is built (these row numbers correspond to those used in {@link DataTable#getRow(int)}).  If row
 * ordering changes after the index is built, then the index must be rebuilt to correctly reflect the changes to the
 * data table (and correctly access its elements).
 * </p>
 * <p>
 * There is no requirement that an index need be built (initialized) upon object construction.  That is, depending on
 * the implementation, it may be the user's duty to call {@link #buildIndex()} in order to initialize the index.
 * </p>
 * <p>
 * There is nothing explicitly connecting this interface with a given data table, though the column labels and the
 * index mapping itself imply a connection between the index and its parent table. This "disconnection" allows the
 * index to be shifted across parallel data table instances holding the same data (but used for efficiency reasons),
 * as well as lessening the restrictions on implementations.
 * </p>
 *
 * @param <I>
 *        The type of the table index components.
 *
 * @author crf <br>
 *         Started: May 7, 2008 5:39:13 PM <br>
 */
public interface TableIndex<I> {

    /**
     * Get the column labels used in the index, in the order that they are to be referenced.
     *
     * @return this index's ordered column labels.
     */
    String[] getIndexColumnLabels();

    /**
     * Get the row numbers corresponding to a given set of index values. The index column values must be provided in
     * the same order as returned by {@link #getIndexColumnLabels()}. If no rows are found corresponding to the
     * index values, an exception is thrown (as opposed to returning an empty set, which could mask errors). The row
     * numbers returned will correspond to the order of the source data table when the index was last built.
     *
     * @param indexValues
     *        The column values specifying the index entry.
     *
     * @return the row numbers corresponding to the index column values.
     *
     * @throws IllegalArgumentException if the number of index values does not match the number of columns in this index.
     *
     * @throws TableDataException if no rows corresponding to {@code indexValues} exist, or if this index has not been
     *                            built yet.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    Set<Integer> getRowNumbers(I ... indexValues);

    /**
     * Get all of the available unique column values in this index.  This method returns an array of arrays, each
     * containing a unique set of column index values, in the same order as returned by {@link #getIndexColumnLabels()}.
     *
     * @return an array of all of the unique index column values found in this index.
     *
     * @throws TableDataException if this index has not been built yet.
     */
    I[][] getUniqueValues();

    /**
     * Get the number of rows corresponding to a given set of index values. The index column values must be provided in
     * the same order as returned by {@link #getIndexColumnLabels()}. If no rows correspond to the provided index
     * values, then this method will return {@code 0}.
     *
     * @param indexValues
     *        The column values specifying the index entry.
     *
     * @return the number of rows corresponding to {@code indexValues}.
     *
     * @throws TableDataException if this index has not been built yet.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    int getIndexCount(I ... indexValues);

    /**
     * Build the index. Calling this method should update both the index's row number mapping as well as the array
     * returned by {@link #getUniqueValues()}. This method need not be called upon object creation; that is, there is
     * no expectation that implementing classes should build the index when constructed. There is also no expectation
     * that an index should be rebuilt after every data table update; an index is only consistent with its
     * corresponding data table so long as that data table has not be altered (and the index not rebuilt).
     */
    void buildIndex();
}
