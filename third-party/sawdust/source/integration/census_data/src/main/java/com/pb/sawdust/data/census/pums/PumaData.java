package com.pb.sawdust.data.census.pums;

import com.pb.sawdust.data.census.pums.acs.AcsDataDictionary2005_2009;
import com.pb.sawdust.data.census.pums.acs.AcsDataDictionary2007_2009;
import com.pb.sawdust.data.census.pums.acs.AcsDataDictionary2009;
import com.pb.sawdust.data.census.pums.decennial.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code PumaData} enum specifies the various PUMA data products that are available for use in this packages classes
 * (and sub-classes).
 *
 * @author crf
 *         Started 1/24/12 11:53 AM
 */
public enum PumaData {
    /**
     * The {@code PumaData} instance corresponding to {@link PumsDataDictionary1990}.
     */
    PUMS_1990(PumsDataDictionary1990.getDictionary(),PumaDataClass.DECENNIAL),
    /**
     * The {@code PumaData} instance corresponding to {@link PumsDataDictionary2000FivePercent}.
     */
    PUMS_2000_FIVE_PERCENT(PumsDataDictionary2000FivePercent.getDictionary(),PumaDataClass.DECENNIAL),
    /**
     * The {@code PumaData} instance corresponding to {@link PumsDataDictionary2000OnePercent}.
     */
    PUMS_2000_ONE_PERCENT(PumsDataDictionary2000OnePercent.getDictionary(),PumaDataClass.DECENNIAL),
    /**
     * The {@code PumaData} instance corresponding to {@link AcsDataDictionary2009}.
     */
    ACS_2009(AcsDataDictionary2009.getDictionary(),PumaDataClass.ACS),
    /**
     * The {@code PumaData} instance corresponding to {@link AcsDataDictionary2007_2009}.
     */
    ACS_2007_2009(AcsDataDictionary2007_2009.getDictionary(),PumaDataClass.ACS),
    /**
     * The {@code PumaData} instance corresponding to {@link AcsDataDictionary2005_2009}.
     */
    ACS_2005_2009(AcsDataDictionary2005_2009.getDictionary(),PumaDataClass.ACS)
    ;
    
    private final PumaDataDictionary<?,?> dictionary;
    private final PumaDataClass dataClass;
    
    private PumaData(PumaDataDictionary<?,?> dictionary, PumaDataClass dataClass) {
        this.dictionary = dictionary;
        this.dataClass = dataClass;
    }

    /**
     * Get the data dictionary corresponding to this PUMA data category.
     *
     * @return this PUMA data category's data dictionary.
     */
    public PumaDataDictionary<?,?> getDictionary() {
        return dictionary;
    }

    /**
     * Get the PUMA data type that this data category belongs to.
     *
     * @return this PUMA data category's PUMA data type.
     */
    public PumaDataClass getDataClass() {
        return dataClass;
    }

    /**
     * Get a {@code PumaDataReader} instance for reading data from files produced form this PUMA data category.
     *
     * @param files
     *        A map holding the household file as a key, and the person file as a value. For data categories which have
     *        household and person tables in one file, the key and value should be the same.
     *
     * @return a reader for {@code files}.
     *
     * @throws IllegalArgumentException if the PUMA data category holds both household and person tables in the same file,
     *                                  and any entry in {@code files} doesn't have its key equal to its value.
     */
    public PumaDataReader<?,?,?> getReader(Map<String,String> files) {
        switch (this) {
            case ACS_2009 : return new AcsDataDictionary2009.AcsDataReader2009(files);
            case ACS_2007_2009 : return new AcsDataDictionary2007_2009.AcsDataReader2007_2009(files);
            case ACS_2005_2009 : return new AcsDataDictionary2005_2009.AcsDataReader2005_2009(files);
        }
        for (String householdFile : files.keySet())
            if (!files.get(householdFile).equals(householdFile))
                throw new IllegalArgumentException(String.format("Household and person files must be identical for the %s puma data category: %s , %s",this,householdFile,files.get(householdFile)));
        return getReader(new LinkedList<>(files.keySet()));
    }

    private PumaDataReader<?,?,?> getReader(List<String> files) {
        switch (this) {
            case PUMS_1990 : return new PumsDataDictionary1990.PumsDataReader1990(files);
            case PUMS_2000_FIVE_PERCENT : return new PumsDataDictionary2000FivePercent.PumsDataReader2000FivePercent(files);
            case PUMS_2000_ONE_PERCENT : return new PumsDataDictionary2000OnePercent.PumsDataReader2000OnePercent(files);
            default : throw new IllegalArgumentException("Cannot build a reader from a list for this puma type: " + this);
        }
    }


    /**
     * The {@code PumaDataType} enum identifies broad types of PUMA data products. These are often useful for distinguishing
     * the file formats used to hold the data.
     */
    public static enum PumaDataClass {
        /**
         * The {@code PumaDataType} representing the American Community Survey (ACS) data type.
         */
        ACS(true),
        /**
         * The {@code PumaDataType} representing the decennial United States Census data type.
         */
        DECENNIAL(false);

        private final boolean separateFiles;

        private PumaDataClass(boolean separateFiles) {
            this.separateFiles = separateFiles;
        }

        /**
         * Get whether or not this PUMA data type uses separate files for household and person tables or not.
         *
         * @return {@code true} if this data type uses separate files, {@code false} if not.
         */
        public boolean usesSeparateFiles() {
            return separateFiles;
        }
    }
}
