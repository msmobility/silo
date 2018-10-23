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

import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.PrintWriter;
import java.util.*;

import static de.tum.bgu.msm.data.household.HouseholdUtil.getHHLicenseHolders;
import static de.tum.bgu.msm.data.household.HouseholdUtil.getHhIncome;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 *
 */
public class HouseholdDataManager {
    private final static Logger LOGGER = Logger.getLogger(HouseholdDataManager.class);
    private final SiloDataContainer dataContainer;
    private final PersonFactory ppFactory;
    private final HouseholdFactory hhFactory;

    private int highestHouseholdIdInUse;
    private int highestPersonIdInUse;

    private float[][][] initialIncomeDistribution;              // income by age, gender and occupation
    public static int[] quitJobPersonIds;
    private static float[] medianIncome;

    private final Map<Integer, Person> persons = new HashMap<>();
    private final Map<Integer, Household> households = new HashMap<>();

    private Map<Integer, int[]> updatedHouseholds = new HashMap<>();
    private HashMap<Integer, int[]> conventionalCarsHouseholds = new HashMap<>();

    public HouseholdDataManager(SiloDataContainer dataContainer, PersonFactory ppFactory, HouseholdFactory hhFactory) {
        this.dataContainer = dataContainer;
        this.ppFactory = ppFactory;
        this.hhFactory = hhFactory;
    }

    public Household getHouseholdFromId(int householdId) {
        return households.get(householdId);
    }

    public Collection<Household> getHouseholds() {
        return Collections.unmodifiableCollection(households.values());
    }

    public Person getPersonFromId(int id) {
        return persons.get(id);
    }

    public void removePerson(int id) {
        removePersonFromHousehold(persons.get(id));
        persons.remove(id);
    }

    public int getPersonCount() {
        return persons.size();
    }

    public Collection<Person> getPersons() {
        return persons.values();
    }

    public void removePersonFromHousehold (Person person) {
        Household household = person.getHousehold();
        if(household != null ) {
            household.removePerson(person.getId());
            person.setHousehold(null);
            if (household.getPersons().isEmpty()) {
                removeHousehold(household.getId());
            }
            if (household.getId() == SiloUtil.trackHh || person.getId() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Person " +
                        person.getId() + " was removed from household " + household.getId() + ".");
            }
        }
    }

    public void addPersonToHousehold(Person person, Household household) {
        // add existing person per (not a newborn child) to household
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

    public int getTotalPopulation () {
        int tp = 0;
        for (Household hh: households.values()) {
            tp += hh.getHhSize();
        }
        return tp;
    }

    private float getAverageHouseholdSize() {
        float ahs = 0;
        int cnt = 0;
        for (Household hh: households.values()) {
            ahs += hh.getHhSize();
            cnt++;
        }
        return ahs/(float) cnt;
    }

    public static IncomeCategory getIncomeCategoryForIncome(int hhInc) {
        // return income category defined exogenously
        for (int i = 0; i < Properties.get().main.incomeBrackets.length; i++) {
            if (hhInc < Properties.get().main.incomeBrackets[i]) {
                return IncomeCategory.values()[i];
            }
        }
        // if income is larger than highest category
        return IncomeCategory.values()[IncomeCategory.values().length-1];
    }

    public void removeHousehold(int householdId) {
        // remove household and add dwelling to vacancy list

        Household household = households.get(householdId);
        int dwellingId = household.getDwellingId();
        if (dwellingId != -1) {
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(dwellingId);
            dd.setResidentID(-1);
            dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        }
        for(Person pp: household.getPersons().values()) {
            pp.setHousehold(null);
            persons.remove(pp.getId());
        }
        households.remove(householdId);
        updatedHouseholds.remove(householdId);
        if (householdId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Households " + householdId + " was removed");
        }
    }

    public void summarizePopulation (SiloDataContainer dataContainer, SiloModelContainer siloModelContainer) {
        // summarize population for summary file

        final GeoData geoData = dataContainer.getGeoData();
        int pers[][] = new int[2][101];
        int ppRace[] = new int[4];
        for (Person per: persons.values()) {
            Gender gender = per.getGender();
            int age = Math.min(per.getAge(), 100);
            pers[gender.ordinal()][age] += 1;
            ppRace[per.getRace().ordinal()]++;
        }
        int hhs[] = new int[10];
        int hht[] = new int[HouseholdType.values().length + 1];
        int hhRace[] = new int[4];
        int[] hhIncome = new int[households.size()];
        int hhIncomePos = 0;
        int hhByRegion[] = new int[dataContainer.getGeoData().getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt() + 1];
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
        for (Household hh: households.values()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
            hht[hh.getHouseholdType().ordinal()]++;
            hhRace[hh.getRace().ordinal()]++;
            hhIncome[hhIncomePos] = getHhIncome(hh);
            hhIncomePos++;
            int homeZone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                homeZone = dwelling.getZoneId();
            }
            int region = dataContainer.getGeoData().getZones().get(homeZone).getRegion().getId();
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
        row = "AveHHSize," + getAverageHouseholdSize();
        SummarizeData.resultFile(row);
        double aveHHincome = SiloUtil.getSum(hhIncome) / households.size();
        row = "AveHHInc," + aveHHincome + ",MedianHHInc," + SiloUtil.getMedian(hhIncome);
        SummarizeData.resultFile(row);
        // labor participation and commuting distance
        float[][][] labP = new float[2][2][5];
        float[][] commDist = new float[2][geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt() + 1];
        for (Person per: persons.values()) {
            int age = per.getAge();
            Gender gender = per.getGender();
            boolean employed = per.getWorkplace() > 0;
            int ageGroup = 0;
            if (age >= 65) ageGroup = 4;
            else if (age >= 50) ageGroup = 3;
            else if (age >= 30) ageGroup = 2;
            else if (age >= 18) ageGroup = 1;
            if (employed) labP[1][gender.ordinal()][ageGroup]++;
            else labP[0][gender.ordinal()][ageGroup]++;
            if (employed) {
                Zone zone = null;
                Household household = households.get(per.getHousehold().getId());
                Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(household.getDwellingId());
                if(dwelling != null) {
                    zone = geoData.getZones().get(dwelling.getZoneId());
                }
//                double ds = siloModelContainer.getAcc()
//                        .getPeakAutoTravelTime(zone.getZoneId(),
//                                dataContainer.getJobData().getJobFromId(per.getWorkplace()).getZone());
                Zone destination = geoData.getZones().get(dataContainer.getJobData().getJobFromId(per.getWorkplace()).getZoneId());
                double ds = dataContainer.getTravelTimes().
                		getTravelTime(zone, destination, Properties.get().main.peakHour, TransportMode.car);
                commDist[0][zone.getRegion().getId()] += ds;
                commDist[1][zone.getRegion().getId()] ++;
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
        for (int i: geoData.getRegions().keySet()) {
            SummarizeData.resultFile(i + "," + commDist[0][i] / commDist[1][i]);
        }
        int[] carOwnership = new int[4];
        for (Household hh: households.values()) {
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
        for (Household hh: households.values()) {
            highestHouseholdIdInUse = Math.max(highestHouseholdIdInUse, hh.getId());
        }
        highestPersonIdInUse = 0;
        for (Person pp: persons.values()) {
            highestPersonIdInUse = Math.max(highestPersonIdInUse, pp.getId());
        }
    }

    public int getNextHouseholdId () {
        return ++highestHouseholdIdInUse;
    }

    public int getNextPersonId () {
        return ++highestPersonIdInUse;
    }

    public int getHighestHouseholdIdInUse() {
        return highestHouseholdIdInUse;
    }

    public int getHighestPersonIdInUse() {
        return highestPersonIdInUse;
    }

    public void calculateInitialSettings () {
        initialIncomeDistribution = calculateIncomeDistribution();
    }

    private float[][][] calculateIncomeDistribution() {
        // calculate income distribution by age, gender and occupation

        float[][][] averageIncome = new float[2][100][2];              // income by gender, age and unemployed/employed
        int[][][] count = new int[2][100][2];
        for (Person pp: persons.values()) {
            int age = Math.min(99, pp.getAge());
            int occupation = 0;
            if (pp.getOccupation() == Occupation.EMPLOYED) {
                occupation = 1;
            }
            averageIncome[pp.getGender().ordinal()][age][occupation] += pp.getIncome();
            count[pp.getGender().ordinal()][age][occupation]++;
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
        float meanIncomeChange = Properties.get().householdData.meanIncomeChange;
        ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
        for (Person person: persons.values()) {
            executor.addTaskToQueue(new IncomeAdjustment(person, meanIncomeChange, currentIncomeDistribution, initialIncomeDistribution));
        }
        executor.execute();
    }

    public void selectIncomeForPerson (Person person) {
        final Gender gender = person.getGender();
        final int age = Math.min(99, person.getAge());
        final float meanIncomeChange = Properties.get().householdData.meanIncomeChange;
        final double[] prob = new double[21];
        final int[] change = new int[21];
        for (int i = 0; i < prob.length; i++) {
            // normal distribution to calculate change of income
            //TODO: Use normal distribution from library (e.g. commons math)
            change[i] = (int) (-5000f + 10000f * (float) i / (prob.length - 1f));
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) *
                    Math.exp(-(Math.pow(change[i], 2) / (2 * Math.pow(meanIncomeChange, 2))));
        }
        final int sel = SiloUtil.select(prob);
        final int inc = Math.max((int) initialIncomeDistribution[gender.ordinal()][age][person.getOccupation().getCode()] + change[sel], 0);
        person.setIncome(inc);
    }

    public static int[] getQuitJobPersonIds() {
        return quitJobPersonIds;
    }


    public HashMap<Integer, int[]> getHouseholdsByZone () {
        // return HashMap<Zone, ArrayOfHouseholdIds>

        HashMap<Integer, int[]> hhByZone = new HashMap<>();
        RealEstateDataManager realEstateData = dataContainer.getRealEstateData();
        for (Household hh: households.values()) {
            int zone = -1;
            Dwelling dwelling = realEstateData.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZoneId();
            }
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
        RealEstateDataManager realEstateData = dataContainer.getRealEstateData();
        for (Household hh: households.values()) {
            int zone = -1;
            Dwelling dwelling = realEstateData.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZoneId();
            }
            int homeMSA = geoData.getZones().get(zone).getMsa();
            if (rentHashMap.containsKey(homeMSA)) {
                ArrayList<Integer> inc = rentHashMap.get(homeMSA);
                inc.add(getHhIncome(hh));
            } else {
                ArrayList<Integer> inc = new ArrayList<>();
                inc.add(getHhIncome(hh));
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


    public void addHouseholdThatChanged (Household hh){
        // Add one household that probably had changed their attributes for the car updating model
        // Households are added to this HashMap only once, even if several changes happen to them. They are only added
        // once, because this HashMap stores the previous socio-demographics before any change happened in a given year.
        if (!updatedHouseholds.containsKey(hh.getId())) {
            int[] currentHouseholdAttributes = new int[4];
            currentHouseholdAttributes[0] = hh.getHhSize();
            currentHouseholdAttributes[1] = getHhIncome(hh);
            currentHouseholdAttributes[2] = getHHLicenseHolders(hh);
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
            currentHouseholdAttributes[1] = getHhIncome(hh);
            currentHouseholdAttributes[2] = getHHLicenseHolders(hh);
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

    public HashMap<Integer, int[]> getConventionalCarsHouseholds(){
        // return HashMap<Household, ArrayOfHouseholdAttributes>. These are the households eligible for switching
        // to autonomous cars. currently income is the only household attribute used but room is left for additional
        // attributes in the future
        for (Household hh: households.values()){
            if(hh.getAutos() > hh.getAutonomous()){
                int[] hhAttributes = new int[1];
                hhAttributes[0] = getHhIncome(hh);
                conventionalCarsHouseholds.put(hh.getId(), hhAttributes);
            }
        }
        return conventionalCarsHouseholds;
    }

    public void clearConventionalCarsHouseholds(){
        conventionalCarsHouseholds.clear();
    }

    public void addPerson(Person person) {
        persons.put(person.getId(), person);
    }

    public void addHousehold(Household household) {
        households.put(household.getId(), household);
    }

    public PersonFactory getPersonFactory() {
        return this.ppFactory;
    }

    public HouseholdFactory getHouseholdFactory() {
        return this.hhFactory;
    }
}
