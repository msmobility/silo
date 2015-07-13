package com.pb.sawdust.io;

import com.pb.sawdust.util.SystemType;
import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.charset.Charset;

/**
 * The {@code LineSeparatorPrintWriter} provides a {@code PrintWriter} whose line separator can be set. This avoids
 * the restriction of using the default (platform specific) line-separator as with {@code java.io.PrintWriter}. Instances
 * of this class are constructed through static factory methods so that checked expections can be wrapped as runtime
 * exceptions.
 *
 * @author crf <br/>
 *         Started Mar 14, 2010 7:11:57 PM
 */
public class LineSeparatorPrintWriter extends PrintWriter {
    private final String lineSeparator;
    private final boolean autoFlush;

    private LineSeparatorPrintWriter(Writer out, String lineSeparator) {
        super(out);
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    private LineSeparatorPrintWriter(Writer out, boolean autoFlush, String lineSeparator) {
        super(out,autoFlush);
        this.lineSeparator = lineSeparator;
        this.autoFlush = autoFlush;
    }

    private LineSeparatorPrintWriter(OutputStream out, String lineSeparator) {
        super(out);
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    private LineSeparatorPrintWriter(OutputStream out, boolean autoFlush, String lineSeparator) {
        super(out,autoFlush);
        this.lineSeparator = lineSeparator;
        this.autoFlush = autoFlush;
    }

    private LineSeparatorPrintWriter(String fileName, String lineSeparator) throws FileNotFoundException {
        super(fileName);
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    private LineSeparatorPrintWriter(String fileName, Charset charset, String lineSeparator) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName,charset.name());
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    private LineSeparatorPrintWriter(File file, String lineSeparator) throws FileNotFoundException {
        super(file);
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    private LineSeparatorPrintWriter(File file, Charset charset, String lineSeparator) throws FileNotFoundException, UnsupportedEncodingException {
        super(file,charset.name());
        this.lineSeparator = lineSeparator;
        autoFlush = false;
    }

    /**
     * Terminates the current line by writing the line separator string. The line separator is specified when constructing
     * this class and <i>is not</i> necessarily the default value as used by this class' parent.
     */
    public void println() {
	    try {
            synchronized (lock) {
            if (out == null)
	            throw new IOException("Stream closed");
            out.write(lineSeparator);
            if (autoFlush)
                out.flush();
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException x) {
            setError();
        }
    }
    
    /****************factory methods**************************/

    /**
     * Construct a writer without automatic line flushing.
     *
     * @param  out
     *         A character-output stream
     *
     * @param lineSeparator
     *        The line separator to use with the writer.
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(Writer out, String lineSeparator) {
        return new LineSeparatorPrintWriter(out,lineSeparator);
    }

    /**
     * Construct a writer specifying automatic line flushing.
     *
     * @param  out
     *         A character-output stream
     *
     * @param  autoFlush
     *         A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the
     *         output buffer
     *
     * @param lineSeparator
     *        The line separator to use with the writer.    
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(Writer out, boolean autoFlush, String lineSeparator) {
        return new LineSeparatorPrintWriter(out,autoFlush,lineSeparator);
    }

    /**
     * Construct a writer without automatic line flushing specifying the output stream to wrap.
     *
     * @param  out
     *         An output stream.
     *
     * @param lineSeparator
     *        The line separator to use with the writer.     
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(OutputStream out, String lineSeparator) {
        return new LineSeparatorPrintWriter(out,lineSeparator);
    }

    /**
     * Construct a writer specifying automatic line flushing and the output stream to wrap.
     *
     * @param  out
     *         An output stream.
     *
     * @param  autoFlush
     *         A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the
     *         output buffer
     *
     * @param lineSeparator
     *        The line separator to use with the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(OutputStream out, boolean autoFlush, String lineSeparator) {
        return new LineSeparatorPrintWriter(out,autoFlush,lineSeparator);
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to.
     *
     * @param  fileName
     *         The name of the file to use as the destination of the writer.
     *         If the file exists then it will be truncated to zero size;
     *         otherwise, a new file will be created.  The output will be
     *         written to the file and is buffered.
     *
     * @param lineSeparator
     *        The line separator to use with the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(String fileName, String lineSeparator) {
        try {
            return new LineSeparatorPrintWriter(fileName,lineSeparator);
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to and the character encoding
     * to use.
     *
     * @param  fileName
     *         The name of the file to use as the destination of the writer.
     *         If the file exists then it will be truncated to zero size;
     *         otherwise, a new file will be created.  The output will be
     *         written to the file and is buffered.
     *
     * @param  charset
     *         The name of the charset to use.
     *
     * @param lineSeparator
     *        The line separator to use with the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(String fileName, Charset charset, String lineSeparator) {
        try {
            return new LineSeparatorPrintWriter(fileName,charset,lineSeparator);
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to..
     *
     * @param  file
     *         The file to use as the destination of the writer.  If the file
     *         exists then it will be truncated to zero size; otherwise, a new
     *         file will be created.  The output will be written to the file
     *         and is buffered.
     *
     * @param lineSeparator
     *        The line separator to use with the writer.   
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(File file, String lineSeparator) {
        try {
            return new LineSeparatorPrintWriter(file,lineSeparator);
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     *Construct a writer without automatic line flushing specifying the file to write to and the character encoding
     * to use.
     *
     * @param  file
     *         The file to use as the destination of the writer.  If the file
     *         exists then it will be truncated to zero size; otherwise, a new
     *         file will be created.  The output will be written to the file
     *         and is buffered.
     *
     * @param  charset
     *         The charset to use.
     *
     * @param lineSeparator
     *        The line separator to use with the writer.
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(File file, Charset charset, String lineSeparator) {
        try {
            return new LineSeparatorPrintWriter(file,charset,lineSeparator);
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Construct a writer without automatic line flushing.
     *
     * @param  out
     *         A character-output stream
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(Writer out, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(out,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer specifying automatic line flushing.
     *
     * @param  out
     *         A character-output stream
     *
     * @param  autoFlush
     *         A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the
     *         output buffer
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer.  
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(Writer out, boolean autoFlush, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(out,autoFlush,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer without automatic line flushing specifying the output stream to wrap.
     *
     * @param  out
     *         An output stream.
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer.
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(OutputStream out, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(out,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer specifying automatic line flushing and the output stream to wrap.
     *
     * @param  out
     *         An output stream.
     *
     * @param  autoFlush
     *         A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the
     *         output buffer
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(OutputStream out, boolean autoFlush, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(out,autoFlush,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to.
     *
     * @param  fileName
     *         The name of the file to use as the destination of the writer.
     *         If the file exists then it will be truncated to zero size;
     *         otherwise, a new file will be created.  The output will be
     *         written to the file and is buffered.
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(String fileName, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(fileName,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to and the character encoding
     * to use.
     *
     * @param  fileName
     *         The name of the file to use as the destination of the writer.
     *         If the file exists then it will be truncated to zero size;
     *         otherwise, a new file will be created.  The output will be
     *         written to the file and is buffered.
     *
     * @param  charset
     *         The charset to use.
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(String fileName, Charset charset, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(fileName,charset,systemFamily.getLineSeparator());
    }

    /**
     * Construct a writer without automatic line flushing specifying the file to write to..
     *
     * @param  file
     *         The file to use as the destination of the writer.  If the file
     *         exists then it will be truncated to zero size; otherwise, a new
     *         file will be created.  The output will be written to the file
     *         and is buffered.
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer. 
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(File file, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(file,systemFamily.getLineSeparator());
    }

    /**
     *Construct a writer without automatic line flushing specifying the file to write to and the character encoding
     * to use.
     *
     * @param  file
     *         The file to use as the destination of the writer.  If the file
     *         exists then it will be truncated to zero size; otherwise, a new
     *         file will be created.  The output will be written to the file
     *         and is buffered.
     *
     * @param  charset
     *         The charset to use.
     *
     * @param systemFamily
     *        The operating system family whose line separator should be used for the writer.
     * 
     * @return a writer with the provided specifications.
     */
    public static LineSeparatorPrintWriter lineSeparatorPrintWriter(File file, Charset charset, SystemType.SystemFamily systemFamily) {
        return lineSeparatorPrintWriter(file,charset,systemFamily.getLineSeparator());
    }


















}
