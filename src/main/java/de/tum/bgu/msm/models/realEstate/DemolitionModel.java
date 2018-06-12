package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.realEstate.DemolitionEvent;
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

public class DemolitionModel extends AbstractModel implements MicroEventModel<DemolitionEvent> {

    private final DemolitionJSCalculator calculator;
    private final MovesModelI moves;
    private final InOutMigration inOutMigration;

    private int currentYear = -1;

    public DemolitionModel(SiloDataContainer dataContainer, MovesModelI moves, InOutMigration inOutMigration) {
        super(dataContainer);
        this.moves = moves;
        this.inOutMigration = inOutMigration;
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));

        }
        calculator = new DemolitionJSCalculator(reader);
    }

    @Override
    public Collection<DemolitionEvent> prepareYear(int year) {
        currentYear = year;
        final List<DemolitionEvent> events = new ArrayList<>();
        for (Dwelling dwelling : dataContainer.getRealEstateData().getDwellings()) {
            events.add(new DemolitionEvent(dwelling.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(DemolitionEvent event) {
        Dwelling dd = dataContainer.getRealEstateData().getDwelling(event.getDwellingId());
        if (dd != null) {
            if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDemolitionProbability(dd, currentYear)) {
                return demolishDwelling(dd);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
    }

    private boolean demolishDwelling(Dwelling dd) {
        int dwellingId = dd.getId();
        int hhId = dd.getResidentId();
        Household hh = dataContainer.getHouseholdData().getHouseholdFromId(hhId);
        if (hh != null) {
            moveOutHousehold(dwellingId, hh);
        } else {
            dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
        }
        dataContainer.getRealEstateData().removeDwelling(dwellingId);
        if (dwellingId == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Dwelling " +
                    dwellingId + " was demolished.");
        }
        return true;
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

