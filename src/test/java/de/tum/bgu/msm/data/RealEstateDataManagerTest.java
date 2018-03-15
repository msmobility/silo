package de.tum.bgu.msm.data;

import cern.colt.matrix.io.MatrixVectorWriter;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import junitx.framework.FileAssert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class RealEstateDataManagerTest {

    @Before
    public void setup() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
    }

    @Test
    public void testVacancyRateByTypeAndRegion() {
        SiloDataContainer dataContainer = SiloDataContainer.createSiloDataContainer();
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        int[][] count = realEstate.getDwellingCountByTypeAndRegion();

        Locale.setDefault(Locale.ENGLISH);
        MatrixVectorWriter writerZone2Region = null;
        try {
            new File("./test/output/").mkdirs();
            writerZone2Region = new MatrixVectorWriter(new FileWriter("./test/output/dwellingCountByTypeAndRegion.txt"));
            for(int i = 0; i < count.length; i++) {
                writerZone2Region.printArray(count[i]);
            }
            writerZone2Region.flush();
            writerZone2Region.close();

            FileAssert.assertEquals("dwelling count by type and region is different.", new File("./test/input/dwellingCountByTypeAndRegion.txt"), new File("./test/output/dwellingCountByTypeAndRegion.txt"));
//
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
