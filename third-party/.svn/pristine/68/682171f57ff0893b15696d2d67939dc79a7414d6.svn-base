package com.pb.sawdust.model.models;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.BaseSlice;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.array.ArrayUtil;

import static com.pb.sawdust.util.Range.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code AbstractDiscreteChoiceModel} ...
 *
 * @author crf <br/>
 *         Started 3/2/11 7:13 AM
 */
public abstract class AbstractDiscreteChoiceModel<C extends Choice> implements DiscreteChoiceModel<C> {

    @Override
    public Map<C,DoubleVector> getProbabilities(DataProvider data) {
        Map<C,DoubleVector> probabilities = new HashMap<C,DoubleVector>();
        for (C choice : getChoices())
            probabilities.put(choice,getProbabilities(choice,data));
        return probabilities;
    }

    @Override
    public DoubleVector getProbabilities(C choice, DataProviderHub<C> data) {
        return getProbabilities(choice,data.getProvider(choice));
    }

    @Override
    public Map<C,DoubleVector> getProbabilities(DataProviderHub<C> data) {
        Map<C,DoubleVector> probabilities = new HashMap<C,DoubleVector>();
        for (C choice : getChoices())
            probabilities.put(choice,getProbabilities(choice,data));
        return probabilities;
    }
    
    @Override
    public Map<C,BooleanVector> getAvailabilities(DataProvider data) {
        Map<C,BooleanVector> availabilities = new HashMap<C,BooleanVector>();
        for (C choice : getChoices())
            availabilities.put(choice,getAvailabilities(choice,data));
        return availabilities;
    }

    @Override
    public BooleanVector getAvailabilities(C choice, DataProviderHub<C> data) {
        return getAvailabilities(choice,data.getProvider(choice));
    }

    @Override
    public Map<C,BooleanVector> getAvailabilities(DataProviderHub<C> data) {
        Map<C,BooleanVector> availabilities = new HashMap<C,BooleanVector>();
        for (C choice : getChoices())
            availabilities.put(choice,getAvailabilities(choice,data));
        return availabilities;
    }

    private Slice getAvailabilitySlice(BooleanVector availabilities, boolean anti) {
        List<Integer> sliceIndices = new LinkedList<Integer>();
        if (anti) {
            for (int i : range(availabilities.size(0)))
                if (!availabilities.getCell(i))
                    sliceIndices.add(i);
        } else {
            for (int i : range(availabilities.size(0)))
                if (availabilities.getCell(i))
                    sliceIndices.add(i);
        }
        int[] si = new int[sliceIndices.size()];
        int counter = 0;
        for (Integer sliceIndex : sliceIndices)
            si[counter++] = sliceIndex;
        return new BaseSlice(si);
    }

    private <I> Index<I> getAvailableIndex(Index<I> sourceIndex, BooleanVector availabilities, boolean anti) {
        Slice[] slices = new Slice[sourceIndex.size()];
        if (slices.length > 1)
            for (int i : range(1,slices.length))
                slices[i] = SliceUtil.fullSlice(sourceIndex.size(i));
        slices[0] = getAvailabilitySlice(availabilities,anti);
        return SliceIndex.getSliceIndex(sourceIndex,slices);
    }

    protected <I> Index<I> getAvailableIndex(Index<I> sourceIndex, BooleanVector availabilities) {
        return getAvailableIndex(sourceIndex,availabilities,false);
    }

    protected <I> Index<I> getUnavailableIndex(Index<I> sourceIndex, BooleanVector availabilities) {
        return getAvailableIndex(sourceIndex,availabilities,true);
    }

    @Override
    public Map<C,CalculationTrace> traceCalculation(DataProvider data, int observation) {
        Map<C,CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
        for (C choice : getChoices())
            utilityTrace.put(choice,traceCalculation(choice,data,observation));
        return utilityTrace;
    }

    @Override
    public Map<C,CalculationTrace> traceCalculation(DataProviderHub<C> data, int observation) {
        Map<C,CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
        for (C choice : getChoices())
            utilityTrace.put(choice,traceCalculation(choice,data,observation));
        return utilityTrace;
    }

    @Override
    public CalculationTrace traceCalculation(C choice, DataProviderHub<C> data, int observation) {
        return traceCalculation(choice,data.getProvider(choice),observation);
    }
}
