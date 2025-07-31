package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.io.output.DwellingWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;

public class DwellingWriterTak implements DwellingWriter {
    private final static Logger logger = LogManager.getLogger(DwellingWriterTak.class);
    private final RealEstateDataManager realEstateDataManager;


    public DwellingWriterTak(RealEstateDataManager realEstateDataManager) {
        this.realEstateDataManager = realEstateDataManager;
    }

    @Override
    public void writeDwellings(String path) {
        logger.info("  Writing dwelling file to " + path);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(path, false);
        pwd.print("id,zone,type,hhID,bedrooms,quality,monthlyCost,yearBuilt");
        pwd.print(",");
        pwd.print("floor");
        pwd.print(",");
        pwd.print("usage");
        pwd.print(",");
        pwd.print("coordX");
        pwd.print(",");
        pwd.print("coordY");

        pwd.println();

        for (Dwelling dd : realEstateDataManager.getDwellings()) {
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
            pwd.print(dd.getFloorSpace());
            pwd.print(",");
            pwd.print(dd.getUsage());
            pwd.print(",");
            pwd.print("NULL");
            pwd.print(",");
            pwd.print("NULL");
            pwd.println();
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();
    }
}
