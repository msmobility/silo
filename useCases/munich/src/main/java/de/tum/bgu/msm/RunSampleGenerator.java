package de.tum.bgu.msm;

import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.DataContainerWithSchoolsImpl;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.output.DefaultHouseholdWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RunSampleGenerator {

    public static void main(String[] args) {

        final Properties properties = SiloUtil.siloInitialization(args[0]);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        final Collection<Zone> zones = dataContainer.getGeoData().getRegions().get(174).getZones();
        final Set<Integer> zoneIds = zones.stream().map(Location::getZoneId).collect(Collectors.toSet());

        SampleGenerator.generateSampleForZones(dataContainer, zoneIds);


//        final String carSkimFile = Properties.get().accessibility.autoSkimFile(Properties.get().main.startYear);
//
//        final IndexedDoubleMatrix2D matrix = AbstractOmxReader.readAndConvertToDoubleMatrix(carSkimFile, Properties.get().accessibility.autoPeakSkim, 1.);
//        final IndexedDoubleMatrix2D newMatrix = new IndexedDoubleMatrix2D(zones, zones);
//        for(int zoneId: zoneIds) {
//            for(int zoneId2: zoneIds) {
//                final double indexed = matrix.getIndexed(zoneId, zoneId2);
//                newMatrix.setIndexed(zoneId, zoneId2, indexed);
//            }
//        }
//
//
//        OmxMatrixWriter.createOmxFile("C:/Users/Nico/Desktop/skimsAllIntrazonal.omx", newMatrix.columns());
//        OmxMatrixWriter.createOmxSkimMatrix(newMatrix, "C:/Users/Nico/Desktop/skimsAllIntrazonal.omx", Properties.get().accessibility.autoPeakSkim);
//
//        OmxMatrixWriter.createOmxSkimMatrix(newMatrix, "C:/Users/Nico/Desktop/skimsAllIntrazonal.omx", "mat1");
//        OmxMatrixWriter.createOmxSkimMatrix(newMatrix, "C:/Users/Nico/Desktop/skimsAllIntrazonal.omx", "distanceByTime");
//        OmxMatrixWriter.createOmxSkimMatrix(newMatrix, "C:/Users/Nico/Desktop/skimsAllIntrazonal.omx", "distanceByDistance");
//


        new DwellingWriterMuc(dataContainer).writeDwellings("C:/Users/Qin/Desktop/dd_2011.csv");
        new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager().getHouseholds()).writeHouseholds("C:/Users/Qin/Desktop/hh_2011.csv");
        new PersonWriterMuc(dataContainer.getHouseholdDataManager()).writePersons("C:/Users/Qin/Desktop/pp_2011.csv");
        new JobWriterMuc(dataContainer.getJobDataManager()).writeJobs("C:/Users/Qin/Desktop/jj_2011.csv");
        new SchoolsWriter(dataContainer.getSchoolData()).writeSchools("C:/Users/Qin/Desktop/ss_2011.csv");

    }
}
