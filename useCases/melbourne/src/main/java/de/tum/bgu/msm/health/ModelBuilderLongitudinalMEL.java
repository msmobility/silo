package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.MitoDataConverterMEL;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipMEL;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipStrategyMEL;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModelImpl;
import de.tum.bgu.msm.models.demography.death.DeathModel;
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
import de.tum.bgu.msm.models.demography.marriage.MarriageModelImpl;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdate;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdateImpl;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionModel;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionModelImpl;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionOverwrite;
import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModelImpl;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.moves.*;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

public class ModelBuilderLongitudinalMEL {


    public static ModelContainer getModelContainerForMelbourne(HealthDataContainerImpl dataContainer, Properties properties, Config config) {

        PersonFactoryMELHealth ppFactory = (PersonFactoryMELHealth) dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelMEL birthModel = new BirthModelMEL(dataContainer, ppFactory, properties, new BirthStrategyMEL(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelMEL(dataContainer, properties, new DeathStrategyMEL(dataContainer, properties.healthData.adjustByRelativeRisk), SiloUtil.provideNewRandom());

        MovesModelMEL movesModel = new MovesModelMEL(
                dataContainer, properties,
                new DefaultMovesStrategy(),
                new SimpleCommuteHousingStrategyWithoutCarOwnership(dataContainer,
                        properties, dataContainer.getTravelTimes(),
                        new DwellingUtilityStrategyImpl(), new DefaultDwellingProbabilityStrategy(),
                        new RegionUtilityStrategyImpl(), new RegionProbabilityStrategyImpl())
                , SiloUtil.provideNewRandom());

        CreateCarOwnershipModel carOwnershipModel = new CreateCarOwnershipMEL(dataContainer, new CreateCarOwnershipStrategyMEL());

        DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, carOwnershipModel, hhFactory,
                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());

        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());

        EducationModel educationModel = new EducationModelMEL(dataContainer, properties, SiloUtil.provideNewRandom());

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());

        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        ConstructionModel construction = new ConstructionModelImpl(dataContainer, ddFactory,
                properties, new ConstructionLocationStrategyMEL(), new ConstructionDemandStrategyMEL(), SiloUtil.provideNewRandom());

        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());

//        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy(), SiloUtil.provideNewRandom());
//
        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteMELImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        InOutMigration inOutMigration = new InOutMigrationMEL(dataContainer, employmentModel, movesModel,
                carOwnershipModel, driversLicenseModel, properties, SiloUtil.provideNewRandom());

//        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
//                inOutMigration, properties, new DefaultDemolitionStrategy(), SiloUtil.provideNewRandom());
//
        MarriageModel marriageModel = new MarriageModelImpl(dataContainer, movesModel, inOutMigration,
                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom());


        TransportModel transportModel;
        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            matsimData = new MatsimData(config, properties, ZoneConnectorManager.ZoneConnectorMethod.RANDOM, dataContainer, scenario.getNetwork());
        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MITO_MATSIM:
                MatsimScenarioAssembler delegate = new MitoMatsimScenarioAssemblerMEL(dataContainer, properties, new MitoDataConverterMEL());
                transportModel = new MatsimTransportModelMELHealth(dataContainer, config, properties, delegate, matsimData, SiloUtil.provideNewRandom());
                break;
            case MATSIM:
                delegate = new SimpleMatsimScenarioAssembler(dataContainer, properties);
                transportModel = new MatsimTransportModelMELHealth(dataContainer, config, properties, delegate, matsimData, SiloUtil.provideNewRandom());
                break;
            case NONE:
            default:
                transportModel = null;
        }

        final ModelContainer modelContainer = new ModelContainer(
                birthModel, birthdayModel,
                deathModel, marriageModel,
                divorceModel, driversLicenseModel,
                educationModel, employmentModel,
                leaveParentsModel, jobMarketUpdateModel,
                construction, null, pricing, null,
                constructionOverwrite, inOutMigration, movesModel, transportModel);


        //modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new SportPAModelMEL(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new HealthExposureModelMEL(dataContainer, properties, SiloUtil.provideNewRandom(),config));

        modelContainer.registerModelUpdateListener(new DiseaseModelMEL(dataContainer, properties, SiloUtil.provideNewRandom()));

        return modelContainer;
    }
}
