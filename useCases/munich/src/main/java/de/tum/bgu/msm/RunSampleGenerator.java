package de.tum.bgu.msm;

import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.io.DwellingWriterMuc;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.SchoolsWriter;
import de.tum.bgu.msm.io.output.DefaultHouseholdWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RunSampleGenerator {

    public static void main(String[] args) {

        final Properties properties = SiloUtil.siloInitialization(args[0]);
        DataContainerMuc dataContainer = DataBuilder.getModelDataForMuc(properties);
        DataBuilder.read(properties, dataContainer);

        final Collection<Zone> zones = dataContainer.getGeoData().getRegions().get(174).getZones();
        final Set<Integer> zoneIds = zones.stream().map(Location::getZoneId).collect(Collectors.toSet());

        SampleGenerator.generateSampleForZones(dataContainer, zoneIds);

        new DwellingWriterMuc(dataContainer).writeDwellings("C:/Users/Nico/Desktop/ddSample.csv");
        new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager()).writeHouseholds("C:/Users/Nico/Desktop/hhSample.csv");
        new PersonWriterMuc(dataContainer.getHouseholdDataManager()).writePersons("C:/Users/Nico/Desktop/ppSample.csv");
        new JobWriterMuc(dataContainer.getJobDataManager()).writeJobs("C:/Users/Nico/Desktop/jjSample.csv");
        new SchoolsWriter(dataContainer.getSchoolData()).writeSchools("C:/Users/Nico/Desktop/ssSample.csv");
    }
}
