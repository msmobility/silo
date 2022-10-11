package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.io.output.DefaultHouseholdWriter;
import de.tum.bgu.msm.io.output.HouseholdWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.jboss.logging.Logger;

import java.io.PrintWriter;

public class HouseholdWriterMucDisability implements HouseholdWriter {

    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final static  Logger logger = Logger.getLogger(DefaultHouseholdWriter.class);

    public HouseholdWriterMucDisability(HouseholdDataManager householdData, RealEstateDataManager realEstateData) {
        this.householdData = householdData;
        this.realEstateData = realEstateData;
    }
   
    @Override
    public void writeHouseholds(String path) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,dwelling,hhSize,zone,autos");
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
            pwh.print(realEstateData.getDwelling(hh.getDwellingId()).getZoneId());
            pwh.print(",");
            pwh.println(hh.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count());
        }
        pwh.close();
    }
}