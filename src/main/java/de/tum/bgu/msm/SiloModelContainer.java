package de.tum.bgu.msm;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.demography.*;
import de.tum.bgu.msm.jobmography.UpdateJobs;
import de.tum.bgu.msm.realEstate.*;
import de.tum.bgu.msm.relocation.InOutMigration;
import de.tum.bgu.msm.relocation.MovesModel;
import de.tum.bgu.msm.autoOwnership.AutoOwnershipModel;
import de.tum.bgu.msm.data.JobDataManager;
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
    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final JobDataManager jobData;
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
    private final MovesModel move;
    private final ChangeEmploymentModel changeEmployment;
    private final Accessibility acc;
    private final AutoOwnershipModel aoModel;
    private final UpdateJobs updateJobs;

    /**
     *
     * The contructor is private, with a factory method {link {@link SiloModelContainer#createSiloModelContainer(ResourceBundle)}}
     * being used to encapsulate the object creation.
     *
     *
     * @param householdData
     * @param realEstateData
     * @param jobData
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
     * @param acc
     * @param aoModel
     * @param updateJobs
     */
    private SiloModelContainer(HouseholdDataManager householdData, RealEstateDataManager realEstateData,
                               JobDataManager jobData, InOutMigration iomig, ConstructionModel cons,
                               ConstructionOverwrite ddOverwrite, RenovationModel renov, DemolitionModel demol,
                               PricingModel prm, BirthModel birth, DeathModel death, MarryDivorceModel mardiv,
                               LeaveParentHhModel lph, MovesModel move, ChangeEmploymentModel changeEmployment,
                               Accessibility acc, AutoOwnershipModel aoModel, UpdateJobs updateJobs) {
        this.householdData = householdData;
        this.realEstateData = realEstateData;
        this.jobData = jobData;
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
        this.acc = acc;
        this.aoModel = aoModel;
        this.updateJobs = updateJobs;
    }

    /**
     * This factory method is used to create all the models needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each model is created sequentially, before being passed as parameters to the private constructor.
     * @param rbLandUse The configuration file, as a @see {@link ResourceBundle}
     * @return A SiloModelContainer, with each model created within
     */
    public static SiloModelContainer createSiloModelContainer(ResourceBundle rbLandUse) {

        // read micro data
        RealEstateDataManager realEstateData = new RealEstateDataManager(rbLandUse);
        HouseholdDataManager householdData = new HouseholdDataManager(rbLandUse);
        JobDataManager jobData = new JobDataManager(rbLandUse);
        if (!ResourceUtil.getBooleanProperty(rbLandUse, "run.synth.pop.generator")) {   // read data only if synth. pop. generator did not run
            householdData.readPopulation();
            realEstateData.readDwellings();
            jobData.readJobs();
            householdData.connectPersonsToHouseholds();
            householdData.setTypeOfAllHouseholds();
        }

        jobData.updateEmploymentForecast();
        jobData.identifyVacantJobs();
        jobData.calculateJobDensityByZone();
        realEstateData.fillQualityDistribution();
        realEstateData.setHighestVariables();
        realEstateData.readLandUse();
        realEstateData.identifyVacantDwellings();
        householdData.setHighestHouseholdAndPersonId();
        householdData.calculateInitialSettings();

        logger.info("Creating UEC Models");
        DeathModel death = new DeathModel(rbLandUse);
        BirthModel birth = new BirthModel(rbLandUse);
        LeaveParentHhModel lph = new LeaveParentHhModel(rbLandUse);
        MarryDivorceModel mardiv = new MarryDivorceModel(rbLandUse);
        ChangeEmploymentModel changeEmployment = new ChangeEmploymentModel();
        Accessibility acc = new Accessibility(rbLandUse, SiloUtil.getStartYear());
//        summarizeData.summarizeAutoOwnershipByCounty();

        MovesModel move = new MovesModel(rbLandUse);
        InOutMigration iomig = new InOutMigration(rbLandUse);
        ConstructionModel cons = new ConstructionModel(rbLandUse);
        RenovationModel renov = new RenovationModel(rbLandUse);
        DemolitionModel demol = new DemolitionModel(rbLandUse);
        PricingModel prm = new PricingModel(rbLandUse);
        UpdateJobs updateJobs = new UpdateJobs(rbLandUse);
        AutoOwnershipModel aoModel = new AutoOwnershipModel(rbLandUse);
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(rbLandUse);
        updateJobs = new UpdateJobs(rbLandUse);

        return new SiloModelContainer(householdData, realEstateData, jobData, iomig, cons, ddOverwrite, renov, demol,
                prm, birth, death, mardiv, lph, move, changeEmployment, acc, aoModel, updateJobs);
    }

    public HouseholdDataManager getHouseholdData() {
        return householdData;
    }

    public RealEstateDataManager getRealEstateData() {
        return realEstateData;
    }

    public JobDataManager getJobData() {
        return jobData;
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

    public MovesModel getMove() {
        return move;
    }

    public ChangeEmploymentModel getChangeEmployment() {
        return changeEmployment;
    }

    public Accessibility getAcc() {
        return acc;
    }

    public AutoOwnershipModel getAoModel() {
        return aoModel;
    }

    public UpdateJobs getUpdateJobs() {
        return updateJobs;
    }
}
