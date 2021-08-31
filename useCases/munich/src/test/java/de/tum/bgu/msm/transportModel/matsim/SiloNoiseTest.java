package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.MultiFileResultsMonitorMuc;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;
import de.tum.bgu.msm.matsim.noise.NoiseModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.scenarios.noise.*;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.testcases.MatsimTestUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SiloNoiseTest {

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    private static final Logger log = Logger.getLogger(SiloMatsimMucTest.class);

    @Test
    public final void testMain() throws IOException {

        String path = "./test/muc/siloMatsimMucTest.properties";
        Config config = ConfigUtils.loadConfig("./test/muc/matsim_input/config.xml") ;

        try {
            utils.initWithoutJUnitForFixture(this.getClass(), this.getClass().getMethod("testMain", null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        File dir = new File("./test/muc/scenOutput/test/");

        config.global().setNumberOfThreads(1);
        config.parallelEventHandling().setNumberOfThreads(1);
        config.qsim().setNumberOfThreads(1);

        Properties properties = SiloUtil.siloInitialization(path);

        NoiseDataContainerImpl dataContainer = DataBuilderNoise.getModelDataForMuc(properties, config);
        DataBuilderNoise.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMucNoise.getModelContainerForMuc(dataContainer, properties, config);

        ResultsMonitor resultsMonitor = new MultiFileResultsMonitorMuc(dataContainer, properties);

        final List<ModelUpdateListener> modelUpdateListeners = modelContainer.getModelUpdateListeners();
        for(ModelUpdateListener listenr: modelUpdateListeners) {
            if(listenr instanceof NoiseModel) {
                listenr.endYear(2011);
            }
        }

        NoiseDwelling refDwelling = new NoiseDwellingIml(new DwellingFactoryImpl().createDwelling(1, 20, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 1, 1, 1000, 1980));
        refDwelling.setNoiseImmision(45);

        Household poor = new HouseholdMuc(1, 1, 2);
        poor.addPerson(new PersonMuc(1, 23, Gender.MALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 6000));
        poor.addPerson(new PersonMuc(2, 23, Gender.FEMALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 6000));


        Household rich = new HouseholdMuc(2, 1, 2);
        rich.addPerson(new PersonMuc(1, 23, Gender.MALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 35000));
        rich.addPerson(new PersonMuc(2, 23, Gender.FEMALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 35000));

        Household avg = new HouseholdMuc(3, 1, 2);
        avg.addPerson(new PersonMuc(1, 23, Gender.MALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 15000));
        avg.addPerson(new PersonMuc(2, 23, Gender.FEMALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 15000));

        final TravelTimes travelTimes = new TravelTimes() {
            @Override
            public double getTravelTime(Location location, Location location1, double v, String s) {
                return 10;
            }

            @Override
            public double getTravelTimeFromRegion(Region region, Zone zone, double v, String s) {
                return 10;
            }

            @Override
            public double getTravelTimeToRegion(Zone zone, Region region, double v, String s) {
                return 10;
            }

            @Override
            public IndexedDoubleMatrix2D getPeakSkim(String s) {
                return null;
            }

            @Override
            public TravelTimes duplicate() {
                return null;
            }
        };

        HuntNoiseSensitiveDwellingUtilityStrategy strategy = new HuntNoiseSensitiveDwellingUtilityStrategy(travelTimes, dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(), null);
        HuntNoiseInsensitiveDwellingUtilityStrategy strategyIns = new HuntNoiseInsensitiveDwellingUtilityStrategy(travelTimes, dataContainer.getJobDataManager(), dataContainer.getRealEstateDataManager(), null);

        BufferedWriter writer = new BufferedWriter(new FileWriter("C:/users/nico/desktop/noiseutil9.csv"));
        writer.write("noise,price,type,poorUitl,mediumUtil,richUtil,poorUitlIn,mediumUtilIn,richUtilIn");
        writer.newLine();

        Random random = new Random(42);

        dataContainer.getRealEstateDataManager().addDwelling(refDwelling);
        for(Dwelling dwelling: dataContainer.getRealEstateDataManager().getDwellings()) {
            ((NoiseDwelling)dwelling).setNoiseImmision(((NoiseDwelling) dwelling).getNoiseImmission()+random.nextInt(25));
            if(dwelling == refDwelling) {
                continue;
            }
            double price = 1000;
            if(((NoiseDwelling) dwelling).getNoiseImmission() > 75) {
                price = price * (1 -0.1005);
            } else if(((NoiseDwelling) dwelling).getNoiseImmission() > 65) {
                price = price * (1 -0.0583);
            } else if(((NoiseDwelling) dwelling).getNoiseImmission() > 55) {
                price = price * (1  -0.0360);
            }
            dwelling.setPrice((int) price);

            final double v = strategy.calculateHousingUtility(poor, dwelling);
            final double v2 = strategy.calculateHousingUtility(avg, dwelling);
            final double v3 = strategy.calculateHousingUtility(rich, dwelling);

            final double v4 = strategyIns.calculateHousingUtility(poor, dwelling);
            final double v5 = strategyIns.calculateHousingUtility(avg, dwelling);
            final double v6 = strategyIns.calculateHousingUtility(rich, dwelling);
            writer.write(((NoiseDwelling)dwelling).getNoiseImmission()+","+dwelling.getPrice()
                    +","+dwelling.getType()+","+v+","+v2+","+v3+","+v4+","+v5+","+v6);
            writer.newLine();
        }
        writer.flush();
        writer.close();



//        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
//        model.addResultMonitor(resultsMonitor);
//        model.runModel();
    }
}
