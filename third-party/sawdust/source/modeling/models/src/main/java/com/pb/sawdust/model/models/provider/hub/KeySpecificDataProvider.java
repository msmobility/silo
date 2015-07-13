package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.AbstractIdData;
import com.pb.sawdust.model.models.provider.ConstantDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.EmptyDataProvider;
import com.pb.sawdust.model.models.provider.hub.PolyDataProvider;
import com.pb.sawdust.model.models.provider.hub.SubPolyDataProvider;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.id.IdDoubleVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.MirrorIndex;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.collections.SetList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code KeySpecificDataProvider} ...
 *
 * @author crf
 *         Started 4/10/12 7:53 PM
 */
public abstract class KeySpecificDataProvider<K> extends AbstractIdData implements PolyDataProvider<K> {
    private final DataProvider generalProvider;
    private final SetList<K> keys;
    private final int dataLength;
    private final TensorFactory factory;

    KeySpecificDataProvider(SetList<K> keys, int dataLength, TensorFactory factory) {
        this.keys = keys;
        this.dataLength = dataLength;
        this.factory = factory;
        generalProvider = new EmptyDataProvider(dataLength,factory);
    }

    abstract protected double getVariableData(K key, String variable);

    protected IdDoubleVector<K> getPolyDataVector(String variable) {
        IdDoubleVector<K> data = factory.doubleVector(keys,dataLength);
        for (int i : range(dataLength))
            data.setCell(getVariableData(keys.get(i),variable),i);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked") //I think this cast to a K extension is correct, through the mirror index is Integer keys, so note that for future reference
    public IdDoubleMatrix<? super K> getPolyData(String variable) {
        if (!getPolyDataVariables().contains(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        Map<Integer,Integer> dimensionMap = new HashMap<>();
        dimensionMap.put(0,dataLength);
        DoubleVector v = getPolyDataVector(variable);
        return (IdDoubleMatrix<? super K>) v.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(v.getIndex(),dimensionMap));
    }

    @Override
    public DataProvider getProvider(K key) {
        return generalProvider;
    }

    @Override
    public PolyDataProvider<K> getSubDataHub(int start, int end) {
        return new SubPolyDataProvider<>(this,start,end);
    }

    @Override
    public int getAbsoluteStartIndex() {
        return 0;
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    @Override
    public DataProvider getFullProvider(K key) {
        if (!keys.contains(key))
            throw new IllegalArgumentException("Poly data key not found: " + key);
        Map<String,Double> variableData = new HashMap<>();
        for (String variable : getPolyDataVariables())
            variableData.put(variable,getVariableData(key,variable));
        return  new ConstantDataProvider(variableData,dataLength,factory);
    }

    @Override
    public SetList<K> getDataKeys() {
        return keys;
    }

    @Override
    public DataProvider getSharedProvider() {
        return generalProvider;
    }


}
