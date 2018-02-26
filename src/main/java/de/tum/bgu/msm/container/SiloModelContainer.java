package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.models.autoOwnership.CarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.maryland.MaryLandCarOwnershipModel;
import de.tum.bgu.msm.models.autoOwnership.munich.MunichCarOwnerShipModel;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.models.demography.*;
import de.tum.bgu.msm.models.jobmography.UpdateJobs;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.models.realEstate.*;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.models.relocation.mstm.MovesModelMstm;
import de.tum.bgu.msm.models.relocation.munich.MovesModelMuc;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(SiloModelContainer.class);
    private final InOutMigration iomig;
    private final ConstructionModel cons;
    private final ConstructionOverwrite ddOverwrite;
    private final RenovationModel renov;
    private final DemolitionModel demol;
    private final PricingModel prm;
    private final BirthModel birth;
    private final DeathModel death;
    private final MarryDivorceModel mardiv;
    private final LeaveParentHhModel lph;
    private final MovesModelI move;
    private final ChangeEmploymentModel changeEmployment;
    private final ChangeSchoolUnivModel changeSchoolUniv;
    private final ChangeDriversLicense changeDriversLicense;
    private final Accessibility acc;
    private final CarOwnershipModel carOwnershipModel;
    private final UpdateJobs updateJobs;
    private final CreateCarOwnershipModel createCarOwnershipModel;

    /**
     *
     * The contructor is private, with a factory method {link {@link SiloModelContainer#createSiloModelContainer(SiloDataContainer)}}
     * being used to encapsulate the object creation.
     *
     *
     * @param iomig
     * @param cons
     * @param ddOverwrite
     * @param renov
     * @param demol
     * @param prm
     * @param birth
     * @param death
     * @param mardiv
     * @param lph
     * @param move
     * @param changeEmployment
     * @param changeSchoolUniv
     * @param changeDriversLicense
     * @param acc
     * @param carOwnershipModel
     * @param updateJobs
     * @param createCarOwnershipModel
     */
    private SiloModelContainer(InOutMigration iomig, ConstructionModel cons,
                               ConstructionOverwrite ddOverwrite, RenovationModel renov, DemolitionModel demol,
                               PricingModel prm, BirthModel birth, DeathModel death, MarryDivorceModel mardiv,
                               LeaveParentHhModel lph, MovesModelI move, ChangeEmploymentModel changeEmployment,
                               ChangeSchoolUnivModel changeSchoolUniv, ChangeDriversLicense changeDriversLicense,
                               Accessibility acc, CarOwnershipModel carOwnershipModel, UpdateJobs updateJobs, CreateCarOwnershipModel createCarOwnershipModel) {
        this.iomig = iomig;
        this.cons = cons;
        this.ddOverwrite = ddOverwrite;
        this.renov = renov;
        this.demol = demol;
        this.prm = prm;
        this.birth = birth;
        this.death = death;
        this.mardiv = mardiv;
        this.lph = lph;
        this.move = move;
        this.changeEmployment = changeEmployment;
        this.changeSchoolUniv = changeSchoolUniv;
        this.changeDriversLicense = changeDriversLicense;
        this.acc = acc;
        this.carOwnershipModel = carOwnershipModel;
        this.updateJobs = updateJobs;
        this.createCarOwnershipModel = createCarOwnershipModel;
    }

    /**
     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each model is created sequentially, before being passed as parameters to the private constructor.
     * @return A SiloModelContainer, with each model created within
     */
    public static SiloModelContainer createSiloModelContainer(SiloDataContainer dataContainer) {

        logger.info("Creating UEC Models");
        DeathModel death = new DeathModel(dataContainer.getHouseholdData());
        BirthModel birth = new BirthModel(dataContainer.getHouseholdData());
        LeaveParentHhModel lph = new LeaveParentHhModel();
        MarryDivorceModel mardiv = new MarryDivorceModel();
        ChangeEmploymentModel changeEmployment = new ChangeEmploymentModel(dataContainer.getGeoData(), dataContainer.getHouseholdData());
        ChangeSchoolUnivModel changeSchoolUniv = new ChangeSchoolUnivModel(dataContainer.getGeoData());
        ChangeDriversLicense changeDriversLicense = new ChangeDriversLicense();
        Accessibility acc = new Accessibility(dataContainer.getGeoData());
        //SummarizeData.summarizeAutoOwnershipByCounty(acc, jobData);
        MovesModelI move;
        InOutMigration iomig = new InOutMigration();
        ConstructionModel cons = new ConstructionModel(dataContainer.getGeoData());
        RenovationModel renov = new RenovationModel();
        DemolitionModel demol = new DemolitionModel();
        PricingModel prm = new PricingModel();
        UpdateJobs updateJobs = new UpdateJobs();
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite();

        CarOwnershipModel carOwnershipModel;
        CreateCarOwnershipModel createCarOwnershipModel = null;
        switch(Properties.get().main.implementation) {
            case MARYLAND:
                carOwnershipModel = new MaryLandCarOwnershipModel(dataContainer.getJobData(), acc);
                move = new MovesModelMstm((GeoDataMstm)dataContainer.getGeoData(), dataContainer.getRealEstateData(), acc);
                break;
            case MUNICH:
                createCarOwnershipModel = new CreateCarOwnershipModel();
                carOwnershipModel = new MunichCarOwnerShipModel();
                move = new MovesModelMuc(dataContainer.getGeoData(), acc);
                break;
            default:
                throw new RuntimeException("Models not defined for implementation " + Properties.get().main.implementation);
        }
        carOwnershipModel.initialize();

        return new SiloModelContainer(iomig, cons, ddOverwrite, renov, demol,
                prm, birth, death, mardiv, lph, move, changeEmployment, changeSchoolUniv, changeDriversLicense, acc,
                carOwnershipModel, updateJobs, createCarOwnershipModel);
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

    public DeathModel getDeath() {
        return death;
    }

    public MarryDivorceModel getMardiv() {
        return mardiv;
    }

    public LeaveParentHhModel getLph() {
        return lph;
    }

    public MovesModelI getMove() {
        return move;
    }

    public ChangeEmploymentModel getChangeEmployment() {
        return changeEmployment;
    }

    public ChangeSchoolUnivModel getChangeSchoolUniv() {
        return changeSchoolUniv;
    }

    public ChangeDriversLicense getChangeDriversLicense() {
        return changeDriversLicense;
    }

    public Accessibility getAcc() {
        return acc;
    }

    public CarOwnershipModel getCarOwnershipModel() {
        return carOwnershipModel;
    }

    public UpdateJobs getUpdateJobs() {
        return updateJobs;
    }

    public CreateCarOwnershipModel getCreateCarOwnershipModel(){
        if(createCarOwnershipModel != null) {
            return createCarOwnershipModel;
        } else {
            throw new NullPointerException("Create car ownership model not available. Check implementation!");
        }
    }
}
