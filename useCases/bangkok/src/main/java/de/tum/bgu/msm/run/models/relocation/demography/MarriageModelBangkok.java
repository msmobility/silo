package de.tum.bgu.msm.run.models.relocation.demography;

import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.events.impls.person.MarriageEvent;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.marriage.DefaultMarriageStrategy;
import de.tum.bgu.msm.models.demography.marriage.MarriageModel;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;

import java.util.Collection;
import java.util.Random;

public class MarriageModelBangkok implements MarriageModel {
    public MarriageModelBangkok(DataContainerWithSchools dataContainer, MovesModelImpl movesModel, InOutMigration inOutMigration, CreateCarOwnershipModel carOwnershipModel, HouseholdFactory hhFactory, Properties properties, DefaultMarriageStrategy defaultMarriageStrategy, Random provideNewRandom) {

    }

    @Override
    public Collection<MarriageEvent> getEventsForCurrentYear(int year) {
        return null;
    }

    @Override
    public boolean handleEvent(MarriageEvent event) {
        return false;
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }
}
