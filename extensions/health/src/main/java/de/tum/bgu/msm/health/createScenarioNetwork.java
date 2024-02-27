package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class createScenarioNetwork {

    public static void main(String[] args) {

        Network scenarioNetwork = NetworkUtils.createNetwork();
        new MatsimNetworkReader(scenarioNetwork).readFile("F:\\models\\healthModel\\muc\\input\\mito\\trafficAssignment/studyNetworkDenseCarHealth_Hbefa.xml.gz");

        for(Link link : scenarioNetwork.getLinks().values()){
            double freeSpeed = link.getFreespeed();
            link.setFreespeed(freeSpeed*2);
        }

        new NetworkWriter(scenarioNetwork).writeV2("F:\\models\\healthModel\\muc\\input\\mito\\trafficAssignment/studyNetworkDenseCarHealth_doubleSpeed.xml.gz");
    }
}
