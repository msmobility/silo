package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.events.impls.MarriageEvent;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.events.impls.person.*;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.events.impls.realEstate.DemolitionEvent;
import de.tum.bgu.msm.events.impls.realEstate.RenovationEvent;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.birth.BirthModel;
import de.tum.bgu.msm.models.demography.birthday.BirthdayModel;
import de.tum.bgu.msm.models.demography.death.DeathModel;
import de.tum.bgu.msm.models.demography.divorce.DivorceModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.education.EducationModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.demography.leaveParentalHousehold.LeaveParentHhModel;
import de.tum.bgu.msm.models.demography.marriage.MarriageModel;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdate;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionModel;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionOverwrite;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModel;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.moves.MovesModel;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author joemolloy
 * The Silo Model Container holds all the various models used by the SILO events.
 * It will eventually simplify the passing of models to events. Currently the cevents are still
 * called with the individual models, but this approach will be phased out. \n
 * Once the ModelContainer is created using the resourceBundle, each model can be retrieved
 * using the repsective getter.  \n
 * All the models are constructed within the ModelContainer, removing the initialization code from the SiloModel main body
 * @see SiloModel
 */
public class ModelContainer {

    private final static Logger logger = Logger.getLogger(ModelContainer.class);

    private final Map<Class<? extends MicroEvent>, EventModel> eventModels = new LinkedHashMap<>();
    private final List<ModelUpdateListener> modelUpdateListeners = new ArrayList<>();

    public ModelContainer(BirthModel birthModel, BirthdayModel birthdayModel,
                          DeathModel deathModel, MarriageModel marriageModel,
                          DivorceModel divorceModel, DriversLicenseModel driversLicenseModel,
                          EducationModel educationModel, EmploymentModel employmentModel,
                          LeaveParentHhModel leaveParentHhModel, JobMarketUpdate jobMarketUpdate,
                          ConstructionModel constructionModel, DemolitionModel demolitionModel,
                          PricingModel pricingModel, RenovationModel renovationModel,
                          ConstructionOverwrite constructionOverwrite, InOutMigration inOutMigration,
                          MovesModel movesModel, TransportModel transportModel) {

        eventModels.put(BirthEvent.class, birthModel);
        eventModels.put(BirthDayEvent.class, birthdayModel);
        eventModels.put(DeathEvent.class, deathModel);
        eventModels.put(MarriageEvent.class, marriageModel);
        eventModels.put(DivorceEvent.class, divorceModel);
        eventModels.put(LicenseEvent.class, driversLicenseModel);
        eventModels.put(EducationEvent.class, educationModel);
        eventModels.put(EmploymentEvent.class, employmentModel);
        eventModels.put(LeaveParentsEvent.class, leaveParentHhModel);
        eventModels.put(ConstructionEvent.class, constructionModel);
        eventModels.put(DemolitionEvent.class, demolitionModel);
        eventModels.put(RenovationEvent.class, renovationModel);
        eventModels.put(MigrationEvent.class, inOutMigration);
        eventModels.put(MoveEvent.class, movesModel);

        modelUpdateListeners.add(jobMarketUpdate);
        modelUpdateListeners.add(pricingModel);
        modelUpdateListeners.add(constructionOverwrite);
        modelUpdateListeners.add(transportModel);
    }



    /**
     * The contructor is private, with a factory method {link {@link ModelContainerImpl#( DataContainer , Config, Properties)}}
     * being used to encapsulate the object creation.
     *
     * @param inOutMigration
     * @param construction
     * @param constructionOverwrite
     * @param renovation
     * @param demolition
     * @param pricing
     * @param birth
     * @param death
     * @param marriage
     * @param divorce
     * @param leaveParentalHousehold
     * @param move
     * @param changeEmployment
     * @param educationUpdate
     * @param driversLicense
     * @param updateCarOwnership
     * @param jobMarketUpdate
     * @param createCarOwnershipModel
     * @param switchToAutonomousVehicle
     */
    private ModelContainer(InOutMigration inOutMigration, ConstructionModel construction,
                           ConstructionOverwrite constructionOverwrite, RenovationModel renovation,
                           DemolitionModel demolition, PricingModel pricing,
                           BirthModel birth, BirthdayModel birthday,
                           DeathModel death, MarriageModel marriage,
                           DivorceModel divorce, LeaveParentHhModel leaveParentalHousehold,
                           MovesModel move, EmploymentModel changeEmployment,
                           EducationModel educationUpdate, DriversLicenseModel driversLicense,
                           ModelUpdateListener updateCarOwnership,
                           JobMarketUpdate jobMarketUpdate, CreateCarOwnershipModel createCarOwnershipModel,
                           ModelUpdateListener switchToAutonomousVehicle, ModelUpdateListener transportModel) {
        Properties properties = Properties.get();
        if (properties.eventRules.allDemography) {
            if (properties.eventRules.birthday) {
                eventModels.put(BirthDayEvent.class, birthday);
            }
            if (properties.eventRules.birth) {
                eventModels.put(BirthEvent.class, birth);
            }
            if (properties.eventRules.death) {
                eventModels.put(DeathEvent.class, death);
            }
            if (properties.eventRules.leaveParentHh) {
                eventModels.put(LeaveParentsEvent.class, leaveParentalHousehold);
            }
            if (properties.eventRules.divorce) {
                eventModels.put(MarriageEvent.class, marriage);
            }
            if (properties.eventRules.marriage) {
                eventModels.put(DivorceEvent.class, divorce);
            }
            if (properties.eventRules.schoolUniversity) {
                eventModels.put(EducationEvent.class, educationUpdate);
            }
            if (properties.eventRules.driversLicense) {
                eventModels.put(LicenseEvent.class, driversLicense);
            }
            if (properties.eventRules.quitJob || properties.eventRules.startNewJob) {
                eventModels.put(EmploymentEvent.class, changeEmployment);
            }
        }
        if (properties.eventRules.allHhMoves) {
            eventModels.put(MoveEvent.class, move);
            if (properties.eventRules.outMigration || properties.eventRules.inmigration) {
                eventModels.put(MigrationEvent.class, inOutMigration);
            }
        }
        if (properties.eventRules.allDwellingDevelopments) {
            if (properties.eventRules.dwellingChangeQuality) {
                eventModels.put(RenovationEvent.class, renovation);
            }
            if (properties.eventRules.dwellingDemolition) {
                eventModels.put(DemolitionEvent.class, demolition);
            }
            if (properties.eventRules.dwellingConstruction) {
                eventModels.put(ConstructionEvent.class, construction);
            }
        }
        modelUpdateListeners.add(jobMarketUpdate);
        modelUpdateListeners.add(transportModel);
        modelUpdateListeners.add(constructionOverwrite);
        modelUpdateListeners.add(pricing);
        modelUpdateListeners.add(updateCarOwnership);
        modelUpdateListeners.add(switchToAutonomousVehicle);
    }

//    /**
//     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
//     * Each model is created sequentially, before being passed as parameters to the private constructor.
//     *
//     * @return A ModelContainer, with each model created within
//     */
//    public static ModelContainer createSiloModelContainer(DataContainer dataContainer, Config matsimConfig,
//                                                          Properties properties) {
//
//        ModelUpdateListener transportModel;
//        switch (properties.transportModel.transportModelIdentifier) {
//            case MITO:
//                LOGGER.info("  MITO is used as the transport model");
////                transportModel = new MitoTransportModel(properties.main.baseDirectory, dataContainer, properties);
//                break;
//            case MATSIM:
//                LOGGER.info("  MATSim is used as the transport model");
//                transportModel = new MatsimTransportModel(dataContainer, matsimConfig, properties);
//                break;
//            case NONE:
//            default:
//                transportModel = null;
//                LOGGER.info(" No transport model is used");
//        }
//
//
//        DeathModel death = new DeathModel(dataContainer, properties, null);
//        BirthModel birth = new BirthModel(dataContainer, PersonUtils.getFactory(), properties, null);
//        BirthdayModel birthday = new BirthdayModel(dataContainer, properties);
//
//        DriversLicenseModel driversLicense = new DriversLicenseModel(dataContainer, properties, null);
//
//        //SummarizeData.summarizeAutoOwnershipByCounty(acc, jobDataManager);
//        MovesModel move;
//        RenovationModel renov = new RenovationModel(dataContainer, properties, null);
//        PricingModel prm = new PricingModel(dataContainer, properties, null);
//        JobMarketUpdate updateJobs = new JobMarketUpdate(dataContainer, properties);
//        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(dataContainer, DwellingUtils.getFactory(), properties);
//
//        CreateCarOwnershipModel createCarOwnershipModel = null;
//
//        EducationModel educationUpdate;
//
//
////                move = new MovesModelImplMstm(dataContainer, acc, properties);
////                educationUpdate = new MstmEducationModelImpl(dataContainer, properties);
////                break;
////            case MUNICH:
////                createCarOwnershipModel = new CreateCarOwnershipModel(dataContainer,
////                         dataContainer.getGeoData());
////                updateCarOwnershipModel = new MunichUpdateCarOwnerShipModel(dataContainer, properties);
////                switchToAutonomousVehicleModel = new SwitchToAutonomousVehicleModel(dataContainer);
////                move = new MovesModelMuc(dataContainer, acc, properties);
////                educationUpdate = new MucEducationModelImpl(dataContainer, properties);
////                break;
////            case KAGAWA:
////                createCarOwnershipModel = new CreateCarOwnershipModel(dataContainer,
////                        (GeoDataMuc) dataContainer.getGeoData());
////                updateCarOwnershipModel = new MunichUpdateCarOwnerShipModel(dataContainer, properties);
////                switchToAutonomousVehicleModel = new SwitchToAutonomousVehicleModel(dataContainer);
////                move = new MovesModelMuc(dataContainer, acc, properties);
////                educationUpdate = new MucEducationModelImpl(dataContainer, properties);
////                break;
////            // To do: may need to replace this with Austin car ownership, moves, and education models
////            case AUSTIN:
////                updateCarOwnershipModel = new MaryLandUpdateCarOwnershipModel(dataContainer, acc, properties);
////                move = new MovesModelImplMstm(dataContainer, acc, properties);
////                educationUpdate = new MstmEducationModelImpl(dataContainer, properties);
////                break;
////            case PERTH:
////            default:
////                throw new RuntimeException("Models not defined for implementation " + Properties.get().main.implementation);
//////        }
////        ConstructionModel cons
////                = new ConstructionModel(dataContainer, move, acc, DwellingUtils.getFactory(), properties);
////        EmploymentModel changeEmployment
////                = new EmploymentModel(dataContainer, acc, properties);
////        updateCarOwnershipModel.initialize();
////        LeaveParentHhModel lph
////                = new LeaveParentHhModel(dataContainer, move, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);
////        InOutMigration iomig
////                = new InOutMigration(dataContainer, changeEmployment, move, createCarOwnershipModel, driversLicense, properties);
////        DemolitionModel demol
////                = new DemolitionModel(dataContainer, move, iomig, properties);
////        MarriageModel marriage
////                = new MarriageModelImpl(dataContainer, move, iomig, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);
////        DivorceModel divorce
////                = new DivorceModel(dataContainer, move, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);
//
//        return new ModelContainerImpl(iomig, cons, ddOverwrite, renov, demol,
//                prm, birth, birthday, death, marriage, divorce, lph, move, changeEmployment, educationUpdate, driversLicense, acc,
//                updateCarOwnershipModel, updateJobs, createCarOwnershipModel,  transportModel);
//    }

    public Map<Class<? extends MicroEvent>, EventModel> getEventModels() {
        return eventModels;
    }

    public List<ModelUpdateListener> getModelUpdateListeners() {
        return modelUpdateListeners;
    }
}
