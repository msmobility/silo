package edu.umd.ncsg.transportModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;
import org.matsim.vehicles.Vehicle;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author dziemke
 */
public class Zone2ZoneTravelTimeListener implements IterationEndsListener {
	private final static Logger log = Logger.getLogger(Zone2ZoneTravelTimeListener.class);
	
	private Controler controler;
	private Network network;
	private int finalIteration;
	private Map<Integer,SimpleFeature> zoneFeatureMap;
	private double timeOfDay;
	private int numberOfCalcPoints;
	private Map<Tuple<Integer, Integer>, Float> travelTimesMap;

	
	public Zone2ZoneTravelTimeListener(Controler controler, Network network, int finalIteration, Map<Integer,SimpleFeature> zoneFeatureMap,
			double timeOfDay, int numberOfCalcPoints, Map<Tuple<Integer, Integer>, Float> travelTimesMap) {
		this.controler = controler;
		this.network = network;
		this.finalIteration = finalIteration;
		this.zoneFeatureMap = zoneFeatureMap;
		this.timeOfDay = timeOfDay;
		this.numberOfCalcPoints = numberOfCalcPoints;
		this.travelTimesMap = travelTimesMap;
	}
	
	
	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		if (event.getIteration() == this.finalIteration) {
			
			log.info("Starting to calculate average zone-to-zone travel times based on MATSim.");
			
			TravelTime travelTime = controler.getLinkTravelTimes();
			TravelDisutility travelDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(travelTime);
			
			LeastCostPathTree leastCoastPathTree = new LeastCostPathTree(travelTime, travelDisutility);
			
			Map<Integer, List<Node>> zoneCalculationNodesMap = new HashMap<>();
			
			for (int zoneId : zoneFeatureMap.keySet()) {
				for (int i = 0; i < numberOfCalcPoints; i++) { // Several points in a given origin zone
					SimpleFeature originFeature = zoneFeatureMap.get(zoneId);
					Coord originCoord = SiloMatsimUtils.getRandomCoordinateInGeometry(originFeature);
					Link originLink = NetworkUtils.getNearestLink(network, originCoord);
					Node originNode = originLink.getFromNode();
					
					if (!zoneCalculationNodesMap.containsKey(zoneId)) {
						zoneCalculationNodesMap.put(zoneId, new LinkedList<Node>());
					}
					zoneCalculationNodesMap.get(zoneId).add(originNode);
				}
			}			
			
			
			for (int originZoneId : zoneFeatureMap.keySet()) { // Going over all origin zones
				
				for (Node originNode : zoneCalculationNodesMap.get(originZoneId)) { // Several points in a given origin zone
					leastCoastPathTree.calculate(network, originNode, timeOfDay);
					
					for (int destinationZoneId : zoneFeatureMap.keySet()) { // Going over all destination zones
						
						Tuple<Integer, Integer> originDestinationRelation = new Tuple<>(originZoneId, destinationZoneId);
						
						if (!travelTimesMap.containsKey(originDestinationRelation)) {
							travelTimesMap.put(originDestinationRelation, 0.f);
						}

						for (Node destinationNode : zoneCalculationNodesMap.get(destinationZoneId)) {// several points in a given destination zone
							
							double arrivalTime = leastCoastPathTree.getTree().get(destinationNode.getId()).getTime();
							// congested car travel times in minutes
							float congestedTravelTimeMin = (float) ((arrivalTime - timeOfDay) / 60.);
//							System.out.println("congestedTravelTimeMin = " + congestedTravelTimeMin);
							
							// following lines form kai/thomas, see Zone2ZoneImpedancesControlerListener
//							// we guess that any value less than 1.2 leads to errors on the UrbanSim side
//							// since ln(0) is not defined or ln(1) = 0 causes trouble as a denominator ...
//							if(congestedTravelTime_min < 1.2)
//								congestedTravelTime_min = 1.2;
//							Path path = dijkstra.calcLeastCostPath(originLink.getFromNode(), destinationNode, timeOfDay, null, null);
							float previousSumTravelTimeMin = travelTimesMap.get(originDestinationRelation);
							travelTimesMap.put(originDestinationRelation, previousSumTravelTimeMin + congestedTravelTimeMin);
//							System.out.println("previousSumTravelTimeMin = " + previousSumTravelTimeMin);
						}
					}
				}
			}
			
			for (Tuple<Integer, Integer> originDestinationRelation : travelTimesMap.keySet()) {
				float sumTravelTimeMin = travelTimesMap.get(originDestinationRelation);
				float averageTravelTimeMin = sumTravelTimeMin / numberOfCalcPoints / numberOfCalcPoints;
				travelTimesMap.put(originDestinationRelation, averageTravelTimeMin);
			}
		}
	}
	
	
	// Inner class to use travel time as travel disutility
	class MyTravelTimeDisutility implements TravelDisutility{
		TravelTime travelTime;
		
		public MyTravelTimeDisutility(TravelTime travelTime) {
			this.travelTime = travelTime;
		}
		
		@Override
		public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
			return travelTime.getLinkTravelTime(link, time, person, vehicle);
		}

		@Override
		public double getLinkMinimumTravelDisutility(Link link) {
			return link.getLength() / link.getFreespeed(); // minimum travel time
		}
	}
}