package com.pb.sawdust.io;

import com.pb.sawdust.util.array.ArrayUtil;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code LineEntryInputStream} is a convenience class for constructing an input stream on a series of text "lines."
 * The constructor takes a list of lines (without a line-separator) and will build input stream with the appropriate line-separator
 * ending each line.
 *
 * @author crf <br/>
 *         Started 7/20/11 8:16 AM
 */
public class LineEntryInputStream extends ByteArrayInputStream {
    private static byte[] linesToBytes(List<String> lines) {
        List<Byte> bytes = new LinkedList<Byte>();
        for (String line : lines) {
            for (byte b : line.getBytes())
                bytes.add(b);
            for (byte b : FileUtil.getLineSeparator().getBytes())
                bytes.add(b);
        }
        return ArrayUtil.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }


    /**
     * Constructor specifying the lines to build the input stream from. The line-separators will be added automatically,
     * so they should be omitted from the input list.
     *
     * @param lines
     *        The lines to build the input stream from.
     */
    public LineEntryInputStream(List<String> lines) {
        super(linesToBytes(lines));
    }
}
