package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.tabledata.transform.DataTableTransformation;

import java.util.List;

/**
 * The {@code PumaDataTableTransformation} interface specifies a data table transformation that acts on PUMA data tables.
 * A PUMA data transformation may act on either the household data or the person data (as identified by its transformation
 * {@code PumaDataType}). The transformation is considered to be specific to a PUMA data product, identified by its corresponding
 * {@code PumaDataDictionary}.
 *
 * @author crf
 *         Started 1/20/12 8:35 AM
 */
public interface PumaDataTableTransformation extends DataTableTransformation {
    /**
     * Get the data dictionary associated with the PUMA data that this transformation acts on.
     *
     * @return the data dictionary specific to this transformation.
     */
    PumaDataDictionary<?,?> getDataDictionary();

    /**
     * Indicates the type of PUMA data this transformation acts on.
     *
     * @return the type of PUMA data this transformation applies to.
     */
    PumaDataType getTransformationType();
}
