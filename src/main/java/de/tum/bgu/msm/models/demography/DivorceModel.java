package de.tum.bgu.msm.models.demography;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.events.impls.person.DivorceEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DivorceModel extends AbstractModel implements MicroEventModel<DivorceEvent> {

    private final MovesModelI movesModel;
    private final CreateCarOwnershipModel carOwnership;

    private MarryDivorceJSCalculator calculator;

    public DivorceModel(SiloDataContainer dataContainer, MovesModelI movesModel, CreateCarOwnershipModel carOwnership) {
        super(dataContainer);
        setupModel();
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
    }

    private void setupModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        }
        calculator = new MarryDivorceJSCalculator(reader, 0);
    }

    @Override
    public Collection<Event> prepareYear(int year) {
        final List<Event> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            if (person.getRole() == PersonRole.MARRIED) {
                events.add(new DivorceEvent(person.getId()));
            }
        }
        return events;
    }

    @Override
    public EventResult handleEvent(DivorceEvent event) {
        return chooseDivorce(event.getPersonId());    }

    @Override
    public void finishYear(int year) {

    }

    private DivorceResult chooseDivorce(int perId) {
        // select if person gets divorced/leaves joint dwelling

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        Person per = householdData.getPersonFromId(perId);
        if (per != null && per.getRole() == PersonRole.MARRIED) {
            final double probability = calculator.calculateDivorceProbability(per.getType().ordinal()) / 2;
            if (SiloUtil.getRandomNumberAsDouble() < probability) {
                // check if vacant dwelling is available
                int newDwellingId = movesModel.searchForNewDwelling(Collections.singletonList(per));
                if (newDwellingId < 0) {
                    if (perId == SiloUtil.trackPp || per.getHh().getId() == SiloUtil.trackHh) {
                        SiloUtil.trackWriter.println(
                                "Person " + perId + " wanted to but could not divorce from household " + per.getHh().getId() +
                                        " because no appropriate vacant dwelling was found.");
                    }
                    IssueCounter.countLackOfDwellingFailedDivorce();
                    return null;
                }

                // divorce
                Household oldHh = per.getHh();
                Person divorcedPerson = HouseholdDataManager.findMostLikelyPartner(per, oldHh);
                divorcedPerson.setRole(PersonRole.SINGLE);
                per.setRole(PersonRole.SINGLE);
                householdData.removePersonFromHousehold(per);
                oldHh.determineHouseholdRace();
                oldHh.setType();

                int newHhId = householdData.getNextHouseholdId();
                Household newHh = householdData.createHousehold(newHhId, -1, 0);
                householdData.addPersonToHousehold(per, newHh);
                newHh.setType();
                newHh.determineHouseholdRace();
                // move divorced person into new dwelling
                movesModel.moveHousehold(newHh, -1, newDwellingId);
                if (perId == SiloUtil.trackPp || newHh.getId() == SiloUtil.trackHh ||
                        oldHh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                        " has divorced from household " + oldHh + " and established the new household " +
                        newHhId + ".");
                householdData.addHouseholdThatChanged(oldHh); // consider original household for update in car ownership
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    carOwnership.simulateCarOwnership(newHh); // set initial car ownership of new household
                }
                return new DivorceResult(perId, divorcedPerson.getId(), newHhId, oldHh.getId(), newDwellingId);
            }
        }
        return null;
    }

    public static class DivorceResult implements EventResult {

        @JsonProperty("id1")
        public final int id1;
        @JsonProperty("id2")
        public final int id2;
        @JsonProperty("hh1")
        public final int oldHH;
        @JsonProperty("hh2")
        public final int newHH;
        @JsonProperty("dd1")
        public final int dd;

        public DivorceResult(int id1, int id2, int oldHH, int newHH, int dd) {
            this.id1 = id1;
            this.id2 = id2;
            this.oldHH = oldHH;
            this.newHH = newHH;
            this.dd = dd;
        }

        @Override
        public EventType getType() {
            return EventType.DIVORCE;
        }
    }
}
