/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentFunctionExecutor;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import static de.tum.bgu.msm.SiloUtil.openFileForSequentialWriting;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 *
 */
public class HouseholdDataManager {
    static Logger logger = Logger.getLogger(HouseholdDataManager.class);

    private static int highestHouseholdIdInUse;
    private static int highestPersonIdInUse;

    private float[][] laborParticipationShares;
    private static float[][][] initialIncomeDistribution;              // income by age, gender and occupation
    private static float meanIncomeChange;
    public static int[] startNewJobPersonIds;
    public static int[] quitJobPersonIds;
    private static float[] medianIncome;
    private RealEstateDataManager realEstateData;
    private HashMap<Integer, int[]> updatedHouseholds = new HashMap<>();


    public HouseholdDataManager(RealEstateDataManager realEstateData) {
        this.realEstateData = realEstateData;
        meanIncomeChange = Properties.get().householdData.meanIncomeChange;
    }


    public void readPopulation (boolean readSmallSynPop, int sizeSmallSynPop) {
        boolean readBin = Properties.get().householdData.readBinaryPopulation;
        if (readBin) {
            readBinaryPopulationDataObjects();
        } else {
            readHouseholdData(readSmallSynPop,  sizeSmallSynPop);
            readPersonData(readSmallSynPop, sizeSmallSynPop);
        }
    }


    private void readHouseholdData(boolean readSmallSynPop, int sizeSmallSynPop) {
        logger.info("Reading household micro data from ascii file");

        int year = Properties.get().main.startYear;
        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName;
        if (readSmallSynPop) fileName += "_" + sizeSmallSynPop;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId    = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling",header);
            int posTaz   = SiloUtil.findPositionInArray("zone",header);
            int posSize  = SiloUtil.findPositionInArray("hhSize",header);
            int posAutos = SiloUtil.findPositionInArray("autos",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int dwellingID = Integer.parseInt(lineElements[posDwell]);
                int taz        = Integer.parseInt(lineElements[posTaz]);
                int autos      = Integer.parseInt(lineElements[posAutos]);

                Household hh = new Household(id, dwellingID, autos);  // this automatically puts it in id->household map in Household class
                if (id == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(hh.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");
    }


    public static void writeBinaryPopulationDataObjects() {
        // Store population object data in binary file
        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.binaryPopulationFile;
        logger.info("  Writing population data to binary file.");
        Object[] data = {Household.getHouseholds().toArray(new Household[Household.getHouseholdCount()]),
                Person.getPersons().toArray(new Person[Person.getPersonCount()])};
        try {
            File fl = new File(fileName);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fl));
            out.writeObject(data);
            out.close();
        } catch (Exception e) {
            logger.error("Error saving to binary file " + fileName + ". Object not saved.\n" + e);
        }
    }


    private void readBinaryPopulationDataObjects() {
        // read households and persons from binary file
        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.binaryPopulationFile;
        logger.info("Reading population data from binary file.");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object[] data = (Object[]) in.readObject();
            Household.saveHouseholds((Household[]) data[0]);
            Person.savePersons((Person[]) data[1]);
        } catch (Exception e) {
            logger.error ("Error reading from binary file " + fileName + ". Object not read.\n" + e);
        }
        setHighestHouseholdAndPersonId();
        logger.info("Finished reading " + Household.getHouseholdCount() + " households.");
        logger.info("Finished reading " + Person.getPersonCount() + " persons.");
    }


    private void readPersonData(boolean readSmallSynPop, int sizeSmallSynPop) {
        logger.info("Reading person micro data from ascii file");

        int year = Properties.get().main.startYear;
        String fileName = Properties.get().main.baseDirectory +  Properties.get().householdData.personFileName;
        if (readSmallSynPop) fileName += "_" + sizeSmallSynPop;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhid",header);
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
                int gender     = Integer.parseInt(lineElements[posGender]);
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp.toUpperCase());
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                int occupation = Integer.parseInt(lineElements[posOccupation]);
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                boolean license = true;
                if (Integer.parseInt(lineElements[posDriver]) == 0){
                    license = false;
                }
                Household household = Household.getHouseholdFromId(hhid);
                if(household == null) {
                    throw new RuntimeException(new StringBuilder("Person ").append(id).append(" refers to non existing household ").append(hhid).append("!").toString());
                }
                Person pp = new Person(id, age, gender, race, occupation, workplace, income); //this automatically puts it in id->person map in Person class
                household.addPerson(pp);
                pp.setRole(pr);
                pp.setDriverLicense(license);
                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(Person.getPersonFromId(id).toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
    }

    public void setTypeOfAllHouseholds () {
        // define household types
        for (Household hh: Household.getHouseholds()) {
            hh.setType();
            hh.determineHouseholdRace();
        }
    }


    public static HouseholdType defineHouseholdType (int hhSize, int hhIncome) {
        // define household type based on size and income

        HouseholdType ht = null;
        if (hhSize == 1) {
            if (hhIncome == 1) ht = HouseholdType.size1inc1;
            else if (hhIncome == 2) ht = HouseholdType.size1inc2;
            else if (hhIncome == 3) ht = HouseholdType.size1inc3;
            else ht = HouseholdType.size1inc4;
        } else if (hhSize == 2) {
            if (hhIncome == 1) ht = HouseholdType.size2inc1;
            else if (hhIncome == 2) ht = HouseholdType.size2inc2;
            else if (hhIncome == 3) ht = HouseholdType.size2inc3;
            else ht = HouseholdType.size2inc4;
        } else if (hhSize == 3) {
            if (hhIncome == 1) ht = HouseholdType.size3inc1;
            else if (hhIncome == 2) ht = HouseholdType.size3inc2;
            else if (hhIncome == 3) ht = HouseholdType.size3inc3;
            else ht = HouseholdType.size3inc4;
        } else if (hhSize > 3) {
            if (hhIncome == 1) ht = HouseholdType.size4inc1;
            else if (hhIncome == 2) ht = HouseholdType.size4inc2;
            else if (hhIncome == 3) ht = HouseholdType.size4inc3;
            else ht = HouseholdType.size4inc4;
        }
        return ht;
    }


    public static int getIncomeCategoryForIncome(int hhInc) {
        // return income category defined exogenously

        for (int category = 1; category <= Properties.get().main.incomeBrackets.length; category++) {
            if (hhInc <= Properties.get().main.incomeBrackets[category - 1]) return category;
        }
        return Properties.get().main.incomeBrackets.length + 1;  // if income is larger than highest category
    }


    public static int getSpecifiedIncomeCategoryForIncome(int[] incCats, int hhInc) {
        // return income category defined exogenously

        for (int category = 1; category <= incCats.length; category++) {
            if (hhInc <= incCats[category - 1]) return category;
        }
        return incCats.length + 1;  // if income is larger than highest category
    }


    public static int getNumberOfWorkersInHousehold(Household hh) {
        // return number of workers in household hh
        int numberOfWorkers = 0;
        for (Person pp: hh.getPersons()) {
            if (pp.getOccupation() == 1) numberOfWorkers++;
        }
        return numberOfWorkers;
    }


    public static void definePersonRolesInHousehold (Household hh) {
        // define roles in this household
        findMarriedCouple(hh);
        defineUnmarriedPersons(hh);
    }


    public static void findMarriedCouple(Household hh) {
        // define role of person with ageMain in household where members have ageAll[]
        int[] ages = new int[hh.getHhSize()];
        List<Person> personsCopy = new ArrayList<>(hh.getPersons());
        Collections.sort(personsCopy, new Person.PersonByAgeComparator());

        for (Person person: personsCopy) {
            Person partner = findMostLikelyUnmarriedPartner(person, hh);
            if (partner != null) {
                partner.setRole(PersonRole.MARRIED);
                person.setRole(PersonRole.MARRIED);
                if (person.getId() == SiloUtil.trackPp || person.getHh().getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Defined role of person  " + person.getId() + " in household " + person.getHh().getId() +
                            " as " + person.getRole());
                }
                if (partner.getId() == SiloUtil.trackPp || partner.getHh().getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Defined role of partner " + partner.getId() + " in household " + partner.getHh().getId() +
                            " as " + partner.getRole());
                }
                return;
            }
        }
    }


    public static void defineUnmarriedPersons (Household hh) {
        // For those that did not become the married couple define role in household (child or single)
        for (Person pp: hh.getPersons()) {
            if (pp.getRole() == PersonRole.MARRIED) {
                continue;
            }
            boolean someone15to40yearsOlder = false;      // assumption that this person is a parent
            final int ageMain = pp.getAge();
            for (Person per: hh.getPersons()) {
                if (pp.equals(per)) {
                    continue;
                }
                int age = per.getAge();
                if (age >= ageMain + 15 && age <= ageMain + 40) {
                    someone15to40yearsOlder = true;
                }
            }
            if ((someone15to40yearsOlder && ageMain < 50) || ageMain <= 15) {
                pp.setRole(PersonRole.CHILD);
            } else {
                pp.setRole(PersonRole.SINGLE);
            }
            if (pp.getId() == SiloUtil.trackPp || pp.getHh().getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Defined role of person " + pp.getId() + " in household " + pp.getHh().getId() +
                        " as " + pp.getRole());
            }
        }
    }


    public void removeHousehold(int householdId) {
        // remove household and add dwelling to vacancy list

        int dwellingId = Household.getHouseholdFromId(householdId).getDwellingId();
        if (dwellingId != -1) {
            Dwelling dd = Dwelling.getDwellingFromId(dwellingId);
            dd.setResidentID(-1);
            realEstateData.addDwellingToVacancyList(dd);
        }
        Household.remove(householdId);
        if (householdId == SiloUtil.trackHh)
            SiloUtil.trackWriter.println("Households " + householdId + " was removed");
    }


    public int getNumberOfHouseholds() {
        return Household.getHouseholdCount();
    }


    public int getNumberOfPersons() {
        return Person.getPersonCount();
    }


    public Collection<Person> getPersons() {
        return Person.getPersons();
    }


    public static void summarizePopulation (GeoData geoData, SiloModelContainer siloModelContainer) {
        // summarize population for summary file

        int pers[][] = new int[2][101];
        int ppRace[] = new int[4];
        for (Person per: Person.getPersons()) {
            int gender = per.getGender();
            int age = Math.min(per.getAge(), 100);
            pers[gender-1][age] += 1;
            ppRace[per.getRace().ordinal()]++;
        }
        int hhs[] = new int[10];
        int hht[] = new int[HouseholdType.values().length + 1];
        int hhRace[] = new int[4];
        int[] hhIncome = new int[Household.getHouseholdCount()];
        int hhIncomePos = 0;
        int hhByRegion[] = new int[SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        SummarizeData.resultFile("Age,Men,Women");
        for (int i = 0; i <= 100; i++) {
            String row = i + "," + pers[0][i] + "," + pers[1][i];
            SummarizeData.resultFile(row);
        }
        SummarizeData.resultFile("ppByRace,hh");
        SummarizeData.resultFile("white," + ppRace[0]);
        SummarizeData.resultFile("black," + ppRace[1]);
        SummarizeData.resultFile("hispanic," + ppRace[2]);
        SummarizeData.resultFile("other," + ppRace[3]);
        for (Household hh: Household.getHouseholds()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
            hht[hh.getHouseholdType().ordinal()]++;
            hhRace[hh.getRace().ordinal()]++;
            hhIncome[hhIncomePos] = hh.getHhIncome();
            hhIncomePos++;
            int region = geoData.getZones().get(hh.getHomeZone()).getRegion().getId();
            hhByRegion[region]++;
        }
                SummarizeData.resultFile("hhByType,hh");
        for (HouseholdType ht: HouseholdType.values()) {
            String row = ht + "," + hht[ht.ordinal()];
            SummarizeData.resultFile(row);
        }
        SummarizeData.resultFile("hhByRace,hh");
        SummarizeData.resultFile("white," + hhRace[0]);
        SummarizeData.resultFile("black," + hhRace[1]);
        SummarizeData.resultFile("hispanic," + hhRace[2]);
        SummarizeData.resultFile("other," + hhRace[3]);
        String row = "hhBySize";
        for (int i: hhs) row = row + "," + i;
        SummarizeData.resultFile(row);
        row = "AveHHSize," + Household.getAverageHouseholdSize();
        SummarizeData.resultFile(row);
        double aveHHincome = SiloUtil.getSum(hhIncome) / Household.getHouseholdCount();
        row = "AveHHInc," + aveHHincome + ",MedianHHInc," + SiloUtil.getMedian(hhIncome);
        SummarizeData.resultFile(row);
        // labor participation and commuting distance
        float[][][] labP = new float[2][2][5];
        float[][] commDist = new float[2][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        for (Person per: Person.getPersons()) {
            int age = per.getAge();
            int gender = per.getGender() - 1;
            boolean employed = per.getWorkplace() > 0;
            int ageGroup = 0;
            if (age >= 65) ageGroup = 4;
            else if (age >= 50) ageGroup = 3;
            else if (age >= 30) ageGroup = 2;
            else if (age >= 18) ageGroup = 1;
            if (employed) labP[1][gender][ageGroup]++;
            else labP[0][gender][ageGroup]++;
            if (employed) {
                double ds = siloModelContainer.getAcc().getPeakAutoTravelTime(per.getHomeTaz(), Job.getJobFromId(per.getWorkplace()).getZone());
                commDist[0][geoData.getRegionOfZone(per.getHomeTaz())] += ds;
                commDist[1][geoData.getRegionOfZone(per.getHomeTaz())] ++;
            }
        }
        String[] grp = {"<18","18-29","30-49","50-64",">=65"};
        SummarizeData.resultFile("laborParticipationRateByAge,male,female");
        for (int ag = 0; ag < 5; ag++) {
            Formatter f = new Formatter();
            f.format("%s,%f,%f", grp[ag], labP[1][0][ag]/(labP[0][0][ag]+labP[1][0][ag]), labP[1][1][ag]/(labP[0][1][ag]+labP[1][1][ag]));
            SummarizeData.resultFile(f.toString());
        }
        // todo: Add distance in kilometers to this summary
        SummarizeData.resultFile("aveCommuteDistByRegion,minutes");
        for (int i: geoData.getRegionIdsArray()) SummarizeData.resultFile(i + "," + commDist[0][i] / commDist[1][i]);
        int[] carOwnership = new int[4];
        for (Household hh: Household.getHouseholds()) {
            carOwnership[hh.getAutos()]++;
        }
        SummarizeData.resultFile("carOwnershipLevel,households");
        SummarizeData.resultFile("0cars," + carOwnership[0]);
        SummarizeData.resultFile("1car," + carOwnership[1]);
        SummarizeData.resultFile("2cars," + carOwnership[2]);
        SummarizeData.resultFile("3+cars," + carOwnership[3]);
    }


    public void setHighestHouseholdAndPersonId () {
        // identify highest household ID and highest person ID in use
        highestHouseholdIdInUse = 0;
        for (Household hh: Household.getHouseholds()) {
            highestHouseholdIdInUse = Math.max(highestHouseholdIdInUse, hh.getId());
        }
        highestPersonIdInUse = 0;
        for (Person pp: Person.getPersons()) highestPersonIdInUse = Math.max(highestPersonIdInUse, pp.getId());
    }


    public static int getNextHouseholdId () {
        // increase highestHouseholdIdInUse by 1 and return value
        highestHouseholdIdInUse++;
        return highestHouseholdIdInUse;
    }


    public static int getNextPersonId () {
        // increase highestPersonIdInUse by 1 and return value
        highestPersonIdInUse++;
        return highestPersonIdInUse;
    }


    public static int getHighestHouseholdIdInUse() {
        return highestHouseholdIdInUse;
    }

    public static int getHighestPersonIdInUse() {
        return highestPersonIdInUse;
    }

    private static Person findMostLikelyUnmarriedPartner (Person per, Household hh) {
        // when assigning roles to persons, look for likely partner in household that is not married yet

        Person selectedPartner = null;
        double highestUtil = Double.NEGATIVE_INFINITY;
        double tempUtil;
        for (Person partner: hh.getPersons()) {
            if (partner.getGender() != per.getGender() && partner.getRole() != PersonRole.MARRIED) {
                int ageDiff = Math.abs(per.getAge() - partner.getAge());
                if (ageDiff == 0) {
                    tempUtil = 2;
                } else {
                    tempUtil = 1f / (float) ageDiff;
                }
                if (tempUtil > highestUtil) {
                    selectedPartner = partner;     // find most likely partner
                }
            }
        }
        return selectedPartner;
    }


    public static Person findMostLikelyPartner(Person per, Household hh) {
        // find married partner that fits best for person per
        double highestUtil = Double.NEGATIVE_INFINITY;
        double tempUtil;
        Person selectedPartner = null;
        for(Person partner: hh.getPersons()) {
            if (!partner.equals(per) && partner.getGender() != per.getGender() && partner.getRole() == PersonRole.MARRIED) {
                final int ageDiff = Math.abs(per.getAge() - partner.getAge());
                if (ageDiff == 0) {
                    tempUtil = 2.;
                } else  {
                    tempUtil = 1. / ageDiff;
                }
                if (tempUtil > highestUtil) {
                    highestUtil = tempUtil;
                    selectedPartner = partner;     // find most likely partner
                }
            }
        }
        if (selectedPartner == null) {
            logger.error("Could not find spouse of person " + per.getId() + " in household " + hh.getId());
            for (Person person: hh.getPersons()) {
                logger.error("Houshold member " + person.getId() + " (gender: " + person.getGender() + ") is " +
                        person.getRole());
            }
        }
        return selectedPartner;
    }


    public void calculateInitialSettings () {
        calculateInitialLaborParticipation();
        initialIncomeDistribution = calculateIncomeDistribution();
    }


    private void calculateInitialLaborParticipation() {
        // calculate share of people employed by age and gender

        laborParticipationShares = new float[2][100];
        int[][] count = new int[2][100];
        for (Person pp: Person.getPersons()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) laborParticipationShares[gender-1][age]++;
            count[gender-1][age]++;
        }
        // calculate shares
        for (int gen = 0; gen <=1; gen++) {
            for (int age = 0; age < 100; age++) {
                if (count[gen][age] > 0) laborParticipationShares[gen][age] = laborParticipationShares[gen][age] / (1f * count[gen][age]);
            }

            // smooth out shares
            for (int age = 18; age < 98; age++) {
                laborParticipationShares[gen][age] = (laborParticipationShares[gen][age-2]/4f +
                        laborParticipationShares[gen][age-1]/2f + laborParticipationShares[gen][age] +
                        laborParticipationShares[gen][age+1]/2f + laborParticipationShares[gen][age+2]/4f) / 2.5f;
            }
        }
    }


    private float[][][] calculateIncomeDistribution() {
        // calculate income distribution by age, gender and occupation

        float[][][] averageIncome = new float[2][100][2];              // income by gender, age and unemployed/employed
        int[][][] count = new int[2][100][2];
        for (Person pp: Person.getPersons()) {
            int age = Math.min(99, pp.getAge());
            int occupation = 0;
            if (pp.getOccupation() == 1) occupation = 1;
            averageIncome[pp.getGender() - 1][age][occupation] += pp.getIncome();
            count[pp.getGender() - 1][age][occupation]++;
        }
        for (int i = 0; i < averageIncome.length; i++) {
            for (int j = 0; j < averageIncome[i].length; j++) {
                for (int k = 0; k < averageIncome[i][j].length; k++) {
                    if (count[i][j][k] > 0) averageIncome[i][j][k] = averageIncome[i][j][k] / count[i][j][k];
                }
            }
        }
        // smooth out income
        for (int i = 0; i < averageIncome.length; i++) {
            for (int j = 2; j < averageIncome[i].length - 2; j++) {
                for (int k = 0; k < averageIncome[i][j].length; k++) {
                    averageIncome[i][j][k] = (averageIncome[i][j-2][k]/4f + averageIncome[i][j-1][k]/2f +
                            averageIncome[i][j][k] + averageIncome[i][j+1][k]/2f + averageIncome[i][j+2][k]/4f) / 2.5f;
                }
            }
        }
        return averageIncome;
    }


    public void adjustIncome() {
        // select who will get a raise or drop in salary

        float[][][] currentIncomeDistribution = calculateIncomeDistribution();

        ConcurrentFunctionExecutor executor = new ConcurrentFunctionExecutor();
        for (Person person: Person.getPersons()) {
            float desiredShift = getDesiredShift(currentIncomeDistribution, person);
            executor.addFunction(new IncomeAdjustment(person, desiredShift, meanIncomeChange));
        }
        executor.execute();
    }

    private float getDesiredShift(float[][][] currentIncomeDistribution, Person person) {
        int gender = person.getGender() - 1;
        int age = Math.min(99, person.getAge());
        int occ = 0;
        if (person.getOccupation() == 1) {
            occ = 1;
        }
        return initialIncomeDistribution[gender][age][occ] - currentIncomeDistribution[gender][age][occ];
    }

    public static int selectIncomeForPerson (int gender, int age, int occupation) {
        // select income for household based on gender, age and occupation

        double[] prob = new double[21];
        int[] change = new int[21];
        for (int i = 0; i < prob.length; i++) {
            // normal distribution to calculate change of income
            change[i] = (int) (-5000f + 10000f * (float) i / (prob.length - 1f));
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) *
                    Math.exp(-(Math.pow(change[i], 2) / (2 * Math.pow(meanIncomeChange, 2))));
        }
        int sel = SiloUtil.select(prob);
        return Math.max((int) initialIncomeDistribution[gender][age][occupation] + change[sel], 0);
    }


    public void setUpChangeOfJob(int year) {
        // select people that will lose employment or start new job

        if (!EventRules.ruleQuitJob() && !EventRules.ruleStartNewJob()) return;
        logger.info("  Planning job changes (hire and fire) for the year " + year);

        // count currently employed people
        final float[][] currentlyEmployed = new float[2][100];
        final float[][] currentlyUnemployed = new float[2][100];
        for (Person pp : Person.getPersons()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) {
                currentlyEmployed[gender - 1][age]++;
            } else {
                currentlyUnemployed[gender - 1][age]++;
            }
        }

        // calculate change rates
        float[][] changeRate = new float[2][100];
        for (int gen = 0; gen <= 1; gen++) {
            for (int age = 0; age < 100; age++) {
                float change = laborParticipationShares[gen][age] *
                        (currentlyEmployed[gen][age] + currentlyUnemployed[gen][age]) - currentlyEmployed[gen][age];
                if (change > 0) {
                    // probability to find job
                    changeRate[gen][age] = (change / (1f * currentlyUnemployed[gen][age]));
                } else {
                    // probability to lose job
                    changeRate[gen][age] = (change / (1f * currentlyEmployed[gen][age]));
                }
            }
        }

        int[][] testCounter = new int[2][100];
        // plan employment changes
        ArrayList<Integer> alFindJob = new ArrayList<>();
        ArrayList<Integer> alQuitJob = new ArrayList<>();
        for (Person pp : Person.getPersons()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gen = pp.getGender() - 1;
            boolean employed = pp.getWorkplace() > 0;

            // find job
            if (changeRate[gen][age] > 0 && !employed) {
                if (SiloUtil.getRandomNumberAsFloat() < changeRate[gen][age]) {
                    alFindJob.add(pp.getId());
                    testCounter[gen][age]++;
                }
            }
            // lose job
            if (changeRate[gen][age] < 0 && employed) {
                if (SiloUtil.getRandomNumberAsFloat() < Math.abs(changeRate[gen][age])) {
                    alQuitJob.add(pp.getId());
                    testCounter[gen][age]--;
                }
            }
        }

        quitJobPersonIds = SiloUtil.convertIntegerArrayListToArray(alQuitJob);
        startNewJobPersonIds = SiloUtil.convertIntegerArrayListToArray(alFindJob);

    }


    public static int[] getStartNewJobPersonIds() {
        return startNewJobPersonIds;
    }

    public static int[] getQuitJobPersonIds() {
        return quitJobPersonIds;
    }


    public HashMap<Integer, int[]> getHouseholdsByZone () {
        // return HashMap<Zone, ArrayOfHouseholdIds>

        HashMap<Integer, int[]> hhByZone = new HashMap<>();
        for (Household hh: Household.getHouseholds()) {
            int zone = hh.getHomeZone();
            if (hhByZone.containsKey(zone)) {
                int[] oldList = hhByZone.get(zone);
                int[] newList = SiloUtil.expandArrayByOneElement(oldList, hh.getId());
                hhByZone.put(zone, newList);
            } else {
                hhByZone.put(zone, new int[]{hh.getId()});
            }

        }
        return hhByZone;
    }

    public void calculateMedianHouseholdIncomeByMSA(GeoData geoData) {

        HashMap<Integer, ArrayList<Integer>> rentHashMap = new HashMap<>();
        for (Household hh: Household.getHouseholds()) {
            int homeMSA = geoData.getZones().get(hh.getHomeZone()).getMsa();
            if (rentHashMap.containsKey(homeMSA)) {
                ArrayList<Integer> inc = rentHashMap.get(homeMSA);
                inc.add(hh.getHhIncome());
            } else {
                ArrayList<Integer> inc = new ArrayList<>();
                inc.add(hh.getHhIncome());
                rentHashMap.put(homeMSA, inc);
            }
        }
        medianIncome = new float[99999];
        for (Integer thisMsa: rentHashMap.keySet()) {
            medianIncome[thisMsa] = SiloUtil.getMedian(SiloUtil.convertIntegerArrayListToArray(rentHashMap.get(thisMsa)));
        }
    }


    public static float getMedianIncome(int msa) {
        return medianIncome[msa];
    }


    public void summarizeHouseholdsNearMetroStations (SiloModelContainer siloModelContainer) {
        // summarize households in the vicinity of selected Metro stops

        if (!Properties.get().householdData.summarizeMetro){
            return;
        }
        TableDataSet selectedMetro = SiloUtil.readCSVfile(Properties.get().householdData.selectedMetroStopsFile);

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        String fileName = (directory + "/" + Properties.get().householdData.householdsNearMetroFile + "_" +
                Properties.get().main.gregorianIterator + ".csv");
        PrintWriter pw = openFileForSequentialWriting(fileName, false);
        pw.print("income,dist");
        for (int row = 1; row <= selectedMetro.getRowCount(); row++) pw.print("," +
                selectedMetro.getStringValueAt(row, "MetroStation") + " (" + (int) selectedMetro.getValueAt(row, "Zone") + ")");
        pw.println();

        // summarize households by distance from Metro stop and income group
        int[][][] hhCounter = new int[selectedMetro.getRowCount()][11][4];
        HashMap<Integer, ArrayList> hhByDistToMetro = new HashMap<>();
        for (Integer dist = 0; dist <= 20; dist++) hhByDistToMetro.put(dist, new ArrayList<Integer>());

        for (Household hh: Household.getHouseholds()) {
            int incCat = getIncomeCategoryForIncome(hh.getHhIncome());
            Integer smallestDist = 21;
            for (int row = 1; row <= selectedMetro.getRowCount(); row++) {
                int metroZone = (int) selectedMetro.getValueAt(row, "Zone");
                int dist = (int) SiloUtil.rounder((float) siloModelContainer.getAcc().getPeakAutoTravelTime(hh.getHomeZone(), metroZone), 0);
                smallestDist = Math.min(smallestDist, dist);
                if (dist > 10) continue;
                hhCounter[row-1][dist][incCat-1]++;
            }
            if (smallestDist <= 20) {
                ArrayList<Integer> al = hhByDistToMetro.get(smallestDist);
                al.add(hh.getHhIncome());
                hhByDistToMetro.put(smallestDist, al);
            }
        }

        // write out summary by Metro Stop
        for (int inc = 1; inc <= 4; inc++) {
            for (int dist = 0; dist <= 10; dist++) {
                pw.print(inc + "," + dist);
                for (int row = 1; row <= selectedMetro.getRowCount(); row++) {
                    pw.print("," + hhCounter[row - 1][dist][inc - 1]);
                }
                pw.println();
            }
        }

        // write out summary by distance bin
        pw.println("distanceRing,householdCount,medianIncome");
        for (int dist = 0; dist <= 20; dist++) {
            int[] incomes = SiloUtil.convertIntegerArrayListToArray(hhByDistToMetro.get(dist));
            pw.println(dist + "," + incomes.length + "," + SiloUtil.getMedian(incomes));
        }

        pw.close();
    }


    public void writeOutSmallSynPop() {
        // write out numberOfHh number of households to have small file for running tests
        String baseDirectory = Properties.get().main.baseDirectory;
        int startYear = Properties.get().main.startYear;

        int numberOfHh = Properties.get().main.smallSynPopSize;
        logger.info("  Writing out smaller files of synthetic population with " + numberOfHh + " households only");
        String filehh = baseDirectory + Properties.get().householdData.householdFileName + "_" +
                numberOfHh + "_" + startYear + ".csv";
        String filepp = baseDirectory + Properties.get().householdData.personFileName + "_" +
                numberOfHh + "_" + startYear + ".csv";
        String filedd = baseDirectory + Properties.get().householdData.dwellingsFileName + "_" +
                numberOfHh + "_" + startYear + ".csv";
        String filejj = baseDirectory + Properties.get().householdData.jobsFileName + "_" +
                numberOfHh + "_" + startYear + ".csv";
        PrintWriter pwh = openFileForSequentialWriting(filehh, false);
        PrintWriter pwp = openFileForSequentialWriting(filepp, false);
        PrintWriter pwd = openFileForSequentialWriting(filedd, false);
        PrintWriter pwj = openFileForSequentialWriting(filejj, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        pwp.println("id,hhID,age,gender,relationShip,race,occupation,driversLicense,workplace,income");
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt");
        pwj.println("id,zone,personId,type");
        int counter = 0;
        for (Household hh : Household.getHouseholds()) {
            counter++;
            if (counter > numberOfHh) break;

            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            pwh.print(hh.getHomeZone());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
            // write out person attributes
            for (Person pp : hh.getPersons()) {
                pwp.print(pp.getId());
                pwp.print(",");
                pwp.print(hh.getId());
                pwp.print(",");
                pwp.print(pp.getAge());
                pwp.print(",");
                pwp.print(pp.getGender());
                pwp.print(",\"");
                pwp.print(pp.getRole().toString());
                pwp.print("\",\"");
                pwp.print(pp.getRace());
                pwp.print("\",");
                pwp.print(pp.getOccupation());
                pwp.print(",0,");
                pwp.print(pp.getWorkplace());
                pwp.print(",");
                pwp.println(pp.getIncome());
                // write out job attributes (if person is employed)
                int job = pp.getWorkplace();
                if (job > 0 && pp.getOccupation() == 1) {
                    Job jj = Job.getJobFromId(job);
                    pwj.print(jj.getId());
                    pwj.print(",");
                    pwj.print(jj.getZone());
                    pwj.print(",");
                    pwj.print(jj.getWorkerId());
                    pwj.print(",\"");
                    pwj.print(jj.getType());
                    pwj.println("\"");
                }
            }
            // write out dwelling attributes
            Dwelling dd = Dwelling.getDwellingFromId(hh.getDwellingId());
            pwd.print(dd.getId());
            pwd.print(",");
            pwd.print(dd.getZone());
            pwd.print(",\"");
            pwd.print(dd.getType());
            pwd.print("\",");
            pwd.print(dd.getResidentId());
            pwd.print(",");
            pwd.print(dd.getBedrooms());
            pwd.print(",");
            pwd.print(dd.getQuality());
            pwd.print(",");
            pwd.print(dd.getPrice());
            pwd.print(",");
            pwd.print(dd.getRestriction());
            pwd.print(",");
            pwd.println(dd.getYearBuilt());
        }
        // add a few empty dwellings
        for (Dwelling dd: Dwelling.getDwellings()) {
            if (dd.getResidentId() == -1 && SiloUtil.select(100) > 90) {
                // write out dwelling attributes
                pwd.print(dd.getId());
                pwd.print(",");
                pwd.print(dd.getZone());
                pwd.print(",\"");
                pwd.print(dd.getType());
                pwd.print("\",");
                pwd.print(dd.getResidentId());
                pwd.print(",");
                pwd.print(dd.getBedrooms());
                pwd.print(",");
                pwd.print(dd.getQuality());
                pwd.print(",");
                pwd.print(dd.getPrice());
                pwd.print(",");
                pwd.print(dd.getRestriction());
                pwd.print(",");
                pwd.println(dd.getYearBuilt());
            }
        }
        // add a few empty jobs
        for (Job jj: Job.getJobs()) {
            if (jj.getWorkerId() == -1 && SiloUtil.select(100) > 90) {
                pwj.print(jj.getId());
                pwj.print(",");
                pwj.print(jj.getZone());
                pwj.print(",");
                pwj.print(jj.getWorkerId());
                pwj.print(",\"");
                pwj.print(jj.getType());
                pwj.println("\"");
            }
        }

        pwh.close();
        pwp.close();
        pwd.close();
        pwj.close();
        //System.exit(0);
    }

    public void addHouseholdThatChanged (Household hh){
        // Add one household that probably had changed their attributes for the car updating model
        // Households are added to this HashMap only once, even if several changes happen to them. They are only added
        // once, because this HashMap stores the previous socio-demographics before any change happened in a given year.
        if (!updatedHouseholds.containsKey(hh.getId())) {
            int[] currentHouseholdAttributes = new int[4];
            currentHouseholdAttributes[0] = hh.getHhSize();
            currentHouseholdAttributes[1] = hh.getHhIncome();
            currentHouseholdAttributes[2] = hh.getHHLicenseHolders();
            currentHouseholdAttributes[3] = 0;
            updatedHouseholds.put(hh.getId(), currentHouseholdAttributes);
        }
    }

    public void addHouseholdThatMoved (Household hh){
        // Add one household that moved out for the car updating model
        // Different from method addHouseholdThatChanged(), because here the hasMoved-flag is set from 0 to 1
        if (updatedHouseholds.containsKey(hh.getId())) {
            int[] currentHouseholdAttributes = updatedHouseholds.get(hh.getId());
            currentHouseholdAttributes [3] = 1;
            updatedHouseholds.put(hh.getId(), currentHouseholdAttributes);
        } else {
            int[] currentHouseholdAttributes = new int[4];
            currentHouseholdAttributes[0] = hh.getHhSize();
            currentHouseholdAttributes[1] = hh.getHhIncome();
            currentHouseholdAttributes[2] = hh.getHHLicenseHolders();
            currentHouseholdAttributes[3] = 1;
            updatedHouseholds.put(hh.getId(), currentHouseholdAttributes);
        }
    }

    public void clearUpdatedHouseholds() {
        updatedHouseholds.clear();
    }

    public Map<Integer, int[]> getUpdatedHouseholds() {
        return updatedHouseholds;
    }
}
