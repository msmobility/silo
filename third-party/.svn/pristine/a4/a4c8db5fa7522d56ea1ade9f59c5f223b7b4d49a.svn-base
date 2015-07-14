package com.pb.sawdust.calculator.tabledata;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericResultFunction1;
import com.pb.sawdust.calculator.ParameterizedNumericFunction;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.JavaType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code ParameterizedDataRowFunction} is a one argument function which acts on data rows and returns a number.
 * <p>
 * An instance of this class is initialized with a function, but not with any information specific to a data row's structure.
 * Once one of the instance's {@code apply...} methods are called on a specific data row, then that instance is permanently
 * tied to the structure of that data row. Thus, as long as the data row's source data table is not modified (structurally),
 * all rows in that table will be correctly processed with the instance. However, other data tables (which are structured
 * differently) will be processed incorrectly (and may indicate the error because, for efficiency, data checks are not
 * performed for {@code apply...} calls). To use the underlying function held by the instance with a different data table,
 * use the {@code getFreshCopy()} method to get a new instance that has not been tied to a specific data table structure.
 *
 * @author crf
 *         Started 1/26/12 10:20 AM
 */
public class ParameterizedDataRowFunction implements NumericResultFunction1<DataRow> {
    private final ParameterizedNumericFunction function;
    private final NumericFunctionN numericFunction;
    private final int[] indices;
    private volatile DataType type;
    private volatile DataType returnType;
    private final boolean automaticallyWiden;
    private final ThreadLocal<byte[]> byteArgs;
    private final ThreadLocal<short[]> shortArgs;
    private final ThreadLocal<int[]> intArgs;
    private final ThreadLocal<long[]> longArgs;
    private final ThreadLocal<float[]> floatArgs;
    private final ThreadLocal<double[]> doubleArgs;
    private final AtomicBoolean indexBuilt = new AtomicBoolean(false);

    /**
     * Constructor specifying the function, the data type the function returns, and whether or not the function should automatically
     * widen its parameters when it is applied. If the function is set to automatically widen, then the argument with the
     * "widest" data type (as specified by {@link JavaType#getWideningType(com.pb.sawdust.util.JavaType,com.pb.sawdust.util.JavaType)})
     * is used to specify the data type that the function will process with. If it is set to not automatically widen, then
     * the function will be applied with all of the arguments cast to the function's return type.
     *
     * @param function
     *        The function.
     *
     * @param type
     *        The data type that {@code function} returns.
     *
     * @param automaticallyWiden
     *        If {@code true}, {@code function} will widen its arguments when applied, if {@code false} it will use {@code type}
     *        for all of its arguments.
     */
    public ParameterizedDataRowFunction(ParameterizedNumericFunction function, DataType type, boolean automaticallyWiden) {
        this.function = function;
        numericFunction = function.getFunction();
        indices = new int[function.getArgumentNames().size()];
        this.returnType = type;
        this.automaticallyWiden = automaticallyWiden;
        if (!automaticallyWiden)
            this.type = returnType;
        byteArgs = new ThreadLocal<byte[]>() {
            public byte[] initialValue() {
                return new byte[indices.length];
            }
        };
        shortArgs = new ThreadLocal<short[]>() {
            public short[] initialValue() {
                return new short[indices.length];
            }
        };
        intArgs = new ThreadLocal<int[]>() {
            public int[] initialValue() {
                return new int[indices.length];
            }
        };
        longArgs = new ThreadLocal<long[]>() {
            public long[] initialValue() {
                return new long[indices.length];
            }
        };
        floatArgs = new ThreadLocal<float[]>() {
            public float[] initialValue() {
                return new float[indices.length];
            }
        };
        doubleArgs = new ThreadLocal<double[]>() {
            public double[] initialValue() {
                return new double[indices.length];
            }
        };
    }
    /**
     * Constructor specifying the function and the data type the function returns. The function will automatically widen
     * its arguments to that with the "widest" data type (as specified by
     * {@link JavaType#getWideningType(com.pb.sawdust.util.JavaType,com.pb.sawdust.util.JavaType)}), and the result of the
     * function will be cast to the specified return type.
     *
     * @param function
     *        The function.
     *
     * @param type
     *        The data type that {@code function} returns.
     */
    public ParameterizedDataRowFunction(ParameterizedNumericFunction function, DataType type) {
        this(function,type,true);
    }

    /**
     * Constructor specifying the function. The function will automatically widen its arguments to that with the "widest"
     * data type (as specified by {@link JavaType#getWideningType(com.pb.sawdust.util.JavaType,com.pb.sawdust.util.JavaType)}),
     * and that type will also be the return type of the function.
     *
     * @param function
     *        The function.
     */
    public ParameterizedDataRowFunction(ParameterizedNumericFunction function) {
        this(function,null,true);
    }

    public ParameterizedDataRowFunction getFreshCopy() { //todo: this doesn't allow for dynamic return typing...
        return new ParameterizedDataRowFunction(function,type,automaticallyWiden);
    }

    private void buildIndices(DataRow row, DataType defaultDataType) {
        if (!indexBuilt.compareAndSet(false,false))
            return;
        buildIndicesInternal(row.getColumnLabels(),row.getColumnTypes(),defaultDataType);
    }

    private synchronized void buildIndicesInternal(String[] columnNames, DataType[] columnTypes, DataType defaultDataType) {
        if (indexBuilt.get())
            return;
        int counter = 0;
        for (String parameter : function.getArgumentNames()) {
            boolean foundIt = false;
            for (int i = 0; i < columnNames.length; i++) {
                if (parameter.equals(columnNames[i])) {
                    indices[counter++] = i;
                    foundIt = true;
                    break;
                }
            }
            if (!foundIt) {
                throw new IllegalArgumentException("Parameter not found in data row: " + parameter);
            }
        }
        if (automaticallyWiden) {
            List<DataType> usedTypes = new LinkedList<>();
            for (String s : function.getArgumentNames()) {
                for (int i = 0; i < columnNames.length; i++) {
                    if (columnNames[i].equals(s)) {
                        usedTypes.add(columnTypes[i]);
                        break;
                    }
                }
            }
            for (DataType cType : usedTypes)
                if (type == null || JavaType.getWideningType(type.getJavaType(),cType.getJavaType()) == cType.getJavaType())
                    type = cType;
            if (returnType == null) //would have to set below anyway with same process, so this will be more efficient
                returnType = type;
            else if (type != null && (JavaType.getWideningType(type.getJavaType(),returnType.getJavaType()) == returnType.getJavaType()))
                type = returnType; //need to widen to return type, if return type is wider than parameter types
        }
        if (returnType == null) {
            List<DataType> usedTypes = new LinkedList<>();
            for (String s : function.getArgumentNames()) {
                for (int i = 0; i < columnNames.length; i++) {
                    if (columnNames[i].equals(s)) {
                        usedTypes.add(columnTypes[i]);
                        break;
                    }
                }
            }
            for (DataType cType : usedTypes)
                if (returnType == null || JavaType.getWideningType(returnType.getJavaType(),cType.getJavaType()) == cType.getJavaType())
                    returnType = cType;
            if (type == null) //same as not automatically widen
                type = returnType;
        }
        if (type == null)
            type = returnType = defaultDataType;
        indexBuilt.set(true);
    }

    private DataType getReturnType(DataRow row) {
        if (returnType != null)
            return returnType;
        for (String parameter : function.getArgumentNames()) {
            DataType type = row.getColumnType(parameter);
            if (returnType == null || JavaType.getWideningType(returnType.getJavaType(),row.getColumnType(parameter).getJavaType()) == type.getJavaType())
                returnType = type;
        }
        return returnType;
    }

    @Override
    public Number apply(DataRow dataRow) {
        switch (getReturnType(dataRow)) {
            case BOOLEAN : return applyByte(dataRow);
            case BYTE : return applyByte(dataRow);
            case SHORT : return applyShort(dataRow);
            case INT : return applyInt(dataRow);
            case LONG : return applyLong(dataRow);
            case FLOAT : return applyFloat(dataRow);
            case DOUBLE : return applyDouble(dataRow);
            case STRING : return applyDouble(dataRow);
            default : throw new IllegalStateException("Shouldn't be here: " + returnType);
        }
    }

    public byte applyByte(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.BYTE);
        if (type != DataType.BYTE) {
            switch (type) {
                case SHORT : return (byte) applyShort(row);    
                case INT : return (byte) applyInt(row);
                case LONG : return (byte) applyLong(row);
                case FLOAT : return (byte) applyFloat(row);
                case DOUBLE : return (byte) applyDouble(row);
            }
        }
        byte [] arguments = byteArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsByte(indices[i]);
        return numericFunction.applyByte(arguments);
    }    

    public short applyShort(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.SHORT);
        switch (type) {                                   
            case BYTE : return (short) applyByte(row); 
            case INT : return (short) applyInt(row);
            case LONG : return (short) applyLong(row);
            case FLOAT : return (short) applyFloat(row);
            case DOUBLE : return (short) applyDouble(row);
        }
        short [] arguments = shortArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsShort(indices[i]);
        return numericFunction.applyShort(arguments);
    }

    public int applyInt(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.INT);
        switch (type) {                                   
            case BYTE : return (int) applyByte(row); 
            case SHORT : return (int) applyShort(row);  
            case LONG : return (int) applyLong(row);
            case FLOAT : return (int) applyFloat(row);
            case DOUBLE : return (int) applyDouble(row);
        }
        int [] arguments = intArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsInt(indices[i]);
        return numericFunction.applyInt(arguments);
    }

    public long applyLong(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.LONG);
        switch (type) {                                   
            case BYTE : return (long) applyByte(row); 
            case SHORT : return (long) applyShort(row);    
            case INT : return (long) applyInt(row);
            case FLOAT : return (long) applyFloat(row);
            case DOUBLE : return (long) applyDouble(row);
        }
        long [] arguments = longArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsLong(indices[i]);
        return numericFunction.applyLong(arguments);
    }

    public float applyFloat(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.FLOAT);
        switch (type) {                                   
            case BYTE : return (float) applyByte(row); 
            case SHORT : return (float) applyShort(row);    
            case INT : return (float) applyInt(row);
            case LONG : return (float) applyLong(row);
            case DOUBLE : return (float) applyDouble(row);
        }
        float [] arguments = floatArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsFloat(indices[i]);
        return numericFunction.applyFloat(arguments);
    }

    public double applyDouble(DataRow row) {
        if (!indexBuilt.get())
            buildIndices(row,DataType.DOUBLE);
        switch (type) {                                   
            case BYTE : return (double) applyByte(row); 
            case SHORT : return (double) applyShort(row);    
            case INT : return (double) applyInt(row);
            case LONG : return (double) applyLong(row);
            case FLOAT : return (double) applyFloat(row);
        }
        double [] arguments = doubleArgs.get();
        for (int i = 0; i < arguments.length; i++)
            arguments[i] = row.getCellAsDouble(indices[i]);
        return numericFunction.applyDouble(arguments);
    }
}
