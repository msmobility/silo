package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.slice.Slice;

/**
 * The {@code DataFilter} ...
 *
 * @author crf <br/>
 *         Started 3/2/11 1:50 PM
 */
public interface DataFilter {
    BooleanVector getFilter(DataProvider provider);
    Slice getFilteredSlice(DataProvider provider);
    Slice getUnfilteredSlice(DataProvider provider);
    CalculationTrace traceFilterCalculation(DataProvider data, int observation);
}
