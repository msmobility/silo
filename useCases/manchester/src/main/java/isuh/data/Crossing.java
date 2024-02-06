package isuh.data;
import org.matsim.api.core.v01.network.Link;

public enum Crossing {
    UNCONTROLLED,
    PARALLEL,
    SIGNAL;

    public static Crossing getType(Link link, String mode) {
        String name = (String) link.getToNode().getAttributes().getAttribute(mode + "Crossing");
        switch (name) {
            case "null":
                return UNCONTROLLED;
            case "Parallel crossing point":
                return PARALLEL;
            default:
                return SIGNAL;
        }
    }
}