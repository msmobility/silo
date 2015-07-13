package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.model.models.provider.CompositeDataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.ChoiceUtil;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.*;

/**
 * The {@code NestedLogitModelTest} ...
 *
 * @author crf <br/>
 *         Started Oct 1, 2010 10:26:28 PM
 */
public class NestedLogitModelTest extends LogitModelTest {
    protected NestedLogitModel<Choice> nestedLogitModel;
    protected Map<Choice,NestedLogitUtility<Choice>> nestedUtilityMap;
    protected Map<Choice,NestedLogitUtility<Choice>> allUtilitiesMap;
    protected Set<Choice> finalChoices;
    protected Set<Choice> nestedChoices;
    protected Map<Choice,Set<Choice>> nestChoices;
    protected Map<Choice,Choice> upChoices;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected DataProviderHub<Choice> getHub(DataProvider provider) {
        return getHub(allUtilitiesMap,provider,false,null);
    }

    protected DataProviderHub<Choice> getHub(DataProvider provider, boolean bad) {
        return getHub(allUtilitiesMap,provider,bad,null);
    }

    protected DataProviderHub<Choice> getHub(DataProvider provider, Choice choiceToDrop) {
        return getHub(allUtilitiesMap,provider,false,choiceToDrop);
    }

    @SuppressWarnings("unchecked") //let nX be a Set<Choice> without any issues
    protected Map<Choice,? extends Utility> getUtilityMap(String sharedVariable) {
        //model structure:
        //        a----+-----b------c
        //      d-+-e     f--+--g----h
        //        i-+-j

        allUtilitiesMap = new HashMap<Choice,NestedLogitUtility<Choice>>();

        Set<? extends Choice> n1 = ChoiceUtil.getStringChoiceList("a","b","c");
        Set<? extends Choice> n2 = ChoiceUtil.getStringChoiceList("d","e");
        Set<? extends Choice> n3 = ChoiceUtil.getStringChoiceList("f","g","h");
        Set<? extends Choice> n4 = ChoiceUtil.getStringChoiceList("i","j");
        nestedChoices = new HashSet<Choice>();
        nestedChoices.addAll(n2);
        nestedChoices.addAll(n3);
        nestedChoices.addAll(n4);
        finalChoices = new HashSet<Choice>();
        finalChoices.addAll(n3);
        finalChoices.addAll(n4);
        finalChoices.add(new ChoiceUtil.StringChoice("d"));
        finalChoices.add(new ChoiceUtil.StringChoice("c"));
        nestChoices = new HashMap<Choice,Set<Choice>>();
        upChoices = new HashMap<Choice,Choice>();
        for (Choice c : n1) {
            nestChoices.put(c,(Set<Choice>) n1);
            upChoices.put(c,null);
        }
        for (Choice c : n2) {
            nestChoices.put(c,(Set<Choice>) n2);
            upChoices.put(c,new ChoiceUtil.StringChoice("a"));
        }
        for (Choice c : n3) {
            nestChoices.put(c,(Set<Choice>) n3);
            upChoices.put(c,new ChoiceUtil.StringChoice("b"));
        }
        for (Choice c : n4) {
            nestChoices.put(c,(Set<Choice>) n4);
            upChoices.put(c,new ChoiceUtil.StringChoice("e"));
        }

        Map<Choice,NestedLogitUtility<Choice>> nest1 = new HashMap<Choice,NestedLogitUtility<Choice>>();
        Map<Choice,NestedLogitUtility<Choice>> nest2 = new HashMap<Choice,NestedLogitUtility<Choice>>();
        Map<Choice,NestedLogitUtility<Choice>> nest3 = new HashMap<Choice,NestedLogitUtility<Choice>>();
        Map<Choice,NestedLogitUtility<Choice>> nest4 = new HashMap<Choice,NestedLogitUtility<Choice>>();


        for (Choice c : n4) {
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coefs = new LinkedList<Double>();
            for (int i : range(random.nextInt(4,10))) {
                vars.add(random.nextAsciiString(8));
                coefs.add(random.nextDouble());
            }
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
            nest4.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,ArrayTensor.getFactory()));
        }
        NestedLogitModel<Choice> m4 = new NestedLogitModel<Choice>("nest4",nest4,ArrayTensor.getFactory());
        double m4LogsumParam = random.nextDouble();

        for (Choice c : n3) {
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coefs = new LinkedList<Double>();
            for (int i : range(random.nextInt(4,10))) {
                vars.add(random.nextAsciiString(8));
                coefs.add(random.nextDouble());
            }
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
            nest3.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,ArrayTensor.getFactory()));
        }
        NestedLogitModel<Choice> m3 = new NestedLogitModel<Choice>("nest3",nest3,ArrayTensor.getFactory());
        double m3LogsumParam = random.nextDouble();

        for (Choice c : n2) {
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coefs = new LinkedList<Double>();
            for (int i : range(random.nextInt(4,10))) {
                vars.add(random.nextAsciiString(8));
                coefs.add(random.nextDouble());
            }
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
            if (c.equals(new ChoiceUtil.StringChoice("e")))
                nest2.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,m4,m4LogsumParam,ArrayTensor.getFactory()));
            else
                nest2.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,ArrayTensor.getFactory()));
        }
        NestedLogitModel<Choice> m2 = new NestedLogitModel<Choice>("nest2",nest2,ArrayTensor.getFactory());
        double m2LogsumParam = random.nextDouble();

        for (Choice c : n1) {
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coefs = new LinkedList<Double>();
            for (int i : range(random.nextInt(4,10))) {
                vars.add(random.nextAsciiString(8));
                coefs.add(random.nextDouble());
            }
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
            if (c.equals(new ChoiceUtil.StringChoice("a")))
                nest1.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,m2,m2LogsumParam,ArrayTensor.getFactory()));
            else if (c.equals(new ChoiceUtil.StringChoice("b")))
                nest1.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,m3,m3LogsumParam,ArrayTensor.getFactory()));
            else
                nest1.put(c,new NestedLogitUtility<Choice>(new LinkedSetList<String>(vars),coefs,ArrayTensor.getFactory()));
        }
        allUtilitiesMap.putAll(nest1);    
        allUtilitiesMap.putAll(nest2);
        allUtilitiesMap.putAll(nest3);
        allUtilitiesMap.putAll(nest4);

        return nest1;
    }

    private Choice getRandomChoice(Set<Choice> choices) {
        Iterator<Choice> it = choices.iterator();
        Choice c = null; //won't ever be null, so no worries
        for (int i : range(random.nextInt(1,choices.size())))
            c = it.next();
        return c;
    }

    @SuppressWarnings("unchecked") //map will be correct
    protected LogitModel<Choice> getLogitModel(String name, Map<Choice,? extends Utility> utilityMap, Map<Choice,DataFilter> availabilityFilters) {
        if (availabilityFilters == null)
            return new NestedLogitModel<Choice>(name,(Map<Choice,NestedLogitUtility<Choice>>) utilityMap,factory);
        else
            return new NestedLogitModel<Choice>(name,(Map<Choice,NestedLogitUtility<Choice>>) utilityMap,availabilityFilters,factory);
    }

    @Before
    @SuppressWarnings("unchecked") //map will be correct
    public void beforeTest() {
        super.beforeTest();
        nestedLogitModel = (NestedLogitModel<Choice>) logitModel;
        nestedUtilityMap = (Map<Choice,NestedLogitUtility<Choice>>) utilityMap;
        //replace data with full data
        Set<String> variables = new HashSet<String>();
        Map<String,double[]> availabilityVariables = new HashMap<String,double[]>();
        for (Choice c : allUtilitiesMap.keySet()) {
            variables.addAll(allUtilitiesMap.get(c).getVariableSet());
            //copy over availabilities
            String av = getAvailabilityVariable(c);
            if (data.hasVariable(av))
                availabilityVariables.put(av, data.getVariableData(av));
        }
        //copy over availability data
        data = availabilityVariables.size() > 0 ? new CompositeDataProvider(factory,getDataProvider(variables,data.getDataLength()),new SimpleDataProvider(availabilityVariables,factory)) :
                                                  getDataProvider(variables,data.getDataLength());

    }

    private DataProvider getBadProvider(Choice c) {
        Set<String> variables = new HashSet<String>();
        Map<String,double[]> availabilityVariables = new HashMap<String,double[]>();
        for (Choice cc : allUtilitiesMap.keySet()) {
            variables.addAll(allUtilitiesMap.get(cc).getVariableSet());
            //copy over availabilities
            String av = getAvailabilityVariable(cc);
            if (data.hasVariable(av))
                availabilityVariables.put(av, data.getVariableData(av));
        }
        variables.remove(allUtilitiesMap.get(c).getVariableSet().iterator().next());
        return new CompositeDataProvider(factory,getDataProvider(variables,data.getDataLength()),new SimpleDataProvider(availabilityVariables,factory));
    }

    private DoubleVector getExponentiatedUtility(Choice c) {
        DoubleVector expUt = allUtilitiesMap.get(c).getUtilities(data);
        for (int i : range(data.getDataLength()))
            expUt.setCell(Math.exp(expUt.getCell(i)),i);
        if (availabilities.containsKey(c)) { //skip availabilities from upper nests
            BooleanVector filter = availabilities.get(c);
            for (int i : range(data.getDataLength()))
                if (!filter.getCell(i))
                    expUt.setCell(0.0,i);
        }
        return expUt;
    }

    private DoubleVector getProbabilitiesInNest(Choice c) {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        DoubleVector expSum = null;
        for (Choice choice : nestChoices.get(c)) {
            if (expSum == null)
                expSum = getExponentiatedUtility(choice);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(choice), NumericFunctions.ADD);
        }
        return (DoubleVector) cwtc.calculate(getExponentiatedUtility(c),expSum,NumericFunctions.DIVIDE);
    }

    @Test
    public void testGetFullChoices() {
        assertEquals(allUtilitiesMap.keySet(),nestedLogitModel.getFullChoices());
    }

    @Test
    public void testGetFinalChoices() {
        assertEquals(finalChoices,nestedLogitModel.getFinalChoices());
    }

    @Test
    public void testGetProbabilitiesNestedChoice() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        Choice c = getRandomChoice(nestedChoices);
        DoubleVector probs = getProbabilitiesInNest(c);
        Choice upChoice = upChoices.get(c);
        while (upChoice != null) {
            probs = (DoubleVector) cwtc.calculate(probs,getProbabilitiesInNest(upChoice),NumericFunctions.MULTIPLY);
            upChoice = upChoices.get(upChoice);
        }
        assertTrue(TensorUtil.almostEquals(probs,nestedLogitModel.getProbabilities(c,data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesNestedChoiceMissingData() {
        nestedLogitModel.getProbabilities(getBadProvider(getRandomChoice(nestedChoices)));
    }

    @Test
    public void testGetFinalProbabilities() {
        Map<Choice,DoubleVector> probs = new HashMap<Choice,DoubleVector>();
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        for (Choice c : finalChoices) {
            DoubleVector p = getProbabilitiesInNest(c);
            Choice upChoice = upChoices.get(c);
            while (upChoice != null) {
                p = (DoubleVector) cwtc.calculate(p,getProbabilitiesInNest(upChoice),NumericFunctions.MULTIPLY);
                upChoice = upChoices.get(upChoice);
            }
            probs.put(c,p);
        }
        //have to check by hand
        Map<Choice,DoubleVector> mprobs = nestedLogitModel.getFinalProbabilities(data);
        assertEquals(probs.keySet(),mprobs.keySet());
        for (Choice c : probs.keySet())
            assertTrue(TensorUtil.almostEquals(probs.get(c),mprobs.get(c)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetFinalProbabilitiesMissingData() {
        nestedLogitModel.getFinalProbabilities(getBadProvider(getRandomChoice(nestedChoices)));
    }

    @Test
    public void testGetProbabilitiesNestedChoiceHub() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        Choice c = getRandomChoice(nestedChoices);
        DoubleVector probs = getProbabilitiesInNest(c);
        Choice upChoice = upChoices.get(c);
        while (upChoice != null) {
            probs = (DoubleVector) cwtc.calculate(probs,getProbabilitiesInNest(upChoice),NumericFunctions.MULTIPLY);
            upChoice = upChoices.get(upChoice);
        }
        assertTrue(TensorUtil.almostEquals(probs,nestedLogitModel.getProbabilities(c,getHub(data))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesNestedChoiceHubMissingData() {
        nestedLogitModel.getProbabilities(getRandomChoice(nestedChoices),getHub(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesNestedChoiceHubMissingChoice() {
        nestedLogitModel.getProbabilities(getRandomChoice(nestedChoices),getHub(data,getRandomChoice(nestedChoices)));
    }

    @Test
    public void testGetFinalProbabilitiesHub() {
        Map<Choice,DoubleVector> probs = new HashMap<Choice,DoubleVector>();
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(ArrayTensor.getFactory());
        for (Choice c : finalChoices) {
            DoubleVector p = getProbabilitiesInNest(c);
            Choice upChoice = upChoices.get(c);
            while (upChoice != null) {
                p = (DoubleVector) cwtc.calculate(p,getProbabilitiesInNest(upChoice),NumericFunctions.MULTIPLY);
                upChoice = upChoices.get(upChoice);
            }
            probs.put(c,p);
        }
        //have to check by hand
        Map<Choice,DoubleVector> mprobs = nestedLogitModel.getFinalProbabilities(getHub(data));
        assertEquals(probs.keySet(),mprobs.keySet());
        for (Choice c : probs.keySet())
            assertTrue(TensorUtil.almostEquals(probs.get(c),mprobs.get(c)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetFinalProbabilitiesHubMissingData() {
        nestedLogitModel.getFinalProbabilities(getHub(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetFinalProbabilitiesHubMissingChoice() {
        nestedLogitModel.getFinalProbabilities(getHub(data,getRandomChoice(nestedChoices)));
    }
}
