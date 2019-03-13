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

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;

import static de.tum.bgu.msm.data.household.HouseholdUtil.getHhIncome;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public class HouseholdDataImpl implements HouseholdData {

    private final static Logger logger = Logger.getLogger(HouseholdDataImpl.class);

    private final DataContainer dataContainer;

    private final PersonFactory ppFactory;
    private final HouseholdFactory hhFactory;

    private int highestHouseholdIdInUse;
    private int highestPersonIdInUse;

    private float[][][] avgIncomeByGenderByAgeByOccupation;
    private final Map<Integer, Float> medianIncomeByMsa = new HashMap<>();

    private final Map<Integer, Person> persons = new HashMap<>();
    private final Map<Integer, Household> households = new HashMap<>();

    private List<Household> updatedHouseholds = new ArrayList<>();

    public HouseholdDataImpl(DataContainer dataContainer, PersonFactory ppFactory, HouseholdFactory hhFactory) {
        this.dataContainer = dataContainer;
        this.ppFactory = ppFactory;
        this.hhFactory = hhFactory;
    }

    @Override
    public void setup() {
        identifyHighestHouseholdAndPersonId();
        avgIncomeByGenderByAgeByOccupation = calculateIncomeDistribution();
    }

    @Override
    public void prepareYear(int year) {
        adjustIncome();
        // needs to be calculated even if no dwellings are added this year:
        // median income is needed in housing search in MovesModelMstm.searchForNewDwelling (int hhId)
        calculateMedianHouseholdIncomeByMSA();
    }

    @Override
    public void endYear(int year) {
        updatedHouseholds.clear();
    }

    @Override
    public void endSimulation() {

    }

    @Override
    public float getMedianIncome(int msa) {
        return medianIncomeByMsa.get(msa);
    }

    @Override
    public float getAverageIncome(Gender gender, int age, Occupation occupation) {
        return avgIncomeByGenderByAgeByOccupation[gender.ordinal()][age][occupation==Occupation.EMPLOYED?1:0];
    }

    @Override
    public Household getHouseholdFromId(int householdId) {
        return households.get(householdId);
    }

    @Override
    public Collection<Household> getHouseholds() {
        return Collections.unmodifiableCollection(households.values());
    }

    @Override
    public Person getPersonFromId(int id) {
        return persons.get(id);
    }

    @Override
    public void removePerson(int id) {
        removePersonFromHousehold(persons.get(id));
        persons.remove(id);
    }

    @Override
    public Collection<Person> getPersons() {
        return persons.values();
    }

    @Override
    public void removePersonFromHousehold(Person person) {
        Household household = person.getHousehold();
        if (household != null) {
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

    @Override
    public void addPersonToHousehold(Person person, Household household) {
        // add existing person per (not a newborn child) to household
        if (household.getPersons().containsKey(person.getId())) {
            throw new IllegalArgumentException("Person " + person.getId() + " was already added to household " + household.getId());
        }
        household.addPerson(person);
        person.setHousehold(household);
        if (person.getId() == SiloUtil.trackPp || household.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("A person " +
                    "(not a child) named " + person.getId() + " was added to household " + household.getId() + ".");
        }
    }

    @Override
    public int getNextHouseholdId() {
        return ++highestHouseholdIdInUse;
    }

    @Override
    public int getNextPersonId() {
        return ++highestPersonIdInUse;
    }

    @Override
    public int getHighestHouseholdIdInUse() {
        return highestHouseholdIdInUse;
    }

    @Override
    public int getHighestPersonIdInUse() {
        return highestPersonIdInUse;
    }

    @Override
    public void removeHousehold(int householdId) {
        // remove household and add dwelling to vacancy list

        Household household = households.get(householdId);
        int dwellingId = household.getDwellingId();
        if (dwellingId != -1) {
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(dwellingId);
            dd.setResidentID(-1);
            dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        }
        for (Person pp : household.getPersons().values()) {
            pp.setHousehold(null);
            persons.remove(pp.getId());
        }
        households.remove(householdId);
        updatedHouseholds.remove(householdId);
        if (householdId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Households " + householdId + " was removed");
        }
    }

    private float getAverageHouseholdSize() {
        float ahs = 0;
        int cnt = 0;
        for (Household hh : households.values()) {
            ahs += hh.getHhSize();
            cnt++;
        }
        return ahs / (float) cnt;
    }

    public void summarizePopulation(DataContainer dataContainer) {
        // summarize population for summary file

        final GeoData geoData = dataContainer.getGeoData();
        int pers[][] = new int[2][101];
        int ppRace[] = new int[4];
        for (Person per : persons.values()) {
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
        for (Household hh : households.values()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
            hht[hh.getHouseholdType().ordinal()]++;
            hhRace[hh.getRace().ordinal()]++;
            hhIncome[hhIncomePos] = getHhIncome(hh);
            hhIncomePos++;
            int homeZone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                homeZone = dwelling.getZoneId();
            }
            int region = dataContainer.getGeoData().getZones().get(homeZone).getRegion().getId();
            hhByRegion[region]++;
        }
        SummarizeData.resultFile("hhByType,hh");
        for (HouseholdType ht : HouseholdType.values()) {
            String row = ht + "," + hht[ht.ordinal()];
            SummarizeData.resultFile(row);
        }
        SummarizeData.resultFile("hhByRace,hh");
        SummarizeData.resultFile("white," + hhRace[0]);
        SummarizeData.resultFile("black," + hhRace[1]);
        SummarizeData.resultFile("hispanic," + hhRace[2]);
        SummarizeData.resultFile("other," + hhRace[3]);
        String row = "hhBySize";
        for (int i : hhs) row = row + "," + i;
        SummarizeData.resultFile(row);
        row = "AveHHSize," + getAverageHouseholdSize();
        SummarizeData.resultFile(row);
        double aveHHincome = SiloUtil.getSum(hhIncome) / households.size();
        row = "AveHHInc," + aveHHincome + ",MedianHHInc," + SiloUtil.getMedian(hhIncome);
        SummarizeData.resultFile(row);
        // labor participation and commuting distance
        float[][][] labP = new float[2][2][5];
        float[][] commDist = new float[2][geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt() + 1];
        for (Person per : persons.values()) {
            int age = per.getAge();
            Gender gender = per.getGender();
            boolean employed = per.getJobId() > 0;
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
                if (dwelling != null) {
                    zone = geoData.getZones().get(dwelling.getZoneId());
                }
                Zone destination = geoData.getZones().get(dataContainer.getJobData().getJobFromId(per.getJobId()).getZoneId());
                double ds = dataContainer.getTravelTimes().
                        getTravelTime(zone, destination, Properties.get().transportModel.peakHour_s, TransportMode.car);
                commDist[0][zone.getRegion().getId()] += ds;
                commDist[1][zone.getRegion().getId()]++;
            }
        }
        String[] grp = {"<18", "18-29", "30-49", "50-64", ">=65"};
        SummarizeData.resultFile("laborParticipationRateByAge,male,female");
        for (int ag = 0; ag < 5; ag++) {
            Formatter f = new Formatter();
            f.format("%s,%f,%f", grp[ag], labP[1][0][ag] / (labP[0][0][ag] + labP[1][0][ag]), labP[1][1][ag] / (labP[0][1][ag] + labP[1][1][ag]));
            SummarizeData.resultFile(f.toString());
        }
        // todo: Add distance in kilometers to this summary
        SummarizeData.resultFile("aveCommuteDistByRegion,minutes");
        for (int i : geoData.getRegions().keySet()) {
            SummarizeData.resultFile(i + "," + commDist[0][i] / commDist[1][i]);
        }
        int[] carOwnership = new int[4];
        for (Household hh : households.values()) {
            carOwnership[hh.getAutos()]++;
        }
        SummarizeData.resultFile("carOwnershipLevel,households");
        SummarizeData.resultFile("0cars," + carOwnership[0]);
        SummarizeData.resultFile("1car," + carOwnership[1]);
        SummarizeData.resultFile("2cars," + carOwnership[2]);
        SummarizeData.resultFile("3+cars," + carOwnership[3]);
    }

    private void identifyHighestHouseholdAndPersonId() {
        // identify highest household ID and highest person ID in use
        highestHouseholdIdInUse = 0;
        for (Household hh : households.values()) {
            highestHouseholdIdInUse = Math.max(highestHouseholdIdInUse, hh.getId());
        }
        highestPersonIdInUse = 0;
        for (Person pp : persons.values()) {
            highestPersonIdInUse = Math.max(highestPersonIdInUse, pp.getId());
        }
    }


    private float[][][] calculateIncomeDistribution() {
        // calculate income distribution by age, gender and occupation

        // income by gender, age and unemployed/employed
        float[][][] averageIncome = new float[2][100][2];
        int[][][] count = new int[2][100][2];
        for (Person pp : persons.values()) {
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
                    averageIncome[i][j][k] = (averageIncome[i][j - 2][k] / 4f + averageIncome[i][j - 1][k] / 2f +
                            averageIncome[i][j][k] + averageIncome[i][j + 1][k] / 2f + averageIncome[i][j + 2][k] / 4f) / 2.5f;
                }
            }
        }
        return averageIncome;
    }

    private void adjustIncome() {
        // select who will get a raise or drop in salary
        float[][][] previousIncomeDistribution = avgIncomeByGenderByAgeByOccupation;
        float[][][] currentIncomeDistribution = calculateIncomeDistribution();;
        float meanIncomeChange = Properties.get().householdData.meanIncomeChange;
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.cachedService();
        for (Person person : persons.values()) {
            executor.addTaskToQueue(new IncomeAdjustment(person, meanIncomeChange, currentIncomeDistribution, previousIncomeDistribution));
        }
        executor.execute();
    }


    private void calculateMedianHouseholdIncomeByMSA() {

        Map<Integer, List<Integer>> incomesByMsa = new HashMap<>();
        RealEstateData realEstateData = dataContainer.getRealEstateData();
        for (Household hh : households.values()) {
            int zone = -1;
            Dwelling dwelling = realEstateData.getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            int homeMSA = dataContainer.getGeoData().getZones().get(zone).getMsa();
            if (incomesByMsa.containsKey(homeMSA)) {
                List<Integer> inc = incomesByMsa.get(homeMSA);
                inc.add(getHhIncome(hh));
            } else {
                List<Integer> inc = new ArrayList<>();
                inc.add(getHhIncome(hh));
                incomesByMsa.put(homeMSA, inc);
            }
        }
        for (Integer thisMsa : incomesByMsa.keySet()) {
            medianIncomeByMsa.put(thisMsa, SiloUtil.getMedian(SiloUtil.convertIntegerArrayListToArray(incomesByMsa.get(thisMsa))));
        }
    }


    /**
     * Add one household that probably had changed their attributes for the car updating model
     * Households are added to this List only once, even if several changes happen to them. They are only added
     * once, because this HashMap stores the previous socio-demographics before any change happened in a given year
     * @param hh
     */
    @Override
    public void addHouseholdAboutToChange(Household hh) {
        if (!updatedHouseholds.contains(hh)) {
            updatedHouseholds.add(duplicateHousehold(hh));
        }
    }

    /**
     * //TODO
     * @return
     */
    @Override
    public List<Household> getUpdatedHouseholds() {
        return updatedHouseholds;
    }

    @Override
    public void addPerson(Person person) {
        persons.put(person.getId(), person);
    }

    @Override
    public void addHousehold(Household household) {
        households.put(household.getId(), household);
    }

    @Override
    public PersonFactory getPersonFactory() {
        return this.ppFactory;
    }

    @Override
    public HouseholdFactory getHouseholdFactory() {
        return this.hhFactory;
    }

    /**
     * Duplicates the given household by copying household attributes and individual persons.
     * Ids of persons and the household as well as spatial relationships like dwelling, jobs and schools
     * are not copied. Household roles are preserved.
     *
     * @param original the original household to be copied
     * @return a duplication of the original household and its persons
     */
    @Override
    public Household duplicateHousehold(Household original) {
        Household duplicate = hhFactory.duplicate(original, getNextHouseholdId());
        for (Person originalPerson : original.getPersons().values()) {
            Person personDuplicate = ppFactory.duplicate(originalPerson, getNextPersonId());
            personDuplicate.setRole(originalPerson.getRole());
            addPersonToHousehold(personDuplicate, duplicate);
        }
        return duplicate;
    }
}
