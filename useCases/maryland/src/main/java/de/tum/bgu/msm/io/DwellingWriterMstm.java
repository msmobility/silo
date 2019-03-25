package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingMstm;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.io.output.DwellingWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class DwellingWriterMstm implements DwellingWriter {

    private final static Logger logger = Logger.getLogger(DwellingWriterMstm.class);
    private final RealEstateDataManager dwellingData;

    public DwellingWriterMstm(RealEstateDataManager dwellingData) {
        this.dwellingData = dwellingData;
    }

    @Override
    public void writeDwellings(String path) {
        logger.info("  Writing dwelling file to " + path);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(path, false);
        pwd.print("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt");
        pwd.println();

        for (Dwelling dd : dwellingData.getDwellings()) {
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
            pwd.print(((DwellingMstm)dd).getRestriction());
            pwd.print(",");
            pwd.print(dd.getYearBuilt());
            pwd.println();
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();
    }
}
