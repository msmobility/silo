package de.tum.bgu.msm.syntheticPopulationGenerator.kagawa;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SyntheticPopulationGeneratorKagawa {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorKagawa.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();
    private static ResourceBundle rb;

    public static void main (String[] args) {

        SiloUtil.siloInitialization(Implementation.KAGAWA, args[0]);
        try {
            rb = new PropertyResourceBundle(new FileReader(args[0]));
            PropertiesSynPop.initializePropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0])), Implementation.MUNICH);
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopJP(rb, dataSetSynPop);
        syntheticPop.runSP();
    }
}
