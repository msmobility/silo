package com.pb.sawdust.io;

import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The {@code ByteBufferInputStream} is a data input stream which allows the endianness of the input to be specified.
 *
 * @author crf <br/>
 *         Started Aug 21, 2010 5:42:29 PM
 */
public class ByteOrderDataInputStream extends FilterInputStream implements DataInput {
    private final ByteBuffer buffer;
    private byte[] backingBuffer;

    /**
     * Constructor specifying the input stream and its endianness.
     *
     * @param in
     *        The input stream.
     *
     * @param endianness
     *        The endianness of the stream.
     */
    public ByteOrderDataInputStream(InputStream in, ByteOrder endianness) {
        super(in);
        buffer = ByteBuffer.allocate(8).order(endianness);
        backingBuffer = buffer.array();
    }

    private void fillBuffer(int byteCount) throws IOException {
        buffer.rewind();
        if (in.read(backingBuffer,0,byteCount) < byteCount)
            throw new EOFException();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
	    readFully(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        int total = 0;
        int cur;
        while ((total<n) && ((cur = (int) in.skip(n-total)) > 0))
            total += cur;
	    return total;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        fillBuffer(1);
        return buffer.get();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xFF;
    }

    @Override
    public short readShort() throws IOException {
        fillBuffer(2);
        return buffer.getShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xFFFF;
    }

    @Override
    public char readChar() throws IOException {
        fillBuffer(2);
        return buffer.getChar();
    }

    @Override
    public int readInt() throws IOException {
        fillBuffer(4);
        return buffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
        fillBuffer(8);
        return buffer.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        fillBuffer(4);
        return buffer.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        fillBuffer(8);
        return buffer.getDouble();
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException(); //this is deprecated anyway
    }

    /**
     * {@inheritDoc}
     * Since the modified UTF-8 format specified by this method is byte-order independent, only the leading ({@code short})
     * string length specifier is affected by the endianness of the input stream.
     */
    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
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
