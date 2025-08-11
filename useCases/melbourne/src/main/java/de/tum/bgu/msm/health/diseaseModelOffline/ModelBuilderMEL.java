package de.tum.bgu.msm.health.diseaseModelOffline;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.health.DiseaseModelMEL;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModelImpl;
import de.tum.bgu.msm.models.demography.death.DeathModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.health.DeathModelMEL;
import de.tum.bgu.msm.health.DeathStrategyMEL;
import de.tum.bgu.msm.health.HealthDataContainerImpl;
import org.matsim.core.config.Config;

public class ModelBuilderMEL {


    public static ModelContainer getModelContainerForMelbourne(HealthDataContainerImpl dataContainer, Properties properties, Config config) {

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelMEL(dataContainer, properties, new DeathStrategyMEL(dataContainer, properties.healthData.adjustByRelativeRisk), SiloUtil.provideNewRandom());

        final ModelContainer modelContainer = new ModelContainer(
                null, birthdayModel,
                deathModel, null,
                null, null,
                null, null,
                null, null,
                null, null, null, null,
                null, null, null, null);


        modelContainer.registerModelUpdateListener(new DiseaseModelMEL(dataContainer,properties, SiloUtil.provideNewRandom()));
        return modelContainer;
    }
}
