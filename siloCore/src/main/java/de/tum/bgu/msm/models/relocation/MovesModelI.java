package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.events.impls.household.MoveEvent;

/**
 * Interface to generate an application-specific implementation of the MovesModel
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */
public interface MovesModelI extends EventModel<MoveEvent> {

    int searchForNewDwelling(Household household);

    void moveHousehold(Household hh, int idOldDD, int idNewDD);
}
