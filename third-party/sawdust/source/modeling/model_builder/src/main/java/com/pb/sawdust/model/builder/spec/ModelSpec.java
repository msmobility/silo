package com.pb.sawdust.model.builder.spec;

import java.util.Map;

/**
 * The {@code UtilityModelSpec} ...
 *
 * @author crf <br/>
 *         Started 4/10/11 2:34 PM
 */
public interface ModelSpec {
    String getName();
    Object buildModel();
}
