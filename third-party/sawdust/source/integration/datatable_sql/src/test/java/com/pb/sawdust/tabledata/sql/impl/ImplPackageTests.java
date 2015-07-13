package com.pb.sawdust.tabledata.sql.impl;

import com.pb.sawdust.tabledata.sql.impl.hsqldb.HsqldbPackageTests;
import com.pb.sawdust.tabledata.sql.impl.sqlite.SqlitePackageTests;
import com.pb.sawdust.tabledata.sql.impl.derby.DerbyPackageTests;
import com.pb.sawdust.tabledata.sql.impl.h2.H2PackageTests;

/**
 * @author crf <br/>
 *         Started: Dec 5, 2008 9:41:21 AM
 */
public class ImplPackageTests {  
    public static void main(String ... args) {
        H2PackageTests.main();
        HsqldbPackageTests.main();
        SqlitePackageTests.main();
        DerbyPackageTests.main();
    }
}
