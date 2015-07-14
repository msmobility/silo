package com.pb.sawdust.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The {@code FlushingOutputStream} ...
 *
 * @author crf
 *         Started 12/8/11 8:34 AM
 */
public class FlushingOutputStream extends OutputStream {
        private final OutputStream stream;

        public FlushingOutputStream(OutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
            stream.flush();
        }
}
