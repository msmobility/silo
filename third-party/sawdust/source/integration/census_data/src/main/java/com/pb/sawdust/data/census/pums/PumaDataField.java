package com.pb.sawdust.data.census.pums;

import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code PumaDataField} interface provides a structure for holding information about a particular field (column)
 * in a PUMA/PUMS file.
 *
 * @author crf
 *         Started 10/13/11 7:46 AM
 */
public interface PumaDataField {
    /**
     * Get the name for this field/column.
     *
     * @return this field's name.
     */
    String getColumnName();

    /**
     * Get the (0-based) ordinal number of this field/column.
     *
     * @return the column number of this field.
     */
    int getColumnOrdinal();

    /**
     * Get the data type of this field/column.
     *
     * @return this field's data type.
     */
    DataType getColumnType();

    /**
     * Get a description of this field/column.
     *
     * @return this field's description.
     */
    String getColumnDescription();
    Enum getSelf(); //to ensure we are an enum

    /**
     * The {@code PumaDataHouseholdField} interface is an extension of {@code PumaDataField} used to indicate a household
     * record field.
     */
    public interface PumaDataHouseholdField extends PumaDataField {}

    /**
     * The {@code PumaDataPersonField} interface is an extension of {@code PumaDataField} used to indicate a person
     * record field.
     */
    public interface PumaDataPersonField extends PumaDataField {}
}
