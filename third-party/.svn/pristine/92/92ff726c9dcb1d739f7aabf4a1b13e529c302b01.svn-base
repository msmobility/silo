package com.pb.sawdust.meta.build;

//import com.pb.sawdust.cookbook.examples.IpfExample;
//import com.pb.sawdust.cookbook.reference_model.sawdust.SawdustDC;
import com.pb.sawdust.io.StreamConnector;
import com.pb.sawdust.io.ZipFile;
import com.pb.sawdust.util.annotations.Transient;
import com.pb.sawdust.util.property.PropertyDeluxe;
import crf.slimjim.SlimJim;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * The {@code ToolMaker} ...
 *
 * @author crf <br/>
 *         Started May 16, 2011 2:46:53 PM
 */
@Transient
public class ToolMaker {

    public static void main(String ... args) {
//        buildFullSawdust();
    }

//    public static void buildIpf() {
//        buildTool("D:\\transfers\\ipf.jar",IpfExample.class.getName());
//    }
//
//    private static void referenceModelSawdust() {
//        buildTool("D:\\transfers\\sawdust_model.jar",SawdustDC.class.getName());
//    }
//
//    private static void referenceModelSawdustLimited() {
//        buildNamespaceLimitedTool("D:\\transfers\\sawdust_model.jar",new HashSet<String>(Arrays.asList("com.pb.sawdust")),SawdustDC.class.getName());
//    }

    private static void buildFullSawdust() {
        String clsUri = SimpleJar.class.getName().replace('.','/') + ".class";
        String clsPath = SimpleJar.class.getClassLoader().getResource(clsUri).getPath();
        clsPath = new File(clsPath.substring(0,clsPath.length()-clsUri.length())).getParentFile().getPath();
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.getName().equals("test");
            }
        };
        List<String> clss = new LinkedList<String>();
        for (File file : new File(clsPath).listFiles(ff))
            addFiles(clss,"",file,ff);
        buildTool("D:\\transfers\\sawdust_complete.jar",clss.toArray(new String[clss.size()]));
    }

    private static void addFiles(List<String> clsNames, String baseName, File path, FileFilter ff) {
        for (File file : path.listFiles(ff))
            if (file.isDirectory())
                addFiles(clsNames,baseName + file.getName() + ".",file,ff);
            else
                clsNames.add((baseName + file.getName()).replace(".class",""));
    }

    public static void buildTool(String outJar, String ... entryClasses) {
        buildTool(outJar, new HashMap<String,String>(),entryClasses);
    }

    public static void buildTool(String outJar, Map<String,String> fileAdds, String ... entryClasses) {
        //file adds are: complete file path : entry dir (so entry is (entry dir)(file path).getName())
        String toutJar = outJar + ".temp";
        SlimJim sj = new SlimJim();
        Set<String> classPathDependencies = sj.getClassPathSet(entryClasses);
        System.out.println(classPathDependencies);

        List<String> arguments = new LinkedList<String>();
        arguments.add("-o");
        arguments.add(toutJar);
//        arguments.add("-v");
        for (String fileAdd : fileAdds.keySet()) {
            arguments.add("-e");
            arguments.add(fileAdd + ":" + fileAdds.get(fileAdd));
        }
        arguments.addAll(Arrays.asList(entryClasses));
        sj.entry(arguments.toArray(new String[arguments.size()]));

        skimOutMetaInf(toutJar);
        addLicenses(toutJar,sj.getClassPathSet(entryClasses));
        SimpleJar.combineJars(outJar,SimpleJar.getBasicManifest(null,"ToolMaker"),toutJar);
        new File(toutJar).deleteOnExit();
    }

    public static void buildNamespaceLimitedTool(String outJar, Set<String> namespaceMasks, String ... entryClasses) {
        buildNamespaceLimitedTool(outJar,namespaceMasks,new HashMap<String, String>(),entryClasses);
    }

    public static void buildNamespaceLimitedTool(String outJar, Set<String> namespaceMasks, Map<String,String> fileAdds, String ... entryClasses) {
        //file adds are: complete file path : entry dir (so entry is (entry dir)(file path).getName())
        String toutJar = outJar + ".temp";
        SlimJim sj = new SlimJim();

        List<String> arguments = new LinkedList<String>();
        arguments.add("-o");
        arguments.add(toutJar);
//        arguments.add("-v");
        for (String fileAdd : fileAdds.keySet()) {
            arguments.add("-e");
            arguments.add(fileAdd + ":" + fileAdds.get(fileAdd));
        }
        arguments.add("-k");
        arguments.add("*");
        for (String namespaceMask : namespaceMasks) {
            arguments.add("-i");
            arguments.add(namespaceMask);
        }
        arguments.addAll(Arrays.asList(entryClasses));
        sj.entry(arguments.toArray(new String[arguments.size()]));

        skimOutMetaInf(toutJar);
        addLicenses(toutJar,sj.getClassPathSet(entryClasses));
        SimpleJar.combineJars(outJar,SimpleJar.getBasicManifest(null,"ToolMaker"),toutJar);
        new File(toutJar).deleteOnExit();
    }

    private static void addLicenses(String jar, Set<String> jarDependencies) {
        PropertyDeluxe licenses = new PropertyDeluxe("license.properties");
        ZipFile zipFile = new ZipFile(jar);
        String entryPrefix = "META-INF/licenses/";
        zipFile.addDirectoryByName(entryPrefix);
        for (String jarDependency : jarDependencies) {
            String jarName = new File(jarDependency).getName();
            String licPrefix = entryPrefix + jarName + "/";
            if (!licenses.hasKey(jarName) || licenses.getProperty(jarName).equals("null")) {
                System.out.println("Missing license: " + jarName); //todo: better logging?
                continue;
            }
            zipFile.addDirectoryByName(licPrefix);
            Object licenseo = licenses.getProperty(jarName);
            if (!(licenseo instanceof List))
                licenseo = Arrays.asList((String) licenseo);
            @SuppressWarnings("unchecked") //explicitly dealing with this
            List<String> license = (List<String>) licenseo;
            for (String lic : license) {
                if (isLicenseInJar(lic,jarDependency)) {
                    final String jarFile = jarDependency;
                    final String lc = lic;
                    zipFile.addEntry(licPrefix + lic,new ZipFile.ZipEntrySource() {
                        public void writeData(OutputStream os) throws IOException {
                            StreamConnector.connectStreams(new ZipFile(jarFile).getInputStream(lc),os);
                        }

                        public void close() throws IOException { }
                    });
                } else {
                    zipFile.addEntry(licPrefix + lic,new ZipFile.FileZipEntrySource(SimpleJar.class.getClassLoader().getResource(lic).getPath()));
                }
            }
        }
        zipFile.write();
    }

    private static boolean isLicenseInJar(String license, String jar) {
        ZipFile zipFile = new ZipFile(jar);
        return zipFile.getEntryList().get(ZipFile.LIST_FILE_KEY).contains(license);
    }

    private static void skimOutMetaInf(String jar) {
        ZipFile zipFile = new ZipFile(jar);
        for (String entry : zipFile) {
            if (entry.toLowerCase().startsWith("meta-inf"))
                zipFile.deleteEntry(entry);
        }
        zipFile.write();
    }
}
