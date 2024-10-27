package de.tum.bgu.msm.health.io;

import cern.colt.map.tlong.OpenLongLongHashMap;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DoseResponseLookupReader {

    private final static Logger logger = Logger.getLogger(DoseResponseLookupReader.class);

    private EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> doseResponseData = new EnumMap<>(HealthExposures.class);
    private EnumMap<Diseases, String> diseaseOutcomeTypeLookup = new EnumMap<>(Diseases.class);

    public DoseResponseLookupReader() {
        for(HealthExposures exposures : HealthExposures.values()) {
            doseResponseData.put(exposures,new EnumMap<>(Diseases.class));
        }
    }

    public void readData(String basePath){
        String lookupTableFile = basePath + Properties.get().healthData.diseaseLookupTable;
        readDiseaseOutcomeLookupTable(lookupTableFile);

        for(HealthExposures exposures : HealthExposures.values()) {
            if(doseResponseData.get(exposures)==null){
                logger.error("Dose response data for exposure "+exposures.name()+" not found");
            }

            for(Diseases diseases : doseResponseData.get(exposures).keySet()){
                String doseResponseFile = basePath + exposures.toString().toLowerCase() + "/" +diseases.toString().toLowerCase() + "_" + diseaseOutcomeTypeLookup.get(diseases) + ".csv";
                File file = new File(doseResponseFile);
                if(!file.exists()){
                    throw new RuntimeException("Dose response data for exposure|disease: "+ exposures + "|" + diseases.name()+" not found");
                }
                doseResponseData.get(exposures).put(diseases, SiloUtil.readCSVfile(doseResponseFile));
            }
        }

    }

    private void readDiseaseOutcomeLookupTable(String path){
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posDisease = SiloUtil.findPositionInArray("acronym_inJava", header);
            int posAirPollutantPM = SiloUtil.findPositionInArray("air_pollution_pm25", header);
            int posAirPollutantNO = SiloUtil.findPositionInArray("air_pollution_no2", header);
            int posPhysicalActivity = SiloUtil.findPositionInArray("physical_activity", header);
            int posOutcome = SiloUtil.findPositionInArray("outcome", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                Diseases diseases = Diseases.valueOf(lineElements[posDisease]);
                int airPollutantPM = Integer.parseInt(lineElements[posAirPollutantPM]);
                int airPollutantNO = Integer.parseInt(lineElements[posAirPollutantNO]);
                int physicalActivity = Integer.parseInt(lineElements[posPhysicalActivity]);
                String outcome = lineElements[posOutcome];

                diseaseOutcomeTypeLookup.put(diseases, outcome);

                if (airPollutantPM == 1) {
                    doseResponseData.get(HealthExposures.AIR_POLLUTION_PM25).put(diseases, new TableDataSet());
                }

                if (airPollutantNO == 1) {
                    doseResponseData.get(HealthExposures.AIR_POLLUTION_NO2).put(diseases, new TableDataSet());
                }

                if (physicalActivity == 1) {
                    doseResponseData.get(HealthExposures.PHYSICAL_ACTIVITY).put(diseases, new TableDataSet());
                }

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading dose-response file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
            logger.fatal(e.getMessage());
        }
    }

    public EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> getDoseResponseData() {
        return doseResponseData;
    }
}
