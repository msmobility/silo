package com.pb.sawdust.util;

import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Serializable;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;

/**
 * @author crf <br/>
 *         Started: Sep 5, 2008 9:29:06 AM
 */
public class ClassInspectorTest extends TestBase {

    public static void main(String ... args) {
        TestBase.main();
    }

    @Test
    public void testLeastCommonClassSameClass() {
        Class<String> c1 = String.class;
        Class<String> c2 = String.class;
        assertEquals(c2, ClassInspector.getLowestCommonClass(c1,c2));
    }

    @Test
    public void testLeastCommonClassObject() {
        Class<String> c1 = String.class;
        Class<Object> c2 = Object.class;
        assertEquals(c2, ClassInspector.getLowestCommonClass(c1,c2));
    }

    @Test
    public void testLeastCommonClassUnrelated() {
        Class<String> c1 = String.class;
        Class<IllegalArgumentException> c2 = IllegalArgumentException.class;
        assertEquals(Object.class, ClassInspector.getLowestCommonClass(c1,c2));
    }

    @Test
    public void testLeastCommonClassDeeplyRelated() {
        Class<SecurityException> c1 = SecurityException.class;
        Class<StackOverflowError> c2 = StackOverflowError.class;
        assertEquals(Throwable.class, ClassInspector.getLowestCommonClass(c1,c2));
    }

    @SuppressWarnings("unchecked") //generic array creation, no biggie
    @Test
    public void testLeastCommonClassMulitpleClasses() {
        Class<?> c1 = DataInputStream.class;
        Class<?> c2 = DigestInputStream.class;
        Class<?> c3 = ByteArrayInputStream.class;
        assertEquals(InputStream.class,ClassInspector.getLowestCommonClass(c1,c2,c3));
    }

    @SuppressWarnings("unchecked") //generic array creation, no biggie
    @Test
    public void testLeastCommonClassOneClass() {
        assertEquals(String.class,ClassInspector.getLowestCommonClass(String.class));
    }

    @SuppressWarnings("unchecked") //supposed to throw an error for a different reason, so ignore
    @Test(expected=IllegalArgumentException.class)
    public void testLeastCommonClassNoClass() {
        ClassInspector.getLowestCommonClass(new Class[0]);
    }

    @Test
    public void testLeastCommonClassMulitpleObjects() {
        Object o1 = 8;
        Object o2 = 5.6;
        Object o3 = new BigInteger("34");
        assertEquals(Number.class,ClassInspector.getLowestCommonClass(o1,o2,o3));
    }

    @Test
    public void testLeastCommonClassOneObject() {
        assertEquals(String.class,ClassInspector.getLowestCommonClass(""));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLeastCommonClassNoObject() {
        ClassInspector.getLowestCommonClass(new Object[0]);
    }

    @Test
    public void testGetLowestCommonComponentClass() {
        Integer[] a1 = new Integer[0];
        Float[] a2 = new Float[0];
        BigInteger[] a3 = new BigInteger[0];
        assertEquals(Number.class,ClassInspector.getLowestCommonComponentClass(a1,a2,a3));
    }

    @Test
    public void testGetLowestCommonComponentClassOneArray() {
        assertEquals(String.class,ClassInspector.getLowestCommonComponentClass(new String[0]));
    }

    @SuppressWarnings("unchecked") //generic array creation, no biggie - but will throw an error, so doesn't matter
    @Test(expected=IllegalArgumentException.class)
    public void testGetLowestCommonComponentClassNoArrays() {
        ClassInspector.getLowestCommonComponentClass();
    }

    @Test
    public void testInterfaceIntersectionInclusive() {
        Class<Double> c1 = Double.class;
        Class<Integer> c2 = Integer.class;
        Set<Class<?>> c1Interfaces = new HashSet<Class<?>>(Arrays.asList(c1.getInterfaces()));
        assertEquals(c1Interfaces, ClassInspector.getInterfaceIntersection(c1,c2));
    }

    @Test
    public void testInterfaceIntersectionExclusive() {
        Class<Double> c1 = Double.class;
        Class<Package> c2 = Package.class;
        assertEquals(0, ClassInspector.getInterfaceIntersection(c1,c2).size());
    }

    @Test
    public void testInterfaceIntersectionMixed() {
        Class<String> c1 = String.class;
        Class<StringBuilder> c2 = StringBuilder.class;
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        interfaces.add(Serializable.class);
        interfaces.add(CharSequence.class);
        assertEquals(interfaces, ClassInspector.getInterfaceIntersection(c1,c2));
    }


}
