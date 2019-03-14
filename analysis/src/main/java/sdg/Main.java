package sdg;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.properties.Properties;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);

        DataContainer dataContainer = DataContainer.loadSiloDataContainer(properties);

        SDGCalculator.calculateSdgIndicators(dataContainer, Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName,
                Properties.get().main.startYear);

    }

}
