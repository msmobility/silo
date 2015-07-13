package com.pb.sawdust.tabledata.sql.impl.derby;

import com.pb.sawdust.tabledata.sql.SqlDataTablePartitionTest;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;

/**
 * The {@code DerbySqlDataTablePartitionTest} ...
 *
 * @author crf <br/>
 *         Started 3/13/11 3:21 PM
 */
public class DerbySqlDataTablePartitionTest extends SqlDataTablePartitionTest {

    public static void main(String ... args) {
        //if should perform finish operations, then this test is being run by itself, so make sure that the derby dir is clear
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            DerbyPackageTests.performTestStartOperations();
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            TestBase.classFinished(new Runnable() {
                public void run() {
                     DerbyPackageTests.performTestFinishOperations();
                }
            });
    }
}
