//package de.tum.bgu.msm.syntheticPopulationGenerator.maryland;
//
//import cern.colt.matrix.tdouble.DoubleMatrix2D;
//import de.tum.bgu.msm.data.Zone;
//import de.tum.bgu.msm.data.geo.GeoDataMstm;
//import de.tum.bgu.msm.io.GeoDataReaderMstm;
//import de.tum.bgu.msm.io.output.OmxMatrixWriter;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.util.matrices.Matrices;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.locationtech.jts.geom.Coordinate;
//import org.matsim.api.core.v01.Coord;
//import org.matsim.api.core.v01.Id;
//import org.matsim.api.core.v01.network.Link;
//import org.matsim.api.core.v01.network.Network;
//import org.matsim.api.core.v01.network.Node;
//import org.matsim.api.core.v01.population.Person;
//import org.matsim.core.network.NetworkUtils;
//import org.matsim.core.network.io.MatsimNetworkReader;
//import org.matsim.core.router.util.TravelDisutility;
//import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
//import org.matsim.utils.leastcostpathtree.LeastCostPathTree;
//import org.matsim.vehicles.Vehicle;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by zhenpeng on 1/9/2019.
// */
//public class CreateFreeFlowCarSkim {
//
//    private final static double TIME = 0;
//
//    public static void main(String[] args) {
//
//        final Properties properties = SiloUtil.siloInitialization("T:/2Kuehnel_Nico/synpop/siloMstm.properties");
//        GeoDataMstm geoData = new GeoDataMstm(properties);
//        GeoDataReaderMstm geoDataReaderMstm = new GeoDataReaderMstm(geoData);
//        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
//        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
//        geoDataReaderMstm.readZoneCsv(fileName);
//        geoDataReaderMstm.readZoneShapefile(pathShp);
//        geoDataReaderMstm.readCrimeData(Properties.get().main.baseDirectory + Properties.get().geo.countyCrimeFile);
//
//        Network network = NetworkUtils.createNetwork();
//        new MatsimNetworkReader(network).readFile("T:\\2Kuehnel_Nico/networkCleaned_epsg26918.xml.gz");
//
//        Collection<Zone> zones = geoData.getZones().values();
//        System.out.println("after initialization");
//        LeastCostPathTree leastCostPathTree = new LeastCostPathTree(new FreeSpeedTravelTime(), new TravelDisutility() {
//            @Override
//            public double getLinkTravelDisutility(Link link, double v, Person person, Vehicle vehicle) {
//                return 0;
//            }
//
//            @Override
//            public double getLinkMinimumTravelDisutility(Link link) {
//                return 0;
//            }
//        });
//
//
//        final Map<Zone, Node> zoneCalculationNodesMap = new HashMap<>();
//        for (Zone zone : zones) {
//            Coordinate coordinate = zone.getRandomCoordinate();
//            Coord originCoord = new Coord(coordinate.x, coordinate.y);
//            Node originNode = NetworkUtils.getNearestLink(network, originCoord).getToNode();
//            zoneCalculationNodesMap.put(zone, originNode);
//        }
//
//        final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(zones, zones);
//
//        for (Zone zone : zones) {
//            System.out.println("Finished zone " + zone.getId());
//            Node originNode = zoneCalculationNodesMap.get(zone);
//            leastCostPathTree.calculate(network, originNode, TIME);
//            Map<Id<Node>, LeastCostPathTree.NodeData> tree = leastCostPathTree.getTree();
//            for (Zone zoneTo : zones) {
//
//                Node toNode;
//                double time;
//                if(zone.equals(zoneTo)) {
//                    double timeSum = 0;
//                    for(int i = 0; i <3; i++) {
//                        Coordinate coordinate = zone.getRandomCoordinate();
//                        Coord originCoord = new Coord(coordinate.x, coordinate.y);
//                        toNode = NetworkUtils.getNearestLink(network, originCoord).getToNode();
//                        timeSum += tree.get(toNode.getId()).getTime();
//                    }
//                    time = timeSum / 3;
//                } else {
//                    toNode = zoneCalculationNodesMap.get(zoneTo);
//                     time = tree.get(toNode.getId()).getTime();
//                }
//
//                matrix.setQuick(zone.getZoneId(), zoneTo.getZoneId(), time);
//
//            }
//        }
//
//        OmxMatrixWriter.createOmxFile("T:/2Kuehnel_Nico/carTravelSkim.omx", zones.stream().mapToInt(de.tum.bgu.msm.data.Id::getId).max().getAsInt()+1);
//        OmxMatrixWriter.createOmxSkimMatrix(matrix, "T:/2Kuehnel_Nico/carTravelSkim.omx", "carSkim");
//    }
//}
