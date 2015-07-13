package com.pb.sawdust.tabledata;

import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

/**
 * <p>
 * The {@code TableDataException} class provides a lightweight, non-checked exception specific to the {@code TableData}
 * and associated packages.  It can also be used to wrap checked exceptions in a runtime framework. It provides basic
 * constructors, as well as a message constructor which allows for formatted strings to be passed in, along with the
 * format parameters.  In addition, a few predefined messages are provided for convenience.
 * </p>
 * @author crf <br/>
 *         Started: May 12, 2008 8:10:42 PM
 */
public class TableDataException extends RuntimeWrappingException {
    private static final long serialVersionUID = -1069895096604022184L;

    /**
     * A column not found message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the column.
     */
    public static final String COLUMN_NOT_FOUND = "Column not found: %1$s";

    /**
     * A column index out of bounds message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the (int) index of the column.
     */
    public static final String COLUMN_INDEX_OUT_OF_BOUNDS = "Column index out of bounds: %1$d";

    /**
     * A column already exists message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the column.
     */
    public static final String COLUMN_ALREADY_EXISTS = "Column already exists: %1$s";

    /**
     * A row index out of bounds message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the (int) row of the column.
     */
    public static final String ROW_NUMBER_OUT_OF_BOUNDS = "Row number out of bounds: %1$d";

    /**
     * A data table not found not found message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the data table.
     */
    public static final String DATA_TABLE_NOT_FOUND = "Data table not found: %1$s";

    /**
     * A data table already exists message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the data table.
     */
    public static final String DATA_TABLE_ALREADY_EXISTS = "Data table already exists: %1$s";

    /**
     * An index not initialized yet message.
     */
    public static final String INDEX_NOT_INITIALIZED = "Index not built yet, call buildIndex before using this method.";

    /**
     * An index not found int table message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the index value.
     */
    public static final String INDEX_VALUE_NOT_FOUND = "Index value not found in table: %1$s";

    public static final String INVALID_INDEX_VALUE_TYPE = "Index value type invalid: found %1$s, expected %2$s";

    /**
     * A key column does not contain unique values message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the key column.
     */
    public static final String KEY_COLUMN_NOT_UNIQUE = "Key column must contain unique values: %1$s";

    /**
     * A cannot delete primary key column message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes one argument: the name of the primary key column that was to be deleted.
     */
    public static final String CANNOT_DELETE_PRIMARY_KEY_COLUMN = "Column used for primary key, cannot delete: %1$s";

    /**
     * An invalid data type message to be used with {@link TableDataException#TableDataException(String, Object[])}.
     * Takes two argument: the name of the entered class/type, and the name of the expected class/type. 
     */
    public static final String INVALID_DATA_TYPE = "Invalid data type: found %1$s, expected %2$s";


    /**
     * No argument constructor.
     */
    public TableDataException() {
        super();
    }     

    /**
     * Constructs a new runtime io exception with a new runtime exception (with the specified detail message and
     * cause) as the wrapped exception.
     *
     * @param message
     *        The detail message.
     *
     * @param cause
     *        The cause of the exception. (A {@code null} value is permitted, and indicates that the cause is
     *        nonexistent or unknown.)
     */
    public TableDataException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * Constructor with a defined cause.
     *
     * @param cause
     *        The cause of the exception.
     *
     * @param dummyForCause
     *        A dummy variable which is not used, but which differentiates this method from
              {@code TableDataException(Exception)}.
     */
    public TableDataException(Throwable cause, boolean dummyForCause) {
        super(cause,dummyForCause);
    }

    /**
     * Constructor with a message, which may include formatting information and parameters.  This method constructs
     * a message using {@code  String.format(message,args)}, so any formatting valid with that method will be used
     * here; likewise, any formatting errors associated with invalid parameters or incorrect parameter counts will
     * cause an exception to be thrown.
     *
     * @param message
     *        A message for the exception, with optional formatting information.
     *
     * @param args
     *        The formatting arguments.  Should only be included if {@code message} includes formatting information.
     *
     * @see  java.util.Formatter
     */
    public TableDataException(String message, Object ... args) {
        super(String.format(message,args));
    }

    /**
     * Constructor specifying the exception this exception should wrap.
     *
     * @param wrappedException
     *        The exception to wrap.
     */
    public TableDataException(Exception wrappedException) {
        super(wrappedException);
    }
}
