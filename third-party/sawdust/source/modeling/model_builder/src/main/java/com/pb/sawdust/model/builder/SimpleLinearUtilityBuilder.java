package com.pb.sawdust.model.builder;

import com.pb.sawdust.model.builder.spec.CoefficientSpec;
import com.pb.sawdust.model.builder.spec.LinearUtilitySpec;
import com.pb.sawdust.model.builder.spec.VariableSpec;
import com.pb.sawdust.model.models.utility.LinearUtility;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code SimpleLinearUtilityBuilder} ...
 *
 * @author crf <br/>
 *         Started 4/12/11 11:07 AM
 */
public class SimpleLinearUtilityBuilder implements LinearUtilityBuilder {
    public static final TensorFactory factory = ArrayTensor.getFactory();

    @Override
    public LinearUtility buildUtility(LinearUtilitySpec utilitySpec, String coefficientBranch) {
        SetList<String> variables = new LinkedSetList<String>();
        List<Double> coefficients = new LinkedList<Double>();

        Map<VariableSpec,CoefficientSpec> utilitySpecMap = utilitySpec.getUtilitySpecMap();
        for (VariableSpec v : utilitySpecMap.keySet()) {
            variables.add(v.getName());
            Map<String,Double> coeffSpec = utilitySpecMap.get(v).getCoefficient();
            coefficients.add(coeffSpec.containsKey(coefficientBranch) ? coeffSpec.get(coefficientBranch) : 0.0);
        }
        return new SimpleLinearUtility(utilitySpec.getName(),variables,coefficients,factory);
    }
}
