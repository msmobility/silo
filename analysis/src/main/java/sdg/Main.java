package sdg;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0], Implementation.MUNICH);

        SiloDataContainer siloDataContainer = SiloDataContainer.loadSiloDataContainer(properties);

        SDGCalculator.calculateSdgIndicators(siloDataContainer, Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName,
                Properties.get().main.startYear);

    }

}
