package de.tum.bgu.msm.transportModel.matsim;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.ResultsMonitorMuc;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.matsim.noise.NoiseModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.scenarios.noise.DataBuilderNoise;
import de.tum.bgu.msm.scenarios.noise.ModelBuilderMucNoise;
import de.tum.bgu.msm.scenarios.noise.NoiseDataContainerImpl;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.testcases.MatsimTestUtils;

import java.io.File;

public class SiloNoiseTest {

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    private static final Logger log = Logger.getLogger(SiloMatsimMucTest.class);

    @Test
    public final void testMain() {

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
        ResultsMonitor resultsMonitor = new ResultsMonitorMuc(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.runModel();
    }
}
