package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.Collection;

public class DefaultHouseholdWriter implements HouseholdWriter {

    private final Collection<Household> householdData;
    private final static Logger logger = Logger.getLogger(DefaultHouseholdWriter.class);

    public DefaultHouseholdWriter(Collection<Household> households) {
        this.householdData = households;
    }
    @Override
    public void writeHouseholds(String path) {
        logger.info("  Writing household file to " + path);
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(path, false);
        pwh.println("id,dwelling,hhSize,autos");
        for (Household hh : householdData) {
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
            pwh.println((int) hh.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count());
        }
        pwh.close();
    }
}
