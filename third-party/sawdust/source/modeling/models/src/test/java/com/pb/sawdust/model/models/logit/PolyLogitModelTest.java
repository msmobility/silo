package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.TensorMarginal;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.utility.LinearCompositeUtility;
import com.pb.sawdust.model.models.utility.LinearUtility;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.ChoiceUtil;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.model.models.provider.hub.PolyDataProvider;
import com.pb.sawdust.model.models.provider.hub.SimplePolyDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.index.MixedIndex;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.*;

/**
 * The {@code PolyLogitModelTest} ...
 *
 * @author crf <br/>
 *         Started Oct 4, 2010 12:23:32 PM
 */
public class PolyLogitModelTest extends LogitModelTest {
    public static final String POLY_LOGIT_MODE = "poly logit mode";

    private static enum PolyLogitModelMode {
        ONLY_SHARED_VARIABLES,
        NON_SHARED_VARIABLES
    }
    private PolyLogitModelMode mode;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected PolyLogitModel<Choice> polyLogitModel;
    protected LinearUtility sharedUtility;
    protected Map<Choice,LinearUtility> choiceUtilities;
    protected SetList<Choice> choices;

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> contexts = new LinkedList<Map<String,Object>>();
        for (PolyLogitModelMode plm : PolyLogitModelMode.values())
            contexts.add(buildContext(POLY_LOGIT_MODE,plm));
        addClassRunContext(this.getClass(),contexts);
        return super.getAdditionalTestClasses();
    }

    protected LogitModel<Choice> getLogitModel(String name, Map<Choice,? extends Utility> utilityMap, Map<Choice,DataFilter> availabilityFilters) {
        PolyLogitModel<Choice> m;
        switch (mode) {
            case ONLY_SHARED_VARIABLES : m = new PolyLogitModel<Choice>(name,utilityMap.keySet(),sharedUtility,ArrayTensor.getFactory()); break;
            case NON_SHARED_VARIABLES : m = new PolyLogitModel<Choice>(name,choiceUtilities,sharedUtility,ArrayTensor.getFactory()); break;
            default : throw new IllegalStateException("Shouldn't be here");
        }
        return availabilityFilters == null ? m : PolyLogitModel.getModelWithAvailabilityFilters(m,availabilityFilters);
    }

    protected Map<Choice,? extends Utility> getUtilityMap(String sharedVariable) {
        //hacky because we're defining the variables within this method, but should be ok for now
        SetList<String> vars = new LinkedSetList<String>();
        List<Double> coefs = new LinkedList<Double>();
        for (int i : range(random.nextInt(4,10))) {
            vars.add(random.nextAsciiString(8));
            coefs.add(random.nextDouble());
        }
        if (mode == PolyLogitModelMode.ONLY_SHARED_VARIABLES) {
            //add here if only shared variables, otherwise add to choice utilities to ensure still working
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
        }
        sharedUtility = new SimpleLinearUtility(new LinkedSetList<String>(vars),coefs,ArrayTensor.getFactory());

        Map<Choice,Utility> um = new HashMap<Choice,Utility>();
        Set<ChoiceUtil.IntChoice> choices = ChoiceUtil.getChoiceRange(random.nextInt(3,10));

        choiceUtilities = new HashMap<Choice,LinearUtility>();
        if (mode == PolyLogitModelMode.NON_SHARED_VARIABLES) {
            for (Choice c : choices) {
                vars = new LinkedSetList<String>();
                coefs = new LinkedList<Double>();
                for (int i : range(random.nextInt(4,10))) {
                    String var;
                    while (sharedUtility.getVariables().contains(var = random.nextAsciiString(8))); //loop to get unique variable
                    vars.add(var);
                    coefs.add(random.nextDouble());
                }
                vars.add(sharedVariable);
                coefs.add(random.nextDouble());
                choiceUtilities.put(c, new SimpleLinearUtility(vars, coefs, ArrayTensor.getFactory()));
            }

            for (Choice c : choiceUtilities.keySet())
                um.put(c,new LinearCompositeUtility(ArrayTensor.getFactory(),sharedUtility,choiceUtilities.get(c)));
        } else {
            for (Choice c : choices)
                um.put(c,sharedUtility);
        }
        this.choices = new LinkedSetList<Choice>(um.keySet());
        return um;
    }

    protected PolyDataProvider<Choice> getPolyProvider(DataProvider provider) {
        return getPolyProvider(choiceUtilities,sharedUtility,provider,false,false,null);
    }

    protected PolyDataProvider<Choice> getPolyProvider(DataProvider provider, boolean bad) {
        return getPolyProvider(choiceUtilities,sharedUtility,provider,bad,false,null);
    }

    protected PolyDataProvider<Choice> getPolyProvider(DataProvider provider, Choice choiceToDrop) {
        return getPolyProvider(choiceUtilities,sharedUtility,provider,false,false,choiceToDrop);
    }

    protected PolyDataProvider<Choice> getPolyProviderExtraChoice(DataProvider provider) {
        return getPolyProvider(choiceUtilities,sharedUtility,provider,false,true,null);
    }

    protected PolyDataProvider<Choice> getPolyProvider(Map<Choice,LinearUtility> choiceUtilities, LinearUtility sharedUtilities, DataProvider provider, boolean bad, boolean addChoice, Choice choiceToDrop) {
        //if bad=true, then data will be missing; if choiceToDrop <> null, then a choice will be missing

        Choice ec = new ChoiceUtil.DefaultContainerChoice<Void>(null) {};
        SetList<Choice> css = new LinkedSetList<Choice>(choices);
        if (addChoice)
            css.add(ec);

        SimplePolyDataProvider<Choice> pp = new SimplePolyDataProvider<Choice>(css,ArrayTensor.getFactory());
        String variableToDrop = bad ? random.getRandomValue(pp.getPolyDataVariables()) : null;
        if (choiceUtilities.size() > 0) {
            //add some choices
            for (Choice c : css) {
                if (c.equals(choiceToDrop))
                    continue;
                Map<String,double[]> choiceData = new HashMap<String,double[]>();
                if (choiceUtilities.containsKey(c)) {
                    for (String variable : choiceUtilities.get(c).getVariables())
                        if (!variable.equals(variableToDrop))
                            choiceData.put(variable,data.getVariableData(variable));
                    if (availabilities != null && availabilities.containsKey(c)) {
                        //add availability variable
                        BooleanVector avv = availabilities.get(c);
                        double[] av = new double[avv.size(0)];
                        for (int i : range(av.length))
                            if (avv.getCell(i))
                                av[i] = 1.0;
                        choiceData.put(getAvailabilityVariable(c),av);
                    }
                } else {
                    choiceData.put(random.nextAsciiString(6),new double[provider.getDataLength()]);
                }
                pp.addKeyedProvider(c,new SimpleDataProvider(choiceData,ArrayTensor.getFactory()));
            }
        }

        for (String variable : sharedUtilities.getVariableSet()) {
            if (variable.equals(variableToDrop))
                continue;
            //poly provider IS NOT consistent with source provider! - we want to test variances across choices
            DoubleMatrix m = (DoubleMatrix) ArrayTensor.getFactory().doubleTensor(data.getDataLength(),choices.size() - (choiceToDrop == null ? 0 : 1) + (addChoice ? 1 : 0));
            int counter = 0;
            for (Choice c : choices) {
                if (c.equals(choiceToDrop))
                    continue;
                for (int i : range(m.size(0)))
                    m.setCell(random.nextDouble(),i,counter);
                counter++;
            }
            if (addChoice) {
                for (int i : range(m.size(0)))
                    m.setCell(random.nextDouble(),i,counter);
            }
            pp.addPolyData(variable,m);
        }

        return pp;
    }


    @Before
    public void beforeTest() {
        mode = (PolyLogitModelMode) getTestData(POLY_LOGIT_MODE);
        super.beforeTest();
        polyLogitModel = (PolyLogitModel<Choice>) logitModel;
        setAdditionalTestInformation(" (" + mode + ")");
    }

    @Test
    public void testGetUtilities() {
        Map<Choice,? extends Utility> utMap = logitModel.getUtilities();
        //check by hand
        assertEquals(utilityMap.keySet(),utMap.keySet());
        for (Choice c : utMap.keySet()) {
            Utility umu = utilityMap.get(c);
            Utility utmu = utMap.get(c);
            assertTrue(utmu instanceof LinearUtility);
            //might be setlists, so have to move them to sets to ensure order ignorance
            assertEquals(new HashSet<String>(umu.getVariableSet()),new HashSet<String>(utmu.getVariableSet()));
            //verify coefficients one by one
            for (String variable : umu.getVariableSet())
                assertAlmostEquals(umu.getCoefficients().getCell(umu.getVariables().indexOf(variable)),
                                   utmu.getCoefficients().getCell(utmu.getVariables().indexOf(variable)));
        }
    }

    private DoubleMatrix getPolyExponentiatedUtilities(PolyDataProvider<Choice> data) {
        DoubleMatrix m = (DoubleMatrix) ArrayTensor.getFactory().doubleTensor(data.getDataLength(),choices.size());
        IdDoubleMatrix<Object> mm = (IdDoubleMatrix<Object>) m.getReferenceTensor(MixedIndex.replaceIds(m.getIndex(),1,choices));
        for (Choice c : choices) {
            DoubleVector v = utilityMap.get(c).getUtilities(data.getFullProvider(c));
            if (availabilities != null && availabilities.containsKey(c) && mode == PolyLogitModelTest.PolyLogitModelMode.NON_SHARED_VARIABLES)
                for (int i : range(v.size(0)))
                    if (!availabilities.get(c).getCell(i))
                        v.setCell(LogitModel.UNAVAILABLE_UTILITY,i);
            for (int i : range(v.size(0)))
                mm.setCellById(v.getCell(i),i,c);
        }
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        return (DoubleMatrix) cwtc.calculate(m, NumericFunctions.EXP);
    }

    @Test
    public void testGetExponentiatedUtilitiesPoly() {
        PolyDataProvider<Choice> pp = getPolyProvider(data);
        assertTrue(TensorUtil.almostEquals(getPolyExponentiatedUtilities(pp),polyLogitModel.getExponentiatedUtilities(pp)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesPolyBadVariable() {
        polyLogitModel.getExponentiatedUtilities(getPolyProvider(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesPolyMissingChoice() {
        polyLogitModel.getExponentiatedUtilities(getPolyProvider(data,random.getRandomValue(choices)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesPolyExtraChoice() {
        polyLogitModel.getExponentiatedUtilities(getPolyProviderExtraChoice(data));
    }

    @Test
    public void testGetExpsumsPoly() {
        TensorMarginal tm = new TensorMarginal(ArrayTensor.getFactory());
        PolyDataProvider<Choice> pp = getPolyProvider(data);
        assertTrue(TensorUtil.almostEquals(tm.getMarginal(getPolyExponentiatedUtilities(pp),1, TensorMarginal.Marginal.SUM),polyLogitModel.getExpsums(pp)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsPolyBadVariable() {
        polyLogitModel.getExpsums(getPolyProvider(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsPolyMissingChoice() {
        polyLogitModel.getExpsums(getPolyProvider(data,random.getRandomValue(choices)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsPolyExtraChoice() {
        polyLogitModel.getExpsums(getPolyProviderExtraChoice(data));
    } 

    @Test
    public void testGetLogsumsPoly() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        TensorMarginal tm = new TensorMarginal(ArrayTensor.getFactory());
        PolyDataProvider<Choice> pp = getPolyProvider(data);
        assertTrue(TensorUtil.almostEquals(cwtc.calculate(tm.getMarginal(getPolyExponentiatedUtilities(pp),1, TensorMarginal.Marginal.SUM),NumericFunctions.LOG),polyLogitModel.getLogsums(pp)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsPolyBadVariable() {
        polyLogitModel.getLogsums(getPolyProvider(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsPolyMissingChoice() {
        polyLogitModel.getLogsums(getPolyProvider(data,random.getRandomValue(choices)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsPolyExtraChoice() {
        polyLogitModel.getLogsums(getPolyProviderExtraChoice(data));
    }
}
