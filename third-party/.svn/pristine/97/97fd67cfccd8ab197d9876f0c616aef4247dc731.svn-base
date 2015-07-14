package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.array.ArrayUtil;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.*;

/**
 * The {@code AbstractSubTableReader} is a skeletal {@code SubTableReader} easing the programming burden on sub-table
 * reader implementations.
 *  
 * @author crf <br/>
 *         Started: Nov 2, 2008 1:14:15 PM
 */
public abstract class AbstractSubTableReader extends AbstractTableReader implements SubTableReader {
    private InjectiveMap<String,Integer> allColumnsNameMap = null;
    private DataType[] allColumnTypes = null;
    private Filter<Integer> rowIndexFilter = null;
    private Filter<Object[]> rowDataFilter = null;

    /**
     * Array of (0-based) indices of the columns to keep. Will be {@code null} if all columns are to be kept.
     */
    protected int[] columnsToKeep = null;

    /**
     * Get all of the column names in the table (as opposed to just those for the columns being kept). User defined
     * data types (via {@code setColumnNames(String...)} will override these values; nonetheless, this method should
     * always return valid names for each column. Changes to the columns to keep will cause the reader to use the
     * names returned from this method until a user overrides them; that is, these names form the default names for
     * the table.
     *
     * @return the names of all of the columns in the table, in the order that they occur.
     */
    abstract protected String[] getAllColumnNames();

    /**
     * Get all of the column types in the table (as opposed to just those for the columns being kept). User defined
     * data types (via {@code setColumnTypes(DataType...)} will override these values; nonetheless, this method should
     * always return valid types for each column. Changes to the columns to keep will cause the reader to use the
     * types returned from this method until a user overrides them; that is, these types form the default types for
     * the table.
     *
     * @return the types of all of the columns in the table, in the order that they occur.
     */
    abstract protected DataType[] getAllColumnTypes();

    /**
     * Get an {@code Iterator} which iterates over all the rows of data in the table, in their correct order. The arrays of
     * data should contain all of the columns (not just those from the columns to be kept) and each data element should
     * be of the correct type corresponding to its column's data type. Data in the array corresponding to columns which
     * are not going to be kept will be ignored, so correctly typing those elements is unnecessary. Because column
     * types may be set externally, the typing should be performed using a combination of {@link #getColumnTypes()} and
     * {@link #columnsToKeep}, not {@link #getAllColumnTypes()}.
     *
     * @return an iterator which cycles over all of the rows in the table.
     */
    abstract protected Iterator<Object[]> getDataIterator();

    /**
     * Constructor specifying the table name.
     *
     * @param tableName
     *        The name to use for the table.
     */
    public AbstractSubTableReader(String tableName) {
        super(tableName);
    }

    /**
     * Set all of the column names in the source table. This is useful to use if column names need to be referenced or
     * set and {@code getAllColumnNames} is too expensive or does not return the desired names.
     *
     * @param allColumnNames
     *        An arry of all of the column names in the source table.
     */
    public void setAllColumnNames(String ... allColumnNames) {
        setAllColumnNamesMap(allColumnNames);
    }

    private void setAllColumnNamesMap(String[] allColumnNames) {
        allColumnsNameMap  = new InjectiveHashMap<String,Integer>();
        if (allColumnNames == null)
            throw new IllegalStateException("Column name array must not be null.");
        for (int i : range(allColumnNames.length))
            allColumnsNameMap.put(allColumnNames[i],i);
    }

    private void loadAllColumnsNameMap() {
        if (allColumnsNameMap == null)
            setAllColumnNamesMap(getAllColumnNames());
    }

    private void loadAllColumnTypes() {
        if (allColumnTypes == null) {
            allColumnTypes = getAllColumnTypes();
            if (allColumnTypes == null)
                throw new IllegalStateException("Default column types must not be null.");
        }
    }

    public void setColumnsToKeep(int ... columns) {
        if (columns == null)
            throw new IllegalArgumentException("Columns to keep must be an array of ints, not null.");
        Set<Integer> columnsToKeep = new HashSet<Integer>();
        for (int column : columns)
            if (columnsToKeep.contains(column))
                throw new IllegalArgumentException("Columns must be unique: " + column);
            else
                columnsToKeep.add(column);
        this.columnsToKeep = columns;
    }

    public void setColumnsToKeep(String ... columns) {
        if (columns == null)
            throw new IllegalArgumentException("Columns to keep must be an array of Strings, not null.");
        loadAllColumnsNameMap();
        Set<Integer> columnsToKeep = new LinkedHashSet<Integer>();
        for (String column : columns) {
            Integer columnIndex = allColumnsNameMap.get(column);
            if (columnIndex == null)
                throw new IllegalArgumentException("Column name not found: " + column);
            if (columnsToKeep.contains(columnIndex))
                throw new IllegalArgumentException("Columns must be unique: " + column);
            columnsToKeep.add(columnIndex);
        }
        this.columnsToKeep = ArrayUtil.toPrimitive(columnsToKeep.toArray(new Integer[columnsToKeep.size()]));
        setColumnNames(columns);
    }

    public void resetColumnsToKeep() {
        columnsToKeep = null;
        setColumnNames(getAllColumnNames());
    }

    public void setRowsToKeep(Filter<Integer> rowFilter) {
        rowIndexFilter = rowFilter;
    }

    public void setRowFilter(Filter<Object[]> rowFilter) {
        rowDataFilter = rowFilter;
    }

    public void resetRowsToKeep() {
        rowIndexFilter = null;
        rowDataFilter = null;
    }

    /**
     * Get the filter used to determine which rows (by index) to skip.
     *
     * @return the row index filter, or {@code null} if there is none.
     */
    protected Filter<Integer> getRowsToKeepFilter() {
        return rowIndexFilter;
    }

    /**
     * Get the filter used to determine which rows (by data) to skip.
     *
     * @return the row data filter, or {@code null} if there is none.
     */
    protected Filter<Object[]> getRowFilter() {
        return rowDataFilter;
    }

    public void setColumnNames(String ... columnNames) {
        if (columnsToKeep != null && columnsToKeep.length != columnNames.length)
            throw new IllegalArgumentException("Number of column names must be equal to number of columns specified in columns to keep; found " + columnNames.length + ", expected " + columnsToKeep.length);
        super.setColumnNames(columnNames);
    }

    public String[] getColumnNames() {
        String[] names = super.getColumnNames();
        if (names == null || (columnsToKeep != null && columnsToKeep.length != names.length)) {
            loadAllColumnsNameMap();
            if (columnsToKeep == null) {
                names = new String[allColumnsNameMap.size()];
                for (int i = 0; i < names.length; i++)
                    names[i] = allColumnsNameMap.getKey(i);
            } else {
                names = new String[columnsToKeep.length];
                int counter = 0;
                for (int index : columnsToKeep)
                    names[counter++] = allColumnsNameMap.getKey(index);
            }
            setColumnNames(names);
        }
        return names;
    }

    public void setColumnTypes(DataType ... columnTypes) {
        if (columnsToKeep != null && columnsToKeep.length != columnTypes.length)
            throw new IllegalArgumentException("Number of column types must be equal to number of columns specified in columns to keep; found " + columnTypes.length + ", expected " + columnsToKeep.length);
        super.setColumnTypes(columnTypes);
    }

    public DataType[] getColumnTypes() {
        DataType[] types = super.getColumnTypes();
        if (types == null || (columnsToKeep != null && columnsToKeep.length != types.length)) {
            loadAllColumnTypes();
            types = columnsToKeep == null ? allColumnTypes : ArrayUtil.buildArray(allColumnTypes,this.columnsToKeep);
            setColumnTypes(types);
        }
        return types;
    }

    private interface DataAdder {
        void addData(List<Object[]> data, Object[] rowData, int rowIndex);
    }

    private Object[][] getData(DataAdder da) {
        //ensure that types and names are updated/valid
        getColumnNames();
        getColumnTypes();
        int rowIndex = 0;
        List<Object[]> data = new LinkedList<Object[]>();
        Iterator<Object[]> iterator = getDataIterator();
        while(iterator.hasNext())
            da.addData(data,iterator.next(),rowIndex++);
        return data.toArray(new Object[data.size()][]);
    }

    public Object[][] getData() {
        DataAdder da;
        final Filter<Object[]> rowDataFilter = this.rowDataFilter;
        final Filter<Integer> rowIndexFilter = this.rowIndexFilter;
        if (rowDataFilter == null) {
            if (rowIndexFilter == null) {
                da = new DataAdder() {
                    public void addData(List<Object[]> data, Object[] rowData, int rowIndex) {
                        data.add(columnsToKeep == null ? rowData : ArrayUtil.buildArray(rowData,columnsToKeep));
                    }
                };
            } else {
                da = new DataAdder() {
                    public void addData(List<Object[]> data, Object[] rowData, int rowIndex) {
                        if (rowIndexFilter.filter(rowIndex))
                            data.add(columnsToKeep == null ? rowData : ArrayUtil.buildArray(rowData,columnsToKeep));
                    }
                };
            }
        } else {
            if (rowIndexFilter == null) {
                da = new DataAdder() {
                    public void addData(List<Object[]> data, Object[] rowData, int rowIndex) {
                        if (rowDataFilter.filter(rowData))
                            data.add(columnsToKeep == null ? rowData : ArrayUtil.buildArray(rowData,columnsToKeep));
                    }
                };
            } else {
                da = new DataAdder() {
                    public void addData(List<Object[]> data, Object[] rowData, int rowIndex) {
                        if (rowIndexFilter.filter(rowIndex) && rowDataFilter.filter(rowData))
                            data.add(columnsToKeep == null ? rowData : ArrayUtil.buildArray(rowData,columnsToKeep));
                    }
                };
            }
        }
        return getData(da);
    }
}
