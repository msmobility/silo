package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.*;

import java.util.*;

/**
 * The {@code OrderedDataTable} ...
 *
 * @author crf <br/>
 *         Started Nov 4, 2010 1:11:20 AM
 */
public class OrderedDataTable extends ReadOnlyWrappedDataTable {
    //todo: make this a fixed size and allow edits to data? - I think it is better to not
    private final int[] order;
    private final int start;
    private final int end;

    private static <T extends Number> int[] getOrderNumber(DataTable table, int columnIndex, SetList<T> referenceCollection) {
        boolean coersion = table.willCoerceData();
        table.setDataCoersion(true);
        try {
            switch (table.getColumnDataType(columnIndex)) {
                case BYTE :
                case SHORT :
                case INT :
                case LONG : { //cast up to long for integer types
                    SetList<Long> longReference = new LinkedSetList<Long>();
                    for (Number n : referenceCollection)
                        longReference.add(n == null ? null : n.longValue());
                    return CollectionOrder.getOrder(table.getLongColumn(columnIndex), longReference);
                }
                case FLOAT :
                case DOUBLE : { //cast up to double for decimal types
                    SetList<Double> doubleReference = new LinkedSetList<Double>();
                    for (Number n : referenceCollection)
                        doubleReference.add(n == null ? null : n.doubleValue());
                    return CollectionOrder.getOrder(table.getDoubleColumn(columnIndex),doubleReference);
                }
            }
        } catch (ClassCastException e) { //capturing "bad" number is too much of a pain
            throw new IllegalArgumentException("Numeric data column can only be ordered by numeric reference.");
        } finally {
            table.setDataCoersion(coersion); //reset coersion to original place
        }
        throw new IllegalStateException("Should not be here");
    }

    @SuppressWarnings("unchecked") //generic castings are ok here as errors will get caught later in the call chain
    private static int[] getOrder(DataTable table, int columnIndex, SetList<?> referenceCollection) {
        if (referenceCollection.size() == 0)
            return new int[0];
        DataType columnType = table.getColumnDataType(columnIndex);
        switch(columnType) { //deal with non-numerics
            case BOOLEAN : return CollectionOrder.getOrder(table.getBooleanColumn(columnIndex),(SetList<Boolean>) referenceCollection);
            case STRING : return CollectionOrder.getOrder(table.getStringColumn(columnIndex),(SetList<String>) referenceCollection);
        }
        //now deal with numbers rationally
        return getOrderNumber(table,columnIndex,(SetList<? extends Number>) referenceCollection);
    }

    private OrderedDataTable(DataTable table, int[] order, int start, int end, boolean checkOrder) {
        super(table);
        if (start >= end)
            throw new IllegalArgumentException(String.format("Table partition start (%d) must be strictly less than end (%d).",start,end));
        int rows = table.getRowCount();
        if (start < 0 || end < 0 || end > rows)
            throw new IllegalArgumentException(String.format("Table partition (%d,%d) is out of bounds for table with %d rows.",start,end,rows));
        this.order = order;
        this.start = start;
        this.end = end;
        if (checkOrder)
            checkOrder();
    }

    public OrderedDataTable(DataTable table, int[] order) {
        this(table,order,0,table.getRowCount(),true);
    }

    @SuppressWarnings("unchecked") //all data column types are comparable
    public OrderedDataTable(DataTable table, String columnLabel) {
        this(table,CollectionOrder.getOrder(table.<Comparable>getColumn(columnLabel)));
    }

    @SuppressWarnings("unchecked") //all data column types are comparable
    public OrderedDataTable(DataTable table, int columnIndex) {
        this(table, CollectionOrder.getOrder(table.<Comparable>getColumn(columnIndex)));
    }

    @SuppressWarnings("unchecked") //cannot ensure column will hold Ts, but if not it comparator should throw error, so suppressing
    public <T> OrderedDataTable(DataTable table, String columnLabel, Comparator<T> comparator) {
        this(table,CollectionOrder.getOrder((DataColumn<T>) table.getColumn(columnLabel),comparator));
    }

    @SuppressWarnings("unchecked") //cannot ensure column will hold Ts, but if not it comparator should throw error, so suppressing
    public <T> OrderedDataTable(DataTable table, int columnIndex, Comparator<T> comparator) {
        this(table,CollectionOrder.getOrder((DataColumn<T>) table.getColumn(columnIndex),comparator));
    }  

    public <T> OrderedDataTable(DataTable table, String columnLabel, SetList<T> referenceOrdering) {
        this(table,getOrder(table,table.getSchema().getColumnIndex(columnLabel),referenceOrdering));
    }     

    public <T> OrderedDataTable(DataTable table, int columnIndex, SetList<T> referenceOrdering) {
        this(table,getOrder(table,columnIndex,referenceOrdering));
    }

    private void checkOrder() {
        if (order.length != wrappedTable.getRowCount())
            throw new IllegalArgumentException(String.format("Order size (%d) must be the same as data table row count(%d).",order.length,wrappedTable.getRowCount()));
        int[] orderCopy = ArrayUtil.copyArray(order);
        Arrays.sort(orderCopy);
        if (!Arrays.equals(orderCopy,range(order.length).getRangeArray()))
            throw new IllegalArgumentException("Order array must contain every value between 0 and the data table length (" + wrappedTable.getRowCount() + ")");
    }

    @Override
    public int getRowCount() {
        return end - start;
    }

    @Override
    public <K> TableKey<K> getPrimaryKey() {
        return new OrderedTableKey<K>(wrappedTable.<K>getPrimaryKey());
    }

    private int checkRow(int rowIndex) {
        int row = rowIndex+start;
        if (row >= end)
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,row);
        return row;

    }

    @Override
    public Object getCellValue(int rowIndex, int columnIndex) {
        return wrappedTable.getCellValue(order[checkRow(rowIndex)], columnIndex);
    }

    @Override
    public Object getCellValue(int rowIndex, String columnLabel) {
        return wrappedTable.getCellValue(order[checkRow(rowIndex)], columnLabel);
    }

    @Override
    public DataRow getRow(int rowIndex) {
        return wrappedTable.getRow(order[checkRow(rowIndex)]);
    }
    
    @Override
    public BooleanDataColumn getBooleanColumn(int columnIndex) {
        return new BooleanDataColumn(new OrderedDataColumn<Boolean>(wrappedTable.getBooleanColumn(columnIndex)));
    }

    @Override
    public BooleanDataColumn getBooleanColumn(String columnLabel) {
        return new BooleanDataColumn(new OrderedDataColumn<Boolean>(wrappedTable.getBooleanColumn(columnLabel)));
    }
    
    @Override
    public ByteDataColumn getByteColumn(int columnIndex) {
        return new ByteDataColumn(new OrderedDataColumn<Byte>(wrappedTable.getByteColumn(columnIndex)));
    }

    @Override
    public ByteDataColumn getByteColumn(String columnLabel) {
        return new ByteDataColumn(new OrderedDataColumn<Byte>(wrappedTable.getByteColumn(columnLabel)));
    }
    
    @Override
    public ShortDataColumn getShortColumn(int columnIndex) {
        return new ShortDataColumn(new OrderedDataColumn<Short>(wrappedTable.getShortColumn(columnIndex)));
    }

    @Override
    public ShortDataColumn getShortColumn(String columnLabel) {
        return new ShortDataColumn(new OrderedDataColumn<Short>(wrappedTable.getShortColumn(columnLabel)));
    }
    
    @Override
    public IntegerDataColumn getIntColumn(int columnIndex) {
        return new IntegerDataColumn(new OrderedDataColumn<Integer>(wrappedTable.getIntColumn(columnIndex)));
    }

    @Override
    public IntegerDataColumn getIntColumn(String columnLabel) {
        return new IntegerDataColumn(new OrderedDataColumn<Integer>(wrappedTable.getIntColumn(columnLabel)));
    }
    
    @Override
    public LongDataColumn getLongColumn(int columnIndex) {
        return new LongDataColumn(new OrderedDataColumn<Long>(wrappedTable.getLongColumn(columnIndex)));
    }

    @Override
    public LongDataColumn getLongColumn(String columnLabel) {
        return new LongDataColumn(new OrderedDataColumn<Long>(wrappedTable.getLongColumn(columnLabel)));
    }
    
    @Override
    public FloatDataColumn getFloatColumn(int columnIndex) {
        return new FloatDataColumn(new OrderedDataColumn<Float>(wrappedTable.getFloatColumn(columnIndex)));
    }

    @Override
    public FloatDataColumn getFloatColumn(String columnLabel) {
        return new FloatDataColumn(new OrderedDataColumn<Float>(wrappedTable.getFloatColumn(columnLabel)));
    }
    
    @Override
    public DoubleDataColumn getDoubleColumn(int columnIndex) {
        return new DoubleDataColumn(new OrderedDataColumn<Double>(wrappedTable.getDoubleColumn(columnIndex)));
    }

    @Override
    public DoubleDataColumn getDoubleColumn(String columnLabel) {
        return new DoubleDataColumn(new OrderedDataColumn<Double>(wrappedTable.getDoubleColumn(columnLabel)));
    }

    @Override
    public StringDataColumn getStringColumn(int columnIndex) {
        return new StringDataColumn(new OrderedDataColumn<String>(wrappedTable.getStringColumn(columnIndex)));
    }

    @Override
    public StringDataColumn getStringColumn(String columnLabel) {
        return new StringDataColumn(new OrderedDataColumn<String>(wrappedTable.getStringColumn(columnLabel)));
    }

    @Override
    public <T> DataColumn<T> getColumn(int columnIndex) {
        return new OrderedDataColumn<T>(wrappedTable.<T>getColumn(columnIndex));
    }

    @Override
    public <T> DataColumn<T> getColumn(String columnLabel) {
        return new OrderedDataColumn<T>(wrappedTable.<T>getColumn(columnLabel));
    }

    @Override
    public DataTable getTablePartition(int start, int end) {
        return new OrderedDataTable(wrappedTable,order,start,end,false);
    }

    @Override
    public Iterator<DataRow> iterator() {
        @SuppressWarnings("unchecked")
        final Iterator<Integer> it = ArrayUtil.getIterator(Arrays.copyOfRange(order,start,end));

        return new Iterator<DataRow>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public DataRow next() {
                //return wrappedTable.getRow(order[it.next()]);
                return wrappedTable.getRow(it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private class OrderedDataColumn<T> implements DataColumn<T> {
        private final DataColumn<T> column;
        
        OrderedDataColumn(DataColumn<T> column) {
            this.column = column;
        }

        @Override
        public String getLabel() {
            return column.getLabel();
        }

        @Override
        public DataType getType() {
            return column.getType();
        }

        @Override
        public int getRowCount() {
            return OrderedDataTable.this.getRowCount();
        }

        @Override
        public T[] getData() {
            return ArrayUtil.buildArray(column.getData(),Arrays.copyOfRange(order,start,end));
        }

        @Override
        public T getCell(int rowNumber) {
            return column.getCell(order[checkRow(rowNumber)]);
        }

        @Override
        public <K> T getCellByKey(K key) {
            return column.getCellByKey(key);
        }

        @Override
        public Iterator<T> iterator() {
            @SuppressWarnings("unchecked")
            final Iterator<Integer> it = ArrayUtil.getIterator(Arrays.copyOfRange(order,start,end));
    
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }
    
                @Override
                public T next() {
                    return column.getCell(it.next());
                }
    
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private class OrderedTableKey<K> extends AbstractTableKey<K> {
        private TableKey<K> wrappedKey;
        private final InjectiveMap<K,Integer> keyIndex;

        private OrderedTableKey(TableKey<K> key) {
            super(key.getKeyColumnLabel(),key.getKeyColumnType());
            keyIndex = formKeyIndex(key);
        }

        private InjectiveMap<K,Integer> formKeyIndex(TableKey<K> key) {
            InjectiveMap<K,Integer> index = new InjectiveHashMap<K,Integer>();
            for (int i : range(start,end))
                index.put(key.getKey(order[i]),i);
            return index;
        }

        @Override
        protected InjectiveMap<K,Integer> getKeyIndex() {
            return keyIndex;
        }
    }
}
