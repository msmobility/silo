package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.io.output.HouseholdWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class HouseholdWriterMucMito implements HouseholdWriter {

    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final static Logger logger = Logger.getLogger(HouseholdWriterMucMito.class);

    public HouseholdWriterMucMito(HouseholdDataManager householdData, RealEstateDataManager realEstateData) {
        this.householdData = householdData;
        this.realEstateData = realEstateData;
    }
    @Override
    public void writeHouseholds(String path) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        for (Household hh : householdData.getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            pwh.print(realEstateData.getDwelling(hh.getDwellingId()).getZoneId());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();
    }

    public void writeHouseholdsWithCoordinates(String path, int subPopulation) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, true);
        if (subPopulation == 0) {
            pwh.println("id,zone,hhSize,autos,coordX,coordY");
        }
        for (Household hh : householdData.getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(realEstateData.getDwelling(hh.getDwellingId()).getZoneId());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.print(hh.getAutos());
            pwh.print(",");
            pwh.print(realEstateData.getDwelling(hh.getDwellingId()).getCoordinate().x);
            pwh.print(",");
            pwh.println(realEstateData.getDwelling(hh.getDwellingId()).getCoordinate().y);
        }
        pwh.close();
    }
}
