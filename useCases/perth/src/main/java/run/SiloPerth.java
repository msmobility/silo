package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.OmxTravelTimesWriter;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Implements SILO for for the Maryland Statewide Transportation Model
 *
 * @author Rolf Moeckel
 */
public class SiloPerth {

    private final static Logger logger = Logger.getLogger(SiloPerth.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for Perth");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties);
        DataBuilder.readInput(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilder.getModelContainerForMstm(dataContainer, properties, config);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer, new DefaultResultsMonitor(dataContainer, properties));
        model.runModel();

        TravelTimes tt = dataContainer.getTravelTimes();
        GeoData geo = dataContainer.getGeoData();
        Map<Integer, Zone> zoneMap = geo.getZones();
        Collection<Zone> zoneCollection = new ArrayList<Zone>(zoneMap.values());
        OmxTravelTimesWriter oxmTTWriter = new OmxTravelTimesWriter(tt , zoneCollection);
        oxmTTWriter.writeTravelTimes("./output/skim"+".omx", "skim", TransportMode.car);

        logger.info("Finished SILO.");
    }
}
