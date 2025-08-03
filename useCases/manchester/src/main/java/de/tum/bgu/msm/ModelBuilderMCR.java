package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.MitoDataConverterMCR;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.health.*;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.health.MitoMatsimScenarioAssemblerMCR;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.SimpleMatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.ZoneConnectorManager;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModelImpl;
import de.tum.bgu.msm.models.demography.death.DeathModel;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

public class ModelBuilderMCR {


    public static ModelContainer getModelContainerForManchester(HealthDataContainerImpl dataContainer, Properties properties, Config config) {

        PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        //final BirthModelImpl birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new BirthStrategyMCR(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelMCR(dataContainer, properties, new DeathStrategyMCR(dataContainer, properties.healthData.adjustByRelativeRisk), SiloUtil.provideNewRandom());

//        MovesModelImpl movesModel = new MovesModelImpl(
//                dataContainer, properties,
//                new DefaultMovesStrategy(),
//                new SimpleCommuteHousingStrategyWithoutCarOwnership(dataContainer,
//                        properties, dataContainer.getTravelTimes(),
//                        new DwellingUtilityStrategyImpl(), new DefaultDwellingProbabilityStrategy(),
//                        new RegionUtilityStrategyImpl(), new RegionProbabilityStrategyImpl())
//                , SiloUtil.provideNewRandom());
//
//        CreateCarOwnershipModel carOwnershipModel = new CreateCarOwnershipMCR(dataContainer, new CreateCarOwnershipStrategyMCR());
//
//        DivorceModel divorceModel = new DivorceModelImpl(
//                dataContainer, movesModel, carOwnershipModel, hhFactory,
//                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());
//
//        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());
//
//        EducationModel educationModel = new EducationModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());
//
//        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());
//
//        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
//                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());
//
//        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());
//
//        ConstructionModel construction = new ConstructionModelImpl(dataContainer, ddFactory,
//                properties, new ConstructionLocationStrategyMCR(), new ConstructionDemandStrategyMCR(), SiloUtil.provideNewRandom());
//
//        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());
//
//        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy(), SiloUtil.provideNewRandom());
//
//        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteMCRImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());
//
//        InOutMigration inOutMigration = new InOutMigrationImpl(dataContainer, employmentModel, movesModel,
//                carOwnershipModel, driversLicenseModel, properties, SiloUtil.provideNewRandom());
//
//        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
//                inOutMigration, properties, new DefaultDemolitionStrategy(), SiloUtil.provideNewRandom());
//
//        MarriageModel marriageModel = new MarriageModelImpl(dataContainer, movesModel, inOutMigration,
//                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom());


        TransportModel transportModel;
        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            matsimData = new MatsimData(config, properties, ZoneConnectorManager.ZoneConnectorMethod.RANDOM, dataContainer, scenario.getNetwork());
        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MITO_MATSIM:
                MatsimScenarioAssembler delegate = new MitoMatsimScenarioAssemblerMCR(dataContainer, properties, new MitoDataConverterMCR());
                transportModel = new MatsimTransportModelMCRHealth(dataContainer, config, properties, delegate, matsimData, SiloUtil.provideNewRandom());
                break;
            case MATSIM:
                delegate = new SimpleMatsimScenarioAssembler(dataContainer, properties);
                transportModel = new MatsimTransportModelMCRHealth(dataContainer, config, properties, delegate, matsimData, SiloUtil.provideNewRandom());
                break;
            case NONE:
            default:
                transportModel = null;
        }

//        final ModelContainer modelContainer = new ModelContainer(
//                birthModel, birthdayModel,
//                deathModel, marriageModel,
//                divorceModel, driversLicenseModel,
//                educationModel, employmentModel,
//                leaveParentsModel, jobMarketUpdateModel,
//                construction, demolition, pricing, renovation,
//                constructionOverwrite, inOutMigration, movesModel, transportModel);

        final ModelContainer modelContainer = new ModelContainer(
                null, birthdayModel,
                deathModel, null,
                null, null,
                null, null,
                null, null,
                null, null, null, null,
                null, null, null, transportModel);

        //modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new SportPAModelMCR(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config));

        modelContainer.registerModelUpdateListener(new DiseaseModelMCR(dataContainer, properties, SiloUtil.provideNewRandom()));

        return modelContainer;
    }
}
