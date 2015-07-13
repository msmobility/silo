package com.pb.sawdust.data.census.pums.acs;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataField;

/**
 * The {@code AcsDataDictionary} provides a skeletal implementation of {@code PumaDataDictionary} for use with American
 * Community Survey (ACS) PUMS data.  ACS PUMS data is held in CSV files, with a separate file for person
 * and household records.
 *
 * @param <H>
 *        The type of the household field this reader reads. This field must be an {@code enum}.
 *
 * @param <P>
 *        The type of the person field this reader reads. This field must be an {@code enum}.
 *
 * @author crf
 *         Started 10/14/11 7:17 AM
 */
public abstract class AcsDataDictionary<H extends Enum<H> & AcsDataDictionary.AcsHouseholdField ,P extends Enum<P> & AcsDataDictionary.AcsPersonField> implements PumaDataDictionary<H,P> {
    private final Class<H> householdFieldClass;
    private final Class<P> personFieldClass;

    /**
     * Constructor specifying the household and person field classes used by the dictionary.
     *
     * @param householdFieldClass
     *        The household field class.
     *
     * @param personFieldClass
     *        The person field class.
     */
    public AcsDataDictionary(Class<H> householdFieldClass, Class<P> personFieldClass) {
        this.householdFieldClass = householdFieldClass;
        this.personFieldClass = personFieldClass;
    }

    public Class<H> getHouseholdFieldClass() {
        return householdFieldClass;
    }

    public Class<P> getPersonFieldClass() {
        return  personFieldClass;
    }

    /**
     * The {@code AcsHouseholdField} interface is an extension of {@code PumaDataHouseholdField} used to indicate an American
     * Community Survey (ACS) PUMS microdata household record field.
     */
    public static interface AcsHouseholdField extends PumaDataField.PumaDataHouseholdField {}

    /**
     * The {@code AcsPersonField} interface is an extension of {@code PumaDataPersonField} used to indicate an American
     * Community Survey (ACS) PUMS microdata person record field.
     */
    public static interface AcsPersonField extends PumaDataField.PumaDataPersonField {}
}
