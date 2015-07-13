package com.pb.sawdust.tensor;

import com.pb.sawdust.util.ContainsMetadata;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.id.IdTensor;

import java.util.Iterator;

/**
 * The {@code Tensor} interface is used for objects holding specifically typed, multidimensional, rectangular containers
 * of data. A tensor could be visualized as a non-jagged java {@code array} with more extensive (and extensible)
 * capabilities. A given tensor is defined by what type of data it holds and its dimensionality, or shape. Each tensor
 * dimension must have a non-zero length - degenerate dimensions are not allowed. All locations (positions where single
 * data elements are held) in a tensor are referenced by zero-based indices, each of which refers to the location of
 * the element in a particular dimension. For example, for a given 1x6 tensor, its first element would be indexed as
 * <code>(0,0)</code>, its second element <code>(0,1)</code>, all the way to its last element, <code>(0,5)</code>.
 * Each dimensions in a tensor is also referred to by a zero-based index, so the "first" dimension is indexed by {@code 0},
 * the "second" by {@code 1}, and so on. This zero-based indexing system keeps tensor indexing consistent with
 * conventions used injava (such as with arrays, strings, <i>etc.</i>).
 * <p>
 * Each tensor has an associated {@code Index} object which can be considered to be a repository of information for the
 * dimensions/indexing of the tensor. The index not only holds information about the shape of the tensor, but also
 * allows a tensor to transparently reference that of another tensor. For example, given a {@code 1x6} tensor:
 * <pre>
 *     <code>
 *         [2,4,6,8,10,12]
 *     </code>
 * </pre>
 * a 2x2 tensor may be defined whose first row is made up by the first and second element of the {@code 1x6} tensor, and
 * whose second row is made up by the fourth and fifth elements:
 * <pre>
 *     <code>
 *         [2, 4]
 *         [8,10]
 *     </code>
 * </pre>
 * Changes to the first tensor:
 * <pre>
 *     <code>
 *         [1,2,3,4,5,6]
 *     </code>
 * </pre>
 * will be reflected in the second:
 * <pre>
 *     <code>
 *         [1,2]
 *         [4,5]
 *     </code>
 * </pre>
 * and vice-versa. This tensor referencing allows the tensor memory footprint to be lower (only one copy of the data even
 * though there are more than one matrices) and as well as a simple way through which composed and sub-matrices can be
 * built. {@code Index} instances are also paramaterized so that ids (aliases) can be used for index referencing, instead
 * of the zero-based numeric index. Though this functionality is not directly used in this core interface, it is leveraged
 * in the {@link com.pb.sawdust.tensor.decorators.id.IdTensor} interface and its implementations.  Because {@code Index}
 * instances associated with matrices are accessible through the {@code getIndex()} method, they should generally be
 * made immutable so as to avoid internal state inconsitencies.
 * <p>
 * Every tensor is paramaterized with the type that it holds. To allow for matrices which hold primitives (type
 * parameters cannot be primitives) an additional type specification is provided through the {@code getType()} method.
 * If the tensor holds an object type, then this method should just return {@code JavaType.OBJECT}; however, if the
 * tensor holds a primitive type, then this method will return the corresponding {@code JavaType}. If the tensor
 * holds primitives, then it should be parameterized with the primitive's object equivalent. Because none of the
 * methods in this interface actually use primitives, a number of decorator interfaces are provided in the
 * {@code com.pb.sawdust.tensor.decorators.primitive} package which offer alternative methods using primitives
 * (which will then avoid any inefficiencies associated with autoboxing).  All tensors holding primitives are expected
 * to implement at least one of these extending interfaces.
 * <p>
 * The tensor iterator prescribed by {@code Iterable<Tensor<T>>} iterates over the "last" sub-matrices held by a given
 * tensor. More specifically, the dimensions of each iterated tensor is equal to the dimensions of the source tensor,
 * minus its last dimension; each tensor has the index for the last dimension incremented by one. So, given a
 * {@code 2x2x3} tensor, made up of the following sub-matrices:
 * <pre><code>
 *     d2 = 0: [1,2]
 *             [3,4]
 *
 *     d2 = 1: [5,6]
 *             [7,8]
 *
 *     d2 = 2: [9,0]
 *             [1,2]
 * </code></pre>
 * the tensor's iterator will iterate over these submatrices, in that order. If a tensor is one-dimensional, then the
 * iterator iterates over each tensor value, putting each value in a new {@code 1x1} (scalar) tensor.
 * <p>
 * Tensors also provide a simple metadata facility in the form of ({@code String}) key to ({@code Object}) value pairs.
 *
 * @param <T>
 *        The type this tensor holds.
 *
 * @author crf <br/>               
 *         Started: Jan 11, 2009 6:11:48 PM
 *         Revised: Jun 16, 2009 8:09:31 AM
 */
public interface Tensor <T> extends ContainsMetadata<String>,Iterable<Tensor<T>> {
    /**
     * Get the dimensionality (shape) of this tensor. Each element in the returned array refers to the corresponding
     * dimension in this tensor, and its value gives the size of the dimension.
     *
     * @return an array describing the dimensionality of this tensor.
     */
    int[] getDimensions();

    /**
     * Get the size (length) of a specified dimension in this tensor.
     *
     * @param dimension
     *        The (zero-based) index of the dimension.
     *
     * @return the size of the dimension.
     *
     * @throws IllegalArgumentException if {@code dimension} is less than zero or greater than one less then the number
     *                                  of dimensions in this tensor.
     */
    int size(int dimension);

    /**
     * Get the number of dimensions in this tensor.
     *
     * @return the number of dimensions in this tensor.
     */
    int size();

    /**
     * Get the type that this tensor holds. If this is anything other than {@code JavaType.OBJECT}, then this tensor
     * should hold the appropriate primitive and be paramaterized with the object equivalent of that primitive.
     *
     * @return the type that this tensor holds.
     */
    JavaType getType();

    /**
     * Get the tensor value at a specified location.
     *
     * @param indices
     *        The location of the value, with each index referring to a point in the corresponding tensor dimension.
     *
     * @return the value of the tensor at {@code indices}.
     *
     * @throws IllegalArgumentException if {@code indices.length} greater than or equal to the number of dimensions in this
     *                                  tensor.
     * @throws IndexOutOfBoundsException if any index in {@code indices} is less than zero or greater than or equal to
     *                                   the size of its corresponding dimension.
     */
    T getValue(int ... indices);

    /**
     * Set the tensor value at a specified location.
     *
     * @param value
     *        The value to set the location to.
     *
     * @param indices
     *        The location of the value, with each index referring to a point in the corresponding tensor dimension.
     *
     * @throws IllegalArgumentException if {@code indices.length} greater than or equal to the number of dimensions in this
     *                                  tensor.
     * @throws IndexOutOfBoundsException if any index in {@code indices} is less than zero or greater than or equal to
     *                                   the size of its corresponding dimension.
     */
    void setValue(T value, int ... indices);

    /**
     * Get the values of this tensor. The returned values array will contain a copy of the values, so subsequent changes
     * to the tensor will not affect the returned array, and vice-versa.
     *
     * @param type
     *        The base component type of the returned array. If {@code getType() != JavaType.OBJECT}, then this parameter
     *        can be ignored as the component type can be inferred from that method's returned value. 
     *
     * @return an array holding the values of this tensor.
    **/
    TypeSafeArray<T> getTensorValues(Class<T> type);

    /**
     * Set all of the values in this tensor to those specified in a specified array. This method will copy the values
     * from the array to this tensor, so changes to the input array will not be reflected in this tensor. The dimensionality
     * of the input array must exactly match that of this tensor.
     *
     * @param array
     *        The input array holding the values to which this tensor will be set.
     *
     * @throws IllegalArgumentException if the dimensionality of {@code array} does not equal that of this tensor.
     */
    void setTensorValues(TypeSafeArray<? extends T> array);

    /**
     * Set all of the values in this tensor to those specified in a specified tensor. This method will copy the values
     * from the source tensor to this tensor, so changes to the input tensor will not be reflected in this tensor. The
     * dimensionality of the input tensor must exactly match that of this tensor.
     *
     * @param tensor
     *        The input tensor holding the values to which this tensor will be set.
     *
     * @throws IllegalArgumentException if the dimensionality of {@code tensor} does not equal that of this tensor.
     */
    void setTensorValues(Tensor<? extends T> tensor);

    /**
     * Get a new tensor referencing this tensor. A referenced tensor is composed of elements from this tensor and has
     * the property that when an element in either tensor is changed, the other tensor reflects that change. Through
     * the use of an appropriate index, the new tensor can be a reshaped version of this tensor, or one which shuffles
     * or repeats this tensor's elements.
     *
     * @param index
     *        The index to be used for the new tensor. This index will provide the mapping from the new tensor to this
     *        one.
     *
     * @return a new tensor referencing this one through {@code index}.
     *
     * @throws IllegalArgumentException if {@code index} is not valid for this tensor ({@code !index.isValidFor(this)}).
     */
    <I> IdTensor<T,I> getReferenceTensor(Index<I> index);

    /**
     * Get this tensor's index.
     *
     * @return the index backing this tensor.
     */
    Index<?> getIndex();

    /**
     * Get an iterator iterating over each of this tensor's component sub-matrices. The component sub-matrices will
     * contain the same dimensional indices as this tensor, save the last dimension, which is fixed. More information
     * on the iterator is available in this class's main documentation.
     *
     * @return an iterator iterating over this tensor's sub-matrices.
     */
    Iterator<Tensor<T>> iterator();
}
