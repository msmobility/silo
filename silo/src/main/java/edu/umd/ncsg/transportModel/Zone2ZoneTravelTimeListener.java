/**
 * 
 */
package edu.umd.ncsg.transportModel;

import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.vehicles.Vehicle;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author dziemke
 */
public class Zone2ZoneTravelTimeListener implements IterationEndsListener {
	private final static Logger log = Logger.getLogger(Zone2ZoneTravelTimeListener.class);
	
	private Controler controler;
//	private TravelTime travelTime;
	private Network network;
	private int finalIteration;
	private Map<Integer,SimpleFeature> featureMap;
	private int timeOfDay;
	private int numberOfCalcPoints;
	private CoordinateTransformation ct;
	private Map<Tuple<Integer, Integer>, Float> travelTimesMap;

	

	public Zone2ZoneTravelTimeListener(Controler controler, Network network, int finalIteration, Map<Integer,SimpleFeature> featureMap,
			int timeOfDay, int numberOfCalcPoints, CoordinateTransformation ct, Map<Tuple<Integer, Integer>, Float> travelTimesMap) {
		this.controler = controler;
//		this.travelTime = travelTime;
		this.network = network;
		this.finalIteration = finalIteration;
		this.featureMap = featureMap;
		this.timeOfDay = timeOfDay;
		this.numberOfCalcPoints = numberOfCalcPoints;
		this.ct = ct;
		this.travelTimesMap = travelTimesMap;
	}
	
	
	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		if (event.getIteration() == this.finalIteration) {
			
			
			Dijkstra dijkstra = new Dijkstra(network, new MyTravelTimeDisutility(controler.getLinkTravelTimes()), controler.getLinkTravelTimes());
			
			for (int originFipsPuma5 : featureMap.keySet()) {
				for (int destinationFipsPuma5 : featureMap.keySet()) {
					SimpleFeature originFeature = featureMap.get(originFipsPuma5);
					SimpleFeature destinationFeature = featureMap.get(destinationFipsPuma5);
					
					float averageTravelTime = 0.f;
					
					if (originFipsPuma5 == 2400804) { // this is just here to speed up computation for testing // TODO remove later

						
						double sumTravelTime = 0.f;

						for (int i = 0; i < numberOfCalcPoints; i++) {
							Coord originCoord = ct.transform(SiloMatsimUtils.getRandomCoordinateInGeometry(originFeature));
							//						System.out.println("originCoord = " + originCoord);
							Link originLink = NetworkUtils.getNearestLink(network, originCoord);
							//						System.out.println("originLink = " + originLink);

							for (int j = 0; j < numberOfCalcPoints; j++) {
								Coord destinationCoord = ct.transform(SiloMatsimUtils.getRandomCoordinateInGeometry(destinationFeature));
								//							System.out.println("destinationCoord = " + destinationCoord);
								Link destinationlink = NetworkUtils.getNearestLink(network, destinationCoord);
								//							System.out.println("destinationlink = " + destinationlink);

								Path path = dijkstra.calcLeastCostPath(originLink.getFromNode(), destinationlink.getFromNode(), timeOfDay, null, null);
								//							System.out.println("path.travelTime = " + path.travelTime);
								sumTravelTime = sumTravelTime + path.travelTime;
							}
						}
						averageTravelTime = (float) (sumTravelTime / numberOfCalcPoints / numberOfCalcPoints / 60.);
						log.info("Travel time from FipsPuma5 " + originFipsPuma5 + " to FipsPuma5 " + destinationFipsPuma5 + " is " + averageTravelTime);
					} else {
						averageTravelTime = 60.f; // this is just here to speed up computation for testing // TODO remove later
					}
						
					
					
					travelTimesMap.put(new Tuple<Integer, Integer>(originFipsPuma5, destinationFipsPuma5), averageTravelTime);
				}
			}
		}
	}
	
	
	// inner class to use travel time as travel disutility
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