package de.tum.bgu.msm.run;

import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.models.demography.education.EducationModelImpl;
import de.tum.bgu.msm.models.demography.marriage.MarriageModelImpl;
import de.tum.bgu.msm.models.modeChoice.CommuteModeChoiceWithoutCarOwnership;
import de.tum.bgu.msm.models.modeChoice.SimpleCommuteModeChoice;
import de.tum.bgu.msm.run.models.carOwnership.CreateCarOwnershipBangkok;
import de.tum.bgu.msm.run.models.realEstate.BangkokPricingStrategy;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.*;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
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
import de.tum.bgu.msm.models.realEstate.construction.*;
import de.tum.bgu.msm.models.realEstate.demolition.DefaultDemolitionStrategy;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModel;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModelImpl;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModelImpl;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModel;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModelImpl;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.run.models.realEstate.RenovationStrategyBangkok;
import de.tum.bgu.msm.run.models.realEstate.construction.BangkokConstructionLocationStrategy;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;

public class ModelBuilderBangkok {

//    private static float bTime = 10f;
//    private static float bPt = 3f;



    public static ModelContainer getModelContainerForBangkok(DefaultDataContainer dataContainer, Properties properties, Config config) {

        PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelImpl birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new DefaultBirthStrategy(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelImpl(dataContainer, properties, new DefaultDeathStrategy(), SiloUtil.provideNewRandom());

        MovesModelImpl movesModel = new MovesModelImpl(
                dataContainer, properties,
                new DefaultMovesStrategy(),
                new SimpleCommuteHousingStrategyWithoutCarOwnership(dataContainer,
                        properties, dataContainer.getTravelTimes(),
                        new DwellingUtilityStrategyImpl(), new DefaultDwellingProbabilityStrategy(),
                        new RegionUtilityStrategyImpl(), new RegionProbabilityStrategyImpl())
                , SiloUtil.provideNewRandom());

        CreateCarOwnershipModel carOwnershipModel = new CreateCarOwnershipBangkok(dataContainer);

        DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, carOwnershipModel, hhFactory,
                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());

        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());

        EducationModel educationModel = new EducationModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());

        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        ConstructionModel construction = new ConstructionModelImpl(dataContainer, ddFactory,
                properties, new BangkokConstructionLocationStrategy(), new DefaultConstructionDemandStrategy(), SiloUtil.provideNewRandom());


        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new BangkokPricingStrategy(), SiloUtil.provideNewRandom());

        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new RenovationStrategyBangkok(), SiloUtil.provideNewRandom());

        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        InOutMigration inOutMigration = new InOutMigrationImpl(dataContainer, employmentModel, movesModel,
                carOwnershipModel, driversLicenseModel, properties, SiloUtil.provideNewRandom());

        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
                inOutMigration, properties, new DefaultDemolitionStrategy(), SiloUtil.provideNewRandom());

        MarriageModel marriageModel = new MarriageModelImpl(dataContainer, movesModel, inOutMigration,
                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom());


        TransportModel transportModel = null;
        MatsimScenarioAssembler scenarioAssembler;


        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            matsimData = new MatsimData(config, properties, ZoneConnectorManager.ZoneConnectorMethod.RANDOM, dataContainer, scenario.getNetwork(), scenario.getTransitSchedule());
        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MATSIM:
                CommuteModeChoiceWithoutCarOwnership commuteModeChoice = new CommuteModeChoiceWithoutCarOwnership(dataContainer,
                        properties, SiloUtil.provideNewRandom());
                scenarioAssembler = new SimpleCommuteModeChoiceMatsimScenarioAssembler(dataContainer, properties, commuteModeChoice);
                transportModel = new MatsimTransportModel(dataContainer, config, properties, scenarioAssembler, matsimData);
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
                construction, demolition, pricing, renovation,
                constructionOverwrite, inOutMigration, movesModel, transportModel);

        //modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));
        //test to print MATSim plans only
//        CommuteModeChoiceWithoutCarOwnership commuteModeChoice = new CommuteModeChoiceWithoutCarOwnership(dataContainer, properties, SiloUtil.provideNewRandom(), K_MC_CALIBRATION_PT);
//        scenarioAssembler = new SimpleCommuteModeChoiceMatsimScenarioAssembler(dataContainer, properties, commuteModeChoice);
//        modelContainer.registerModelUpdateListener(new MatsimPopulationConeversorAndWriter(dataContainer, scenarioAssembler, properties, config));

        return modelContainer;
    }
}
