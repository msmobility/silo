package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdType;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.household.MoveEvent;

import java.util.EnumMap;
import java.util.List;

/**
 * Interface to generate an application-specific implementation of the MovesModel
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */
public interface MovesModelI extends MicroEventModel<MoveEvent> {

    void calculateAverageHousingSatisfaction();

    EnumMap<HouseholdType,Double> updateUtilitiesOfVacantDwelling (Dwelling dd);

    void calculateRegionalUtilities();

    int searchForNewDwelling(List<Person> persons);

    void moveHousehold(Household hh, int idOldDD, int idNewDD);
}
