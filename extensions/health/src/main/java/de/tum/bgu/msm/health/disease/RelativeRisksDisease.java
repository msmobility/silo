package de.tum.bgu.msm.health.disease;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.PersonHealth;

import java.util.EnumMap;

// Dose-response functions for health exposures (simple for now but will become more complex)
public class RelativeRisksDisease {

    public static EnumMap<Diseases, Float> calculateForPA(PersonHealth personHealth, DataContainerHealth dataContainer) {

        double total_mmet = Math.min(17.5, personHealth.getWeeklyMarginalMetHours(Mode.walk) +
                personHealth.getWeeklyMarginalMetHours(Mode.bicycle) +
                personHealth.getWeeklyMarginalMetHoursSport());

        EnumMap<Diseases, Float> relativeRisksByDisease = new EnumMap<>(Diseases.class);

        for(Diseases diseases : dataContainer.getDoseResponseData().get(HealthExposures.PHYSICAL_ACTIVITY).keySet()){
            TableDataSet doseResponseTable = dataContainer.getDoseResponseData().get(HealthExposures.PHYSICAL_ACTIVITY).get(diseases);
            if(doseResponseTable==null){
                System.out.println("No dose response data for PA, disease: " + diseases.name());
                continue;
            }
            double rr = LinearInterpolation.interpolate(doseResponseTable.getColumnAsDouble("dose"),
                    doseResponseTable.getColumnAsDouble("RR"), total_mmet);
            relativeRisksByDisease.put(diseases, (float) rr);
        }


        return relativeRisksByDisease;
    }

    public static EnumMap<Diseases, Float> calculateForPM25(PersonHealth personMRC, DataContainerHealth dataContainer) {
        double total_pm25 = personMRC.getWeeklyExposureByPollutantNormalised("pm2.5");
        EnumMap<Diseases, Float> relativeRisksByDisease = new EnumMap<>(Diseases.class);

        for(Diseases diseases : dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_PM25).keySet()){
            double rr = LinearInterpolation.interpolate(dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_PM25).get(diseases).getColumnAsDouble("dose"),
                    dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_PM25).get(diseases).getColumnAsDouble("RR"), total_pm25);
            relativeRisksByDisease.put(diseases, (float) rr);
        }

        return relativeRisksByDisease;
    }

    public static EnumMap<Diseases, Float> calculateForNO2(PersonHealth personMRC, DataContainerHealth dataContainer) {
        double total_pm25 = personMRC.getWeeklyExposureByPollutantNormalised("no2");
        EnumMap<Diseases, Float> relativeRisksByDisease = new EnumMap<>(Diseases.class);

        for(Diseases diseases : dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_NO2).keySet()){
            double rr = LinearInterpolation.interpolate(dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_NO2).get(diseases).getColumnAsDouble("dose"),
                    dataContainer.getDoseResponseData().get(HealthExposures.AIR_POLLUTION_NO2).get(diseases).getColumnAsDouble("RR"), total_pm25);
            relativeRisksByDisease.put(diseases, (float) rr);
        }

        return relativeRisksByDisease;
    }



}
