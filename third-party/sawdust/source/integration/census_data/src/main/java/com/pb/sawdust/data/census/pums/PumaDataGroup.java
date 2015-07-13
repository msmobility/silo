package com.pb.sawdust.data.census.pums;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.util.TableDataUtil;
import com.pb.sawdust.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* The {@code PumaDataGroup} provides the structure for holding all of the PUMA table data for a single household. That is,
 * this class is a container for a single household data row, and the rows from the person table corresponding to the
 * household row.
*
* @author crf
*         Started 1/20/12 11:54 AM
*/
public class PumaDataGroup {
    private final DataRow householdRow;
    private final Iterable<DataRow> personRows;

    /**
     * Constructor specifying the household data row and person data rows.
     *
     * @param householdRow
     *        The household data row.
     *
     * @param personRows
     *        The person data rows.
     */
    public PumaDataGroup(DataRow householdRow, Iterable<DataRow> personRows) {
        this.householdRow = householdRow;
        this.personRows = personRows;
    }

    /**
     * Get the household data row for this group.
     *
     * @return this group's household data row.
     */
    public DataRow getHouseholdRow() {
        return householdRow;
    }

    /**
     * Get an iterable over the person rows for this group.
     *
     * @return this group's person rows, as an iterable.
     */
    public Iterable<DataRow> getPersonRows() {
        return personRows;
    }

    /**
     * Get the number of persons in this group.
     *
     * @return this group's person count.
     */
    public int getPersonCount() {
        int persons = 0;
        for (DataRow row : personRows)
            persons++;
        return persons;
    }

    private String rowsToString(int indent, Iterable<DataRow> rows) {
        return StringUtil.indent(TableDataUtil.toString(rows),indent);
    }

    /**
     * Get a string representation of the data group which presents the data held by this group's data rows in a simple
     * text format.
     *
     * @return this group's data as text.
     */
    public String semiDescriptiveToString() {
        int personCount = getPersonCount();
        StringBuilder sb = new StringBuilder("PumaDataGroup\n");
        sb.append("\thousehold with ").append(personCount).append(" persons: \n").append(rowsToString(1,Arrays.asList(householdRow)));
        if (personCount > 0)
            sb.append("\t\tpersons:\n").append(rowsToString(2,personRows));
        return sb.toString();
    }
}
