package isuh.routing.disutility.components;

import org.matsim.api.core.v01.network.Link;

public class LinkAttractiveness {


    public static double getVgviFactor (Link link){
        Double vgvi = (double) link.getAttributes().getAttribute("vgvi");
        return 1. - vgvi;
    }

    public static double getLightingFactor (Link link){
        int lights = (int) link.getAttributes().getAttribute("streetLights");
        return 1. - Math.min(1., 15 * lights / link.getLength());
    }

    public static double getShannonFactor (Link link){
        double shannon = (double) link.getAttributes().getAttribute("shannon");
        return 1. - Math.min(1., shannon / 1.6);
    }

    public static double getPoiFactor (Link link){
        double pois = (double) link.getAttributes().getAttribute("POIs");
        return 1 - Math.min(1., 5 * pois / link.getLength());
    }

    public static double getNegativePoiFactor (Link link){
        double negPois = (double) link.getAttributes().getAttribute("negPOIs");
        return Math.min(1., 5 * negPois / link.getLength());
    }

    public static double getCrimeFactor (Link link){
        double crime = (double) link.getAttributes().getAttribute("crime");
        return Math.min(1., 4 * crime / link.getLength());
    }

    public static double getDayAttractiveness (Link link){
        double vgvi = getVgviFactor(link);
        double pois = getPoiFactor(link);
        double shannon = getShannonFactor(link);
        double negativePois = getNegativePoiFactor(link);
        double crime = getCrimeFactor(link);

        return vgvi / 3 + (pois + shannon + negativePois + crime) / 6;
    }

    public static double getNightAttractiveness (Link link){
        double pois = getPoiFactor(link);
        double shannon = getShannonFactor(link);
        double lighting = getLightingFactor(link);
        double negativePois = getNegativePoiFactor(link);
        double crime = getCrimeFactor(link);

        return (pois + shannon) / 6 + (lighting + negativePois + crime) * 2 / 9;
    }

}
