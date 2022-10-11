package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.io.output.OmxMatrixWriter;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StandaloneSkimCreator {

    private final static Logger logger = Logger.getLogger(StandaloneSkimCreator.class);

    public static void main(String[] args) {
//        createSkims(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4], Integer.parseInt(args[5]));
        createSkims(8, "Z:\\projects\\2019\\TraMPA\\San Francisco\\Data\\TAZ\\censusTracts_projected_4269.shp",
                "Z:\\projects\\2019\\TraMPA\\San Francisco\\Network\\sf_network_motorway_residential.xml.gz",
                "",
                "skimout.omx", 28800, false);
    }

    public static void createSkims(int threads, String shapePath, String networkPath,
                                   String schedulePath, String outputPath, int time, boolean transit) {

        SiloUtil.loadHdf5Lib();

        logger.info("Preparing config...");
        Config config = ConfigUtils.createConfig();
        if(transit) {
            config.transit().setUseTransit(true);
        }
        Scenario scenario = ScenarioUtils.createScenario(config);

        logger.info("Reading network...");
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(networkPath);


        TransitSchedule schedule = null;
        if(transit) {
            logger.info("Reading schedule...");
            new TransitScheduleReader(scenario).readFile(schedulePath);
            schedule = scenario.getTransitSchedule();
        }

        logger.info("Reading shape file...");
        final Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(shapePath);
        final Map<Integer, List<SimpleFeature>> zonesById = features.stream()
                .collect(Collectors.groupingBy(f -> Integer.parseInt(String.valueOf(f.getAttribute("TRACTCE")))));

        ZoneConnectorManager zoneConnectorManager = zoneId -> zonesById.get(zoneId).stream()
                .map( f -> CoordUtils.createCoord(((Geometry) f.getDefaultGeometry()).getCentroid().getCoordinate())).
                        collect(Collectors.toList());

        logger.info("Preparing MATSim data...");
        MatsimData data = new MatsimData(config, threads, network, schedule, zoneConnectorManager);
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(config.planCalcScore());
        data.update(freespeed, freespeed);

        final MatsimSkimCreator matsimSkimCreator = new MatsimSkimCreator(data);
        final SkimTravelTimes skimTravelTimes = new SkimTravelTimes();

        logger.info("Creating car skim...");
        final IndexedDoubleMatrix2D carSkim = matsimSkimCreator.createCarSkim(zonesById.keySet().stream().map(id -> (Id) () -> id).collect(Collectors.toList()), threads, time);
        logger.info("Done.");
        skimTravelTimes.updateSkimMatrix(carSkim, TransportMode.car);

        if(transit) {
            logger.info("Creating pt skim...");
            final IndexedDoubleMatrix2D ptSkim = matsimSkimCreator.createPtSkim(zonesById.keySet().stream().map(id -> (Id) () -> id).collect(Collectors.toList()), threads, time);
            logger.info("Done.");
            skimTravelTimes.updateSkimMatrix(ptSkim, TransportMode.pt);
        }


        int dimension = zonesById.size();
        OmxMatrixWriter.createOmxFile(outputPath, dimension);
        skimTravelTimes.printOutCarSkim(TransportMode.car, outputPath, "carTravelTimes");

        if(transit) {
            skimTravelTimes.printOutCarSkim(TransportMode.pt, outputPath, "ptTravelTimes");
        }
    }
}
