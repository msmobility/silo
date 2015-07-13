package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.tabledata.TableDataException;

import java.util.*;

/**
 * The {@code ReaderType} enum is used to hold information about all known {@code TableReader} classes. This
 * information includes what class the enum constant maps to, as well as what file extensions it is (usually)
 * appropriate to use the table reader type with.
 *
 * @author crf <br/>
 *         Started: Jul 8, 2008 11:03:00 AM
 */
public enum ReaderType {
    /**
     * Represents the {@link CsvTableReader} class.
     */
    CSV(CsvTableReader.class,"csv"),
    /**
     * Represents the {@link FixedWidthTextTableReader} class.
     */
    FIXED_WIDTH(FixedWidthTextTableReader.class,"txt"),
    /**
     * Represents the {@link WhitespaceDelimitedTextTableReader} class.
     */
    WHITESPACE_DELIMITED(WhitespaceDelimitedTextTableReader.class),
    /**
     * Represents the {@link DbfTableReader} class.
     */
    DBF(DbfTableReader.class,"dbf");

    private Class<? extends TableReader> tableReaderClass;
    private Set<String> fileExtensions;
    private static Map<String, ReaderType> extensionMap = null;

    private ReaderType(Class<? extends TableReader> tableReaderClass,String ... extensions) {
        this.tableReaderClass = tableReaderClass;
        fileExtensions = new HashSet<String>();
        for (String extension : extensions)
            fileExtensions.add(extension.toLowerCase());
    }

    /**
     * The the {@code TableReader} class this enum constant refers to.
     *
     * @return the table reader class corresponding to this enum constant.
     */
    public Class<? extends TableReader> getTableReaderClass() {
        return tableReaderClass;
    }

    /**
     * Get the file extensions for files which this enum constant's table reader can commonly read.
     *
     * @return a set of file extensions which indicate files the table reader can commonly read successfully.
     */
    public Set<String> getFileExtensions() {
        return Collections.unmodifiableSet(fileExtensions);
    }

    /**
     * Infer the appropriate reader type to use with the specified file extension. This method checks to see if the
     * extension exists in any of the {@code ReaderType}'s commonly used file types, and return the type that can
     * use the file type. If no reader is found which is known to work with the file type, {@code null} will be
     * returned.
     *
     * @param fileExtension
     *        The file extension in question.
     *
     * @return the reader type which was deemed appropriate for use with {@code fileExtension}, or {@code null} if
     *         none was found.
     */
    public static ReaderType getReaderTypeFromExtension(String fileExtension) {
        if (extensionMap == null) {
            extensionMap = new HashMap<String, ReaderType>();
            for(ReaderType readerType : values())
                for (String extension : readerType.fileExtensions)
                    extensionMap.put(extension,readerType);
        }
        return extensionMap.get(fileExtension.toLowerCase());
    }

    /**
     * Infer the appropriate reader type to use with the specified file. The {@code ReaderType} class holds a list of
     * possible file extensions mapping to each {@code ReaderType}, and this mapping is used by this method to determine
     * the reader type to use.
     *
     * @param filePath
     *        The path to the data file; this must have an extension, as this is used to determine the reader type.
     *
     * @return the reader type which was deemed appropriate for use with {@code file}.
     *
     * @throws IllegalArgumentException if a file extension was not found.
     * @throws TableDataException if no known mapping from {@code file}'s extension to a table reader type is known.
     */
    public static ReaderType getReaderType(String filePath) {
        //find extension
        int dotLocation = filePath.lastIndexOf('.');
        if (dotLocation == -1)
            throw new TableDataException("File extension not found, cannot determine reader type: " + filePath);
        String extension = filePath.substring(dotLocation + 1);
        ReaderType readerType = getReaderTypeFromExtension(extension);
        if (readerType == null)
            throw new TableDataException("File extension unknown, reader type cannot be determined: " + extension);
        return readerType;
    }
}
