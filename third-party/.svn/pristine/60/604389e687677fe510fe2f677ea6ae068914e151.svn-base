package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.VariableCalculation;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;

import java.util.*;

/**
 * The {@code CachedCalculationPolyDataProvider} ...
 *
 * @author crf <br/>
 *         Started 2/16/11 10:07 AM
 */
public class CachedCalculationPolyDataProvider<K> extends SimpleCalculationPolyDataProvider<K> {
    private final Map<String,IdDoubleMatrix<? super K>> calculatedPolyDataCache;

    public CachedCalculationPolyDataProvider(int dataId, PolyDataProvider<K> baseProvider, TensorFactory factory) {
        super(dataId,baseProvider,factory);
        calculatedPolyDataCache = new HashMap<String, IdDoubleMatrix<? super K>>();
    }

    public CachedCalculationPolyDataProvider(PolyDataProvider<K> baseProvider, TensorFactory factory) {
        super(baseProvider,factory);
        calculatedPolyDataCache = new HashMap<String, IdDoubleMatrix<? super K>>();
    }

    @Override
    protected IdDoubleMatrix<? super K> getCalculatedPolyData(VariableCalculation calculation) {
        String variable = calculation.getName();
        if (!calculatedPolyDataCache.containsKey(variable))
            calculatedPolyDataCache.put(variable,super.getCalculatedPolyData(calculation));
        return calculatedPolyDataCache.get(variable);
    }

    @Override
    public CalculationPolyDataProvider<K> getSubDataHub(int start, int end) {
        return new CachedCalculationSubPolyDataProvider(start,end);
    }

    /**
     * Calculate all of the (calculated) variables in this provider. If a variable has already been calculated, then
     * it will not be updated (the cached version is preferred). Also, if any calculated variables are added <i>after</i>
     * this method has been called, they will not automatically be calculated.
     */
    public void calculateAllVariables() {
        for (String variable : getPolyDataVariables())
            if (getCalculation(variable).isCalculated())
                getPolyData(variable);
    }

    private class CachedCalculationSubPolyDataProvider extends WrappedCalculationSubPolyDataProvider {
        private final Map<String,IdDoubleMatrix<? super K>> calculatedSubPolyDataCache;

        private CachedCalculationSubPolyDataProvider(int start, int end) {
            super(start, end);
            calculatedSubPolyDataCache = new HashMap<String,IdDoubleMatrix<? super K>>();
        }

        @Override
        protected IdDoubleMatrix<? super K> getCalculatedPolyData(VariableCalculation calculation) {
            /*  two options:
                    1) call base provider to calculate poly data and then pass a sub variable
                    2) use sub matrices from base provider to calculate sub data

                    (2) is more consistent with what is done with CalculationProvider, and
                        also has the advantage of parsing the calculations out across the sub
                        providers (which is desired); the tough part is transmitting the results
                        back if the sub providers are remote, but remote providers are an issue period,
                        so I guess don't worry about it

                    Note that we cannot just make a global matrix cache for in the base provider, and then
                    a sub (indexed) matrix to the sub providers so that the results automatically get
                    filled into the base result. Why? Well, because there is no efficient way (without
                    a lot of cruft) to represent what part of what has already been filled in; down the
                    road if that level of cache sophistication is desired, we'll do it, but for the moment
                    this will do (this being the innefficient but simple method of recalculating for
                    each sub provider)
             */
            String variable = calculation.getName();
            if (!calculatedSubPolyDataCache.containsKey(variable)) { //is result already cached
                IdDoubleMatrix<? super K> result;
                if (!calculatedPolyDataCache.containsKey(variable)) { //will only be in base if fully filled in
                    result = super.getCalculatedPolyData(calculation);
                } else { //use the result from the base provider
                    IdDoubleMatrix<? super K> m = calculatedPolyDataCache.get(variable);
                    Index<?> index = SliceIndex.getSliceIndex(m.getIndex(), SliceUtil.range(start,end), SliceUtil.fullSlice(m.size(1)));
                    @SuppressWarnings("unchecked") //index pulled from m, so should maintain correct type
                    IdDoubleMatrix<? super K> rt = (IdDoubleMatrix<? super K>) m.getReferenceTensor(index);
                    result = rt;
                }
                calculatedSubPolyDataCache.put(variable,result);
            }
            return calculatedSubPolyDataCache.get(variable);
        }
    }
}
