/*
 * Copyright (c) 2011, Christopher R. Frazier
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package crf.slimjim;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPoolException;

import static com.sun.tools.classfile.ConstantPool.*;

import java.io.*;
import java.lang.String;
import java.util.*;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

                               

/**
 * The {@code SlimJim} class is used to run the SlimJim program.  The SlimJim program takes a java program and creates
 *  a jar file holding only those classes necessary to run it.  Essentially what it does is, given some starting class(es),
 * it uses a java decompiler to find all of the linked classes in the classfile(s), and then recursively perform this operation
 * on the linked classes.
 * <p>
 * This can be used to reduce the size of a projects jar distribution, as well as combining it into a single jar file.
 * The original intention, though, was to provide a way to give access to tools without requiring a huge jar file holding
 * many unwanted tools/classes.  The former use might be a little sketchy if you are using third party libraries, as, they
 * often must be kept in their entirety, and, depending on the situation, this tool may violate their license.  The second
 * use is generally more valid, as internal tool distribution tends to be less dependent on third party libraries.
 * <p></p>
 * This program requires access to a tools.jar library from a Sun/Oracle JDK or JVM distribution, version 1.7 or later.
 * If one is not available, you may use the one included with the JVM distributed with this release. (Note that although
 * the JVM in this release is a Windows distribution, the /lib/tools.jar file is not platform-dependant.)
 *
 * @version 1.3
 * @author crf
 */
public class SlimJim {

    /**
     * Print the usage for this program.
     */
    public static void printUsage() {
        System.out.println("java -cp [classpath] SlimJim [-p] [-d] [-v] [-k mask] [-i mask] [-o file] [-a file] [-u] [-f file] [-e file:path] [-j file:entry] [-z file] classes ...");
        System.out.println("where:");
        System.out.println("\tclasspath = the classpath for the programs to slim");
        System.out.println("\t-p = print class dependencies");
        System.out.println("\t-d = print classpath dependencies");
        System.out.println("\t-v = print verbose output");
        System.out.println("\t-k = skip classes starting with mask");
        System.out.println("\t-ka = skip all classes (use with -i to build package-limited jar)");
        System.out.println("\t-i = force inclusion of classes starting with mask");
        System.out.println("\t-o = the output jar file");
        System.out.println("\t-a = auto-add non-class jar entries from (assumed jar) file");
        System.out.println("\t-u = same as -a, only automatically includes all jar files");
        System.out.println("\t-f = add file to output jar");
        System.out.println("\t-e = add file to output jar in path (jar entry will be path/filename)");
        System.out.println("\t-j = add entry from jar to output jarfile");
        System.out.println("\t-z = include all entries from jar file in output jar");
        System.out.println("\tclasses ... = classes to determine dependencies for and add to output jarfile");
    }

    /**
     * The main method for this class. The args usage is as follows:
     *
     * <pre>
     * [-p] [-d] [-v] [-k mask] [-i mask] [-o file] [-a file] [-u] [-f file] [-e file:path] [-j file:entry] [-z file] classes ...
     *     where:
     *     classpath = the classpath for the programs to slim
     *     -p = print class dependencies
     *     -d = print classpath dependencies
     *     -v = print verbose output
     *     -k = skip classes starting with mask
     *     -ka = skip all classes (use with -i to build package-limited jar)
     *     -i = force inclusion of classes starting with mask
     *     -o = the output jar file
     *     -a = auto-add non-class jar entries from (assumed jar) file
     *     -u = same as -a, only automatically includes all jar files
     *     -f = add file to output jar
     *     -e = add file to output jar in path (jar entry will be path/filename)
     *     -j = add entry from jar to output jarfile
     *     -z = include all entries from jar file in output jar
     *     classes ... = classes to determine dependencies for and add to output jarfile
     * </pre>
     *
     * @param args
     *        The list of arguments for running this program.
     */
    public static void main(String ... args) {
        if (args.length == 0 || args[0].equals("-help")) {
            printUsage();
            return;
        }
        new SlimJim().entry(args);
    }

    private Map<File,String> extraFileMap = new HashMap<>();
    private Map<String,File> jarEntryMap = new HashMap<>();
    private List<String> autoAddJars = new LinkedList<>();
    private Set<String> includeAllJars = new HashSet<>();
    private boolean printClasses = false;
    private boolean printClassPaths = false;
    private boolean verbose = false;
    private Set<String> skips = new HashSet<>(Arrays.asList("java.*","javax.*","sun.*","org.ietf.jgss","org.omg.*","org.w3c.*","org.xml.*","\""));
    private boolean skipAll = false;
    private Set<String> includes = new HashSet<>();

    /**
     * The entry point for the class. The arguments are the same as those used for the {@link #main(String...)} method.
     *
     * @param args
     *        The arguments (presumably from a main method).
     */
    public void entry(String ... args) {
        String out = null;
        List<String> classes = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-p":
                    printClasses = true;
                    break;
                case "-d":
                    printClassPaths = true;
                    break;
                case "-v":
                    verbose = true;
                    break;
                case "-o":
                    if (i == args.length - 1)
                        error("-o requires argument");
                    out = args[++i];
                    break;
                case "-a":
                    if (i == args.length - 1)
                        error("-a requires argument");
                    addAutoAddNonClassJarEntries(args[++i]);
                    break;
                case "-u":
                    for (String cp : System.getProperties().getProperty("java.class.path").split(File.pathSeparator))
                        if (cp.endsWith(".jar"))
                            addAutoAddNonClassJarEntries(cp);
                    break;
                case "-f":
                    if (i == args.length - 1)
                        error("-f requires argument");
                    addFileForJar(args[++i]);
                    break;
                case "-e": {
                    if (i == args.length - 1)
                        error("-e requires argument");
                    String[] ex = arg.split(":");
                    if (ex.length != 2)
                        error("-e requires argument of the form file:path");
                    addFileForJar(ex[0], ex[1]);
                    break;
                }
                case "-j": {
                    if (i == args.length - 1)
                        error("-j requires argument");
                    String[] ex = arg.split(":");
                    if (ex.length != 2)
                        error("-j requires argument of the form file:entry");
                    addJarEntryForJar(ex[0], ex[1]);
                    break;
                }
                case "-k":
                    if (i == args.length - 1)
                        error("-k requires argument");
                    String sk = args[++i];
                    checkMask(sk);
                    includes.remove(sk);
                    skips.add(sk);
                    break;
                case "-ka":
                    skipAll = true;
                    break;
                case "-i":
                    if (i == args.length - 1)
                        error("-i requires argument");
                    String inc = args[++i];
                    checkMask(inc);
                    skips.remove(inc);
                    includes.add(inc);
                    break;
                case "-z":
                    if (i == args.length - 1)
                        error("-z requires argument");
                    includeAllJars.add(args[++i]);
                    break;
                default:
                    if (arg.startsWith("-"))
                        throw new IllegalArgumentException("Unknown switch: " + arg);
                    if (classes == null)
                        classes = new LinkedList<>();
                    classes.add(arg);
                    break;
            }
        }
        if (classes == null) 
            error("No classes specified");
        if (!printClasses && !printClassPaths && out == null)
            error("Nothing to do");
        Set<String> classSet = getClassSet(classes.toArray(new String[classes.size()]));
        if (out != null)
            slimJar(out,classSet);
    }

    private void checkMask(String mask) {
        if (mask.contains("*") && mask.indexOf("*") != (mask.length()-1))
                throw new IllegalArgumentException("Invalid mask: " + mask);
    }
    
    private void error(String message) {
        System.err.println(message);
        System.exit(1);
    }

    /**
     * Add a file to the output jar file.
     *
     * @param file
     *        The path of the file to add. The file will retain its name, but its path will be dropped.
     *
     * @param entryDir
     *        The directory (path) in the jar file to which the file will be added.
     */
    public void addFileForJar(String file, String entryDir) {
        File f = new File(file);
        if (!f.exists())
            throw new IllegalArgumentException("File not found: " + file);
        if (!entryDir.endsWith("/"))
            entryDir += "/";
        extraFileMap.put(f,entryDir);
    }

    /**
     * Add a file to the base directory (path) of the output jar.
     *
     * @param file
     *        The file to add. The file will retain its name, but its path will be dropped.
     */
    public void addFileForJar(String file) {
        addFileForJar(file,"");
    }

    /**
     * Add an entry from an existing jar file to the output jar.
     *
     * @param entry
     *        The entry name from the source jar file.
     *
     * @param jarFile
     *        The source jar file.
     */
    public void addJarEntryForJar(String entry, String jarFile) {
        File f = new File(jarFile);
        if (!f.exists())
            throw new IllegalArgumentException("Jar file not found: " + jarFile);
        jarEntryMap.put(entry,f);
    }

    /**
     * Automatically add all non-classfile jar entries for the specified jar file.
     *
     * @param jarFile
     *        The jar file to add the non-classfile jar entries for.
     */
    public void addAutoAddNonClassJarEntries(String jarFile) {
        autoAddJars.add(jarFile);
    }

    private void autoAddNonClassJarEntries(String cpElement) {
        try {
            JarFile jf = new JarFile(cpElement);
            Enumeration<JarEntry> jenum = jf.entries();
            while (jenum.hasMoreElements()) {
                String entry = jenum.nextElement().getName();
                if (!entry.endsWith(".class") && !entry.endsWith("/") && !entry.equalsIgnoreCase("META-INF/MANIFEST.MF"))
                    addJarEntryForJar(entry,cpElement);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void slimJar(String jarFile, Set<String> classes) {
        JarOutputStream jos = null;
        System.out.println("Creating " + jarFile + "...");
        try {
            for (String autoAddJar : autoAddJars)
                autoAddNonClassJarEntries(autoAddJar);
            Set<String> jarDirs = new HashSet<>();
            jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)));
            Set<String> writtenEntries = new HashSet<>();
            //write full jars first
            for (String jarF : includeAllJars)
                writtenEntries.addAll(copyJarToJar(jarF,jos));
            for (String className : classes) {
                if (verbose)
                    System.out.println("\tadding " + className);
                String csName = className.replace(".","/");
                addJarDirs(csName,jarDirs);
                addEntryToJar(csName + ".class",ClassLoader.getSystemResourceAsStream((className).replace(".","/") + ".class"),jos,jarDirs,writtenEntries);
            }
            for (File f : extraFileMap.keySet())
                addFileToJar(f,extraFileMap.get(f),jos,jarDirs,writtenEntries);
            for (String e : jarEntryMap.keySet())
                addJarEntryToJar(e,jarEntryMap.get(e),jos,jarDirs,writtenEntries);
            for (String entry : writtenEntries)
                jarDirs.remove(entry);
            writeJarDirs(jarDirs,jos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (jos != null) {
                try {
                    jos.closeEntry();
                } catch (IOException e) {
                    //swallow
                }
                try {
                    jos.close();
                } catch (IOException e) {
                    //swallow
                }
            }
        }
        System.out.println("...finished");
    }

    private Set<String> copyJarToJar(String jarFile, JarOutputStream jos) throws IOException {
        Set<String> addedEntries = new HashSet<>();
        try (JarFile jf = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().toUpperCase().endsWith("MANIFEST.MF"))
                    continue;
                if (verbose)
                    System.out.println("\tadding " + entry.getName());
                addedEntries.add(entry.getName());
                addEntryToJar(entry.getName(),jf.getInputStream(entry),jos,null,null);
            }
        }
        return addedEntries;
    }

    private void addFileToJar(File input, String entryDir, JarOutputStream jos, Set<String> jarDirs, Set<String> writtenEntries) throws IOException {
        String entry = entryDir + input.getName();
        if (writtenEntries.contains(entry))
            return;
        if (verbose)
            System.out.println("\tadding " + entry);
        addEntryToJar(entry,new FileInputStream(input),jos,jarDirs,writtenEntries);
    }

    private void addJarEntryToJar(String entry, File jarFile, JarOutputStream jos, Set<String> jarDirs, Set<String> writtenEntries) throws IOException {
        if (writtenEntries.contains(entry))
            return;
        try (JarFile jf = new JarFile(jarFile)) {
            if (verbose)
                System.out.println("\tadding " + entry);
            addEntryToJar(entry,jf.getInputStream(jf.getEntry(entry)),jos,jarDirs,writtenEntries);
        }

    }

    private void addEntryToJar(String name, InputStream in, JarOutputStream jos, Set<String> jarDirs, Set<String> writtenEntries) throws IOException {
        if (writtenEntries != null && writtenEntries.contains(name))
            return;
        if (jarDirs != null)
            addJarDirs(name,jarDirs);
        jos.putNextEntry(new JarEntry(name));
        byte[] buffer = new byte[8192];
        int length;
        while ((length = (in.read(buffer))) > -1)
            jos.write(buffer,0,length);
    }

    private void addJarDirs(String className, Set<String> dirs) {
        if (className.endsWith("/"))
            dirs.add(className);
        int ind = className.lastIndexOf('/',className.length()-2);
        if (ind == -1)
            return;
        addJarDirs(className.substring(0,ind+1),dirs);
    }

    private void writeJarDirs(Set<String> dirs, JarOutputStream jos) throws IOException {
        for (String dir : dirs)
            jos.putNextEntry(new JarEntry(dir));
    }

    /**
     * Get set of dependent classes which the specified input classes depend on.
     *
     * @param inputClasses
     *        The input classes to check against.
     *
     * @return the set of classes which {@code inputClasses} depend on, including themselves.
     */
    public Set<String> getClassSet(String ... inputClasses) {
        Set<String> classes = new HashSet<>();
        for (String inputClass : inputClasses)
            recurseToGetClasses(inputClass,classes,true);
        if (printClasses) {
            System.out.println("Dependencies...");
            for (String cls : new TreeSet<>(classes))
                System.out.println(cls);
        }
        if (printClassPaths)
            getClassPathSet(classes);
        return classes;
    }

    private Set<String> getClassPathSet(Set<String> classes) {
        Set<String> classPaths = new HashSet<>();
        for (String cls : classes) {
            String csName = cls.replace(".","/") + ".class";
            String resource = ClassLoader.getSystemResource(csName).toString();
            if (resource.startsWith("jar:"))
                resource = resource.replace(".jar!/",".jar").substring(4);
            if (resource.startsWith("file:/"))
                resource = resource.replace(csName,"").substring(6);
            classPaths.add(resource);
        }
        if (printClassPaths) {
            System.out.println("*** Classpath dependencies...");
            for (String cp : classPaths)
                System.out.println(cp);
            System.out.println("***");
        }
        return classPaths;
    }

    /**
     * Get set of dependent classpath entries which the specified input classes depend on.
     *
     * @param inputClasses
     *        The input classes to check against.
     *
     * @return the set of classpath entries which {@code inputClasses} depend on, including those they are in.
     */
    public Set<String> getClassPathSet(String ... inputClasses) {
        return getClassPathSet(getClassSet(inputClasses));
    }

    private boolean checkClassNameAgainstMask(String className, String mask) {
        return className.equals(mask) || className.startsWith(mask.replace("*",""));
    }

    private boolean inMasks(String className, Collection<String> masks) {
        for (String mask : masks)
            if (checkClassNameAgainstMask(className,mask))
                return true;
        return false;
    }

//old version - doesn't handle explicit includes/skips correctly
//    private boolean skipClass(String className) {
//        for (String mask : (skipAll ? includes : skips))
//            if (checkClassNameAgainstMask(className,mask))
//                return !skipAll;
//        return skipAll;
//    }

    private boolean skipClass(String className) {
        //if in includes and skips, then use following logic
        //  if skipAll is enabled, then explicit skips override includes (because we drop everything, include stuff, then refine with skipping)
        //  if skipAll is not enabled, then explicit includes override skips (because we include everything, drop stuff, then refine with including)
        boolean inIncludes = inMasks(className,includes);
        boolean inSkips = inMasks(className,skips);
        return skipAll ? (inSkips || !inIncludes) : (!inIncludes && inSkips);
    }

    private void recurseToGetClasses(String className, Set<String> currentSet, boolean inputClass) {
        if (!inputClass && skipClass(className)) {
            if (verbose)
                System.out.println("SKIPPING: " + className);
            return;
        }
        InputStream is = ClassLoader.getSystemResourceAsStream((className).replace(".","/") + ".class");
        if (is == null) //not found
            return;
        if (currentSet.add(className) && verbose)
            System.out.println("ADDING: " + className);
        try {
            for (String cls : getClasses(ClassFile.read(is)))
                if (!currentSet.contains(cls))
                    recurseToGetClasses(cls,currentSet,false);
        } catch (IOException | ConstantPoolException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> getClasses(ClassFile cls) throws ConstantPoolException {
        Set<String> classes = new HashSet<>();
        for (CPInfo info : cls.constant_pool.entries()) {
            if (info.getTag() == CONSTANT_Class)
                classes.add(((CONSTANT_Class_info) info).getName().replace("/","."));
        }
        return classes;
    }
}

