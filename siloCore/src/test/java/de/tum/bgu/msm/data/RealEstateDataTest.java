//package de.tum.bgu.msm.data;
//
//import cern.colt.matrix.io.MatrixVectorWriter;
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
//import de.tum.bgu.msm.utils.SiloUtil;
//import de.tum.bgu.msm.container.DataContainer;
//import de.tum.bgu.msm.properties.Properties;
//import junitx.framework.FileAssert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Locale;
//
//public class RealEstateDataTest {
//
//    @BeforeClass
//    public static void setup() {
//        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//    }
//
//    @Test
//    public void testdwellingCountByTypeAndRegion() {
//        DataContainer dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
//        int[][] count = realEstate.getDwellingCountByTypeAndRegion();
//
//        Locale.setDefault(Locale.ENGLISH);
//        MatrixVectorWriter writerZone2Region = null;
//        try {
//            new File("./test/output/").mkdirs();
//            writerZone2Region = new MatrixVectorWriter(new FileWriter("./test/output/dwellingCountByTypeAndRegion.txt"));
//            for(int i = 0; i < count.length; i++) {
//                writerZone2Region.printArray(count[i]);
//            }
//            writerZone2Region.flush();
//            writerZone2Region.close();
//
//            FileAssert.assertEquals("dwelling count by type and region is different.", new File("./test/input/dwellingCountByTypeAndRegion.txt"), new File("./test/output/dwellingCountByTypeAndRegion.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testVacancyRateByTypeAndRegion() {
//        DataContainer dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
//        double[][] vacRate = realEstate.getVacancyRateByTypeAndRegion();
//
//        Locale.setDefault(Locale.ENGLISH);
//        MatrixVectorWriter writerZone2Region = null;
//        try {
//            new File("./test/output/").mkdirs();
//            writerZone2Region = new MatrixVectorWriter(new FileWriter("./test/output/vacancyRateByTypeAndRegion.txt"));
//            for(int i = 0; i < vacRate.length; i++) {
//                writerZone2Region.printArray(vacRate[i]);
//            }
//            writerZone2Region.flush();
//            writerZone2Region.close();
//
//            FileAssert.assertEquals("vacancy Rate by type and region is different.", new File("./test/input/vacancyRateByTypeAndRegion.txt"), new File("./test/output/vacancyRateByTypeAndRegion.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
