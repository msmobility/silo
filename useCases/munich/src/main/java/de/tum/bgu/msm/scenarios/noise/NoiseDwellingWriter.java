package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.io.output.DwellingWriter;
import de.tum.bgu.msm.matsim.noise.NoiseDataContainer;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class NoiseDwellingWriter implements DwellingWriter {

    public NoiseDwellingWriter(NoiseDataContainer noiseDataContainer) {
        this.realEstateDataManager = noiseDataContainer.getRealEstateDataManager();
    }

    private final static Logger logger = Logger.getLogger(DefaultDwellingWriter.class);
    private final RealEstateDataManager realEstateDataManager;

    public NoiseDwellingWriter(RealEstateDataManager realEstateDataManager) {
        this.realEstateDataManager = realEstateDataManager;
    }

    @Override
    public void writeDwellings(String path) {
        logger.info("  Writing dwelling file to " + path);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(path, false);
        pwd.print("id,zone,type,hhID,bedrooms,quality,monthlyCost,yearBuilt,coordX,coordY,noise");
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
            if(dd.getCoordinate() != null) {
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
            } else {
                pwd.print("NULL,NULL");
            }
            pwd.print(",");
            pwd.print(((NoiseDwelling)dd).getNoiseImmission());
            pwd.println();
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();
    }
}
