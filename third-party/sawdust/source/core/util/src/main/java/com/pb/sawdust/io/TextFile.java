package com.pb.sawdust.io;

import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The {@code TextFile} is a convenience class for reading and writing text files.
 * <p>
 * For reading, it implements {@code Iterable&lt;String&gt;}, providing an iterator over its lines (stripping off the
 * end-of-line characters). This iterator is provided via the {@link com.pb.sawdust.io.IterableFileReader} class.
 * The read mode is immune to the specified line separator: all valid end-of-line characters (<tt>\n</tt>, <tt>\r</tt>,
 * <tt>\r\n</tt>) are treated equally.
 * <p>
 * Additionally, a persistent read mode may be used, through which the {@code readLine()}, {@code readString(int)}, and
 * {@code skipChars(long)} methods may be used.  This mode is initiated automatically by calling either of these methods,
 * or explicitly through the {@code openForReading()} method. The file remains open for reading until the {@code close()}
 * method is called.  The {@code readAll()} method may be called at any time to read the rest of the file and close it
 * automatically.
 * <p>
 * For writing, three modes are offered:
 * <ul>
 *   <li>
 *     "auto flush" - (default) in which every write call opens the file, appends the text, and then closes the file
 *   </li>                                                                                                 
 *   <li>
 *     buffered - this is set by calling {@code setAutoFlush(false)}; in this mode, all write calls are added to an
 *     internal buffer which is only written out when {@code write()} is called or auto flush is set back to {@code true}
 *   </li>                                                                                                               
 *   <li>
 *     open mode - this is set by calling {@code openForWriting()}; this opens the file for writing (write calls send the
 *     text directly to the file), leaving it open until the {@code close()} method is called
 *   </li> 
 * </ul>
 * <p>
 * No checks are made to prevent a file opened for writing from being read from, or vice versa; system-specific issues
 * may cause exceptions to be thrown if reading and writing is performed simultaneously. Intermixing writing or reading
 * modes (simultaneously) may also cause problems and is generally not recommended. Attempting to open two line iterators
 * from the same {@code TextFile} (without closing them in-between) is explicitly forbidden and will throw an error.
 * <p>
 * Note that care should be taken when writing with some character sets, as this class's methods will often (depending on
 * the mode) open multiple writers during the writing process, which can result in errant bytes be entered into the
 * text. For example, with {@code UTF-16}, java will add a byte order mark at the beginning of the encoding; if more
 * than one writer is opened (subsequently or simultaneously) then more than one byte order mark may be inserted, effectively
 * corrupting the text. 
 *
 * @author crf <br/>
 *         Started Mar 9, 2010 8:57:48 PM
 */
public class TextFile implements Iterable<String>,RuntimeCloseable {
    private final File file;
    private Charset charset;
    private String lineSeparator;
    private boolean autoFlush = true;
    private boolean writeOpened = false;
    private boolean readOpened = false;
    private PrintWriter writer;
    private BufferedReader reader;

    private StringBuilder lastBuffer = new StringBuilder();
    private Deque<String> lines = new LinkedList<String>();

    private IterableFileReader.LineIterableReader lineReader = null;

    private static String getLineSeparator(File file) {
        return file.exists() ? FileUtil.getLineSeparator(file) : FileUtil.getLineSeparator();
    }

    private static String getLineSeparator(String file) {
        return getLineSeparator(new File(file));
    }

    /**
     * Constructor specifying the file, line separator, character set, and whether the file should be replaced or not.
     * 
     * @param file
     *        The text file.
     * 
     * @param lineSeparator
     *        The line separator for this text file.  
     * 
     * @param charset
     *        The character encoding to use with this file.
     * 
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(File file, String lineSeparator, Charset charset, boolean replace) {
        this.file = file;
        this.charset = charset;
        this.lineSeparator = lineSeparator;
        if (replace && file.exists())
            freshFile();
    }

    /**
     * Constructor specifying the file, line separator, character set, and whether the file should be replaced or not.
     * 
     * @param file
     *        The text file.
     * 
     * @param lineSeparator
     *        The line separator for this text file.  
     * 
     * @param charset
     *        The character encoding to use with this file.
     * 
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(String file, String lineSeparator, Charset charset, boolean replace) {
        this(new File(file),lineSeparator,charset,replace);
    }

    /**
     * Constructor specifying the file, line separator, and whether the file should be replaced or not. The default
     * character encoding will be used.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(File file, String lineSeparator, boolean replace) {
        this(file,lineSeparator,Charset.defaultCharset(),replace);
    }

    /**
     * Constructor specifying the file, line separator, and whether the file should be replaced or not. The default
     * character encoding will be used.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(String file, String lineSeparator, boolean replace) {
        this(file,lineSeparator,Charset.defaultCharset(),replace);
    }

    /**
     * Constructor specifying the file, character set, and whether the file should be replaced or not. The line separator
     * used will be that used in the file, or the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(File file, Charset charset, boolean replace) {
        this(file,getLineSeparator(file),charset,replace);
    }

    /**
     * Constructor specifying the file, character set, and whether the file should be replaced or not. The line separator
     * used will be that used in the file, or the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(String file, Charset charset, boolean replace) {
        this(file,getLineSeparator(file),charset,replace);
    }

    /**
     * Constructor specifying the file and whether the file should be replaced or not. The defalt character encoding will
     * be used, and the line separator used will be that used in the file, or the default line separator if the file does
     * not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(File file, boolean replace) {
        this(file,getLineSeparator(file),Charset.defaultCharset(),replace);
    }

    /**
     * Constructor specifying the file and whether the file should be replaced or not. The defalt character encoding will
     * be used, and the line separator used will be that used in the file, or the default line separator if the file does
     * not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param replace
     *        If {@code true}, the file will be replaced if it exists, if {@code false} it will be appended to. If the
     *        file does not exist, this parameter is ignored.
     */
    public TextFile(String file, boolean replace) {
        this(file,getLineSeparator(file),Charset.defaultCharset(),replace);
    }

    /**
     * Constructor specifying the file, line separator, character set. The file will be appended to if it exists.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     */
    public TextFile(File file, String lineSeparator, Charset charset) {
        this(file,lineSeparator,charset,false);
    }

    /**
     * Constructor specifying the file, line separator, character set. The file will be appended to if it exists.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     */
    public TextFile(String file, String lineSeparator, Charset charset) {
        this(file,lineSeparator,charset,false);
    }

    /**
     * Constructor specifying the file, line separator, character set. The file will be appended to if it exists, and the
     * default character encoding will be used.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     */
    public TextFile(File file, String lineSeparator) {
        this(file,lineSeparator,false);
    }

    /**
     * Constructor specifying the file, line separator, character set. The file will be appended to if it exists, and the
     * default character encoding will be used.
     *
     * @param file
     *        The text file.
     *
     * @param lineSeparator
     *        The line separator for this text file.
     */
    public TextFile(String file, String lineSeparator) {
        this(file,lineSeparator,false);
    }

    /**
     * Constructor specifying the file and character set. The file will be appended to if it exists and the line separator
     * used will be that used in the file, or the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     */
    public TextFile(File file, Charset charset) {
        this(file,charset,false);
    }

    /**
     * Constructor specifying the file and character set. The file will be appended to if it exists and the line separator
     * used will be that used in the file, or the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     *
     * @param charset
     *        The character encoding to use with this file.
     */
    public TextFile(String file, Charset charset) {
        this(file,charset,false);
    }

    /**
     * Constructor specifying the file and whether the file should be replaced or not. The file will be appended to if it
     * exists, the defalt character encoding will be used, and the line separator used will be that used in the file, or
     * the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     */
    public TextFile(File file) {
        this(file,false);
    }

    /**
     * Constructor specifying the file and whether the file should be replaced or not. The file will be appended to if it
     * exists, the defalt character encoding will be used, and the line separator used will be that used in the file, or
     * the default line separator if the file does not exist or only has one line.
     *
     * @param file
     *        The text file.
     */
    public TextFile(String file) {
        this(file,false);
    }

    /**
     * Set whether the text file should be in auto flush mode or not. If auto flush mode is on, then all write calls
     * will open the file, write the text and then close the file.  If auto flush is off, then write calls buffer
     * internally and will only be written when {@code write()} is called. The default is to set auto flush to {@code true}.
     *
     * @param autoFlush
     *        Whether auto flush should be on or off.
     */
    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
        //if turning on, write out any buffer
        write();
    }

    /**
     * Open the file for writing.  All write calls will be sent to the file to be written, though buffering may mean
     * that writes don't appear until {@code close()} is called.  To appropriately free up resources, {@code close()}
     * should be called when all desired write calls have been made.  Calling this method more than once without closing
     * in between will have no effect.
     */
    public void openForWriting() {
        writer = getWriter(true);
        writeOpened = true;
        autoFlush = true;
    }

    /**
     * Open the file for reading. To appropriately free up resources, {@code close()} should be called when all desired
     * read calls have been made.  Calling this method more than once without closing in between will have no effect.
     */
    public void openForReading() {
        if (!readOpened)
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
                readOpened = true;
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
    }

    /**
     * Close an opened text file. If the file has already been closed, this has no effect.  If the text file was opened
     * for writing, then this will flush its buffer and close the handle.  If this text file has an opened line iterator,
     * it will be closed.
     */
    public void close() {
        if (writeOpened) {
            writer.close();
            writeOpened = false;
        }
        if (readOpened) {
            try {
                reader.close();
                readOpened = false;
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
        if (lineReader != null && !lineReader.isClosed())
            lineReader.close();
    }

    /**
     * Write some text to the file.
     *
     * @param text
     *        The text to write to the file.
     */
    public void writeText(String text) {
        if (autoFlush)
            writeText(text,false);
        else
            lastBuffer.append(text);
    }

    /**
     * Write a line to the file.  The appropriate line separator will be appended to {@code line} when writing.
     *
     * @param line
     *        The line to write.
     */
    public void writeLine(String line) {
        if (autoFlush)
            writeText(line,true);
        else
            lines.add((lastBuffer.length() > 0 ? getLastBuffer() + line : line));
    }

    /**
     * Write a series of texts to the file.
     *
     * @param texts
     *        The texts to write to the file.
     */
    public void writeText(Collection<String> texts) {
        if (autoFlush)
            writeText(texts,false);
        else
            for (String text : texts)
                lastBuffer.append(text);
    }

    /**
     * Write a series of lines to the file.  The appropriate line separator will be appended to each line when writing.
     *
     * @param lines
     *        The lines to write.
     */
    public void writeLines(Collection<String> lines) {
        if (autoFlush) {
            writeText(lines,true);
        } else {
            if (lastBuffer.length() > 0) {
                for (String line : lines)
                    writeLine(line);
            } else {
                this.lines.addAll(lines);
            }
        }
    }

    public void writeLines(String ... lines) {
        writeLines(Arrays.asList(lines));
    }

    private String getLastBuffer() {
        String buffer = lastBuffer.toString();
        lastBuffer = new StringBuilder();
        return buffer;
    }

    /**
     * Write any text that has been buffered by {@code write...} calls.
     */
    public void write() {
        if (lines.size() > 0 || lastBuffer.length() > 0) {
            writeBufferedText();
        }
    }

    private PrintWriter getWriter(boolean append) {
        if (writeOpened)
            return writer;
        try {
            return LineSeparatorPrintWriter.lineSeparatorPrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append),charset)),lineSeparator);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void freshFile() {
        PrintWriter writer = null;
        try {
            writer = getWriter(false);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private void writeText(Collection<String> texts, final boolean writeLineSeparator) {
        PrintWriter writer = null;
        try {
            writer = getWriter(true);
            if (writeLineSeparator)
                for (String text : texts)
                    writer.println(text);
            else
                for (String text : texts)
                    writer.print(text);
        } finally {
            if (!writeOpened && writer != null)
                writer.close();
        }
    }

    private void writeText(String text, final boolean writeLineSeparator) {
        PrintWriter writer = null;
        try {
            writer = getWriter(true);
            if (writeLineSeparator)
                writer.println(text);
            else
                writer.print(text);
        } finally {
            if (!writeOpened && writer != null)
                writer.close();
        }
    }

    private void writeBufferedText() {
        PrintWriter writer = null;
        try {
            writer = getWriter(true);
            if (lines.size() > 0) {
                for (String line : lines)
                    writer.println(line);
                lines.clear();
            }
            if (lastBuffer.length() > 0)
                writer.print(getLastBuffer());
        } finally {
            if (!writeOpened && writer != null)
                writer.close();
        }
    }

    /**
     * Read a line of text. All valid end-of-line characters are consumed when reading the lines (this behavior is inherited
     * from {@code java.io.BufferedReader}; the returned line of text <i>will not</i> contain any end-of-line characters.
     *
     * @return a line of text, or {@code null} if the end of file has been reached.
     */
    public String readLine() {
        if (!readOpened)
            openForReading();
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Skip characters in the file.  If the file has not been opened for reading yet, it will be opened automatically,
     * and must be closed explicitly by calling {@code close()}.
     *
     * @param charsToSkip
     *        The number of characters in the file.
     */
    public void skipChars(long charsToSkip) {
        if (!readOpened)
            openForReading();
        try {
            reader.skip(charsToSkip);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Read a string of a given length from this file.  If the file has not been opened for reading yet, it will be opened
     * automatically, and must be closed explicitly by calling {@code close()}.  The returned string will be at most
     * the number of requested characters long; it will be shorter if the end of file is reached before the desired
     * string length is reached.
     *
     * @param chars
     *        The (maximum) number of characters to read.
     *
     * @return a string that is at most {@code chars} long, or {@code null} if the end of the file has been reached.
     */
    public String readString(int chars) {
        if (!readOpened)
            openForReading();
        try {
            char[] charArray = new char[chars];
            int length = reader.read(charArray,0,chars);
            return length == -1 ? null : new String(charArray,0,length);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Read the entire text file into a string.  If the file is already opened for reading (not including a line iterator),
     * this method will read the remaining unread text in the file.  The file will be closed for reading when this method
     * completes, so calling {@code close()} is not necessary.
     *
     * @return the text in this file, or the remaining unread text if it is already opened for reading.
     */
    public String readAll() {
        if (!readOpened)
            openForReading();
        try {
            char[] buffer = new char[1024];
            StringBuilder sb = new StringBuilder();
            int length;
            while ((length = reader.read(buffer)) > -1)
                sb.append(buffer,0,length);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (readOpened) {
                try {
                    reader.close();
                    readOpened = false;
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            }
        }
    }

    /**
     * Get an iterator which will iterate over the lines in this text file. The line separator specified when constructing
     * this instance is effectively ignored: all valid end-of-line characters are consumed when reading the lines (this
     * behavior is inherited from {@code java.io.BufferedReader}.  When the iterator is finished, the file handle will
     * automatically be closed. Calling {@code close()} before this iterator is finished will close the file handle
     * and (effectively) this iterator.  Thus, if an iterator is not to be used to completion, the {@code close()} should
     * be called to free the file resource.
     * <p>
     * Calling this method again before a previously created iterator has been closed will lead to an exception being thrown.
     *
     * @return an iterator iterating over the lines in this file.
     *
     * @throws IllegalStateException if an unclosed iterator from this file already exists.
     */
    public Iterator<String> iterator() {
        if (lineReader != null && !lineReader.isClosed())
            throw new IllegalStateException("Only one unclosed iterator may be created at once from a given TextFile instance: " + file);
        return (lineReader = IterableFileReader.getLineIterableFile(file,charset)).iterator();
    }
}
