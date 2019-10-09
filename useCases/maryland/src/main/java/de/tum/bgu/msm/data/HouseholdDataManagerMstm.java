package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.*;

import static de.tum.bgu.msm.data.household.HouseholdUtil.getAnnualHhIncome;

public class HouseholdDataManagerMstm implements HouseholdDataManager {

    private final Map<Integer, Float> medianIncomeByMsa = new HashMap<>();
    private final HouseholdDataManagerImpl delegate;
    private final GeoData geoData;
    private final DwellingData dwellingData;

    public HouseholdDataManagerMstm(HouseholdData householdData, DwellingData dwellingData, GeoData geoData, PersonFactory ppFactory, HouseholdFactory hhFactory, Properties properties, RealEstateDataManager realEstateDataManager) {
        delegate = new HouseholdDataManagerImpl(householdData, dwellingData,
                ppFactory, hhFactory, properties, realEstateDataManager);
        this.dwellingData = dwellingData;
        this.geoData = geoData;
    }

    public float getMedianIncome(int msa) {
        return medianIncomeByMsa.get(msa);
    }

    private void calculateMedianHouseholdIncomeByMSA() {

        Map<Integer, List<Integer>> incomesByMsa = new HashMap<>();
        for (Household hh : delegate.getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dwellingData.getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            int homeMSA = ((MstmZone) geoData.getZones().get(zone)).getMsa();
            if (incomesByMsa.containsKey(homeMSA)) {
                List<Integer> inc = incomesByMsa.get(homeMSA);
                inc.add(getAnnualHhIncome(hh));
            } else {
                List<Integer> inc = new ArrayList<>();
                inc.add(getAnnualHhIncome(hh));
                incomesByMsa.put(homeMSA, inc);
            }
        }
        for (Integer thisMsa : incomesByMsa.keySet()) {
            medianIncomeByMsa.put(thisMsa, SiloUtil.getMedian(SiloUtil.convertIntegerArrayListToArray(incomesByMsa.get(thisMsa))));
        }
    }

    @Override
    public float getAverageIncome(Gender gender, int age, Occupation occupation) {
        return delegate.getAverageIncome(gender, age, occupation);
    }

    @Override
    public Household getHouseholdFromId(int householdId) {
        return delegate.getHouseholdFromId(householdId);
    }

    @Override
    public Collection<Household> getHouseholds() {
        return delegate.getHouseholds();
    }

    @Override
    public Person getPersonFromId(int id) {
        return delegate.getPersonFromId(id);
    }

    @Override
    public void removePerson(int id) {
        delegate.removePerson(id);
    }

    @Override
    public Collection<Person> getPersons() {
        return delegate.getPersons();
    }

    @Override
    public void removePersonFromHousehold(Person person) {
        delegate.removePersonFromHousehold(person);
    }

    @Override
    public void addPersonToHousehold(Person person, Household household) {
        delegate.addPersonToHousehold(person, household);
    }

    @Override
    public void removeHousehold(int householdId) {
        delegate.removeHousehold(householdId);
    }

    @Override
    public int getNextHouseholdId() {
        return delegate.getNextHouseholdId();
    }

    @Override
    public int getNextPersonId() {
        return delegate.getNextPersonId();
    }

    @Override
    public int getHighestHouseholdIdInUse() {
        return delegate.getHighestHouseholdIdInUse();
    }

    @Override
    public int getHighestPersonIdInUse() {
        return delegate.getHighestPersonIdInUse();
    }

    @Override
    public void saveHouseholdMemento(Household hh) {
        delegate.saveHouseholdMemento(hh);
    }

    @Override
    public Collection<Household> getHouseholdMementos() {
        return delegate.getHouseholdMementos();
    }

    @Override
    public void addPerson(Person person) {
        delegate.addPerson(person);
    }

    @Override
    public void addHousehold(Household household) {
        delegate.addHousehold(household);
    }

    @Override
    public PersonFactory getPersonFactory() {
        return delegate.getPersonFactory();
    }

    @Override
    public HouseholdFactory getHouseholdFactory() {
        return delegate.getHouseholdFactory();
    }

    @Override
    public Household duplicateHousehold(Household original) {
        return delegate.duplicateHousehold(original);
    }

    @Override
    public void setup() {
        delegate.setup();
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
        // needs to be calculated even if no dwellings are added this year:
        // median income is needed in housing search in MovesModelImplMstm.searchForNewDwelling (int hhId)
        calculateMedianHouseholdIncomeByMSA();
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
    }
}
