package sdg;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingDataImpl;
import de.tum.bgu.msm.io.DwellingReaderMuc;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.Collection;

public class TransitStopBufferMuc {

    private static String[] scenarios = {
            "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc\\microData\\dd_2011.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\baseSDG\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\baseSDG_coreCities_withGrowth\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\mucDraconicResettlement_scaled\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\oneCar\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\scenario_2\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\scenario_3\\microData\\dd_2050.csv",
            "Z:\\projects\\2018\\DAAD Japan\\Scenarios\\muc\\scenario_4\\microData\\dd_2050.csv"
    };

    public static void main(String[] args) {

        int total = 0;
        int access = 0;

        final Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new TransitScheduleReader(scenario).readFile("C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc\\input\\mito\\trafficAssignment\\pt_2020\\schedule.xml");

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        Collection<TransitStopFacility> stops = scenario.getTransitSchedule().getFacilities().values();
        for (TransitStopFacility stopFacility : stops) {

            double x = stopFacility.getCoord().getX();
            double y = stopFacility.getCoord().getY();

            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        QuadTree<TransitStopFacility> stopsQT = new QuadTree<>(minX, minY, maxX, maxY);
        for (TransitStopFacility stopFacility : stops) {
//            if(!stopFacility.getId().toString().endsWith("db") &&
//                    !stopFacility.getId().toString().endsWith("rb")) {
//                continue;
//            }
            double x = stopFacility.getCoord().getX();
            double y = stopFacility.getCoord().getY();
            stopsQT.put(x, y, stopFacility);
        }


        for(String result: scenarios) {
            final DwellingDataImpl realEstate = new DwellingDataImpl();
            new DwellingReaderMuc(realEstate).readData(result);

            for (Dwelling dwelling : realEstate.getDwellings()) {
                if (dwelling.getResidentId() > 0) {
                    total++;
                    final Coordinate coordinate = dwelling.getCoordinate();
                    final double distance = coordinate.distance(CoordUtils.createGeotoolsCoordinate(stopsQT.getClosest(coordinate.x, coordinate.y).getCoord()));
                    if (distance < 500) {
                        access++;
                    }
                }
            }

            System.out.println(result + "| Share: " + (double) access / (double) total);
        }
    }
}
