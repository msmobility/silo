package de.tum.bgu.msm.syntheticPopulationGenerator.germany;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.GeoDataReaderMuc;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.io.output.DwellingWriter;
import de.tum.bgu.msm.io.output.HouseholdWriter;
import de.tum.bgu.msm.io.output.JobWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.ReadZonalData;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Generates a synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopGermanyMitoByState implements SyntheticPopI {

    public static final Logger logger = Logger.getLogger(SyntheticPopGermanyMitoByState.class);
    private final DataSetSynPop dataSetSynPop;
    private Properties properties;

    public SyntheticPopGermanyMitoByState(DataSetSynPop dataSetSynPop, Properties properties) {
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
        new ReadZonalData(dataSetSynPop).run();

        if (PropertiesSynPop.get().main.readMergeAndSplit) { //
            new ReadSubPopulations(dataContainer, false, 0).run();
            for (String state : PropertiesSynPop.get().main.states) {
                readAndSplit(state);
            }
        } else {
            if (PropertiesSynPop.get().main.runBySubpopulation) {
                new GenerateJobCounters(dataContainer, dataSetSynPop).run();
                new GenerateSchoolMicrolocations(dataContainer, dataSetSynPop).run();
                dataSetSynPop.setNextVacantJobId(PropertiesSynPop.get().main.firstVacantJob);
                for (int subPopulation = 0; subPopulation < PropertiesSynPop.get().main.numberOfSubpopulations; subPopulation++) {
                    if (PropertiesSynPop.get().main.runJobAllocation) {
                        new ReadSubPopulations(dataContainer, true, subPopulation).run();
                        new Read2011JobsForMicrolocation(dataContainer, dataSetSynPop, subPopulation).run();
                        new AssignJobsBySubpopulation(dataContainer, dataSetSynPop).run();
                        new GenerateJobsBySubpopulation(dataContainer, dataSetSynPop).run();
                        new GenerateVacantJobs(dataContainer, dataSetSynPop).run();
                        new AssignSchoolsBySubpopulation(dataContainer, dataSetSynPop).run();
                        new ValidateTripLengthDistributionByState(dataContainer, dataSetSynPop, subPopulation).run();
                        summarizeMitoData(dataContainer, subPopulation);
                        removeHouseholds(dataContainer);
                    } else {
                        new ReadSubPopulations(dataContainer, true, subPopulation).run();
                        summarizeMitoData(dataContainer, subPopulation);
                        removeHouseholds(dataContainer);
                    }

                }
            } else {
                new ReadSubPopulations(dataContainer, true, 0).run();
                writesubsample(dataContainer, 20);
            }
        }


            long estimatedTime = System.nanoTime() - startTime;
            logger.info("   Finished creating the synthetic population for state " + "state" + ". Elapsed time: " + estimatedTime);

    }


    private void removeHouseholds(DataContainerWithSchools dataContainer){
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for( Household hh : householdDataManager.getHouseholds()){
            int dwellingId = hh.getDwellingId();
            for (Person person : hh.getPersons().values()) {
                person.setHousehold(null);
                householdDataManager.removePersonFromHousehold(person);
                householdDataManager.removePerson(person.getId());
                Job job = jobDataManager.getJobFromId(person.getJobId());
                if (job != null){
                    jobDataManager.removeJob(job.getId());
                }
            }
            householdDataManager.removeHousehold(hh.getId());
            realEstateDataManager.removeDwellingFromVacancyList(dwellingId);
            realEstateDataManager.removeDwelling(dwellingId);
        }
    }


    private void createDirectoryForOutput() {
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/" +  PropertiesSynPop.get().main.state);
        SiloUtil.createDirectoryIfNotExistingYet("microData/" +  PropertiesSynPop.get().main.state + "/interimFiles");
    }

    private void readAndSplit(String state){
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

    private void summarizeMitoData(DataContainerWithSchools dataContainer, int subPopulation){

        String outputFolder = properties.main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulationsWithJobsAndSchool/" ;
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);

        String filehh = outputFolder
                + PropertiesSynPop.get().main.householdsFileName
                + subPopulation + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriter hhwriter = new HouseholdWriterMucMito(dataContainer.getHouseholdDataManager(), dataContainer.getRealEstateDataManager());
        hhwriter.writeHouseholds(filehh);

        String filepp = outputFolder
                + PropertiesSynPop.get().main.personsFileName
                + subPopulation + "_"
                + properties.main.baseYear
                + ".csv";
        String filejj = outputFolder
                + PropertiesSynPop.get().main.jobsFileName
                + subPopulation + "_"
                + properties.main.baseYear
                + ".csv";
        PersonJobWriterMucMito ppwriter = new PersonJobWriterMucMito(dataContainer.getHouseholdDataManager(),
                dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(), dataContainer.getSchoolData());
        ppwriter.writePersonsWithJob(filepp, filejj);

        String filehhForShortDistance = outputFolder
                + PropertiesSynPop.get().main.householdsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriterMucMito hhwriterSD = new HouseholdWriterMucMito(dataContainer.getHouseholdDataManager(), dataContainer.getRealEstateDataManager());
        hhwriterSD.writeHouseholdsWithCoordinates(filehhForShortDistance, subPopulation);

        String fileppForShortDistance = outputFolder
                + PropertiesSynPop.get().main.personsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";

        PersonJobWriterMucMito ppwriterSD = new PersonJobWriterMucMito(dataContainer.getHouseholdDataManager(),
                dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(), dataContainer.getSchoolData());
        ppwriterSD.writePersonsWithJobAttributes(fileppForShortDistance, subPopulation);

        String filedd = outputFolder
                + PropertiesSynPop.get().main.dwellingsFileName
                + subPopulation + "_"
                + properties.main.baseYear
                + ".csv";
        DwellingWriter ddwriter = new DwellingWriterMucMito(dataContainer.getHouseholdDataManager(), dataContainer.getRealEstateDataManager());
        ddwriter.writeDwellings(filedd);

        String fileedu = outputFolder
                + "ee"
                + subPopulation + "_"
                + properties.main.baseYear
                + ".csv";
        SchoolsWriter eduwriter = new SchoolsWriter(dataContainer.getSchoolData());
        eduwriter.writeSchools(fileedu);

        if (PropertiesSynPop.get().main.populationSplitting){
            writeMultipleFilesForHouseholdsAndPersons(dataContainer);
        }
    }

    private void summarizeJobs(DataContainerWithSchools dataContainer){

        String outputFolder = properties.main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulationsWithJobs/" ;
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);

        String filejj = outputFolder
                + PropertiesSynPop.get().main.jobsFileName
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


    private void writesubsample(DataContainerWithSchools dataContainer, int samplingRate){

        Map<Integer, PrintWriter> householdWriter = new HashMap<>();
        Map<Integer, PrintWriter> personWriter = new HashMap<>();
        Map<Integer, PrintWriter> dwellingWriter = new HashMap<>();

        String outputFolder = properties.main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulations00/" ;
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);

        ArrayList<Household> householdArrayList = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            householdArrayList.add(hh);
        }
        Collections.shuffle(householdArrayList);

        for (int part = 0; part < 1; part++) {
            String filehh = outputFolder
                    + PropertiesSynPop.get().main.householdsFileName + part + "_"
                    + properties.main.baseYear
                    + "_1.csv";
            String filepp = outputFolder
                    + PropertiesSynPop.get().main.personsFileName + part + "_"
                    + properties.main.baseYear
                    + "_1.csv";
            String filedd = outputFolder
                    + PropertiesSynPop.get().main.dwellingsFileName + part + "_"
                    + properties.main.baseYear
                    + "_1.csv";
            PrintWriter pwHousehold0 = SiloUtil.openFileForSequentialWriting(filehh, false);
            pwHousehold0.println("id,dwelling,zone,hhSize,autos,state,originalId");
            PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
            pwp.println("id,hhid,age,gender,occupation,driversLicense,workplace,income,jobType,disability,schoolId,schoolType,schoolPlace,state,originalId,workZone");
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

        int numberOfHhSubpopulation = (int) (householdArrayList.size() * samplingRate / 100);
        for (Household hh : householdArrayList) {
            Dwelling dd = realEstateDataManager.getDwelling(hh.getDwellingId());
            if (hhCount <= numberOfHhSubpopulation) {
                PrintWriter pwh = householdWriter.get(partCount);
                pwh.print(hh.getId());
                pwh.print(",");
                pwh.print(hh.getDwellingId());
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
                    pwp.print(",");
                    pwp.print(hh.getAttribute("state").get().toString());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("originalId").get().toString());
                    pwp.print(",");
                    pwp.print(0);
                    pwp.println();
                    personWriter.put(partCount, pwp);
                }
                PrintWriter pwd = dwellingWriter.get(partCount);
                pwd.print(dd.getId());
                pwd.print(",");
                pwd.print(dd.getResidentId());
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
            } else {
                //hhCount = 1;
                //partCount++;
                //if (partCount > PropertiesSynPop.get().main.numberOfSubpopulations - 1){
                //    partCount = PropertiesSynPop.get().main.numberOfSubpopulations - 1;
                //}
            }
            hhCount++;
        }
        for (int part = 0; part < 1; part++) {
            householdWriter.get(part).close();
            personWriter.get(part).close();
            dwellingWriter.get(part).close();
        }

    }

}
