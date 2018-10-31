package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class RunSyntheticPopulation {

    public static void main(String[] args) {
        Properties properties = SiloUtil.siloInitialization(Implementation.valueOf(args[0]), args[1]);
        ResourceBundle bundle = null;
        try {
            bundle = new PropertyResourceBundle(new FileReader(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SyntheticPopulationGenerator spg = new SyntheticPopulationGenerator(bundle, properties);

        spg.run();
    }
}
