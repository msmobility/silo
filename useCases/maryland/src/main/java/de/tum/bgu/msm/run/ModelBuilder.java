package de.tum.bgu.msm.run;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.models.*;
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
import de.tum.bgu.msm.models.realEstate.construction.ConstructionOverwrite;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionOverwriteImpl;
import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionDemandStrategy;
import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionLocationStrategy;
import de.tum.bgu.msm.models.realEstate.demolition.DefaultDemolitionStrategy;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModel;
import de.tum.bgu.msm.models.realEstate.demolition.DemolitionModelImpl;
import de.tum.bgu.msm.models.realEstate.pricing.DefaultPricingStrategy;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModel;
import de.tum.bgu.msm.models.realEstate.pricing.PricingModelImpl;
import de.tum.bgu.msm.models.realEstate.renovation.DefaultRenovationStrategy;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModel;
import de.tum.bgu.msm.models.realEstate.renovation.RenovationModelImpl;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.AbstractMovesModelImpl;
import de.tum.bgu.msm.models.relocation.moves.DefaultDwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.DefaultMovesStrategy;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTransportModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

public class ModelBuilder {

    private final static Logger logger = Logger.getLogger(ModelBuilder.class);

    private ModelBuilder() {}

    public static ModelContainer getModelContainerForMstm(DataContainer dataContainer, Properties properties, Config config) {

        PersonFactory ppFactory = dataContainer.getHouseholdDataManager().getPersonFactory();
        HouseholdFactory hhFactory = dataContainer.getHouseholdDataManager().getHouseholdFactory();
        DwellingFactory ddFactory = dataContainer.getRealEstateDataManager().getDwellingFactory();

        final BirthModelImpl birthModel = new BirthModelImpl(dataContainer, ppFactory, properties, new DefaultBirthStrategy());

        BirthdayModel birthdayModel = new BirthdayModelImpl(dataContainer, properties);

        DeathModel deathModel = new DeathModelImpl(dataContainer, properties, new DefaultDeathStrategy());

        AbstractMovesModelImpl movesModel = new MovesModelMstm(
                dataContainer, properties, new DefaultMovesStrategy(), new DwellingUtilityStrategyMstm(),
                new DefaultDwellingProbabilityStrategy(), new SelectRegionStrategyMstm());


        DivorceModel divorceModel = new DivorceModelImpl(
                dataContainer, movesModel, null, hhFactory,
                properties, new DefaultDivorceStrategy());

        DriversLicenseModel driversLicenseModel = new DriversLicenseModelImpl(dataContainer, properties, new DefaultDriversLicenseStrategy());

        EducationModel educationModel = new MstmEducationModelImpl(dataContainer, properties);

        EmploymentModel employmentModel = new EmploymentModelImpl(dataContainer, properties);

        LeaveParentHhModel leaveParentsModel = new LeaveParentHhModelImpl(dataContainer, movesModel,
                null, hhFactory, properties, new DefaultLeaveParentalHouseholdStrategy());

        JobMarketUpdate jobMarketUpdateModel = new JobMarketUpdateImpl(dataContainer, properties);

        ConstructionModelMstm construction = new ConstructionModelMstm(dataContainer, ddFactory,
                properties, new DefaultConstructionLocationStrategy(), new DefaultConstructionDemandStrategy());


        PricingModel pricing = new PricingModelImpl(dataContainer, properties, new DefaultPricingStrategy());

        RenovationModel renovation = new RenovationModelImpl(dataContainer, properties, new DefaultRenovationStrategy());

        ConstructionOverwriteMstm constructionOverwrite = new ConstructionOverwriteMstm(dataContainer, ddFactory, properties);

        InOutMigrationImpl inOutMigration = new InOutMigrationImpl(dataContainer, employmentModel, movesModel,
                null, driversLicenseModel, properties);

        DemolitionModel demolition = new DemolitionModelImpl(dataContainer, movesModel,
                inOutMigration, properties, new DefaultDemolitionStrategy());

        MarriageModel marriageModel = new MarriageModelMstm(dataContainer, movesModel, inOutMigration,
                null, hhFactory, properties, new DefaultMarriageStrategy());


        TransportModel transportModel;
        switch (properties.transportModel.transportModelIdentifier) {
            case MATSIM:
                transportModel = new MatsimTransportModel(dataContainer, config, properties);
                break;
            case NONE:
            case MITO:
                logger.warn("Mito not implemented for mstm. setting transport model to \"NONE\"");
            default:
                transportModel = null;

        }
        return new ModelContainer(
                birthModel, birthdayModel,
                deathModel, marriageModel,
                divorceModel, driversLicenseModel,
                educationModel, employmentModel,
                leaveParentsModel, jobMarketUpdateModel,
                construction, demolition, pricing, renovation,
                constructionOverwrite, inOutMigration, movesModel, transportModel);
    }
}
