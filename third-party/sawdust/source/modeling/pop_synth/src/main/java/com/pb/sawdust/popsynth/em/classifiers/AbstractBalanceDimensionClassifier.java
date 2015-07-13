package com.pb.sawdust.popsynth.em.classifiers;

import com.pb.sawdust.popsynth.em.BalanceDimensionClassifier;
import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.tabledata.DataRow;

import java.util.*;

/**
 * The {@code AbstractClassifier} ...
 *
 * @author crf
 *         Started 10/1/11 11:40 AM
 */
public abstract class AbstractBalanceDimensionClassifier<T> implements BalanceDimensionClassifier<T> {
    private final String name;
    private final Set<T> classificationCategories;
    private final TargetDataSpec<T> targetDataSpec;

    public AbstractBalanceDimensionClassifier(String name, Set<T> classificationCategories, TargetDataSpec<T> targetDataSpec) {
        this.name = name;
        this.classificationCategories = Collections.unmodifiableSet(new LinkedHashSet<>(classificationCategories)); //to preserve ordering, if it exists
        this.targetDataSpec = targetDataSpec;
    }

    @Override
    public String getDimensionName() {
        return name;
    }

    @Override
    public Set<T> getClassificationCategories() {
        return classificationCategories;
    }

    @Override
    public Set<String> getTargetFields() {
        return new HashSet<>(targetDataSpec.getTargetFields().values());
    }

    @Override
    public Map<T,Double> getTargetMap(DataRow row) {
        Map<T,Double> targetMap = new LinkedHashMap<>();
        Map<T,String> fields = targetDataSpec.getTargetFields();
        for (T t : classificationCategories)
            targetMap.put(t,row.getCellAsDouble(fields.get(t)));
        return targetMap;
    }

    @Override
    public Geography<?,?> getTargetGeography() {
        return targetDataSpec.getTargetGeography();
    }
}
