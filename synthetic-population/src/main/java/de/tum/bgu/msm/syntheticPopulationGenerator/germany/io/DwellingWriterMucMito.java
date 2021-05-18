package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.io.output.DwellingWriter;
import de.tum.bgu.msm.io.output.HouseholdWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class DwellingWriterMucMito implements DwellingWriter {

    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final static Logger logger = Logger.getLogger(DwellingWriterMucMito.class);

    public DwellingWriterMucMito(HouseholdDataManager householdData, RealEstateDataManager realEstateData) {
        this.householdData = householdData;
        this.realEstateData = realEstateData;
    }


    @Override
    public void writeDwellings(String path) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,hhId,zone,coordX,coordY");
        for (Dwelling dd : realEstateData.getDwellings()) {
            if (dd.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
            pwh.print(dd.getId());
            pwh.print(",");
            pwh.print(dd.getResidentId());
            pwh.print(",");
            pwh.print(dd.getZoneId());
            pwh.print(",");
            pwh.print(dd.getCoordinate().x);
            pwh.print(",");
            pwh.println(dd.getCoordinate().y);
        }
        pwh.close();
    }
}
