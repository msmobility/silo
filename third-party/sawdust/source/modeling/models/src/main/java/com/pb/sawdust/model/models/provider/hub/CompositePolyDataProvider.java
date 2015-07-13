package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.AbstractIdData;
import com.pb.sawdust.model.models.provider.CompositeDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.VariableCalculation;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.collections.SetList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The {@code CompositePolyDataProvider} ...
 *
 * @author crf
 *         Started 4/11/12 7:17 AM
 */
public class CompositePolyDataProvider<K> extends AbstractIdData implements CalculationPolyDataProvider<K> {
    private final List<PolyDataProvider<K>> polyDataProviders;
    private final Set<CalculationPolyDataProvider<K>> calculationPolyDataProviders;
    private final TensorFactory factory;
    private final SetList<K> dataKeys;

    public CompositePolyDataProvider(SetList<K> dataKeys, List<PolyDataProvider<K>> polyDataProviders, TensorFactory factory) {
        this.polyDataProviders = new LinkedList<>(polyDataProviders);
        calculationPolyDataProviders = new HashSet<>();
        this.factory = factory;
        this.dataKeys = dataKeys;
    }

    public CompositePolyDataProvider(SetList<K> dataKeys, TensorFactory factory) {
        this(dataKeys,new LinkedList<PolyDataProvider<K>>(),factory);
    }

    private int checkState() {
        int length = DataProvider.UNINITIALIZED_DATA_LENGTH;
        for (PolyDataProvider<K> provider : polyDataProviders)
            if (length == DataProvider.UNINITIALIZED_DATA_LENGTH)
                length = provider.getDataLength();
            else if (provider.getDataLength() == DataProvider.UNINITIALIZED_DATA_LENGTH)
                continue; //uninitialized - ignore
            else if (length != provider.getDataLength())
                throw new IllegalStateException(String.format("All providers in composite poly data provider must have same length (%d vs. %d)",length,provider.getDataLength()));
        return length;
    }

    public void addProvider(PolyDataProvider<K> provider) {
        if (provider == this)
            throw new IllegalArgumentException("Cannot add self to composite provider.");
        int dataLength = getDataLength();
        if (dataLength != DataProvider.UNINITIALIZED_DATA_LENGTH && provider.getDataLength() != dataLength)
            throw new IllegalArgumentException("Data length (" + provider.getDataLength() + ") for provider does not match this provider's length (" + dataLength + ")");
        polyDataProviders.add(provider);
        if (provider instanceof CalculationPolyDataProvider)
            calculationPolyDataProviders.add((CalculationPolyDataProvider<K>) provider);
    }

    private PolyDataProvider<K> checkForVariable(String variable) {
        for (PolyDataProvider<K> provider : polyDataProviders)
            if (provider.getPolyDataVariables().contains(variable))
                return provider;
        throw new IllegalArgumentException("Variable not found: " + variable);
    }

    @Override
    public Set<String> getPolyDataVariables() {
        Set<String> variables = new HashSet<String>();
        for (PolyDataProvider<K> provider : polyDataProviders)
            variables.addAll(provider.getPolyDataVariables());
        return variables;
    }

    @Override
    public IdDoubleMatrix<? super K> getPolyData(String variable) {
        return checkForVariable(variable).getPolyData(variable);
    }

    @Override
    public DataProvider getProvider(K key) {
        CompositeDataProvider provider = new CompositeDataProvider(factory);
        for (PolyDataProvider<K> p : polyDataProviders)
            provider.addProvider(p.getProvider(key));
        return provider;
    }

    @Override
    public CalculationPolyDataProvider<K> getSubDataHub(int start, int end) {
        List<PolyDataProvider<K>> subProviders = new LinkedList<>();
        for (PolyDataProvider<K> provider : polyDataProviders)
            subProviders.add(provider.getSubDataHub(start,end));
        return new CompositePolyDataProvider<>(dataKeys,subProviders,factory);
//        return new SubPolyDataProvider<K>(this,start,end);

    }

    @Override
    public int getAbsoluteStartIndex() {
        return 0;
    }

    @Override
    public int getDataLength() {
        return checkState();
    }

    @Override
    public DataProvider getFullProvider(K key) {
        CompositeDataProvider provider = new CompositeDataProvider(factory);
        for (PolyDataProvider<K> p : polyDataProviders)
            provider.addProvider(p.getFullProvider(key));
        return provider;
    }

    @Override
    public SetList<K> getDataKeys() {
        return dataKeys;
    }

    @Override
    public DataProvider getSharedProvider() {
        CompositeDataProvider provider = new CompositeDataProvider(factory);
        for (PolyDataProvider<K> p : polyDataProviders) {
            DataProvider sharedProvider = p.getSharedProvider();
            if (sharedProvider.getVariables().size() > 0) //skip if empty
                provider.addProvider(p.getSharedProvider());
        }
        return provider;
    }

    @Override
    public VariableCalculation getCalculation(String variable) {
        PolyDataProvider<K> provider = checkForVariable(variable);
        return (provider instanceof CalculationPolyDataProvider) ? ((CalculationPolyDataProvider<K>) provider).getCalculation(variable) : new VariableCalculation(variable);
    }

    @Override
    public VariableCalculation getResolvedCalculation(String variable) {
        PolyDataProvider<K> provider = checkForVariable(variable);
        return (provider instanceof CalculationPolyDataProvider) ? ((CalculationPolyDataProvider<K>) provider).getResolvedCalculation(variable) : new VariableCalculation(variable);
    }

    @Override
    public boolean containsCalculatedVariables() {
        for (CalculationPolyDataProvider<K> cp : calculationPolyDataProviders)
            if (cp.containsCalculatedVariables())
                return true;
        return false;
    }
}
