package isuh.calculate;

import isuh.IsuhWorker;
import isuh.TravelAttribute;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.utils.misc.Counter;
import org.matsim.vehicles.Vehicle;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkIndicatorCalculator implements Runnable {

    private final ConcurrentLinkedQueue<IsuhWorker> workers;
    private final Counter counter;
    private final String mode;
    private final Vehicle vehicle;
    private final LeastCostPathCalculator pathCalculator;

    private final TravelDisutility travelDisutility;
    private final Network routingNetwork;
    private final Network xy2lNetwork;
    private final LinkedHashMap<String, TravelAttribute> additionalAttributes;

    public NetworkIndicatorCalculator(ConcurrentLinkedQueue<IsuhWorker> workers, Counter counter, String mode,
                                      Vehicle vehicle, Network routingNetwork, Network xy2lNetwork,
                                      LeastCostPathCalculator pathCalculator, TravelDisutility travelDisutility,
                                      LinkedHashMap<String, TravelAttribute> additionalAttributes) {
        this.workers = workers;
        this.counter = counter;
        this.mode = mode;
        this.vehicle = vehicle;
        this.routingNetwork = routingNetwork;
        this.xy2lNetwork = xy2lNetwork;
        this.pathCalculator = pathCalculator;
        this.travelDisutility = travelDisutility;
        this.additionalAttributes = additionalAttributes;
    }

    public void run() {

        while(true) {
            IsuhWorker worker = this.workers.poll();
            if(worker == null) {
                return;
            }

            this.counter.incCounter();
            Map<String,Double> results = new LinkedHashMap<>();

                Coord cOrig = worker.getHomeCoord();
                Coord cDest = worker.getWorkCoord();
                Node nOrig;
                Node nDest;
                if(xy2lNetwork == null) {
                    nOrig = NetworkUtils.getNearestNode(routingNetwork,cOrig);
                    nDest = NetworkUtils.getNearestNode(routingNetwork,cDest);
                } else {
                    nOrig = routingNetwork.getNodes().get(NetworkUtils.getNearestLink(xy2lNetwork, cOrig).getToNode().getId());
                    nDest = routingNetwork.getNodes().get(NetworkUtils.getNearestLink(xy2lNetwork, cDest).getToNode().getId());
                }

                // Calculate least cost path
                LeastCostPathCalculator.Path path = pathCalculator.calcLeastCostPath(nOrig, nDest, 28800, null, vehicle);

                // Set cost, time, and distance
                results.put("time",path.travelTime);
                results.put("dist",path.links.stream().mapToDouble(Link::getLength).sum());

                // Additional attributes
                if(additionalAttributes != null) {
                    for (Map.Entry<String, TravelAttribute> e : additionalAttributes.entrySet()) {
                        String name = e.getKey();
                        double result = path.links.stream().mapToDouble(l -> e.getValue().getTravelAttribute(l,travelDisutility)).sum();
                        results.put(name,result);
                    }
                }
                worker.setAttributes(mode,results);
        }
    }
}