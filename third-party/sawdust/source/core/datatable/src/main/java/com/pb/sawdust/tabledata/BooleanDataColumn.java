package com.pb.sawdust.tabledata;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Iterator;

/**
 * The {@code BooleanDataColumn} provides a wrapper class for {@code DataColumn}s of the type {@link com.pb.sawdust.tabledata.metadata.DataType#BOOLEAN}.
 * Being a wrapper class, its sole constructor takes a {@code DataColumn<Boolean>} instance as an argument, and all
 * of its {@code DataColumn} interface methods just delegate to that "wrapped" column's methods.  Because the
 * (generic) type of the {@code DataColumn<T>} interface implementation is explicitly stated in this class' signature,
 * it offers method calls without the need for casting.  As an example, look at the following class defnition:
 * <blockquote><pre>
 *     class Foo {
 *         public DataColumn<Boolean> getBooleanDataColumn() {
 *             ...
 *         }
 *     }
 * </pre></blockquote>
 * Because of erasure, any call to {@code getBooleanDataColumn()} will, from the compiler's perspective, return a
 * {@code DataColumn} instance, <i>not</i> a {@code DataColumn<Boolean>} instance.  That is, to get access to the
 * {@code <Boolean>} type parameter, a cast is required:
 * <blockquote><pre>
 *     Foo foo = new Foo();
 *     DataColumn<Boolean> column = (DataColumn<Boolean>) foo.getBooleanDataColumn();
 * </pre></blockquote>
 * This cast seems redundant and is rather annoying.  If it was not done, then any call to {@code DataColumn} methods
 * would not have access to the specified type parameter (without a cast):
 * <blockquote><pre>
 *     DataColumn column = foo.getBooleanDataColumn();
 *     Boolean[] columnData = (Boolean[]) column.getData();
 * </pre></blockquote>
 * Again, an annoying cast. However, by specifying the generic type in the class defninition, as done with this class,
 * such a cast is not necessary:
 * <blockquote><pre>
 *     class Foo {
 *         public BooleanDataColumn getBooleanDataColumn() {
 *             ...
 *         }
 *     }
 *     ...
 *     DataColumn<Boolean> column = foo.getBooleanDataColumn();
 *     Boolean[] columnData = column.getData();
 * </pre></blockquote>
 * Preferably, the second to last line above could be written as:
 * <blockquote><pre>
 *     BooleanDataColumn column = foo.getBooleanDataColumn();
 * </pre></blockquote>
 * Thus, the casts are removed with little programming hassle (the addition of a wrapper constructor).
 *
 * @author crf <br/>
 *         Started: May 17, 2008 11:52:10 AM
 */
public class BooleanDataColumn implements DataColumn<Boolean> {
    private final DataColumn<Boolean> wrappedColumn;

    public BooleanDataColumn(DataColumn<Boolean> wrappedColumn) {
        this.wrappedColumn = wrappedColumn;
    }

    public String getLabel() {
        return wrappedColumn.getLabel();
    }

    public DataType getType() {
        return DataType.BOOLEAN;
    }

    public int getRowCount() {
        return wrappedColumn.getRowCount();
    }

    public Boolean[] getData() {
        return wrappedColumn.getData();
    }

    /**
     * Get this column as a {@code boolean} array.
     *
     * @return this column as a {@code boolean} array.
     */
    public boolean[] getPrimitiveColumn() {
        return ArrayUtil.toPrimitive(getData());
    }

    public Boolean getCell(int rowIndex) {
        return wrappedColumn.getCell(rowIndex);
    }

    public Boolean getCellByKey(Object key) {
        return wrappedColumn.getCellByKey(key);
    }

    public Iterator<Boolean> iterator() {
        return wrappedColumn.iterator();
    }
}
