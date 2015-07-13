package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataGroup;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.row.ContextDataRow;
import com.pb.sawdust.tabledata.transform.row.DataRowsRemoval;
import com.pb.sawdust.util.EmptyFilter;
import com.pb.sawdust.util.Filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code PumaDataRowsRemoval} is a row-removal data table transformation for PUMA data. Because PUMA data is spread
 * across both person and household tables, it is necessary to provide complementary transformations for both tables; the
 * {@link #getComplementaryRemoval()} method provides this complementary transformation. The complementary transformation
 * sets a row to be removed if
 * <ol>
 *     <li>the row's serial id is shared with that of a row removed from the complementary (original) table.</li>
 *     <li>if the row is a household row, there must be no more person entries in the person table with the household's serial id.</li>
 * </ol>
 * The complementary transformation should only be used <i>after</i> the original transformation has been applied. Also,
 * because of this stateful nature, this transformation should not be reused across tables which may have the same serial
 * id numbers for households/persons.
 *
 * @author crf
 *         Started 1/30/12 9:54 AM
 */
public class PumaDataRowsRemoval extends DataRowsRemoval<PumaDataGroup> implements PumaDataGroupTransformation {
    private final PumaDataDictionary<?,?> dictionary;
    private final PumaDataType transformationType;
    private final Set<Long> removedSerialIds;
    private final String removedHouseholdIdField;

    /**
     * Constructor specifying the dictionary, transformation type, and data row removal filter. A data row will be removed
     * if the filter return {@code true} for it.
     *
     * @param dictionary
     *        The data dictionary for this transformation.
     *
     * @param transformationType
     *        The type of PUMA data this transformation acts on.
     *
     * @param contextDataRowFilter
     *        The data row filter.
     */
    @SuppressWarnings("unchecked") //kind of abusing the type system, but correct in spirit and in use
    public PumaDataRowsRemoval(PumaDataDictionary<?,?> dictionary, PumaDataType transformationType,Filter<PumaTables.PumaDataRow> contextDataRowFilter) {
        super((Filter<ContextDataRow<PumaDataGroup>>) (Filter<? extends ContextDataRow<PumaDataGroup>>) contextDataRowFilter);
        this.dictionary = dictionary;
        this.transformationType = transformationType;
        removedSerialIds = Collections.synchronizedSet(new HashSet<Long>());
        removedHouseholdIdField = transformationType == PumaDataType.HOUSEHOLD ? dictionary.getHouseholdSerialIdField().getColumnName() :
                                                                                 dictionary.getPersonSerialIdField().getColumnName();
    }

    @Override
    public PumaTables.PumaDataRow getContextDataRow(DataRow row, DataTable table, int rowIndex) {
        return (PumaTables.PumaDataRow) row;
    }

    @Override
    public PumaDataDictionary<?,?> getDataDictionary() {
        return dictionary;
    }

    @Override
    public PumaDataType getTransformationType() {
        return transformationType;
    }

    /**
     * {@inheritDoc}
     *
     * This method is used to collect the serial ids of the rows that have been removed from the table. If this method is
     * overridden, then this implementation should be invoked somewhere in the code (via a {@code super} call).
     */
    protected void processRowToBeRemoved(ContextDataRow<PumaDataGroup> contextDataRow, DataTable table, int rowIndex) {
        removedSerialIds.add(contextDataRow.getCellAsLong(removedHouseholdIdField));
    }

    /**
     * Get the complementary row-removal transformation for this transformation. The returned transformation should only
     * be invoked <i>after</i> this one has completed.
     *
     * @return this transformation's complementary PUMA data transformation.
     */
    //only works within one table group; if serial ids are repeated across hh entries, then need to create new instance
    public PumaDataRowsRemoval getComplementaryRemoval() {
        if (transformationType == PumaDataType.PERSON) {
            return new PumaDataRowsRemoval(dictionary,PumaDataType.HOUSEHOLD,new EmptyFilter<PumaTables.PumaDataRow>()) {
                @Override
                public DataTable transform(DataTable table) {
                    return table; //do nothing
                }
            };
        }
        Filter<PumaTables.PumaDataRow> complementaryFilter = transformationType == PumaDataType.HOUSEHOLD ?
                new Filter<PumaTables.PumaDataRow>() {
                    @Override
                    public boolean filter(PumaTables.PumaDataRow input) {
                        return removedSerialIds.contains(input.getCellAsLong(dictionary.getHouseholdSerialIdField().getColumnName()));
                    }
                } :
                new Filter<PumaTables.PumaDataRow>() {
                    @Override
                    public boolean filter(PumaTables.PumaDataRow input) {
                        return removedSerialIds.contains(input.getCellAsLong(dictionary.getHouseholdSerialIdField().getColumnName())) && input.getContext().getPersonCount() == 0;
                    }
                };
        return new PumaDataRowsRemoval(dictionary,PumaDataType.HOUSEHOLD,complementaryFilter);
    }

}
