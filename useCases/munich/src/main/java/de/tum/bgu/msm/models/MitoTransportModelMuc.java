package de.tum.bgu.msm.models;

import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.MunichZone;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.jobTypes.munich.MunichJobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.data.school.SchoolImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.TripRouterFactoryBuilderWithDefaults;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import javax.inject.Provider;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of Transport Model Interface for MITO
 *
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public final class MitoTransportModelMuc extends AbstractModel implements TransportModel {

    private static final Logger logger = Logger.getLogger(MitoTransportModelMuc.class);
    private TravelTimes travelTimes;
    private final String propertiesPath;
    private final String baseDirectory;
    private final Config config;

    private TravelTimes mitoInputTravelTimes;

    public MitoTransportModelMuc(String baseDirectory, DataContainer dataContainer, Properties properties, Config config) {
        super(dataContainer, properties);
        this.travelTimes = Objects.requireNonNull(dataContainer.getTravelTimes());
        this.propertiesPath = Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath);
        this.baseDirectory = Objects.requireNonNull(baseDirectory);
        this.config = config;
    }

    @Override
    public void setup() {
        if(travelTimes instanceof MatsimTravelTimes) {
            logger.warn("Using mito with matsim travel times.");
            mitoInputTravelTimes = new SkimTravelTimes();
            TravelTimeUtil.updateCarSkim((SkimTravelTimes) mitoInputTravelTimes, properties.main.startYear, properties);
            Scenario scenario = ScenarioUtils.loadScenario(config);
            Network network = scenario.getNetwork();
            TransitSchedule schedule = null;
            if(config.transit().isUseTransit()) {
                schedule = scenario.getTransitSchedule();
            }
//            Network transitNetwork = NetworkUtils.createNetwork();
//            new MatsimNetworkReader(transitNetwork).readFile("C:/Users/Nico/IdeaProjects/silo/useCases/munich/test/muc/matsim_input/network/05/transit_Only_network2018.xml");

            ((MatsimTravelTimes)travelTimes).initialize(dataContainer.getGeoData(), network, schedule);
            if (properties.transportModel.matsimInitialEventsFile == null) {
                runTransportModel(properties.main.startYear);
            } else {
                logger.warn("Using initial events file to populate travel times for initial year!");
                String eventsFile = properties.main.baseDirectory + properties.transportModel.matsimInitialEventsFile;
                replayFromEvents(eventsFile);
            }
        } else {
            logger.warn("Using mito with skim travel times.");
            mitoInputTravelTimes = travelTimes;
        }
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void endYear(int year) {
        if (properties.transportModel.transportModelYears.contains(year + 1)) {
            runTransportModel(year + 1);
        }
    }

    @Override
    public void endSimulation() {

    }

    public void runTransportModel(int year) {
        logger.info("  Running travel demand model MITO for the year " + year);
        DataSet dataSet = convertData(year);
        logger.info("  SILO data being sent to MITO");
        MitoModel mito = MitoModel.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.setBaseDirectory(baseDirectory);
        mito.runModel();
        if(travelTimes instanceof MatsimTravelTimes) {
            final Controler controler = mito.getData().getMatsimControler();
            TravelTime matsimTravelTime = controler.getLinkTravelTimes();
            final TravelDisutility matsimDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(matsimTravelTime);
            ((MatsimTravelTimes) travelTimes).update(controler.getTripRouterProvider(), matsimTravelTime, matsimDisutility);
        } else if (travelTimes instanceof SkimTravelTimes) {
            ((SkimTravelTimes) travelTimes).updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
            if(config.transit().isUseTransit()) {
                ((SkimTravelTimes) travelTimes).updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.pt), TransportMode.pt);
            }
            ((SkimTravelTimes) travelTimes).updateRegionalTravelTimes(dataContainer.getGeoData().getRegions().values(),
                    dataContainer.getGeoData().getZones().values());
        }
    }

    private DataSet convertData(int year) {
        DataSet dataSet = new DataSet();
        convertZones(dataSet);
        fillMitoZoneEmployees(dataSet);
        convertSchools(dataSet);
        convertHhs(dataSet);
        dataSet.setTravelTimes(mitoInputTravelTimes);
        dataSet.setYear(year);
        return dataSet;
    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        Scenario scenario = ScenarioUtils.loadScenario(config);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        TripRouterFactoryBuilderWithDefaults builder = new TripRouterFactoryBuilderWithDefaults();
        builder.setLeastCostPathCalculatorFactory(new FastAStarLandmarksFactory(properties.main.numberOfThreads));
        builder.setTravelTime(travelTime);
        builder.setTravelDisutility(travelDisutility);
        final Provider<TripRouter> routerProvider = builder.build(scenario);
        ((MatsimTravelTimes)travelTimes).update(routerProvider, travelTime, travelDisutility);
    }

    private void convertZones(DataSet dataSet) {
        for (Zone siloZone : dataContainer.getGeoData().getZones().values()) {
            MitoZone zone = new MitoZone(siloZone.getZoneId(), ((MunichZone) siloZone).getAreaType());
            zone.setShapeFeature(siloZone.getZoneFeature());
            dataSet.addZone(zone);
        }
    }

    private void convertSchools(DataSet dataSet) {
        Map<Integer, MitoZone> zones = dataSet.getZones();
        for (School school : ((DataContainerMuc)dataContainer).getSchoolData().getSchools()) {
            MitoZone zone = zones.get(school.getZoneId());
            Coordinate coordinate;
            if (school instanceof SchoolImpl) {
                coordinate = ((SchoolImpl) school).getCoordinate();
            } else {
                coordinate = zone.getRandomCoord();
            }
            MitoSchool mitoSchool = new MitoSchool(zones.get(school.getZoneId()), coordinate, school.getId());
            zone.addSchoolEnrollment(school.getOccupancy());
            dataSet.addSchool(mitoSchool);
        }
    }

    private void convertHhs(DataSet dataSet) {
        Map<Integer, MitoZone> zones = dataSet.getZones();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        int householdsSkipped = 0;
        int randomCoordCounter = 0;
        for (Household siloHousehold : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zoneId = -1;
            Dwelling dwelling = realEstateDataManager.getDwelling(siloHousehold.getDwellingId());
            if (dwelling != null) {
                zoneId = dwelling.getZoneId();

            }
            MitoZone zone = zones.get(zoneId);

            MitoHousehold household = new MitoHousehold(
                    siloHousehold.getId(),
                    HouseholdUtil.getHhIncome(siloHousehold) / 12,
                    siloHousehold.getAutos(), zone);

            Coordinate coordinate;
            if (dwelling.getCoordinate() != null) {
                coordinate = dwelling.getCoordinate();
            } else {
                randomCoordCounter++;
                coordinate = zone.getRandomCoord();
            }

            //todo if there are housholds without adults they cannot be processed
            if (siloHousehold.getPersons().values().stream().anyMatch(p -> p.getAge() >= 18)) {
                household.setHomeLocation(coordinate);
                zone.addHousehold();
                dataSet.addHousehold(household);
                for (Person person : siloHousehold.getPersons().values()) {
                    MitoPerson mitoPerson = convertToMitoPp((PersonMuc) person, dataSet);
                    household.addPerson(mitoPerson);
                    dataSet.addPerson(mitoPerson);
                }
            } else {
                householdsSkipped++;
            }
        }
        logger.warn("There are " + randomCoordCounter + " households that were assigned a random coord inside their zone (" +
                randomCoordCounter / dataContainer.getHouseholdDataManager().getHouseholds().size() * 100 + "%)");
        logger.warn("There are " + householdsSkipped + " households without adults that CANNOT be processed in MITO (" +
                householdsSkipped / dataContainer.getHouseholdDataManager().getHouseholds().size() * 100 + "%)");
    }


    private MitoPerson convertToMitoPp(PersonMuc person, DataSet dataSet) {
        final MitoGender mitoGender = MitoGender.valueOf(person.getGender().name());
        final MitoOccupationStatus mitoOccupationStatus = MitoOccupationStatus.valueOf(person.getOccupation().getCode());

        MitoOccupation mitoOccupation = null;
        switch (mitoOccupationStatus) {
            case WORKER:
                if (person.getJobId() > 0) {
                    Job job = dataContainer.getJobDataManager().getJobFromId(person.getJobId());
                    MitoZone zone = dataSet.getZones().get(job.getZoneId());
                    final Coordinate coordinate;
                    if (job instanceof MicroLocation) {
                        coordinate = job.getCoordinate();
                    } else {
                        coordinate = zone.getRandomCoord();
                    }
                    mitoOccupation = new MitoJob(zone, coordinate, job.getId());
                }
                break;
            case UNEMPLOYED:
                break;
            case STUDENT:
                if (person.getSchoolId() > 0) {
                    mitoOccupation = dataSet.getSchools().get(person.getSchoolId());
                }
                break;
        }

        return new MitoPerson(
                person.getId(),
                mitoOccupationStatus,
                mitoOccupation,
                person.getAge(),
                mitoGender,
                person.hasDriverLicense());
    }

    private void fillMitoZoneEmployees(DataSet dataSet) {

        final Map<Integer, MitoZone> zones = dataSet.getZones();

        for (Job jj : dataContainer.getJobDataManager().getJobs()) {
            final MitoZone zone = zones.get(jj.getZoneId());
            final String type = jj.getType().toUpperCase();
            try {
                de.tum.bgu.msm.data.jobTypes.JobType mitoJobType = MunichJobType.valueOf(type);
                zone.addEmployeeForType(mitoJobType);
            } catch (IllegalArgumentException e) {
                logger.warn("Job type " + type + " not defined for MITO implementation: Munich");
            }
        }
    }
}