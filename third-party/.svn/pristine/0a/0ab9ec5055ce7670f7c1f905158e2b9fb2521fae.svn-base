package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.ParameterizedNumericFunction;
import com.pb.sawdust.model.builder.parser.FunctionBuilder;
import com.pb.sawdust.model.builder.parser.ParsedFunctionBuilder;
import com.pb.sawdust.model.builder.spec.*;
import com.pb.sawdust.model.models.utility.LinearUtility;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import java.util.*;

/**
 * The {@code UtilityTable} ...
 *
 * @author crf
 *         Started 4/27/12 7:06 AM
 */
public class UtilityTable {
    public static final String VARIABLE_COLUMN_NAME = "variable";
    public static final String DESCRIPTION_COLUMN_NAME = "description";
    public static final String FILTER_COLUMN_NAME = "filter";
    public static final String FORMULA_COLUMN_NAME = "formula";
    public static final String FORMULA_SPEC_FILTER_CONSTRAINT_KEY = "filter";

    private final LinearUtilitySpec utilitySpecs;
    private final Set<String> utilityNames = new HashSet<>();
    private final Set<String> variableNames = new HashSet<>();
    private volatile boolean dropZeroCoefficientVariables = true;

    public UtilityTable(DataTable utilityTable, String name) {
        checkForColumn(utilityTable,VARIABLE_COLUMN_NAME);
        checkForColumn(utilityTable,FILTER_COLUMN_NAME);
        checkForColumn(utilityTable,FORMULA_COLUMN_NAME);
        boolean dataCoercion = utilityTable.willCoerceData();
        utilityTable.setDataCoersion(true);
        utilitySpecs = parseUtilityTable(utilityTable,name);
        utilityTable.setDataCoersion(dataCoercion);
    }

    private void checkForColumn(DataTable utilityTable, String column) {
        if (!utilityTable.hasColumn(column))
            throw new IllegalStateException("Utility table missing \"" + column + "\" column: " + Arrays.toString(utilityTable.getColumnLabels()));
    }

    private String buildUniqueVariable(String variable, Set<String> usedNames) {
        //unique string is just incremented with numbers
        variable = usedNames.contains(variable) ? variable + "_row" + usedNames.size() : variable;
        usedNames.add(variable);
        return variable;
    }

    private LinearUtilitySpec parseUtilityTable(DataTable utilityTable, String name) {
        Map<VariableSpec,CoefficientSpec> utilityMap = new LinkedHashMap<>();
        Set<String> variableNames = new HashSet<>();
        for (DataRow row : utilityTable) {
            String variable = row.getCellAsString(VARIABLE_COLUMN_NAME).trim();
            if (variable.length() == 0)
                continue;
            variable = buildUniqueVariable(variable,variableNames);
            String formula = row.getCellAsString(FORMULA_COLUMN_NAME).trim();
            String filter = row.getCellAsString(FILTER_COLUMN_NAME).trim();

            Set<String> skipColumns = new HashSet<>();
            skipColumns.add(VARIABLE_COLUMN_NAME);
            skipColumns.add(FILTER_COLUMN_NAME);
            skipColumns.add(FORMULA_COLUMN_NAME);
            skipColumns.add(DESCRIPTION_COLUMN_NAME);

            for (String column : utilityTable.getColumnLabels())
                if (!skipColumns.contains(column))
                    utilityNames.add(column);

            Map<String,Object> constraints = new HashMap<>();
            if (filter.length() > 0)
                constraints.put(FORMULA_SPEC_FILTER_CONSTRAINT_KEY,filter);
            if (constraints.size() > 0 && formula.length() == 0)
                formula = "1.0";

            VariableSpec variableSpec = SpecUtil.buildVariableSpec(variable,(formula.length() > 0) ? SpecUtil.buildFormulaSpec(formula,constraints) : null);

            Map<String,Double> coefficients = new HashMap<>();
            for (String utilityName : utilityNames)
                coefficients.put(utilityName,readCoefficient(row,utilityName));

            utilityMap.put(variableSpec,SpecUtil.buildCoefficientSpec(coefficients));
        }
        this.variableNames.addAll(variableNames);
        return buildLinearUtilitySpec(name,utilityMap);
    }


    protected LinearUtilitySpec buildLinearUtilitySpec(String name, Map<VariableSpec,CoefficientSpec> utilityMap) {
        return SpecUtil.buildLinearUtilitySpec(name,utilityMap);
    }

    private double readCoefficient(DataRow row, String column) {
        try {
            return row.getCellAsDouble(column);
        } catch (NumberFormatException e) {
            if (row.getCellAsString(column).trim().length() == 0)
                return 0.0;
            throw e;
        }
    }

    public LinearUtilitySpec getLinearUtilitySpec() {
        return utilitySpecs;
    }

    public LinearUtility getLinearUtility(String name, TensorFactory factory) {
        if (!utilityNames.contains(name))
            throw new IllegalArgumentException("Utility \"" + name + "\" not found in utility table: " + utilityNames);
        SetList<String> variables = new LinkedSetList<>();
        List<Double> coefficients = new LinkedList<>();
        Map<VariableSpec,CoefficientSpec> utilities = utilitySpecs.getUtilitySpecMap();
        for (VariableSpec variableSpec : utilities.keySet()) {
            variables.add(variableSpec.getName());
            coefficients.add(utilities.get(variableSpec).getCoefficient().get(name));
        }
        return new SimpleLinearUtility(name,variables,coefficients,factory,dropZeroCoefficientVariables);
    }

    public boolean isDropZeroCoefficientVariables() {
        return dropZeroCoefficientVariables;
    }

    public void setDropZeroCoefficientVariables(boolean dropZeroCoefficientVariables) {
        this.dropZeroCoefficientVariables = dropZeroCoefficientVariables;
    }

    public Set<String> getVariableNames() {
        return Collections.unmodifiableSet(variableNames);
    }

    public Map<String,ParameterizedNumericFunction> getVariableFunctions(FunctionBuilder functionBuilder) {
        Map<String,ParameterizedNumericFunction> variableFunctions = new LinkedHashMap<>();
        for (VariableSpec variableSpec : utilitySpecs.getUtilitySpecMap().keySet()) {
            FormulaSpec formulaSpec = variableSpec.getFormula();
            if (formulaSpec == null)
                continue;
            String filterFormula = (String) formulaSpec.getConstraints().get(FORMULA_SPEC_FILTER_CONSTRAINT_KEY);
            String variableFormula = formulaSpec.getFormula();


            ParameterizedNumericFunction formulaFunction = functionBuilder.buildFunction(variableFormula);
            if (filterFormula != null) {
                ParameterizedNumericFunction filterFunction = functionBuilder.buildFunction(filterFormula);
                List<String> parameters = new LinkedList<>(filterFunction.getArgumentNames());
                parameters.addAll(formulaFunction.getArgumentNames());
                NumericFunctionN f = NumericFunctions.shortCircuitTernary(filterFunction.getFunction(),formulaFunction.getFunction(),NumericFunctions.constant(0.0));
                formulaFunction = new ParsedFunctionBuilder.BasicParameterizedNumericFunction(f,parameters);
            }
            variableFunctions.put(variableSpec.getName(),formulaFunction);
        }
        return variableFunctions;
    }

    public static Set<String> getReferencedLinkedSources(Set<ParameterizedNumericFunction> functions, Collection<String> utilityVariables, Set<String> availableSources) {
        Set<String> variables = new HashSet<>(utilityVariables);
        for (ParameterizedNumericFunction function : functions)
            for (String variable : function.getArgumentNames())
                variables.add(variable);
        Set<String> sources = new HashSet<>();
        for (String variable : variables) {
            if (variable.contains("@")) {
                String[] reference = variable.split("@",2);
                if (reference.length == 2 && availableSources.contains(reference[1]))
                    sources.add(reference[1]);
            } else if (availableSources.contains(variable))  {
                    sources.add(variable);
            }
        }
        return sources;
    }
}
