package de.tum.bgu.msm.syntheticPopulationGenerator.germany;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
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
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.ReadPopulation;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.*;


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

    public void readAndSplit(String state){
        //method to create the synthetic population at the base year

        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        GeoDataReader reader = new GeoDataReaderMuc(dataContainer.getGeoData());
        String fileName = "input/syntheticPopulation/zoneSystem.csv";
        reader.readZoneCsv(fileName);

        long startTime = System.nanoTime();

        new ReadPopulationByState(dataContainer, state).run();
        new WriteSubpopulationsByState(dataContainer, state).run();


        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population for state " + "state" + ". Elapsed time: " + estimatedTime);

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
        Map<Integer, PrintWriter> dwellingWriter = new HashMap<>();

        String outputFolder = properties.main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulations/" + PropertiesSynPop.get().main.state + "/";
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);

        ArrayList<Household> householdArrayList = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            householdArrayList.add(hh);
        }
        Collections.shuffle(householdArrayList);

        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            String filehh = outputFolder
                    + PropertiesSynPop.get().main.householdsFileName + part + "_"
                    + properties.main.baseYear
                    + ".csv";
            String filepp = outputFolder
                    + PropertiesSynPop.get().main.personsFileName + part + "_"
                    + properties.main.baseYear
                    + ".csv";
            String filedd = outputFolder
                    + PropertiesSynPop.get().main.dwellingsFileName + part + "_"
                    + properties.main.baseYear
                    + ".csv";
            PrintWriter pwHousehold0 = SiloUtil.openFileForSequentialWriting(filehh, false);
            pwHousehold0.println("id,dwelling,zone,hhSize,autos,state,originalId");
            PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
            pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income,state,originalId");
            PrintWriter pwDwelling0 = SiloUtil.openFileForSequentialWriting(filedd, false);
            pwDwelling0.println("id,hhId,zone,coordX,coordY,state,originalId");
            householdWriter.put(part, pwHousehold0);
            personWriter.put(part, pwp);
            dwellingWriter.put(part, pwDwelling0);
        }

        int hhCount = 1;
        int partCount = 0;

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        int numberOfHhSubpopulation = (int) (householdArrayList.size() / PropertiesSynPop.get().main.numberOfSubpopulations);
        int startingHouseholdId = 0;
        int startingPersonId = 0;
        for (Household hh : householdArrayList) {
            Dwelling dd = realEstateDataManager.getDwelling(hh.getDwellingId());
            if (hhCount <= numberOfHhSubpopulation) {
                PrintWriter pwh = householdWriter.get(partCount);
                pwh.print(hh.getId() + startingHouseholdId);
                pwh.print(",");
                pwh.print(hh.getDwellingId() + startingHouseholdId);
                pwh.print(",");
                pwh.print(dd.getZoneId());
                pwh.print(",");
                pwh.print(hh.getHhSize());
                pwh.print(",");
                pwh.print(hh.getAutos());
                pwh.print(",");
                pwh.print(hh.getAttribute("state").get().toString());
                pwh.print(",");
                pwh.println(hh.getAttribute("originalId").get().toString());
                householdWriter.put(partCount, pwh);
                for (Person pp : hh.getPersons().values()){
                    PrintWriter pwp = personWriter.get(partCount);
                    pwp.print(pp.getId() + startingPersonId);
                    pwp.print(",");
                    pwp.print(pp.getHousehold().getId() + startingHouseholdId);
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
                    pwp.print(",");
                    pwp.print(hh.getAttribute("state").get().toString());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("originalId").get().toString());
                    pwp.println();
                    personWriter.put(partCount, pwp);
                }
                PrintWriter pwd = dwellingWriter.get(partCount);
                pwd.print(dd.getId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getResidentId() + startingPersonId);
                pwd.print(",");
                pwd.print(dd.getZoneId());
                pwd.print(",");
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
                pwd.print(",");
                pwd.print(hh.getAttribute("state").get().toString());
                pwd.print(",");
                pwd.println(dd.getAttribute("originalId").get().toString());
                householdDataManager.removeHousehold(hh.getId());
                realEstateDataManager.removeDwelling(dd.getId());
            } else {
                hhCount = 1;
                partCount++;
                if (partCount > PropertiesSynPop.get().main.numberOfSubpopulations - 1){
                    partCount = PropertiesSynPop.get().main.numberOfSubpopulations - 1;
                }
            }
            hhCount++;
        }
        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            householdWriter.get(part).close();
            personWriter.get(part).close();
            dwellingWriter.get(part).close();
        }

    }

}
