package isuh.routing.disutility.components;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.bicycle.BicycleUtils;

// This comfort factor is taken directly from the MATSim "bicycle" extension
public class LinkComfort {

    public static double getComfortFactor(Link link) {

        String surface = (String) link.getAttributes().getAttribute(BicycleUtils.SURFACE);
        String type = (String) link.getAttributes().getAttribute(BicycleUtils.WAY_TYPE);

        double comfortFactor = 1.0;
        if (surface != null) {
            switch (surface) {
                case "paved":
                case "asphalt": comfortFactor = 1.0; break;
                case "cobblestone": comfortFactor = .40; break;
                case "cobblestone (bad)": comfortFactor = .30; break;
                case "sett": comfortFactor = .50; break;
                case "cobblestone;flattened":
                case "cobblestone:flattened": comfortFactor = .50; break;
                case "concrete": comfortFactor = 1.0; break;
                case "concrete:lanes": comfortFactor = .95; break;
                case "concrete_plates":
                case "concrete:plates": comfortFactor = .90; break;
                case "paving_stones": comfortFactor = .80; break;
                case "paving_stones:35":
                case "paving_stones:30": comfortFactor = .80; break;
                case "unpaved": comfortFactor = .60; break;
                case "compacted": comfortFactor = .70; break;
                case "dirt":
                case "earth": comfortFactor = .30; break;
                case "fine_gravel": comfortFactor = .90; break;
                case "gravel":
                case "ground": comfortFactor = .60; break;
                case "wood":
                case "pebblestone":
                case "sand": comfortFactor = .30; break;
                case "bricks": comfortFactor = .60; break;
                case "stone":
                case "grass":
                case "compressed": comfortFactor = .40; break;
                case "asphalt;paving_stones:35": comfortFactor = .60; break;
                case "paving_stones:3": comfortFactor = .40; break;
                default: comfortFactor = .85;
            }
        } else {
            // For many primary and secondary roads, no surface is specified because they are by default assumed to be is asphalt.
            // For tertiary roads street this is not true, e.g. Friesenstr. in Kreuzberg
            if (type != null) {
                if (type.equals("primary") || type.equals("primary_link") || type.equals("secondary") || type.equals("secondary_link")) {
                    comfortFactor = 1.0;
                }
            }
        }
        return (1. - comfortFactor);
    }

}
