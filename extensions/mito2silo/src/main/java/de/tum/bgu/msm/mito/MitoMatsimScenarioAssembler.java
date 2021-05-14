package de.tum.bgu.msm.mito;

import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataSet;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.trafficAssignment.ConfigureMatsim;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Objects;

public class MitoMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private static final Logger logger = Logger.getLogger(MitoMatsimScenarioAssembler.class);

    private final String propertiesPath;

    private final DataContainer dataContainer;
    private final Properties properties;
    private final MitoDataConverter dataConverter;

    private SkimTravelTimes mitoInputTravelTime;

    public MitoMatsimScenarioAssembler(DataContainer dataContainer,
                                       Properties properties,
                                       MitoDataConverter dataConverter) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.dataConverter = dataConverter;
        this.propertiesPath = Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath);
    }

    @Override
    public Scenario assembleScenario(Config initialMatsimConfig, int year, TravelTimes travelTimes) {

        logger.info("  Running travel demand model MITO for the year " + year);

        DataSet dataSet = convertData(year);

        logger.info("  SILO data being sent to MITO");
        MitoModel mito = MitoModel.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.run();

        logger.info("  Receiving demand from MITO");
        Population population = mito.getData().getPopulation();

        Config config = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
        ConfigureMatsim.setDemandSpecificConfigSettings(config);
        MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
        scenario.setPopulation(population);
        return scenario;
    }

    private DataSet convertData(int year) {
        DataSet dataSet = dataConverter.convertData(dataContainer);
        dataSet.setTravelTimes(mitoInputTravelTime);

        final TravelTimes travelTimes = dataContainer.getTravelTimes();
        if(mitoInputTravelTime == null ) {
            //Transport model runs for the first time.
            if(travelTimes instanceof SkimTravelTimes) {
                mitoInputTravelTime = (SkimTravelTimes) travelTimes;
            } else if(travelTimes instanceof MatsimTravelTimesAndCosts) {
                mitoInputTravelTime = new SkimTravelTimes();
                //MATSim did not run yet
                if(properties.transportModel.matsimInitialEventsFile == null) {
                    //read initial skim for the first year
                    logger.info("Reading car skim for initial Mito input");
                    TravelTimeUtil.updateCarSkim(mitoInputTravelTime, properties.main.startYear, properties);
                } else {
                    //MATSim travel times are initialized from initial events file. Create skim from there.
                    mitoInputTravelTime.updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
                }
            }
        } else {
            //Transport model has run at least once. Update input travel times from skim or matsim.
            mitoInputTravelTime.updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
        }
        dataSet.setTravelTimes(mitoInputTravelTime);
        dataSet.setYear(year);
        return dataSet;
    }
}
