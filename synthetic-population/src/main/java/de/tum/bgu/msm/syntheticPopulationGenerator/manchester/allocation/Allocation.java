package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.allocation;


import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.io.DwellingWriterMCR;
import de.tum.bgu.msm.io.HouseholdWriterMCR;
import de.tum.bgu.msm.io.JobWriterMCR;
import de.tum.bgu.msm.io.PersonWriterMCR;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Allocation extends ModuleSynPop{

    private static final Logger logger = LogManager.getLogger(Allocation.class);
    private final  DataContainerWithSchools dataContainer;
    private HashMap<Person, Integer> educationalLevel;

    public Allocation(DataSetSynPop dataSetSynPop, DataContainerWithSchools dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
    }

    @Override
    public void run(){
        logger.info("   Started allocation model.");
        if (PropertiesSynPop.get().main.runAllocation) {
            generateHouseholdsPersonsDwellings();
            summarizeData(dataContainer, "afterGenerateHHppDD");
            generateVacantDwellings();
            summarizeData(dataContainer, "afterGenerateVacant");
            generateJobs();
            summarizeData(dataContainer, "afterGenerateJobs");
            //TODO:Shall we use the hhAuto from micro data?
            generateAutos();
            summarizeData(dataContainer, "afterGenerateAutos");
        } else {
            readPopulation();
        }

        if (PropertiesSynPop.get().main.runJobAllocation) {
            assignJobs();
            summarizeData(dataContainer, "afterAssignJobs");
            assignSchools();
            summarizeData(dataContainer, "afterAssignSchools");
            validateTripLengths();
        }
        logger.info("   Completed allocation model.");

    }

    public void generateHouseholdsPersonsDwellings(){
        educationalLevel = new HashMap<>();
        new GenerateHouseholdsPersonsDwellings(dataContainer, dataSetSynPop, educationalLevel).run();
    }

    public void generateJobs(){
        new GenerateJobs(dataContainer, dataSetSynPop).run();
    }

    public void assignJobs(){
        new AssignJobs(dataContainer, dataSetSynPop, educationalLevel).run();
    }

    public void assignSchools(){
        new AssignSchools(dataContainer, dataSetSynPop).run();
    }

    public void generateAutos() {new CarOwnership(dataContainer, dataSetSynPop).run();}

    public void readPopulation(){
        educationalLevel = new HashMap<>();
        new ReadPopulation(dataContainer, educationalLevel).run();
    }

    public void validateTripLengths(){
        new ValidateTripLengthDistribution(dataContainer, dataSetSynPop).run();
    }


    public void addBoroughsAsCities(int county){
        //Add to the municipality list the boroughs, because they have weights as well
        //Only if the option of running IPU with three areas is true
        ArrayList<Integer> newCities = new ArrayList<>();
        for (int city : dataSetSynPop.getMunicipalities()){
            if (!dataSetSynPop.getMunicipalitiesByCounty().get(county).contains(city)){
                newCities.add(city);
            }
        }
        newCities.addAll(dataSetSynPop.getBoroughsByCounty().get(county));
        dataSetSynPop.setMunicipalities(newCities);
    }

    public void removeBoroughsAsCities(int county){
        //Remove to the list of boroughs at the municipality, because they have weights for household generation but are not on the job allocation explicitly
        //Only if the option of running IPU with three areas is true
        ArrayList<Integer> newCities = dataSetSynPop.getMunicipalitiesByCounty().get(county);
        newCities.removeAll(dataSetSynPop.getBoroughsByCounty().get(county));
        dataSetSynPop.setMunicipalities(newCities);
    }

    private void generateVacantDwellings(){
        new GenerateVacantDwellings(dataContainer, dataSetSynPop).run();
    }

    private void summarizeData(DataContainerWithSchools dataContainer, String stage){

        String filehh = "microData/interimFiles/hh_" + stage + ".csv";
        HouseholdWriter hhwriter = new HouseholdWriterMCR(dataContainer.getHouseholdDataManager(),dataContainer.getRealEstateDataManager());
        hhwriter.writeHouseholds(filehh);

        String filepp = "microData/interimFiles/pp_" + stage + ".csv";
        PersonWriter ppwriter = new PersonWriterMCR(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = "microData/interimFiles/dd_" + stage + ".csv";
        DwellingWriter ddwriter = new DwellingWriterMCR(dataContainer);
        ddwriter.writeDwellings(filedd);

        String filejj = "microData/interimFiles/jj_" + stage + ".csv";
        JobWriter jjwriter = new JobWriterMCR(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);

    }
}
