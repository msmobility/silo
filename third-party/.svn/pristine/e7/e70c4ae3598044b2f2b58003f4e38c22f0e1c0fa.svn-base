package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.tabledata.DataRow;

import java.util.Map;
import java.util.Set;

/**
 * The {@code BalanceDimensionClassifier} interface provides information about a single balance dimension, including the
 * methods used to calculate the target and control totals for each of its categories.
 *
 * @param <T>
 *        The type of the categories for the balance dimension.
 *
 * @author crf
 *         Started 9/30/11 5:47 AM
 */
public interface BalanceDimensionClassifier<T> {  //T is the type the dimension classifies into

    /**
     * Get the name of the dimension.
     *
     * @return the balance dimension's name.
     */
    String getDimensionName();

    /**
     * Get the set of all categories of the balance dimension.
     *
     * @return the balance dimension's categories.
     */
    Set<T> getClassificationCategories();

    /**
     * Calculate the participation of a single balance element for each category in the balance dimension. The participation
     * of a balance element is how much it counts for each category (if it had a weight of one).
     *
     * @param element
     *        The balance element.
     *
     * @return a mapping from the balance dimension's categories to the participation rate of {@code element}.
     */
    Map<T,Double> getParticipationMap(BalanceElement element); //for synthetic side

    /**
     * Get the targets for the categories of the balance dimension, given some input target data.
     *
     * @param targetData
     *        The target data for the balance dimension (pulled from a data table).
     *
     * @return a mapping from the balance dimension's categories to the target values calculated from {@code targetData}.
     */
    Map<T,Double> getTargetMap(DataRow targetData); //for real side

    /**
     * Get the labels of the fields from a target {@code DataTable} that will be used by {@link #getTargetMap(com.pb.sawdust.tabledata.DataRow)}
     * to calculate the targets for the balance dimension.
     *
     * @return the fields from a target data table used to build the balance dimension targets.
     */
    Set<String> getTargetFields();

    /**
     * Get the geography that the target values apply to. This is needed to correctly calculate the target values, as well
     * as correctly run the balance procedure across multiple dimensions, each of which may operate at a different geography
     * level.
     *
     * @return the target geography for the balance dimension.
     */
    Geography<?,?> getTargetGeography();
}
