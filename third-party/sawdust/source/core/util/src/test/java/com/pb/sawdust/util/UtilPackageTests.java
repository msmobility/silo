package com.pb.sawdust.util;

import com.pb.sawdust.util.abacus.AbacusPackageTests;
import com.pb.sawdust.util.format.FormatPackageTests;
import com.pb.sawdust.util.parsing.ParsingPackageTests;
import com.pb.sawdust.util.array.ArrayPackageTests;

/**
 * @author crf <br/>
 *         Started: Sep 5, 2008 12:32:09 AM
 */
public class UtilPackageTests {

    public static void main(String ... args) {
        RangeTest.main();
        FilterChainTest.main();
        ClassInspectorTest.main();
        ThreadTimerTest.main();
        JavaTypeTest.main();
        FormatPackageTests.main();
        ParsingPackageTests.main();
        ArrayPackageTests.main();
        AbacusPackageTests.main();
    }
}
