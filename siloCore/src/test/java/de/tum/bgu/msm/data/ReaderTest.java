package de.tum.bgu.msm.data;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.utils.SiloUtil;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class ReaderTest {

    @Test
    public void main(){
        testReading((int) 2);
        //testReading((int) 50);
        //testReading((int) 250);
        //testReading((int)100e6);
    }

    public void testReading(int times) {

        long startReader = System.nanoTime();
        String householdFile = "C:/models/silo/muc/muc/microData/hh_2011.csv";
        String personFile = "C:/models/silo/muc/muc/microData/pp_2011.csv";

        for (int i = 1; i < times; i++){
            System.out.println("Reading population. Iteration " + i);
            Map<Integer, Household> householdMap = readHouseholds(householdFile);
            readPersonData(personFile, householdMap);
        }
        long endReader = System.nanoTime();

        Map<Integer, Household> hhMap = readHouseholdFile(householdFile);
        Map<Integer, Person> ppMap = readPersonFile(personFile, hhMap);
        long startCopy = System.nanoTime();
        for (int i = 1; i < times; i++){
            System.out.println("Copying population. Iteration " + i);
            Map<Integer, Household> households = copyHouseholds(hhMap);
            Map<Integer, Person> persons = copyPersons(ppMap, households);

            //just for checking that the copy and the original can change
            /*System.out.println(" Original person 1 age: " + ppMap.get(1).getAge() + ". Copy person 1 age: " + persons.get(1).getAge());
            ppMap.get(1).birthday();
            System.out.println(" Original person 1 age: " + ppMap.get(1).getAge() + ". Copy person 1 age: " + persons.get(1).getAge());
            System.out.println(" Original person 1 role: " + ppMap.get(1).getRole() + ". Copy person 1 role: " + persons.get(1).getRole());
            ppMap.get(1).setRole(PersonRole.CHILD);
            System.out.println(" Original person 1 role: " + ppMap.get(1).getRole() + ". Copy person 1 role: " + persons.get(1).getRole());
            ppMap.get(1).setSchoolCoordinate(new Coordinate(1,1),2);
            System.out.println(" Original person 1 coordinate: " + ppMap.get(1).getSchoolLocation() + ". Copy person 1 coordinate: " + persons.get(1).getSchoolLocation());
            ppMap.get(1).setSchoolCoordinate(new Coordinate(0,0),2);
            System.out.println(" Original person 1 coordinate: " + ppMap.get(1).getSchoolLocation() + ". Copy person 1 coordinate: " + persons.get(1).getSchoolLocation());*/
        }
        long endCopy = System.nanoTime();



        Map<Integer, Household> hhMap1 = readHouseholdFile(householdFile);
        long startClone = System.nanoTime();
        for (int i = 1; i < times; i++){
            cloneHouseholds(hhMap1);
        }
        long endClone = System.nanoTime();

        //outputs
        double t1 = (endReader - startReader) / 1_000_000_000.0;
        double tJS = t1;
        double t3 = (startCopy - endReader) / 1_000_000_000.0;
        double t4 = (endCopy - startCopy) / 1_000_000_000.0;
        double tDist = t3 + t4;
/*        double t5 = (startClone - endCopy)/ 1_000_000_000.0;
        double t6 = (endClone - startClone)/ 1_000_000_000.0;
        double tDist2 = t5 + t6;*/

        System.out.println("Results for " + times + " iterations.");
        System.out.println("    Reading. RunTime: " + tJS + ". Setup: " + t1);
        System.out.println("    Copy afterwards. RunTime: " + tDist + ". Setup: " + t3 + " and execution: " + t4);
        //System.out.println("    Clone afterwards. RunTime: " + tDist2 + ". Setup: " + t5 + " and execution: " + t6);
    }

    private Map<Integer, Household> readHouseholds(String fileName){

        HouseholdFactory factory = HouseholdUtil.getFactory();
        Map<Integer, Household> households = new HashMap<>();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                Household hh = factory.createHousehold(id, -1, 0);  // this automatically puts it in id->household map in Household class
                households.put(hh.getId(), hh);
            }
        } catch (IOException e) {

        }
        return  households;
    }

    private Map<Integer, Household> readHouseholdFile(String fileName){

        HouseholdFactory factory = HouseholdUtil.getFactory();
        Map<Integer, Household> households = new HashMap<>();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                Household hh = factory.createHousehold(id, -1, 0);  // this automatically puts it in id->household map in Household class
                households.put(hh.getId(), hh);
            }
        } catch (IOException e) {

        }
        return households;
    }

    private Map<Integer, Household> copyHouseholds(Map<Integer, Household> householdsToCopy){

        HouseholdFactory factory = HouseholdUtil.getFactory();
        Map<Integer, Household> households = new HashMap<>();
        int recCount = 0;
        for (Household hhToCopy : householdsToCopy.values()){
            recCount++;
            int id = hhToCopy.getId();
            Household hh = factory.createHousehold(id, -1, 0);  // this automatically puts it in id->household map in Household class
            households.put(hh.getId(), hh);
        }
        return households;
    }

    private void cloneHouseholds(Map<Integer, Household> householdsToCopy){
        HouseholdFactory factory = HouseholdUtil.getFactory();
        Map<Integer, Household> households = new HashMap<>();
        int recCount = 0;
        for (Household hhToCopy : householdsToCopy.values()){
            recCount++;
            //Household hh1 = org.apache.commons.lang3.SerializationUtils.clone(hhToCopy);
            Household hh = factory.createHousehold(hhToCopy.getId(), -1, 0);  // this automatically puts it in id->household map in Household class
            households.put(hh.getId(), hh);
        }
    }

    private Map<Integer, Person> readPersonFile (String path, Map<Integer, Household> householdMap) {

        PersonFactory ppFactory = PersonUtils.getFactory();
        String recString = "";
        int recCount = 0;
        Implementation implementation = Implementation.MUNICH;
        Map<Integer, Person> persons = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhID",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posDriver = SiloUtil.findPositionInArray("driversLicense", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                Gender gender     = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                boolean license = Boolean.parseBoolean(lineElements[posDriver]);
                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);
                Household household = householdMap.get(hhid);
                if(household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                Person pp = ppFactory.createPerson(id, age, gender, race, occupation, PersonRole.SINGLE, workplace, income);
                pp.setRole(pr);
                pp.setDriverLicense(license);
                //TODO: remove it when we implement interface
                if(implementation == Implementation.MUNICH){
                    int posSchoolCoordX = SiloUtil.findPositionInArray("schoolCoordX", header);
                    int posSchoolCoordY = SiloUtil.findPositionInArray("schoolCoordY", header);
                    // TODO Currently only instance where we set a zone id to -1. nk/dz, jul'18
                    Coordinate schoolCoord = new Coordinate(
                            Double.parseDouble(lineElements[posSchoolCoordX]),Double.parseDouble(lineElements[posSchoolCoordY]));
                    pp.setSchoolCoordinate(schoolCoord, -1);
                }

                persons.put(id, pp);
                addPersonToHousehold(pp, household);
            }
        } catch (IOException e) {
        }
        return persons;
    }

    private Map<Integer, Person> copyPersons(Map<Integer, Person> personsToCopyMap, Map<Integer, Household> householdMap){
        PersonFactory ppFactory = PersonUtils.getFactory();
        Implementation implementation = Implementation.MUNICH;
        Map<Integer, Person> persons = new HashMap<>();
        int recCount = 0;
        for (Person ppToCopy : personsToCopyMap.values()){
            recCount++;
            int id = ppToCopy.getId();
            int hhid = ppToCopy.getHousehold().getId();
            int age = ppToCopy.getAge();
            Gender gender = ppToCopy.getGender();
            PersonRole pr  = ppToCopy.getRole();
            Race race = ppToCopy.getRace();
            Occupation occupation = ppToCopy.getOccupation();
            int workplace  = ppToCopy.getJobId();
            int income     = ppToCopy.getIncome();
            boolean license = ppToCopy.hasDriverLicense();
            //todo temporary assign driving license since this is not in the current SP version
            //boolean license = MicroDataManager.obtainLicense(gender, age);
            Household household = householdMap.get(hhid);
            if(household == null) {
                throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
            }
            Person pp = ppFactory.createPerson(id, age, gender, race, occupation, PersonRole.SINGLE, workplace, income);
            pp.setRole(pr);
            pp.setDriverLicense(license);
            //TODO: remove it when we implement interface
            if(implementation == Implementation.MUNICH){
                Coordinate schoolCoord = ppToCopy.getSchoolLocation();
                pp.setSchoolCoordinate(schoolCoord, -1);
            }
            persons.put(id, pp);
            addPersonToHousehold(pp, household);
        }
        return persons;
    }

    private void addPersonToHousehold(Person person, Household household) {
        if(household.getPersons().containsKey(person.getId())) {
            throw new IllegalArgumentException("Person " + person.getId() + " was already added to household " + household.getId());
        }
        household.addPerson(person);
        person.setHousehold(household);
        if (person.getId() == SiloUtil.trackPp || household.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("A person " +
                    "(not a child) named " + person.getId() + " was added to household " + household.getId() + ".");
        }
    }

    private void readPersonData(String path, Map<Integer, Household> householdMap) {

        Implementation implementation = Implementation.MUNICH;
        PersonFactory ppFactory = PersonUtils.getFactory();
        String recString = "";
        int recCount = 0;

        Map<Integer, Person> persons = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhID",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);
            int posDriver = SiloUtil.findPositionInArray("driversLicense", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                Gender gender     = Gender.valueOf(Integer.parseInt(lineElements[posGender]));
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                Occupation occupation = Occupation.valueOf(Integer.parseInt(lineElements[posOccupation]));
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                boolean license = Boolean.parseBoolean(lineElements[posDriver]);
                //todo temporary assign driving license since this is not in the current SP version
                //boolean license = MicroDataManager.obtainLicense(gender, age);
                Household household = householdMap.get(hhid);
                if(household == null) {
                    throw new RuntimeException("Person " + id + " refers to non existing household " + hhid + "!");
                }
                Person pp = ppFactory.createPerson(id, age, gender, race, occupation, PersonRole.SINGLE, workplace, income);
                persons.put(id, pp);
                addPersonToHousehold(pp, household);
                pp.setRole(pr);
                pp.setDriverLicense(license);

                //TODO: remove it when we implement interface
                if(implementation == Implementation.MUNICH){
                    int posSchoolCoordX = SiloUtil.findPositionInArray("schoolCoordX", header);
                    int posSchoolCoordY = SiloUtil.findPositionInArray("schoolCoordY", header);
                    // TODO Currently only instance where we set a zone id to -1. nk/dz, jul'18
                    Coordinate schoolCoord = new Coordinate(
                            Double.parseDouble(lineElements[posSchoolCoordX]),Double.parseDouble(lineElements[posSchoolCoordY]));
                    pp.setSchoolCoordinate(schoolCoord, -1);
                }
            }
        } catch (IOException e) {
        }
    }
}
