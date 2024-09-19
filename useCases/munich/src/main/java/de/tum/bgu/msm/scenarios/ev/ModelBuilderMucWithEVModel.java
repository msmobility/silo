package de.tum.bgu.msm.scenarios.ev;

import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.mito.*;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.events.impls.person.VehicleBirthdayEvent;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.mito.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.autoOwnership.*;
import de.tum.bgu.msm.models.carOwnership.*;
import de.tum.bgu.msm.models.construction.*;
import de.tum.bgu.msm.models.demography.birth.*;
import de.tum.bgu.msm.models.demography.birthday.*;
import de.tum.bgu.msm.models.demography.death.*;
import de.tum.bgu.msm.models.demography.divorce.*;
import de.tum.bgu.msm.models.demography.driversLicense.*;
import de.tum.bgu.msm.models.demography.education.*;
import de.tum.bgu.msm.models.demography.employment.*;
import de.tum.bgu.msm.models.demography.leaveParentalHousehold.*;
import de.tum.bgu.msm.models.demography.marriage.*;
import de.tum.bgu.msm.models.jobmography.*;
import de.tum.bgu.msm.models.modeChoice.*;
import de.tum.bgu.msm.models.realEstate.construction.*;
import de.tum.bgu.msm.models.realEstate.demolition.*;
import de.tum.bgu.msm.models.realEstate.pricing.*;
import de.tum.bgu.msm.models.realEstate.renovation.*;
import de.tum.bgu.msm.models.relocation.DwellingUtilityStrategyImpl;
import de.tum.bgu.msm.models.relocation.*;
import de.tum.bgu.msm.models.relocation.migration.*;
import de.tum.bgu.msm.models.relocation.moves.*;
import de.tum.bgu.msm.models.transportModel.*;
import de.tum.bgu.msm.properties.*;
import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.utils.*;
import org.matsim.api.core.v01.*;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;

public class ModelBuilderMucWithEVModel {

    public static ModelContainer getModelContainerForMuc(DataContainerWithSchools dataContainer, Properties properties, Config config) {

        PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelImpl birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new DefaultBirthStrategy(), SiloUtil.provideNewRandom());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());
        VehicleBirthdayAndRenovationModel vehicleBirthdayAndRenovationModel = new EVScenarioBirthdayAndRenovationModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        DeathModel deathModel = new DeathModelImpl(dataContainer, properties, new DefaultDeathStrategy(), SiloUtil.provideNewRandom());

        MovesModelImpl movesModel = new MovesModelImpl(
                dataContainer, properties,
                new DefaultMovesStrategy(),
                new HousingStrategyMuc(dataContainer,
                        properties,
                        dataContainer.getTravelTimes(),
                        new DefaultDwellingProbabilityStrategy(),
                        new DwellingUtilityStrategyImpl(),
                        new RegionUtilityStrategyMucImpl(),
                        new RegionProbabilityStrategyImpl(),
                        new SimpleCommuteModeChoice(dataContainer, properties, SiloUtil.provideNewRandom())
                ), SiloUtil.provideNewRandom());

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


        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());

        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy(), SiloUtil.provideNewRandom());

        ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        InOutMigration inOutMigration = new InOutMigrationMuc(dataContainer, employmentModel, movesModel,
                carOwnershipModel, driversLicenseModel, properties);

        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
                inOutMigration, properties, new DefaultDemolitionStrategy(), SiloUtil.provideNewRandom());

        MarriageModel marriageModel = new MarriageModelMuc(dataContainer, movesModel, inOutMigration,
                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom());


        TransportModel transportModel;
        MatsimScenarioAssembler scenarioAssembler;


        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            matsimData = new MatsimData(config, properties, ZoneConnectorManagerImpl.ZoneConnectorMethod.WEIGHTED_BY_POPULATION, dataContainer, scenario.getNetwork(), scenario.getTransitSchedule());
        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MITO_MATSIM:
                scenarioAssembler = new MitoMatsimScenarioAssembler(dataContainer, properties, new MitoDataConverterMuc());
                transportModel = new MatsimTransportModel(dataContainer, config, properties, scenarioAssembler, matsimData);
                break;
            case MATSIM:
                SimpleCommuteModeChoice commuteModeChoice = new SimpleCommuteModeChoice(dataContainer, properties, SiloUtil.provideNewRandom());
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

        modelContainer.registerEventModel(VehicleBirthdayEvent.class, vehicleBirthdayAndRenovationModel);

        modelContainer.registerModelUpdateListener(new UpdateCarOwnershipModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));

        modelContainer.registerModelUpdateListener(new SwitchToElectricVehicleModelMuc(dataContainer, properties, SiloUtil.provideNewRandom()));

        return modelContainer;
    }
}
