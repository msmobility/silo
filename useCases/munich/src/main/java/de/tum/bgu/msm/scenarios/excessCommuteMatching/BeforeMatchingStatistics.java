package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeforeMatchingStatistics {

    public static void main(String[] args) {

        String path = "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/siloMuc.properties";

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);
        TravelTimeUtil.updateCarSkim((SkimTravelTimes) dataContainer.getTravelTimes(), 2011, properties);


        final Map<String, List<Person>> collect = dataContainer.getHouseholdDataManager().getPersons().stream().filter(p -> p.getJobId() > 0).collect(Collectors.groupingBy(p -> {
            final Job jobFromId = dataContainer.getJobDataManager().getJobFromId(p.getJobId());
            return jobFromId.getType();

        }));

        for(Map.Entry<String, List<Person>> entry: collect.entrySet()) {

            final IntSummaryStatistics summary = entry.getValue().stream().mapToInt(p -> {
                final Job jobFromId = dataContainer.getJobDataManager().getJobFromId(p.getJobId());
                Zone from = dataContainer.getGeoData().getZones().get(dataContainer.getRealEstateDataManager().getDwelling(p.getHousehold().getDwellingId()).getZoneId());
                Zone to = dataContainer.getGeoData().getZones().get(jobFromId.getZoneId());
                return (int) dataContainer.getTravelTimes().getTravelTime(from, to, 28800, "car");

            }).summaryStatistics();
            System.out.println("Sector: " + entry.getKey());
            System.out.println("avg: " + summary.getAverage());
            System.out.println("min: " + summary.getMin());
            System.out.println("max: " + summary.getMax());
        }

    }
}
