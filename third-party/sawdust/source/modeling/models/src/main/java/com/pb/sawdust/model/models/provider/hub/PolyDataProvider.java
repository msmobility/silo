package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.util.collections.SetList;

import java.util.Set;

/**
 * The {@code PolyDataProvider} interface represents a {@code DataProviderHub} which can retrieve variable data from
 * multiple data sources (such  as {@code DataProvider}s) at once.  That is, rather than retrieving a vector of data for
 * a given variable, it is possible to get a matrix of data for that variable, where every column in the matrix represents
 * a data vector for that variable from a separate source. This functionality is useful when calculations can be more efficiently
 * applied across multiple data sources by variable, instead of across multiple variable by data source.
 * <p>
 * The data provider keys provide access not only to the base interfaces data providers, but also the ordering of the columns
 * (data sources) in poly data.  That is, because the variable data is provided across multiple providers at once, the
 * ordering of the providers must be well-defined (accessible through {@link #getDataKeys()}). Poly data variables are
 * expected to return values for all data provider keys (so the number of data columns in a poly data matrix equals the
 * number of data provider keys), and to use the keys as ids on the columsns (dimension index 1) of the poly data matrix.
 * <p>
 * In this interface, "regular" (shared) and "poly" providers are inherently separated.  That is, calling the base interface's
 * methods will return results only for the non-poly variables; the methods in this interface (those with {@code poly} in
 * the name) return results for the poly variables. This separation facilitates the separation of "regular" and "poly" calculations.
 * To get a provider combining the two variables, the {@link #getFullProvider(Object)} can be used.
 *
 * @param <K>
 *        The type of the data provider key.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 11:37:55 AM
 */
public interface PolyDataProvider<K> extends DataProviderHub<K> {

    /**
     * Get the <i>poly</i> variables that this poly data provider contains.  This will not include the shared (data provider
     * hub) variables.
     *
     * @return the variables this poly data provider contains.
     */
    Set<String> getPolyDataVariables();

    /**
     * Get the poly data for a specified variable.  Each column represents one data source's variable values, and the
     * column ordering is defined the same as returned by {@link #getDataKeys()}. The columns (dimension index 1) of the
     * matrix will have ids of type {@code K} matching the data provider keys.  (The rows will generally have keys corresponding
     * to their original row numbers; however, to maintain generality when partitioning, the poly data matrix rows should
     * not be referred to by these keys.)
     *
     * @param variable
     *        The variable to get the poly data for.
     *
     * @return the data for {@code variable} for all providers in this provider hub.
     *
     * @throws IllegalArgumentException if {@code variable} is not associated with any poly data in this provider.
     */
    IdDoubleMatrix<? super K> getPolyData(String variable);

    /**
     * {@inheritDoc}
     *
     * This method will return a partitioned {@code PolyDataProvider}.
     */
    PolyDataProvider<K> getSubDataHub(int start, int end);

    /**
     * Get the provider for a given key combining the poly and regular (shared) variables.
     *
     * @param key
     *        The key to get the provider for.
     *
     * @return the provider for {@code key} combining poly and regular variables.
     *
     * @throws IllegalArgumentException if {@code key} is not available from this provider.
     */
    DataProvider getFullProvider(K key);

    /**
     * Get a ordered set of all of the data keys available from this provider hub.
     *
     * @return the data keys available from this hub.
     */
    SetList<K> getDataKeys();
}
