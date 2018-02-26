package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Greg Erhardt, Rolf Moeckel
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 *
 */
public class DeathModel {

    private final HouseholdDataManager householdDataManager;
    private DeathJSCalculator calculator;

    public DeathModel(HouseholdDataManager householdDataManager) {
        this.householdDataManager = householdDataManager;
		setupDeathModel();
	}

	private void setupDeathModel() {
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMstm"));
        }
        calculator = new DeathJSCalculator(reader);
	}


	public void chooseDeath(int perId, SiloDataContainer dataContainer) {
        // simulate if person with ID perId dies in this simulation period

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleDeath(per)) return;  // Person has moved away
        int age = Math.min(per.getAge(), 100);
        int sexIndex = per.getGender();
        if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDeathProbability(age, sexIndex)) {
            if (per.getWorkplace() > 0) {
                per.quitJob(true, dataContainer.getJobData());
            }
            Household hhOfPersonToDie = per.getHh();

            if (per.getRole() == PersonRole.MARRIED) {
                Person widow = HouseholdDataManager.findMostLikelyPartner(per, hhOfPersonToDie);
                widow.setRole(PersonRole.SINGLE);
            }
            hhOfPersonToDie.removePerson(per, dataContainer);
            boolean onlyChildrenLeft = hhOfPersonToDie.checkIfOnlyChildrenRemaining();
            if (onlyChildrenLeft) {
                for (Person pp: hhOfPersonToDie.getPersons()) {
                    Person.removePerson(pp.getId());
                    if (pp.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh)
                        SiloUtil.trackWriter.println("Child " + pp.getId() + " was moved from household " + hhOfPersonToDie.getId() +
                                " to foster care as remaining child just before head of household (ID " +
                                per.getId() + ") passed away.");
                }
            }
            Person.removePerson(per.getId());
            EventManager.countEvent(EventTypes.CHECK_DEATH);
            householdDataManager.addHouseholdThatChanged(hhOfPersonToDie);
            if (perId == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh)
                SiloUtil.trackWriter.println("We regret to inform that person " + perId + " from household " + hhOfPersonToDie.getId() +
                        " has passed away.");
        }
    }
}
