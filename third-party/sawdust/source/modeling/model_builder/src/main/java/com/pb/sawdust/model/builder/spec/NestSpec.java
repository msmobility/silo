package com.pb.sawdust.model.builder.spec;

import com.pb.sawdust.model.models.Choice;

import java.util.Set;

/**
 * The {@code NestSpec} ...
 *
 * @author crf
 *         Started 4/30/12 6:18 AM
 */
public interface NestSpec {
    double getNestingCoefficient();
    Set<ChoiceSpec> getNestChoices();
    String getNestName();
    Set<NestSpec> getChildNests();
}
