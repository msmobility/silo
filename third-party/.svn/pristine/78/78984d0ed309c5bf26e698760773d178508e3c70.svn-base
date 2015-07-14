package com.pb.sawdust.io;

import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The {@code ByteOrderDataOutputStream} is a data output stream which allows the endianness of the output to be specified.
 *
 * @author crf <br/>
 *         Started Nov 1, 2010 7:19:22 AM
 */
public class ByteOrderDataOutputStream extends FilterOutputStream implements DataOutput {
    private final ByteBuffer buffer;
    private byte[] backingBuffer;

    /**
     * Constructor specifying the output stream and its endianness.
     *
     * @param out
     *        The output stream.
     *
     * @param endianness
     *        The endianness of the stream.
     */
    public ByteOrderDataOutputStream(OutputStream out, ByteOrder endianness) {
        super(out);
        buffer = ByteBuffer.allocate(8).order(endianness);
        backingBuffer = buffer.array();
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        writeByte(v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        buffer.rewind();
        buffer.put((byte) v);
        out.write(backingBuffer,0,1);
    }

    @Override
    public void writeShort(int v) throws IOException {
        buffer.rewind();
        buffer.putShort((short) v);
        out.write(backingBuffer,0,2);
    }

    @Override
    public void writeChar(int v) throws IOException {
        writeChar((short) v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        buffer.rewind();
        buffer.putInt(v);
        out.write(backingBuffer,0,4);
    }

    @Override
    public void writeLong(long v) throws IOException {
        buffer.rewind();
        buffer.putLong(v);
        out.write(backingBuffer,0,8);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        buffer.rewind();
        buffer.putFloat(v);
        out.write(backingBuffer,0,4);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        buffer.rewind();
        buffer.putDouble(v);
        out.write(backingBuffer,0,8);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        for (char c : s.toCharArray())
            writeByte(c);
    }

    @Override
    public void writeChars(String s) throws IOException {
        for (char c : s.toCharArray())
            writeChar(c);
    }

    private static final int MAX_UTF_LENGTH = Short.MAX_VALUE-Short.MIN_VALUE; //maximum number of positive 16 bit values

    /**
     * {@inheritDoc}
     * Since the modified UTF-8 format specified by this method is byte-order independent, only the leading ({@code short})
     * string length specifier is affected by the endianness of the output stream.
     */
    @Override
    public void writeUTF(String s) throws IOException {
        int length = 0;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c >= '\u0001' && c <= '\u007F')
                length += 1;
            else if (c >= '\u07FF')
                length += 3;
            else
                length += 2;
        }

        if (length > MAX_UTF_LENGTH)
            throw new UTFDataFormatException("String too long for UTF: " + length);

        byte[] buffer = new byte[length];
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c >= '\u0001' && c <= '\u007F') {
                buffer[count++] = (byte) c;
            } else if (c > '\u0001') {
                buffer[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buffer[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                buffer[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                buffer[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                buffer[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }

        writeShort(length);
        out.write(buffer,0,length);
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
