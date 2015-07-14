package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaDataField;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.WrappedDataTable;

import java.util.*;

/**
 * The {@code PumaTableTransformation} class provides a structure for combining multiple PUMA data table transformations
 * into a single transformation.
 *
 * @author crf
 *         Started 1/20/12 7:57 AM
 */
public class PumaTablesTransformation {
    private final List<PumaDataTableTransformation> transformations;

    public PumaTablesTransformation(List<? extends PumaDataTableTransformation> transformations) {
        this.transformations = new LinkedList<>(transformations);
    }

    public PumaTablesTransformation(PumaDataTableTransformation transformation) {
        this(Arrays.asList(transformation));
    }

    public PumaTables transformTables(PumaTables tables) {
        if (transformations.size() == 0)
            return tables;
        DataTable hhTable = tables.getHouseholdTable();
        DataTable personTable = tables.getPersonTable();
        for (PumaDataTableTransformation transformation : transformations) {
            switch(transformation.getTransformationType()) {
                case HOUSEHOLD : {
                    if (transformation instanceof PumaDataGroupTransformation)
                        hhTable = transformation.transform(new WrappedPumaTable(tables,true));
                    else
                        hhTable = transformation.transform(hhTable);
                } break;
                case PERSON : {
                    if (transformation instanceof PumaDataGroupTransformation)
                        personTable = transformation.transform(new WrappedPumaTable(tables,false));
                    else
                        personTable = transformation.transform(personTable);
                } break;
            }
        }
        return new PumaTables(hhTable,personTable,tables.getDataDictionary());
    }

    public PumaTablesTransformation composite(PumaTablesTransformation transformation) {
        List<PumaDataTableTransformation> newTransformations = new LinkedList<>(transformations);
        newTransformations.addAll(transformation.transformations);
        return new PumaTablesTransformation(newTransformations);
    }

    public PumaTablesTransformation composite(PumaDataTableTransformation transformation) {
        List<PumaDataTableTransformation> newTransformations = new LinkedList<>(transformations);
        newTransformations.add(transformation);
        return new PumaTablesTransformation(newTransformations);
    }

    private class WrappedPumaTable extends WrappedDataTable {
        private final PumaTables tables;
        private final boolean hhTable;

        public WrappedPumaTable(PumaTables tables, boolean hhTable) {
            super(hhTable ? tables.getHouseholdTable() : tables.getPersonTable());
            this.tables = tables;
            this.hhTable = hhTable;
        }

        public Iterator<DataRow> iterator() {
            final Iterator<PumaTables.PumaDataRow> it = hhTable ? tables.getHouseholdPumaDataRowIterator() : tables.getPersonPumaDataRowIterator();
            return new Iterator<DataRow>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public DataRow next() {
                    return it.next();
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }
    }
}
