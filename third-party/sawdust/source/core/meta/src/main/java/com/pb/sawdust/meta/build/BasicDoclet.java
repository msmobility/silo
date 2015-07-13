package com.pb.sawdust.meta.build;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.ProcessUtil;
import com.pb.sawdust.util.SystemType;

import java.io.*;
import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Dec 26, 2008 12:24:38 PM
 */
public class BasicDoclet {

    public static void main(String ... args) {
        String basePath = "d:\\code\\work\\java\\sawdust\\";
        String sourcePath = basePath + "src\\java";
        String outputPath = basePath + "doc\\javadoc";
        String baseClassPath = basePath + "lib";
        String jd2chmPath = baseClassPath + "\\jd2chm.exe";
        FilenameFilter ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.substring(name.length() - 4).equals(".jar");
            }
        };
        StringBuilder jarSb = null;
        for (File jar : new File(baseClassPath).listFiles(ff))
            if (jarSb == null)
                jarSb = new StringBuilder(jar.getPath());
            else
                jarSb.append(";").append(jar.getPath());
        File outputPathFile = new File(outputPath);
        if (outputPathFile.exists()) {
            if (!FileUtil.clearDir(outputPathFile)) {
                System.out.println("Failed clearing output directory.");
                System.exit(1);
            }
        }

        ProcessUtil.runProcess(Arrays.asList("javadoc",
                "-sourcepath",sourcePath,
                "-classpath",jarSb.toString(),
                "-d",outputPath,
                "-group","IO Packages","com.pb.sawdust.io*",
                "-group","Table Data Packages","com.pb.sawdust.tabledata*",
                "-group","Tensor Packages","com.pb.sawdust.tensor*:com.pb.sawdust.calculator.tensor*",
                "-group","Utility Packages","com.pb.sawdust.util*",
                "-group","Econometric Modeling Packages","com.pb.sawdust.calculator.utility*",
                "-group","Modeling Support Packages","com.pb.sawdust.model*",
                "-subpackages","com.pb.sawdust",
                "-exclude","com.pb.sawdust.cookbook.tabledata"));

        if (new File(jd2chmPath).exists() && SystemType.getSystemType().getFamilies().contains(SystemType.SystemFamily.WINDOWS)) {
            String projectName = "Sawdust_API";
            String projectTitle = "Sawdust Javadoc API";
            ProcessUtil.runProcess(Arrays.asList(jd2chmPath,"-t",projectTitle,"-p",projectName),outputPathFile);
            File outputChm = new File(new File(outputPathFile,".."),projectName + ".chm");
            if (outputChm.exists())
                if (!outputChm.delete())
                    throw new RuntimeException("Could not delete existing chm file: " + outputChm);
            FileUtil.move(new File(outputPathFile,projectName + ".chm"),outputChm);
        } else {
            System.out.println("Note: cannot build chm (Windows help file) on a non-windows system.");
        }
    }


}
