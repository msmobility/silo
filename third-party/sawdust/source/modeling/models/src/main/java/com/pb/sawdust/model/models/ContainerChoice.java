package com.pb.sawdust.model.models;

/**
 * The {@code ContainerChoice} ...
 *
 * @author crf
 *         Started 4/10/12 12:06 PM
 */
public interface ContainerChoice<C> extends Choice {
    C getChoiceContents();
}
