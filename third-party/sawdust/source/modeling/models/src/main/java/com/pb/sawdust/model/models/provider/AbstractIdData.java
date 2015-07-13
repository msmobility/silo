package com.pb.sawdust.model.models.provider;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code AbstractIdData} class provides a simple way to get unique data identifiers. An instance of a class extending
 * this one and using the no-argument constructor will be given a data identifier that is guaranteed to be distinct.
 * (Up to <code>Integer.MAX_VALUE-Integer.MAX_VALUE</code> unique identifiers can be allocated before overlaps will
 * occur.)
 * <p>
 * The default starting id number is zero, but this can be changed by setting the system property <code>initial.data.id</code>
 * to the desired starting value. This can help seperate virtual machines use different ids, which may be useful in distributed
 * applications.
 * <p>
 * It is possible to set the data identifier explicitly using the {@link #AbstractIdData(int)} constructor, but this only
 * allows previously allocated identifiers to be used.  This is to allow for cases where a new data source is known to
 * have equivalent data to that of a previously created one.  If specifying unused data identifiers is required, then
 * a different implementation of {@code IdData} should be used, though this class cannot recognize such cases and data
 * id conflicts may inadverently occur.
 *
 * @author crf <br/>
 *         Started Sep 3, 2010 10:54:28 AM
 */
public abstract class AbstractIdData implements IdData {
    /**
     * The system property used to set the initial data id value. The value of this variable is "<code>initial.data.id</code>".
     */
    public static final String INITIAL_DATA_ID_PROPERTY = "initial.data.id";
    private static int initialDataId = Integer.parseInt(System.getProperty(INITIAL_DATA_ID_PROPERTY,"0"));
    private static final AtomicInteger counter = new AtomicInteger(initialDataId);
    private int id;

    /**
     * Default constructor.
     */
    public AbstractIdData() {
        id = counter.getAndIncrement();
    }

    /**
     * Constructor specifying the data identifier to use.  Only identifiers that have been previously allocated can be
     * passed to this constructor.
     *
     * @param id
     *        The data identifier.
     *
     * @throws IllegalArgumentException if {@code id} has not been allocated as a data identifier yet.
     */
    public AbstractIdData(int id) {
        int currentId = counter.get();
        if (id >= initialDataId && id < currentId)
            this.id = id;
        else if (currentId < initialDataId && //wrapped
                 (id >= initialDataId || id < currentId))
            this.id = id;
        else
            throw new IllegalArgumentException("Cannot assigne data id which has not been used yet.");
    }

    /**
     * Get the identifier for this data source.
     *
     * @return the data identifier.
     */
    public int getDataId() {
        return id;
    }
}
