package com.pb.sawdust.tabledata.write;


import java.io.File;

/**
 * The {@code FileTableWriter} class provides a {@code TableWriter} framework for writing data tables to files.
 *
 * @author crf <br/>
 *         Started: Jul 25, 2008 7:19:38 AM
 */
public abstract class FileTableWriter implements TableWriter {
    /**
     * The file that a data table will be written to.
     */
    protected final File tableFile;

    /**
     * Cosntructor specifying the file to which the table data will be written.
     *
     * @param file
     *        The file to which the data table will be written.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code file} is not found or cannot be created.
     */
    public FileTableWriter(File file) {
        if (file.isDirectory())
            throw new IllegalArgumentException("FileTableWriter file cannot be a directory: " + file.getPath());
        tableFile = file;
    }

    /**
     * Constructor specifying the output file.
     *
     * @param tableFilePath
     *        The file that a data table sent through this writer will be written to.
     *
     * @throws IllegalArgumentException if {@code tableFilePath} is not a (real or potential) file.
     */
    public FileTableWriter(String tableFilePath) {
        this(new File(tableFilePath));
    }
}
