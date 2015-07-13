package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.CalculationProvider;

/**
 * The {@code CalculationPolyDataProvider} ...
 *
 * @author crf <br/>
 *         Started 2/15/11 10:53 PM
 */
public interface CalculationPolyDataProvider<K> extends PolyDataProvider<K>,CalculationProvider {
    CalculationPolyDataProvider<K> getSubDataHub(int start, int end);
}
