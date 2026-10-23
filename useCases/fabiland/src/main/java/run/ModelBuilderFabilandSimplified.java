package run;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.matsim.*;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.birth.BirthModel;
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
import de.tum.bgu.msm.models.realEstate.construction.*;
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
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static de.tum.bgu.msm.matsim.SimpleCommuteModeChoiceMatsimScenarioAssembler.HandlingOfRandomness;
import static de.tum.bgu.msm.matsim.ZoneConnectorManagerImpl.*;

public class ModelBuilderFabilandSimplified{

    public static ModelContainer getModelContainer(DataContainer dataContainer, Properties properties, Config config) {
        // As a base assumption, let us say that people work at "facilities", which we can imagine as Berlin-type blocks.  These
        // would have something like 20k or even more sqm.  We could now assume that everybody just stays where they are, but
        // dwellings grow or shring as necessary.  E.g. if people get children, the dwelling grows to a plausible size; and if
        // people die, the dwelling vanisches and leaves sqm for others.  We could even imagine children moving out but staying in
        // the same facility.  Also "male" partners would not move in.

        // My intuition is that the first order error is not so bad, e.g.:
        // * For every "male" partner not moving in, there will be some other "male" partner staying in the facility with having its "female" partner elsewhere.
        // * For each family not moving out when getting children there will be some other person dying and making space.
        // * Etc.

        // I think that for such an approach we can simplify even more than what I have done below.

        final PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        final HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        final DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();
        // (in most if not all cases, the dataContainer is handed over anyways.  --> add separate constructor w/o those factories;
        // set old constructor to deprecated (but do not make effort to remove).

        final BirthModel birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new DefaultBirthStrategy(), SiloUtil.provideNewRandom());

        final BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        final DeathModel deathModel = new DeathModelImpl(dataContainer, properties, new DefaultDeathStrategy(), SiloUtil.provideNewRandom());

        final MovesModel movesModel = new MovesModel(){
            @Override public int searchForNewDwelling( Household household ){
                return -1; // means no dwelling was found
            }
            @Override public void moveHousehold( Household hh, int idOldDD, int idNewDD ){
                // do nothing
            }
            @Override public Collection<MoveEvent> getEventsForCurrentYear( int year ){
                return Collections.emptyList();
            }
            @Override public boolean handleEvent( MoveEvent event ){
                // this should not happen.  Maybe test?
                // If this is needed, one cold use MovesModelImpl as a delegate and then go from there.
                return false;
            }
            @Override public void setup(){
                // do nothing
            }
            @Override public void prepareYear( int year ){
                // do nothing
            }
            @Override public void endYear( int year ){
                // do nothing
            }
            @Override public void endSimulation(){
                // do nothing
            }
        };

        final CreateCarOwnershipModel carOwnershipModel = new FabilandCarOwnership();
        // yy (for VSP purposes, car ownership could  be done by matsim)

        final DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, carOwnershipModel, hhFactory,
                properties, new DefaultDivorceStrategy(), SiloUtil.provideNewRandom());

        final DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy(), SiloUtil.provideNewRandom());
        // yy (for VSP purposes, this might not be needed)

        final EducationModel educationModel = new EducationModelImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties, SiloUtil.provideNewRandom()) {
            @Override
            public boolean lookForJob(int perId) {
                return false;   // stub this one
            }
        };

        final LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                carOwnershipModel, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy(), SiloUtil.provideNewRandom());

        final JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties, SiloUtil.provideNewRandom());

        final PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy(), SiloUtil.provideNewRandom());

        final ConstructionOverwrite constructionOverwrite = new ConstructionOverwriteImpl(dataContainer, ddFactory, properties, SiloUtil.provideNewRandom());

        final InOutMigration inOutMigration = new InOutMigrationImpl(dataContainer, employmentModel, movesModel,
                carOwnershipModel, driversLicenseModel, properties, SiloUtil.provideNewRandom());
        // (do we need this at VSP?)
        // (if we need it, the car ownership model can be null.  Presumably, also the drivers licence model.)

        final MarriageModel marriageModel = new MarriageModelImpl(dataContainer, movesModel, inOutMigration,
                carOwnershipModel, hhFactory, properties, new DefaultMarriageStrategy(), SiloUtil.provideNewRandom()) {
            @Override
            protected boolean moveTogether(Person person1, Person person2, Household moveTo) {
                return true;
            };
        };
        // (do we need this at VSP?  We could also have women have children.)

        TransportModel transportModel;
        MatsimScenarioAssembler scenarioAssembler;

        MatsimData matsimData = null;
        if (config != null) {
            final Scenario scenario = ScenarioUtils.loadScenario(config);
            //  seems to be breaking
//            matsimData = new MatsimData(config, properties, ZoneConnectorMethod.WEIGHTED_BY_POPULATION, dataContainer, scenario.getNetwork(), scenario.getTransitSchedule());
            // (only the constructor is deprecated)

            // old version
            matsimData = new MatsimData(properties, ZoneConnectorMethod.WEIGHTED_BY_POPULATION, dataContainer, (MutableScenario) scenario );

        }
        switch (properties.transportModel.transportModelIdentifier) {
            case MATSIM:
//                SimpleCommuteModeChoice commuteModeChoice = new SimpleCommuteModeChoice(dataContainer, properties, SiloUtil.provideNewRandom());
                SimpleMatsimCommuteModeChoice commuteModeChoice = new SimpleMatsimCommuteModeChoice(dataContainer, properties, SiloUtil.provideNewRandom());
                // (for VSP purposes, this might not be needed)

                scenarioAssembler = new SimpleCommuteModeChoiceMatsimScenarioAssembler(dataContainer, properties, commuteModeChoice, HandlingOfRandomness.localInstanceFromMatsimWithAlwaysSameSeed);
                transportModel = new MatsimTransportModel(dataContainer, config, properties, scenarioAssembler, matsimData);
                break;
            case NONE:
            default:
                transportModel = null;
        }

        final ModelContainer modelContainer = new ModelContainer(
                birthModel, birthdayModel,
                deathModel, marriageModel,
                divorceModel, null,
                null, employmentModel,
                null, null,
                null, null, null, null,
                null, inOutMigration, movesModel, transportModel);

        return modelContainer;
    }

    protected static class FabilandCarOwnership implements CreateCarOwnershipModel {

        private final Random random;

        FabilandCarOwnership() {
            this.random = SiloUtil.provideNewRandom();
        }

        @Override
        public void run() {

        }

        @Override
        public void simulateCarOwnership(Household hh) {
            hh.setAutos(random.nextInt(3)+1);
        }
    }
}
