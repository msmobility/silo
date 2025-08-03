package de.tum.bgu.msm.health.airPollutant.emission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.HbefaRoadTypeMapping;

import java.util.Arrays;
import java.util.List;

public class MCRHbefaRoadTypeMapping extends HbefaRoadTypeMapping {

    private static final Logger logger = LogManager.getLogger(MCRHbefaRoadTypeMapping.class);
    private static final String OSMTYPE_NAME = "type";
    private static final List<String> minorRoadTypes = Arrays.asList("residential","access","service","living","track",
            "unclassified","pedestrian","path","footway","bridleway","bus","cycleway","road","steps");

    /**
     * handled OSM road types:
     *    motorway,trunk,primary,secondary, tertiary, unclassified,residential,service
     *    motorway_link, trunk_link,primary_link, secondary_link
     *    tertiary_link, living_street, pedestrian,track,road
     *
     * Hbefa categories and respective speeds
     *    URB/MW-Nat./80 - 130
     *    URB/MW-City/60 - 110
     *    URB/Trunk-Nat./70 - 110
     *    URB/Trunk-City/50 - 90
     *    URB/Distr/50 - 80
     *    URB/Local/50 - 60
     *    URB/Access/30 - 50
     *
     * Conversions from OSM to hbefa types
     *    motorway;MW
     *    primary;Trunk
     *    secondary;Distr
     *    tertiary;Local
     *    residential;Access
     *    living;Access
     **/

    @Override
    protected String determineHbefaType(Link link) {

        double freeFlowSpeed_kmh = link.getFreespeed()*3.6;

        String type = "";
        String speedRange= "";

        if(link.getAttributes().getAttribute(OSMTYPE_NAME)==null){
            if(link.getAllowedModes().contains("bus")){
                if (freeFlowSpeed_kmh > 80) {
                    type = "MW-Nat.";
                    speedRange = "80";
                } else if (freeFlowSpeed_kmh > 60){
                    type = "MW-City";
                    speedRange = "60";
                } else if (freeFlowSpeed_kmh >= 50){
                    type = "Local";
                    speedRange = "50";
                } else {
                    type = "Access";
                    speedRange = "30";
                }
                return "URB/" + type + "/" + speedRange;
            }else{
                logger.error("Link id: " + link.getId() + " has no attribute: " + OSMTYPE_NAME);
                return "URB/Trunk-Nat./70";
            }
        }

        String osmType = link.getAttributes().getAttribute(OSMTYPE_NAME).toString().split("_")[0];

        if (osmType.equals("motorway")){
            if (freeFlowSpeed_kmh > 80) {
                type = "MW-Nat.";
                speedRange = "80";
            } else if (freeFlowSpeed_kmh > 60){
                type = "MW-City";
                speedRange = "60";
            } else {
                logger.error("Road classified with lower cat: " + osmType + " - " + freeFlowSpeed_kmh);
                type = "Distr";
                speedRange = "50";
            }
        } else if (osmType.equals("primary") || osmType.equals("trunk")){
            if (freeFlowSpeed_kmh >= 70) {
                type = "Trunk-Nat.";
                speedRange = "70";
            } else if (freeFlowSpeed_kmh >= 50){
                type = "Trunk-City";
                speedRange = "50";
            } else {
                logger.error("Road classified with lower cat: " + osmType + " - " + freeFlowSpeed_kmh);
                type = "Distr";
                speedRange = "50";
            }
        } else if (osmType.equals("secondary")){
            type = "Distr";
            speedRange = "50";
        } else if (osmType.equals("tertiary")){
            type = "Local";
            speedRange = "50";
        } else if (minorRoadTypes.contains(osmType)){
            type = "Access";
            speedRange = "30";
        } else {
            logger.error(osmType + " - " + freeFlowSpeed_kmh);
            throw new RuntimeException("road type not known");
        }

        return "URB/" + type + "/" + speedRange;
    }
}
