package com.pb.sawdust.tensor.factory;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.id.primitive.*;
import com.pb.sawdust.tensor.decorators.id.*;
import com.pb.sawdust.tensor.read.TensorReader;

import java.util.List;

/**
 * The {@code ConcurrentTensorFactory} interface specifies a structure for factories which will generate concrete {@code Tensor} 
 * implementation instances which have some level of thread (concurrent access) safety. Methods defined in this interface are 
 * similar to those in {@code TensorFactory}, except they require the additional specification of a concurrency level.  The concurrency
 * level is used to get locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class,
 * which are used to lock the tensor for read/write access.  The specifics about the concurrency policies (including the meaning of
 * "concurrency level") can be found in the documentation for that class.
 * 
 * @author crf <br/>
 *         Started: July 25, 2009 4:27:32 PM
 *         Revised: Dec 14, 2009 12:35:35 PM
 */
public interface ConcurrentTensorFactory {
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code byte} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code ByteDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code ByteScalar}, {@code ByteVector}, or {@code ByteMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    ByteTensor concurrentByteTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code ByteDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code ByteScalar}, {@code ByteVector}, or {@code ByteMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    ByteTensor initializedConcurrentByteTensor(byte defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code byte} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdByteDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdByteScalar}, {@code IdByteVector}, or {@code IdByteMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdByteTensor<I> concurrentByteTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdByteDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdByteScalar}, {@code IdByteVector}, or {@code IdByteMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code byte} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdByteDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdByteScalar}, {@code IdByteVector}, or {@code IdByteMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdByteTensor<I> concurrentByteTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code byte}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdByteDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdByteScalar}, {@code IdByteVector}, or {@code IdByteMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code short} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code ShortDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code ShortScalar}, {@code ShortVector}, or {@code ShortMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    ShortTensor concurrentShortTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code ShortDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code ShortScalar}, {@code ShortVector}, or {@code ShortMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    ShortTensor initializedConcurrentShortTensor(short defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code short} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdShortDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdShortScalar}, {@code IdShortVector}, or {@code IdShortMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdShortTensor<I> concurrentShortTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdShortDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdShortScalar}, {@code IdShortVector}, or {@code IdShortMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code short} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdShortDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdShortScalar}, {@code IdShortVector}, or {@code IdShortMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdShortTensor<I> concurrentShortTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code short}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdShortDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdShortScalar}, {@code IdShortVector}, or {@code IdShortMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code int} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IntDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IntScalar}, {@code IntVector}, or {@code IntMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    IntTensor concurrentIntTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code IntDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IntScalar}, {@code IntVector}, or {@code IntMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    IntTensor initializedConcurrentIntTensor(int defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code int} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdIntDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdIntScalar}, {@code IdIntVector}, or {@code IdIntMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdIntTensor<I> concurrentIntTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdIntDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdIntScalar}, {@code IdIntVector}, or {@code IdIntMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code int} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdIntDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdIntScalar}, {@code IdIntVector}, or {@code IdIntMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdIntTensor<I> concurrentIntTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code int}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdIntDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdIntScalar}, {@code IdIntVector}, or {@code IdIntMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code long} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code LongDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code LongScalar}, {@code LongVector}, or {@code LongMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    LongTensor concurrentLongTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code LongDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code LongScalar}, {@code LongVector}, or {@code LongMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    LongTensor initializedConcurrentLongTensor(long defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code long} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdLongDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdLongScalar}, {@code IdLongVector}, or {@code IdLongMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdLongTensor<I> concurrentLongTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdLongDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdLongScalar}, {@code IdLongVector}, or {@code IdLongMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code long} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdLongDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdLongScalar}, {@code IdLongVector}, or {@code IdLongMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdLongTensor<I> concurrentLongTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code long}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdLongDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdLongScalar}, {@code IdLongVector}, or {@code IdLongMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code float} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code FloatDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code FloatScalar}, {@code FloatVector}, or {@code FloatMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    FloatTensor concurrentFloatTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code FloatDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code FloatScalar}, {@code FloatVector}, or {@code FloatMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    FloatTensor initializedConcurrentFloatTensor(float defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code float} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdFloatDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdFloatScalar}, {@code IdFloatVector}, or {@code IdFloatMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdFloatTensor<I> concurrentFloatTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdFloatDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdFloatScalar}, {@code IdFloatVector}, or {@code IdFloatMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code float} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdFloatDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdFloatScalar}, {@code IdFloatVector}, or {@code IdFloatMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdFloatTensor<I> concurrentFloatTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code float}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdFloatDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdFloatScalar}, {@code IdFloatVector}, or {@code IdFloatMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code double} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code DoubleDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code DoubleScalar}, {@code DoubleVector}, or {@code DoubleMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    DoubleTensor concurrentDoubleTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code DoubleDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code DoubleScalar}, {@code DoubleVector}, or {@code DoubleMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    DoubleTensor initializedConcurrentDoubleTensor(double defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code double} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdDoubleDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdDoubleScalar}, {@code IdDoubleVector}, or {@code IdDoubleMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdDoubleTensor<I> concurrentDoubleTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdDoubleDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdDoubleScalar}, {@code IdDoubleVector}, or {@code IdDoubleMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code double} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdDoubleDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdDoubleScalar}, {@code IdDoubleVector}, or {@code IdDoubleMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdDoubleTensor<I> concurrentDoubleTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code double}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdDoubleDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdDoubleScalar}, {@code IdDoubleVector}, or {@code IdDoubleMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code char} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code CharDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code CharScalar}, {@code CharVector}, or {@code CharMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    CharTensor concurrentCharTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code CharDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code CharScalar}, {@code CharVector}, or {@code CharMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    CharTensor initializedConcurrentCharTensor(char defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code char} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdCharDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdCharScalar}, {@code IdCharVector}, or {@code IdCharMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdCharTensor<I> concurrentCharTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdCharDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdCharScalar}, {@code IdCharVector}, or {@code IdCharMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code char} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdCharDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdCharScalar}, {@code IdCharVector}, or {@code IdCharMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdCharTensor<I> concurrentCharTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code char}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdCharDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdCharScalar}, {@code IdCharVector}, or {@code IdCharMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s and will be safe for concurrent access. 
     * The tensor will be initialized with default {@code boolean} values as specified by the Java Language Specification. Though
     * the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code BooleanDnTensor}, where {@code n} is the number of tensor dimensions.  If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code BooleanScalar}, {@code BooleanVector}, or {@code BooleanMatrix}, respectively. That is, this tensor may be cast to the
     * appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    BooleanTensor concurrentBooleanTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s,
     * will be filled with a user-specified default value, and will be safe for concurrent access. The returned tensor uses locks from the 
     * {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed. Though the tensor returned is not explicitly 
     * sized, if the dimension count  is between 0 and 9 then the returned tensor will actually be a {@code BooleanDnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code BooleanScalar}, {@code BooleanVector}, or {@code BooleanMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    BooleanTensor initializedConcurrentBooleanTensor(boolean defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s,
     * will have its indices accessible by ids and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code boolean} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdBooleanDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdBooleanScalar}, {@code IdBooleanVector}, or {@code IdBooleanMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdBooleanTensor<I> concurrentBooleanTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdBooleanDnTensor<I>}, where {@code n} is the number of tensor dimensions. If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdBooleanScalar}, {@code IdBooleanVector}, or {@code IdBooleanMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s, 
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code boolean} values as specified by the Java Language Specification. Though the tensor returned is not 
     * explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually be a {@code IdBooleanDnTensor<I>}, 
     * where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdBooleanScalar}, {@code IdBooleanVector}, or {@code IdBooleanMatrix}, respectively.
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's
     * methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdBooleanTensor<I> concurrentBooleanTensor(I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold {@code boolean}s,
     * will be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually
     * be a {@code IdBooleanDnTensor<I>}, where {@code n} is the number of tensor dimensions.  If the dimension count is 
     * 0, 1, or 2, then the returned tensor will be a {@code IdBooleanScalar}, {@code IdBooleanVector}, or {@code IdBooleanMatrix}, respectively. That is, this tensor may be
     * cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);


    /**
     * Copy a tensor into a new tensor which is safe for concurrent access. This method will make a copy, not a 
     * reference tensor, so changes to the source tensor will not be reflected in the new tensor (and vice-versa). 
     * Though the tensor returned is not explicitly typed nor sized, if the source tensor holds primitive types and/or 
     * is up to 9 dimensions in size, the returned tensor will implement the appropriate interface from the 
     * {@code com.pb.sawdust.tensor.decorators} packages. (<i>e.g.</i> if the source tensor is a {@code BooleanD2Tensor}, 
     * the returned tensor could be validly cast to that type).
     * <p>
     * The returned tensor uses locks from the {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type the tensors (source and returned) hold.
     *
     * @param tensor
     *        The source tensor to copy.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @return a copy of {@code tensor}.
     */
    <T> Tensor<T> copyTensor(Tensor<T> tensor, int concurrencyLevel);

    /**
     * Copy an id tensor into a new tensor which is safe for concurrent access. This method will make a copy, not a reference tensor, so changes to the
     * source tensor will not be reflected in the new tensor (and vice-versa). Though the tensor returned is not
     * explicitly typed nor sized, if the source tensor holds primitive types and/or is up to 9 dimensions in size,
     * the returned tensor will implement the appropriate interface from the {@code com.pb.sawdust.tensor.decorators}
     * packages. (<i>e.g.</i> if the source tensor is a {@code IdBooleanD2Tensor}, the returned tensor could be validly
     * cast to that type).
     * <p>
     * The returned tensor uses locks from the {@code ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type the tensors (source and returned) hold.
     *
     * @param <I>
     *        The type of the tensor ids.
     * 
     * @param tensor
     *        The source tensor to copy.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     * 
     * @return a copy of {@code tensor}.
     */
    <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor, int concurrencyLevel);

    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold the specified type and will be safe for 
     * concurrent access. The tensor will be initialized with default {@code null} values. Though the tensor returned is not explicitly sized, if the 
     * dimension count is between 0 and 9 then the returned tensor will actually be a {@code DnTensor}, where {@code n} 
     * is the number of tensor dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code Scalar}, {@code Vector}, or {@code Matrix}, respectively. That is, this tensor may be cast to the appropriate sized tensor if that interface's methods 
     * are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <T> Tensor<T> concurrentTensor(int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will be filled with a user-specified default 
     * value and will be safe for concurrent access. Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the 
     * returned tensor will actually be a {@code DnTensor}, where {@code n} is the number of tensor  dimensions. If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code Scalar}, {@code Vector}, or {@code Matrix}, respectively. That is, this tensor 
     * may be cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions filled with {@code defaultValue}.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     * @throws NullPointerException if {@code defaultValue == null}.
     */
    <T> Tensor<T> initializedConcurrentTensor(T defaultValue, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold the specified type,
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized 
     * with default {@code null} values. Though the tensor returned is not explicitly sized, if the dimension count is between 
     * 0 and 9 then the returned tensor will actually be a {@code IdDnTensor}, where {@code n} is the number of tensor dimensions. 
     * If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IDScalar}, {@code IdVector}, or {@code IdMatrix}, respectively. 
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <T,I> IdTensor<T,I> concurrentTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold the specified type, will 
     * be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for 
     * concurrent access. Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will 
     * actually be a {@code IdDnTensor}, where {@code n} is the number of tensor dimensions. 
     * If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IDScalar}, {@code IdVector}, or {@code IdMatrix}, respectively. 
     * That is, this tensor may be cast  to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one list per dimension, and this list's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.size() != dimensions.length},
     *                                  if any of the id lists' sizes do not match their respective dimension's size,
     *                                  if any of the id lists contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     * @throws NullPointerException if {@code defaultValue == null}.
     */
    <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold the specified type,
     * will have its indices accessible by ids, and will be safe for concurrent access. The tensor will be initialized with default 
     * {@code null} values. Though the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then 
     * the returned tensor will actually be a {@code IdDnTensor}, where {@code n} is the number of tensor dimensions.
     * If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IDScalar}, {@code IdVector}, or {@code IdMatrix}, respectively. 
     * That is, this tensor may be cast to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     */
    <T,I> IdTensor<T,I> concurrentTensor(I[][] ids, int concurrencyLevel, int ... dimensions);
    
    /**
     * Factory method used to get a {@code Tensor} of the specified dimensions which will hold the specified type, will 
     * be filled with a user-specified default value, will have its indices accessible by ids, and will be safe for concurrent access. 
     * Though  the tensor returned is not explicitly sized, if the dimension count is between 0 and 9 then the returned tensor will actually 
     * be a {@code IdDnTensor}, where {@code n} is the number of tensor dimensions. 
     * If the dimension count is 0, 1, or 2, then the returned tensor
     * will be a {@code IDScalar}, {@code IdVector}, or {@code IdMatrix}, respectively. 
     * That is, this tensor may be cast  to the appropriate sized tensor if that interface's methods are needed.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} class, in which the concurrency policy is discussed.
     *
     * @param <T>
     *        The type this tensor will hold.
     *
     * @param <I>
     *        The type of the tensor ids.
     *
     * @param defaultValue
     *        The default value that the tensor will be filled with.
     *
     * @param ids
     *        The ids to be used for index referencing. There should be one array per dimension, and this array's length
     *        should match that dimension's size.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @param dimensions
     *        The array specifying the dimensionality of the tensor. The length of this array defines the number of dimensions in
     *        the tensor, and the size of each element specifies the size of each tensor dimension.
     *
     * @return a tensor with the specified dimensions.
     *
     * @throws IllegalArgumentException if any element in {@code dimensions} is less than 1, if {@code ids.length != dimensions.length},
     *                                  if any of the id arrays' lengths do not match their respective dimension's size,
     *                                  if any of the id arrays contains repeated elements,
     *                                  or if {@code concurrencyLevel > dimensions.length}.
     * @throws NullPointerException if {@code defaultValue == null}.
     */
   <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions);

    /**
     * Create a tensor using a reader which will be safe for concurrent access. If ids are specified by the reader, then the 
     * returned tensor is guaranteed to be a (subclass of a) {@code IdTensor<T,I>}. The returned tensor will also implement the 
     * expected size and type interfaces to which it naturally belongs.
     * <p>
     * The returned tensor uses locks from the {@link com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory} 
     * class, in which the concurrency policy is discussed.
     *
     * @param <T> the type held by the tensor.
     * 
     * @param <I> the type of the ids of the tensor (if used).
     *
     * @param reader the reader used to read in the tensor specifications and data.
     *
     * @param concurrencyLevel
     *        The concurrency level of the returned tensor.
     *
     * @return a tensor created from {@code reader} with the specified concurrency level.
     *
     * @throws IllegalArgumentException if {@code concurrencyLevel} is less than 0 or greater than the number of dimensions of the returned tensor.
     **/
    <T,I> Tensor<T> concurrentTensor(TensorReader<T,I> reader, int concurrencyLevel);
}
