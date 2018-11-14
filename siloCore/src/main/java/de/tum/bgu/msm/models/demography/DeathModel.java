package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.DeathEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Greg Erhardt, Rolf Moeckel, Ana Moreno
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 * Revised on Nov 14, 2018 to use precalculated probabilities from parametrized distribution
 */
public class DeathModel extends AbstractModel implements MicroEventModel<DeathEvent> {

    private DeathJSCalculator calculator;
    private HashMap<Gender, double[]> deathProbabilities;

    public DeathModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        //setupDeathModel();
        deathProbabilities = setupDeathModelDistribution();
    }

    private void setupDeathModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMstm"));
        }
        calculator = new DeathJSCalculator(reader);
    }

    private HashMap<Gender, double[]> setupDeathModelDistribution(){
        double alphaFemale = 0.104163121;
        double alphaMale = 0.09156481;
        double scaleFemale = 1.19833E-05;
        double scaleMale = 4.56581E-05;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 0; age < 100; age++){
            probFemale[age] = scaleFemale * Math.exp(age * alphaFemale);
            probMale[age] = scaleMale * Math.exp(age * alphaMale);
        }
        HashMap<Gender, double[]> probabilities = new HashMap<>();
        probabilities.put(Gender.FEMALE,probFemale);
        probabilities.put(Gender.MALE, probMale);
        return probabilities;
    }

    @Override
    public boolean handleEvent(DeathEvent event) {

        // simulate if person with ID perId dies in this simulation period
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(event.getPersonId());
        if (person != null) {
            final int age = Math.min(person.getAge(), 100);
            //if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDeathProbability(age, person.getGender())) {
            if (SiloUtil.getRandomNumberAsDouble() < deathProbabilities.get(person.getGender())[age]) {
                return die(person);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
    }

    @Override
    public Collection<DeathEvent> prepareYear(int year) {
        final List<DeathEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdData().getPersons()) {
            events.add(new DeathEvent(person.getId()));
        }
        return events;
    }

    boolean die(Person person) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();

        if (person.getWorkplace() > 0) {
            dataContainer.getJobData().quitJob(true, person);
        }
        final Household hhOfPersonToDie = person.getHousehold();

        if (person.getRole() == PersonRole.MARRIED) {
            Person widow = HouseholdUtil.findMostLikelyPartner(person, hhOfPersonToDie);
            widow.setRole(PersonRole.SINGLE);
        }
        householdData.removePerson(person.getId());
        householdData.addHouseholdThatChanged(hhOfPersonToDie);

        final boolean onlyChildrenLeft = HouseholdUtil.checkIfOnlyChildrenRemaining(hhOfPersonToDie);
        if (onlyChildrenLeft) {
            for (Person pp : hhOfPersonToDie.getPersons().values()) {
                if (pp.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Child " + pp.getId() + " was moved from household " + hhOfPersonToDie.getId() +
                            " to foster care as remaining child just before head of household (ID " +
                            person.getId() + ") passed away.");
                }
            }
            householdData.removeHousehold(hhOfPersonToDie.getId());
        }

        if (person.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("We regret to inform that person " + person.getId() + " from household " + hhOfPersonToDie.getId() +
                    " has passed away.");
        }

        return true;
    }
}
