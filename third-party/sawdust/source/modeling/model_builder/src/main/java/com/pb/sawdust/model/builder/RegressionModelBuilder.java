package com.pb.sawdust.model.builder;

import com.pb.sawdust.model.builder.spec.CoefficientSpec;
import com.pb.sawdust.model.builder.spec.LinearUtilitySpec;
import com.pb.sawdust.model.models.RegressionModel;

/**
 * The {@code RegressionModelBuilder} ...
 *
 * @author crf <br/>
 *         Started 4/12/11 11:25 AM
 */
public class RegressionModelBuilder {
    public RegressionModel buildModel(LinearUtilitySpec spec) {
        return new RegressionModel(new SimpleLinearUtilityBuilder().buildUtility(spec, CoefficientSpec.DEFAULT_COEFFICIENT_NAME));
    }
}
