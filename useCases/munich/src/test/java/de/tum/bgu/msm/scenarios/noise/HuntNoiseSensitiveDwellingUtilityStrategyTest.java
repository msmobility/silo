package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.dwelling.DwellingImpl;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.io.File;

public class HuntNoiseSensitiveDwellingUtilityStrategyTest {

    @Test
    public void test() {
        String path = "./test/muc/siloMatsimMucTest.properties";
        Config config = ConfigUtils.loadConfig("./test/muc/matsim_input/config.xml");

        File dir = new File("./test/muc/scenOutput/test/");


        Properties properties = SiloUtil.siloInitialization(path);

        NoiseDataContainerImpl dataContainer = DataBuilderNoise.getModelDataForMuc(properties, config);
        DataBuilderNoise.read(properties, dataContainer);
        HuntNoiseSensitiveDwellingUtilityStrategy strategy = new HuntNoiseSensitiveDwellingUtilityStrategy(dataContainer.getTravelTimes(),
                dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(), null);
        int hhid = 1249939;
        final Household household = dataContainer.getHouseholdDataManager().getHouseholdFromId(hhid);
        Dwelling dwelling = new NoiseDwellingIml(new DwellingFactoryImpl().createDwelling(99999, 1, new Coordinate(40000,16000), -1, DefaultDwellingTypeImpl.SFD, 4, 3, 1000, 1990));
        strategy.calculateHousingUtility(household, dwelling);


    }
}
