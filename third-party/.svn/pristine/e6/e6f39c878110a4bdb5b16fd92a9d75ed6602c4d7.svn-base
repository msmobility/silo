package com.pb.sawdust.util;

import java.util.*;
import static com.pb.sawdust.util.Range.*;

/**
 * The {@code ClassInspector} class provides static methods for analyzing {@code Class} objects. It is intended to
 * augment the methods contained in {@code Class}.
 *
 * @author crf <br/>
 *         Started: Sep 4, 2008 3:31:55 PM
 */
public class ClassInspector {

    private ClassInspector() {} //should not be instantiated

    /**
     * Get the "lowest" class shared by two classes in their class heirarchies.  For example, if one class's heirarchy
     * is <tt>Object-A-B</tt>, and anothers is <tt>Object-A-C</tt>, then <tt>A</tt> would be their lowest common class.
     * Since all objects descend from {@code Object}, the lowest common class for two completely unrelated classes would
     * be {@code Object}.
     *
     * @param class1
     *        The first class.
     *
     * @param class2
     *        The second class.
     *
     * @param <T>
     *        The type (or, possibly supertype) of the lowest common class.
     *
     * @return the lowest common class in the inheritence trees for {@code class1} and {@code class2}.
     */
    @SuppressWarnings("unchecked") //eventually everything will descend from T, so all of these casts are ok
    public static <T> Class<? extends T> getLowestCommonClass(Class<? extends T> class1, Class<? extends T> class2) {
        if (class1 == null || class2 == null)
            return null;
        if (class1.isAssignableFrom(class2))
            return class1;
        if (class2.isAssignableFrom(class1))
            return class2;
        return ClassInspector.getLowestCommonClass((Class<T>) class1.getSuperclass(),(Class<T>) class2.getSuperclass());
    }

    /**
     * Get the "lowest" class shared by a series of classes in their class heirarchies.  For example, if one class's heirarchy
     * is <tt>Object-A-B</tt>, and anothers is <tt>Object-A-C</tt>, then <tt>A</tt> would be their lowest common class.
     * Since all objects descend from {@code Object}, the lowest common class for two completely unrelated classes would
     * be {@code Object}. If only one class is passed in, then that class is returned.
     *
     * @param classes
     *        The classes.
     *
     * @param <T>
     *        The type (or, possibly supertype) of the lowest common class.
     *
     * @return the lowest common class in the inheritence trees {@code classes}.
     *
     * @throws IllegalArgumentException if {@code classes} is empty.
     */
    @SuppressWarnings("unchecked") //eventually everything will descend from T, so all of these casts are ok
    public static <T> Class<? extends T> getLowestCommonClass(Class<? extends T> ... classes) {
        if (classes.length < 1)
            throw new IllegalArgumentException("Lowest common class requires at least one input class.");
        Class<? extends T> lcc = classes[0];
        for (int i = 1; i < classes.length; i++)
            lcc = getLowestCommonClass(lcc,classes[i]);
        return lcc;
    }

    /**
     * Get the "lowest" class shared by a series of objects in their class heirarchies.  For example, if one object's
     * class heirarchy is <tt>Object-A-B</tt>, and anothers is <tt>Object-A-C</tt>, then <tt>A</tt> would be their lowest
     * common class. Since all objects descend from {@code Object}, the lowest common class for two completely unrelated
     * classes would be {@code Object}. If only one object is passed in, then that object's is returned.
     *
     * @param objects
     *        The objects in question.
     *
     * @return the lowest common class for {@code objects}.
     *
     * @throws IllegalArgumentException if {@code objects} is empty.
     */
    @SuppressWarnings("unchecked") //eventually everything will descend from ?, so T is irrelevant
    public static Class<?> getLowestCommonClass(Object ... objects) {
        if (objects.length < 1)
            throw new IllegalArgumentException("Lowest common class requires at least one input object.");
        Class[] classes = new Class[objects.length];
        for (int i : range(classes.length))
            classes[i] = objects[i].getClass();
        return getLowestCommonClass(classes);
    }

    /**
     * Get the "lowest" component class shared by a series of arrays in their copmonent class heirarchies. This is
     * essentially a convenience method for calling {@link #getLowestCommonClass(Class[])} on the component classes
     * of the input arrays.
     *
     * @param arrays
     *        The arrays in question.
     *
     * @param <T>
     *        The type of the lowest common class.
     *
     * @param <S>
     *        The type(s) of the input arrays.
     *
     * @return the lowest common component class for {@code arrays}.
     *
     * @throws IllegalArgumentException if {@code arrays} is empty.
     */
    @SuppressWarnings("unchecked") //because input arrays extend T, we'll certainly get a T (or a subtype of T, which can be cast up)
    @SafeVarargs
    public static <T,S extends T> Class<? extends T> getLowestCommonComponentClass(S[] ... arrays) {
        if (arrays.length < 1)
            throw new IllegalArgumentException("Lowest common component class requires at least one input array.");
        Class[] classes = new Class[arrays.length];
        for (int i : range(classes.length))
            classes[i] = arrays[i].getClass().getComponentType();
        return getLowestCommonClass(classes);
    }

    /**
     * Get the set of shared interfaces between two classes. If no interfaces are shared by the two classes, an empty
     * set is returned. 
     *
     * @param class1
     *        The first class.
     *
     * @param class2
     *        The second class.
     *
     * @return the set of interfaces implemented by {@code class1} and {@code class2}.
     */
    public static Set<Class<?>> getInterfaceIntersection(Class<?> class1, Class<?> class2) {
        Set<Class<?>> intersection = new HashSet<Class<?>>(Arrays.asList(class1.getInterfaces()));
        Set<Class<?>> class2Interfaces = new HashSet<Class<?>>(Arrays.asList(class2.getInterfaces()));
        Iterator<Class<?>> iterator = intersection.iterator();
        while (iterator.hasNext())
            if (!class2Interfaces.contains(iterator.next()))
                iterator.remove();
        return Collections.unmodifiableSet(intersection);
    }
}
