package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates demolition of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class DemolitionModel {

    private double[][] demolitionProbability;

    public DemolitionModel() {
        setupDemolitionModel();
    }

    private void setupDemolitionModel() {

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));
        DemolitionJSCalculator calculator = new DemolitionJSCalculator(reader);
        // demolitionProbability["quality-1","vacant/occupied"]
        demolitionProbability = new double[4][2];
        for (int i = 1; i <= 4; i++) {
            demolitionProbability[i-1][0] = calculator.calculateDemolitionProbability(false, i);
            demolitionProbability[i-1][0] = calculator.calculateDemolitionProbability(true, i);
        }
    }

    public void checkDemolition (int dwellingId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // check if is demolished

        Dwelling dd = Dwelling.getDwellingFromId(dwellingId);
        if (!EventRules.ruleDemolishDwelling(dd)) return;  // Dwelling not available for demolition
        int quality = dd.getQuality();
        int residentId = dd.getResidentId();
        int occupied;
        if (residentId > 0) occupied = 1;
        else occupied = 0;
        if (SiloUtil.getRandomNumberAsDouble() < demolitionProbability[quality - 1][occupied]) {
            // demolish dwelling
            if (occupied == 1) {
                // dwelling is currently occupied, force household to move out
                Household hh = Household.getHouseholdFromId(residentId);
                int idNewDD = modelContainer.getMove().searchForNewDwelling(hh.getPersons(), modelContainer);
                if (idNewDD > 0) {
                    modelContainer.getMove().moveHousehold(hh, -1, idNewDD, dataContainer);  // set old dwelling ID to -1 to avoid it from being added to the vacancy list
                } else {
                    modelContainer.getIomig().outMigrateHh(residentId, true, dataContainer);
                    dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
                    IssueCounter.countLackOfDwellingForcedOutmigration();
                }
            } else {
                dataContainer.getRealEstateData().removeDwellingFromVacancyList(dwellingId);
            }
            Dwelling.removeDwelling(dwellingId);
            EventManager.countEvent(EventTypes.DD_DEMOLITION);
            if (dwellingId == SiloUtil.trackDd) SiloUtil.trackWriter.println("Dwelling " +
                    dwellingId + " was demolished.");
        }
    }
}

