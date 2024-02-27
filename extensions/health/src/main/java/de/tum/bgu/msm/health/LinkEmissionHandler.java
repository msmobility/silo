package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.events.ColdEmissionEvent;
import org.matsim.contrib.emissions.events.ColdEmissionEventHandler;
import org.matsim.contrib.emissions.events.WarmEmissionEvent;
import org.matsim.contrib.emissions.events.WarmEmissionEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class LinkEmissionHandler implements WarmEmissionEventHandler, ColdEmissionEventHandler {



    private Network network;
    private Map<Id<Vehicle>, AnalyzedVehicle> emmisionsByVehicle;
    private Map<Id<Link>, AnalyzedLink> emmisionsByLink;


    public LinkEmissionHandler(Network network) {
        this.network = network;
        emmisionsByLink = new HashMap<>();
        emmisionsByVehicle = new HashMap<>();
    }

    @Override
    public void handleEvent(WarmEmissionEvent event) {
        Id<Link> linkId = event.getLinkId();
        Link matsimLink = network.getLinks().get(linkId);
        emmisionsByLink.putIfAbsent(linkId,new AnalyzedLink(linkId, matsimLink));

        if (emmisionsByLink.get(linkId).getWarmEmissions().isEmpty()){
            emmisionsByLink.get(linkId).getWarmEmissions().putAll(event.getWarmEmissions());
        } else {
            Map<Pollutant, Double> currentEmissions = emmisionsByLink.get(linkId).getWarmEmissions();
            for (Pollutant pollutant : currentEmissions.keySet()){
                currentEmissions.put(pollutant, currentEmissions.getOrDefault(pollutant,0.) + event.getWarmEmissions().getOrDefault(pollutant, 0.));
            }
            emmisionsByLink.get(linkId).getWarmEmissions().putAll(currentEmissions);
        }

        Id<Vehicle> vehicleId = event.getVehicleId();
        emmisionsByVehicle.putIfAbsent(vehicleId, new AnalyzedVehicle(vehicleId));
        emmisionsByVehicle.get(vehicleId).addDistanceTravelled(matsimLink.getLength());
        emmisionsByVehicle.get(vehicleId).registerPointOfTime(event.getTime());
        //todo currently we get operating times based on free flow conditions
        double speed_ms;
        if (vehicleId.toString().contains("cargoBike")){
            speed_ms = 5.6;
        } else {
            speed_ms = matsimLink.getFreespeed();
        }
        emmisionsByVehicle.get(vehicleId).addOperatingTime(matsimLink.getLength() / speed_ms);



        if (emmisionsByVehicle.get(vehicleId).getWarmEmissions().isEmpty()){
            emmisionsByVehicle.get(vehicleId).getWarmEmissions().putAll(event.getWarmEmissions());
        } else {
            Map<Pollutant, Double> currentEmissions = emmisionsByVehicle.get(vehicleId).getWarmEmissions();
            for (Pollutant pollutant : currentEmissions.keySet()){
                currentEmissions.put(pollutant, currentEmissions.getOrDefault(pollutant, 0.) + event.getWarmEmissions().getOrDefault(pollutant,0.));
            }
            emmisionsByVehicle.get(vehicleId).getWarmEmissions().putAll(currentEmissions);
        }
    }



    @Override
    public void reset(int iteration) {

    }

    @Override
    public void handleEvent(ColdEmissionEvent event) {

        Id<Link> linkId = event.getLinkId();
        Link matsimLink = network.getLinks().get(linkId);
        emmisionsByLink.putIfAbsent(linkId,new AnalyzedLink(linkId, matsimLink));
        if (emmisionsByLink.get(linkId).getColdEmissions().isEmpty()){
            emmisionsByLink.get(linkId).getColdEmissions().putAll(event.getColdEmissions());
        } else {
            Map<Pollutant, Double> currentEmissions = emmisionsByLink.get(linkId).getColdEmissions();
            for (Pollutant pollutant : currentEmissions.keySet()){
                currentEmissions.put(pollutant, currentEmissions.getOrDefault(pollutant,0.) + event.getColdEmissions().getOrDefault(pollutant,0.));
            }
            emmisionsByLink.get(linkId).getColdEmissions().putAll(currentEmissions);
        }

        Id<Vehicle> vehicleId = event.getVehicleId();
        emmisionsByVehicle.putIfAbsent(vehicleId, new AnalyzedVehicle(vehicleId));
        emmisionsByVehicle.get(vehicleId).registerPointOfTime(event.getTime());

        if (emmisionsByVehicle.get(vehicleId).getColdEmissions().isEmpty()){
            emmisionsByVehicle.get(vehicleId).getColdEmissions().putAll(event.getColdEmissions());
        } else {
            Map<Pollutant, Double> currentEmissions = emmisionsByVehicle.get(vehicleId).getColdEmissions();
            for (Pollutant pollutant : currentEmissions.keySet()){
                currentEmissions.put(pollutant, currentEmissions.getOrDefault(pollutant,0.) + event.getColdEmissions().getOrDefault(pollutant,0.));
            }
            emmisionsByVehicle.get(vehicleId).getColdEmissions().putAll(currentEmissions);
        }
    }

    public Map<Id<Vehicle>, AnalyzedVehicle> getEmmisionsByVehicle() {
        return emmisionsByVehicle;
    }

    public Map<Id<Link>, AnalyzedLink> getEmmisionsByLink() {
        return emmisionsByLink;
    }
}
