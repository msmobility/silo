package com.pb.sawdust.io;


import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;

/**
 * The {@code ZipFile} class provides a convenience class for dealing with zip files.
 * <p>
 * For writing/modifying zip files, the various {@code add} methods and {@code deleteEntry(String)} methods are used.
 * These only queue changes to the zip file; the changes are only written to disk when the {@link #write()} method is
 * called.
 * <p>
 * For reading zip files, the various {@code extract} methods are used.  It is important to note that extracting data
 * only works from for the data on disk; if changes to the zip file have been queued but not written the extract methods
 * will not be able to access these changes.
 *
 * @author crf <br/>
 *         Started: Oct 29, 2009 8:12:54 AM
 */
public class ZipFile implements Iterable<String> { //iterates over entry names
    /**
     * The standard zip file extension.
     */
    public static final String ZIP_EXTENSION = "zip";

    /**
     * The key used for files in the map returned by {@link #getEntryList()}.
     */
    public static final String LIST_FILE_KEY = "files";

    /**
     * The key used for directories in the map returned by {@link #getEntryList()}.
     */
    public static final String LIST_DIRECTORY_KEY = "directories";

    private final File zipFile;
    private boolean replace;

    //for writing
    private Map<String,ZipEntrySource> sourcesToBeWritten = new HashMap<String,ZipEntrySource>();
    private Set<String> sourcesToDelete = new HashSet<String>();

    /**
     * Constructor specifying the zip file and whether it should be replaced or appended to.
     *
     * @param zipFile
     *        The zip file.  It does not need to exist.
     *
     * @param replace
     *        If {@code true} then the zip file will be replaced, otherwise it will be appended to.  If {@code zipFile}
     *        doesn't exist, then this parameter has no effect.
     *
     * @throws IllegalArgumentException if {@code zipFile} exists and is not a file.
     */
    public ZipFile(File zipFile, boolean replace) {
        this.zipFile = zipFile;
        this.replace = replace;
        if (zipFile.exists())
            if (!zipFile.isFile())
                throw new IllegalArgumentException("Zip file must be a file: " + zipFile.getPath());
            else if (!replace)
                loadExistingZipEntries(); //maybe loading existing zip info should be lazy?  check performance
    }

    /**
     * Constructor specifying the zip file and whether it should be replaced or appended to.
     *
     * @param zipFile
     *        The zip file.  It does not need to exist.
     *
     * @param replace
     *        If {@code true} then the zip file will be replaced, otherwise it will be appended to.  If {@code zipFile}
     *        doesn't exist, then this parameter has no effect.
     *
     * @throws IllegalArgumentException if {@code zipFile} exists and is not a file.
     */
    public ZipFile(String zipFile, boolean replace) {
        this(new File(zipFile),replace);
    }

    /**
     * Constructor specifying the zip file, appending to it already exists.
     *
     * @param zipFile
     *        The zip file.  It does not need to exist.
     *
     * @throws IllegalArgumentException if {@code zipFile} exists and is not a file.
     */
    public ZipFile(File zipFile) {
        this(zipFile,false);
    }

    /**
     * Constructor specifying the zip file, appending to it already exists.
     *
     * @param zipFile
     *        The zip file.  It does not need to exist.
     *
     * @throws IllegalArgumentException if {@code zipFile} exists and is not a file.
     */
    public ZipFile(String zipFile) {
        this(new File(zipFile));
    }

    /* ******************adding data to zip file************************* */

    private String normalizeEntryName(String name) {
        //simple todo: check about adding directory
        //return new File(name).getPath().replace(File.pathSeparatorChar,'/');
        return new File(name).getPath().replace('\\','/');
    }

    private void addEntryInternal(String name, ZipEntrySource source, boolean addParentDirectories) {
        if (source == null && sourcesToBeWritten.containsKey(name))
            return; //already contains entry, and should, in theory, contain subentries
//        if (!replace && sourcesToBeWritten.containsKey(name))
//            throw new IllegalArgumentException("Cannot replace already existing zip entry source unless replace is set to true: " + name);
        if (sourcesToDelete.contains(name))
            sourcesToDelete.remove(name); //if adding after deleting, then want to add instead of delete
        sourcesToBeWritten.put(name,source);
        if (!addParentDirectories)
            return;
        int dirInd = name.lastIndexOf('/',name.length()-2);
        if (dirInd > -1)
            addEntryInternal(name.substring(0,dirInd+1),null,addParentDirectories);
    }

    /**
     * Add an entry to this zip file. If the entry name contains directories (denoted by "<code>/</code>" characters)
     * then those directories will be recursively added (as needed) as directory entries.
     *
     * @param name
     *        The name of the entry.
     *
     * @param source
     *        The source for accessing the entry data.
     */
    public void addEntry(String name, ZipEntrySource source) {
        addEntryInternal(normalizeEntryName(name),source,true);
    }

    /**
     * Add an entry containing string data to the zip file. If the entry name contains directories (denoted by "<code>/</code>" characters)
     * then those directories will be recursively added (as needed) as directory entries.
     *
     * @param name
     *        The name of the entry.
     *
     * @param data
     *        The data the entry will hold.
     *
     * @param charset
     *        The character set that {@code data} will be encoded in.
     */
    public void addEntry(String name, String data, Charset charset) {
        addEntry(name,data.getBytes(charset)); //puts string as raw byte data
    }

    /**
     * Add an entry containing string data to the zip file using default character encoding. If the entry name contains
     * directories (denoted by "<code>/</code>" characters) then those directories will be recursively added (as needed)
     * as directory entries.
     *
     * @param name
     *        The name of the entry.
     *
     * @param data
     *        The data the entry will hold.
     */
    public void addEntry(String name, String data) {
        addEntry(name,data.getBytes()); //puts string as raw byte data
    }

    /**
     * Add an entry containing raw byte data to the zip file. If the entry name contains directories (denoted by "<code>/</code>" characters)
     * then those directories will be recursively added (as needed) as directory entries.
     *
     * @param name
     *        The name of the entry.
     *
     * @param data
     *        The data the entry will hold.
     */
    public void addEntry(String name, final byte[] data) {
        addEntry(name,new ZipEntrySource() {
            public void writeData(OutputStream os) {
                try {
                    os.write(data);
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            }

            public void close() {}
        });
    }

    /* ************files and directories**************** */

    /**
     * Add a directory entry to the zip file. If the entry name contains sub-directories (denoted by "<code>/</code>" characters)
     * then those directories will be recursively added (as needed) as directory entries.
     *
     * @param name
     *        The (directory) name of the entry.
     */
    public void addDirectoryByName(String name) {
        String nname = normalizeEntryName(name);
        if (!nname.endsWith("/"))
            nname += "/";
        addEntryInternal(nname,null,true);
    }

    /**
     * Add an existing file to the zip file. All of the directories in the file will be recursively added, as needed,
     * as directory entries. The base path will be used to resolve the file location, but none of its directories will
     * be added or used in the the zip file entries.
     *
     * @param file
     *        The file to add to the zip file (this should be relative, including only those directories which also are
     *        to be included in the zip file).
     *
     * @param basePath
     *        The base path of the file.  The file location should be representable by {@code new File(basePath,file.getPath())}.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFile(File file, File basePath) {
        File f = new File(basePath,file.getPath());
        if (!f.exists())
            throw new IllegalArgumentException("File not found: " + f.getPath());
        String fs = normalizeEntryName(file.getPath());
        if (f.isDirectory() && !fs.endsWith("/"))
            fs += "/";
        addEntry(fs,new FileZipEntrySource(f));
    }

    /**
     * Add an existing file to the zip file. All of the directories in the file will be recursively added, as needed,
     * as directory entries. The base path will be used to resolve the file location, but none of its directories will
     * be added or used in the the zip file entries.
     *
     * @param file
     *        The file to add to the zip file (this should be relative, including only those directories which also are
     *        to be included in the zip file).
     *
     * @param basePath
     *        The base path of the file.  The file location should be representable by {@code new File(basePath,file)}.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFile(String file, String basePath) {
        addFile(new File(file), new File(basePath));
    }

    /**
     * Convenience method for adding a file to a zip file. This method will exclude all directories from being added to
     * the zip file; the zip entry name will be just {@code file.getName()}.
     *
     * @param file
     *        The file to add to the zip file.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFile(File file) {
        addFile(file.getName(),file.getParent());
    }

    /**
     * Convenience method for adding a file to a zip file. This method will exclude all directories from being added to
     * the zip file; the zip entry name will be just {@code new File(file).getName()}.
     *
     * @param file
     *        The file to add to the zip file.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFile(String file) {
        addFile(new File(file));
    }

    /**
     * Add a file to a zip file with an entry indicating a different file path.  That is, the entry will use the combination
     * of {@code directory} and {@code file.getname()} for its name, but {@code file} for its data.  The directories
     * specified by {@code directory} will be added recursively, as needed, as directory entries.
     *
     * @param file
     *        The file whose data will be added to the entry.
     *
     * @param directory
     *        The directory within which the file will be placed in the zip file.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFileInDirectory(File file, File directory) {
        if (!file.exists())
            throw new IllegalArgumentException("File not found: " + file.getPath());
        String fs = normalizeEntryName(new File(directory,file.getName()).getPath());
        addEntry(fs,new FileZipEntrySource(file));
    }

    /**
     * Add a file to this zip file with an entry indicating a different file path.  That is, the entry will use the combination
     * of {@code directory} and {@code new File(file).getname()} for its name, but {@code file} for its data.  The directories
     * specified by {@code directory} will be added recursively, as needed, as directory entries.
     *
     * @param file
     *        The file whose data will be added to the entry.
     *
     * @param directory
     *        The directory within which the file will be placed in the zip file.
     *
     * @throws IllegalArgumentException if {@code file} does not exists.
     */
    public void addFileInDirectory(String file, String directory) {
        addFileInDirectory(new File(file),new File(directory));
    }

    /**
     * Add an entire directory tree to this zip file.  All sub-files/directories not excluded by the filter will be added.
     * All sub-directories in {@code directory} will be recursively added, as needed, as directory entries. The base path
     * is used to resolve the absolute path of the starting directory, but will not be used when naming the zip entries.
     * To add a directory that does not exist, use {@link #addDirectoryByName(String)}.
     *
     * @param directory
     *        The directory to add.
     *
     * @param basePath
     *        The base path of the directory.  The directory location should be representable by {@code new File(basePath,directory.getPath())}.
     *
     * @param filter
     *        The filter used to exclude entries from the zip file.
     *
     * @throws IllegalArgumentException if {@code directory} does not exists, or is not a directory.
     */
    public void addDirectory(File directory, File basePath, FileFilter filter) {
        File dir = new File(basePath,directory.getPath());
        if (!dir.exists())
            throw new IllegalArgumentException("Directory not found: " + dir.getPath());
        if (!dir.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + dir.getPath());
        addDirectoryByName(directory.getPath());
        for (File f : dir.listFiles(filter)) //null is for file, shouldn't be a file
            if (f.isDirectory())
                addDirectory(new File(directory,f.getName()),basePath,filter);
            else
                addFile(new File(directory,f.getName()),basePath);
    }

    /**
     * Add an entire directory tree to this zip file.  All sub-files/directories not excluded by the filter will be added.
     * All sub-directories in {@code directory} will be recursively added, as needed, as directory entries. The base path
     * is used to resolve the absolute path of the starting directory, but will not be used when naming the zip entries.
     * To add a directory that does not exist, use {@link #addDirectoryByName(String)}.
     *
     * @param directory
     *        The directory to add.
     *
     * @param basePath
     *        The base path of the directory.  The directory location should be representable by {@code new File(basePath,directory)}.
     *
     * @param filter
     *        The filter used to exclude entries from the zip file.
     *
     * @throws IllegalArgumentException if {@code directory} does not exists, or is not a directory.
     */
    public void addDirectory(String directory, String basePath, FileFilter filter) {
        addDirectory(new File(directory),new File(basePath),filter);
    }

    /**
     * Add an entire directory tree to this zip file.  All sub-files/directories will be added.  All sub-directories
     * in {@code directory} will be recursively added, as needed, as directory entries. The base path is used to resolve
     * the absolute path of the starting directory, but will not be used when naming the zip entries. To add a directory
     * that does not exist, use {@link #addDirectoryByName(String)}.
     *
     * @param directory
     *        The directory to add.
     *
     * @param basePath
     *        The base path of the directory.  The directory location should be representable by {@code new File(basePath,directory.getPath())}.
     *
     * @throws IllegalArgumentException if {@code directory} does not exists, or is not a directory.
     */
    public void addDirectory(File directory, File basePath) {
        addDirectory(directory,basePath,FileUtil.getTransparentFileFilter());
    }

    /**
     * Add an entire directory tree to this zip file.  All sub-files/directories will be added.  All sub-directories
     * in {@code directory} will be recursively added, as needed, as directory entries. The base path is used to resolve
     * the absolute path of the starting directory, but will not be used when naming the zip entries. To add a directory
     * that does not exist, use {@link #addDirectoryByName(String)}.
     *
     * @param directory
     *        The directory to add.
     *
     * @param basePath
     *        The base path of the directory.  The directory location should be representable by {@code new File(basePath,directory.getPath())}.
     *
     * @throws IllegalArgumentException if {@code directory} does not exists, or is not a directory.
     */
    public void addDirectory(String directory, String basePath) {
        addDirectory(directory,basePath,FileUtil.getTransparentFileFilter());
    }

    /**********************reading zip file****************************/

    private void loadExistingZipEntries() {
        if (replace)
            return;  //nothing exists, because we'll replace them
        sourcesToBeWritten.clear();
        sourcesToDelete.clear();
        java.util.zip.ZipFile zf = null;
        try {
            zf = new java.util.zip.ZipFile(zipFile);
            Enumeration<? extends ZipEntry> zee = zf.entries();
            while (zee.hasMoreElements()) {
                ZipEntry ze = zee.nextElement();
                addEntryInternal(ze.getName(),new DeferredZipFileZipEntrySource(ze.getName()),false);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e) {
                    //swallow
                }
            }
        }
    }

    /**
     * Delete an entry from the zip file.
     *
     * @param name
     *        The entry to delete.
     *
     * @throws IllegalArgumentException if {@code entry} is not in the zip file (as it exists in this class, not necessarily
     *                                  on disk).
     */
    public void deleteEntry(String name) {
        if (!sourcesToBeWritten.containsKey(name))
            throw new IllegalArgumentException("Entry to delete does not exist: " + name);
        sourcesToDelete.add(name);
    }

    /**
     * Get the entries in this zip file.  The entries will be listed as the zip file exists in this instance, not necessarily
     * on disk (the file on disk will not reflect this instance until the {@code write()} method is called.  The returned
     * map will have a seperate key-value pair for files and directories, using the {@link #LIST_FILE_KEY} and
     * {@link #LIST_DIRECTORY_KEY} respectively.
     *
     * @return a map containing the entries of this zip file.
     */
    public Map<String,Set<String>> getEntryList() {
        Set<String> files = new HashSet<String>();
        Set<String> dirs = new HashSet<String>();
        for (String source : sourcesToBeWritten.keySet())
            if (!sourcesToDelete.contains(source))
                if (source.endsWith("/"))
                    dirs.add(source);
                else
                    files.add(source);
        Map<String,Set<String>> entries = new HashMap<String,Set<String>>();
        entries.put(LIST_FILE_KEY,files);
        entries.put(LIST_DIRECTORY_KEY,dirs);
        return entries;
    }

    /**
     * Write the zip file to disk. If the zip file already exists, then this method will write a temporary file, then
     * replace the original (so that the original is not lost if there is an error writing).  It is important to note
     * that once this method is called, this {@code ZipFile} instance will be placed into "append" mode, even if it was
     * constructed in "replace" mode.  To replace the zip file, a replacement instance must be instantiated.
     */
    public void write() {
        if (zipFile.exists() && !replace) {
            Random random = new Random();
            //create randomized temp file for zipping into
            File tmpFile = new File(zipFile.getPath() + "." + random.nextInt() + ".zip");
            while (tmpFile.exists())
                tmpFile = new File(zipFile.getPath() + "." + random.nextInt() + ".zip");
            ZipFile tempZipFile = new ZipFile(tmpFile);
            tempZipFile.sourcesToBeWritten = sourcesToBeWritten;
            tempZipFile.sourcesToDelete = sourcesToDelete;
            tempZipFile.write();
            zipFile.delete();
            tmpFile.renameTo(zipFile);
        } else {
            ZipOutputStream zos = null;
            try {
                zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
                for (String entryName : sourcesToBeWritten.keySet()) {
                    if (!sourcesToDelete.contains(entryName)) {
                        zos.putNextEntry(new ZipEntry(entryName));
                        ZipEntrySource source = sourcesToBeWritten.get(entryName);
                        if (source != null) {
                            //not a directory, so write the data
                            try {
                                source.writeData(zos);
                            } finally {
                                source.close();
                            }
                        }
                        zos.closeEntry();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            } finally {
                if (zos != null)
                    try {
                        zos.close();
                    } catch (IOException e) {
                        //swallow
                    }
            }
        }
        replace = false;
        loadExistingZipEntries();
    }

    private java.util.zip.ZipFile getZipFile() {
        try {
            return new java.util.zip.ZipFile(zipFile);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void checkIfEntryExists(String entry) {
        if (!sourcesToBeWritten.containsKey(entry)) // || !(sourcesToBeWritten.get(entry) instanceof ZipFileZipEntrySource))
            throw new IllegalArgumentException("Entry not found in zip file: " + entry);
    }

    /**
     * Extract an entry from this zip file to disk. If the entry has directories (denoted by "<code>/</code>" characters),
     * then they will be created before extracting the file, even if they are not explicitly listed in the zip file as
     * directory entries.
     *
     * @param entry
     *        The entry to extract.
     *
     * @param location
     *        The location to extract the entry to.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public void extract(String entry, File location) {
        java.util.zip.ZipFile zf = null;
        try {
            extract(entry,location,zf = getZipFile());
        } finally {
            if (zf != null) // && !isOpen())
                try {
                    zf.close();
                } catch (IOException e) {
                    //swallow
                }
        }
    }

    /**
     * Extract an entry from this zip file to disk. If the entry has directories (denoted by "<code>/</code>" characters),
     * then they will be created before extracting the file, even if they are not explicitly listed in the zip file as
     * directory entries.
     *
     * @param entry
     *        The entry to extract.
     *
     * @param location
     *        The location to extract the entry to.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public void extract(String entry, String location) {
        extract(entry,new File(location));
    }

    //will build directory structure as necessary
    private void extract(String entry, File location, java.util.zip.ZipFile zf) {
        if (!location.exists())
            throw new IllegalArgumentException("Location does not exist: " + location);
        checkIfEntryExists(entry);
        File end = new File(location,entry);
        if (entry.endsWith("/")) { // a directory - recursively create
            end.mkdirs();
            return;
        } else if (entry.contains("/") && !end.getParentFile().exists()) { //contains directories that need to be created
            end.getParentFile().mkdirs();
        }

        OutputStream os = null;
        try {
            extract(entry,os = new FileOutputStream(new File(location,entry)),zf);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    //swallow
                }
        }
    }

    /**
     * Extract all of the contents of this zip file to disk.
     *
     * @param location
     *        The location to extract the entries to.
     */
    public void extract(File location) {
        extract(getOrderedEntryList(),location);
    }

    /**
     * Extract all of the contents of this zip file to disk.
     *
     * @param location
     *        The location to extract the entries to.
     */
    public void extract(String location) {
        extract(new File(location));
    }

    /**
     * Extract a directory and all of its contents from this zip file to disk.  That is, all entries starting with
     * {@code directoryEntry} will be extracted.  If no entries start with {@code directoryEntry}, then this method will
     * (effectively) do nothing.
     *
     * @param directoryEntry
     *        The directory (entry) whose contents will be extracted.
     *
     * @param location
     *        The location to extract the entries to.
     */
    public void extractDirectory(String directoryEntry, File location) {
        if (!directoryEntry.endsWith("/"))
            directoryEntry += "/";
        Map<String,Set<String>> entries = getEntryList();
        for (String dir : entries.get(LIST_DIRECTORY_KEY))
            if (dir.startsWith(directoryEntry))
                extract(dir,location);
        for (String dir : entries.get(LIST_FILE_KEY))
            if (dir.startsWith(directoryEntry))
                extract(dir,location);
    }

    /**
     * Extract a directory and all of its contents from this zip file to disk.  That is, all entries starting with
     * {@code directoryEntry} will be extracted.  If no entries start with {@code directoryEntry}, then this method will
     * (effectively) do nothing.
     *
     * @param directoryEntry
     *        The directory (entry) whose contents will be extracted.
     *
     * @param location
     *        The location to extract the entries to.
     */
    public void extractDirectory(String directoryEntry, String location) {
        extractDirectory(directoryEntry,new File(location));
    }

    /**
     * Extract a number of entries from this zip file to disk. If any of the entries has directories (denoted by
     * "<code>/</code>" characters), then they will be created before extracting the file, even if they are not explicitly
     * listed in the zip file as directory entries.
     *
     * @param entries
     *        The entries to extract.
     *
     * @param location
     *        The location to extract the entries to.
     *
     * @throws IllegalArgumentException if any entry in {@code entries} is not found in this zip file.
     */
    public void extract(Collection<String> entries, File location) {
        java.util.zip.ZipFile zf = null;
        try {
            zf = getZipFile();
            for (String entry : entries)
                extract(entry,location,zf);
        } finally {
            if (zf != null) // && !isOpen())
                try {
                    zf.close();
                } catch (IOException e) {
                    //swallow
                }
        }
    }

    /**
     * Extract a number of entries from this zip file to disk. If any of the entries has directories (denoted by
     * "<code>/</code>" characters), then they will be created before extracting the file, even if they are not explicitly
     * listed in the zip file as directory entries.
     *
     * @param entries
     *        The entries to extract.
     *
     * @param location
     *        The location to extract the entries to.
     *
     * @throws IllegalArgumentException if any entry in {@code entries} is not found in this zip file.
     */
    public void extract(Collection<String> entries, String location) {
        extract(entries,new File(location));
    }

    /**
     * Extract the data in an entry to an output stream.
     *
     * @param entry
     *        The entry to extract.
     *
     * @param os
     *        The output stream to extract the entry to.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public void extract(String entry, OutputStream os) {
        java.util.zip.ZipFile zf = null;
        try {
            extract(entry,os,zf = getZipFile());
        } finally {
            if (zf != null) // && !isOpen())
                try {
                    zf.close();
                } catch (IOException e) {
                    //swallow
                }
        }
    }

    /**
     * Extract the data in an entry to a byte array.
     *
     * @param entry
     *        The entry to extract.
     *
     * @return a byte array containing the extracted data for {@code entry}.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public byte[] extractBytes(String entry) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        extract(entry,bos);
        return bos.toByteArray();
    }

    /**
     * Extract the data in an entry to a string with a specified character encoding.
     *
     * @param entry
     *        The entry to extract.
     *
     * @param charset
     *        The character encoding to use when converting the data to a string.
     *
     * @return a string containing the extracted data for {@code entry}.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public String extractString(String entry, Charset charset) {
        return new String(extractBytes(entry),charset);
    }

    /**
     * Extract the data in an entry to a string with the default character encoding.
     *
     * @param entry
     *        The entry to extract.
     *
     * @return a string containing the extracted data for {@code entry}.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public String extractString(String entry) {
        return new String(extractBytes(entry));
    }

    private void extract(String entry, OutputStream os, java.util.zip.ZipFile zf) {
        checkIfEntryExists(entry);
        InputStream zis = null;
        try {
            zis = zf.getInputStream(zf.getEntry(entry));
            byte[] buffer = new byte[1024];
            int readCount;
            while ((readCount = zis.read(buffer)) != -1)
                os.write(buffer,0,readCount);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (zis != null)
                try {
                    zis.close();
                } catch (IOException e) {
                    //swallow
                }
        }
    }

    /**
     * Get an input stream to read the data from an entry in this zip file.  The data read from this input stream will
     * be extracted (uncompressed). The returned input must be closed when finished to release the opened connection
     * to the zip file.
     *
     * @param entry
     *        The entry to get the input stream for.
     *
     * @return an input stream for reading the uncompressed data for {@code entry}.
     *
     * @throws IllegalArgumentException if {@code entry} is not found in this zip file.
     */
    public InputStream getInputStream(String entry) {
        checkIfEntryExists(entry);
        return new ZipEntryInputStream(entry);
    }

    private class ZipEntryInputStream extends InputStream {
        private final InputStream is;
        private final java.util.zip.ZipFile zf;

        private ZipEntryInputStream(String entry) {
            try {
                zf = getZipFile();
                is = zf.getInputStream(zf.getEntry(entry));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }

        public int available() throws IOException {
            return is.available();
        }

        public void mark(int readlimit) {
            is.mark(readlimit);
        }

        public boolean markSupported() {
            return is.markSupported();
        }

        public int read() throws IOException {
            return is.read();
        }

        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return is.read(b,off,len);
        }

        public void reset() throws IOException {
           is.reset();
        }

        public long skip(long n) throws IOException {
            return is.skip(n);
        }

        public void close() {
//            if (!isOpen()) {
            try {
                zf.close();
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
//        }
    }

    private List<String> getOrderedEntryList() {
        Map<String,Set<String>> entries = getEntryList();
        List<String> dirsThenFiles = new LinkedList<String>();
        //lump them together in one collection so only one zip file instance will be opeend in extract call
        dirsThenFiles.addAll(entries.get(LIST_DIRECTORY_KEY)); //extract dirs
        dirsThenFiles.addAll(entries.get(LIST_FILE_KEY)); //then files
        return dirsThenFiles;
    }

    /**
     * Get iterator for iterating over the names of the entries in this zip file.  The entries will be ordered such that
     * the directories will be listed first, then the file.  The entry names will be as they exist in this instance, not
     * necessarily how they exist on disk.
     *
     * @return an iterator for the entry names in this zip file.
     */
    public Iterator<String> iterator() {
        return getOrderedEntryList().iterator();
    }

    /**
     * The {@code ZipEntrySource} provides a framework for sending data into a zip file entry.
     */
    public static interface ZipEntrySource extends Closeable {
        /**
         * Write the data for the entry to an output stream.
         *
         * @param os
         *        The output stream to write the data to.
         *
         * @throws IOException This exception is specified for ease of implementation.
         *
         */
        void writeData(OutputStream os) throws IOException;

        /**
         * Close any resources used by this instance.  <i>Do not</i> close the output stream used in
         * {@link #writeData(java.io.OutputStream)} with this method.
         *
         * @throws IOException This exception is specified for ease of implementation.
         */
        void close() throws IOException;
    }

    private static abstract class ZipEntryByteArraySource implements ZipEntrySource {
        abstract int getBytes(byte[] sink) throws IOException;

        public void writeData(OutputStream os) throws IOException {
            byte[] buffer = new byte[1024];
            int readCount;
            while ((readCount = getBytes(buffer)) > -1)
                os.write(buffer,0,readCount);
        }
    }

    /**
     * The {@code FileZipEntrySource} provides a simple zip entry source which reads its data from a file. That is, this
     * class can be used to read data from a file into a zip entry.
     */
    public static class FileZipEntrySource extends ZipEntryByteArraySource {
        private final File file;
        private FileInputStream source = null;

        /**
         * Constructor specifying the file which will be read from.
         *
         * @param file
         *        The file which will be the source of zip entry data.
         */
        public FileZipEntrySource(File file) {
            this.file = file;
        }

        /**
         * Constructor specifying the file which will be read from.
         *
         * @param file
         *        The file which will be the source of zip entry data.
         */
        public FileZipEntrySource(String file) {
            this(new File(file));
        }

        int getBytes(byte[] sink) throws IOException {
            if (source == null)
                source = new FileInputStream(file);
            return source.read(sink);
        }

        public void close() {
            try {
                source.close();
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }

    /**
     * The {@code CompositeZipEntrySource} provides a zip entry source made from a series of {@code ZipEntrySource}s.
     * This class can be used to condense mulitple data sources into a singe zip entry source.
     */
    public static class CompositeZipEntrySource implements ZipEntrySource {
        private List<ZipEntrySource> sources = new LinkedList<ZipEntrySource>();

        /**
         * Add a source for this zip entry source.
         *
         * @param source
         *        The source to add.
         */
        public void addSource(ZipEntrySource source) {
            sources.add(source);
        }

        /**
         * {@inheritDoc}
         *
         * The data will be written in the order that the sources were added.
         */
        public void writeData(OutputStream os) throws IOException {
            for (ZipEntrySource source : sources)
                source.writeData(os);
        }

        public void close() throws IOException {
            for (ZipEntrySource source : sources)
                source.close();
        }
    }

    private class ZipFileZipEntrySource extends ZipEntryByteArraySource {
        private final ZipEntry entry;
        private final java.util.zip.ZipFile zipFile;
        private InputStream entryInputStream = null;

        public ZipFileZipEntrySource(java.util.zip.ZipFile zipFile, ZipEntry entry) {
            this.entry = entry;
            this.zipFile = zipFile;
        }

        public ZipFileZipEntrySource(java.util.zip.ZipFile zipFile, String entryName) {
            this(zipFile,zipFile.getEntry(entryName));
        }

        private void checkInputStream() throws IOException {
            if (entryInputStream == null)
                entryInputStream = zipFile.getInputStream(entry);
        }

        public int getBytes(byte[] sink) throws IOException {
            checkInputStream();
            return entryInputStream.read(sink);
        }

        public void close() throws IOException {
            if (entryInputStream == null)
              return;
            entryInputStream.close();
        }
    }

    private java.util.zip.ZipFile deferredZipEntryZipFile = null;
    private int writtenDeferredZipEntryCount = 0;
    private int deferredZipEntryCount = 0;

    private class DeferredZipFileZipEntrySource implements ZipEntrySource {
        private final String entry;
        private ZipFileZipEntrySource source = null;

        public DeferredZipFileZipEntrySource(String entry) {
            this.entry = entry;
            deferredZipEntryCount++;
        }

        public void writeData(OutputStream os) throws IOException {
            if (deferredZipEntryZipFile == null)
                deferredZipEntryZipFile = new java.util.zip.ZipFile(zipFile);
            source = new ZipFileZipEntrySource(deferredZipEntryZipFile,entry);
            source.writeData(os);
            writtenDeferredZipEntryCount++;
        }

        public void close() throws IOException {
            if (source != null)
                source.close();
            if (writtenDeferredZipEntryCount == deferredZipEntryCount) {
                deferredZipEntryZipFile.close();
                writtenDeferredZipEntryCount = 0;
            }
        }
    }
}
