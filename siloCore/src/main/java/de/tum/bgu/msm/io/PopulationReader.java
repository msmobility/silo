package de.tum.bgu.msm.io;

import org.locationtech.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PopulationReader {

    private final static Logger logger = Logger.getLogger(PopulationReader.class);

    public Map<Integer, Household> readHouseholdFile(String fileName){

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
        logger.info("Finished reading " + recCount + " households.");
        return households;
    }


    public Map<Integer, Person> readPersonFile (String path, Map<Integer, Household> householdMap) {

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
                Person pp = ppFactory.createPerson(id, age, gender, race, occupation, workplace, income);
                pp.setRole(pr);
                pp.setDriverLicense(license);
                //TODO: remove it when we implement interface
                if(implementation == Implementation.MUNICH){
                    int posSchoolCoordX = SiloUtil.findPositionInArray("schoolCoordX", header);
                    int posSchoolCoordY = SiloUtil.findPositionInArray("schoolCoordY", header);
                    // TODO Currently only instance where we set a zone id to -1. nk/dz, jul'18
                    //Coordinate schoolCoord = new Coordinate(
                            //Double.parseDouble(lineElements[posSchoolCoordX]),Double.parseDouble(lineElements[posSchoolCoordY]));
                    pp.setSchoolCoordinate(null, -1);
                }

                persons.put(id, pp);
                addPersonToHousehold(pp, household);
            }
        } catch (IOException e) {
        }
        logger.info("Finished reading " + recCount + " persons.");
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
}
