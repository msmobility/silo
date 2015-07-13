package com.pb.sawdust.tabledata.sql.impl.derby;

import java.io.OutputStream;

/**
 * The {@code DerbyOutput} is a convenience class for controlling the Derby output/error log.
 *
 * @author crf <br/>
 *         Started 1/5/11 2:34 PM
 */
public class DerbyOutput {
    /**
     * The property specifying the output stream class used for the Derby log.
     */
    public static final String DERBY_ERROR_STREAM_PROPERTY = "derby.stream.error.field";

    /**
     * Null (non-functioning) output stream used to turn off Derby log.
     */
    public static final OutputStream DERBY_NULL = new OutputStream() {
        public void write(int b) { }
    };

    /**
     * Turn off the Derby log.
     */
    public static void turnOffDerbyLog() {
        setDerbyLogStream(DerbyOutput.class.getCanonicalName() + ".DERBY_NULL");
    }

    /**
     * Turn on the Derby log using the default output stream.
     */
    public static void turnOnDerbyLog() {
        setDerbyLogStream(null);
    }

    /**
     * Set the output stream instance to use for the Derby output log.
     *
     * @param outputStreamInstanceName
     *        The canonical name for the output stream to use for the log.
     */
    public static void setDerbyLogStream(String outputStreamInstanceName) {
        System.setProperty(DERBY_ERROR_STREAM_PROPERTY,outputStreamInstanceName);
    }

}
