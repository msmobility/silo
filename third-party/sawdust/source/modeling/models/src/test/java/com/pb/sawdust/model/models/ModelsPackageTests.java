package com.pb.sawdust.model.models;

import com.pb.sawdust.model.models.logit.LogitPackageTests;
import com.pb.sawdust.model.models.provider.ProviderPackageTests;
import com.pb.sawdust.model.models.utility.UtilityPackageTests;

/**
 * The {@code ModelsPackageTests} ...
 *
 * @author crf <br/>
 *         Started Oct 5, 2010 1:56:31 AM
 */
public class ModelsPackageTests {
    public static void main(String ... args) {
        RegressionModelTest.main();
        UtilityPackageTests.main();
        ProviderPackageTests.main();
        LogitPackageTests.main();
    }
}
