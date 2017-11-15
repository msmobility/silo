package de.tum.bgu.msm.container;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloModel.Implementation;
import de.tum.bgu.msm.autoOwnership.CarOwnershipModel;
import de.tum.bgu.msm.autoOwnership.maryland.MaryLandCarOwnershipModel;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.demography.*;
import de.tum.bgu.msm.jobmography.UpdateJobs;
import de.tum.bgu.msm.realEstate.*;
import de.tum.bgu.msm.relocation.InOutMigration;
import de.tum.bgu.msm.relocation.MovesModelMstm;
import de.tum.bgu.msm.relocation.MovesModelI;
import de.tum.bgu.msm.relocation.MovesModelMuc;
import de.tum.bgu.msm.autoOwnership.munich.MunichCarOwnerShipModel;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

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

    /**
     *
     * The contructor is private, with a factory method {link {@link SiloModelContainer#createSiloModelContainer(ResourceBundle, Implementation, SiloDataContainer)}}
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
     */
    private SiloModelContainer(InOutMigration iomig, ConstructionModel cons,
                               ConstructionOverwrite ddOverwrite, RenovationModel renov, DemolitionModel demol,
                               PricingModel prm, BirthModel birth, DeathModel death, MarryDivorceModel mardiv,
                               LeaveParentHhModel lph, MovesModelI move, ChangeEmploymentModel changeEmployment,
                               ChangeSchoolUnivModel changeSchoolUniv, ChangeDriversLicense changeDriversLicense,
                               Accessibility acc, CarOwnershipModel carOwnershipModel, UpdateJobs updateJobs) {
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
    }

    /**
     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each model is created sequentially, before being passed as parameters to the private constructor.
     * @param rbLandUse The configuration file, as a @see {@link ResourceBundle}
     * @return A SiloModelContainer, with each model created within
     */
    public static SiloModelContainer createSiloModelContainer(ResourceBundle rbLandUse, Implementation implementation, SiloDataContainer dataContainer) {

        logger.info("Creating UEC Models");
        DeathModel death = new DeathModel(rbLandUse, dataContainer.getHouseholdData());
        BirthModel birth = new BirthModel(rbLandUse, dataContainer.getHouseholdData());
        LeaveParentHhModel lph = new LeaveParentHhModel(rbLandUse);
        MarryDivorceModel mardiv = new MarryDivorceModel(rbLandUse);
        ChangeEmploymentModel changeEmployment = new ChangeEmploymentModel(dataContainer.getGeoData(), dataContainer.getHouseholdData());
        ChangeSchoolUnivModel changeSchoolUniv = new ChangeSchoolUnivModel(dataContainer.getGeoData());
        ChangeDriversLicense changeDriversLicense = new ChangeDriversLicense();
        Accessibility acc = new Accessibility(rbLandUse, dataContainer.getGeoData());
        //summarizeData.summarizeAutoOwnershipByCounty(acc, jobData);
        MovesModelI move;
        if (implementation.equals(Implementation.MSTM)) {
            move = new MovesModelMstm(rbLandUse, dataContainer.getGeoData());
        } else {
            move = new MovesModelMuc(rbLandUse, dataContainer.getGeoData());
        }
        InOutMigration iomig = new InOutMigration(rbLandUse);
        ConstructionModel cons = new ConstructionModel(rbLandUse, dataContainer.getGeoData());
        RenovationModel renov = new RenovationModel(rbLandUse);
        DemolitionModel demol = new DemolitionModel(rbLandUse);
        PricingModel prm = new PricingModel(rbLandUse);
        UpdateJobs updateJobs = new UpdateJobs(rbLandUse);
        CarOwnershipModel carOwnershipModel;
        if(implementation.equals(Implementation.MSTM)) {
            carOwnershipModel = new MaryLandCarOwnershipModel(rbLandUse,  dataContainer.getJobData(), acc);
        }  else {
            carOwnershipModel = new MunichCarOwnerShipModel(rbLandUse);
        }
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(rbLandUse);

        return new SiloModelContainer(iomig, cons, ddOverwrite, renov, demol,
                prm, birth, death, mardiv, lph, move, changeEmployment, changeSchoolUniv, changeDriversLicense, acc,
                carOwnershipModel, updateJobs);
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

}
