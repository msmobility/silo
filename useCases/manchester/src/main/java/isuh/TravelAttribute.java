package isuh;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.util.TravelDisutility;

public interface TravelAttribute {
    double getTravelAttribute(Link var1, TravelDisutility var2);
}
