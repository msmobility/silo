package de.tum.bgu.msm.syntheticPopulationGenerator.manchester;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.microlocation.Microlocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.Optimization;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Generates a synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopMCR implements SyntheticPopI {

    public static final Logger logger = LogManager.getLogger(SyntheticPopMCR.class);
    private final DataSetSynPop dataSetSynPop;
    private Properties properties;

    public SyntheticPopMCR(DataSetSynPop dataSetSynPop, Properties properties) {
        this.dataSetSynPop = dataSetSynPop;
        this.properties = properties;
    }


    public void runSP(){
        //method to create the synthetic population at the base year

        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
//        GeoDataReader reader =  new GeoDataReaderManchester(dataContainer.getGeoData());
//        String pathShp = PropertiesSynPop.get().main.zoneShapeFile;
//        String fileName = PropertiesSynPop.get().main.zoneFilename;
//        reader.readZoneCsv(fileName);
//        reader.readZoneShapefile(pathShp);
        long startTime = System.nanoTime();

        logger.info("Running Module: Reading inputs");
        new Preparation(dataSetSynPop).run();

        logger.info("Running Module: Optimization IPU");
        new Optimization(dataSetSynPop).run();

        logger.info("Running Module: Allocation");
        new Allocation(dataSetSynPop, dataContainer).run();

        logger.info("Running Module: Microlocation");
        new Microlocation(dataSetSynPop,dataContainer).run();

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
                + ".csv";
        HouseholdWriter hhwriter = new HouseholdWriterMCR(dataContainer.getHouseholdDataManager(),dataContainer.getRealEstateDataManager());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        PersonWriter ppwriter = new PersonWriterMCR(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        DwellingWriter ddwriter = new DwellingWriterMCR(dataContainer);
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        JobWriter jjwriter = new JobWriterMCR(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);


        String fileee = properties.main.baseDirectory
                + properties.schoolData.schoolsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        SchoolsWriter eewriter = new SchoolsWriter(dataContainer.getSchoolData());
        eewriter.writeSchools(fileee);

    }
}
