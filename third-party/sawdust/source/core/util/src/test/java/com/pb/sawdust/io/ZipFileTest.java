package com.pb.sawdust.io;

import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.*;

/**
 * The {@code ZipFileTest} test the ZipFile class.  This essentially only tests for internal consistency, as I don't
 * create a zip file externally and check against it.
 *
 * @author crf <br/>
 *         Started Mar 22, 2010 11:18:08 AM
 */
public class ZipFileTest extends TestBase {

    public static void main(String ... args) {
        TestBase.main();
    }


    private File getTempFile() {
        return getTemporaryFile(ZipFileTest.class,"temp.zip");
    }

    private void deleteTempFile() {
        File temp = getTempFile();
        if (temp.exists())
            temp.delete();
    }

    @After
    public void afterTest() {
        deleteTempFile();
    }

    //todo: zipentrysource adding??
    

    @Test
    public void testAddExtractStringData() {
        ZipFile zf = new ZipFile(getTempFile());
        String zipEntryName = "entry";
        String a = random.nextAsciiString(6);
        zf.addEntry(zipEntryName,a);
        zf.write();
        assertEquals(a,zf.extractString(zipEntryName));
    }


    @Test
    public void testAddExtractStringDataCharset() {
        ZipFile zf = new ZipFile(getTempFile());
        String zipEntryName = "entry";
        String a = random.nextAsciiString(67);
        zf.addEntry(zipEntryName,a,Charset.forName("UTF-8"));
        zf.write();
        assertEquals(a,zf.extractString(zipEntryName,Charset.forName("UTF-8")));
    }

    @Test
    public void testAddExtractBytes() {
        ZipFile zf = new ZipFile(getTempFile());
        String zipEntryName = "entry";
        byte[] bytes = new byte[random.nextInt(40,400)];
        for (int i : range(bytes.length))
            bytes[i] = random.nextByte();
        zf.addEntry(zipEntryName,bytes);
        zf.write();
        assertArrayEquals(bytes,zf.extractBytes(zipEntryName));
    }

    /* **********files and dirs ************** */
    @Test
    public void testAddDirByName() {
        ZipFile zf = new ZipFile(getTempFile());
        String dirName = "dir_test";
        zf.addDirectoryByName(dirName);
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dirName + "/"));
    }

    @Test
    public void testAddDirByNameMultipleDirs() {
        ZipFile zf = new ZipFile(getTempFile());
        String dirName1 = "dir_test";
        String dirName2 = "dir_test1";
        String dirName3 = "dir_test2";
        zf.addDirectoryByName(dirName1 + "/" + dirName2 + "\\" + dirName3);
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dirName1 + "/" + dirName2 + "/" + dirName3 + "/") &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dirName1 + "/" + dirName2 + "/") &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dirName1 + "/") );
    }

    @Test
    public void testAddExtractFile() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
        zf.write();
        temp.delete(); //delete to make sure we read in the extracted copy
        zf.extract(temp.getName(),temp.getParent());
        assertEquals(data,tf.readAll());
        temp.delete();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddFileNotExist() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExtractFileNotExist() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        File temp2 = getTemporaryFile(ZipFileTest.class,"temp2.txt");
        TextFile tf = new TextFile(temp2);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        temp2.delete();
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp2);
        zf.write();
        zf.extract(temp.getName(),temp.getParent());
    }

    @Test
    public void testAddExtractFileRelative() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp.getName(),temp.getParent());
        zf.write();
        temp.delete(); //delete to make sure we read in the extracted copy
        zf.extract(temp.getName(),temp.getParent());
        assertEquals(data,tf.readAll());
        temp.delete();
    }

    @Test
    public void testAddExtractFileRelativeWithDir() {
        String dir = "temp_dir";
        String tempFile = dir + "/temp.txt";
        File temp = getTemporaryFile(ZipFileTest.class,tempFile);
        File tempDir = temp.getParentFile();
        tempDir.mkdir();
        File baseDir = temp.getParentFile().getParentFile();
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(new File(tempFile),baseDir);
        zf.write();
        FileUtil.deleteDir(tempDir); //delete to make sure we read in the extracted copy
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir + "/")); //make sure directory entry added
        zf.extract(tempFile,baseDir);
        assertEquals(data,tf.readAll());
        FileUtil.deleteDir(temp.getParentFile());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddFileRelativeNotExist() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp.getName(),temp.getParent());
    }

    @Test
    public void testAddExtractFileInDirectoryWithDir() {
        String dir = "temp_dir/";
        String tempFile = "temp.txt";
        File temp = getTemporaryFile(ZipFileTest.class,tempFile);
        File tempDir = temp.getParentFile();
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFileInDirectory(temp,new File(dir));
        zf.write();
        temp.delete(); //delete to make sure we read in the extracted copy
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir)); //make sure directory entry added
        zf.extract(dir + tempFile,tempDir);
        tf = new TextFile(new File(tempDir,dir + tempFile));
        assertEquals(data,tf.readAll());
        FileUtil.deleteDir(new File(tempDir,dir));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddExtractFileInDirectoryNotExist() {
        String dir = "temp_dir/";
        String tempFile = "temp.txt";
        File temp = getTemporaryFile(ZipFileTest.class,tempFile);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFileInDirectory(temp,new File(dir));
    }

    @Test
    public void testAddDirectory() {
        //make straight tree: /a/c/d  a/c/e
        String dir1 = "a/";
        String dir2 = "c/";
        String dir3 = "d/";
        String dir4 = "e/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir3);
        temp.mkdirs();
        File temp2 = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir4);
        temp2.mkdirs();
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent());
        zf.write();
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2 + dir3) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2 + dir4));
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
        zf.extractDirectory(dir1,temp.getParentFile().getParentFile().getParent());
        assertTrue(temp.exists() && temp.isDirectory());
        assertTrue(temp2.exists() && temp2.isDirectory());
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddDirectoryNotExist() {
        //make straight tree: /a/c/d  a/c/e
        String dir1 = "a/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParent());
    }

    @Test
    public void testAddDirectoryWithFile() {
        //make straight tree: /a/c/d
        String dir1 = "a/";
        String dir2 = "c/";
        String dir3 = "d/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir3);
        temp.mkdirs();
        String tFile = "temp.txt";
        File temp2 = getTemporaryFile(ZipFileTest.class,dir1 + tFile);
        TextFile tf = new TextFile(temp2);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent());
        zf.write();
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2 + dir3) &&
                   zf.getEntryList().get(ZipFile.LIST_FILE_KEY).contains(dir1 + tFile));
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
        zf.extractDirectory(dir1,temp.getParentFile().getParentFile().getParentFile());
        assertEquals(data,tf.readAll());
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
    }

    @Test
    public void testAddDirectoryWithFileAndFilter() {
        //make straight tree: /a/c/d
        String dir1 = "a/";
        String dir2 = "c/";
        String dir3 = "d/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir3);
        temp.mkdirs();
        String tFile = "temp.txt";
        File temp2 = getTemporaryFile(ZipFileTest.class,dir1 + tFile);
        TextFile tf = new TextFile(temp2);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);

        String tFile2 = "temp2.txt";
        File temp22 = getTemporaryFile(ZipFileTest.class,dir1 + tFile2);
        TextFile tf2 = new TextFile(temp22);
        String data2 = random.nextAsciiString(random.nextInt(20,400));
        tf2.writeText(data2);

        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent(),FileUtil.fileFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.contains("2");
            }
        }));
        zf.write();
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2) &&
                   zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(dir1 + dir2 + dir3) &&
                   zf.getEntryList().get(ZipFile.LIST_FILE_KEY).contains(dir1 + tFile) &&
                   !zf.getEntryList().get(ZipFile.LIST_FILE_KEY).contains(dir1 + tFile2));
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
        zf.extract(dir1 + tFile,temp.getParentFile().getParentFile().getParent());
        assertEquals(data,tf.readAll());
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddDirectoryWithFileAndFilterNotExist() {
        //make straight tree: /a/c/d
        String dir1 = "a/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent(),FileUtil.fileFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.contains("2");
            }
        }));
    }

    @Test
    public void testDeleteEntry() {
        ZipFile zf = new ZipFile(getTempFile());
        String name = "hello/";
        zf.addDirectoryByName(name);
        zf.write();
        assertTrue(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(name));
        zf.deleteEntry(name);
        assertFalse(zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY).contains(name));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteEntryNotExist() {
        ZipFile zf = new ZipFile(getTempFile());
        String name = "hello/";
        zf.addDirectoryByName(name);
        zf.write();
        zf.deleteEntry("a" + name);
    }

    @Test
    public void testAddExtractCollection() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        File temp2 = getTemporaryFile(ZipFileTest.class,"temp2.txt");
        TextFile tf2 = new TextFile(temp2);
        String data2 = random.nextAsciiString(random.nextInt(20,400));
        tf2.writeText(data2);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
        zf.addFile(temp2);
        zf.write();
        temp.delete(); //delete to make sure we read in the extracted copy
        temp2.delete(); //delete to make sure we read in the extracted copy
        zf.extract(Arrays.asList(temp.getName(),temp2.getName()),temp.getParent());
        assertEquals(data,tf.readAll());
        assertEquals(data2,tf2.readAll());
        temp.delete();
        temp2.delete();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddExtractCollectionNotExist() {
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectoryByName("a");
        zf.write();
        zf.extract(Arrays.asList("c","a"),getTemporaryFile(ZipFileTest.class,"temp2.txt").getParent());
    }

    //todo: test extract to output stream?
    //todo: test get extract input stream?

    @Test
    public void testExtractBytes() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
        zf.write();
        assertArrayEquals(data.getBytes(),zf.extractBytes(temp.getName()));
        temp.delete();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExtractBytesNotExist() {
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectoryByName("a");
        zf.write();
        zf.extractBytes("b");
    }

    @Test
    public void testExtractString() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        TextFile tf = new TextFile(temp);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
        zf.write();
        assertEquals(data,zf.extractString(temp.getName()));
        temp.delete();
    }

    @Test
    public void testExtractStringCharset() {
        File temp = getTemporaryFile(ZipFileTest.class,"temp.txt");
        Charset charset = Charset.forName("UTF-8");
        TextFile tf = new TextFile(temp,charset);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addFile(temp);
        zf.write();
        assertEquals(data,zf.extractString(temp.getName(),charset));
        temp.delete();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExtractStringNotExist() {
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectoryByName("a");
        zf.write();
        zf.extractString("b");
    }

    @Test
    public void testGetEntryList() {
        String dir1 = "a/";
        String dir2 = "c/";
        String dir3 = "d/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir3);
        temp.mkdirs();
        String tFile = "temp.txt";
        File temp2 = getTemporaryFile(ZipFileTest.class,dir1 + tFile);
        TextFile tf = new TextFile(temp2);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent());
        zf.write();
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
        Set<String> dirs = new HashSet<String>();
        dirs.add(dir1);
        dirs.add(dir1+dir2);
        dirs.add(dir1+dir2+dir3);
        Set<String> files = new HashSet<String>();
        files.add(dir1 + tFile);
        assertEquals(dirs,zf.getEntryList().get(ZipFile.LIST_DIRECTORY_KEY));
        assertEquals(files,zf.getEntryList().get(ZipFile.LIST_FILE_KEY));
    }

    @Test
    public void testIterator() {
        String dir1 = "a/";
        String dir2 = "c/";
        String dir3 = "d/";
        File temp = getTemporaryFile(ZipFileTest.class,dir1 + dir2 + dir3);
        temp.mkdirs();
        String tFile = "temp.txt";
        File temp2 = getTemporaryFile(ZipFileTest.class,dir1 + tFile);
        TextFile tf = new TextFile(temp2);
        String data = random.nextAsciiString(random.nextInt(20,400));
        tf.writeText(data);
        ZipFile zf = new ZipFile(getTempFile());
        zf.addDirectory(dir1,temp.getParentFile().getParentFile().getParent());
        zf.write();
        FileUtil.deleteDir(getTemporaryFile(ZipFileTest.class,dir1));
        Set<String> dirs = new HashSet<String>();
        dirs.add(dir1);
        dirs.add(dir1+dir2);
        dirs.add(dir1+dir2+dir3);
        Set<String> files = new HashSet<String>();
        files.add(dir1 + tFile);
        Set<String> against = new HashSet<String>();
        Iterator<String> it = zf.iterator();
        for (int i : range(dirs.size()))
            against.add(it.next());
        assertEquals(dirs,against);
        against.clear();
        for (int i : range(files.size()))
            against.add(it.next());
        assertEquals(files,against);
        assertFalse(it.hasNext());
    }

}
