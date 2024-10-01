package de.tum.bgu.msm.health.airPollutant.emission;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Arrays;
import java.util.List;

public class AssignHbefaRoadTypes {

    private static final Logger logger = Logger.getLogger(AssignHbefaRoadTypes.class);
    private static final String OSMTYPE_NAME = "type";
    private static final List<String> minorRoadTypes = Arrays.asList("residential","access","service","living","track",
            "unclassified","pedestrian","path","footway","bridleway","bus","cycleway","road");
    static String inputFile = "/home/qin/models/manchester/scenOutput/mito_1_0_baseStress_basePOI_fullModeset_matsim/matsim/2021/thursday/car/2021.output_network.xml.gz";
    static String outputFile = "/home/qin/models/manchester/scenOutput/mito_1_0_baseStress_basePOI_fullModeset_matsim/matsim/2021/thursday/car/2021.output_network_hbefa.xml.gz";

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

    public static void main(String args[]) {


        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(inputFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Network network = scenario.getNetwork();

        for (Link link : network.getLinks().values()) {
            link.getAttributes().putAttribute("hbefa_road_type", getHbefaType(link));
        }

        new NetworkWriter(network).write(outputFile);

    }

    public static String getHbefaType(Link link) {

        double freeFlowSpeed_kmh = link.getFreespeed()*3.6;
        if(link.getAttributes().getAttribute(OSMTYPE_NAME)==null){
            logger.error("Link id: " + link.getId() + " has no attribute: " + OSMTYPE_NAME);
            return "URB/Trunk-Nat./70";
        }

        String osmType = link.getAttributes().getAttribute(OSMTYPE_NAME).toString().split("_")[0];

        String type = "";
        String speedRange= "";

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
