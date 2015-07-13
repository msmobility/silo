package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.util.probability.Weight;
import com.pb.sawdust.tabledata.DataRow;

import java.util.Map;

/**
 * The {@code BalanceElement} class holds the information about a single basis element in an entropy maximizing balancing
 * procedure. It contains a weight, an identifier, and a collection of data pertaining to the element. Each element's id
 * should be unique to the basis element it represents.Though it is immutable, the element's weight is mutable, which is
 * necessary for the balancing procedures. The balance element retains a memory of its initial weight, and this allows a
 * "fresh" copy of the element to be created, which essentially is a copy of the
 * element with its weight "reset" to its initial value.
 *
 * @author crf
 *         Started 9/30/11 5:41 AM
 */
public class BalanceElement {
    private final int id;
    private final double initialWeight;
    private final Weight weight;
    private final Map<String,DataRow> elementData;


    /**
     * Constructor specifying the element's id, element data, and initial weight.
     *
     * @param id
     *        The unique identifier for the element.
     *
     * @param elementData
     *        The element's data. The structure is a mapping from string keys to data rows specific to the element.
     *
     * @param weight
     *        The initial weight for the element.
     */
    public BalanceElement(int id, Map<String,DataRow> elementData, double weight) {
        this.id = id;
        this.elementData = elementData;
        this.weight = new Weight(weight);
        initialWeight = weight;
    }

    /**
     * Get a "fresh" copy of this balance element. The fresh copy is a new copy of this element with its weight set to
     * this element's initial value. This method is useful for reporting and analysis, as well as rolling back changes
     * so as to retry balancing procedures.
     *
     * @return a copy of this element with its weight set to its initial value.
     */
    public BalanceElement freshCopy() {
        return new BalanceElement(id,elementData,initialWeight);
    }

    /**
     * Get this element's data.
     *
     * @return the string key to data row mapping specific to this element.
     */
    public Map<String,DataRow> getElementData() {
        return elementData;
    }

    /**
     * Get this element's unique identifier.
     *
     * @return this element's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Get this element's weight.
     *
     * @return this element's weight.
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Get this element's initial weight value.
     *
     * @return the value this element's weight was initially set at.
     */
    protected double getInitialWeight() {
        return initialWeight;
    }

    /**
     * "Reset" this element so that it's weight is set to its initial value.
     */
    public void reset() {
        weight.setWeight(initialWeight);
    }
}
