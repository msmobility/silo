package com.pb.sawdust.tensor.read;

import java.util.List;
import java.util.Map;

/**
 * The {@code IndexReader} provides a framework for reading indices.  Implementing classes will provide the information
 * and data needed to reconstruct stored {@code Index} instances.
 *
 * @param <I>
 *        The type of ids in the index read by this reader.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 1:49:18 PM
 */
public interface IndexReader<I> {

    /**
     * The base dimension the index read by this reader refers to. This is the base dimensionality that the read index
     * will refer to, not the shape of the index as returned by {@code index.getDimensions()}.
     *
     * @return the base dimension the index read by this reader corresponds to.
     */
    int[] getBaseDimensions();

    /**
     * Get the reference index values for the index read by this reader.  That is, the returned array should have the an
     * array for each dimension of the index read by this reader, and each sub-array will be the length of the corresponding
     * index dimension. Each element in the sub-array will hold the base index value that the the read index refers to
     * for that location. If the index performs no referencing (is just a pass through for the underlying dimensionality)
     * then this method will return {@code null}.
     *
     * @return the reference index values for the index read by this reader, or {@code null} if it is a standard index.
     */
    int[][] getReferenceIndex(); //null if just a pass through

    /**
     * Get a list of ids (one list for each dimension) for the index read in by this reader.  If the index is just a
     * standard index (pass through for standard 0-based int indexing), then this method will return {@code null}.
     *
     * @return the ids for the tensor read in by this reader, or {@code null} if it is a standard index.
     */
    List<List<I>> getIndexIds(); //null if no ids

    /**
     * Get the metadata for the index read in by this reader.
     *
     * @return the metadata for the index read in by this reader.
     */
    Map<String,Object> getIndexMetadata();
}
