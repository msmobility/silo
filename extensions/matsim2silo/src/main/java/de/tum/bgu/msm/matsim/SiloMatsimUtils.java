package de.tum.bgu.msm.matsim;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;
import org.matsim.vehicles.Vehicle;

import java.util.Collection;
import java.util.Map;

/**
 * @author dziemke
 */
public class SiloMatsimUtils {
	private final static Logger LOG = Logger.getLogger(SiloMatsimUtils.class);
		



	
	public static final Matrix convertTravelTimesToImpedanceMatrix(
			Map<Tuple<Integer, Integer>, Float> travelTimesMap, int rowCount, int columnCount, int year) {
		LOG.info("Converting MATSim travel times to impedance matrix for " + year + ".");
		String name = "travelTimeMatrix";

		Matrix matrix = new Matrix(name, name, rowCount, columnCount);

		// Do not just increment by 1! Some values are missing. So, do not confuse the array index with the array entry!
		for (int i = 1; i <= rowCount; i++) {
			for (int j = 1; j <= columnCount; j++) {
//				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(zones[i], zones[j]);
//				matrix.setValueAt(zones[i], zones[j], travelTimesMap.get(zone2Zone));
				
				Tuple<Integer, Integer> zone2Zone = new Tuple<Integer, Integer>(i, j);
				matrix.setValueAt(i, j, travelTimesMap.getOrDefault(zone2Zone, 0.f));
			}
		}	
		return matrix;
	}
	
	public static void determineExtentOfFacilities(ActivityFacilities activityFacilities) {
		double xmin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = Double.MIN_VALUE;
		
		for (ActivityFacility activityFacility : activityFacilities.getFacilities().values()) {	
			double x = activityFacility.getCoord().getX();
			double y = activityFacility.getCoord().getY();
			if (x < xmin) {
				xmin = x;
			}
			if (x > xmax) {
				xmax = x;
			}
			if (y < ymin) {
				ymin = y;
			}
			if (y > ymax) {
				ymax = y;
			}
		}
		LOG.info("Extent of facilities is: xmin = " + xmin + "; xmax = " + xmax + "; ymin = " + ymin + "; ymax = " + ymax);
	}

	static TravelTime getAnEmptyNetworkTravelTime(){
		return new TravelTime() {
			@Override
			public double getLinkTravelTime(Link link, double time, Person person, Vehicle vehicle) {
				return link.getLength() / link.getFreespeed();
			}
		};
	}

	static TravelDisutility getAnEmptyNetworkTravelDisutility(){
		return new TravelDisutility() {
			@Override
			public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
				return link.getLength() / link.getFreespeed();
			}

			@Override
			public double getLinkMinimumTravelDisutility(Link link) {
				return link.getLength() / link.getFreespeed();
			}
		};

	}
}