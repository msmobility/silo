package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.utils.objectattributes.attributable.Attributes;
import routing.components.JctStress;
import routing.components.LinkStress;

public class RunAggregateAccidentModel {
    private static final String MATSIM_NETWORK = "C:/Users/saadi/Documents/Cambridge/manchester/input/mito/trafficAssignment/network.xml";

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(MATSIM_NETWORK);

        //
        printLinkAttributes(network);

    }

    public static void printLinkAttributes(Network network) {
        // Iterate over all links in the network
        for (Link link : network.getLinks().values()) {
            // Get the Attributes object for the link
            Attributes attributes = link.getAttributes();

            // Retrieve attributes used in the script
            String osmID = getStringAttribute(attributes, "osmID", "residential");
            double width = getDoubleAttribute(attributes, "width", 0.0);
            double speedLimitMPH = getDoubleAttribute(attributes, "speedLimitMPH", 0.0);
            String roadType = getStringAttribute(attributes, "type", "residential"); // todo: set as null
            double length = link.getLength();
            double bikeStress = LinkStress.getStress(link, "bike");
            double bikeStressJct = JctStress.getStress(link, "bike");
            double walkStressJct = JctStress.getStress(link, "walk");

            // Print the attributes for the current link
            System.out.println("Link ID: " + link.getId());
            System.out.println("  Width: " + width);
            System.out.println("  Speed Limit (MPH): " + speedLimitMPH);
            System.out.println("  Road Type: " + roadType);
            System.out.println("  Length: " + length);
            System.out.println("  Bike Stress: " + bikeStress);
            System.out.println("  Bike Stress Junction: " + bikeStressJct);
            System.out.println("  Walk Stress Junction: " + walkStressJct);
            System.out.println("----------------------------------------");
        }
    }

    // Helper method to safely get a double attribute
    public static double getDoubleAttribute(Attributes attributes, String key, double defaultValue) {
        Object value = attributes.getAttribute(key);
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse double for attribute " + key + ", using default: " + defaultValue);
            }
        }
        return defaultValue;
    }

    // Helper method to safely get a string attribute
    public static String getStringAttribute(Attributes attributes, String key, String defaultValue) {
        Object value = attributes.getAttribute(key);
        return value != null ? value.toString() : defaultValue;
    }
}
