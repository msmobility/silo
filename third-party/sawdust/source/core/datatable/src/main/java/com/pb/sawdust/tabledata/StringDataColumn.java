package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Iterator;

/**
 * The {@code StringDataColumn} provides a wrapper class for {@code DataColumn}s of the type {@link com.pb.sawdust.tabledata.metadata.DataType#STRING}.
 * Being a wrapper class, its sole constructor takes a {@code DataColumn<String>} instance as an argument, and all
 * of its {@code DataColumn} interface methods just delegate to that "wrapped" column's methods.  Because the
 * (generic) type of the {@code DataColumn<T>} interface implementation is explicitly stated in this class' signature,
 * it offers method calls without the need for casting.  As an example, look at the following class defnition:
 * <blockquote><pre>
 *     class Foo {
 *         public DataColumn<String> getStringDataColumn() {
 *             ...
 *         }
 *     }
 * </pre></blockquote>
 * Because of erasure, any call to {@code getStringDataColumn()} will, from the compiler's perspective, return a
 * {@code DataColumn} instance, <i>not</i> a {@code DataColumn<String>} instance.  That is, to get access to the
 * {@code <String>} type parameter, a cast is required:
 * <blockquote><pre>
 *     Foo foo = new Foo();
 *     DataColumn<String> column = (DataColumn<String>) foo.getStringDataColumn();
 * </pre></blockquote>
 * This cast seems redundant and is rather annoying.  If it was not done, then any call to {@code DataColumn} methods
 * would not have access to the specified type parameter (without a cast):
 * <blockquote><pre>
 *     DataColumn column = foo.getStringDataColumn();
 *     String[] columnData = (String[]) column.getData();
 * </pre></blockquote>
 * Again, an annoying cast. However, by specifying the generic type in the class defninition, as done with this class,
 * such a cast is not necessary:
 * <blockquote><pre>
 *     class Foo {
 *         public StringDataColumn getStringDataColumn() {
 *             ...
 *         }
 *     }
 *     ...
 *     DataColumn<String> column = foo.getStringDataColumn();
 *     String[] columnData = column.getData();
 * </pre></blockquote>
 * Preferably, the second to last line above could be written as:
 * <blockquote><pre>
 *     StringDataColumn column = foo.getStringDataColumn();
 * </pre></blockquote>
 * Thus, the casts are removed with little programming hassle (the addition of a wrapper constructor).
 * @author crf <br/>
 *         Started: May 17, 2008 11:53:20 AM
 */
public class StringDataColumn implements DataColumn<String> {
    private final DataColumn<String> wrappedColumn;

    public StringDataColumn(DataColumn<String> wrappedColumn) {
        this.wrappedColumn = wrappedColumn;
    }

    public String getLabel() {
        return wrappedColumn.getLabel();
    }

    public DataType getType() {
        return DataType.STRING;
    }

    public int getRowCount() {
        return wrappedColumn.getRowCount();
    }

    public String[] getData() {
        return wrappedColumn.getData();
    }

    public String getCell(int rowIndex) {
        return wrappedColumn.getCell(rowIndex);
    }

    public String getCellByKey(Object key) {
        return wrappedColumn.getCellByKey(key);
    }

    public Iterator<String> iterator() {
        return wrappedColumn.iterator();
    }
}
