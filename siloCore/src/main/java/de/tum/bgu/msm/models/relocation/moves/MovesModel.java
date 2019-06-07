package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.EventModel;

/**
 * Interface to generate an application-specific implementation of the MovesModel
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */
public interface MovesModel extends EventModel<MoveEvent> {

    /**
     * Searches for a new dwelling for the given household and returns its id.
     * If no suitable dwelling is found, -1 is returned.
     */
    int searchForNewDwelling(Household household);

    /**
     * This method moves the given household from the old dwelling to the new dwelling
     * and makes sure that all necessary transactions in the datamanagers are processed.
     */
    void moveHousehold(Household hh, int idOldDD, int idNewDD);
}
