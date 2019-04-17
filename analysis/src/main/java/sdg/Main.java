package sdg;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        DataContainerMuc dataContainer = DataBuilder.getModelDataForMuc(properties);
        DataBuilder.read(properties, dataContainer);

        SDGCalculator.calculateSdgIndicators(dataContainer, Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName,
                Properties.get().main.startYear);

    }

}
