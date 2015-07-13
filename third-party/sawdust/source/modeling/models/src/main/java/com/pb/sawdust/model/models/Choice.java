package com.pb.sawdust.model.models;

/**
 * The {@code Choice} interface is used to indicate a choice used in a choice model. It provides a certain level of intention
 * on the part of a model (or modeler) as to what can be a choice. By virtue of being an interface, it allows {@code enum}s
 * to be choices, in addition to standard classes. The {@code String} identifier returned by {@link #getChoiceIdentifier()}
 * should be unique among its related choices, and is intended to be used as a (contextual) lookup when integrating with
 * other data and processes.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 10:34:11 AM
 */
public interface Choice {
    /**
     * The id uniquely corresponding to this choice.
     *
     * @return the identifier for this choice.
     */
    String getChoiceIdentifier();
}
