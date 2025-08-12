package de.tum.bgu.msm.analysis;

import de.tum.bgu.msm.HourlyVolumeEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class HourlyVolumeEventAnalysisMEL {

    private static final String MATSIM_NETWORK = "input/mito/trafficAssignment/network.xml";
    private static final String scenarioName = "reference";
    private static final String day = "sunday";
    private static final String year = "2018";
    private static final int SCALE_FACTOR_ACTIVE = 1;
    private static final int SCALE_FACTOR_CAR = 10;
    private static final Logger logger = LogManager.getLogger(HourlyVolumeEventAnalysisMEL.class);

    private static void writeHourlyVolumes(Network network, HourlyVolumeEventHandler handler, String outputPath, String header, int scaleFactor, String[] volumeTypes) {
        try (PrintWriter pw = new PrintWriter(outputPath)) {
            pw.println(header);
            for (Link link : network.getLinks().values()) {
                String linkId = link.getId().toString();
                String edgeId = link.getAttributes().getAttribute("edgeID").toString();
                String osmId = link.getAttributes().getAttribute("osmID") == null ? "NA" : link.getAttributes().getAttribute("osmID").toString();
                for (int hour = 0; hour <= 24; hour++) {
                    StringBuilder line = new StringBuilder();
                    line.append(linkId).append(",").append(edgeId).append(",").append(osmId).append(",").append(hour);
                    for (String type : volumeTypes) {
                        int volume = 0;
                        if (type.equals("bike") && handler.getBikeVolumes().get(link.getId()) != null) {
                            volume = handler.getBikeVolumes().get(link.getId()).getOrDefault(hour, 0) * scaleFactor;
                        } else if (type.equals("ped") && handler.getPedVolumes().get(link.getId()) != null) {
                            volume = handler.getPedVolumes().get(link.getId()).getOrDefault(hour, 0) * scaleFactor;
                        } else if (type.equals("car") && handler.getCarVolumes().get(link.getId()) != null) {
                            volume = handler.getCarVolumes().get(link.getId()).getOrDefault(hour, 0) * scaleFactor;
                        } else if (type.equals("truck") && handler.getTruckVolumes().get(link.getId()) != null) {
                            volume = handler.getTruckVolumes().get(link.getId()).getOrDefault(hour, 0) * scaleFactor;
                        }
                        line.append(",").append(volume);
                    }
                    pw.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn("File not found: {}\n{}", outputPath, e);
        }
    }

    private static class ModeCategory {
        String subfolder;
        String outputSuffix;
        String[] volumeTypes;
        int scaleFactor;
        String header;

        ModeCategory(String[] volumeTypes, int scaleFactor, String header) {
            this.subfolder = toCamelCaseSubfolder(volumeTypes);
            this.outputSuffix = String.format("%s_%s.csv", this.subfolder, day);
            this.volumeTypes = volumeTypes;
            this.scaleFactor = scaleFactor;
            this.header = header;
        }
    }

    private static final ModeCategory[] MODES = new ModeCategory[]{
            new ModeCategory(new String[]{"bike", "ped"}, SCALE_FACTOR_ACTIVE, "linkId,edgeId,osmId,hour,bike,ped"),
            new ModeCategory(new String[]{"car", "truck"}, SCALE_FACTOR_CAR, "linkId,edgeId,osmId,hour,car,truck")
    };

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(MATSIM_NETWORK);

        for (ModeCategory mode : MODES) {
            String scenarioPath = String.format("scenOutput/%s/matsim/%s/%s", scenarioName, year, day);
            String eventPath = String.format("%s/%s/%s.output_events.xml.gz", scenarioPath, mode.subfolder, year);
            String vehiclesPath = String.format("%s/%s/%s.output_vehicles.xml.gz", scenarioPath, mode.subfolder, year);
            String outputPath = String.format("%s/hourlyVolume_%s", scenarioPath, mode.outputSuffix);

            Vehicles vehicles = VehicleUtils.createVehiclesContainer();
            new MatsimVehicleReader(vehicles).readFile(vehiclesPath);
            EventsManager eventsManager = new EventsManagerImpl();
            HourlyVolumeEventHandler handler = new HourlyVolumeEventHandler(vehicles);
            eventsManager.addHandler(handler);
            EventsUtils.readEvents(eventsManager, eventPath);
            writeHourlyVolumes(network, handler, outputPath, mode.header, mode.scaleFactor, mode.volumeTypes);
        }
    }

    private static String toCamelCaseSubfolder(String[] types) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (i == 0) {
                sb.append(type.toLowerCase());
            } else {
                sb.append(Character.toUpperCase(type.charAt(0)))
                        .append(type.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

}
