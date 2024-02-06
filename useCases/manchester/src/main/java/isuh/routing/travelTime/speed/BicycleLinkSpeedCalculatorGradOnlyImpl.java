package isuh.routing.travelTime.speed;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.bicycle.BicycleConfigGroup;
import org.matsim.contrib.bicycle.BicycleLinkSpeedCalculator;
import org.matsim.core.mobsim.qsim.qnetsimengine.QVehicle;
import org.matsim.vehicles.Vehicle;

import javax.inject.Inject;

public class BicycleLinkSpeedCalculatorGradOnlyImpl implements BicycleLinkSpeedCalculator {

    @Inject
    private BicycleConfigGroup bicycleConfigGroup;

    @Inject
    public BicycleLinkSpeedCalculatorGradOnlyImpl() {
    }

    /**
     * for unit testing
     */
    public BicycleLinkSpeedCalculatorGradOnlyImpl(BicycleConfigGroup configGroup) {
        this.bicycleConfigGroup = configGroup;
    }

    @Override
    public double getMaximumVelocity(QVehicle qVehicle, Link link, double time) {

        if (isBike(qVehicle))
            return getMaximumVelocityForLink(link, qVehicle.getVehicle());
        else
            return getDefaultMaximumVelocity(qVehicle, link, time);
    }
    @Override
    public double getMaximumVelocityForLink(Link link, Vehicle vehicle) {
        double maxBicycleSpeed = vehicle.getType().getMaximumVelocity();
        double gradientFactor = computeGradientFactor(link);
        double speed = maxBicycleSpeed * gradientFactor;
        return Math.min(speed, link.getFreespeed());
    }

    private double getDefaultMaximumVelocity(QVehicle qVehicle, Link link, double time) {
        return Math.min(qVehicle.getMaximumVelocity(), link.getFreespeed(time));
    }

    /**
     * Based on "Flügel et al. -- Empirical speed models for cycling in the Oslo road network" (not yet published!)
     * Positive gradients (uphill): Roughly linear decrease in speed with increasing gradient
     * At 9% gradient, cyclists are 42.7% slower
     * Negative gradients (downhill):
     * Not linear; highest speeds at 5% or 6% gradient; at gradients higher than 6% braking
     */
    public double computeGradientFactor(Link link) {

        double factor = 1;
        if (link.getFromNode().getCoord().hasZ() && link.getToNode().getCoord().hasZ()) {
            double fromZ = link.getFromNode().getCoord().getZ();
            double toZ = link.getToNode().getCoord().getZ();
            if (toZ > fromZ) { // No positive speed increase for downhill, only decrease for uphill
                double reduction = 1 - 5 * ((toZ - fromZ) / link.getLength());
                factor = Math.max(0.1, reduction); // maximum reduction is 0.1
            }
        }

        return factor;
    }

    private boolean isBike(QVehicle qVehicle) {
        return qVehicle.getVehicle().getType().getId().toString().equals(bicycleConfigGroup.getBicycleMode());
    }
}
