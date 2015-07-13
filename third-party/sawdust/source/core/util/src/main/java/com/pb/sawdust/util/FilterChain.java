package com.pb.sawdust.util;

import java.util.Set;
import java.util.LinkedHashSet;

/**
 * The {@code FilterChain} class is used to combine many filters into a single, composite filter. The order that filters
 * are added to a filter chain determines the order that they are applied when filtering.
 *
 * @param <I>
 *        The type of input which this class filters.
 *
 * @author crf <br/>
 *         Started: Jul 11, 2008 7:40:58 AM
 */
public class FilterChain<I> implements Filter<I> {
    private final Set<Filter<I>> filters = new LinkedHashSet<Filter<I>>();

    /**
     * Constructor initializing an empty filter chain.
     */
    public FilterChain() {
    }

    /**
     * Constructor specifying filter to initialize in this filter chain.
     *
     * @param filter
     *        The filter to initialize this filter chain with.
     */
    public FilterChain(Filter<I> filter) {
        addFilter(filter);
    }

    /**
     * Constructor initializing this filter chain with a series of filters.
     *
     * @param filters
     *        The filters to initialize this filter chain with. The filters will be applied in their order in the iterable.
     */
    public FilterChain(Iterable<Filter<I>> filters) {
        this();
        for (Filter<I> filter : filters)
            this.filters.add(filter);
    }

    /**
     * Add a filter to this filter chain. This filter will be added to the "end" of the chain; that is, calls to
     * {@code filter(I)} will only call this filter if the input passes all other (previously added) filters.
     *
     * @param filter
     *        The filter to add to this filter chain.
     *
     * @return this filter chain.
     */
    public Filter<I> addFilter(Filter<I> filter) {
        filters.add(filter);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * This method will sequentially apply the filters held by this filter chain in the order they were added. This
     * is a <i>short-circuiting</i> filtering process: if any filter in the chain fails (returns {@code false}), then
     * this method will return false without applying any of the subsequent filters in the chain. 
     */
    public boolean filter(I input) {
        for (Filter<I> filter : filters)
            if (!filter.filter(input))
                return false;
        return true;
    }
}
