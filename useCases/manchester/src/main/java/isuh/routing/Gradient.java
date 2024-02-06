package isuh.routing;

import org.matsim.api.core.v01.network.Link;

public class Gradient {
    public static double getGradient (Link link){
        double gradient = 0.;
        if (link.getFromNode().getCoord().hasZ() && link.getToNode().getCoord().hasZ()) {
            double fromZ = link.getFromNode().getCoord().getZ();
            double toZ = link.getToNode().getCoord().getZ();
            gradient = (toZ - fromZ) / link.getLength();
        }
        return gradient;
    }
}
