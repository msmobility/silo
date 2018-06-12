package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.maryland.MaryLandCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.munich.MunichCarOwnerShipModel;
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

/**
 * @author joemolloy
 * The Silo Model Container holds all the various models used by the SILO events.
 * It will eventually simplify the passing of models to events. Currently the cevents are still
 * called with the individual models, but this approach will be phased out. \n
 * Once the SiloModelContainer is created using the resourceBundle, each model can be retrieved
 * using the repsective getter.  \n
 * All the models are constructed within the SiloModelContainer, removing the initialization code from the SiloModel main body
 * @see SiloModel
 * @see SiloModel#initialize()
 */
public class SiloModelContainer {

    private final static Logger LOGGER = Logger.getLogger(SiloModelContainer.class);


    private final InOutMigration iomig;
    private final ConstructionModel cons;
    private final ConstructionOverwrite ddOverwrite;
    private final RenovationModel renov;
    private final DemolitionModel demol;
    private final PricingModel prm;
    private final BirthModel birth;
    private final BirthdayModel birthday;
    private final DeathModel death;
    private final MarriageModel marriage;
    private final DivorceModel divorce;
    private final LeaveParentHhModel lph;
    private final MovesModelI move;
    private final EmploymentModel changeEmployment;
    private final ChangeSchoolUnivModel changeSchoolUniv;
    private final DriversLicense driversLicense;
    private final Accessibility acc;
    private final CreateCarOwnershipModel carOwnershipModel;
    private final UpdateJobs updateJobs;
    private final de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel createCarOwnershipModel;
    private final TransportModelI transportModel;

    /**
     * The contructor is private, with a factory method {link {@link SiloModelContainer#createSiloModelContainer(SiloDataContainer, Config)}}
     * being used to encapsulate the object creation.
     *
     * @param iomig
     * @param cons
     * @param ddOverwrite
     * @param renov
     * @param demol
     * @param prm
     * @param birth
     * @param death
     * @param marriage
     * @param divorce
     * @param lph
     * @param move
     * @param changeEmployment
     * @param changeSchoolUniv
     * @param driversLicense
     * @param acc
     * @param carOwnershipModel
     * @param updateJobs
     * @param createCarOwnershipModel
     */
    private SiloModelContainer(InOutMigration iomig, ConstructionModel cons,
                               ConstructionOverwrite ddOverwrite, RenovationModel renov, DemolitionModel demol,
                               PricingModel prm, BirthModel birth, BirthdayModel birthday, DeathModel death, MarriageModel marriage,
                               DivorceModel divorce, LeaveParentHhModel lph, MovesModelI move, EmploymentModel changeEmployment,
                               ChangeSchoolUnivModel changeSchoolUniv, DriversLicense driversLicense,
                               Accessibility acc, CreateCarOwnershipModel carOwnershipModel, UpdateJobs updateJobs,
                               de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel createCarOwnershipModel, TransportModelI transportModel) {
        this.iomig = iomig;
        this.cons = cons;
        this.ddOverwrite = ddOverwrite;
        this.renov = renov;
        this.demol = demol;
        this.prm = prm;
        this.birth = birth;
        this.birthday = birthday;
        this.death = death;
        this.marriage = marriage;
        this.divorce = divorce;
        this.lph = lph;
        this.move = move;
        this.changeEmployment = changeEmployment;
        this.changeSchoolUniv = changeSchoolUniv;
        this.driversLicense = driversLicense;
        this.acc = acc;
        this.carOwnershipModel = carOwnershipModel;
        this.updateJobs = updateJobs;
        this.createCarOwnershipModel = createCarOwnershipModel;
        this.transportModel = transportModel;
    }

    /**
     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each model is created sequentially, before being passed as parameters to the private constructor.
     *
     * @return A SiloModelContainer, with each model created within
     */
    public static SiloModelContainer createSiloModelContainer(SiloDataContainer dataContainer, Config matsimConfig) {

        boolean runMatsim = Properties.get().transportModel.runMatsim;
        boolean runTravelDemandModel = Properties.get().transportModel.runTravelDemandModel;

        TravelTimes travelTimes;
        TransportModelI transportModel;
        if (runMatsim && (runTravelDemandModel || Properties.get().main.createMstmOutput)) {
            throw new RuntimeException("trying to run both MATSim and MSTM is inconsistent at this point.");
        }
        if (runMatsim) {
            LOGGER.info("  MATSim is used as the transport model");
            MatsimTransportModel tmpModel = new MatsimTransportModel(dataContainer, matsimConfig);
            transportModel = tmpModel ;
            travelTimes = tmpModel.getTravelTimes() ;
        } else {
            travelTimes = new SkimTravelTimes();
            if (runTravelDemandModel) {
                LOGGER.info("  MITO is used as the transport model");
                transportModel = new MitoTransportModel(Properties.get().main.baseDirectory, dataContainer, travelTimes);
            } else {
                LOGGER.info(" No transport model is used");
                transportModel = null;
            }
        }

        Accessibility acc = new Accessibility(dataContainer, travelTimes);

        DeathModel death = new DeathModel(dataContainer);
        BirthModel birth = new BirthModel(dataContainer);
        BirthdayModel birthday = new BirthdayModel(dataContainer);
        ChangeSchoolUnivModel changeSchoolUniv = new ChangeSchoolUnivModel(dataContainer);
        DriversLicense driversLicense = new DriversLicense(dataContainer);

        //SummarizeData.summarizeAutoOwnershipByCounty(acc, jobData);
        MovesModelI move;
        RenovationModel renov = new RenovationModel(dataContainer);
        PricingModel prm = new PricingModel(dataContainer);
        UpdateJobs updateJobs = new UpdateJobs(dataContainer);
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(dataContainer);

        CreateCarOwnershipModel carOwnershipModel;
        de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel createCarOwnershipModel = null;
        switch (Properties.get().main.implementation) {
            case MARYLAND:
                carOwnershipModel = new MaryLandCarOwnershipModel(dataContainer, acc);
                move = new MovesModelMstm(dataContainer, acc);
                break;
            case MUNICH:
                createCarOwnershipModel = new de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel(dataContainer);
                carOwnershipModel = new MunichCarOwnerShipModel(dataContainer);
                move = new MovesModelMuc(dataContainer, acc);
                break;
            default:
                throw new RuntimeException("Models not defined for implementation " + Properties.get().main.implementation);
        }
        ConstructionModel cons = new ConstructionModel(dataContainer, move, acc);
        EmploymentModel changeEmployment = new EmploymentModel(dataContainer, acc);
        carOwnershipModel.initialize();
        LeaveParentHhModel lph = new LeaveParentHhModel(dataContainer, move, createCarOwnershipModel);
        InOutMigration iomig = new InOutMigration(dataContainer, changeEmployment, move, createCarOwnershipModel, driversLicense);
        DemolitionModel demol = new DemolitionModel(dataContainer, move, iomig);
        MarriageModel marriage = new DefaultMarriageModel(dataContainer, move, iomig, createCarOwnershipModel);
//        MarriageModel marriage = new DeferredAcceptanceMarriageModel(dataContainer, acc);
        DivorceModel divorce = new DivorceModel(dataContainer, move, createCarOwnershipModel);

        return new SiloModelContainer(iomig, cons, ddOverwrite, renov, demol,
                prm, birth, birthday, death, marriage, divorce, lph, move, changeEmployment, changeSchoolUniv, driversLicense, acc,
                carOwnershipModel, updateJobs, createCarOwnershipModel, transportModel);
    }


    public InOutMigration getIomig() {
        return iomig;
    }

    public ConstructionModel getCons() {
        return cons;
    }

    public ConstructionOverwrite getDdOverwrite() {
        return ddOverwrite;
    }

    public RenovationModel getRenov() {
        return renov;
    }

    public DemolitionModel getDemol() {
        return demol;
    }

    public PricingModel getPrm() {
        return prm;
    }

    public BirthModel getBirth() {
        return birth;
    }

    public BirthdayModel getBirthday() {
        return birthday;
    }

    public DeathModel getDeath() {
        return death;
    }

    public MarriageModel getMarriage() {
        return marriage;
    }

    public DivorceModel getDivorce() {
        return divorce;
    }

    public LeaveParentHhModel getLph() {
        return lph;
    }

    public MovesModelI getMove() {
        return move;
    }

    public EmploymentModel getEmployment() {
        return changeEmployment;
    }

    public ChangeSchoolUnivModel getChangeSchoolUniv() {
        return changeSchoolUniv;
    }

    public DriversLicense getDriversLicense() {
        return driversLicense;
    }

    public Accessibility getAcc() {
        return acc;
    }

    public CreateCarOwnershipModel getCarOwnershipModel() {
        return carOwnershipModel;
    }

    public UpdateJobs getUpdateJobs() {
        return updateJobs;
    }

    public TransportModelI getTransportModel() {
        return this.transportModel;
    }

    public de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel getCreateCarOwnershipModel() {
        if (createCarOwnershipModel != null) {
            return createCarOwnershipModel;
        } else {
            throw new NullPointerException("Create car ownership model not available. Check implementation!");
        }
    }
}
