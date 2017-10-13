package de.tum.bgu.msm.relocation;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;

/**
 * Interface to generate an application-specific implementation of the MovesModelMstm
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */
public interface MovesModelI {

    void calculateAverageHousingSatisfaction (SiloModelContainer modelContainer);

    double[] updateUtilitiesOfVacantDwelling (Dwelling dd, SiloModelContainer modelContainer);

    void calculateRegionalUtilities(SiloModelContainer modelContainer);

    void chooseMove (int hhId, SiloModelContainer modelContainer, SiloDataContainer dataContainer);

    int searchForNewDwelling(Person[] persons, SiloModelContainer modelContainer);

    void moveHousehold(Household hh, int idOldDD, int idNewDD, SiloDataContainer dataContainer);
}
