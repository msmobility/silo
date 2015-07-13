package com.pb.sawdust.tabledata;

/**
 * <p>
 * The {@code AbstractDataRow} class provides a skeletal version of {@code DataColumn} which implements as many
 * methods as possible, reducing the coding burden on the implementing user.  No internal data structures are
 * used in this class, as it only references interface methods.
 * </p>
 *
 * @param <T>
 *        The type of data held by this data column.
 * 
 * @author crf <br/>
 *         Started: May 12, 2008 10:26:05 AM
 */
public abstract class AbstractDataColumn<T> implements DataColumn<T> {
    
}
