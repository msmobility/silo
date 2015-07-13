package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.Map;
import java.util.Set;

/**
 * The {@code CalculationDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started 2/14/11 4:59 PM
 */
public class SimpleCalculationDataProviderTest extends DataProviderTest {
    protected SimpleCalculationDataProvider calculationDataProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Override
    protected DataProvider getProvider(int id, int length, Map<String, double[]> data) {
        DataProvider baseProvider = new SimpleDataProvider(data,ArrayTensor.getFactory());
        return new SimpleCalculationDataProvider(id,baseProvider,ArrayTensor.getFactory());
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        return null; //cannot be uninitialized
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        calculationDataProvider = (SimpleCalculationDataProvider) provider;
    }
}
