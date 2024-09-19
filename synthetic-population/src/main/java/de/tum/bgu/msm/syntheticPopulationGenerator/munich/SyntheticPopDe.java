package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.io.GeoDataReaderMuc;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMucDisability;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipModelMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.disability.DisabilityBase;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation.Microlocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.Optimization;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;


/**
 * Generates a synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe implements SyntheticPopI {

    public static final Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private final DataSetSynPop dataSetSynPop;
    private Properties properties;

    public SyntheticPopDe(DataSetSynPop dataSetSynPop, Properties properties) {
        this.dataSetSynPop = dataSetSynPop;
        this.properties = properties;
    }


    public void runSP(){
        //method to create the synthetic population at the base year

        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        GeoDataReader reader = new GeoDataReaderMuc(dataContainer.getGeoData());
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        long startTime = System.nanoTime();

        logger.info("Running Module: Reading inputs");
        new Preparation(dataSetSynPop).run();

        logger.info("Running Module: Optimization IPU");
        new Optimization(dataSetSynPop).run();

        logger.info("Running Module: Allocation");
        new Allocation(dataSetSynPop, dataContainer).run();

        logger.info("Running Module: Microlocation");
        new Microlocation(dataSetSynPop,dataContainer).run();

        logger.info("Running Module: Car ownership");
        //new CreateCarOwnershipModelMuc(dataContainer).run();

        logger.info("Running Module: Disability");
        new DisabilityBase(dataSetSynPop, dataContainer).run();

        logger.info("Summary of the synthetic population");
        summarizeData(dataContainer);

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }

    private void createDirectoryForOutput() {
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }

    private void summarizeData(DataContainerWithSchools dataContainer){

        String filehh = properties.main.baseDirectory
                + properties.householdData.householdFileName
                + "_"
                + properties.main.baseYear
                + "P.csv";
        HouseholdWriter hhwriter = new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager().getHouseholds());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear
                + "P.csv";
        PersonWriter ppwriter = new PersonWriterMucDisability(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear
                + "P.csv";
        DwellingWriter ddwriter = new DefaultDwellingWriter(dataContainer.getRealEstateDataManager().getDwellings());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear
                + "P.csv";
        JobWriter jjwriter = new JobWriterMuc(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);


        String fileee = properties.main.baseDirectory
                + properties.schoolData.schoolsFileName
                + "_"
                + properties.main.baseYear
                + "P.csv";
        SchoolsWriter eewriter = new SchoolsWriter(dataContainer.getSchoolData());
        eewriter.writeSchools(fileee);

    }

}
