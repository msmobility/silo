package com.pb.sawdust.tensor.decorators;

import com.pb.sawdust.tensor.decorators.size.SizePackageTests;
import com.pb.sawdust.tensor.decorators.primitive.PrimitivePackageTests;
import com.pb.sawdust.tensor.decorators.id.IdPackageTests;

/**
 * @author crf <br/>
 *         Started: Feb 2, 2009 12:13:17 PM
 */
public class DecoratorsPackageTests {
    public static void main(String ... args) {
        SizePackageTests.main();
        PrimitivePackageTests.main();
        IdPackageTests.main();
    }
}
