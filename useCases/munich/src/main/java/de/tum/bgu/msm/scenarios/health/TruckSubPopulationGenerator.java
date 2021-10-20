package de.tum.bgu.msm.scenarios.health;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.FastDijkstraFactory;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.List;

public class TruckSubPopulationGenerator {

    public static final Logger logger = Logger.getLogger(TruckSubPopulationGenerator.class);

    public static void main(String[] args) {

        Population population = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        Population subPopulation = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        PopulationUtils.readPopulation(population, "C:/models/muc/input/foca/truck_plans_eur.xml");

        Network networkEur = NetworkUtils.readNetwork("C:/models/muc/input/foca/network_europe.xml.gz");
        Network networkMucCoarse = NetworkUtils.readNetwork("C:/models/muc/input/mito/trafficAssignment/studyNetworkDenseCarHealth_Hbefa.xml.gz");
        logger.info("Links in original health network: " + networkMucCoarse.getLinks().size());
        logger.info("Nodes in original health network: " + networkMucCoarse.getNodes().size());

        int countInternal = 0;
        int countThru = 0;
        int countInbound = 0;
        int countOutbound = 0;

        // Filter coarse muc network to just highways/trunks/primary
        int removedLinks = 0;
        List<Link> removeLinks = new ArrayList<>();
        for(Link link : networkMucCoarse.getLinks().values()){
            Object linkType = link.getAttributes().getAttribute("type");
            if(!linkType.equals("motorway") &
                    !linkType.equals("trunk") &
                    !linkType.equals("primary") &
                    !linkType.equals("motorway_link") &
                    !linkType.equals("trunk_link") &
                    !linkType.equals("primary_link")) {
                removeLinks.add(link);
            }
        }

        for(Link link : removeLinks){
            networkMucCoarse.removeLink(link.getId());
            removedLinks++;
        }

        // Remove disconnected nodes
        new NetworkCleaner().run(networkMucCoarse);

        logger.info("Links removed: " + removedLinks);
        logger.info("Links in coarse health network: " + networkMucCoarse.getLinks().size());
        logger.info("Nodes in coarse health network: " + networkMucCoarse.getNodes().size());

        NetworkWriter nw = new NetworkWriter(networkMucCoarse);
        nw.write("C:/models/muc/input/foca/mucNetworkCoarse.xml.gz");

        ArrayList<SimpleFeature> features = new ArrayList<>(ShapeFileReader.getAllFeatures(args[1]));
        SimpleFeature feature = features.get(0);
        Geometry mucRegion = ((Geometry) feature.getDefaultGeometry());

        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(ConfigUtils.createConfig().planCalcScore());

        LeastCostPathCalculator dijkstra = new FastDijkstraFactory(false)
                .createPathCalculator(networkEur, freespeed, freespeed);


        for (Person person : population.getPersons().values()) {

            boolean choosePerson = false;
            boolean origInside = false;
            boolean destInside = false;
            boolean thru = false;

            Plan plan = person.getSelectedPlan();

            // Origin and destination activity
            Activity origActivity = (Activity) plan.getPlanElements().get(0);
            Activity destActivity = (Activity) plan.getPlanElements().get(2);

            // Origin and destination coordinate
            Coord origCoord = origActivity.getCoord();
            Coord destCoord = destActivity.getCoord();

            // Origin and destination node in EUR network
            Node origNodeEur = NetworkUtils.getNearestNode(networkEur, origCoord);
            Node destNodeEur = NetworkUtils.getNearestNode(networkEur, destCoord);

            // Origin and destination point
            GeometryFactory factory = new GeometryFactory();
            Point origPoint = factory.createPoint(new Coordinate(origCoord.getX(), origCoord.getY()));
            Point destPoint = factory.createPoint(new Coordinate(destCoord.getX(), destCoord.getY()));

            // Starts in MITO region
            if (mucRegion.contains(origPoint)) {
                origInside = true;
                choosePerson = true;
            }

            // Ends in MITO region
            if (mucRegion.contains(destPoint)) {
                destInside = true;
                choosePerson = true;
            }

            // Travels through MITO region
            if (!origInside && !destInside) {
                LeastCostPathCalculator.Path path = dijkstra.calcLeastCostPath(origNodeEur, destNodeEur,
                        origActivity.getEndTime().seconds(), null, null);

                for (Link link : path.links) {
                    Coord linkOrigCoord = link.getFromNode().getCoord();
                    Coord linkDestCoord = link.getToNode().getCoord();

                    Point linkOrigPoint = factory.createPoint(new Coordinate(linkOrigCoord.getX(), linkOrigCoord.getY()));
                    Point linkDestPoint = factory.createPoint(new Coordinate(linkDestCoord.getX(), linkDestCoord.getY()));

                    if (mucRegion.contains(linkOrigPoint) && mucRegion.contains(linkDestPoint)) {
                        thru = true;
                        choosePerson = true;
                        break;
                    }
                }
            }

            if (choosePerson) {
                // Update counts
                if(origInside && destInside) {
                    countInternal++;
                } else if(origInside) {
                    countOutbound++;
                } else if(destInside) {
                    countInbound++;
                } else if(thru) {
                    countThru++;
                } else throw new RuntimeException("Person chosen but no situation applies!");

                // Create new origin activity
                Activity newOrigActivity;
                Coord newOrigCoord;
                double newOrigTime;
                if (origInside) {
                    newOrigCoord = origActivity.getCoord();
                    newOrigTime = origActivity.getEndTime().seconds();
                } else {
                    Node newOrigNodeMuc = NetworkUtils.getNearestNode(networkMucCoarse, origCoord);
                    newOrigCoord = newOrigNodeMuc.getCoord();
                    Node newOrigNodeEur = NetworkUtils.getNearestNode(networkEur, newOrigCoord);
                    LeastCostPathCalculator.Path accessPath = dijkstra.calcLeastCostPath(origNodeEur, newOrigNodeEur,
                            origActivity.getEndTime().seconds(), null, null);
                    double accessTime = accessPath.travelTime;
                    newOrigTime = origActivity.getEndTime().seconds() + accessTime;
                }
                newOrigActivity = PopulationUtils.createActivityFromCoord(origActivity.getType(), newOrigCoord);
                newOrigActivity.setEndTime(newOrigTime);

                // Create new destination activity
                Activity newDestActivity;
                Coord newDestCoord;
                if (destInside) {
                    newDestCoord = destActivity.getCoord();
                } else {
                    newDestCoord = NetworkUtils.getNearestNode(networkMucCoarse, destCoord).getCoord();
                }
                newDestActivity = PopulationUtils.createActivityFromCoord(destActivity.getType(), newDestCoord);

                // Assemble new plan
                Person newPerson = subPopulation.getFactory().createPerson(person.getId());
                Plan newPlan = PopulationUtils.createPlan();
                newPerson.addPlan(newPlan);
                newPlan.addActivity(newOrigActivity);
                newPlan.addLeg(PopulationUtils.createLeg(TransportMode.truck));
                newPlan.addActivity(newDestActivity);
                subPopulation.addPerson(newPerson);
            }
        }

        logger.info("Total before: " + population.getPersons().size());
        logger.info("Internal: " + countInternal);
        logger.info("Inbound: " + countInbound);
        logger.info("Outbound: " + countOutbound);
        logger.info("Thru: " + countThru);

        PopulationWriter pw = new PopulationWriter(subPopulation);
        pw.write("C:/models/muc/input/foca/ld_trucks_muc.xml");
    }

}
