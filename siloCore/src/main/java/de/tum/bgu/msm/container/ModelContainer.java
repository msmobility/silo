package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.events.impls.person.MarriageEvent;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.events.impls.person.*;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.events.impls.realEstate.DemolitionEvent;
import de.tum.bgu.msm.events.impls.realEstate.RenovationEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
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
import org.apache.log4j.Logger;

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

    public Map<Class<? extends MicroEvent>, EventModel> getEventModels() {
        return eventModels;
    }

    public List<ModelUpdateListener> getModelUpdateListeners() {
        return modelUpdateListeners;
    }

    public void registerModelUpdateListener(ModelUpdateListener listener) {
        modelUpdateListeners.add(listener);
    }

    public void registerEventModel(Class<? extends MicroEvent> evenType, EventModel handler) {
        eventModels.put(evenType, handler);
    }


//
//    /**
//     * The contructor is private, with a factory method {link {@link ModelContainerImpl#( DataContainer , Config, Properties)}}
//     * being used to encapsulate the object creation.
//     *
//     * @param inOutMigration
//     * @param construction
//     * @param constructionOverwrite
//     * @param renovation
//     * @param demolition
//     * @param pricing
//     * @param birth
//     * @param death
//     * @param marriage
//     * @param divorce
//     * @param leaveParentalHousehold
//     * @param move
//     * @param changeEmployment
//     * @param educationUpdate
//     * @param driversLicense
//     * @param updateCarOwnership
//     * @param jobMarketUpdate
//     * @param createCarOwnershipModel
//     * @param switchToAutonomousVehicle
//     */
//    private ModelContainer(InOutMigration inOutMigration, ConstructionModel construction,
//                           ConstructionOverwrite constructionOverwrite, RenovationModel renovation,
//                           DemolitionModel demolition, PricingModel pricing,
//                           BirthModel birth, BirthdayModel birthday,
//                           DeathModel death, MarriageModel marriage,
//                           DivorceModel divorce, LeaveParentHhModel leaveParentalHousehold,
//                           MovesModel move, EmploymentModel changeEmployment,
//                           EducationModel educationUpdate, DriversLicenseModel driversLicense,
//                           ModelUpdateListener updateCarOwnership,
//                           JobMarketUpdate jobMarketUpdate, CreateCarOwnershipModel createCarOwnershipModel,
//                           ModelUpdateListener switchToAutonomousVehicle, ModelUpdateListener transportModel) {
//        Properties properties = Properties.get();
//        if (properties.eventRules.allDemography) {
//            if (properties.eventRules.birthday) {
//                eventModels.put(BirthDayEvent.class, birthday);
//            }
//            if (properties.eventRules.birth) {
//                eventModels.put(BirthEvent.class, birth);
//            }
//            if (properties.eventRules.death) {
//                eventModels.put(DeathEvent.class, death);
//            }
//            if (properties.eventRules.leaveParentHh) {
//                eventModels.put(LeaveParentsEvent.class, leaveParentalHousehold);
//            }
//            if (properties.eventRules.divorce) {
//                eventModels.put(MarriageEvent.class, marriage);
//            }
//            if (properties.eventRules.marriage) {
//                eventModels.put(DivorceEvent.class, divorce);
//            }
//            if (properties.eventRules.schoolUniversity) {
//                eventModels.put(EducationEvent.class, educationUpdate);
//            }
//            if (properties.eventRules.driversLicense) {
//                eventModels.put(LicenseEvent.class, driversLicense);
//            }
//            if (properties.eventRules.quitJob || properties.eventRules.startNewJob) {
//                eventModels.put(EmploymentEvent.class, changeEmployment);
//            }
//        }
//        if (properties.eventRules.allHhMoves) {
//            eventModels.put(MoveEvent.class, move);
//            if (properties.eventRules.outMigration || properties.eventRules.inmigration) {
//                eventModels.put(MigrationEvent.class, inOutMigration);
//            }
//        }
//        if (properties.eventRules.allDwellingDevelopments) {
//            if (properties.eventRules.dwellingChangeQuality) {
//                eventModels.put(RenovationEvent.class, renovation);
//            }
//            if (properties.eventRules.dwellingDemolition) {
//                eventModels.put(DemolitionEvent.class, demolition);
//            }
//            if (properties.eventRules.dwellingConstruction) {
//                eventModels.put(ConstructionEvent.class, construction);
//            }
//        }
//        modelUpdateListeners.add(jobMarketUpdate);
//        modelUpdateListeners.add(transportModel);
//        modelUpdateListeners.add(constructionOverwrite);
//        modelUpdateListeners.add(pricing);
//        modelUpdateListeners.add(updateCarOwnership);
//        modelUpdateListeners.add(switchToAutonomousVehicle);
//    }
}
