package com.pb.sawdust.tensor.decorators.id;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code IdPackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 8:11:35 AM
 */
public class TensorIdFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    
    static {
        TEST_CLASSES.add(TensorFactoryIdTensorTest.class);
    }
    
    public static List<List<String>> randomIds(RandomDeluxe random, int ... dimensions) {
        List<List<String>> ids = new ArrayList<List<String>>();
        for (int i : dimensions) {
            List<String> id = new ArrayList<String>();
            for (int j : range(i))
                id.add(random.nextAsciiString(random.nextInt(5,8)));
            ids.add(id);
        }
        return ids;
    }

    public static class TensorFactoryIdTensorTest extends IdTensorTest<Double,String> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IdTensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdTensor<Double,String> tensor = factory.tensor(ids, ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return randomIds(random,dimensions);
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }
}
