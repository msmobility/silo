package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.Collection;

public class DefaultDwellingWriter implements DwellingWriter {

    private final static Logger logger = Logger.getLogger(DefaultDwellingWriter.class);
    private final Collection<Dwelling> dwellings;

    public DefaultDwellingWriter(Collection<Dwelling> dwellings) {
        this.dwellings = dwellings;
    }

    @Override
    public void writeDwellings(String path) {
        logger.info("  Writing dwelling file to " + path);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(path, false);
        pwd.print("id,zone,type,hhID,bedrooms,quality,monthlyCost,yearBuilt,coordX,coordY");
        pwd.println();

        for (Dwelling dd : dwellings) {
            pwd.print(dd.getId());
            pwd.print(",");
            pwd.print(dd.getZoneId());
            pwd.print(",\"");
            pwd.print(dd.getType());
            pwd.print("\",");
            pwd.print(dd.getResidentId());
            pwd.print(",");
            pwd.print(dd.getBedrooms());
            pwd.print(",");
            pwd.print(dd.getQuality());
            pwd.print(",");
            pwd.print(dd.getPrice());
            pwd.print(",");
            pwd.print(dd.getYearBuilt());
            pwd.print(",");
            if(dd.getCoordinate() != null) {
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
            } else {
                pwd.print("NULL,NULL");
            }
            pwd.println();
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();
    }
}
