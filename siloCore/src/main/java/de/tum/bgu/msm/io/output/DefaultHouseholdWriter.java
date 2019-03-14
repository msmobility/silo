package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.utils.SiloUtil;
import org.jboss.logging.Logger;

import java.io.PrintWriter;

public class DefaultHouseholdWriter implements HouseholdWriter {

    private final HouseholdData householdData;
    private final DwellingData dwellingData;
    private final static  Logger logger = Logger.getLogger(DefaultHouseholdWriter.class);

    public DefaultHouseholdWriter(HouseholdData householdData, DwellingData dwellingData) {
        this.householdData = householdData;
        this.dwellingData = dwellingData;
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
            int zone = -1;
            Dwelling dwelling = dwellingData.getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();
    }
}
