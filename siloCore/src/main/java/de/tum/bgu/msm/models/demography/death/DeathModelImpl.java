package de.tum.bgu.msm.models.demography.death;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.impls.person.DeathEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Greg Erhardt, Rolf Moeckel
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 */
public class DeathModelImpl extends AbstractModel implements DeathModel {

    private final DeathStrategy strategy;

    public DeathModelImpl(DataContainer dataContainer, Properties properties, DeathStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
    }

    @Override
    public Collection<DeathEvent> getEventsForCurrentYear(int year) {
        final List<DeathEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            events.add(new DeathEvent(person.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(DeathEvent event) {

        // simulate if person with ID perId dies in this simulation period
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Person person = householdDataManager.getPersonFromId(event.getPersonId());
        if (person != null) {
            if (random.nextDouble() < strategy.calculateDeathProbability(person)) {
                return die(person);
            }
        }
        return false;
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {

    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {

    }

    boolean die(Person person) {
        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Household hhOfPersonToDie = person.getHousehold();
        householdDataManager.saveHouseholdMemento(hhOfPersonToDie);

        if (person.getJobId() > 0) {
            dataContainer.getJobDataManager().quitJob(true, person);
        }

        if (person.getRole() == PersonRole.MARRIED) {
            Person widow = HouseholdUtil.findMostLikelyPartner(person, hhOfPersonToDie);
            widow.setRole(PersonRole.SINGLE);
        }
        householdDataManager.removePerson(person.getId());

        final boolean onlyChildrenLeft = HouseholdUtil.checkIfNoAdultsPresent(hhOfPersonToDie);
        if (onlyChildrenLeft) {
            for (Person pp : hhOfPersonToDie.getPersons().values()) {
                if (pp.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Child " + pp.getId() + " was moved from household " + hhOfPersonToDie.getId() +
                            " to foster care as remaining child just before head of household (ID " +
                            person.getId() + ") passed away.");
                }
            }
            householdDataManager.removeHousehold(hhOfPersonToDie.getId());
        }

        if (person.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("We regret to inform that person " + person.getId() + " from household " + hhOfPersonToDie.getId() +
                    " has passed away.");
        }

        return true;
    }
}
