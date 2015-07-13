package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.model.builder.spec.*;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.RowDataTable;
import com.pb.sawdust.tabledata.read.CsvTableReader;

import java.util.*;

/**
 * The {@code NestedLogitTable} ...
 *
 * @author crf
 *         Started 4/30/12 7:40 AM
 */
public class NestedLogitTable extends UtilityTable {
    public static final String NEST_VARIABLE_PREFIX = "NEST_";

    private NestSpec rootNest;

    public NestedLogitTable(DataTable utilityTable, String name) {
        super(utilityTable,name);
    }

//    abstract protected C getChoiceForName(String name);
//    abstract protected Class<C> getChoiceClass();

    public NestSpec getRootNest() {
        return rootNest;
    }

    protected LinearUtilitySpec buildLinearUtilitySpec(String name, Map<VariableSpec,CoefficientSpec> utilityMap) {
        Map<VariableSpec,CoefficientSpec> baseUtilityMap = new LinkedHashMap<>();
        Map<VariableSpec,CoefficientSpec> nestMap = new LinkedHashMap<>();
        for (VariableSpec variableSpec : utilityMap.keySet())
            (variableSpec.getName().startsWith(NEST_VARIABLE_PREFIX) ? nestMap : baseUtilityMap).put(variableSpec,utilityMap.get(variableSpec));

        Map<Set<ChoiceSpec>,Double> nests = new HashMap<>();
        Map<Set<ChoiceSpec>,String> nestNames = new HashMap<>();
        for (VariableSpec variableSpec : nestMap.keySet()) {
            double nestingParameter;
            if (variableSpec.getFormula() == null) {
                nestingParameter = 1.0;
            } else if (variableSpec.getFormula().getConstraints().containsKey(FORMULA_SPEC_FILTER_CONSTRAINT_KEY)) {
                throw new IllegalStateException("Nest definition cannot have filter constraint: " + variableSpec.getFormula().getConstraints().get(FORMULA_SPEC_FILTER_CONSTRAINT_KEY));
            } else {
                try {
                    nestingParameter = Double.parseDouble(variableSpec.getFormula().getFormula());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Nest formula (nesting coefficient) must be a double: " + variableSpec.getFormula().getFormula(),e);
                }
            }
            Set<ChoiceSpec> choices = new HashSet<>();
            Map<String,Double> coefficientSpec = nestMap.get(variableSpec).getCoefficient();
            for (String choice : coefficientSpec.keySet())
                if (coefficientSpec.get(choice) != 0.0)
                    choices.add(SpecUtil.buildChoiceSpec(choice));
            if (nests.containsKey(choices))
                throw new IllegalStateException("Ambiguous (duplicated) nest: " + choices);
            nests.put(choices,nestingParameter);
            nestNames.put(choices,variableSpec.getName().replace(NEST_VARIABLE_PREFIX,""));
        }

        List<NestSpec> currentTopNests = new LinkedList<>();
        for (Set<ChoiceSpec> nest : nests.keySet()) {
            NestSpec newSpec = SpecUtil.buildBaseNestSpec(nest,nestNames.get(nest),nests.get(nest));
            Set<NestSpec> removalSpecs = new HashSet<>();
            for (NestSpec spec : currentTopNests) {
                if (nest.containsAll(spec.getNestChoices())) {
                    newSpec = insertInto(newSpec,spec);
                    removalSpecs.add(spec);
                } else if (spec.getNestChoices().containsAll(nest)) {
                    newSpec = insertInto(spec,newSpec);
                    removalSpecs.add(spec);
                } else if (!Collections.disjoint(spec.getNestChoices(),nest)) {
                    throw new IllegalStateException("Ambiguous nesting with:" + FileUtil.getLineSeparator() + "\t" + spec.getNestChoices() + FileUtil.getLineSeparator() + "\t" + nest);
                }
            }
            currentTopNests.removeAll(removalSpecs);
            currentTopNests.add(newSpec);
        }

        if (currentTopNests.size() == 1)
            rootNest = currentTopNests.get(0);
        else if (currentTopNests.size() > 1)
            rootNest = SpecUtil.buildNestSpec(new HashSet<>(currentTopNests),"root",1.0);
        return SpecUtil.buildLinearUtilitySpec(name,baseUtilityMap);
    }

    private NestSpec insertInto(NestSpec baseSpec, NestSpec newSpec) {
        Set<NestSpec> childNests = new HashSet<>(newSpec.getChildNests());
        for (NestSpec spec : baseSpec.getChildNests()) {
            if (spec.getNestChoices().containsAll(newSpec.getNestChoices())) {
                childNests = new HashSet<>(baseSpec.getChildNests());
                childNests.remove(spec);
                childNests.add(insertInto(spec,newSpec));
                return SpecUtil.buildNestSpec(separateUnspecifiedChoices(baseSpec.getNestChoices(),childNests),childNests,baseSpec.getNestName(),baseSpec.getNestingCoefficient());
            } else if (newSpec.getNestChoices().containsAll(spec.getNestChoices())) {
                childNests.add(spec);
            } else if (!Collections.disjoint(newSpec.getNestChoices(),spec.getNestChoices())) {
                throw new IllegalStateException("Ambiguous nesting with:" + FileUtil.getLineSeparator() + "\t" + spec.getNestChoices() + FileUtil.getLineSeparator() + "\t" + newSpec.getNestChoices());
            }
        }
        Set<NestSpec> newChildNests = new HashSet<>(baseSpec.getChildNests());
        newChildNests.removeAll(childNests);
        newChildNests.add(SpecUtil.buildNestSpec(separateUnspecifiedChoices(newSpec.getNestChoices(),childNests),childNests,newSpec.getNestName(),newSpec.getNestingCoefficient()));
        return SpecUtil.buildNestSpec(separateUnspecifiedChoices(baseSpec.getNestChoices(),newChildNests),newChildNests,baseSpec.getNestName(),baseSpec.getNestingCoefficient());
    }

    private Set<ChoiceSpec> separateUnspecifiedChoices(Set<ChoiceSpec> choices, Set<NestSpec> nests) {
        Set<ChoiceSpec> c = new HashSet<>(choices);
        for (NestSpec spec : nests)
            c.removeAll(spec.getNestChoices());
        return c;
    }

    public static void main(String ... args) {
        NestedLogitTable table = new NestedLogitTable(new RowDataTable(new CsvTableReader("d:/dump/test_nest.csv")),"test_nest");
        System.out.println(SpecUtil.nestToString(table.getRootNest()));
    }
}
