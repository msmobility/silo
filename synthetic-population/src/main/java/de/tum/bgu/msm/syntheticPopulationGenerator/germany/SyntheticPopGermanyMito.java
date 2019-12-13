package de.tum.bgu.msm.syntheticPopulationGenerator.germany;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.io.GeoDataReaderMuc;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipModelMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.disability.DisabilityBase;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.DwellingWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.HouseholdWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.PersonWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;


/**
 * Generates a synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopGermanyMito implements SyntheticPopI {

    public static final Logger logger = Logger.getLogger(SyntheticPopGermanyMito.class);
    private final DataSetSynPop dataSetSynPop;
    private Properties properties;

    public SyntheticPopGermanyMito(DataSetSynPop dataSetSynPop, Properties properties) {
        this.dataSetSynPop = dataSetSynPop;
        this.properties = properties;
    }


    public void runSP(){
        //method to create the synthetic population at the base year

        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();

            DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
            GeoDataReader reader = new GeoDataReaderMuc(dataContainer.getGeoData());
            String fileName = PropertiesSynPop.get().main.zoneSystemFileName;
            reader.readZoneCsv(fileName);

            long startTime = System.nanoTime();

            logger.info("Running Module: Reading inputs");
            new Preparation(dataSetSynPop).run();

            logger.info("Running Module: Optimization IPU");
            new Optimization(dataSetSynPop).run();

            logger.info("Running Module: Allocation");
            new Allocation(dataSetSynPop, dataContainer).run();

           logger.info("Running Module: Car ownership");
            new CreateCarOwnershipModelMuc(dataContainer).run();


/*            logger.info("Running Module: Disability");
            new DisabilityBase(dataSetSynPop, dataContainer).run();*/

            logger.info("Summary of the synthetic population");
            summarizeMitoData(dataContainer);

            long estimatedTime = System.nanoTime() - startTime;
            logger.info("   Finished creating the synthetic population for state " + "state" + ". Elapsed time: " + estimatedTime);

    }

    private void createDirectoryForOutput() {
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/" +  PropertiesSynPop.get().main.state);
        SiloUtil.createDirectoryIfNotExistingYet("microData/" +  PropertiesSynPop.get().main.state + "/interimFiles");
    }


    private void summarizeMitoData(DataContainerWithSchools dataContainer){

        String filehh = properties.main.baseDirectory
                + PropertiesSynPop.get().main.householdsStateFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriter hhwriter = new HouseholdWriterMucMito(dataContainer.getHouseholdDataManager());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + PropertiesSynPop.get().main.personsStateFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        PersonWriter ppwriter = new PersonWriterMucMito(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + PropertiesSynPop.get().main.dwellingsStateFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriter ddwriter = new DwellingWriterMucMito(dataContainer.getHouseholdDataManager());
        ddwriter.writeHouseholds(filedd);

/*        String filejj = properties.main.baseDirectory
                + PropertiesSynPop.get().main.state + "/"
                + PropertiesSynPop.get().main.personsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        JobWriter jjwriter = new JobWriterMuc(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);*/

    }

}
