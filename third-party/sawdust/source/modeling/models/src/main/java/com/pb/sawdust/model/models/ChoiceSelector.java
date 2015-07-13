package com.pb.sawdust.model.models;

import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.probability.DiscreteProbabilisticDistribution;
import com.pb.sawdust.util.collections.SetList;

import java.util.List;
import java.util.Map;

/**
 * The {@code ChoiceSelector} ...
 *
 * @author crf
 *         Started 4/12/12 1:23 PM
 */
public interface ChoiceSelector<C extends Choice> {
    List<C> selectChoices(Map<C,DoubleVector> probabilities);
    List<C> selectChoices(SetList<C> choices, DoubleMatrix probabilities);
    List<C> selectChoices(SetList<C> choices, Iterable<DiscreteProbabilisticDistribution> distributions);
}
