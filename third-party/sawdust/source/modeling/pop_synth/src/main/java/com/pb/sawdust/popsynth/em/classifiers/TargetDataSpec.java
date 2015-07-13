package com.pb.sawdust.popsynth.em.classifiers;

import com.pb.sawdust.geography.Geography;

import java.util.Map;

/**
 * The {@code TargetDataSpec} ...
 *
 * @author crf
 *         Started 10/1/11 6:45 PM
 */
public interface TargetDataSpec<T> {
    Map<T,String> getTargetFields();
    Geography<?,?> getTargetGeography();
}
