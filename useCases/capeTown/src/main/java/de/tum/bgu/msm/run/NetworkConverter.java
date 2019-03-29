package de.tum.bgu.msm.run;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class NetworkConverter {

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader("EPSG:22235", TransformationFactory.HARTEBEESTHOEK94_LO19, network);
        reader.readFile("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\matsim\\2017-10-03_network.xml.gz");
        new NetworkWriter(network).write("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\matsim\\2017-10-03_network_HARTEBEESTHOEK94_LO19.xml.gz");
    }
}
