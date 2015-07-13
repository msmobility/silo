package com.pb.sawdust.tensor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@code StandardTensorMetadataKey} ...
 *
 * @author crf <br/>
 *         Started Aug 15, 2010 2:16:42 PM
 */
public enum StandardTensorMetadataKey {
    NAME("name"),
    DATE_CREATED("creation date"),
    TENSOR_IMPLEMENTATION("tensor impl"),
    SOURCE("source"),
    DIMENSION_NAME("dimension %s name","dimension number")
    ;
    private final String key;
    private final List<String> tokenDescriptions;

    private StandardTensorMetadataKey(String key) {
        this(key,new String[0]);
    }

    private StandardTensorMetadataKey(String key, String ... tokenDescriptions) {
        this.key = key;
        this.tokenDescriptions = Collections.unmodifiableList(Arrays.asList(tokenDescriptions));
    }

    public String getKey() {
        return key;
    }

    public boolean isTokenized() {
        return tokenDescriptions.size() > 0;
    }

    public List<String> getTokenDescriptions() {
        return tokenDescriptions;
    }

    public String getDetokenizedKey(Object ... values) {
        return String.format(key,values);
    }
}
