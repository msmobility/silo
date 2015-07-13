package com.pb.sawdust.tabledata.metadata;

import com.pb.sawdust.util.Caster;
import com.pb.sawdust.util.JavaType;

import java.lang.reflect.Array;

/**
 * <p>
 * The {@code DataType} enum provides a list of data types used by {@code DataTable}s. Each data type has a
 * corresponding primitive and object type; if no primitive correspondance exists, then the corresponding object
 * class is used as the primitive correspondence. These types are intended to form a basic framework for data
 * tables, and should cover all possible types that may be encountered in data tables.
 * //todo: add binary, and date(/)time types
 * @author crf <br>
 *         Started: May 7, 2008 4:48:45 PM
 */
public enum DataType {
    /**
     * Data type corresponding to the {@code boolean} primitive and its wrapper class.
     */
    BOOLEAN(boolean.class,Boolean.class,JavaType.BOOLEAN),

    /**
     * Data type corresponding to the {@code byte} primitive and its wrapper class.
     */
    BYTE(byte.class,Byte.class,JavaType.BYTE),

    /**
     * Data type corresponding to the {@code short} primitive and its wrapper class.
     */
    SHORT(short.class,Short.class,JavaType.SHORT),

    /**
     * Data type corresponding to the {@code int} primitive and its wrapper class.
     */
    INT(int.class,Integer.class,JavaType.INT),

    /**
     * Data type corresponding to the {@code long} primitive and its wrapper class.
     */
    LONG(long.class,Long.class,JavaType.LONG),

    /**
     * Data type corresponding to the {@code float} primitive and its wrapper class.
     */
    FLOAT(float.class,Float.class,JavaType.FLOAT),

    /**
     * Data type corresponding to the {@code double} primitive and its wrapper class.
     */
    DOUBLE(double.class,Double.class,JavaType.DOUBLE),

    /**
     * Data type corresponding to the {@code String} class. It does not have a seperate primitive class.
     */
    STRING(null,String.class,JavaType.OBJECT);

    private final Class primitiveClass;
    private final Class objectClass;
    private final JavaType javaType;

    private DataType(Class primitiveClass, Class objectClass, JavaType javaType) {
        this.primitiveClass = primitiveClass;
        this.objectClass = objectClass;
        this.javaType = javaType;
        objectClass.getName();
    }

    /**
     * Get the primitive class associated with this data type.  If explicit primitive is specified for the class,
     * then its object class is returned.
     *
     * @return this data type's primitive class.
     */
    public Class getPrimitiveClass() {
        return primitiveClass != null ? primitiveClass : objectClass;
    }

    /**
     * Get the object class associated with this data type.
     *
     * @return this data type's object class.
     */
    public Class getObjectClass() {
        return objectClass;
    }

    /**
     * Get the name of this object's primitive type.  If no primitive type is specified, then its fully qualified
     * (binary) object name is returned.
     *
     * @return this data type's primitive class name.
     */
    public String getPrimitiveTypeString() {
        return getPrimitiveClass().getName();
    }

    /**
     * Get the name of this object's object type. The name of the class returned in sthe fully qualified (binary)
     * name.
     *
     * @return this data type's object class name.
     *
     * @see java.lang.Class#getName()
     */
    public String getObjectTypeString() {
        return getObjectClass().getName();
    }

    /**
     * Get an array of specified size whose component type is this data type's primitive class.
     *
     * @param size
     *        The size of the array.
     *
     * @return a new array of length {@code size} which can hold elements of {@link #getPrimitiveClass}.
     */
    public Object getPrimitiveArray(int size) {
        return Array.newInstance(getPrimitiveClass(),size);
    }


    /**
     * Get an array of specified size whose component type is this data type's object class. This returns an {@code Object[]}
     * since all object arrays are subtypes of that type. The returned array can be safely cast to an array of this
     * data type's object class. 
     *
     * @param size
     *        The size of the array.
     *
     * @return a new array of length {@code size} which can hold elements of {@link #getObjectClass}.
     */
    public Object[] getObjectArray(int size) {
        return (Object[]) Array.newInstance(getObjectClass(),size);
    }

    /**
     * Get the JavaType associated with this data type.
     *
     * @return the java type associated with this data type.
     */
    public JavaType getJavaType() {
        return javaType;
    }

    /**
     * Coerce a specified object to this type. Most coercions are performed through the {@link Caster} class, except {@code String}
     * to primitive, which use the appropriate {@code .parse...()} method from the primitive's object equivalent class,
     * and anything to {@code String}, which just uses the input object's {@code toString()} method.
     *
     * @param data
     *        The object to coerce.
     *
     * @param fromType
     *        The data type of {@code object}.
     *
     * @return the result of coercing {@code data}.
     */
    public Object coerce(Object data, DataType fromType) {
        switch (this) {
            case BOOLEAN : {
                switch (fromType) {
                    case BOOLEAN : return data;
                    case BYTE : return Caster.castToBoolean((Byte) data);
                    case SHORT : return Caster.castToBoolean((Short) data);
                    case INT : return Caster.castToBoolean((Integer) data);
                    case LONG : return Caster.castToBoolean((Long) data);
                    case FLOAT : return Caster.castToBoolean((Float) data);
                    case DOUBLE : return Caster.castToBoolean((Double) data);
                    case STRING : return (Boolean.parseBoolean((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case BYTE : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToByte((Boolean) data);
                    case BYTE : return data;
                    case SHORT : return Caster.castToByte((Short) data);
                    case INT : return Caster.castToByte((Integer) data);
                    case LONG : return Caster.castToByte((Long) data);
                    case FLOAT : return Caster.castToByte((Float) data);
                    case DOUBLE : return Caster.castToByte((Double) data);
                    case STRING : return (Byte.parseByte((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case SHORT : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToShort((Boolean) data);
                    case BYTE : return Caster.castToBoolean((Byte) data);
                    case SHORT : return data;
                    case INT : return Caster.castToShort((Integer) data);
                    case LONG : return Caster.castToShort((Long) data);
                    case FLOAT : return Caster.castToShort((Float) data);
                    case DOUBLE : return Caster.castToShort((Double) data);
                    case STRING : return (Short.parseShort((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case INT : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToInt((Boolean) data);
                    case BYTE : return Caster.castToInt((Byte) data);
                    case SHORT : return Caster.castToInt((Short) data);
                    case INT : return data;
                    case LONG : return Caster.castToInt((Long) data);
                    case FLOAT : return Caster.castToInt((Float) data);
                    case DOUBLE : return Caster.castToInt((Double) data);
                    case STRING : return (Integer.parseInt((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case LONG : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToLong((Boolean) data);
                    case BYTE : return Caster.castToLong((Byte) data);
                    case SHORT : return Caster.castToLong((Short) data);
                    case INT : return Caster.castToLong((Integer) data);
                    case LONG : return data;
                    case FLOAT : return Caster.castToLong((Float) data);
                    case DOUBLE : return Caster.castToLong((Double) data);
                    case STRING : return (Long.parseLong((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case FLOAT : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToFloat((Boolean) data);
                    case BYTE : return Caster.castToFloat((Byte) data);
                    case SHORT : return Caster.castToFloat((Short) data);
                    case INT : return Caster.castToFloat((Integer) data);
                    case LONG : return Caster.castToFloat((Long) data);
                    case FLOAT : return data;
                    case DOUBLE : return Caster.castToFloat((Double) data);
                    case STRING : return (Float.parseFloat((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case DOUBLE : {
                switch (fromType) {
                    case BOOLEAN : return Caster.castToDouble((Boolean) data);
                    case BYTE : return Caster.castToDouble((Byte) data);
                    case SHORT : return Caster.castToDouble((Short) data);
                    case INT : return Caster.castToDouble((Integer) data);
                    case LONG : return Caster.castToDouble((Long) data);
                    case FLOAT : return Caster.castToDouble((Float) data);
                    case DOUBLE : return data;
                    case STRING : return (Double.parseDouble((String) data));
                    default : throw new IllegalStateException("Shouldn't be here");
                }
            }
            case STRING : return data.toString();
        }
        throw new IllegalStateException("Shouldn't be here.");
    }

    /**
     * Coerce a specified object to this type. Most coercions are performed through the {@link Caster} class, except {@code String}
     * to primitive, which use the appropriate {@code .parse...()} method from the primitive's object equivalent class,
     * and anything to {@code String}, which just uses the input object's {@code toString()} method.
     *
     * @param data
     *        The object to coerce.
     *
     * @return the result of coercing {@code data}.
     */
    public Object coerce(Object data) {
        return coerce(data,getDataType(data));
    }

    /**
     * Get the data type associated with a specified object.
     *
     * @param data
     *        The object in question.
     *
     * @return the data type that {@code data} belongs to.
     */
    public static DataType getDataType(Object data) {
        Class c = data.getClass();
        for (DataType type : DataType.values())
            if (type.getObjectClass() == c)
                return type;
        throw new IllegalArgumentException("Data type not found for " + data);
    }

    /**
     * Get the data type associated with a specified class. That is, if {@code object} was an instance of the specified
     * class, then this method returns the same value as {@code DataType.getDataType(object)}.
     *
     * @param dataClass
     *        The object in question.
     *
     * @return the data type that {@code data} belongs to.
     */
    public static DataType getDataType(Class dataClass) {
        for (DataType type : DataType.values())
            if (type.getPrimitiveClass() == dataClass || type.getObjectClass() == dataClass && dataClass != null)
                return type;
        throw new IllegalArgumentException("Data type not found for " + dataClass);
    }

    /**
     * Compare two data types and determine which one is "inclusive" of the two. Loosely speaking, the inclusive data type
     * means that the other type can be cast to it, but the opposite is/may not be true. This is not a "strict" answer,
     * but rather should be used more for guidance when determining types for data columns.
     *
     * @param type1
     *        The first type.
     *
     * @param type2
     *        The second type.
     *
     * @return the type which includes the other.
     */
    public static DataType getInclusiveDataType(DataType type1, DataType type2) {
        if (type1 == type2)
            return type1;
        if (type1 == null)
            return type2;
        if (type2 == null)
            return type1;
        return type1.ordinal() > type2.ordinal() ? type1 : type2;
    }
}
