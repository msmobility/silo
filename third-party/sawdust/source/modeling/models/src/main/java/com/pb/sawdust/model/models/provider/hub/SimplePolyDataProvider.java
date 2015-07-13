package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.CompositeDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.index.MixedIndex;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.collections.UnmodifiableCollections;

import java.util.*;

/**
 * The {@code SimplePolyDataProvider} class is a basic implementation of {@code PolyDataProvider}.  In addition to the
 * data providers it holds as a {@code SimpleDataProviderHub}, it also can hold polydata for variables.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 12:09:43 PM
 */
public class SimplePolyDataProvider<K> extends SimpleDataProviderHub<K> implements PolyDataProvider<K> {
    private final SetList<K> keys;
    private final Map<String,IdDoubleMatrix<? super K>> polyData;

    /**
     * Constructor specifying the data id, the (ordered) data keys and the tensor factory used to build data results. The
     * order the keys are specified in this constructor determines the order the variable data will be placed in the polydata
     * matrices returned by the provider.
     *
     * @param dataId
     *        The data id to use for this provider hub.
     *
     * @param keys
     *        The ordered data provider keys to use for the returned instance.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public SimplePolyDataProvider(int dataId, SetList<K> keys, TensorFactory factory) {
        super(dataId,keys,factory);
        this.keys = UnmodifiableCollections.unmodifiableSetList(keys);
        polyData = new HashMap<String,IdDoubleMatrix<? super K>>();
    }

    /**
     * Constructor specifying the (ordered) data keys and the tensor factory used to build data results. The order the
     * keys are specified in this constructor determines the order the variable data will be placed in the polydata
     * matrices returned by the provider.
     *
     * @param keys
     *        The ordered data provider keys to use for the returned instance.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public SimplePolyDataProvider(SetList<K> keys, TensorFactory factory) {
        super(keys,factory);
        this.keys = UnmodifiableCollections.unmodifiableSetList(keys);
        polyData = new HashMap<String,IdDoubleMatrix<? super K>>();
    }

    /**
     * Add polydata for a specified variable. This method will check the column (dimension index 1) ids to determine if
     * every one of this provider's data keys (and only those) are included. If they are, then, if they are in the correct
     * order (matching the ordering implied by {@link #getDataKeys()}) then the data is associated with the specified variable
     *  directly, otherwise a reference matrix is associated with the columns appropriately reordered. If the input polydata's
     * ids do not match this provider's data keys, then it is assumed that the ordering of the columns matches that data
     * key ordering in this provider, and a reference matrix with id keys added is associated with the specified variable.
     *
     * @param variable
     *        The variable name.
     *
     * @param data
     *        The variable data.
     *
     * @throws IllegalArgumentException if the length of dimension 1 of {@code data} does not equal the number of data keys
     *                                  in this provider, or if the length of dimension 0 of {@code data} is not equal to
     *                                  this providrer's data length.
     */
    @SuppressWarnings("unchecked") //this is for the 2nd polyData.put call - reference tensor will be of correct type
    public void addPolyData(String variable, IdDoubleMatrix<? super K> data) {
        checkDataLength(data.size(0));
        //assumes has id with keys in 2nd dimension
        @SuppressWarnings("unchecked") //may be invalid, but internally it will be ok - just using it as a check against keys
        List<K> ids = (List<K>) data.getIndex().getIndexIds().get(1);
        if ((new HashSet<K>(ids)).equals(keys)) {
            if (ids.equals(keys))
                 polyData.put(variable,data);
            else //wrong order, so reorder
                polyData.put(variable,(IdDoubleMatrix<? super K>) data.getReferenceTensor(SliceIndex.getSliceIndex(data.getIndex(), SliceUtil.fullSlice(data.size(0)),SliceUtil.idSlice(ids,keys))));
        } else {
            //need to place keys on matrix
            if (data.size(1) != keys.size())
                throw new IllegalArgumentException(String.format("Data column count (%d) does not equal key count (%d) in poly data provider.",data.size(1),keys.size())) ;
            polyData.put(variable,getIdMatrix(data));
        }
    }

    /**
     * Add polydata for a specified variable. If the input data is an instance of an {@code IdDoubleMatrix} then
     * {@link #addPolyData(String, com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix)} will be called directly.
     * Otherwise, it is assumed that the columns of the data (dimension index 1) are ordered correctly to match this provider's
     * data key ordering.
     *
     * @param variable
     *        The variable name.
     *
     * @param data
     *        The variable data.
     *
     * @throws IllegalArgumentException if the length of dimension 1 of {@code data} does not equal the number of data keys
     *                                  in this provider, or if the length of dimension 0 of {@code data} is not equal to
     *                                  this providrer's data length.
     */
    public void addPolyData(String variable, DoubleMatrix data) {
        checkDataLength(data.size(0));
        if (data.size(1) != keys.size())
            throw new IllegalArgumentException(String.format("Data column count (%d) does not equal key count (%d) in poly data provider.",data.size(1),keys.size())) ;
        @SuppressWarnings("unchecked") //won't let me specify <? super K> as type, even though it probably is, so it is ok
        IdDoubleMatrix<? super K> t = data instanceof IdDoubleMatrix ? (IdDoubleMatrix) data : getIdMatrix(data);
        addPolyData(variable,t);
    }

    private IdDoubleMatrix<? super K> getIdMatrix(DoubleMatrix data) {
        return (IdDoubleMatrix<? super K>) data.getReferenceTensor(MixedIndex.replaceIds(data.getIndex(),1,keys));
    }

    @Override
    public SetList<K> getDataKeys() {
        return keys;
    }

    @Override
    public Set<String> getPolyDataVariables() {
        return polyData.keySet();
    }

    @Override
    public IdDoubleMatrix<? super K> getPolyData(String variable) {
        if (polyData.containsKey(variable))
            return polyData.get(variable);
        DataProvider p = getSharedProvider();
        if (p.hasVariable(variable)) {
            //construct poly variable
            DoubleMatrix m = factory.doubleMatrix(getDataLength(),1);
            int counter = 0;
            for (double d : p.getVariableData(variable))
                m.setCell(d,counter++,0);
            return (IdDoubleMatrix<? super K>) m.getReferenceTensor(MixedIndex.replaceIds(SliceIndex.getDefaultIdSliceIndex(m.getIndex(),SliceUtil.fullSlice(getDataLength()),SliceUtil.copy(0,getDataKeys().size())),1,keys));
        }
        throw new IllegalArgumentException("Variable not found: " + variable);
    }

    @Override
    public PolyDataProvider<K> getSubDataHub(int start, int end) {
        return new SubPolyDataProvider<K>(this,start,end);
    }

    public DataProvider getFullProvider(K key) {
        if (!keys.contains(key))
            throw new IllegalArgumentException("Poly data key not found: " + key);
        Map<String,double[]> data = new HashMap<String,double[]>();
        int index = keys.indexOf(key);
        int dataLength = getDataLength();
        for (String variable : polyData.keySet()) {
            double[] d = new double[dataLength];
            for (int i = 0; i < dataLength; i++)
                d[i] = polyData.get(variable).getCell(i,index);
            data.put(variable,d);
        }
        return new CompositeDataProvider(factory,getProvider(key),new SimpleDataProvider(data,factory));
    }
}
