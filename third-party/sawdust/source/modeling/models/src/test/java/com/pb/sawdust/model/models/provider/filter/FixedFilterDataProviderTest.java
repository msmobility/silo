package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.DataProviderTest;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * The {@code FixedFilterDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started 3/7/11 8:18 PM
 */
public class FixedFilterDataProviderTest extends DataProviderTest {
    protected FixedFilterDataProvider filterDataProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Override
    protected DataProvider getProvider(int id, int dataLength, Map<String, double[]> data) {
        List<Boolean> filter = new LinkedList<Boolean>();
        int counter = dataLength;
        while (counter > 0) {
            boolean e = random.nextBoolean();
            filter.add(e);
            if (e)
                counter--;
        }
        int parentSize = filter.size();
        BooleanVector filterVector = ArrayTensor.getFactory().booleanVector(parentSize);
        for (int i : range(parentSize))
            filterVector.setCell(filter.get(i),i);
        //now fill in gaps with some junk
        Map<String, double[]> parentData = new HashMap<String, double[]>();
        for (String key : data.keySet()) {
            double[] pData = new double[parentSize];
            double[] d = data.get(key);
            counter = 0;
            for (int i : range(parentSize))
                pData[i] = filterVector.getCell(i) ? d[counter++] : random.nextDouble();
            parentData.put(key,pData);
        }
        //make parent provider
        DataProvider p = new SimpleDataProvider(parentData,ArrayTensor.getFactory());
        return new FixedFilterDataProvider(ArrayTensor.getFactory(),p,filterVector);
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        return null;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        filterDataProvider = (FixedFilterDataProvider) provider;
    }

    @Override
    @Ignore
    @Test
    public void testGetId() {
        //no way to set id
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorBadFilterVectorTooBig() {
        new FixedFilterDataProvider(ArrayTensor.getFactory(),new SimpleDataProvider(data,ArrayTensor.getFactory()),ArrayTensor.getFactory().booleanVector(dataLength+1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorBadFilterVectorTooSmall() {
        new FixedFilterDataProvider(ArrayTensor.getFactory(),new SimpleDataProvider(data,ArrayTensor.getFactory()),ArrayTensor.getFactory().booleanVector(dataLength-1));
    }
}
