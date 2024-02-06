package isuh.calculate;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import isuh.IsuhWorker;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.utils.misc.Counter;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PtIndicatorCalculator implements Runnable {

    private final ConcurrentLinkedQueue<IsuhWorker> workers;
    private final String mode;
    private final Counter counter;
    private final SwissRailRaptor raptor;
    private final Scenario scenario;
    private final ActivityFacilitiesFactory activityFacilitiesFactory;

    public PtIndicatorCalculator(ConcurrentLinkedQueue<IsuhWorker> workers, Counter counter, String mode,
                                 Scenario scenario, SwissRailRaptor raptor,
                                 ActivityFacilitiesFactoryImpl activityFacilitiesFactory) {
        this.workers = workers;
        this.counter = counter;
        this.mode = mode;
        this.scenario = scenario;
        this.raptor = raptor;
        this.activityFacilitiesFactory = activityFacilitiesFactory;
    }

    public void run() {
        while(true) {
            IsuhWorker worker = this.workers.poll();
            if(worker == null) {
                return;
            }
            this.counter.incCounter();

            Coord cOrig = worker.getHomeCoord();
            Coord cDest = worker.getWorkCoord();

            int departureTime = 9 * 60 * 60;
            int earliestDepartureTime = Math.max(departureTime - 900, 0);
            int latestDepartureTime = Math.min(departureTime + 900, 86399);

            Facility fOrig = activityFacilitiesFactory.createActivityFacility(Id.create(1, ActivityFacility.class), cOrig);
            Facility fDest = activityFacilitiesFactory.createActivityFacility(Id.create(1, ActivityFacility.class), cDest);

            List<Leg> legs = raptor.calcRoute(fOrig, fDest, earliestDepartureTime, departureTime, latestDepartureTime, null);

            int ptLegs = 0;
            int walkLegs = 0;
            double ptTime = 0.;
            double walkTime = 0.;
            double walkDistance = 0.;

            if (legs != null) {
                for (Leg leg : legs) {
                    String mode = leg.getMode();
                    double travelTime = leg.getTravelTime().seconds();
                    if (mode.equals("pt")) {
                        ptLegs++;
                        ptTime += travelTime;
                    } else if (mode.equals("walk")) {
                        walkLegs++;
                        walkTime += travelTime;
                        walkDistance += leg.getRoute().getDistance();
                    } else {
                        throw new RuntimeException("Unknown transit leg mode " + mode);
                    }
                }
            }
            double totalTravelTime = ptTime + walkTime;

            if (legs != null) {
                Leg firstLeg = legs.get(0);
                Leg lastLeg = legs.get(legs.size() - 1);
                if (!lastLeg.getMode().equals("walk") || !firstLeg.getMode().equals("walk")) {
                    throw new RuntimeException("First or last leg for worker " + worker.getPerson().getId() +
                            " job " + worker.getPerson().getJobId() + " not walk!");
                }
            }

            Map<String,Double> results = new LinkedHashMap<>();
            results.put("time",totalTravelTime);

            worker.setAttributes(mode, results);
        }
    }
}