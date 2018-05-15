package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates demolition of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class DemolitionModel extends AbstractModel {

    private final DemolitionJSCalculator calculator;

    public DemolitionModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));

        }
        calculator = new DemolitionJSCalculator(reader);
    }

    public void checkDemolition (int dwellingId, SiloModelContainer modelContainer, int year) {

        Dwelling dd = dataContainer.getRealEstateData().getDwelling(dwellingId);
        if (!EventRules.ruleDemolishDwelling(dd)) {
            return;  // Dwelling not available for demolition
        }

        if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDemolitionProbability(dd, year)) {
            demolishDwelling(dwellingId, modelContainer, dd);
        }
    }

    private void demolishDwelling(int dwellingId, SiloModelContainer modelContainer, Dwelling dd) {
        Household hh = dataContainer.getHouseholdData().getHouseholdFromId(dd.getResidentId());
        if (hh != null) {
            moveOutHousehold(dwellingId, modelContainer, dataContainer, hh);
        } else {
            dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
        }
        dataContainer.getRealEstateData().removeDwelling(dwellingId);
        EventManager.countEvent(EventTypes.DD_DEMOLITION);
        if (dwellingId == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Dwelling " +
                    dwellingId + " was demolished.");
        }
    }

    private void moveOutHousehold(int dwellingId, SiloModelContainer modelContainer, SiloDataContainer dataContainer, Household hh) {
        int idNewDD = modelContainer.getMove().searchForNewDwelling(hh.getPersons());
        if (idNewDD > 0) {
            modelContainer.getMove().moveHousehold(hh, -1, idNewDD);  // set old dwelling ID to -1 to avoid it from being added to the vacancy list
        } else {
            modelContainer.getIomig().outMigrateHh(hh.getId(), true);
            dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
            IssueCounter.countLackOfDwellingForcedOutmigration();
        }
    }
}

