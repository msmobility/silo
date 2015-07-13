package com.pb.sawdust.tabledata;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Iterator;

/**
 * The {@code ShortDataColumn} provides a wrapper class for {@code DataColumn}s of the type {@link com.pb.sawdust.tabledata.metadata.DataType#SHORT}.
 * Being a wrapper class, its sole constructor takes a {@code DataColumn<Short>} instance as an argument, and all
 * of its {@code DataColumn} interface methods just delegate to that "wrapped" column's methods.  Because the
 * (generic) type of the {@code DataColumn<T>} interface implementation is explicitly stated in this class' signature,
 * it offers method calls without the need for casting.  As an example, look at the following class defnition:
 * <blockquote><pre>
 *     class Foo {
 *         public DataColumn<Short> getShortDataColumn() {
 *             ...
 *         }
 *     }
 * </pre></blockquote>
 * Because of erasure, any call to {@code getShortDataColumn()} will, from the compiler's perspective, return a
 * {@code DataColumn} instance, <i>not</i> a {@code DataColumn<Short>} instance.  That is, to get access to the
 * {@code <Short>} type parameter, a cast is required:
 * <blockquote><pre>
 *     Foo foo = new Foo();
 *     DataColumn<Short> column = (DataColumn<Short>) foo.getShortDataColumn();
 * </pre></blockquote>
 * This cast seems redundant and is rather annoying.  If it was not done, then any call to {@code DataColumn} methods
 * would not have access to the specified type parameter (without a cast):
 * <blockquote><pre>
 *     DataColumn column = foo.getShortDataColumn();
 *     Short[] columnData = (Short[]) column.getData();
 * </pre></blockquote>
 * Again, an annoying cast. However, by specifying the generic type in the class defninition, as done with this class,
 * such a cast is not necessary:
 * <blockquote><pre>
 *     class Foo {
 *         public ShortDataColumn getShortDataColumn() {
 *             ...
 *         }
 *     }
 *     ...
 *     DataColumn<Short> column = foo.getShortDataColumn();
 *     Short[] columnData = column.getData();
 * </pre></blockquote>
 * Preferably, the second to last line above could be written as:
 * <blockquote><pre>
 *     ShortDataColumn column = foo.getShortDataColumn();
 * </pre></blockquote>
 * Thus, the casts are removed with little programming hassle (the addition of a wrapper constructor).
 * @author crf <br/>
 *         Started: May 17, 2008 11:52:29 AM
 */
public class ShortDataColumn implements DataColumn<Short> {
    private final DataColumn<Short> wrappedColumn;

    public ShortDataColumn(DataColumn<Short> wrappedColumn) {
        this.wrappedColumn = wrappedColumn;
    }

    public String getLabel() {
        return wrappedColumn.getLabel();
    }

    public DataType getType() {
        return DataType.SHORT;
    }

    public int getRowCount() {
        return wrappedColumn.getRowCount();
    }

    public Short[] getData() {
        return wrappedColumn.getData();
    }

    /**
     * Get this column as a {@code short} array.
     *
     * @return this column as a {@code short} array.
     */
    public short[] getPrimitiveColumn() {
        return ArrayUtil.toPrimitive(getData());
    }

    public Short getCell(int rowIndex) {
        return wrappedColumn.getCell(rowIndex);
    }

    public Short getCellByKey(Object key) {
        return wrappedColumn.getCellByKey(key);
    }

    public Iterator<Short> iterator() {
        return wrappedColumn.iterator();
    }
}
