package de.tum.bgu.msm.syntheticPopulationGenerator.germany;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.GeoDataReaderMuc;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipModelMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.DwellingWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.HouseholdWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.PersonWriterMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


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
        HouseholdWriter hhwriter = new HouseholdWriterMucMito(dataContainer.getHouseholdDataManager(), dataContainer.getRealEstateDataManager());
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
        DwellingWriter ddwriter = new DwellingWriterMucMito(dataContainer.getHouseholdDataManager(), dataContainer.getRealEstateDataManager());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + PropertiesSynPop.get().main.jobsStateFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        JobWriter jjwriter = new JobWriterMuc(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);

        if (PropertiesSynPop.get().main.populationSplitting){
            writeMultipleFilesForHouseholdsAndPersons(dataContainer);
        }
    }


    private void writeMultipleFilesForHouseholdsAndPersons(DataContainerWithSchools dataContainer){

        Map<Integer, PrintWriter> householdWriter = new HashMap<>();
        Map<Integer, PrintWriter> personWriter = new HashMap<>();

        String outputFolder = properties.main.baseDirectory;

        for (int part = 0; part <= PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            String filehh = properties.main.baseDirectory
                    + PropertiesSynPop.get().main.householdsStateFileName
                    + "_subPop_" + part + "_"
                    + properties.main.baseYear
                    + ".csv";
            String filepp = properties.main.baseDirectory
                    + PropertiesSynPop.get().main.personsStateFileName
                    + "_subPop_" + part + "_"
                    + properties.main.baseYear
                    + ".csv";
            PrintWriter pwHousehold0 = SiloUtil.openFileForSequentialWriting(filehh, false);
            pwHousehold0.println("id,dwelling,zone,hhSize,autos");
            PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
            pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income");
            householdWriter.put(part, pwHousehold0);
            personWriter.put(part, pwp);
        }

        int hhCount = 1;
        int partCount = 0;

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        int numberOfHhSubpopulation = (int) (householdDataManager.getHouseholds().size() / PropertiesSynPop.get().main.numberOfSubpopulations);
        for (Household hh : householdDataManager.getHouseholds()) {
            if (hhCount < numberOfHhSubpopulation) {
                PrintWriter pwh = householdWriter.get(partCount);
                pwh.print(hh.getId());
                pwh.print(",");
                pwh.print(hh.getDwellingId());
                pwh.print(",");
                pwh.print(realEstateDataManager.getDwelling(hh.getDwellingId()).getZoneId());
                pwh.print(",");
                pwh.print(hh.getHhSize());
                pwh.print(",");
                pwh.println(hh.getAutos());
                householdWriter.put(partCount, pwh);
                for (Person pp : hh.getPersons().values()){
                    PrintWriter pwp = personWriter.get(partCount);
                    pwp.print(pp.getId());
                    pwp.print(",");
                    pwp.print(pp.getHousehold().getId());
                    pwp.print(",");
                    pwp.print(pp.getAge());
                    pwp.print(",");
                    pwp.print(pp.getGender().getCode());
                    pwp.print(",");
                    pwp.print(pp.getOccupation().getCode());
                    pwp.print(",");
                    pwp.print(pp.hasDriverLicense());
                    pwp.print(",");
                    pwp.print(pp.getJobId());
                    pwp.print(",");
                    pwp.print(pp.getAnnualIncome());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("jobType").get().toString());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("disability").get().toString());
                    pwp.print(",");
                    pwp.print(0);
                    pwp.print(",");
                    pwp.print(pp.getAttribute("schoolType").get().toString());
                    pwp.print(",");
                    pwp.print(((PersonMuc)pp).getSchoolPlace());
                    pwp.println();
                    personWriter.put(partCount, pwp);
                }
            } else {
                hhCount = 1;
                partCount++;
            }
            hhCount++;
        }
        for (int part = 0; part <= PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            householdWriter.get(part).close();
            personWriter.get(part).close();
        }

    }

}
