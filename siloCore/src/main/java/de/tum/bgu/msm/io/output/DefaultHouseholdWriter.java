package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class DefaultHouseholdWriter implements HouseholdWriter {

    private final HouseholdDataManager householdData;
    private final static Logger logger = Logger.getLogger(DefaultHouseholdWriter.class);

    public DefaultHouseholdWriter(HouseholdDataManager householdData) {
        this.householdData = householdData;
    }
    @Override
    public void writeHouseholds(String path) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,dwelling,hhSize,autos");
        for (Household hh : householdData.getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();
    }
}
