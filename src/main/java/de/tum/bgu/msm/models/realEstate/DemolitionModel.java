package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates demolition of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class DemolitionModel extends AbstractModel implements EventHandler, EventCreator{

    private final DemolitionJSCalculator calculator;
    private final MovesModelI moves;
    private final InOutMigration inOutMigration;

    public DemolitionModel(SiloDataContainer dataContainer, MovesModelI moves, InOutMigration inOutMigration) {
        super(dataContainer);
        this.moves = moves;
        this.inOutMigration = inOutMigration;
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));

        }
        calculator = new DemolitionJSCalculator(reader);
    }

    @Override
    public Collection<Event> createEvents(int year) {
        final List<Event> events = new ArrayList<>();
        for(Dwelling dwelling: dataContainer.getRealEstateData().getDwellings()) {
            events.add(new EventImpl(EventType.DD_DEMOLITION, dwelling.getId(), year));
        }
        return events;
    }

    @Override
    public void handleEvent(Event event) {
        if(event.getType() == EventType.DD_DEMOLITION) {
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(event.getId());
            if (dd == null) {
                return;  // Dwelling not available for demolition
            }

            if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDemolitionProbability(dd, event.getYear())) {
                demolishDwelling(dd);
            }
        }
    }

    private void demolishDwelling(Dwelling dd) {
        int dwellingId = dd.getId();
        Household hh = dataContainer.getHouseholdData().getHouseholdFromId(dd.getResidentId());
        if (hh != null) {
            moveOutHousehold(dwellingId, hh);
        } else {
            dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
        }
        dataContainer.getRealEstateData().removeDwelling(dwellingId);
        EventManager.countEvent(EventType.DD_DEMOLITION);
        if (dwellingId == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Dwelling " +
                    dwellingId + " was demolished.");
        }
    }

    private void moveOutHousehold(int dwellingId, Household hh) {
        int idNewDD = moves.searchForNewDwelling(hh.getPersons());
        if (idNewDD > 0) {
            moves.moveHousehold(hh, -1, idNewDD);  // set old dwelling ID to -1 to avoid it from being added to the vacancy list
        } else {
            inOutMigration.outMigrateHh(hh.getId(), true);
            dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
            IssueCounter.countLackOfDwellingForcedOutmigration();
        }
    }
}

