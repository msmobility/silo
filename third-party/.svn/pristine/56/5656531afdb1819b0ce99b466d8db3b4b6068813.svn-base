package com.pb.sawdust.model.builder;

import com.pb.sawdust.model.builder.spec.ModelSpec;
import com.pb.sawdust.model.builder.spec.ModelTypeSpec;

/**
 * The {@code ModelType} ...
 *
 * @author crf <br/>
 *         Started 4/11/11 4:40 PM
 */
public enum ModelType {
    REGRESSION,
    LOGIT,
    NESTED_LOGIT;

    public static interface ModelTypeVersioner {
        ModelSpec getModelSpec(String version);
    }

    public static ModelType getModelType(ModelTypeSpec spec) {
        String canonicalName = spec.getTypeName().replace(" ","_").toUpperCase();
        try {
            return ModelType.valueOf(canonicalName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Model type '%s' not found",spec.getTypeName()));
        }
    }
}
