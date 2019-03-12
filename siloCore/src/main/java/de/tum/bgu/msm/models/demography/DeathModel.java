package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.container.SiloDataContainerImpl;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.impls.person.DeathEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Greg Erhardt, Rolf Moeckel
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 */
public class DeathModel extends AbstractModel implements EventModel<DeathEvent> {

    private DeathJSCalculator calculator;

    public DeathModel(SiloDataContainerImpl dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public boolean handleEvent(DeathEvent event) {

        // simulate if person with ID perId dies in this simulation period
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(event.getPersonId());
        if (person != null) {
            final int age = Math.min(person.getAge(), 100);
            if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDeathProbability(age, person.getGender())) {
                return die(person);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
    }

    @Override
    public void setup() {
        final Reader reader;

        switch (properties.main.implementation) {
            case MUNICH:
                reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
                break;
            case MARYLAND:
                reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMstm"));
                break;
            case PERTH:
            case KAGAWA:
            case CAPE_TOWN:
            default:
                throw new RuntimeException("DeathModel implementation not applicable for " + properties.main.implementation);
        }
        calculator = new DeathJSCalculator(reader);
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
        final Household hhOfPersonToDie = person.getHousehold();
        householdData.addHouseholdAboutToChange(hhOfPersonToDie);

        if (person.getJobId() > 0) {
            dataContainer.getJobData().quitJob(true, person);
        }

        if (person.getRole() == PersonRole.MARRIED) {
            Person widow = HouseholdUtil.findMostLikelyPartner(person, hhOfPersonToDie);
            widow.setRole(PersonRole.SINGLE);
        }
        householdData.removePerson(person.getId());

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
