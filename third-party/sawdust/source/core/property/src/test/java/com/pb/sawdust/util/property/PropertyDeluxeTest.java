package com.pb.sawdust.util.property;

import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.io.TextFile;
import com.pb.sawdust.util.test.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

/**
 * The {@code PropertyDeluxeTest} ...
 *
 * @author crf <br/>
 *         Started Oct 5, 2010 8:37:42 PM
 */
public class PropertyDeluxeTest extends TestBase {
    public static final String NAMESPACE_TYPE_KEY = "namespace type";

    protected static enum NamespaceType {
        NONE,
        ONE_A,
        ONE_B,
        BOTH_A_B
    }

    protected PropertyDeluxe propertyDeluxe;
    protected Map<String,Object[]> noNameData;
    protected Map<String,Object[]> aData;
    protected Map<String,Object[]> abData;
    protected Map<String,Object[]> bData;
    protected Map<String,Object[]> data;
    protected List<String> keys;
    protected String[] namespace;

    public static void main(String ... args) {
            TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (NamespaceType type : NamespaceType.values())
            context.add(buildContext(NAMESPACE_TYPE_KEY,type));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    private Map<String,Object[]> getPropertyData() {
        Map<String,Object[]> propertyData = new LinkedHashMap<String,Object[]>();
        for (int i : range(12)) {
            String k = random.nextAlphaNumericString(random.nextInt(12,27));
            Object[] value = null;
            switch (i) {
                case 0 : value = new Object[] {k,random.nextInt() + ""}; break;
                case 1 : value = new Object[] {k,random.nextInt()}; break;
                case 2 : value = new Object[] {k,random.nextDouble()}; break;
                case 3 : value = new Object[] {k,random.nextBoolean()}; break;
                case 4 : value = new Object[] {k,random.nextAlphaNumericString(random.nextInt(3,8))}; break;
                case 5 : value = new Object[] {k,random.nextAlphaNumericString(random.nextInt(3,8)),random.nextAlphaNumericString(random.nextInt(3,8)),random.nextAlphaNumericString(random.nextInt(3,8))}; break;
                case 6 : value = new Object[] {k,random.nextInt(),random.nextInt(),random.nextInt()}; break;
                case 7 : value = new Object[] {k,random.nextInt(),random.nextInt(),random.nextInt(),random.nextInt(),random.nextInt(),random.nextInt(),random.nextInt(),random.nextInt()}; break;
                case 8 : value = new Object[] {k,random.nextBoolean(),random.nextInt(),random.nextBoolean(),random.nextInt(),random.nextBoolean(),random.nextInt(),random.nextBoolean(),random.nextInt(),random.nextBoolean(),random.nextInt()}; break;
                case 9 : value = new Object[] {k,random.nextBoolean() + "",random.nextDouble() + "",random.nextInt() + "",random.nextAlphaNumericString(8)}; break;
                case 10 : value = new Object[] {k,random.nextInt(),random.nextDouble() + "",random.nextInt(),random.nextDouble() + ""}; break;
                case 11 : value = new Object[] {k,random.nextInt(),random.nextDouble() + "",random.nextBoolean(),random.nextBoolean(),random.nextDouble() + "",random.nextBoolean(),random.nextInt(),random.nextDouble() + "",random.nextBoolean()}; break;
            }
            propertyData.put(k,value);
        }
        return propertyData;
    }

    protected List<String> getPropertyFileData(Map<String,Object[]> propertyData) {
        //properties.length = 12
        Iterator<String> kit = propertyData.keySet().iterator();
        List<String> lines = new LinkedList<String>();
        lines.add(String.format("%s = %s",propertyData.get(kit.next())));
        lines.add(String.format("%s(I) = %d",propertyData.get(kit.next())));
        lines.add(String.format("%s(D) = %f",propertyData.get(kit.next())));
        lines.add(String.format("%s(B) = %b",propertyData.get(kit.next())));
        lines.add(String.format("%s(S) = %s",propertyData.get(kit.next())));
        lines.add(String.format("%s(L) = [%s,%s,%s]",propertyData.get(kit.next())));
        lines.add(String.format("%s(LI) = [%d,%d,%d]",propertyData.get(kit.next())));
        lines.add(String.format("%s(LLI) = [[%d,%d,%d],[%d,%d],[%d,%d,%d]]",propertyData.get(kit.next())));
        lines.add(String.format("%s(LMBI) = [{%b:%d},{%b:%d,%b:%d},{%b:%d,%b:%d}]",propertyData.get(kit.next())));
        lines.add(String.format("%s(M) = {%s:%s,%s:%s}",propertyData.get(kit.next())));
        lines.add(String.format("%s(MIS) = {%d:%s,%d:%s}",propertyData.get(kit.next())));
        lines.add(String.format("%s(MIMSLB) = {%d:{%s:[%b,%b],%s:[%b]},%d:{%s:[%b]}}",propertyData.get(kit.next())));
        return lines;
    }

    protected String getResourceName() {
        return "com.pb.sawdust.util.property.testit";
    }

    protected File getResourceFile() {
        return getTemporaryFile(PropertyDeluxeTest.class,getResourceName() + ".properties");
    }

    @Before
    public void beforeTest() {
        noNameData = getPropertyData();
        aData = getPropertyData();
        bData = getPropertyData();
        abData = getPropertyData();
        List<String> lines = getPropertyFileData(noNameData);
        lines.add("+a");
        lines.addAll(getPropertyFileData(aData));
        lines.add("+b");
        lines.addAll(getPropertyFileData(abData));
        lines.add("-a");
        lines.addAll(getPropertyFileData(bData));
        TextFile tf = new TextFile(getResourceFile(),true);
        tf.writeLines(lines);
        NamespaceType type = ((NamespaceType) getTestData(NAMESPACE_TYPE_KEY));
        switch (type) {
            case NONE : {
                data = noNameData;
                namespace = new String[0];
                break;
            }
            case ONE_A : {
                data = aData;
                namespace = new String[] {"a"};
                break;
            }
            case ONE_B : {
                data = bData;
                namespace = new String[] {"b"};
                break;
            }
            case BOTH_A_B : {
                data = abData;
                namespace = new String[] {"a","b"};
                break;
            }
        }
        keys = new ArrayList<String>(data.keySet());
        propertyDeluxe = new PropertyDeluxe(getResourceFile().getPath());
    }

    @After
    public void afterTest() {
        getResourceFile().delete();
    }

    @Test
    public void testGetProperty() {
        assertEquals(data.get(keys.get(0))[1],propertyDeluxe.getProperty(keys.get(0),namespace));
    }

    @Test
    public void testGetIntFromString() {
        assertEquals(Integer.parseInt((String) data.get(keys.get(0))[1]),propertyDeluxe.getInt(keys.get(0),namespace));
    }

    @Test
    public void testGetInt() {
        assertEquals(data.get(keys.get(1))[1],propertyDeluxe.getInt(keys.get(1),namespace));
    }

    @Test
    public void testGetDouble() {
        assertAlmostEquals(data.get(keys.get(2))[1],propertyDeluxe.getDouble(keys.get(2),namespace));
    }

    @Test
    public void testGetIntFromDouble() {
        assertEquals(((Double) data.get(keys.get(2))[1]).intValue(),propertyDeluxe.getInt(keys.get(2),namespace));
    }

    @Test
    public void testGetBoolean() {
        assertEquals(data.get(keys.get(3))[1],propertyDeluxe.getBoolean(keys.get(3),namespace));
    }

    @Test
    public void testGetString() {
        assertEquals(data.get(keys.get(4))[1],propertyDeluxe.getString(keys.get(4),namespace));
    }

    @Test
    public void testGetList1() {
        assertEquals(Arrays.asList(Arrays.copyOfRange(data.get(keys.get(5)),1,data.get(keys.get(5)).length)),propertyDeluxe.getList(keys.get(5),namespace));
    }

    @Test
    public void testGetList2() {
        assertEquals(Arrays.asList(Arrays.copyOfRange(data.get(keys.get(6)),1,data.get(keys.get(6)).length)),propertyDeluxe.getList(keys.get(6),namespace));
    }

    @Test
    public void testGetList3() {
        List<List<Object>> list = new LinkedList<List<Object>>();
        Object[] d = data.get(keys.get(7));
        List<Object> list1 = new LinkedList<Object>();
        List<Object> list2 = new LinkedList<Object>();
        List<Object> list3 = new LinkedList<Object>();
        list1.add(d[1]);
        list1.add(d[2]);
        list1.add(d[3]);
        list2.add(d[4]);
        list2.add(d[5]);
        list3.add(d[6]);
        list3.add(d[7]);
        list3.add(d[8]);
        list.add(list1);
        list.add(list2);
        list.add(list3);
        assertEquals(list,propertyDeluxe.getList(keys.get(7),namespace));
    }

    @Test
    public void testGetList4() {
        List<Map<Object,Object>> list = new LinkedList<Map<Object,Object>>();
        Object[] d = data.get(keys.get(8));
        Map<Object,Object> map1 = new HashMap<Object,Object>();
        Map<Object,Object> map2 = new HashMap<Object,Object>();
        Map<Object,Object> map3 = new HashMap<Object,Object>();
        map1.put(d[1],d[2]);
        map2.put(d[3],d[4]);
        map2.put(d[5],d[6]);
        map3.put(d[7],d[8]);
        map3.put(d[9],d[10]);
        list.add(map1);
        list.add(map2);
        list.add(map3);
        assertEquals(list,propertyDeluxe.getList(keys.get(8),namespace));
    }

    @Test
    public void testGetMap1() {
        Map<Object,Object> map = new HashMap<Object,Object>();
        Object[] d = data.get(keys.get(9));
        map.put(d[1],d[2]);
        map.put(d[3],d[4]);
        assertEquals(map,propertyDeluxe.getMap(keys.get(9),namespace));
    }

    @Test
    public void testGetMap2() {
        Map<Object,Object> map = new HashMap<Object,Object>();
        Object[] d = data.get(keys.get(10));
        map.put(d[1],d[2]);
        map.put(d[3],d[4]);
        assertEquals(map,propertyDeluxe.getMap(keys.get(10),namespace));
    }

    @Test
    public void testGetMap3() {
        Map<Object,Object> map = new HashMap<Object,Object>();
        Object[] d = data.get(keys.get(10));
        map.put(d[1],d[2]);
        map.put(d[3],d[4]);
        assertEquals(map,propertyDeluxe.getMap(keys.get(10),namespace));
    }

    @Test
    public void testGetMap4() {
        Map<Object,Map<Object,List<Object>>> map = new HashMap<Object,Map<Object,List<Object>>>();
        Object[] d = data.get(keys.get(11));
        List<Object> list1 = new LinkedList<Object>();
        List<Object> list2 = new LinkedList<Object>();
        List<Object> list3 = new LinkedList<Object>();
        Map<Object,List<Object>> map1 = new HashMap<Object,List<Object>>();
        Map<Object,List<Object>> map2 = new HashMap<Object,List<Object>>();
        map.put(d[1],map1);
        map1.put(d[2],list1);
        list1.add(d[3]);
        list1.add(d[4]);
        map1.put(d[5],list2);
        list2.add(d[6]);
        map.put(d[7],map2);
        map2.put(d[8],list3);
        list3.add(d[9]);
        assertEquals(map,propertyDeluxe.getMap(keys.get(11),namespace));
    }

}
