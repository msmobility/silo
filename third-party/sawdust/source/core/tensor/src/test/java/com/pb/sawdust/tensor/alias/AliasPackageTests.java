package com.pb.sawdust.tensor.alias;

import com.pb.sawdust.tensor.alias.matrix.MatrixPackageTests;
import com.pb.sawdust.tensor.alias.vector.VectorPackageTests;
import com.pb.sawdust.tensor.alias.scalar.ScalarPackageTests;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 11:03:12 AM
 */
public class AliasPackageTests {
    public static void main(String ... args)  {
        ScalarPackageTests.main();
        VectorPackageTests.main();
        MatrixPackageTests.main();
    }
}
