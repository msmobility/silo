package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;
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
        for(Person p: household.getPersons().values()) {
            p.setIncome(p.getAnnualIncome()+50000);
        }
        NoiseDwelling quietDwelling = new NoiseDwellingIml(new DwellingFactoryImpl().createDwelling(99999, 1, new Coordinate(40000,16000), -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 4, 3, 800, 1990));
        quietDwelling.setNoiseImmision(40);
        final double v = strategy.calculateHousingUtility(household, quietDwelling);

        NoiseDwelling noiseDwelling = new NoiseDwellingIml(new DwellingFactoryImpl().createDwelling(99999, 1, new Coordinate(40000,16000), -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 4, 3, 800, 1990));
        noiseDwelling.setNoiseImmision(60);
        final double v1 = strategy.calculateHousingUtility(household, noiseDwelling);

        NoiseDwelling noiseDwelling2 = new NoiseDwellingIml(new DwellingFactoryImpl().createDwelling(99999, 1, new Coordinate(40000,16000), -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 4, 3, 800, 1990));
        noiseDwelling2.setNoiseImmision(66);
        final double v2 = strategy.calculateHousingUtility(household, noiseDwelling2);


    }
}
