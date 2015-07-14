package com.pb.sawdust.model.integration.libraries;

import java.io.File;
import java.lang.reflect.Field;

/**
 * The {@code LibUtil} ...
 *
 * @author crf <br/>
 *         Started Oct 18, 2010 10:37:36 AM
 */
public class LibUtil {
    private static boolean ALLOW_LIBRARY_PATH_HACKING = true; //probably should be turned off in production code for security reasons

    public static void addLibraryPath(String libraryName) {
        //allows a library found in the classpath to have its path added to library path
        //will work with Sun (ahem, Oracle) JVMs, but uses some class reflection hacking so shouldn't be relied on
        //this is convenient for an internal environment so java.library.path doesn't have to be set for every run target
        if (!ALLOW_LIBRARY_PATH_HACKING)
            return;
        try {
            System.loadLibrary(libraryName);
            return; //no need to add to path, already there
        } catch (UnsatisfiedLinkError e) {
            //swallow, we need to add to path
        }
        libraryName = System.mapLibraryName(libraryName);
        String pathSeparator = System.getProperty("path.separator");
        for (String path : System.getProperty("java.class.path").split(pathSeparator)) {
            File f = new File(path,libraryName);
            if (f.exists()) {
                System.setProperty("java.library.path",System.getProperty("java.library.path") + pathSeparator + path);
                Class c = ClassLoader.class;
                try {
                    Field sys_paths = c.getDeclaredField("sys_paths");
                    boolean accessible = sys_paths.isAccessible();
                    if (!accessible)
                        sys_paths.setAccessible(true);
                    sys_paths.set(c,null); //this will make it reset itself and reload the library path
                    sys_paths.setAccessible(accessible);
                    for (String s : (String[]) c.getDeclaredField("sys_paths").get(c))
                System.out.println(s);
                } catch (NoSuchFieldException e) {
                    return; //can't do it, so move on
                } catch (IllegalAccessException e) {
                    return; //can't do it, so move on
                }
                return;
            }
        }
    }
}
