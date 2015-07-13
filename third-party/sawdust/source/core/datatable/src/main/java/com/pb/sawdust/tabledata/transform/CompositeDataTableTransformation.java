package com.pb.sawdust.tabledata.transform;

import com.pb.sawdust.tabledata.DataTable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code CompositeDataTableTransformation} class provides a simple structure for combining multiple data table transformations
 * into a single transformation. It provides no real efficiency gains (transformations are just called sequentially), but
 * can simplify the application of complex transformation.
 * <p>
 * Instances of this class implement {@code Iterable<DataTableTransformation>}, and will iterate over the composited data
 * transformations in the order that they are applied.
 *
 * @author crf
 *         Started 1/20/12 7:25 AM
 */
public class CompositeDataTableTransformation implements DataTableTransformation,Iterable<DataTableTransformation> {
    private final List<DataTableTransformation> transformations;

    /**
     * Constructor specifying the transformations to composite.
     *
     * @param transformations
     *        The transformations this transformation will be comprised of, in the order they will be applied.
     */
    public CompositeDataTableTransformation(List<? extends DataTableTransformation> transformations) {
        this.transformations = new LinkedList<>(transformations);
    }

    @Override
    public DataTable transform(DataTable table) {
        for (DataTableTransformation transformation : transformations)
            table = transformation.transform(table);
        return table;
    }

    @Override
    public Iterator<DataTableTransformation> iterator() {
        return transformations.iterator();
    }
}
