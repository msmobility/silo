package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.io.ByteOrderDataOutputStream;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.ByteOrder;

/**
 * The {@code TranscadDataConverter} is a convenience class for converting Java data to Transcad binary format.  Since
 * {@code DataTable}s do not allow primitive null data, null Transcad field values are converted to a number.  By default,
 * this null conversion value is {@code 0} for integer types and {@code NaN}  for decimal types, but it can be changed if desired.
 *
 * @author crf <br/>
 *         Started Oct 30, 2010 11:06:44 PM
 */
public class TranscadBinaryDataConverter {

    /**
     * The byte value (unsigned in Transcad) used to represent a null value.
     */
    public static final byte TRANSCAD_NULL_BYTE = (byte) 0xFF; //255 - non-negative int

    /**
     * The short value used to represent a null value.
     */
    public static final short TRANSCAD_NULL_SHORT = (short) 0x8001;

    /**
     * The int value used to represent a null value.
     */
    public static final int TRANSCAD_NULL_INT = 0x80000001;

    /**
     * The float value used to represent a null value.
     */
    public static final float TRANSCAD_NULL_FLOAT = Float.intBitsToFloat(0xFF7FFFFF);

    /**
     * The double value used to represent a null value.
     */
    public static final double TRANSCAD_NULL_DOUBLE = Double.longBitsToDouble(0xFFEFFFFFFFFFFFFFL);

    /**
     * Convenience method to get an input stream that is appropriate for reading in Transcad fixed-format binary data.
     *
     * @param file
     *        The name of the file.
     *
     * @return a data input stream for {@code file} suitable for reading Transcad fixed-format binary data.
     */
    public ByteOrderDataInputStream getBinaryReader(File file) {
        try {
            return new ByteOrderDataInputStream(new BufferedInputStream(new FileInputStream(file)),ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e ) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Convenience method to get an input stream that is appropriate for writing out Transcad fixed-format binary data.
     *
     * @param file
     *        The name of the file.
     *
     * @return a data output stream for {@code file} suitable for writing Transcad fixed-format binary data.
     */
    public ByteOrderDataOutputStream getBinaryWriter(File file) {
        try {
            return new ByteOrderDataOutputStream(new BufferedOutputStream(new FileOutputStream(file)),ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e ) {
            throw new RuntimeIOException(e);
        }
    }

    private byte nullByteConversionValue = 0;
    private short nullShortConversionValue = 0;
    private int nullIntConversionValue = 0;    
    private float nullFloatConversionValue = Float.NaN;
    private double nullDoubleConversionValue = Double.NaN;
    
    short getByte(short n) {
        return n == TRANSCAD_NULL_BYTE ? nullByteConversionValue : n;
    }
    
    short getShort(short n) {
        return n == TRANSCAD_NULL_SHORT ? nullShortConversionValue : n;
    }                                  
    
    int getInt(int n) {
        return n == TRANSCAD_NULL_INT ? nullIntConversionValue : n;
    }
    
    float getFloat(float n) {
        return n == TRANSCAD_NULL_FLOAT ? nullFloatConversionValue : n;
    }
    
    double getDouble(double n) {
        return n == TRANSCAD_NULL_DOUBLE ? nullDoubleConversionValue : n;
    }

    /**
     * Read a byte value from a data input pointing to a Transcad fixed-format binary data source. Since bytes in Transcad
     * are unsigned, this returns the value as a short. If the value equals {@link #TRANSCAD_NULL_BYTE} then the value
     * will be converted using the appropriate null conversion.
     *
     * @param di
     *        The data input.
     *
     * @return an unsigned byte value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public short readByte(DataInput di) throws IOException {
        return getByte((short) di.readUnsignedByte());
    }

    /**
     * Read a short value from a data input pointing to a Transcad fixed-format binary data source. If the value equals
     * {@link #TRANSCAD_NULL_SHORT} then the value will be converted using the appropriate null conversion.
     *
     * @param di
     *        The data input.
     *
     * @return a short value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public short readShort(DataInput di) throws IOException {
        return getShort(di.readShort());
    }

    /**
     * Read a int value from a data input pointing to a Transcad fixed-format binary data source. If the value equals
     * {@link #TRANSCAD_NULL_INT} then the value will be converted using the appropriate null conversion.
     *
     * @param di
     *        The data input.
     *
     * @return a int value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public int readInt(DataInput di) throws IOException {
        return getInt(di.readInt());
    }

    /**
     * Read a float value from a data input pointing to a Transcad fixed-format binary data source. If the value equals
     * {@link #TRANSCAD_NULL_FLOAT} then the value will be converted using the appropriate null conversion.
     *
     * @param di
     *        The data input.
     *
     * @return a float value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public float readFloat(DataInput di) throws IOException {
        return getFloat(di.readFloat());
    }

    /**
     * Read a double value from a data input pointing to a Transcad fixed-format binary data source. If the value equals
     * {@link #TRANSCAD_NULL_DOUBLE} then the value will be converted using the appropriate null conversion.
     *
     * @param di
     *        The data input.
     *
     * @return a double value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public double readDouble(DataInput di) throws IOException {
        return getDouble(di.readDouble());
    }

    /**
     * Read a string with a specified length from a data input pointing to a Transcad fixed-format binary data source.
     * The returned string will not necessary be {@code length} characters in length, as right-side padding (space characters)
     * will be stripped off.
     *
     * @param di
     *        The data input.
     *
     * @param length
     *        The length of the string field.
     *
     * @return a string value.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public String readString(DataInput di, int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        int point = length;
        while (point-- > 0)
            sb.append((char) di.readByte());
        point = length-1;
        char space = ' ';
        while (sb.charAt(point) == space)
            if (point-- == 0)
                break;
        return sb.delete(point+1,length).toString();
    }

    /**
     * Read a value for a given data type from a data input pointing to a Transcad fixed-format binary data source.
     *
     * @param type
     *        The data type of the value to read in.
     *
     * @param di
     *        The data input.
     *
     * @param length
     *        The length of the value - this is ignored for all data types except {@code DataType.STRING}.
     *
     * @return a value appropriate for {@code type}.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalStateException if an invalid data type for Transcad is specified ({@code DataType.LONG} or {@code DataType.BOOLEAN}).
     */
    public Object readValue(DataType type, DataInput di, int length) throws IOException {
        switch (type) {
            case BYTE : return readByte(di);
            case SHORT : return readShort(di);
            case INT : return readInt(di);
            case FLOAT : return readFloat(di);
            case DOUBLE : return readDouble(di);
            case STRING : return readString(di,length);
            default : throw new IllegalStateException("Invalid data type for Transcad data: " + type);
        }
    }

    /**
     * Read a value for a given data type from a data input pointing to a Transcad fixed-format binary data source.  This
     * is a convenience version of {@link #readValue(com.pb.sawdust.tabledata.metadata.DataType, java.io.DataInput, int)}
     * for non-string data types.
     *
     * @param type
     *        The data type of the value to read in.
     *
     * @param di
     *        The data input.
     *
     * @return a value appropriate for {@code type}.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalStateException if an invalid data type for Transcad is specified ({@code DataType.LONG} or {@code DataType.BOOLEAN})
     *                               or if the data type is {@code DataType.STRING} (which must specify a field length).
     */
    public Object readValue(DataType type, DataInput di) throws IOException {
        if (type == DataType.STRING)
            throw new IllegalStateException("String data type length must be specified.");
        return readValue(type,di,-1);
    }

    /**
     * Turn off all null conversions. That is, pass the "null" TransCAD data values through as their numeric equivalents.
     */
    public void turnOffNullConversion() {
        setNullByteConversionValue(TRANSCAD_NULL_BYTE);
        setNullShortConversionValue(TRANSCAD_NULL_SHORT);
        setNullIntConversionValue(TRANSCAD_NULL_INT);
        setNullFloatConversionValue(TRANSCAD_NULL_FLOAT);
        setNullDoubleConversionValue(TRANSCAD_NULL_DOUBLE);
    }

    /**
     * Set the null conversion value for bytes. Any null bytes from a Transcad binary source by the methods in this class
     * will be converted to the value specified by this method.
     *
     * @param nullByteConversionValue
     *        The null conversion value.
     */
    public void setNullByteConversionValue(byte nullByteConversionValue) {
        this.nullByteConversionValue = nullByteConversionValue;
    }

    /**
     * Set the null conversion value for shorts. Any null shorts from a Transcad binary source by the methods in this class
     * will be converted to the value specified by this method.
     *
     * @param nullShortConversionValue
     *        The null conversion value.
     */
    public void setNullShortConversionValue(short nullShortConversionValue) {
        this.nullShortConversionValue = nullShortConversionValue;
    }

    /**
     * Set the null conversion value for ints. Any null ints from a Transcad binary source by the methods in this class
     * will be converted to the value specified by this method.
     *
     * @param nullIntConversionValue
     *        The null conversion value.
     */
    public void setNullIntConversionValue(int nullIntConversionValue) {
        this.nullIntConversionValue = nullIntConversionValue;
    }

    /**
     * Set the null conversion value for floats. Any null floats from a Transcad binary source by the methods in this class
     * will be converted to the value specified by this method.
     *
     * @param nullFloatConversionValue
     *        The null conversion value.
     */
    public void setNullFloatConversionValue(float nullFloatConversionValue) {
        this.nullFloatConversionValue = nullFloatConversionValue;
    }

    /**
     * Set the null conversion value for doubles. Any null doubles from a Transcad binary source by the methods in this class
     * will be converted to the value specified by this method.
     *
     * @param nullDoubleConversionValue
     *        The null conversion value.
     */
    public void setNullDoubleConversionValue(double nullDoubleConversionValue) {
        this.nullDoubleConversionValue = nullDoubleConversionValue;
    }

    /**
     * Write an (unsigned) byte value to a data output for Transcad fixed-format binary data. Because Transcad bytes
     * are unsigned, the value passed to this method is a short, however, it must be in the range {@code [0,255]}.
     *
     * @param value
     *        The value to write.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalArgumentException if {@code value < 0 || value > 255}.
     */
    public void writeByte(short value, DataOutput dout) throws IOException {
        if (value < 0 || value > 255)
            throw new IllegalArgumentException("Byte value in Transcad is unsigned and must be between 0 and 255: " + value);
        dout.writeByte(value);
    }

    /**
     * Write a short value to a data output for Transcad fixed-format binary data.
     *
     * @param value
     *        The value to write.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public void writeShort(short value, DataOutput dout) throws IOException {
        dout.writeShort(value);
    }

    /**
     * Write an int value to a data output for Transcad fixed-format binary data.
     *
     * @param value
     *        The value to write.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public void writeInt(int value, DataOutput dout) throws IOException {
        dout.writeInt(value);
    }

    /**
     * Write a float value to a data output for Transcad fixed-format binary data.
     *
     * @param value
     *        The value to write.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public void writeFloat(float value, DataOutput dout) throws IOException {
        dout.writeFloat(value);
    }

    /**
     * Write a double value to a data output for Transcad fixed-format binary data.
     *
     * @param value
     *        The value to write.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     */
    public void writeDouble(double value, DataOutput dout) throws IOException {
        dout.writeDouble(value);
    }

    private String pad(String value, int length) {
        int valueLength = value.length();
        if (valueLength == length)
            return value;
        StringBuilder sb = new StringBuilder(value);
        int spaces = length - valueLength;
        for (int i = 0; i < spaces; i++)
            sb.append(' ');
        return sb.toString();
    }

    /**
     * Write a string value to a data output for Transcad fixed-format binary data. If the specified length of the field
     * is larger than the string value, then the value is right-padded with spaces before writing.
     *
     * @param value
     *        The value to write.
     *
     * @param length
     *        The length of the field for {@code value}.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalArgumentException if the length of {@code value} is larger than {@code length}.
     */
    public void writeString(String value, int length, DataOutput dout) throws IOException {
        if (value.length() > length)
            throw new IllegalArgumentException(String.format("String value longer than specified length (%d): %s",length,value));
        for (char c : pad(value,length).toCharArray())
            dout.writeByte(c);
    }

    /**
     * Write a string value to a data output for Transcad fixed-format binary data. If the specified length of the field
     * is larger than the string value, then the value is right-padded with spaces before writing.  If {@code forced} is
     * {@code true}, then values greater than length will be truncated, otherwise an exception will be thrown.
     *
     * @param value
     *        The value to write.
     *
     * @param length
     *        The length of the field for {@code value}.
     *
     * @param dout
     *        The data output.
     *
     * @param forced
     *        If {@code true}, then {@code value} will be (right) truncated if it exceed {@code length} in length.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalArgumentException if the length of {@code value} is larger than {@code length} abd {@code forced == false}.
     */
    public void writeString(String value, int length, DataOutput dout, boolean forced) throws IOException {
        if (forced)
            writeString(value.length() > length ? value.substring(0,length) : value,length,dout);
        else
            writeString(value,length,dout);
    }

    /**
     * Write a value for a given data type to a data output for Transcad fixed-format binary data.
     *
     * @param value
     *        The value to write.
     *
     * @param type
     *        The data type of the value to read in.
     *
     * @param dout
     *        The data output.
     *
     * @param length
     *        The length of the value - this is ignored for all data types except {@code DataType.STRING}.
     *
     * @param forced
     *        If {@code true}, then a {@code String} {@code value} will be (right) truncated if it exceed {@code length} in length.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalStateException if an invalid data type for Transcad is specified ({@code DataType.LONG} or {@code DataType.BOOLEAN}).
     */
    public void writeValue(Object value, DataType type, DataOutput dout, int length, boolean forced) throws IOException {
        switch (type) {
            case BYTE : writeByte((Short) value,dout); break;
            case SHORT : writeShort((Short) value,dout); break;
            case INT : writeInt((Integer) value,dout); break;
            case FLOAT : writeFloat((Float) value,dout); break;
            case DOUBLE : writeDouble((Double) value,dout); break;
            case STRING : writeString((String) value,length,dout,forced); break;
            default : throw new IllegalStateException("Invalid data type for Transcad data: " + type);
        }
    }

    /**
     * Write a value for a given data type to a data output for Transcad fixed-format binary data. This
     * is a convenience version of {@link #writeValue(Object, com.pb.sawdust.tabledata.metadata.DataType, java.io.DataOutput, int, boolean)}
     * with {@code forced} set to {@code false}.
     *
     * @param value
     *        The value to write.
     *
     * @param type
     *        The data type of the value to read in.
     *
     * @param dout
     *        The data output.
     *
     * @param length
     *        The length of the value - this is ignored for all data types except {@code DataType.STRING}.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalStateException if an invalid data type for Transcad is specified ({@code DataType.LONG} or {@code DataType.BOOLEAN}).
     */
    public void writeValue(Object value, DataType type, DataOutput dout, int length) throws IOException {
        writeValue(value,type,dout,length,false);
    }

    /**
     * Write a value for a given data type to a data output for Transcad fixed-format binary data. This
     * is a convenience version of {@link #writeValue(Object, com.pb.sawdust.tabledata.metadata.DataType, java.io.DataOutput, int)}
     * for non-string data types.
     *
     * @param value
     *        The value to write.
     *
     * @param type
     *        The data type of the value to read in.
     *
     * @param dout
     *        The data output.
     *
     * @throws IOException if an i/o exception occurs.
     * @throws IllegalStateException if an invalid data type for Transcad is specified ({@code DataType.LONG} or {@code DataType.BOOLEAN}).
     *                               or if the data type is {@code DataType.STRING} (which must specify a field length).
     */
    public void writeValue(Object value, DataType type, DataOutput dout) throws IOException {
        if (type == DataType.STRING)
            throw new IllegalStateException("String data type length must be specified.");
        writeValue(value,type,dout,-1,false);
    }
    
}
