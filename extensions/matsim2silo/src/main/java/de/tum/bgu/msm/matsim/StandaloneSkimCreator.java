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
        createSkims(8, "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc\\input\\zonesShapefile\\zones.shp",
                "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc\\input\\mito\\trafficAssignment\\pt_2020\\network_pt_road.xml.gz",
                "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc\\input\\mito\\trafficAssignment\\pt_2020\\schedule.xml",
                "skimout.omx", 28800);
    }

    public static void createSkims(int threads, String shapePath, String networkPath,
                                   String schedulePath, String outputPath, int time) {

        SiloUtil.loadHdf5Lib();

        logger.info("Preparing config...");
        Config config = ConfigUtils.createConfig();
        config.transit().setUseTransit(true);

        logger.info("Reading network...");
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(networkPath);

        Scenario scenario = ScenarioUtils.createScenario(config);
        logger.info("Reading schedule...");
        new TransitScheduleReader(scenario).readFile(schedulePath);
        TransitSchedule schedule = scenario.getTransitSchedule();

        logger.info("Reading shape file...");
        final Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(shapePath);
        final Map<Integer, List<SimpleFeature>> zonesById = features.stream()
                .collect(Collectors.groupingBy(f -> Integer.parseInt(String.valueOf(f.getAttribute("id")))));

        ZoneConnectorManager zoneConnectorManager = zoneId -> zonesById.get(zoneId).stream()
                .map( f -> CoordUtils.createCoord(((Geometry) f.getDefaultGeometry()).getCentroid().getCoordinate())).
                        collect(Collectors.toList());

        logger.info("Preparing MATSim data...");
        MatsimData data = new MatsimData(config, threads, network, schedule, zoneConnectorManager);
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(config.planCalcScore());
        data.update(freespeed, freespeed);

        final MatsimSkimCreator matsimSkimCreator = new MatsimSkimCreator(data);

        logger.info("Creating car skim...");
        final IndexedDoubleMatrix2D carSkim = matsimSkimCreator.createCarSkim(zonesById.keySet().stream().map(id -> (Id) () -> id).collect(Collectors.toList()), threads, time);
        logger.info("Done.");
        logger.info("Creating pt skim...");
        final IndexedDoubleMatrix2D ptSkim = matsimSkimCreator.createPtSkim(zonesById.keySet().stream().map(id -> (Id) () -> id).collect(Collectors.toList()), threads, time);
        logger.info("Done.");

        final SkimTravelTimes skimTravelTimes = new SkimTravelTimes();

        skimTravelTimes.updateSkimMatrix(carSkim, TransportMode.car);
        skimTravelTimes.updateSkimMatrix(ptSkim, TransportMode.pt);

        int dimension = zonesById.size();
        OmxMatrixWriter.createOmxFile(outputPath, dimension);
        skimTravelTimes.printOutCarSkim(TransportMode.car, outputPath, "carTravelTimes");
        skimTravelTimes.printOutCarSkim(TransportMode.pt, outputPath, "ptTravelTimes");
    }
}
