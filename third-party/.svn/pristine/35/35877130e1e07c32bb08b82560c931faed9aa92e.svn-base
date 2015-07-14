package com.pb.sawdust.tabledata;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Iterator;

/**
 * The {@code LongDataColumn} provides a wrapper class for {@code DataColumn}s of the type {@link com.pb.sawdust.tabledata.metadata.DataType#LONG}.
 * Being a wrapper class, its sole constructor takes a {@code DataColumn<Long>} instance as an argument, and all
 * of its {@code DataColumn} interface methods just delegate to that "wrapped" column's methods.  Because the
 * (generic) type of the {@code DataColumn<T>} interface implementation is explicitly stated in this class' signature,
 * it offers method calls without the need for casting.  As an example, look at the following class defnition:
 * <blockquote><pre>
 *     class Foo {
 *         public DataColumn<Long> getLongDataColumn() {
 *             ...
 *         }
 *     }
 * </pre></blockquote>
 * Because of erasure, any call to {@code getLongDataColumn()} will, from the compiler's perspective, return a
 * {@code DataColumn} instance, <i>not</i> a {@code DataColumn<Long>} instance.  That is, to get access to the
 * {@code <Long>} type parameter, a cast is required:
 * <blockquote><pre>
 *     Foo foo = new Foo();
 *     DataColumn<Long> column = (DataColumn<Long>) foo.getLongDataColumn();
 * </pre></blockquote>
 * This cast seems redundant and is rather annoying.  If it was not done, then any call to {@code DataColumn} methods
 * would not have access to the specified type parameter (without a cast):
 * <blockquote><pre>
 *     DataColumn column = foo.getLongDataColumn();
 *     Long[] columnData = (Long[]) column.getData();
 * </pre></blockquote>
 * Again, an annoying cast. However, by specifying the generic type in the class defninition, as done with this class,
 * such a cast is not necessary:
 * <blockquote><pre>
 *     class Foo {
 *         public LongDataColumn getLongDataColumn() {
 *             ...
 *         }
 *     }
 *     ...
 *     DataColumn<Long> column = foo.getLongDataColumn();
 *     Long[] columnData = column.getData();
 * </pre></blockquote>
 * Preferably, the second to last line above could be written as:
 * <blockquote><pre>
 *     LongDataColumn column = foo.getLongDataColumn();
 * </pre></blockquote>
 * Thus, the casts are removed with little programming hassle (the addition of a wrapper constructor).
 * @author crf <br/>
 *         Started: May 17, 2008 11:52:41 AM
 */
public class LongDataColumn implements DataColumn<Long> {
    private final DataColumn<Long> wrappedColumn;

    public LongDataColumn(DataColumn<Long> wrappedColumn) {
        this.wrappedColumn = wrappedColumn;
    }

    public String getLabel() {
        return wrappedColumn.getLabel();
    }

    public DataType getType() {
        return DataType.LONG;
    }

    public int getRowCount() {
        return wrappedColumn.getRowCount();
    }

    public Long[] getData() {
        return wrappedColumn.getData();
    }

    /**
     * Get this column as a {@code long} array.
     *
     * @return this column as a {@code long} array.
     */
    public long[] getPrimitiveColumn() {
        return ArrayUtil.toPrimitive(getData());
    }

    public Long getCell(int rowIndex) {
        return wrappedColumn.getCell(rowIndex);
    }

    public Long getCellByKey(Object key) {
        return wrappedColumn.getCellByKey(key);
    }

    public Iterator<Long> iterator() {
        return wrappedColumn.iterator();
    }
}
