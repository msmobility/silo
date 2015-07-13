package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.ParameterizedNumericFunction;
import com.pb.sawdust.calculator.tabledata.ParameterizedDataRowFunction;
import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataField;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.data.census.pums.transform.PumaDataGroupTransformation;
import com.pb.sawdust.data.census.pums.transform.PumaRowWiseDataTableTransformation;
import com.pb.sawdust.data.census.pums.transform.PumaRowWiseTransformation;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.transform.row.ContextDataRow;
import com.pb.sawdust.tabledata.transform.row.ContextlessRowWiseColumnTransformation;
import com.pb.sawdust.tabledata.transform.row.RowWiseDataTableTransformation;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code SpecPumaTransformatinFunction} ...
 *
 * @author crf
 *         Started 1/26/12 1:06 PM
 */
public class SpecPumaTransformationFunction extends PumaRowWiseTransformation {

    private final ParameterizedDataRowFunction transformationFunction;
    private final ParameterizedDataRowFunction filterFunction;
    private final boolean filterOn;
    private final String columnName;
    private volatile int columnIndex;
    private volatile DataType dataType;
    private final AtomicBoolean indexBuilt = new AtomicBoolean(false);

    private volatile PumaDataRowGenerator rowGenerator;

    public SpecPumaTransformationFunction(String columnName, ParameterizedNumericFunction transformationFunction, ParameterizedNumericFunction filterFunction, PumaDataDictionary<?,?> dictionary, PumaDataType transformationType) {
        super(dictionary,transformationType);
        this.columnName = columnName;
        this.transformationFunction = new ParameterizedDataRowFunction(transformationFunction);
        this.filterFunction = filterFunction == null ? null : new ParameterizedDataRowFunction(filterFunction);
        filterOn = filterFunction != null;
    }

    private void buildIndices(PumaTables.PumaDataRow row) {
        if (indexBuilt.compareAndSet(false,false))
            buildIndicesInternal(row);
        else
            buildIndicesInternal(row);
    }

    private synchronized void buildIndicesInternal(PumaTables.PumaDataRow row) {
        if (indexBuilt.get())
            return;
        columnIndex = row.getColumnIndex(columnName);
        dataType = row.getColumnType(columnName);
        rowGenerator = new PumaDataRowGenerator(row,getTransformationType());
//        if (getTransformationType() == PumaDataType.PERSON) {
//            DataRow hhRow = row.getContext().getHouseholdRow();
//            shift = row.getColumnLabels().length;
//            pumaRowColumnLabels = new String[shift + hhRow.getColumnLabels().length];
//            pumaRowColumnTypes = new DataType[pumaRowColumnLabels.length];
//            System.arraycopy(row.getColumnLabels(),0,pumaRowColumnLabels,0,shift);
//            System.arraycopy(row.getColumnTypes(),0,pumaRowColumnTypes,0,shift);
//            System.arraycopy(hhRow.getColumnLabels(),0,pumaRowColumnLabels,shift,pumaRowColumnLabels.length-shift);
//            System.arraycopy(hhRow.getColumnTypes(),0,pumaRowColumnTypes,shift,pumaRowColumnTypes.length-shift);
//            for (int i = shift; i < pumaRowColumnLabels.length; i++)
//                pumaRowColumnLabels[i] = formHouseholdVariable(pumaRowColumnLabels[i]);
//            pumaRowWillCoerceData = row.willCoerceData() && hhRow.willCoerceData();
//        }
        indexBuilt.set(true);
    }

    @Override
    protected void transformRow(PumaTables.PumaDataRow row, DataTable table, int rowIndex) {
        if (!indexBuilt.get())
            buildIndices(row);
        DataRow pumaRow = rowGenerator.getRow(row);
//        switch(getTransformationType()) {
//            case PERSON : pumaRow = new PumaRow(row,row.getContext().getHouseholdRow()); break;
//            case HOUSEHOLD : pumaRow = row; break;
//            default : throw new IllegalStateException("Shouldn't be here...");
//        }
        if (filterOn && filterFunction.applyInt(pumaRow) == 0)
            return;
        switch (dataType) {
            case BYTE : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyByte(pumaRow)); return;
            case SHORT : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyShort(pumaRow)); return;
            case INT : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyInt(pumaRow)); return;
            case LONG : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyLong(pumaRow)); return;
            case FLOAT : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyFloat(pumaRow)); return;
            case DOUBLE : table.setCellValue(rowIndex,columnIndex,transformationFunction.applyDouble(pumaRow)); return;
        }
        throw new IllegalStateException("This type shouldn't have gotten through: " + dataType);
    }

//    private volatile String[] pumaRowColumnLabels;
//    private volatile DataType[] pumaRowColumnTypes;
//    private volatile int shift;
//    private volatile boolean pumaRowWillCoerceData;
//
//    private class PumaRow extends AbstractDataRow {
//        private final DataRow primaryRow;
//        private final DataRow secondaryRow;
//
//        private PumaRow(DataRow primaryRow, DataRow secondaryRow) {
//            this.primaryRow = primaryRow;
//            this.secondaryRow = secondaryRow;
//        }
//
//        public String[] getColumnLabels() {
//            return pumaRowColumnLabels;
//        }
//
//        public DataType[] getColumnTypes() {
//            return pumaRowColumnTypes;
//        }
//
//        public Object[] getData() {
//            Object[] data = new Object[pumaRowColumnLabels.length];
//            System.arraycopy(primaryRow.getData(),0,data,0,shift);
//            System.arraycopy(secondaryRow.getData(),0,data,shift,data.length-shift);
//            return data;
//        }
//
//        public <T> T getCell(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCell(columnIndex - shift);
//            return primaryRow.getCell(columnIndex);
//        }
//
//        public byte getCellAsByte(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsByte(columnIndex-shift);
//            return primaryRow.getCellAsByte(columnIndex);
//        }
//
//        public short getCellAsShort(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsShort(columnIndex - shift);
//            return primaryRow.getCellAsShort(columnIndex);
//        }
//        public int getCellAsInt(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsInt(columnIndex - shift);
//            return primaryRow.getCellAsInt(columnIndex);
//        }
//
//        public long getCellAsLong(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsLong(columnIndex - shift);
//            return primaryRow.getCellAsLong(columnIndex);
//        }
//
//        public float getCellAsFloat(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsFloat(columnIndex - shift);
//            return primaryRow.getCellAsFloat(columnIndex);
//        }
//
//        public double getCellAsDouble(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsDouble(columnIndex - shift);
//            return primaryRow.getCellAsDouble(columnIndex);
//        }
//
//        public boolean getCellAsBoolean(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsBoolean(columnIndex - shift);
//            return primaryRow.getCellAsBoolean(columnIndex);
//        }
//
//        public String getCellAsString(int columnIndex) {
//            if (columnIndex >= shift)
//                return secondaryRow.getCellAsString(columnIndex - shift);
//            return primaryRow.getCellAsString(columnIndex);
//        }
//
//        public boolean willCoerceData() {
//            return pumaRowWillCoerceData;
//        }
//    }


    //transformation builders
    public static <T extends RowWiseDataTableTransformation & PumaDataGroupTransformation>
        T getTransformation(PumaDataDictionary<?,?> dictionary, PumaDataField field, ParameterizedNumericFunction transformation, ParameterizedNumericFunction filter) {
        return getTransformation(dictionary,field.getColumnName(),transformation,filter,PumaDataType.getFieldType(field));
    }

    @SuppressWarnings("unchecked") //casts are generally ok in spirit - if user sets wrong T then error could be thrown, but that would indicate an error on their end
    public static <T extends RowWiseDataTableTransformation & PumaDataGroupTransformation>
      T getTransformation(PumaDataDictionary<?,?> dictionary, String column, final ParameterizedNumericFunction transformation, final ParameterizedNumericFunction filter, final PumaDataType transformationType) {
        int transformationArgsSize = transformation.getArgumentNames().size();
        int filterArgsSize = filter == null ? 0 : filter.getArgumentNames().size();
        if (filterArgsSize == 0) {
            if (filter != null && filter.getFunction().applyDouble() == 0.0) {
                return (T) new PumaRowWiseDataTableTransformation(new RowWiseDataTableTransformation() {
                    @Override
                    public void transformRow(DataRow row, DataTable table, int rowIndex) {
                        //do nothing
                    }
                },dictionary,transformationType);
            }
        }
        if (transformationArgsSize == 0) {
            if (filterArgsSize == 0) { //must evaluate to true, because of the previous section
                final double result = transformation.getFunction().applyDouble();
                new PumaRowWiseDataTableTransformation(new ContextlessRowWiseColumnTransformation<>(
                    column,
                    DataType.DOUBLE,
                    new Function1<Double,Double>() {
                        @Override
                        public Double apply(Double aDouble) {
                            return result;
                        }
                    }
                ),dictionary,transformationType);
            }
        }
        boolean singular = (transformationArgsSize < 2 && filterArgsSize < 2) &&
                           (transformationArgsSize == 0 || (transformation.getArgumentNames().get(0).equals(column))) &&
                           (filterArgsSize == 0 || (filter.getArgumentNames().get(0).equals(column)));
        if (singular)
            return (T) new FilteredContextlessRowWiseColumnTransformation(column,(Function1<Double,Double>) transformation.getFunction(),(NumericFunction1) filter.getFunction());

        return (T) new SpecPumaTransformationFunction(column,transformation,filter,dictionary,transformationType);
    }


    private static class FilteredContextlessRowWiseColumnTransformation extends ContextlessRowWiseColumnTransformation<Double> {
        private final boolean filterOn;
        private final NumericFunction1 filter;

        public FilteredContextlessRowWiseColumnTransformation(String column, Function1<Double,Double> transformation, NumericFunction1 filter) {
            super(column,DataType.DOUBLE,transformation);
            filterOn = filter == null;
            this.filter = filter;
        }

         @Override
        protected void transformRow(ContextDataRow<Void> contextDataRow, DataTable table, int rowIndex) {
             if (filterOn && filter.applyDouble(getColumnData(contextDataRow)) == 0.0)
                 return;
             super.transformRow(contextDataRow,table,rowIndex);
        }
    }
}
