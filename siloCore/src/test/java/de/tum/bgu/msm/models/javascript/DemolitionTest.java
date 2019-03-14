//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
//import de.tum.bgu.msm.utils.SiloUtil;
//import de.tum.bgu.msm.container.DataContainer;
//import de.tum.bgu.msm.data.dwelling.Dwelling;
//import de.tum.bgu.msm.data.dwelling.DwellingUtils;
//import de.tum.bgu.msm.models.realEstate.demolition.DefaultDemolitionStrategy;
//import de.tum.bgu.msm.properties.Properties;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//public class DemolitionTest {
//
//    private DefaultDemolitionStrategy calculator;
//    private Dwelling dwelling1;
//    private Dwelling dwelling2;
//
//    @BeforeClass
//    public static void initializeProperties() {
//        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//    }
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));
//        calculator = new DefaultDemolitionStrategy(reader);
//        DataContainer dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        dwelling1 = DwellingUtils.getFactory().createDwelling(1,-1, null,1, DefaultDwellingTypeImpl.SFD, 1,1,1,1,1);
//        dataContainer.getRealEstateDataManager().addDwelling(dwelling1);
//        dwelling2 = DwellingUtils.getFactory().createDwelling(1,-1, null,1, DefaultDwellingTypeImpl.SFD, 1,5,1,1,5);
//        dataContainer.getRealEstateDataManager().addDwelling(dwelling2);
//
//    }
//
//    @Test
//    public void testModel() {
//        Assert.assertEquals(0.0001, calculator.calculateDemolitionProbability(dwelling1,0), 0.);
//    }
//
//    @Test (expected = RuntimeException.class)
//    public void testModelFailure() {
//        calculator.calculateDemolitionProbability(dwelling2, 0);
//    }
//}
