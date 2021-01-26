package inputGeneration;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashSet;
import java.util.Set;

public class CreateNetwork {

    /*
     * (1)------(2)------(3)------(4)------(5)
     * 	 |		 |		  |		   |	    |
     * 	 |		 |		  |		   |	    |
     * (6)------(7)------(8)------(9)-----(10)
     * 	 |		 |		  |		   |	    |
     * 	 |		 |		  |		   |	    |
     * (11)----(12)-----(13)-----(14)-----(15)
     *   |		 |		  |		   |	    |
     *   |		 |		  |		   |	    |
     * (16)----(17)-----(18)-----(19)-----(20)
     * 	 |		 |		  |		   |	    |
     * 	 |		 |		  |		   |	    |
     * (21)----(22)-----(23)-----(24)-----(25)
     */
    public static void main(String[] args) {
        String networkFileName = "useCases/fabiland/scenario/matsimInput/nw_cap30.xml";
        double freespeed = 50./3.6;
        double capacity = 30.;
        double numLanes = 1.;
        double length = 5000.;

        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network network = scenario.getNetwork();

        // Nodes
        Node node1 = NetworkUtils.createAndAddNode(network, Id.create(1, Node.class), new Coord((double) -10000, (double) 10000));
        Node node2 = NetworkUtils.createAndAddNode(network, Id.create(2, Node.class), new Coord((double) -5000, (double) 10000));
        Node node3 = NetworkUtils.createAndAddNode(network, Id.create(3, Node.class), new Coord((double) 0, (double) 10000));
        Node node4 = NetworkUtils.createAndAddNode(network, Id.create(4, Node.class), new Coord((double) 5000, (double) 10000));
        Node node5 = NetworkUtils.createAndAddNode(network, Id.create(5, Node.class), new Coord((double) 10000, (double) 10000));
        Node node6 = NetworkUtils.createAndAddNode(network, Id.create(6, Node.class), new Coord((double) -10000, (double) 5000));
        Node node7 = NetworkUtils.createAndAddNode(network, Id.create(7, Node.class), new Coord((double) -5000, (double) 5000));
        Node node8 = NetworkUtils.createAndAddNode(network, Id.create(8, Node.class), new Coord((double) 0, (double) 5000));
        Node node9 = NetworkUtils.createAndAddNode(network, Id.create(9, Node.class), new Coord((double) 5000, (double) 5000));
        Node node10 = NetworkUtils.createAndAddNode(network, Id.create(10, Node.class), new Coord((double) 10000, (double) 5000));
        Node node11 = NetworkUtils.createAndAddNode(network, Id.create(11, Node.class), new Coord((double) -10000, (double) 0));
        Node node12 = NetworkUtils.createAndAddNode(network, Id.create(12, Node.class), new Coord((double) -5000, (double) 0));
        Node node13 = NetworkUtils.createAndAddNode(network, Id.create(13, Node.class), new Coord((double) 0, (double) 0));
        Node node14 = NetworkUtils.createAndAddNode(network, Id.create(14, Node.class), new Coord((double) 5000, (double) 0));
        Node node15 = NetworkUtils.createAndAddNode(network, Id.create(15, Node.class), new Coord((double) 10000, (double) 0));
        Node node16 = NetworkUtils.createAndAddNode(network, Id.create(16, Node.class), new Coord((double) -10000, (double) -5000));
        Node node17 = NetworkUtils.createAndAddNode(network, Id.create(17, Node.class), new Coord((double) -5000, (double) -5000));
        Node node18 = NetworkUtils.createAndAddNode(network, Id.create(18, Node.class), new Coord((double) 0, (double) -5000));
        Node node19 = NetworkUtils.createAndAddNode(network, Id.create(19, Node.class), new Coord((double) 5000, (double) -5000));
        Node node20 = NetworkUtils.createAndAddNode(network, Id.create(20, Node.class), new Coord((double) 10000, (double) -5000));
        Node node21 = NetworkUtils.createAndAddNode(network, Id.create(21, Node.class), new Coord((double) -10000, (double) -10000));
        Node node22 = NetworkUtils.createAndAddNode(network, Id.create(22, Node.class), new Coord((double) -5000, (double) -10000));
        Node node23 = NetworkUtils.createAndAddNode(network, Id.create(23, Node.class), new Coord((double) 0, (double) -10000));
        Node node24 = NetworkUtils.createAndAddNode(network, Id.create(24, Node.class), new Coord((double) 5000, (double) -10000));
        Node node25 = NetworkUtils.createAndAddNode(network, Id.create(25, Node.class), new Coord((double) 10000, (double) -10000));

        Set<String> carModeSet = new HashSet<>();
        carModeSet.add(TransportMode.car);

        // Links (bi-directional)
        NetworkUtils.createAndAddLink(network,Id.create("0102", Link.class), node1, node2, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0102", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0201", Link.class), node2, node1, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0201", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0203", Link.class), node2, node3, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0203", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0302", Link.class), node3, node2, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0302", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0304", Link.class), node3, node4, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0304", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0403", Link.class), node4, node3, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0403", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0405", Link.class), node4, node5, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0405", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0504", Link.class), node5, node4, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0504", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("0106", Link.class), node1, node6, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0106", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0601", Link.class), node6, node1, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0601", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0207", Link.class), node2, node7, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0207", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0702", Link.class), node7, node2, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0702", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0308", Link.class), node3, node8, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0308", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0803", Link.class), node8, node3, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0803", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0409", Link.class), node4, node9, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0409", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0904", Link.class), node9, node4, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0904", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0510", Link.class), node5, node10, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0510", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1005", Link.class), node10, node5, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1005", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("0607", Link.class), node6, node7, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0607", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0706", Link.class), node7, node6, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0706", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0708", Link.class), node7, node8, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0708", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0807", Link.class), node8, node7, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0807", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0809", Link.class), node8, node9, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0809", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("0908", Link.class), node9, node8, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0908", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0910", Link.class), node9, node10, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0910", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1009", Link.class), node10, node9, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1009", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("0611", Link.class), node6, node11, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0611", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1106", Link.class), node11, node6, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1106", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0712", Link.class), node7, node12, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0712", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1207", Link.class), node12, node7, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1207", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0813", Link.class), node8, node13, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0813", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1308", Link.class), node13, node8, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1308", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("0914", Link.class), node9, node14, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("0914", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1409", Link.class), node14, node9, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1409", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1015", Link.class), node10, node15, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1015", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1510", Link.class), node15, node10, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1510", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("1112", Link.class), node11, node12, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1112", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1211", Link.class), node12, node11, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1211", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1213", Link.class), node12, node13, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1213", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1312", Link.class), node13, node12, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1312", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1314", Link.class), node13, node14, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1314", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1413", Link.class), node14, node13, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1413", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1415", Link.class), node14, node15, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1415", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1514", Link.class), node15, node14, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1514", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("1116", Link.class), node11, node16, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1116", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1611", Link.class), node16, node11, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1611", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1217", Link.class), node12, node17, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1217", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1712", Link.class), node17, node12, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1712", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1318", Link.class), node13, node18, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1318", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1813", Link.class), node18, node13, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1813", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1419", Link.class), node14, node19, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1419", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1914", Link.class), node19, node14, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1914", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1520", Link.class), node15, node20, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1520", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2015", Link.class), node20, node15, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2015", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("1617", Link.class), node16, node17, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1617", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1716", Link.class), node17, node16, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1716", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1718", Link.class), node17, node18, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1718", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1817", Link.class), node18, node17, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1817", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1819", Link.class), node18, node19, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1819", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("1918", Link.class), node19, node18, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1918", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1920", Link.class), node19, node20, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1920", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2019", Link.class), node20, node19, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2019", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("1621", Link.class), node16, node21, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1621", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2116", Link.class), node21, node16, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2116", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1722", Link.class), node17, node22, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1722", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2217", Link.class), node22, node17, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2217", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1823", Link.class), node18, node23, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1823", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2318", Link.class), node23, node18, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2318", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("1924", Link.class), node19, node24, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("1924", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2419", Link.class), node24, node19, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2419", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("2025", Link.class), node20, node25, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2025", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2520", Link.class), node25, node20, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2520", Link.class)).setAllowedModes(carModeSet);


        NetworkUtils.createAndAddLink(network,Id.create("2122", Link.class), node21, node22, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2122", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2221", Link.class), node22, node21, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2221", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("2223", Link.class), node22, node23, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2223", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2322", Link.class), node23, node22, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2322", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("2324", Link.class), node23, node24, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2324", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2423", Link.class), node24, node23, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2423", Link.class)).setAllowedModes(carModeSet);

        NetworkUtils.createAndAddLink(network,Id.create("2425", Link.class), node24, node25, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2425", Link.class)).setAllowedModes(carModeSet);
        NetworkUtils.createAndAddLink(network,Id.create("2524", Link.class), node25, node24, length, freespeed, capacity, numLanes);
        network.getLinks().get(Id.create("2524", Link.class)).setAllowedModes(carModeSet);

        NetworkWriter networkWriter = new NetworkWriter(network);
        networkWriter.write(networkFileName);
    }
}