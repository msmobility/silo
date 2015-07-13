package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataField;
import com.pb.sawdust.data.census.pums.PumaDataReader;
import com.pb.sawdust.data.census.pums.transform.PumaTablesTransformation;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.read.TableReader;

import java.util.Set;

/**
 * The {@code PumaDataReaderSpec} interface is used to specify the parameters for reading and transforming PUMA data into
 * data tables.
 *
 * @param <H>
 *        The type of the household fields the reader spec applies to.
 *
 * @param <P>
 *        The type of the person fields the reader spec applies to.
 *
 * @author crf
 *         Started 1/19/12 2:14 PM
 */
public interface PumaDataReaderSpec<H extends Enum<H> & PumaDataField.PumaDataHouseholdField,
                                    P extends Enum<P> & PumaDataField.PumaDataPersonField> {
    /**
     * Get the reader for reading the PUMA data.
     *
     * @return the PUMA data reader.
     */
    PumaDataReader<?,H,P> getReader();

    /**
     * Get the PUMA dictionary for the PUMA data.
     *
     * @return the PUMA data dictionary.
     */
    PumaDataDictionary<H,P> getDictionary();

    /**
     * Get the set of household fields that should be read/saved when reading the PUMA data.
     *
     * @return the household PUMA data fields which will be read in.
     */
    Set<H> getHouseholdFields();

    /**
     * Get the set of person fields that should be read/saved when reading the PUMA data.
     *
     * @return the person PUMA data fields which will be read in.
     */
    Set<P> getPersonFields();

    /**
     * Read the PUMA data from a reader into a data table. This method is used to specify the actual {@code DataTable} instance
     * that this spec will return when the PUMA data is read in.
     *
     * @param reader
     *        The table reader for the PUMA data.
     *
     * @return a data table containing the PUMA data read from {@code reader}.
     */
    DataTable getTable(TableReader reader);

    /**
     * Get the PUMA data table transformations which will be applied to the PUMA data after it is read in.
     *
     * @return the transformations for the PUMA data.
     */
    PumaTablesTransformation getTransformation();
}
