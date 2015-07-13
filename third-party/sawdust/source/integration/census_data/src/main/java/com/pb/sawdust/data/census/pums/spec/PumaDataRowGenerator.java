package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.AbstractDataRow;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code PumaDataGroupFunction} ...
 *
 * @author crf
 *         Started 1/30/12 10:24 AM
 */
public class PumaDataRowGenerator {
    public static final String HOUSEHOLD_VARIABLE_PREFIX = "h_";
    public static final String PERSON_VARIABLE_PREFIX = "p_";

//    private final boolean buildRow;
    private final PumaDataType transformationType;
    private final String[] pumaRowColumnLabels;
    private final DataType[] pumaRowColumnTypes;
    private final int shift;
    private final boolean pumaRowWillCoerceData;

    public PumaDataRowGenerator(PumaTables.PumaDataRow row, PumaDataType transformationType) {
        DataRow contextRow = transformationType == PumaDataType.HOUSEHOLD ? row.getContext().getPersonRows().iterator().next() : row.getContext().getHouseholdRow();
        shift = row.getColumnLabels().length;
        pumaRowColumnLabels = new String[shift + contextRow.getColumnLabels().length];
        pumaRowColumnTypes = new DataType[pumaRowColumnLabels.length];
        System.arraycopy(row.getColumnLabels(),0,pumaRowColumnLabels,0,shift);
        System.arraycopy(row.getColumnTypes(),0,pumaRowColumnTypes,0,shift);
        System.arraycopy(contextRow.getColumnLabels(),0,pumaRowColumnLabels,shift,pumaRowColumnLabels.length-shift);
        System.arraycopy(contextRow.getColumnTypes(),0,pumaRowColumnTypes,shift,pumaRowColumnTypes.length-shift);
        for (int i = shift; i < pumaRowColumnLabels.length; i++)
            pumaRowColumnLabels[i] = transformationType == PumaDataType.HOUSEHOLD ? formPersonVariable(pumaRowColumnLabels[i]) : formHouseholdVariable(pumaRowColumnLabels[i]);
        pumaRowWillCoerceData = row.willCoerceData() && contextRow.willCoerceData();
        this.transformationType = transformationType;


//        buildRow = transformationType == PumaDataType.PERSON;
//        if (buildRow) {
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
//        } else {
////            pumaRowColumnLabels = null;
////            pumaRowColumnTypes = null;
////            shift = -1;
////            pumaRowWillCoerceData = row.willCoerceData();
//
//
//            DataRow personRow = row.getContext().getPersonRows().iterator().next();
//            shift = row.getColumnLabels().length;
//            pumaRowColumnLabels = new String[shift + personRow.getColumnLabels().length];
//            pumaRowColumnTypes = new DataType[pumaRowColumnLabels.length];
//            System.arraycopy(row.getColumnLabels(),0,pumaRowColumnLabels,0,shift);
//            System.arraycopy(row.getColumnTypes(),0,pumaRowColumnTypes,0,shift);
//            System.arraycopy(personRow.getColumnLabels(),0,pumaRowColumnLabels,shift,pumaRowColumnLabels.length-shift);
//            System.arraycopy(personRow.getColumnTypes(),0,pumaRowColumnTypes,shift,pumaRowColumnTypes.length-shift);
//            for (int i = shift; i < pumaRowColumnLabels.length; i++)
//                pumaRowColumnLabels[i] = formHouseholdVariable(pumaRowColumnLabels[i]);
//            pumaRowWillCoerceData = row.willCoerceData() && hhRow.willCoerceData();
//        }
    }

    public DataRow getRow(PumaTables.PumaDataRow row) {
        switch (transformationType) {
            case PERSON : return new PumaRow(row,row.getContext().getHouseholdRow(),null);
            case HOUSEHOLD : return new PumaRow(row,null,row.getContext().getPersonRows());
            default : throw new IllegalStateException("Shouldn't be here...");
        }
    }

    public String formHouseholdVariable(String variable) {
        return HOUSEHOLD_VARIABLE_PREFIX + variable;
    }

    public String formPersonVariable(String variable) {
        return PERSON_VARIABLE_PREFIX + variable;
    }

    private class PumaRow extends AbstractDataRow {
        private final DataRow primaryRow;
        private final DataRow secondaryRow;
        private final Iterable<DataRow> secondaryRows;

        private PumaRow(DataRow primaryRow, DataRow secondaryRow, Iterable<DataRow> secondaryRows) {
            this.primaryRow = primaryRow;
            this.secondaryRow = secondaryRow;
            this.secondaryRows = secondaryRows;
        }

        public String[] getColumnLabels() {
            return pumaRowColumnLabels;
        }

        public DataType[] getColumnTypes() {
            return pumaRowColumnTypes;
        }

        public Object[] getData() {
            Object[] data = new Object[pumaRowColumnLabels.length];
            System.arraycopy(primaryRow.getData(),0,data,0,shift);
            switch (transformationType) {
                case PERSON : System.arraycopy(secondaryRow.getData(),0,data,shift,data.length-shift); break;
                case HOUSEHOLD : {
                    for (int i = shift; i < data.length; i++)
                        data[i] = getCell(i);
                }
            }
            return data;
        }

        @SuppressWarnings("unchecked") //these are the correct casts
        public <T> T getCell(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCell(index);
                    case HOUSEHOLD : {
                        switch (pumaRowColumnTypes[columnIndex]) {
                            case BOOLEAN : return (T) (Boolean) getCellAsBoolean(columnIndex);
                            case BYTE : return (T) (Byte) getCellAsByte(columnIndex);
                            case SHORT : return (T) (Short) getCellAsShort(columnIndex);
                            case INT : return (T) (Integer) getCellAsInt(columnIndex);
                            case LONG : return (T) (Long) getCellAsLong(columnIndex);
                            case FLOAT : return (T) (Float) getCellAsFloat(columnIndex);
                            case DOUBLE : return (T) (Double) getCellAsDouble(columnIndex);
                            case STRING : return (T) getCellAsString(columnIndex);
                            default : throw new IllegalStateException("Shouldn't be here...");
                        }
                    }
                }
            }
            return primaryRow.getCell(columnIndex);
        }

        public byte getCellAsByte(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsByte(index);
                    case HOUSEHOLD : {
                        byte sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsByte(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsByte(columnIndex);
        }

        public short getCellAsShort(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsShort(index);
                    case HOUSEHOLD : {
                        short sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsShort(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsShort(columnIndex);
        }
        public int getCellAsInt(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsInt(index);
                    case HOUSEHOLD : {
                        int sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsInt(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsInt(columnIndex);
        }

        public long getCellAsLong(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsLong(index);
                    case HOUSEHOLD : {
                        long sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsLong(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsLong(columnIndex);
        }

        public float getCellAsFloat(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsFloat(index);
                    case HOUSEHOLD : {
                        float sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsFloat(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsFloat(columnIndex);
        }

        public double getCellAsDouble(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsDouble(index);
                    case HOUSEHOLD : {
                        double sum = 0;
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsDouble(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsDouble(columnIndex);
        }

        public boolean getCellAsBoolean(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsBoolean(index);
                    case HOUSEHOLD : {
                        boolean sum = false;
                        for (DataRow row : secondaryRows)
                            sum |= row.getCellAsBoolean(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsBoolean(columnIndex);
        }

        public String getCellAsString(int columnIndex) {
            if (columnIndex >= shift) {
                int index = columnIndex - shift;
                switch (transformationType) {
                    case PERSON : return secondaryRow.getCellAsString(index);
                    case HOUSEHOLD : {
                        String sum = "";
                        for (DataRow row : secondaryRows)
                            sum += row.getCellAsString(index);
                        return sum;
                    }
                }
            }
            return primaryRow.getCellAsString(columnIndex);
        }

        public boolean willCoerceData() {
            return pumaRowWillCoerceData;
        }
    }
}
