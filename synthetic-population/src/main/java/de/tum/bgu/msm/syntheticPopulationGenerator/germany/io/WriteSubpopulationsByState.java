package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
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
public class WriteSubpopulationsByState {

    public static final Logger logger = Logger.getLogger(WriteSubpopulationsByState.class);
    private final DataContainerWithSchools dataContainer;
    private String outputFolder;
    private final String state;

    public WriteSubpopulationsByState(DataContainerWithSchools dataContainer, String state) {
        this.dataContainer = dataContainer;
        this.state = state;
    }


    public void run(){
        //method to write the subpopulations
        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();
        writeMultipleFilesForHouseholdsAndPersons(dataContainer);
        writeOnePopulationWithPercentSampling(dataContainer, 1);
    }

    private void createDirectoryForOutput() {
        outputFolder = Properties.get().main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulations/" ;
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);
    }


    private void writeMultipleFilesForHouseholdsAndPersons(DataContainerWithSchools dataContainer){

        Map<Integer, PrintWriter> householdWriter = new HashMap<>();
        Map<Integer, PrintWriter> personWriter = new HashMap<>();
        Map<Integer, PrintWriter> dwellingWriter = new HashMap<>();

        ArrayList<Household> householdArrayList = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            householdArrayList.add(hh);
        }
        Collections.shuffle(householdArrayList);

        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            String filehh = outputFolder
                    + PropertiesSynPop.get().main.householdsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            String filepp = outputFolder
                    + PropertiesSynPop.get().main.personsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            String filedd = outputFolder
                    + PropertiesSynPop.get().main.dwellingsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            PrintWriter pwHousehold0 = SiloUtil.openFileForSequentialWriting(filehh, true);
            PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, true);
            PrintWriter pwDwelling0 = SiloUtil.openFileForSequentialWriting(filedd, true);
            if (state.equals(PropertiesSynPop.get().main.states[0])) {
                pwHousehold0.println("id,dwelling,zone,hhSize,autos,state,originalId");
                pwp.println("id,hhid,age,gender,occupation,driversLicense,workplace,income,jobType,disability,schoolId,schoolType,schoolPlace,state,originalId");
                pwDwelling0.println("id,hhId,zone,coordX,coordY,state,originalId");
            }
            householdWriter.put(part, pwHousehold0);
            personWriter.put(part, pwp);
            dwellingWriter.put(part, pwDwelling0);
        }

        int hhCount = 1;
        int partCount = 0;

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        int numberOfHhSubpopulation = (int) (householdArrayList.size() / PropertiesSynPop.get().main.numberOfSubpopulations);
        int startingHouseholdId = (int) PropertiesSynPop.get().main.counters.getStringIndexedValueAt(state, "startingHhId");
        int startingPersonId = (int) PropertiesSynPop.get().main.counters.getStringIndexedValueAt(state, "startingPpId");
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
                pwh.print(state);
                pwh.print(",");
                pwh.println(hh.getId());
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
                    pwp.print(state);
                    pwp.print(",");
                    pwp.print(pp.getId());
                    pwp.println();
                    personWriter.put(partCount, pwp);
                }
                PrintWriter pwd = dwellingWriter.get(partCount);
                pwd.print(dd.getId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getResidentId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getZoneId());
                pwd.print(",");
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
                pwd.print(",");
                pwd.print(state);
                pwd.print(",");
                pwd.println(dd.getId());
            } else {
                hhCount = 1;
                partCount++;
                if (partCount > PropertiesSynPop.get().main.numberOfSubpopulations - 1){
                    partCount = PropertiesSynPop.get().main.numberOfSubpopulations - 1;
                }
            }
            householdDataManager.removeHousehold(hh.getId());
            realEstateDataManager.removeDwelling(dd.getId());
            hhCount++;
        }
        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            householdWriter.get(part).close();
            personWriter.get(part).close();
            dwellingWriter.get(part).close();
        }

    }

    private void writeOnePopulationWithPercentSampling(DataContainerWithSchools dataContainer, int percentSampling){


        ArrayList<Household> householdArrayList = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            householdArrayList.add(hh);
        }
        Collections.shuffle(householdArrayList);
        String outputFolder = Properties.get().main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulations00/" ;
        String filehh = outputFolder
                + PropertiesSynPop.get().main.householdsFileName + "_1_"
                + Properties.get().main.baseYear
                + ".csv";
        String filepp = outputFolder
                + PropertiesSynPop.get().main.personsFileName + "_1_"
                + Properties.get().main.baseYear
                + ".csv";
        String filedd = outputFolder
                + PropertiesSynPop.get().main.dwellingsFileName + "_1_"
                + Properties.get().main.baseYear
                + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, true);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, true);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, true);
        if (state.equals(PropertiesSynPop.get().main.states[0])) {
            pwh.println("id,dwelling,zone,hhSize,autos,state,originalId");
            pwp.println("id,hhid,age,gender,occupation,driversLicense,workplace,income,jobType,disability,schoolId,schoolType,state,originalId");
            pwd.println("id,hhId,zone,coordX,coordY,state,originalId");
        }


        int hhCount = 1;

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        int numberOfHhSubpopulation = (int) (householdArrayList.size() * percentSampling / 100);
        int startingHouseholdId = (int) PropertiesSynPop.get().main.counters.getStringIndexedValueAt(state, "startingHhId");
        int startingPersonId = (int) PropertiesSynPop.get().main.counters.getStringIndexedValueAt(state, "startingPpId");
        for (Household hh : householdArrayList) {
            Dwelling dd = realEstateDataManager.getDwelling(hh.getDwellingId());
            if (hhCount <= numberOfHhSubpopulation) {
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
                pwh.print(state);
                pwh.print(",");
                pwh.println(hh.getId());
                for (Person pp : hh.getPersons().values()){
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
                    pwp.print(state);
                    pwp.print(",");
                    pwp.print(pp.getId());
                    pwp.println();
                }
                pwd.print(dd.getId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getResidentId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getZoneId());
                pwd.print(",");
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
                pwd.print(",");
                pwd.print(state);
                pwd.print(",");
                pwd.println(dd.getId());
            }
            householdDataManager.removeHousehold(hh.getId());
            realEstateDataManager.removeDwelling(dd.getId());
            hhCount++;
        }
        pwh.close();
        pwp.close();
        pwd.close();
    }

}
