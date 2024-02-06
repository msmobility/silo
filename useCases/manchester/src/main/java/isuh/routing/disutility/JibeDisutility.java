package isuh.routing.disutility;


import isuh.routing.Gradient;
import isuh.routing.disutility.components.JctStress;
import isuh.routing.disutility.components.LinkAttractiveness;
import isuh.routing.disutility.components.LinkComfort;
import isuh.routing.disutility.components.LinkStress;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

/**
 * Custom walk and bicycle disutility for JIBE
 * based on BicycleTravelDisutility by Dominik Ziemke
 */
public class JibeDisutility implements TravelDisutility {

    private final String mode;
    private final double marginalCostOfTime_s;
    private final double marginalCostOfDistance_m;
    private final double marginalCostOfGradient_m_100m;
    private final double marginalCostOfComfort_m;
    private final double marinalCostAttractiveness_m;
    private final double marginalCostStress_m;
    private final double marginalCostJunction;

    private final TravelTime timeCalculator;

    // Default parameters
    public JibeDisutility(String mode, TravelTime timeCalculator) {

        this.mode = mode;
        this.marginalCostOfTime_s = 2./300;
        this.marginalCostOfDistance_m = 0.;
        this.timeCalculator = timeCalculator;

        if(mode.equals(TransportMode.bike)) {
            this.marginalCostOfGradient_m_100m = 0.02;
            this.marginalCostOfComfort_m = 2e-4;
            this.marinalCostAttractiveness_m = 4e-3;
            this.marginalCostStress_m = 8e-3;
            this.marginalCostJunction = 8e-2;
        } else if(mode.equals(TransportMode.walk)) {
            this.marginalCostOfGradient_m_100m = 0.01;
            this.marginalCostOfComfort_m = 0.;
            this.marinalCostAttractiveness_m = 6e-3;
            this.marginalCostStress_m = 6e-3;
            this.marginalCostJunction = 6e-2;
        } else {
            throw new RuntimeException("Mode " + mode + " not recognised.");
        }
    }

    // Custom parameters
    public JibeDisutility(String mode, TravelTime timeCalculator,
                          double marginalCostOfTime_s, double marginalCostOfDistance_m,
                          double marginalCostOfGradient_m_100m, double marginalCostOfComfort_m,
                          double marginalCostAttractiveness_m, double marginalCostStress_m,
                          double marginalCostJunction) {

        this.mode = mode;
        this.marginalCostOfTime_s = marginalCostOfTime_s;
        this.marginalCostOfDistance_m = marginalCostOfDistance_m;
        this.marginalCostOfGradient_m_100m = marginalCostOfGradient_m_100m;
        this.marginalCostOfComfort_m = marginalCostOfComfort_m;
        this.marinalCostAttractiveness_m = marginalCostAttractiveness_m;
        this.marginalCostStress_m = marginalCostStress_m;
        this.marginalCostJunction = marginalCostJunction;
        this.timeCalculator = timeCalculator;

    }

    @Override
    public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
        double travelTime = timeCalculator.getLinkTravelTime(link, time, person, vehicle);

        double distance = link.getLength();

        // Travel time disutility
        double disutility = marginalCostOfTime_s * travelTime;

        // Distance disutility (0 by default)
        disutility += marginalCostOfDistance_m * distance;

        // Gradient factor
        double gradient = Gradient.getGradient(link);
        if(gradient < 0.) gradient = 0.;
        disutility += marginalCostOfGradient_m_100m * gradient * distance;

        // Comfort of surface
        double comfortFactor = LinkComfort.getComfortFactor(link);
        disutility += marginalCostOfComfort_m * comfortFactor * distance;

        // Attractiveness factors
        double attractiveness = LinkAttractiveness.getDayAttractiveness(link);
        disutility += marinalCostAttractiveness_m * attractiveness * distance;

        // Stress factors
        double stress = LinkStress.getStress(link,mode);
        disutility += marginalCostStress_m * stress * distance;

        // Junction stress factor
        double junctionStress = JctStress.getJunctionStress(link,mode);
        disutility += marginalCostJunction * junctionStress;

        return disutility;

    }

    @Override
    public double getLinkMinimumTravelDisutility(Link link) {
        return 0;
    }

    public double getTimeComponent(Link link, double time, Person person, Vehicle vehicle) {
        double travelTime = timeCalculator.getLinkTravelTime(link, time, person, vehicle);
        return marginalCostOfTime_s * travelTime;
    }

    public double getDistanceComponent(Link link) {
        return marginalCostOfDistance_m * link.getLength();
    }

    public double getGradientComponent(Link link) {
        double gradient = Gradient.getGradient(link);
        if(gradient < 0.) gradient = 0.;
        return marginalCostOfGradient_m_100m * gradient * link.getLength();
    }

    public double getSurfaceComponent(Link link) {
        double comfortFactor = LinkComfort.getComfortFactor(link);
        return marginalCostOfComfort_m * comfortFactor * link.getLength();
    }

    public double getAttractivenessComponent(Link link) {
        double attractiveness = LinkAttractiveness.getDayAttractiveness(link);
        return marinalCostAttractiveness_m * attractiveness * link.getLength();
    }

    public double getStressComponent(Link link) {
        double stress = LinkStress.getStress(link, mode);
        return marginalCostStress_m * stress * link.getLength();
    }

    public double getJunctionComponent(Link link) {
        double jctStress = JctStress.getJunctionStress(link, mode);
        return marginalCostJunction * jctStress;
    }
}
