package de.tum.bgu.msm.mito;

import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataSet;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.matsim.MatsimSkimCreator;
import de.tum.bgu.msm.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.matsim.ZoneConnectorManager;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import java.util.Objects;

/**
 * Implementation of Transport Model Interface for MITO
 *
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public final class MitoTransportModel extends AbstractModel implements TransportModel {

    private static final Logger logger = Logger.getLogger(MitoTransportModel.class);

    private final MatsimData matsimData;
    private TravelTimes travelTimes;
    private final String propertiesPath;
    private final String baseDirectory;
    private final Config config;

    private final MitoDataConverter dataConverter;

    private TravelTimes mitoInputTravelTimes;
    private Scenario scenario;

    public MitoTransportModel(String baseDirectory, DataContainer dataContainer, Properties properties, Config config, MitoDataConverter dataConverter) {
        super(dataContainer, properties);
        this.travelTimes = Objects.requireNonNull(dataContainer.getTravelTimes());
        this.dataConverter = dataConverter;
        matsimData = new MatsimData(
                config,
                properties,
                ZoneConnectorManager.ZoneConnectorMethod.WEIGHTED_BY_POPULATION,
                dataContainer);
        this.propertiesPath = Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath);
        this.baseDirectory = Objects.requireNonNull(baseDirectory);
        this.config = config;
    }

    @Override
    public void setup() {
        if (travelTimes instanceof MatsimTravelTimes) {
            logger.warn("Using mito with matsimData travel times.");
            mitoInputTravelTimes = new SkimTravelTimes();
            TravelTimeUtil.updateCarSkim((SkimTravelTimes) mitoInputTravelTimes, properties.main.startYear, properties);
            scenario = ScenarioUtils.loadScenario(config);

            ((MatsimTravelTimes) travelTimes).initialize(dataContainer, matsimData);
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
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        if (properties.transportModel.transportModelYears.contains(year + 1)) {
            runTransportModel(year + 1);
        }
    }

    @Override
    public void endSimulation() {

    }

    private void runTransportModel(int year) {
        logger.info("  Running travel demand model MITO for the year " + year);
        DataSet dataSet = convertData(year);
        logger.info("  SILO data being sent to MITO");
        MitoModel mito = MitoModel.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.setBaseDirectory(baseDirectory);
        mito.runModel();

        final Controler controler = mito.getData().getMatsimControler();
        TravelTime matsimTravelTime = controler.getLinkTravelTimes();
        final TravelDisutility matsimDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(matsimTravelTime);
        Network network = scenario.getNetwork();
        TransitSchedule schedule = null;
        if (config.transit().isUseTransit()) {
            schedule = scenario.getTransitSchedule();
        }
        matsimData.update(network, schedule, matsimDisutility, matsimTravelTime);

        if (travelTimes instanceof MatsimTravelTimes) {
            ((MatsimTravelTimes) travelTimes).update(matsimData);
            ((SkimTravelTimes) mitoInputTravelTimes).updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
        } else if (travelTimes instanceof SkimTravelTimes) {
            final IndexedDoubleMatrix2D carSkim = new MatsimSkimCreator(matsimData).createCarSkim(dataContainer.getGeoData().getZones().values());
            ((SkimTravelTimes) travelTimes).updateSkimMatrix(carSkim, TransportMode.car);
//            if(config.transit().isUseTransit()) {
//                ((SkimTravelTimes) travelTimes).updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.pt), TransportMode.pt);
//            }
            ((SkimTravelTimes) travelTimes).updateRegionalTravelTimes(dataContainer.getGeoData().getRegions().values(),
                    dataContainer.getGeoData().getZones().values());
        }
    }

    private DataSet convertData(int year) {
        DataSet dataSet = dataConverter.convertData(dataContainer);
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
        matsimData.update(scenario.getNetwork(), scenario.getTransitSchedule(), travelDisutility, travelTime);
        ((MatsimTravelTimes) travelTimes).update(matsimData);
    }
}