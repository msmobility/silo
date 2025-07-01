package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.network.Link;
import routing.components.JctStress;
import routing.components.LinkStress;

import java.util.*;
import java.util.stream.Collectors;

public class OsmLink {

    int osmId;
    public String roadType;
    public String highway;
    public String onwysmm;
    public double speedLimitMPH;

    public boolean bikeAllowed;
    public boolean carAllowed;
    public boolean walkAllowed;

    public double lengthSum;
    public double width;
    public double bikeStress;
    public double bikeStressJct;
    public double walkStressJct;

    // Updated to arrays for hourly demands
    public double[] carHourlyDemand = new double[24];
    public double[] truckHourlyDemand = new double[24];
    public double[] pedHourlyDemand = new double[24];
    public double[] bikeHourlyDemand = new double[24];
    public double[] motorHourlyDemand = new double[24];

    public Set<Link> networkLinks;

    public OsmLink(int osmId, Set<Link> links) {
        this.osmId = osmId;
        this.networkLinks = links;
    }

    public void computeAttributes() {
        if (networkLinks.isEmpty()) {
            // Set default values if no links
            this.roadType = "residential";
            this.highway = "unclassified";
            this.onwysmm = "Two Way";
            this.speedLimitMPH = 0.0;
            this.bikeAllowed = false;
            this.carAllowed = false;
            this.walkAllowed = false;
            this.lengthSum = 0.0;
            this.width = 0.0;
            this.bikeStress = 0.0;
            this.bikeStressJct = 0.0;
            this.walkStressJct = 0.0;
            Arrays.fill(carHourlyDemand, 0.0);
            Arrays.fill(truckHourlyDemand, 0.0);
            Arrays.fill(pedHourlyDemand, 0.0);
            Arrays.fill(bikeHourlyDemand, 0.0);
            Arrays.fill(motorHourlyDemand, 0.0);
            return;
        }

        Link first = networkLinks.iterator().next();

        this.roadType = getStringAttribute(first, "type", "residential");
        this.highway = getStringAttribute(first, "highway", "unclassified");
        this.onwysmm = getStringAttribute(first, "onwysmm", "Two Way");
        this.speedLimitMPH = getDoubleAttribute(first, "speedLimitMPH", 0.0);

        int bikeAllowedInt = networkLinks.stream()
                .mapToInt(link -> link.getAllowedModes().contains("bike") ? 1 : 0)
                .max().orElse(0);
        this.bikeAllowed = bikeAllowedInt == 1;

        int carAllowedInt = networkLinks.stream()
                .mapToInt(link -> link.getAllowedModes().contains("car") ? 1 : 0)
                .max().orElse(0);
        this.carAllowed = carAllowedInt == 1;

        int walkAllowedInt = networkLinks.stream()
                .mapToInt(link -> link.getAllowedModes().contains("walk") ? 1 : 0)
                .max().orElse(0);
        this.walkAllowed = walkAllowedInt == 1;

        double totalLength = networkLinks.stream().mapToDouble(Link::getLength).sum();
        this.lengthSum = onwysmm.startsWith("Two Way") ? totalLength / 2.0 : totalLength;

        Map<Double, Long> widthFreq = networkLinks.stream()
                .map(l -> getDoubleAttribute(l, "width", 0.0))
                .filter(w -> w != 0.0)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        this.width = widthFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);

        double weightedSum = 0.0;
        double totalWeight = 0.0;
        for (Link link : networkLinks) {
            double stress = LinkStress.getStress(link, "bike");
            double length = link.getLength();
            if (!Double.isNaN(stress) && !Double.isInfinite(stress)) {
                weightedSum += stress * length;
                totalWeight += length;
            }
        }
        this.bikeStress = totalWeight > 0 ? weightedSum / totalWeight : 0.0;

        this.bikeStressJct = networkLinks.stream()
                .mapToDouble(link -> {
                    double stress = JctStress.getStress(link, "bike");
                    return Double.isNaN(stress) || Double.isInfinite(stress) ? Double.NEGATIVE_INFINITY : stress;
                })
                .max()
                .orElse(0.0);

        this.walkStressJct = networkLinks.stream()
                .mapToDouble(link -> {
                    double stress = JctStress.getStress(link, "walk");
                    return Double.isNaN(stress) || Double.isInfinite(stress) ? Double.NEGATIVE_INFINITY : stress;
                })
                .max()
                .orElse(0.0);
    }

    private double getDoubleAttribute(Link l, String key, double defaultVal) {
        Object attr = l.getAttributes().getAttribute(key);
        return attr instanceof Number ? ((Number) attr).doubleValue() : defaultVal;
    }

    private String getStringAttribute(Link l, String key, String defaultVal) {
        Object attr = l.getAttributes().getAttribute(key);
        return attr instanceof String ? (String) attr : defaultVal;
    }

    public Set<Link> getNetworkLinks() {
        return new HashSet<>(networkLinks);
    }
}