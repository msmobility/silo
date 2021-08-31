package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.mito.MitoDataConverterMuc;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.matsim.noise.NoiseModel;
import de.tum.bgu.msm.mito.MitoMatsimScenarioAssembler;
import de.tum.bgu.msm.models.EducationModelMuc;
import de.tum.bgu.msm.models.MarriageModelMuc;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipModelMuc;
import de.tum.bgu.msm.models.carOwnership.UpdateCarOwnershipModelMuc;
import de.tum.bgu.msm.models.construction.ConstructionDemandStrategyMuc;
import de.tum.bgu.msm.models.demography.birth.BirthModelImpl;
import de.tum.bgu.msm.models.demography.birth.DefaultBirthStrategy;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModelImpl;
import de.tum.bgu.msm.models.demography.death.DeathModel;
import de.tum.bgu.msm.models.demography.death.DeathModelImpl;
import de.tum.bgu.msm.models.demography.death.DefaultDeathStrategy;
import de.tum.bgu.msm.models.demography.divorce.DefaultDivorceStrategy;
import de.tum.bgu.msm.models.demography.divorce.DivorceModel;
import de.tum.bgu.msm.models.demography.divorce.DivorceModelImpl;
import de.tum.bgu.msm.models.demography.driversLicense.DefaultDriversLicenseStrategy;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModelImpl;
import de.tum.bgu.msm.models.demography.education.EducationModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModelImpl;
import de.tum.bgu.msm.models.demography.leaveParentalHousehold.DefaultLeaveParentalHouseholdStrategy;
import de.tum.bgu.msm.models.demography.leaveParentalHousehold.LeaveParentHhModel;
import de.tum.bgu.msm.models.demography.leaveParentalHousehold.LeaveParentHhModelImpl;
import de.tum.bgu.msm.models.demography.marriage.DefaultMarriageStrategy;
import de.tum.bgu.msm.models.demography.marriage.MarriageModel;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdate;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdateImpl;
import de.tum.bgu.msm.models.modeChoice.SimpleCommuteModeChoice;
import de.tum.bgu.msm.models.realEstate.construction.*;
import de.tum.bgu.msm.models.realEstate.demolition.DefaultDemolitionStrategy;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModel;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModelImpl;
import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.renovation.DefaultRenovationStrategy;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModel;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModelImpl;
import de.tum.bgu.msm.models.relocation.DwellingUtilityStrategyImpl;
import de.tum.bgu.msm.models.relocation.HousingStrategyMuc;
import de.tum.bgu.msm.models.relocation.InOutMigrationMuc;
import de.tum.bgu.msm.models.relocation.RegionUtilityStrategyMucImpl;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.moves.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

public class ModelBuilderMucNoise {

    public static ModelContainer getModelContainerForMuc(NoiseDataContainerImpl dataContainer, Properties properties, Config config) {

        PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelImpl birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new DefaultBirthStrategy(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelImpl(dataContainer, properties, new DefaultDeathStrategy(), SiloUtil.provideNewRandom());


        HousingStrategy housingStrategyDelegate
                = new HousingStrategyMuc(dataContainer, properties,
                dataContainer.getTravelTimes(), new DefaultDwellingProbabilityStrategy(),
                new DwellingUtilityStrategyImpl(), new RegionUtilityStrategyMucImpl(),
                new RegionProbabilityStrategyImpl(), new SimpleCommuteModeChoice(dataContainer, properties,SiloUtil.getRandomObject()));

        HuntNoiseSensitiveDwellingUtilityStrategy housingStrategy
                = new HuntNoiseSensitiveDwellingUtilityStrategy(dataContainer.getTravelTimes(),
                dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(),
                housingStrategyDelegate);

        MovesModelImpl movesModel = new MovesModelImpl(
                dataContainer, properties,
                new DefaultMovesStrategy(),
                housingStrategy, SiloUtil.provideNewRandom());

        CreateCarOwnershipModel carOwnershipModel = new CreateCarOwnershipModelMuc(dataContainer);

        DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, carOwnershipModel, hhFactory,
                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());

        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());

        EducationModel educationModel = new EducationModelMuc(dataContainer, properties, SiloUtil.provideNewRandom());

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());

        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        ConstructionModel construction = new ConstructionModelImpl(dataContainer, ddFactory,
                properties, new DefaultConstructionLocationStrategy(), new ConstructionDemandStrategyMuc(), SiloUtil.provideNewRandom());


//        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());

        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy(), SiloUtil.provideNewRandom());

//        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        InOutMigration inOutMigration = new InOutMigrationMuc(dataContainer, employmentModel, movesModel,
                carOwnershipModel, driversLicenseModel, properties);

        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
                inOutMigration, properties, new DefaultDemolitionStrategy(), SiloUtil.provideNewRandom());

        MarriageModel marriageModel = new MarriageModelMuc(dataContainer, movesModel, inOutMigration,
                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom());


        MatsimTransportModel transportModel;
        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            matsimData = new MatsimData(config, properties, ZoneConnectorManager.ZoneConnectorMethod.WEIGHTED_BY_POPULATION, dataContainer, scenario.getNetwork(), scenario.getTransitSchedule());
        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MITO_MATSIM:
                MatsimScenarioAssembler delegate = new MitoMatsimScenarioAssembler(dataContainer, properties, new MitoDataConverterMuc());
                transportModel = new MatsimTransportModel(dataContainer, config, properties, delegate, matsimData);
                break;
            case MATSIM:
                delegate = new SimpleMatsimScenarioAssembler(dataContainer, properties);

                transportModel = new MatsimTransportModel(dataContainer, config, properties, delegate, matsimData);
                break;
            case NONE:
            default:
                transportModel = null;
        }
        NoiseModel noiseModel  = new NoiseModel(config, dataContainer, properties, SiloUtil.provideNewRandom(), transportModel.getMatsimData());


        PricingModel pricing = new HedonicPricingModelImpl(dataContainer, properties,
                new DefaultPricingStrategy(), SiloUtil.getRandomObject(), new NKHedonicPricingModelPredictor(dataContainer.getGeoData()),
                new OsmAccessibilityCalculator(config, properties, dataContainer.getGeoData(), matsimData.getZoneConnectorManager()));

        final ModelContainer modelContainer = new ModelContainer(
                birthModel, birthdayModel,
                deathModel, marriageModel,
                divorceModel, driversLicenseModel,
                educationModel, employmentModel,
                leaveParentsModel, jobMarketUpdateModel,
                construction, demolition, pricing, renovation,
                null, inOutMigration, movesModel, transportModel);

        modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));
        modelContainer.registerModelUpdateListener(noiseModel);

        return modelContainer;
    }
}
