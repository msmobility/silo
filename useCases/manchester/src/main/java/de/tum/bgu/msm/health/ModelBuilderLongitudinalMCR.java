package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.MitoDataConverterMCR;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipMCR;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipStrategyMCR;
import de.tum.bgu.msm.models.demography.birth.BirthModelImpl;
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
import de.tum.bgu.msm.models.demography.education.EducationModelImpl;
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
import de.tum.bgu.msm.models.realEstate.demolition.DefaultDemolitionStrategy;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModel;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModelImpl;
import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModelImpl;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.*;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

public class ModelBuilderLongitudinalMCR {


    public static ModelContainer getModelContainerForManchester(HealthDataContainerImpl dataContainer, Properties properties, Config config) {

        PersonFactoryMCRHealth ppFactory = (PersonFactoryMCRHealth) dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelMCR birthModel = new BirthModelMCR(dataContainer, ppFactory, properties, new BirthStrategyMCR(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelMCR(dataContainer, properties, new DeathStrategyMCR(dataContainer, properties.healthData.adjustByRelativeRisk), SiloUtil.provideNewRandom());

        MovesModelMCR movesModel = new MovesModelMCR(
                dataContainer, properties,
                new DefaultMovesStrategy(),
                new SimpleCommuteHousingStrategyWithoutCarOwnership(dataContainer,
                        properties, dataContainer.getTravelTimes(),
                        new DwellingUtilityStrategyImpl(), new DefaultDwellingProbabilityStrategy(),
                        new RegionUtilityStrategyImpl(), new RegionProbabilityStrategyImpl())
                , SiloUtil.provideNewRandom());

        CreateCarOwnershipModel carOwnershipModel = new CreateCarOwnershipMCR(dataContainer, new CreateCarOwnershipStrategyMCR());

        DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, carOwnershipModel, hhFactory,
                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());

        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());

        EducationModel educationModel = new EducationModelMCR(dataContainer, properties, SiloUtil.provideNewRandom());

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());

        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        ConstructionModel construction = new ConstructionModelImpl(dataContainer, ddFactory,
                properties, new ConstructionLocationStrategyMCR(), new ConstructionDemandStrategyMCR(), SiloUtil.provideNewRandom());

        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());

//        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy(), SiloUtil.provideNewRandom());
//
        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteMCRImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        InOutMigration inOutMigration = new InOutMigrationMCR(dataContainer, employmentModel, movesModel,
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

        final ModelContainer modelContainer = new ModelContainer(
                birthModel, birthdayModel,
                deathModel, marriageModel,
                divorceModel, driversLicenseModel,
                educationModel, employmentModel,
                leaveParentsModel, jobMarketUpdateModel,
                construction, null, pricing, null,
                constructionOverwrite, inOutMigration, movesModel, transportModel);


        //modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new SportPAModelMCR(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config));

        modelContainer.registerModelUpdateListener(new DiseaseModelMCR(dataContainer, properties, SiloUtil.provideNewRandom()));

        return modelContainer;
    }
}
