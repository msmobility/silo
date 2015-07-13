package com.pb.sawdust.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * The {@code ReaderInputStream} ...
 *
 * @author crf
 *         Started 12/8/11 8:43 AM
 */
public class ReaderInputStream extends InputStream {
    private final Reader reader;

    public ReaderInputStream(Reader reader) {
        this.reader = reader;
    }

    @Override
    public int read() throws IOException {
        return reader.read();
    }
}
