package de.tum.bgu.msm.health.diseaseModelOffline;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.health.DiseaseModelMCR;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModelImpl;
import de.tum.bgu.msm.models.demography.death.DeathModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.health.DeathModelMCR;
import de.tum.bgu.msm.health.DeathStrategyMCR;
import de.tum.bgu.msm.health.HealthDataContainerImpl;
import org.matsim.core.config.Config;

public class ModelBuilderMCR {


    public static ModelContainer getModelContainerForManchester(HealthDataContainerImpl dataContainer, Properties properties, Config config) {

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelMCR(dataContainer, properties, new DeathStrategyMCR(dataContainer, false), SiloUtil.provideNewRandom());

        final ModelContainer modelContainer = new ModelContainer(
                null, birthdayModel,
                deathModel, null,
                null, null,
                null, null,
                null, null,
                null, null, null, null,
                null, null, null, null);


        modelContainer.registerModelUpdateListener(new DiseaseModelMCR(dataContainer,properties, SiloUtil.provideNewRandom()));
        return modelContainer;
    }
}
