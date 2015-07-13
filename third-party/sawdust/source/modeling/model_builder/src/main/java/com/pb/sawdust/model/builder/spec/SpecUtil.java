package com.pb.sawdust.model.builder.spec;


import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.model.models.Choice;

import java.util.*;

/**
 * The {@code ModelBuilderSpecs} ...
 *
 * @author crf
 *         Started 12/30/11 8:23 AM
 */
public class SpecUtil {
    private SpecUtil() {} //only static convenience methods

    //coefficient specs
    private static class SimpleCoefficientSpec implements CoefficientSpec {
        private final Map<String,Double> coefficientMapping;

        private SimpleCoefficientSpec(Map<String,Double> coefficientMapping) {
            this.coefficientMapping = Collections.unmodifiableMap(new HashMap<>(coefficientMapping));
        }

        private SimpleCoefficientSpec(CoefficientSpec spec) {
            this(spec.getCoefficient());
        }

        @Override
        public Map<String,Double> getCoefficient() {
            return coefficientMapping;
        }

        @Override
        public String toString() {
            return "SimpleCoefficientSpec{" +
                    "coefficientMapping=" + coefficientMapping +
                    '}';
        }

        private SimpleCoefficientSpec mergeWith(CoefficientSpec spec) {
            Map<String,Double> otherMapping = spec.getCoefficient();
            if (!Collections.disjoint(coefficientMapping.keySet(),otherMapping.keySet())) {
                List<String> sharedCoefficientNames = new LinkedList<>();
                for (String coefficientName : coefficientMapping.keySet())
                    if (otherMapping.containsKey(coefficientName))
                        sharedCoefficientNames.add(coefficientName);
                throw new IllegalArgumentException("Cannot merge coefficient specs because they share coefficient names: " + sharedCoefficientNames);
            }
            Map<String,Double> mapping = new LinkedHashMap<>(coefficientMapping);
            mapping.putAll(otherMapping);
            return new SimpleCoefficientSpec(mapping);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleCoefficientSpec that = (SimpleCoefficientSpec) o;

            if (coefficientMapping != null ? !coefficientMapping.equals(that.coefficientMapping) : that.coefficientMapping != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return coefficientMapping != null ? coefficientMapping.hashCode() : 0;
        }
    }

    public static CoefficientSpec buildCoefficientSpec(Map<String,Double> coefficientMapping) {
        return new SimpleCoefficientSpec(coefficientMapping);
    }

    public static CoefficientSpec buildCoefficientSpec(String name, double coefficient) {
        Map<String,Double> coefficientMap = new HashMap<>();
        coefficientMap.put(name,coefficient);
        return new SimpleCoefficientSpec(coefficientMap);
    }

    private static final CoefficientSpec EMPTY_COEFFICIENT_SPEC = new SimpleCoefficientSpec(Collections.<String,Double>emptyMap());
    public static CoefficientSpec emptyCoefficientSpec() {
        return EMPTY_COEFFICIENT_SPEC;
    }

    public static CoefficientSpec mergeSpecs(List<CoefficientSpec> specs) {
        Iterator<CoefficientSpec> specIterator = specs.iterator();
        SimpleCoefficientSpec spec = new SimpleCoefficientSpec(specIterator.next());
        while (specIterator.hasNext())
            spec = spec.mergeWith(specIterator.next());
        return spec;
    }

    //formula specs
    private static class SimpleFormulaSpec implements FormulaSpec {
        private final String formula;
        private final Map<String,Object> constraints;

        private SimpleFormulaSpec(String formula, Map<String,Object> constraints) {
            this.formula = formula;
            this.constraints = Collections.unmodifiableMap(new HashMap<>(constraints));
        }

        private SimpleFormulaSpec(String formula) {
            this(formula,Collections.<String,Object>emptyMap());
        }

        @Override
        public String getFormula() {
            return formula;
        }

        @Override
        public Map<String,Object> getConstraints() {
            return constraints;
        }

        @Override
        public String toString() {
            return "SimpleFormulaSpec{" +
                    "formula='" + formula + '\'' +
                    ", constraints=" + constraints +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleFormulaSpec that = (SimpleFormulaSpec) o;

            if (constraints != null ? !constraints.equals(that.constraints) : that.constraints != null) return false;
            if (formula != null ? !formula.equals(that.formula) : that.formula != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = formula != null ? formula.hashCode() : 0;
            result = 31 * result + (constraints != null ? constraints.hashCode() : 0);
            return result;
        }
    }


    public static FormulaSpec buildFormulaSpec(String formula, Map<String,Object> constraints) {
        return new SimpleFormulaSpec(formula,constraints);
    }

    public static FormulaSpec buildFormulaSpec(String formula) {
        return new SimpleFormulaSpec(formula);
    }

    //variable spec
    private static class SimpleVariableSpec implements VariableSpec {
        private final String name;
        private final FormulaSpec formula;

        private SimpleVariableSpec(String name, FormulaSpec formula) {
            this.name = name;
            this.formula = formula;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public FormulaSpec getFormula() {
            return formula;
        }

        @Override
        public String toString() {
            return "SimpleVariableSpec{" +
                    "name='" + name + '\'' +
                    ", formula=" + formula +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleVariableSpec that = (SimpleVariableSpec) o;

            if (formula != null ? !formula.equals(that.formula) : that.formula != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (formula != null ? formula.hashCode() : 0);
            return result;
        }
    }

    public static VariableSpec buildVariableSpec(String name, FormulaSpec formula) {
        return new SimpleVariableSpec(name,formula);
    }

    public static boolean isCompatible(VariableSpec spec1, VariableSpec spec2) {
        return !(spec1.getName().equals(spec2.getName()) && !spec1.getFormula().equals(spec2.getFormula()));

    }

    //linear utility spec
    private static class SimpleLinearUtilitySpec implements LinearUtilitySpec {
        private final String name;
        private final Map<VariableSpec,CoefficientSpec> utilitySpecMap;

        private SimpleLinearUtilitySpec(String name, Map<VariableSpec,CoefficientSpec> utilitySpecMap) {
            this.name = name;
            this.utilitySpecMap = Collections.unmodifiableMap(new LinkedHashMap<>(utilitySpecMap));
        }

        private SimpleLinearUtilitySpec(LinearUtilitySpec spec) {
            this(spec.getName(),spec.getUtilitySpecMap());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<VariableSpec,CoefficientSpec> getUtilitySpecMap() {
            return utilitySpecMap;
        }

        @Override
        public String toString() {
            return "SimpleLinearUtilitySpec{" +
                    "name='" + name + '\'' +
                    ", utilitySpecMap=" + utilitySpecMap +
                    '}';
        }

        private SimpleLinearUtilitySpec mergeWith(LinearUtilitySpec utilitySpec) {
            if (!name.equals(utilitySpec.getName()))
                throw new IllegalArgumentException(String.format("Cannot merge linear utility specs with different names: \"%s\" vs. \"%s\"",name,utilitySpec.getName()));
            Map<VariableSpec,CoefficientSpec> otherUtilitySpecMap = utilitySpec.getUtilitySpecMap();
            Map<VariableSpec,CoefficientSpec> newUtilitySpec = new LinkedHashMap<>(utilitySpecMap);
            for (VariableSpec variableSpec : otherUtilitySpecMap.keySet()) {
                if (utilitySpecMap.containsKey(variableSpec)) {
                    newUtilitySpec.put(variableSpec,mergeSpecs(Arrays.asList(utilitySpecMap.get(variableSpec),otherUtilitySpecMap.get(variableSpec))));
                } else {
                    //check for incompatibilities
                    for (VariableSpec vSpec : utilitySpecMap.keySet())
                        if (!isCompatible(vSpec,variableSpec))
                            throw new IllegalArgumentException(String.format("Incompatible variable specs cannot be merged: %s vs %s",vSpec,variableSpec));
                    newUtilitySpec.put(variableSpec,otherUtilitySpecMap.get(variableSpec));
                }
            }
            return new SimpleLinearUtilitySpec(name,newUtilitySpec);
        }
    }

    public static LinearUtilitySpec buildLinearUtilitySpec(String name, Map<VariableSpec,CoefficientSpec> utilitySpecMap) {
        return new SimpleLinearUtilitySpec(name,utilitySpecMap);
    }

    public static LinearUtilitySpec buildLinearUtilitySpec(String name, VariableSpec variableSpec, CoefficientSpec coefficientSpec) {
        Map<VariableSpec,CoefficientSpec> utilitySpecMap = new LinkedHashMap<>();
        utilitySpecMap.put(variableSpec,coefficientSpec);
        return new SimpleLinearUtilitySpec(name,utilitySpecMap);
    }

    public static LinearUtilitySpec emptyLinearUtilitySpec(String name) {
        return new SimpleLinearUtilitySpec(name,Collections.<VariableSpec,CoefficientSpec>emptyMap());
    }

    public static LinearUtilitySpec mergeLinearUtilitySpecs(List<LinearUtilitySpec> specs) {
        Iterator<LinearUtilitySpec> specIterator = specs.iterator();
        SimpleLinearUtilitySpec spec = new SimpleLinearUtilitySpec(specIterator.next());
        while(specIterator.hasNext())
            spec = spec.mergeWith(specIterator.next());
        return spec;
    }

    public static ChoiceSpec buildChoiceSpec (String choiceName) {
        return new SimpleChoiceSpec(choiceName);
    }

    private static class SimpleChoiceSpec implements ChoiceSpec {
        private final String name;

        private SimpleChoiceSpec(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof ChoiceSpec))
                return false;
            ChoiceSpec choiceSpec = (ChoiceSpec) o;
            return choiceSpec.getName().equals(name);
        }

        public int hashCode() {
            return Objects.hash(name);
        }

        public String toString() {
            return "SimpleChoiceSpec(" + name + ")";
        }
    }

    public static NestSpec buildNestSpec(Set<ChoiceSpec> nestChoices, Set<NestSpec> childNests, String name, double nestingCoefficient) {
        return new SimpleNestSpec(nestChoices,childNests,name,nestingCoefficient);
    }

    public static NestSpec buildBaseNestSpec(Set<ChoiceSpec> nestChoices, String name, double nestingCoefficient) {
        return buildNestSpec(nestChoices,new HashSet<NestSpec>(),name,nestingCoefficient);
    }

    public static NestSpec buildNestSpec(Set<NestSpec> childNests, String name, double nestingCoefficient) {
        return new SimpleNestSpec(new HashSet<ChoiceSpec>(),childNests,name,nestingCoefficient);
    }

    private static class SimpleNestSpec implements NestSpec {
        private final Set<ChoiceSpec> nestChoices;
        private final double nestingCoefficient;
        private final String name;
        private final Set<NestSpec> childNests;

        private SimpleNestSpec(Set<ChoiceSpec> nestChoices, Set<NestSpec> childNests, String name, double nestingCoefficient) {
            //check for consistency
            this.nestChoices = new HashSet<>(nestChoices);
            for (NestSpec nestSpec : childNests)
                for (ChoiceSpec choice : nestSpec.getNestChoices())
                    if (!this.nestChoices.add(choice))
                        throw new IllegalArgumentException("Choice duplicated across nests: " + choice);
            this.childNests = childNests;
            this.name = name;
            this.nestingCoefficient = nestingCoefficient;
        }

        @Override
        public double getNestingCoefficient() {
            return nestingCoefficient;
        }

        @Override
        public Set<ChoiceSpec> getNestChoices() {
            return nestChoices;
        }

        @Override
        public String getNestName() {
            return name;
        }

        @Override
        public Set<NestSpec> getChildNests() {
            return childNests;
        }

        @Override
        public String toString() {
            return "SimpleNestSpec{" +
                    "name='" + name + '\'' +
                    "coefficient=" + nestingCoefficient +
                    ", choices=" + nestChoices +
                    '}';
        }
    }

    private static List<String> nestToStringList(NestSpec spec) {
        List<String> nameBox = formBox(spec.getNestName() + " (" + spec.getNestingCoefficient() + ")",false);
        List<String> choices = null;
        Set<ChoiceSpec> choiceSpecs = new HashSet<>(spec.getNestChoices());
        for (NestSpec childSpec : spec.getChildNests())
            choiceSpecs.removeAll(childSpec.getNestChoices());
        for (ChoiceSpec c : choiceSpecs) {
            List<String> cbox = formBox(c.getName(),true);
            choices = choices == null ? cbox : joinStringLists(choices,cbox);
        }
        for (NestSpec childSpec : spec.getChildNests())
            choices = choices == null ? nestToStringList(childSpec) : joinStringLists(choices,nestToStringList(childSpec));

        String first = choices.iterator().next();
        int length = first.length();
        int nboxLength = nameBox.iterator().next().length();
        List<String> bufferList = null;
        String rightBuffer = null;
        String leftBuffer = null;
        if (length < nboxLength) {
            rightBuffer = repeat(' ',(nboxLength - length) / 2);
            leftBuffer = repeat(' ',nboxLength - rightBuffer.length() - length);
            bufferList = choices;
        } else if (nboxLength < length) {
            rightBuffer = repeat(' ',(length - nboxLength) / 2);
            leftBuffer = repeat(' ',length - rightBuffer.length() - nboxLength);
            bufferList = nameBox;
        }
        if (bufferList != null)
            for (int i = 0; i < bufferList.size(); i++)
                bufferList.set(i,rightBuffer + bufferList.get(i) + leftBuffer);
        String last = nameBox.get(nameBox.size() -1);
        first = choices.iterator().next();
        String connector = "";
        for (int i = 0; i < last.length(); i++)
            connector += (first.charAt(i) == '|' || last.charAt(i) == '|') ? "+" : "-";
        connector = repeat(' ',connector.indexOf('+')) + connector.substring(connector.indexOf('+'),connector.lastIndexOf('+')+1) + repeat(' ',connector.length() - connector.lastIndexOf('+') - 1);
        nameBox.add(connector);
        nameBox.addAll(choices);
        return nameBox;
    }

    private static void cleanStringNest(List<String> stringNest) {
        for (int i = 0; i < stringNest.size(); i += 6) {
            if (i+5 < stringNest.size()) {
                String c = stringNest.get(i+5);
                String b = stringNest.get(i+4);
                while (c.contains("++")) {
                    //find upper guy
                    int index = c.indexOf("++");
                    if (b.charAt(index) == '|') {
                        b = b.substring(0,index) + " |" + b.substring(index + 2);
                        c = c.substring(0,index) + " +" + c.substring(index + 2);
                    } else {
                        b = b.substring(0,index) + "| " + b.substring(index + 2);
                        c = c.substring(0,index) + "+ " + c.substring(index + 2);
                    }
                    stringNest.set(i+5,c);
                    stringNest.set(i+4,b);
                }
            }

            String a = stringNest.get(i+3);
            String b = stringNest.get(i+4);
            for (int j = 0; j < a.length(); j++)
                if (b.charAt(j) == '|')
                    a = a.substring(0,j) + '+' + a.substring(j+1);
            stringNest.set(i+3,a);
            if (i > 0) {
                a = stringNest.get(i+1);
                b = stringNest.get(i);
                for (int j = 0; j < a.length(); j++)
                    if (b.charAt(j) == '|')
                        a = a.substring(0,j) + '+' + a.substring(j+1);
                stringNest.set(i+1,a);
            }
        }
        stringNest.remove(0);
        for (int i = 0; i < stringNest.size(); i++)
            stringNest.set(i,stringNest.get(i).replace(" + "," | "));
    }

    public static String nestToString(NestSpec spec) {
        List<String> nameBox = nestToStringList(spec);
        cleanStringNest(nameBox);
        String structure = "";
        for (String s : nameBox) {
            if (structure.length() == 0)
                structure = s;
            else
                structure += FileUtil.getLineSeparator() + s;
        }
        return structure;
    }

    private static List<String> joinStringLists(List<String> s1, List<String> s2) {
        if (s1.size() > s2.size())
            bufferLines(s2,s1.size() - s2.size());
        else if (s2.size() > s1.size())
            bufferLines(s1,s2.size() - s1.size());
        for (int i = 0; i < s1.size(); i++)
            s1.set(i,s1.get(i) + " " + s2.get(i));
        return s1;
    }

    private static void bufferLines(List<String> s, int count) {
        String buffer = repeat(' ',s.iterator().next().length());
        while (count-- > 0)
            s.add(buffer);
    }

    private static List<String> formBox(String name, boolean end) {
        int boxSize = name.length()+2;
        List<String> box = new LinkedList<>();
        box.add(repeat(' ',boxSize/2 + 1) + "|" + repeat(' ',boxSize - boxSize/2));
        box.add("+" + repeat('-',boxSize) + "+");
        box.add("| " + name + " |");
        box.add("+" + repeat('-',boxSize) + "+");
        box.add(repeat(' ',boxSize/2 + 1) + ((end) ? " " : "|") + repeat(' ',boxSize - boxSize/2));
        return box;
    }

    private static String repeat(char s, int count) {
        StringBuilder sb = new StringBuilder();
        while (count-- > 0)
            sb.append(s);
        return sb.toString();
    }
}
