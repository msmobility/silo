package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code TranscadMatrixHeader} ...
 *
 * @author crf <br/>
 *         Started 5/3/11 9:32 AM
 */
class TranscadMatrixHeader {
    public static final int MAGIC_NUMBER = 0x0020041A;

    private boolean compressed;
    private String name;
    private int matrixCount;
    private int indexCount;
    private int rowCount;
    private int columnCount;
    private JavaType type;
    private byte dataWidth;
    private boolean rowMajor;
    private byte storageType; //00:memory based, 01:file based, 04:automatic
    private byte secondType; //00:memory and uncompressed, 02:file and uncompressed, 04: file and compressed
    private long matrixStartPosition;
    private long indexStartPosition;
    private int[] remainingPadding;

    public TranscadMatrixHeader(File transcadMatrixFile) {
        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = new ByteOrderDataInputStream(new BufferedInputStream(new FileInputStream(transcadMatrixFile)), ByteOrder.LITTLE_ENDIAN);
            readHeader(inputStream,transcadMatrixFile.getPath());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }


    private void readHeader(ByteOrderDataInputStream inputStream, String matrixFilePath) throws IOException {
        /*
        1A 04 20 00 - magic number?
        FA FF / FB FF  - compressed/not compressed
        4 x 16 = 64 8 bit (ascii) characters padded with 00 - title of matrix
        XX XX - number of matrices (little endian short)
        XX XX - number of indices (little endian short)
        XX XX XX XX - number of rows (little endian long)
        XX XX XX XX - number of cols (little endian long)
        XX - data type (01:short,02:long,03:float,04:double)
        XX - width of data type (in bytes)
        XX XX - ? (seems to always be 00 00)
        XX - storage type (00:row major,01:col major)
        XX - type (00:memory based, 01:file based, 04:automatic)
        XX - typing of some sort (00:memory and uncompressed, 02:file and uncompressed, 04: file and compressed)
        8 8 bit FF paddings
        [61h]
        8 8 bit values - (71 04 00 00 00 00 00 00) starting point in file of matrix entries (little endian long)
        8 8 bit values - starting point in file of index entries (little endian long), or 0 if compression used
        XX XX XX XX - number of matrices
        XX XX XX XX - number of indices
        63.5 x 16 = 1016 8 bit values (or 254 XX XX XX XX integers) - all 0s
            if compressed, then 25th byte is 01 (or 7th XX XX XX XX integer = 01 00 00 00)
         */

        if (inputStream.readInt() != MAGIC_NUMBER)
            throw new IllegalArgumentException("File is not supported TransCAD matrix file: " + matrixFilePath);

        short comp = inputStream.readShort();
        if (comp == (short) 0xFFFB)
            compressed = false;
        else if (comp == (short) 0xFFFA)
            compressed = true;
        else
            throw new IllegalStateException(String.format("Unrecognized compression status when reading %s: %d.",matrixFilePath,comp));

        name = TranscadMatrixUtil.readPaddedString(inputStream,64);

        matrixCount = inputStream.readShort();

        indexCount = inputStream.readShort();

        rowCount = inputStream.readInt();

        columnCount = inputStream.readInt();

        byte typeByte;
        switch (typeByte = (inputStream.readByte())) {
            case 0x01 : type = JavaType.SHORT; break;
            case 0x02 : type = JavaType.INT; break;
            case 0x03 : type = JavaType.FLOAT; break;
            case 0x04 : type = JavaType.DOUBLE; break;
            default : throw new IllegalArgumentException(String.format("Unexpected data type when reading %s: %d",matrixFilePath,typeByte));
        }

        dataWidth = inputStream.readByte();

        inputStream.readShort(); //ignored

        rowMajor = inputStream.readByte() == (byte) 0x00;

        storageType = inputStream.readByte();

        secondType = inputStream.readByte();

        inputStream.skipBytes(8);

        matrixStartPosition = inputStream.readLong();

        indexStartPosition = inputStream.readLong();

        remainingPadding = new int[((int) matrixStartPosition - 0x71) / 4];
        for (int i : range(remainingPadding.length))
            remainingPadding[i] = inputStream.readInt();
    }

    public boolean isCompressed() {
        return compressed;
    }

    public String getName() {
        return name;
    }

    public int getMatrixCount() {
        return matrixCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public JavaType getType() {
        return type;
    }

    public byte getDataWidth() {
        return dataWidth;
    }

    public boolean isRowMajor() {
        return rowMajor;
    }

    public byte getStorageType() {
        return storageType;
    }

    public byte getSecondType() {
        return secondType;
    }

    public long getMatrixStartPosition() {
        return matrixStartPosition;
    }

    public long getIndexStartPosition() {
        return indexStartPosition;
    }

    public int[] getRemainingPadding() {
        return ArrayUtil.copyArray(remainingPadding);
    }
}
