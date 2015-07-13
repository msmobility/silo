package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code CompositeRowWiseDataTableTransformation} class provides a simple structure for combining multiple row-wise
 * data table transformations into a single row-wise transformation. It provides slight efficiency gains in that the transformations
 * will be performed in a single step, rather than through multiple iterations through the input data table.
 * <p>
 * Instances of this class implement {@code Iterable<R>}, and will iterate over the composited data transformations in the
 * order that they are applied.
 *
 * @param <R>
 *        The type of the row-wise transformations that are composited by this class.
 *
 * @author crf
 *         Started 1/20/12 6:25 AM
 */
public class CompositeRowWiseDataTableTransformation<R extends RowWiseDataTableTransformation> extends RowWiseDataTableTransformation implements Iterable<R> {
    private final List<R> transformations;

    /**
     * Constructor specifying the transformations to composite.
     *
     * @param transformations
     *        The transformations this transformation will be comprised of, in the order they will be applied.
     */
    public CompositeRowWiseDataTableTransformation(Collection<? extends R> transformations) {
        this.transformations = new LinkedList<>(transformations);
    }

    @Override
    public void transformRow(DataRow row, DataTable table, int rowIndex) {
        for (RowWiseDataTableTransformation transformation : transformations) {
            transformation.transformRow(row,table,rowIndex);
            row = table.getRow(rowIndex); //so we can update row values...
        }
    }

    @Override
    public Iterator<R> iterator() {
        return transformations.iterator();
    }
}
