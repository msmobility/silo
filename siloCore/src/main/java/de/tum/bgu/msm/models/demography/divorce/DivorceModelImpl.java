package de.tum.bgu.msm.models.demography.divorce;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.impls.person.DivorceEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DivorceModelImpl extends AbstractModel implements DivorceModel {

    private final static Logger logger = Logger.getLogger(DivorceModelImpl.class);

    private final MovesModelImpl movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final DivorceStrategy strategy;
    private final HouseholdFactory hhFactory;
    private int lackOfDwellingFailedDivorce;

    public DivorceModelImpl(DataContainer dataContainer, MovesModelImpl movesModel,
                            CreateCarOwnershipModel carOwnership, HouseholdFactory hhFactory,
                            Properties properties, DivorceStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.hhFactory = hhFactory;
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
        this.strategy = strategy;
    }


    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
        lackOfDwellingFailedDivorce = 0;
    }

    @Override
    public Collection<DivorceEvent> getEventsForCurrentYear(int year) {
        final List<DivorceEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            if (person.getRole() == PersonRole.MARRIED) {
                events.add(new DivorceEvent(person.getId()));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(DivorceEvent event) {
        return chooseDivorce(event.getPersonId());
    }

    @Override
    public void endYear(int year) {
        if (lackOfDwellingFailedDivorce > 0) {
            logger.warn("  Encountered " + lackOfDwellingFailedDivorce + " cases where " +
                    "couple wanted to get divorced but could not find vacant dwelling.");
        }
    }

    @Override
    public void endSimulation() {

    }

    private boolean chooseDivorce(int perId) {
        // select if person gets divorced/leaves joint dwelling

        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        Person per = householdDataManager.getPersonFromId(perId);
        if (per != null && per.getRole() == PersonRole.MARRIED) {
            final double probability = strategy.calculateDivorceProbability(per) / 2;
            if (random.nextDouble() < probability) {
                // check if vacant dwelling is available

                Household fakeHypotheticalHousehold = hhFactory.createHousehold(-1, -1, 0);
                fakeHypotheticalHousehold.addPerson(per);
                int newDwellingId = movesModel.searchForNewDwelling(fakeHypotheticalHousehold);
                if (newDwellingId < 0) {
                    if (perId == SiloUtil.trackPp || per.getHousehold().getId() == SiloUtil.trackHh) {
                        SiloUtil.trackWriter.println(
                                "Person " + perId + " wanted to but could not divorce from household "
                                        + per.getHousehold().getId() + " because no appropriate vacant dwelling was found.");
                    }
                    lackOfDwellingFailedDivorce++;
                    return false;
                }

                // divorce
                Household oldHh = householdDataManager.getHouseholdFromId(per.getHousehold().getId());
                householdDataManager.saveHouseholdMemento(oldHh);
                Person divorcedPerson = HouseholdUtil.findMostLikelyPartner(per, oldHh);
                divorcedPerson.setRole(PersonRole.SINGLE);
                per.setRole(PersonRole.SINGLE);
                householdDataManager.removePersonFromHousehold(per);

                int newHhId = householdDataManager.getNextHouseholdId();
                Household newHh = hhFactory.createHousehold(newHhId, -1, 0);
                householdDataManager.addHousehold(newHh);
                householdDataManager.addPersonToHousehold(per, newHh);

                // move divorced person into new dwelling
                movesModel.moveHousehold(newHh, -1, newDwellingId);
                if (perId == SiloUtil.trackPp || newHh.getId() == SiloUtil.trackHh ||
                        oldHh.getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Person " + perId +
                            " has divorced from household " + oldHh + " and established the new household " +
                            newHhId + ".");
                }
                if (carOwnership != null) {
                    carOwnership.simulateCarOwnership(newHh); // set initial car ownership of new household
                }
                return true;
            }
        }
        return false;
    }
}
