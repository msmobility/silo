package sdg;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        SDGCalculator.calculateSdgIndicators(dataContainer, Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName,
                Properties.get().main.startYear);

    }

}
