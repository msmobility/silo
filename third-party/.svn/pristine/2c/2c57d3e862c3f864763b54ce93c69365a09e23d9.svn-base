package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.la.TransposedMatrix;
import com.pb.sawdust.model.models.provider.*;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.UniformTensor;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdDoubleD2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD2TensorShell;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.MirrorIndex;
import com.pb.sawdust.tensor.index.MixedIndex;
import com.pb.sawdust.util.collections.SetList;

import java.util.*;

/**
 * The {@code WrappedCalculationPolyDataProvider} ...
 *
 * @author crf <br/>
 *         Started 3/8/11 7:22 AM
 */
public class SimpleCalculationPolyDataProvider<K> extends AbstractIdData implements CalculationPolyDataProvider<K> {
    private static final NumericFunction1 TRANSPOSE_PLACEHOLDER = NumericFunctions.getPlaceholderFunction("transpose");

    private final Map<String,VariableCalculation> calculationMap = new LinkedHashMap<String,VariableCalculation>();
    private final Set<String> transposedVariables = new HashSet<String>();
    private final PolyDataProvider<K> baseProvider;
    private final TensorFactory factory;

    public SimpleCalculationPolyDataProvider(int dataId, PolyDataProvider<K> baseProvider, TensorFactory factory) {
        super(dataId);
        this.baseProvider = baseProvider;
        this.factory = factory;
    }

    public SimpleCalculationPolyDataProvider(PolyDataProvider<K> baseProvider, TensorFactory factory) {
        super();
        this.baseProvider = baseProvider;
        this.factory = factory;
    }

    @Override
    public Set<String> getPolyDataVariables() {
        Set<String> variables = new HashSet<String>(baseProvider.getPolyDataVariables());
        variables.addAll(calculationMap.keySet());
        return variables;
    }

    @Override
    public DataProvider getProvider(K key) {
        return baseProvider.getProvider(key); // calculations are all consider to be poly data
    }

    @Override
    public int getAbsoluteStartIndex() {
        return baseProvider.getAbsoluteStartIndex();
    }

    @Override
    public int getDataLength() {
        return baseProvider.getDataLength();
    }

    @Override
    public DataProvider getFullProvider(K key) {
        return getCalculationProvider(baseProvider.getFullProvider(key),calculationMap.values(),key);
    }

    protected DataProvider getCalculationProvider(DataProvider baseProvider, Collection<VariableCalculation> calculations, K key) {
        ExpandableDataProvider transposes = new ExpandableDataProvider(getDataLength(),factory);
        if (transposedVariables.size() > 0)
            baseProvider = new CompositeDataProvider(factory,baseProvider,transposes);
        SimpleCalculationDataProvider cdp = new SimpleCalculationDataProvider(baseProvider,factory);
        int index = getDataKeys().indexOf(key);
        for (VariableCalculation calculation : calculations) {
            String variableName = calculation.getName();
            if (!transposedVariables.contains(variableName)) {
                cdp.addCalculatedVariable(calculation);
            } else {
                VariableCalculation vc = calculationMap.get(calculationMap.get(variableName).getArguments().get(0));
                String transposeVariableName = transposePrefix+calculation.getArguments().get(0);
                fillTransposedCalculations(transposeVariableName,vc,transposes,cdp,index,1);
                cdp.addCalculatedVariable(changeNames(calculation,calculation.getName(),Arrays.asList(transposeVariableName)));
            }
        }
        return cdp;
    }

    private final String transposePrefix = "<|transpose|>";
    private void fillTransposedCalculations(String name, VariableCalculation calculation, ExpandableDataProvider transposedProvider, SimpleCalculationDataProvider calculationProvider, int index, int transposeCount) {
        List<String> args = calculation.getArguments();
        List<String> newArgs = new LinkedList<String>();
        for (String arg : args) {
            String newArg = transposePrefix + arg;
            newArgs.add(newArg);
            if (transposedVariables.contains(arg) && !calculationProvider.hasVariable(newArg)) {
                fillTransposedCalculations(newArg,calculationMap.get(arg),transposedProvider,calculationProvider,index,transposeCount+1);
            } else if (calculationMap.containsKey(arg) && !calculationProvider.hasVariable(newArg)) { //non transpose calculation
                fillTransposedCalculations(newArg,calculationMap.get(arg),transposedProvider,calculationProvider,index,transposeCount);
            } else if (!transposedProvider.hasVariable(newArg)) { //skip if another calculation has already filled this in
                double[] data = new double[getDataLength()];
                DoubleMatrix m = getPolyData(arg);
                boolean transpose = transposeCount % 2 == 1; //this handles transposes of transposes
                for (int i = 0; i < data.length; i++)
                    if (transpose)
                        data[i] = m.getCell(index,i); //this is the transposition
                    else
                        data[i] = m.getCell(i,index); //this is regular
                transposedProvider.addVariable(newArg,data);
            }
        }
        calculationProvider.addCalculatedVariable(changeNames(calculation,name,newArgs));
    }

    private VariableCalculation changeNames(final VariableCalculation vc, String newName, final List<String> newArguments) {//, final DataProvider transposedProvider) {
        return new VariableCalculation(newName, vc.getFunction(), newArguments) {
            private final Map<String,String> variableMap = new HashMap<String,String>();
            {
                Iterator<String> newArgsIt = getArguments().iterator();
                for (String arg : vc.getArguments())
                    variableMap.put(arg,newArgsIt.next());
            }
            public CalculationTrace getVariableTrace(final DataProvider provider, int observation) {
                DataProvider newProvider = new WrappedDataProvider(provider, provider.getDataId()) {
                    public double[] getVariableData(String variable) {
                        return provider.getVariableData(getVariableName(variable));
                    }

                    public double[] getVariableData(String variable, int start, int end) {
                        return provider.getVariableData(getVariableName(variable),start,end);
                    }

                    public CalculationTrace getVariableTrace(String variable, int observation) {
                        return provider.getVariableTrace(getVariableName(variable),observation);
                    }

                    private String getVariableName(String variable) {
                        return variableMap.containsKey(variable) ? variableMap.get(variable) : variable;
                    }

                    public DataProvider getSubData(int start, int end) {
                        return new LazySubDataProvider(this,factory,start,end);
                    }
                };
                return vc.getVariableTrace(newProvider,observation);
            }

            public List<String> getArguments() {
                return newArguments;
            }
        };
    }

    @Override
    public SetList<K> getDataKeys() {
        return baseProvider.getDataKeys();
    }

    @Override
    public VariableCalculation getCalculation(String variable) {
        if (!calculationMap.containsKey(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        return calculationMap.get(variable);
    }

    @Override
    public VariableCalculation getResolvedCalculation(String variable) {
        return SimpleCalculationDataProvider.getResolvedCalculation(this, variable);
    }

    @Override
    public boolean containsCalculatedVariables() {
        return calculationMap.size() > 0;
    }

    @Override
    public DataProvider getSharedProvider() {
        return baseProvider.getSharedProvider();
    }

    public IdDoubleMatrix<? super K> getPolyData(String variable) {
        if (calculationMap.containsKey(variable)) {
            return getCalculatedPolyData(calculationMap.get(variable));
        } else {
            return baseProvider.getPolyData(variable);
        }
    }

    private boolean containsPolyVariable(String variable) {
        return getPolyDataVariables().contains(variable) || calculationMap.containsKey(variable);
    }

    private boolean containsFullyRepresentedVariable(String variable) {
        DataProvider generalProvider = baseProvider.getSharedProvider();
        for (K key : getDataKeys())
            if (!generalProvider.hasVariable(variable) && !baseProvider.getProvider(key).hasVariable(variable)) //check against general provider first, since that is quicker (I think?!)
                return false;
        return true;
    }

    protected IdDoubleMatrix<? super K> getCalculatedPolyData(VariableCalculation calculation) {
        return getCalculatedPolyData(calculation,this,-1,-1);
    }

    private boolean isVectorSizedCalculation(VariableCalculation calculation, DataProvider generalProvider) {
        for (String variable : calculation.getArguments()) {
            if (!(calculation.getFunction() != TRANSPOSE_PLACEHOLDER && //transpose is a matrix size function
                  (generalProvider.hasVariable(variable) ||
                   (calculationMap.containsKey(variable) && isVectorSizedCalculation(calculationMap.get(variable),generalProvider)))))
                return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked") //indices should be Ks throughout, so should returns should be ok
    private IdDoubleMatrix<? super K> getCalculatedPolyData(VariableCalculation calculation, CalculationPolyDataProvider<K> provider, int start, int end) {
        DoubleMatrix result;
        if (transposedVariables.contains(calculation.getName())) { //short circuit if tranpsose...
            String transposedVariable = calculation.getArguments().get(0);
            if (calculationMap.containsKey(transposedVariable)) //calculated
                result = TransposedMatrix.transpose(getCalculatedPolyData(calculationMap.get(calculation.getArguments().get(0)), provider, start, end));
            else if (baseProvider.getPolyDataVariables().contains(transposedVariable))
                result = TransposedMatrix.transpose(baseProvider.getSubDataHub(start,end).getPolyData(transposedVariable));
            else
            //todo: build result if variable is in base provider but not poly - allowed in add transpose (maybe shouldn't be) but not handled here
                result = null;
            return (IdDoubleMatrix<K>) result.getReferenceTensor(MixedIndex.replaceIds(result.getIndex(),1,getDataKeys()));
        }
        DataProvider generalProvider = provider.getSharedProvider(); //to save calculation time since the variables in here are shared across all calculations
        if (start >= 0)
            generalProvider = generalProvider.getSubData(start,end);
        NumericFunctionN function = calculation.getFunction();
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        List<DoubleTensor> args = new LinkedList<DoubleTensor>();

        Map<Integer, Integer> mirrorDims = new HashMap<Integer, Integer>();
        mirrorDims.put(1,getDataKeys().size());
        boolean general = isVectorSizedCalculation(calculation,generalProvider);

        for (String argument : calculation.getArguments()) {
            //todo: parallelize/fork-join this? - can't exactly because of ordering requirements (args.add(arg))
            //   better to let parallelization happen in cell-wise calcluation (already there)
            //   and outside of this as subprovider calculations
            DoubleTensor arg;
            if (containsPolyVariable(argument)) {
                arg = provider.getPolyData(argument);
            } else if (generalProvider.hasVariable(argument)) {
                arg = TensorUtil.arrayToVector(factory, generalProvider.getVariableData(argument));
                if (!general)
                    arg = arg.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(arg.getIndex(), mirrorDims));
            } else {
                //don't do TensorUtil.fill right now, because providers aren't necessarily concurrent-safe
                SetList<K> keys = provider.getDataKeys();
                arg = factory.doubleMatrix(provider.getDataLength(),keys.size());
                int j = 0;
                for (K key : keys) {
                    double[] data = provider.getProvider(key).getVariableData(argument);
                    for (int i = 0; i < data.length; i++)
                        arg.setCell(data[i],i,j);
                    j++;
                }
            }
            args.add((general && arg.size() == 2) ? ((VectorWrappedIdDoubleMatrix) arg).wrappedVector : arg);
        }

        if (general) { //only general (vector) arguments, so do vector calculation and then mirror it out (more efficient)
//            System.out.println("general vector: " + calculation.getName());
            DoubleVector t = (args.size() == 0) ? //if no arguments, then some sort of constant, in theory...
                    (DoubleVector) UniformTensor.getFactory().initializedDoubleTensor(function.applyDouble(),start >= 0 ? end-start : getDataLength()) :
                    (DoubleVector) cwtc.calculate(function,args);
            result = (DoubleMatrix) t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),mirrorDims));
            result = new VectorWrappedDoubleMatrix(result,t); //placeholder
        } else {
//            System.out.println("poly: " + calculation.getName());
            result = (DoubleMatrix) cwtc.calculate(function,args);
        }
        if (general) {
            @SuppressWarnings("unchecked") //this is correct
            VectorWrappedDoubleMatrix r = (VectorWrappedDoubleMatrix) result;
            result = r.wrappedMatrix;
            return new VectorWrappedIdDoubleMatrix((IdDoubleMatrix<K>) result.getReferenceTensor(MixedIndex.replaceIds(result.getIndex(),1,getDataKeys())),r.wrappedVector);
        } else {
            return (IdDoubleMatrix<K>) result.getReferenceTensor(MixedIndex.replaceIds(result.getIndex(),1,getDataKeys()));
        }
    }

    private class VectorWrappedDoubleMatrix extends DoubleD2TensorShell {
        private final DoubleVector wrappedVector;
        private final DoubleMatrix wrappedMatrix;
        public VectorWrappedDoubleMatrix(DoubleMatrix wrappedMatrix, DoubleVector wrappedVector) {
            super(wrappedMatrix);
            this.wrappedVector = wrappedVector;
            this.wrappedMatrix = wrappedMatrix;
        }
    }

    private class VectorWrappedIdDoubleMatrix extends IdDoubleD2TensorShell<K> {
        private final DoubleVector wrappedVector;
        public VectorWrappedIdDoubleMatrix(DoubleMatrix matrix, DoubleVector wrappedVector) {
            super(matrix);
            this.wrappedVector = wrappedVector;
        }
    }

    public void addCalculatedPolyVariable(String variable, NumericFunctionN function, List<String> args) {
        addCalculatedPolyVariable(new VariableCalculation(variable,function,args));
    }

    public void addCalculatedPolyVariable(VariableCalculation calculation) {
        String variable = calculation.getName();
        if (containsPolyVariable(variable))
            throw new IllegalArgumentException("Poly variable already exists: " + variable);
        for (String arg : calculation.getArguments())
            if (!containsPolyVariable(arg) && !containsFullyRepresentedVariable(arg)) {

                throw new IllegalArgumentException("Variable not found (must be either poly data or exist in all non-poly data providers): " + arg);
            }
        calculationMap.put(variable,calculation);
    }

    public void addTransposedPolyVariable(String sourceVariable, String transposedVariableName) {
        addCalculatedPolyVariable(transposedVariableName,TRANSPOSE_PLACEHOLDER,Arrays.asList(sourceVariable));
        transposedVariables.add(transposedVariableName);
    }


    public CalculationPolyDataProvider<K> getSubDataHub(int start, int end) {
        return new WrappedCalculationSubPolyDataProvider(start,end);
    }

    protected class WrappedCalculationSubPolyDataProvider extends SubPolyDataProvider<K> implements CalculationPolyDataProvider<K> {

        public WrappedCalculationSubPolyDataProvider(int start, int end) {
            super(SimpleCalculationPolyDataProvider.this,start,end);
        }

        @Override
        public VariableCalculation getCalculation(String variable) {
            return SimpleCalculationPolyDataProvider.this.getCalculation(variable);
        }

        @Override
        public VariableCalculation getResolvedCalculation(String variable) {
            return SimpleCalculationPolyDataProvider.this.getResolvedCalculation(variable);
        }

        @Override
        public boolean containsCalculatedVariables() {
            return SimpleCalculationPolyDataProvider.this.containsCalculatedVariables();
        }

        @Override
        public CalculationPolyDataProvider<K> getSubDataHub(int start, int end) {
            return (CalculationPolyDataProvider<K>) super.getSubDataHub(start,end);
        }

        public IdDoubleMatrix<? super K> getPolyData(String variable) {
            if (calculationMap.containsKey(variable)) {
                return getCalculatedPolyData(calculationMap.get(variable));
            } else {
                return super.getPolyData(variable);
            }
        }

        protected IdDoubleMatrix<? super K> getCalculatedPolyData(VariableCalculation calculation) {
            return SimpleCalculationPolyDataProvider.this.getCalculatedPolyData(calculation, this, start, end);
        }
    }
}
