package de.tum.bgu.msm.scenarios.healthMuc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;

import java.util.*;

public class TruckEditNetwork {

    public static final Logger logger = LogManager.getLogger(TruckEditNetwork.class);

    public static void main(String[] args) {

        Network networkMuc = NetworkUtils.readNetwork("C:/models/muc/input/mito/trafficAssignment/studyNetworkDenseCarHealth_Hbefa_old.xml.gz");

        // Add new link at Kolbermoor bridge
        NetworkFactory factory = networkMuc.getFactory();

        Link bridge1 = factory.createLink(Id.create("add_1",Link.class),
                networkMuc.getNodes().get(Id.create("1754278078",Node.class)),
                networkMuc.getNodes().get(Id.create("60721062",Node.class)));

        Link bridge2 = factory.createLink(Id.create("add_2",Link.class),
                networkMuc.getNodes().get(Id.create("60721062",Node.class)),
                networkMuc.getNodes().get(Id.create("1754278078",Node.class)));

        Link reference = networkMuc.getLinks().get(Id.create("339031",Link.class));

        bridge1.setAllowedModes(reference.getAllowedModes());
        bridge2.setAllowedModes(reference.getAllowedModes());

        bridge1.setLength(60);
        bridge2.setLength(60);

        bridge1.setNumberOfLanes(reference.getNumberOfLanes());
        bridge2.setNumberOfLanes(reference.getNumberOfLanes());

        bridge1.getAttributes().putAttribute("type",reference.getAttributes().getAttribute("type"));
        bridge2.getAttributes().putAttribute("type",reference.getAttributes().getAttribute("type"));

        bridge1.getAttributes().putAttribute("origid",reference.getAttributes().getAttribute("origid"));
        bridge2.getAttributes().putAttribute("origid",reference.getAttributes().getAttribute("origid"));

        bridge1.getAttributes().putAttribute("hbefa_road_type",reference.getAttributes().getAttribute("hbefa_road_type"));
        bridge2.getAttributes().putAttribute("hbefa_road_type",reference.getAttributes().getAttribute("hbefa_road_type"));

        bridge1.getAttributes().putAttribute("accidentLinkType",reference.getAttributes().getAttribute("accidentLinkType"));
        bridge2.getAttributes().putAttribute("accidentLinkType",reference.getAttributes().getAttribute("accidentLinkType"));


        networkMuc.removeNode(Id.create("60721076",Node.class));
        networkMuc.removeNode(Id.create("60721074",Node.class));

        networkMuc.addLink(bridge1);
        networkMuc.addLink(bridge2);

        new NetworkCleaner().run(networkMuc);

        // Add truck to specified links
        for(Link link : networkMuc.getLinks().values()){

            Set<String> allowedModes = new HashSet<>(link.getAllowedModes());

            // Adds trucks for any link that allows car
            if(allowedModes.contains("car")) {
                allowedModes.add("truck");
            }
            link.setAllowedModes(allowedModes);

            //  Add trucks based on link type (not used)
/*            Object linkType = link.getAttributes().getAttribute("type");
            if(linkType.equals("motorway") ||
                    linkType.equals("motorway_link") ||
                    linkType.equals("trunk") ||
                    linkType.equals("trunk_link") ||
                    linkType.equals("primary") ||
                    linkType.equals("primary_link") ||
                    linkType.equals("secondary") ||
                    linkType.equals("secondary_link") ||
                    linkType.equals("tertiary") ||
                    linkType.equals("tertiary_link") ||
                    linkType.equals("residential")) {
                Set<String> allowedModes = new HashSet<>(link.getAllowedModes());
                allowedModes.add("truck");
                link.setAllowedModes(allowedModes);*/
            }

        NetworkWriter nw = new NetworkWriter(networkMuc);
        nw.write("C:/models/muc/input/mito/trafficAssignment/studyNetworkDenseCarHealth_Hbefa.xml.gz");

    }


}
