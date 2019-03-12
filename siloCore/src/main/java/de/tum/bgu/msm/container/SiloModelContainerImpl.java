package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.events.impls.MarriageEvent;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.events.impls.person.*;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.events.impls.realEstate.DemolitionEvent;
import de.tum.bgu.msm.events.impls.realEstate.RenovationEvent;
import de.tum.bgu.msm.models.AnnualModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.autoOwnership.UpdateCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.maryland.MaryLandUpdateCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.munich.MunichUpdateCarOwnerShipModel;
import de.tum.bgu.msm.models.autoOwnership.munich.SwitchToAutonomousVehicleModel;
import de.tum.bgu.msm.models.demography.*;
import de.tum.bgu.msm.models.jobmography.UpdateJobs;
import de.tum.bgu.msm.models.realEstate.*;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.models.relocation.mstm.MovesModelMstm;
import de.tum.bgu.msm.models.relocation.munich.MovesModelMuc;
import de.tum.bgu.msm.models.transportModel.MitoTransportModel;
import de.tum.bgu.msm.models.transportModel.TransportModelI;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTransportModel;
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
 * Once the SiloModelContainer is created using the resourceBundle, each model can be retrieved
 * using the repsective getter.  \n
 * All the models are constructed within the SiloModelContainer, removing the initialization code from the SiloModel main body
 * @see SiloModel
 */
public class SiloModelContainerImpl implements SiloModelContainer {

    private final static Logger LOGGER = Logger.getLogger(SiloModelContainerImpl.class);
    
    private final Map<Class<? extends MicroEvent>, EventModel> eventModels = new LinkedHashMap<>();
    private final List<AnnualModel> annualModels = new ArrayList<>();

    /**
     * The contructor is private, with a factory method {link {@link SiloModelContainerImpl#(SiloDataContainer, Config, Properties)}}
     * being used to encapsulate the object creation.
     *
     * @param inOutMigration
     * @param construction
     * @param ddOverwrite
     * @param renovation
     * @param demolition
     * @param prm
     * @param birth
     * @param death
     * @param marriage
     * @param divorce
     * @param leaveParentalHousehold
     * @param move
     * @param changeEmployment
     * @param educationUpdate
     * @param driversLicense
     * @param acc
     * @param updateCarOwnershipModel
     * @param updateJobs
     * @param createCarOwnershipModel
     * @param switchToAutonomousVehicleModel
     */
    private SiloModelContainerImpl(InOutMigration inOutMigration, ConstructionModel construction,
                                   ConstructionOverwrite ddOverwrite, RenovationModel renovation, DemolitionModel demolition,
                                   PricingModel prm, BirthModel birth, BirthdayModel birthday, DeathModel death, MarriageModel marriage,
                                   DivorceModel divorce, LeaveParentHhModel leaveParentalHousehold, MovesModelI move, EmploymentModel changeEmployment,
                                   EducationModel educationUpdate, DriversLicense driversLicense,
                                   Accessibility acc, UpdateCarOwnershipModel updateCarOwnershipModel, UpdateJobs updateJobs,
                                   CreateCarOwnershipModel createCarOwnershipModel, SwitchToAutonomousVehicleModel switchToAutonomousVehicleModel,
                                   TransportModelI transportModel) {
        Properties properties = Properties.get();
        if(properties.eventRules.allDemography) {
            if (properties.eventRules.birthday ) {
                eventModels.put(BirthDayEvent.class, birthday);
            }
            if(properties.eventRules.birth) {
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
            if(properties.eventRules.marriage) {
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
        if(properties.eventRules.allHhMoves) {
            eventModels.put(MoveEvent.class, move);
            if(properties.eventRules.outMigration || properties.eventRules.inmigration) {
                eventModels.put(MigrationEvent.class, inOutMigration);
            }
        }
        if(properties.eventRules.allDwellingDevelopments) {
            if(properties.eventRules.dwellingChangeQuality) {
                eventModels.put(RenovationEvent.class, renovation);
            }
            if(properties.eventRules.dwellingDemolition) {
                eventModels.put(DemolitionEvent.class, demolition);
            }
            if(properties.eventRules.dwellingConstruction) {
                eventModels.put(ConstructionEvent.class, construction);
            }
        }
        annualModels.add(updateJobs);
    }

    /**
     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each model is created sequentially, before being passed as parameters to the private constructor.
     *
     * @return A SiloModelContainer, with each model created within
     */
    public static SiloModelContainer createSiloModelContainer(SiloDataContainerImpl dataContainer, Config matsimConfig,
                                                              Properties properties) {

        TransportModelI transportModel = null;
        switch (properties.transportModel.transportModelIdentifier) {
            case MITO:
                LOGGER.info("  MITO is used as the transport model");
                transportModel = new MitoTransportModel(properties.main.baseDirectory, dataContainer, properties);
                break;
            case MATSIM:
                LOGGER.info("  MATSim is used as the transport model");
                transportModel = new MatsimTransportModel(dataContainer, matsimConfig, properties);
                break;
            case NONE:
                LOGGER.info(" No transport model is used");
        }

        Accessibility acc = new Accessibility(dataContainer);
        DeathModel death = new DeathModel(dataContainer, properties);
        BirthModel birth = new BirthModel(dataContainer, PersonUtils.getFactory(), properties);
        BirthdayModel birthday = new BirthdayModel(dataContainer, properties);

        DriversLicense driversLicense = new DriversLicense(dataContainer, properties);

        //SummarizeData.summarizeAutoOwnershipByCounty(acc, jobData);
        MovesModelI move;
        RenovationModel renov = new RenovationModel(dataContainer, properties);
        PricingModel prm = new PricingModel(dataContainer, properties);
        UpdateJobs updateJobs = new UpdateJobs(dataContainer, properties);
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(dataContainer, DwellingUtils.getFactory(), properties);

        UpdateCarOwnershipModel updateCarOwnershipModel;
        CreateCarOwnershipModel createCarOwnershipModel = null;
        SwitchToAutonomousVehicleModel switchToAutonomousVehicleModel = null;

        EducationModel educationUpdate;
        switch (Properties.get().main.implementation) {
            case MARYLAND:
                updateCarOwnershipModel = new MaryLandUpdateCarOwnershipModel(dataContainer, acc, properties);
                move = new MovesModelMstm(dataContainer, acc, properties);
                educationUpdate = new MstmEducationModelImpl(dataContainer, properties);
                break;
            case MUNICH:
                createCarOwnershipModel = new CreateCarOwnershipModel(dataContainer,
                        (GeoDataMuc) dataContainer.getGeoData());
                updateCarOwnershipModel = new MunichUpdateCarOwnerShipModel(dataContainer, properties);
                switchToAutonomousVehicleModel = new SwitchToAutonomousVehicleModel(dataContainer);
                move = new MovesModelMuc(dataContainer, acc, properties);
                educationUpdate = new MucEducationModelImpl(dataContainer, properties);
                break;
            case KAGAWA:
                createCarOwnershipModel = new CreateCarOwnershipModel(dataContainer,
                        (GeoDataMuc) dataContainer.getGeoData());
                updateCarOwnershipModel = new MunichUpdateCarOwnerShipModel(dataContainer, properties);
                switchToAutonomousVehicleModel = new SwitchToAutonomousVehicleModel(dataContainer);
                move = new MovesModelMuc(dataContainer, acc, properties);
                educationUpdate = new MucEducationModelImpl(dataContainer, properties);
                break;
            // To do: may need to replace this with Austin car ownership, moves, and education models
            case AUSTIN:
            	updateCarOwnershipModel = new MaryLandUpdateCarOwnershipModel(dataContainer, acc, properties);
            	move = new MovesModelMstm(dataContainer, acc, properties);
                educationUpdate = new MstmEducationModelImpl(dataContainer, properties);
                break;
            case PERTH:
            default:
                throw new RuntimeException("Models not defined for implementation " + Properties.get().main.implementation);
        }
        ConstructionModel cons
                = new ConstructionModel(dataContainer, move, acc, DwellingUtils.getFactory(), properties);
        EmploymentModel changeEmployment
                = new EmploymentModel(dataContainer, acc, properties);
        updateCarOwnershipModel.initialize();
        LeaveParentHhModel lph
                = new LeaveParentHhModel(dataContainer, move, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);
        InOutMigration iomig
                = new InOutMigration(dataContainer, changeEmployment, move, createCarOwnershipModel, driversLicense, properties);
        DemolitionModel demol
                = new DemolitionModel(dataContainer, move, iomig, properties);
        MarriageModel marriage
                = new DefaultMarriageModel(dataContainer, move, iomig, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);
        DivorceModel divorce
                = new DivorceModel(dataContainer, move, createCarOwnershipModel, HouseholdUtil.getFactory(), properties);

        return new SiloModelContainerImpl(iomig, cons, ddOverwrite, renov, demol,
                prm, birth, birthday, death, marriage, divorce, lph, move, changeEmployment, educationUpdate, driversLicense, acc,
                updateCarOwnershipModel, updateJobs, createCarOwnershipModel, switchToAutonomousVehicleModel, transportModel);
    }

    @Override
    public Map<Class<? extends MicroEvent>, EventModel> getEventModels() {
        return eventModels;
    }

    @Override
    public List<AnnualModel> getAnnualModels() {
        return annualModels;
    }
}
