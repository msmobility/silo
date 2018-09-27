package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
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
import java.util.List;

/**
 * @author Greg Erhardt, Rolf Moeckel
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 */
public class DeathModel extends AbstractModel implements MicroEventModel<DeathEvent> {

    private DeathJSCalculator calculator;

    public DeathModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        setupDeathModel();
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
        final Household hhOfPersonToDie = person.getHh();

        if (person.getRole() == PersonRole.MARRIED) {
            Person widow = HouseholdDataManager.findMostLikelyPartner(person, hhOfPersonToDie);
            widow.setRole(PersonRole.SINGLE);
        }
        householdData.removePerson(person.getId());
        householdData.addHouseholdThatChanged(hhOfPersonToDie);

        final boolean onlyChildrenLeft = hhOfPersonToDie.checkIfOnlyChildrenRemaining();
        if (onlyChildrenLeft) {
            for (Person pp : hhOfPersonToDie.getPersons()) {
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
