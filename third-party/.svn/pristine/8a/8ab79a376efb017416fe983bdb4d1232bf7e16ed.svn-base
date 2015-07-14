package com.pb.sawdust.util;

import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Feb 7, 2009 11:40:20 AM
 */
public class JavaTypeTest extends TestBase {

    public static void main(String ... args) {
        TestBase.main();
    }

    @Test
    public void testGetJavaTypeByte() {
        assertEquals(JavaType.BYTE,JavaType.getJavaType(byte.class));
    }

    @Test
    public void testGetJavaTypeByteObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Byte.class));
    }

    @Test
    public void testGetJavaTypeShort() {
        assertEquals(JavaType.SHORT,JavaType.getJavaType(short.class));
    }

    @Test
    public void testGetJavaTypeShortObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Short.class));
    }

    @Test
    public void testGetJavaTypeInt() {
        assertEquals(JavaType.INT,JavaType.getJavaType(int.class));
    }

    @Test
    public void testGetJavaTypeIntObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Integer.class));
    }

    @Test
    public void testGetJavaTypeLong() {
        assertEquals(JavaType.LONG,JavaType.getJavaType(long.class));
    }

    @Test
    public void testGetJavaTypeLongObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Long.class));
    }

    @Test
    public void testGetJavaTypeFloat() {
        assertEquals(JavaType.FLOAT,JavaType.getJavaType(float.class));
    }

    @Test
    public void testGetJavaTypeFloatObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Float.class));
    }

    @Test
    public void testGetJavaTypeDouble() {
        assertEquals(JavaType.DOUBLE,JavaType.getJavaType(double.class));
    }

    @Test
    public void testGetJavaTypeDoubleObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Double.class));
    }

    @Test
    public void testGetJavaTypeBoolean() {
        assertEquals(JavaType.BOOLEAN,JavaType.getJavaType(boolean.class));
    }

    @Test
    public void testGetJavaTypeBooleanObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Boolean.class));
    }

    @Test
    public void testGetJavaTypeChar() {
        assertEquals(JavaType.CHAR,JavaType.getJavaType(char.class));
    }

    @Test
    public void testGetJavaTypeCharObject() {
        assertEquals(JavaType.OBJECT,JavaType.getJavaType(Character.class));
    }

    @Test
    public void testGetComponentTypePrimitive() {
        assertEquals(JavaType.BOOLEAN,JavaType.getComponentType(new boolean[0]));
    }

    @Test
    public void testGetComponentTypeObject() {
        assertEquals(JavaType.OBJECT,JavaType.getComponentType(new Boolean[0]));
    }

    @Test
    public void testGetComponentTypeMultidimensionalPrimitive() {
        assertEquals(JavaType.OBJECT,JavaType.getComponentType(new boolean[0][]));
    }

    @Test
    public void testGetBaseComponentTypePrimitive() {
        assertEquals(JavaType.BOOLEAN,JavaType.getBaseComponentType(new boolean[0]));
    }

    @Test
    public void testGetBaseComponentTypeObject() {
        assertEquals(JavaType.OBJECT,JavaType.getBaseComponentType(new Boolean[0]));
    }

    @Test
    public void testGetBaseComponentTypeMultidimensionalPrimitive() {
        assertEquals(JavaType.BOOLEAN,JavaType.getBaseComponentType(new boolean[0][]));
    }
}
