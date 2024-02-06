package isuh;

// Additional utils beyond NetworkUtils

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;

import java.util.Collections;
import java.util.function.Predicate;

public class NetworkUtils2 {

    // Extracts mode-specific network  (e.g. walk network, car network, cycle network)
    public static Network extractModeSpecificNetwork(Network network, String transportMode) {
        Network modeSpecificNetwork = NetworkUtils.createNetwork();
        new TransportModeNetworkFilter(network).filter(modeSpecificNetwork, Collections.singleton(transportMode));
        NetworkUtils.runNetworkCleaner(modeSpecificNetwork);
        return modeSpecificNetwork;
    }

    public static void identifyDisconnectedLinks(Network network, String transportMode) {
        Network modeSpecificNetwork = extractModeSpecificNetwork(network, transportMode);
        for(Link link : network.getLinks().values()) {
            boolean disconnected = false;
            if (link.getAllowedModes().contains(transportMode)) {
                disconnected = !modeSpecificNetwork.getLinks().containsKey(link.getId());
            }
            link.getAttributes().putAttribute("disconnected_" + transportMode, disconnected);
        }
    }

    // Extracts network of usable nearest links to start/end journey (e.g. a car trip cannot start on a motorway)
    public static Network extractXy2LinksNetwork(Network network, Predicate<Link> xy2linksPredicate) {
        Network xy2lNetwork = NetworkUtils.createNetwork();
        NetworkFactory nf = xy2lNetwork.getFactory();
        for (Link link : network.getLinks().values()) {
            if (xy2linksPredicate.test(link)) {
                // okay, we need that link
                Node fromNode = link.getFromNode();
                Node xy2lFromNode = xy2lNetwork.getNodes().get(fromNode.getId());
                if (xy2lFromNode == null) {
                    xy2lFromNode = nf.createNode(fromNode.getId(), fromNode.getCoord());
                    xy2lNetwork.addNode(xy2lFromNode);
                }
                Node toNode = link.getToNode();
                Node xy2lToNode = xy2lNetwork.getNodes().get(toNode.getId());
                if (xy2lToNode == null) {
                    xy2lToNode = nf.createNode(toNode.getId(), toNode.getCoord());
                    xy2lNetwork.addNode(xy2lToNode);
                }
                Link xy2lLink = nf.createLink(link.getId(), xy2lFromNode, xy2lToNode);
                xy2lLink.setAllowedModes(link.getAllowedModes());
                xy2lLink.setCapacity(link.getCapacity());
                xy2lLink.setFreespeed(link.getFreespeed());
                xy2lLink.setLength(link.getLength());
                xy2lLink.setNumberOfLanes(link.getNumberOfLanes());
                xy2lNetwork.addLink(xy2lLink);
            }
        }
        return xy2lNetwork;
    }


}
