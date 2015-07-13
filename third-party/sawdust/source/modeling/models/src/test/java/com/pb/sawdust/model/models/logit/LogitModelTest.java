package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.provider.filter.VariableDataFilter;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.ChoiceUtil;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.provider.hub.SimpleDataProviderHub;
import com.pb.sawdust.tensor.ArrayTensor;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.BooleanTypeSafeArray;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * The {@code LogitModelTest} ...
 *
 * @author crf <br/>
 *         Started Sep 27, 2010 8:19:45 PM
 */
public class LogitModelTest extends TestBase {
    public static final String FILTERED_DATA_KEY = "data filtered";

    protected TensorFactory factory = ArrayTensor.getFactory();
    protected LogitModel<Choice> logitModel;
    protected DataProvider data;
    protected Map<Choice,? extends Utility> utilityMap;
    protected String name;
    protected String availabilityVariablePrefix = "avail_";
    protected Map<Choice,BooleanVector> availabilities;
    protected Choice unfilteredChoice;
    String sharedVariable;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> contexts = new LinkedList<Map<String,Object>>();
        contexts.add(buildContext(FILTERED_DATA_KEY,false));
        contexts.add(buildContext(FILTERED_DATA_KEY,true));
        addClassRunContext(this.getClass(), contexts);
        return super.getAdditionalTestClasses();
    }
    
    protected int getDataLength() {
        return random.nextInt(10,30);
    }
    
    protected Map<Choice,DataFilter> getAvailabilitityFilters(Map<Choice,String> availabilityVariables) {
        if (availabilityVariables.size() == 0)
            return null; //indicates no filtering for constructor
        Map<Choice,DataFilter> filters = new HashMap<Choice,DataFilter>();
        for (Choice c : availabilityVariables.keySet())
            filters.put(c,new VariableDataFilter(availabilityVariables.get(c),factory));
        return filters;
    }
    
    protected Set<Choice> getFilteredChoices(Set<Choice> choices) {
        Set<Choice> filteredChoices = new HashSet<Choice>();
        if ((Boolean) getTestData(FILTERED_DATA_KEY)) {
            unfilteredChoice = random.getRandomValue(choices);
            for (Choice c : choices)
                if (!c.equals(unfilteredChoice))
                    filteredChoices.add(c);
        } else {
            unfilteredChoice = null;
        }
        return filteredChoices;
    }
    
    protected Map<Choice,BooleanVector> getAvailabilities(Set<Choice> choices, Set<Choice> filteredChoices, int length) {
        Map<Choice,BooleanVector> availabilities = new HashMap<Choice,BooleanVector>();
        for (Choice choice : choices) {
            if (filteredChoices.contains(choice)) {
                BooleanVector av = factory.booleanVector(length);
                av.setTensorValues(new BooleanTypeSafeArray(random.nextBooleans(length)));
                availabilities.put(choice,av);
            } else {
                availabilities.put(choice,factory.initializedBooleanVector(true, length));
            }
        }
        return availabilities;
    }

    protected DataProvider getDataProvider(Set<String> variables, int length) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        for (String variable : variables)
            data.put(variable,random.nextDoubles(length));
        //add extra variable
        data.put(random.nextAsciiString(10),random.nextDoubles(length));
        return new SimpleDataProvider(data,factory);
    }

    protected DataProvider getDataProvider(Set<String> variables, Map<String,double[]> availabilities, int length) {
        Map<String,double[]> data = new HashMap<String,double[]>();
        for (String variable : variables)
            data.put(variable,random.nextDoubles(length));
        //add extra variable
        data.put(random.nextAsciiString(10),random.nextDoubles(length));
        //add availabilities
        for (String av : availabilities.keySet())
            data.put(av,availabilities.get(av));
        return new SimpleDataProvider(data,factory);
    }

    protected Map<Choice,? extends Utility> getUtilityMap(String sharedVariable) {
        Map<Choice,Utility> um = new HashMap<Choice,Utility>();
        Set<ChoiceUtil.IntChoice> choices = ChoiceUtil.getChoiceRange(random.nextInt(3,10));
        for (ChoiceUtil.IntChoice c : choices) {
            SetList<String> vars = new LinkedSetList<String>();
            List<Double> coefs = new LinkedList<Double>();
            for (int i : range(random.nextInt(4,10))) {
                vars.add(random.nextAsciiString(8));
                coefs.add(random.nextDouble());
            }
            vars.add(sharedVariable);
            coefs.add(random.nextDouble());
            um.put(c,new SimpleLinearUtility(new LinkedSetList<String>(vars),coefs,factory));
        }
        return um;
    }

    protected LogitModel<Choice> getLogitModel(String name, Map<Choice,? extends Utility> utilityMap, Map<Choice,DataFilter> availabilityFilters) {
        if (availabilityFilters == null)
            return new LogitModel<Choice>(name,utilityMap,factory);
        else
            return new LogitModel<Choice>(name,utilityMap,availabilityFilters,factory);
    }

    protected String getAvailabilityVariable(Choice c) {
        return availabilityVariablePrefix + c;
    }

    private Map<String,double[]> getAvailabilityData(Set<Choice> filteredChoices, int dataLength) {
        Map<String,double[]> avs = new HashMap<String,double[]>();
        for (Choice c : filteredChoices) {
            double[] av = new double[dataLength];
            BooleanVector abv = availabilities.get(c);
            for (int i : range(dataLength))
                if (abv.getCell(i))
                    av[i] = 1.0;
            avs.put(getAvailabilityVariable(c),av);
        }
        return avs;
    }

    @Before
    @SuppressWarnings("unchecked") //want generic logit map for extensability
    public void beforeTest() {
        name = random.nextAsciiString(12);
        sharedVariable = random.nextAsciiString(6);
        utilityMap = getUtilityMap(sharedVariable);
        Set<String> variables = new HashSet<String>();
        for (Choice c : utilityMap.keySet())
            variables.addAll(utilityMap.get(c).getVariables());
        int dataLength = getDataLength();
        Set<Choice> filteredChoices = getFilteredChoices(utilityMap.keySet());
        availabilities = getAvailabilities(utilityMap.keySet(),filteredChoices,dataLength);
        Map<Choice,String> availabilityVariables = new HashMap<Choice,String>();
        for (Choice c : filteredChoices)
            availabilityVariables.put(c,getAvailabilityVariable(c));
        data = getDataProvider(variables,getAvailabilityData(filteredChoices,dataLength),dataLength);
        logitModel = getLogitModel(name,utilityMap,getAvailabilitityFilters(availabilityVariables));
    }

    @Test
    public void testGetName() {
        assertEquals(name,logitModel.getName());
    }

    @Test
    public void testGetUtilities() {
        assertEquals(utilityMap,logitModel.getUtilities());
    }

    @Test
    public void testGetChoices() {
        assertEquals(utilityMap.keySet(),logitModel.getChoices());
    }

    private Choice getRandomChoice() {
        int counter = 0;
        int end = random.nextInt(utilityMap.size());
        for (Choice c : utilityMap.keySet())
            if (counter++ >= end)
                return c;
        throw new IllegalStateException("Shouldn't be here");
    }

    private DoubleVector getExponentiatedUtility(Choice c) {
        DoubleVector expUt = utilityMap.get(c).getUtilities(data);
        for (int i : range(data.getDataLength()))
            expUt.setCell(Math.exp(expUt.getCell(i)),i);
        BooleanVector filter = availabilities.get(c);
        for (int i : range(data.getDataLength()))
            if (!filter.getCell(i))
                expUt.setCell(0.0,i);
        return expUt;
    }
    
    protected DataProviderHub<Choice> getHub(DataProvider provider) {
        return getHub(utilityMap,provider,false,null);
    } 
    
    protected DataProviderHub<Choice> getHub(DataProvider provider, boolean bad) {
        return getHub(utilityMap,provider,bad,null);
    }  
    
    protected DataProviderHub<Choice> getHub(DataProvider provider, Choice choiceToDrop) {
        return getHub(utilityMap,provider,false,choiceToDrop);
    }

    protected DataProviderHub<Choice> getHub(Map<Choice,? extends Utility> utilityMap, DataProvider provider, boolean bad, Choice choiceToDrop) {
        //if bad=true, then data will be missing; if choiceToDrop <> null, then a choice will be missing
        Map<String,double[]> overallMap = new HashMap<String,double[]>();
        overallMap.put(sharedVariable,provider.getVariableData(sharedVariable));
        DataProvider overall = new SimpleDataProvider(overallMap,factory);
        Map<String,double[]> splitMap = new HashMap<String,double[]>();
        for (Choice c : utilityMap.keySet()) {
            for (String variable : utilityMap.get(c).getVariableSet())
                if (!variable.equals(sharedVariable))
                    splitMap.put(variable,data.getVariableData(variable));
            if (unfilteredChoice != c  && unfilteredChoice != null && availabilities.containsKey(c)) { //last one is for child classes that may include choices that don't need availabilities (see NestedLogitModelTest)
                String avv = getAvailabilityVariable(c);
                splitMap.put(avv,data.getVariableData(avv));
            }
        }
        DataProvider split = new SimpleDataProvider(splitMap,factory);
        Map<Choice,DataProvider> providerMap = new HashMap<Choice,DataProvider>();
        for (Choice c : utilityMap.keySet())
            if (c != choiceToDrop)
                providerMap.put(c,split);
        SimpleDataProviderHub<Choice> hub = new SimpleDataProviderHub<Choice>(providerMap.keySet(),factory);
        if (!bad)
            hub.addProvider(overall);
        hub.addKeyedProviders(providerMap);
        return hub;
    }

    private DataProvider getBadProvider(Choice c) {
        Set<String> variables = new HashSet<String>(utilityMap.get(c).getVariableSet());
        variables.remove(variables.iterator().next());
        Set<Choice> filteredChoices = utilityMap.keySet();
        int dataLength = availabilities.get(random.getRandomValue(filteredChoices)).size(0);
        return getDataProvider(variables,getAvailabilityData(filteredChoices,dataLength),dataLength);
    }

    private DataProvider getBadAvailabilityProvider(Choice c) {
        Set<String> variables = new HashSet<String>(utilityMap.get(c).getVariableSet());
        Set<Choice> filteredChoices = utilityMap.keySet();
        int dataLength = availabilities.get(random.getRandomValue(filteredChoices)).size(0);
        Map<String,double[]> availabilityData = getAvailabilityData(filteredChoices,dataLength);
        availabilityData.remove(random.getRandomValue(availabilityData.keySet()));
        return getDataProvider(variables,availabilityData,dataLength);
    }

    @Test
    public void testGetExponentiatedUtilities() {
        Choice c = getRandomChoice();
        assertTrue(TensorUtil.almostEquals(getExponentiatedUtility(c), logitModel.getExponentiatedUtilities(c, data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesMissingData() {
        Choice c = getRandomChoice();
        logitModel.getExponentiatedUtilities(c,getBadProvider(c));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesBadChoice() {
        Choice c = new ChoiceUtil.DefaultContainerChoice<Void>(null) {};
        logitModel.getExponentiatedUtilities(c,data);
    }

    @Test
    public void testGetExpsums() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        assertTrue(TensorUtil.almostEquals(expSum,logitModel.getExpsums(data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsMissingData() {
        logitModel.getExpsums(getBadProvider(getRandomChoice()));
    }

    @Test
    public void testGetLogsums() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        expSum = (DoubleVector) cwtc.calculate(expSum,NumericFunctions.LOG);
        assertTrue(TensorUtil.almostEquals(expSum,logitModel.getLogsums(data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsMissingData() {
        logitModel.getLogsums(getBadProvider(getRandomChoice()));
    }

    @Test
    public void testGetProbabilitiesOneChoice() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        Choice c = getRandomChoice();
        DoubleVector probs = (DoubleVector) cwtc.calculate(getExponentiatedUtility(c),expSum,NumericFunctions.DIVIDE);
        assertTrue(TensorUtil.almostEquals(probs,logitModel.getProbabilities(c,data)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesOneChoiceBadChoice() {
        Choice c = new ChoiceUtil.DefaultContainerChoice<Void>(null) {};
        logitModel.getProbabilities(c,data);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesOneChoiceMissingData() {
        logitModel.getProbabilities(getRandomChoice(),getBadProvider(getRandomChoice()));
    }

    @Test
    public void testGetProbabilities() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        Map<Choice,DoubleVector> probs = new HashMap<Choice,DoubleVector>();
        for (Choice c : utilityMap.keySet())
            probs.put(c,(DoubleVector) cwtc.calculate(getExponentiatedUtility(c),expSum,NumericFunctions.DIVIDE));
        //have to do equals by hand
        Map<Choice,DoubleVector> probabilities = logitModel.getProbabilities(data);
        assertEquals(probs.keySet(),probabilities.keySet());
        for (Choice c : utilityMap.keySet())
            assertTrue(TensorUtil.almostEquals(probs.get(c),probabilities.get(c)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesMissingData() {
        logitModel.getProbabilities(getBadProvider(getRandomChoice()));
    }

    @Test
    public void testGetExponentiatedUtilitiesHub() {
        Choice c = getRandomChoice();
        assertTrue(TensorUtil.almostEquals(getExponentiatedUtility(c),logitModel.getExponentiatedUtilities(c,getHub(data))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesMissingDataHub() {
        Choice c = getRandomChoice();
        logitModel.getExponentiatedUtilities(c,getHub(data,true));
    } 

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesMissingChoiceHub() {
        Choice c = getRandomChoice();
        logitModel.getExponentiatedUtilities(c,getHub(data,c));
    } 

    @Test(expected=IllegalArgumentException.class)
    public void testGetExponentiatedUtilitiesHubBadChoice() {
        Choice c = new ChoiceUtil.DefaultContainerChoice<Void>(null) {};
        logitModel.getExponentiatedUtilities(c,getHub(data));
    }

    @Test
    public void testGetExpsumsHub() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        assertTrue(TensorUtil.almostEquals(expSum,logitModel.getExpsums(getHub(data))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsMissingDataHub() {
        logitModel.getExpsums(getHub(data,true));
    }  

    @Test(expected=IllegalArgumentException.class)
    public void testGetExpsumsMissingChoiceHub() {
        logitModel.getExpsums(getHub(data,getRandomChoice()));
    }

    @Test
    public void testGetLogsumsHub() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        expSum = (DoubleVector) cwtc.calculate(expSum,NumericFunctions.LOG);
        assertTrue(TensorUtil.almostEquals(expSum,logitModel.getLogsums(getHub(data))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsMissingDataHub() {
        logitModel.getLogsums(getHub(data,true));
    }  

    @Test(expected=IllegalArgumentException.class)
    public void testGetLogsumsMissingChoiceHub() {
        logitModel.getLogsums(getHub(data,getRandomChoice()));
    } 

    @Test
    public void testGetProbabilitiesOneChoiceHub() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        Choice c = getRandomChoice();
        DoubleVector probs = (DoubleVector) cwtc.calculate(getExponentiatedUtility(c),expSum,NumericFunctions.DIVIDE);
        assertTrue(TensorUtil.almostEquals(probs,logitModel.getProbabilities(c,getHub(data))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesOneChoiceMissingDataHub() {
        Choice c = getRandomChoice();
        logitModel.getProbabilities(c,getHub(data,true));
    } 

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesOneChoiceMissingChoiceHub() {
        Choice c = getRandomChoice();
        logitModel.getProbabilities(c,getHub(data,c));
    } 

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesOneChoiceHubBadChoice() {
        Choice c = new ChoiceUtil.DefaultContainerChoice<Void>(null) {};
        logitModel.getProbabilities(c,getHub(data));
    }

    @Test
    public void testGetProbabilitiesHub() {
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        DoubleVector expSum = null;
        for (Choice c : utilityMap.keySet()) {
            if (expSum == null)
                expSum = getExponentiatedUtility(c);
            else
                expSum = (DoubleVector) cwtc.calculate(expSum,getExponentiatedUtility(c),NumericFunctions.ADD);
        }
        Map<Choice,DoubleVector> probs = new HashMap<Choice,DoubleVector>();
        for (Choice c : utilityMap.keySet())
            probs.put(c,(DoubleVector) cwtc.calculate(getExponentiatedUtility(c),expSum,NumericFunctions.DIVIDE));
        //have to do equals by hand
        Map<Choice,DoubleVector> probabilities = logitModel.getProbabilities(getHub(data));
        assertEquals(probs.keySet(),probabilities.keySet());
        for (Choice c : utilityMap.keySet())
            assertTrue(TensorUtil.almostEquals(probs.get(c),probabilities.get(c)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesMissingDataHub() {
        logitModel.getProbabilities(getHub(data,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProbabilitiesMissingChoiceHub() {
        Choice c = getRandomChoice();
        logitModel.getProbabilities(getHub(data,c));
    }



}
