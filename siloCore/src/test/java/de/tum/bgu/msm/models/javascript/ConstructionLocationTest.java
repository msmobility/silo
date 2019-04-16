//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
//import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionLocationStrategy;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//public class ConstructionLocationTest {
//
//    private DefaultConstructionLocationStrategy calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionLocationCalc"));
//        calculator = new DefaultConstructionLocationStrategy(reader);
//    }
//
//    @Test
//    public void testModel() {
//        Assert.assertEquals(100, calculator.calculateConstructionProbability(DefaultDwellingTypeImpl.SFD, 100, 100), 0.);
//    }
//}
