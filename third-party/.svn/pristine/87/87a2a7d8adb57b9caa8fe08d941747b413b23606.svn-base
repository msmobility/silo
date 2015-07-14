package com.pb.sawdust.tabledata.sql.impl;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.test.TestBase;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The {@code SqlImplTestUtil} ...
 *
 * @author crf <br/>
 *         Started Dec 27, 2010 11:08:49 AM
 */
public class SqlImplTestUtil {
    public static final File DATABASE_TEMP_DIR = TestBase.getTemporaryFileDirectory(SqlImplTestUtil.class,new File("db_temp"));
    public static final String SKIP_FINISH_OPERATIONS_INDICATOR = "skip finish operations";
    public static final String[] SKIP_FINISH_OPERATIONS_ARGS = {SKIP_FINISH_OPERATIONS_INDICATOR};

    public static String[] formShouldSkipFinishOperationsMainArgs() {
        return SKIP_FINISH_OPERATIONS_ARGS;
    }

    public static boolean shouldPerformTestFinishOperations(String[] args) {
        return args.length == 0 || !args[0].equals(SKIP_FINISH_OPERATIONS_INDICATOR);
    }

    public static void performTestFinishOperations() {
        //need to delete database directory
        FileUtil.deleteDirOnExit(SqlImplTestUtil.DATABASE_TEMP_DIR);
    }

    public static File getDatabasePath(String name) {
        return new File(DATABASE_TEMP_DIR,name + "_" + Thread.currentThread().getId());
    }

    public static FilenameFilter getDatabasePathFilter(String name) {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.equals(DATABASE_TEMP_DIR) && name.equals(name);
            }
        };
    }
}
