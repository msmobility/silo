import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

/**
 * Implements SILO for the Great Manchester
 *
 * @author Qin Zhang*/


public class RunIsuhAnalysis {

    private final static Logger logger = Logger.getLogger(RunIsuhAnalysis.class);

    private final static String networkFilePath = "C:\\Users\\Corin Staves\\Documents\\manchester\\JIBE\\network\\network.xml";

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Great Manchester");
        DefaultDataContainer dataContainer = DataBuilderMCR.getModelDataForManchester(properties, config);
        DataBuilderMCR.read(properties, dataContainer);

        // Read network
        logger.info("Reading MATSim network...");
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(networkFilePath);

        // Initiate calculator
        IsuhCalculator isuhCalculator = new IsuhCalculator(network, dataContainer, 14);

        // Extract workers
        isuhCalculator.extractWorkers(0.1);

        // Write workers
        isuhCalculator.writeWorkers();

        // Calculate indicators
//        isuhCalculator.calculateIndicators();

        // Calculate modes

        logger.info("Finished SILO.");
    }



}
