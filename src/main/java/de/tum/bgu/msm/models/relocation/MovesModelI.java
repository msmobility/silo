package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;

import java.util.List;

/**
 * Interface to generate an application-specific implementation of the MovesModel
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */
public interface MovesModelI {

    void calculateAverageHousingSatisfaction();

    double[] updateUtilitiesOfVacantDwelling (Dwelling dd);

    void calculateRegionalUtilities();

    void chooseMove (int hhId);

    int searchForNewDwelling(List<Person> persons);

    void moveHousehold(Household hh, int idOldDD, int idNewDD);
}
