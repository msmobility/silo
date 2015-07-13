package com.pb.sawdust.popsynth.em.classifiers;

import com.pb.sawdust.popsynth.em.BalanceDimensionClassifier;
import com.pb.sawdust.geography.Geography;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.PieceWiseDataTable;
import com.pb.sawdust.tabledata.basic.RowDataTable;
import com.pb.sawdust.tabledata.read.CsvTableReader;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code ClassificationReader} ...
 *
 * @author crf
 *         Started 10/5/11 10:32 PM
 */
public class ClassificationReader {

    public static List<BalanceDimensionClassifier> readClassifications(String tableFile, Map<String,Geography<?,?>> geographies) {
        List<BalanceDimensionClassifier> classifiers = new LinkedList<>();
        for (DataTable classificationTable : readClassificationTable(tableFile))
            classifiers.add(buildClassifier(classificationTable,geographies));
        return classifiers;
    }

    private static List<DataTable> readClassificationTable(String tableFile) {
        DataTable table = new RowDataTable(new CsvTableReader(tableFile));
        List<int[]> classificationRows = new LinkedList<>();
        List<Integer> indices = null;
        int counter = -1;
        for (DataRow row : table) {
            counter++;
            String firstColumn = row.getCellAsString(0);
            int firstColumnLength = firstColumn.length();
            if(firstColumnLength == 0)
                continue;
            else if (firstColumnLength > 1 && firstColumn.charAt(1) == ':') {
                if (indices != null)
                    classificationRows.add(ArrayUtil.toIntArray(indices));
                indices = new LinkedList<>();
            } else if (indices == null)
                throw new IllegalArgumentException("Invalid specification for first row: " + Arrays.toString(row.getData()));
            indices.add(counter);
        }
        if (indices != null && indices.size() > 0)
            classificationRows.add(ArrayUtil.toIntArray(indices));
        List<DataTable> classifications = new LinkedList<>();
        for (int[] ind : classificationRows)
            classifications.add(new PieceWiseDataTable(table,ind));
        return classifications;
    }

    public static final String NUMERIC_TYPE_NAME = "NUMERIC";
    public static final String STRING_TYPE_NAME = "STRING";

    private static BalanceDimensionClassifier<String> buildClassifier(DataTable classifierSpec, Map<String,Geography<?,?>> geographies) {
        //class,type,field
        boolean first = true;
        String classifierName = null;
        boolean household = false;
        String typeName = null;
        String fieldName = null;
        String targetGeography = null;
        Map<String,List<List<Double>>> ranges = new LinkedHashMap<>();
        Map<String,String> classificationMap = new LinkedHashMap<>();
        Map<String,String> targetFields = new HashMap<>();
        for (DataRow row : classifierSpec) {
            if (first) {
                switch (row.getCellAsString(0).substring(0,1).toUpperCase()) {
                    case "H": household = true; break;
                    case "P": household = false; break;
                    default: throw new IllegalArgumentException(String.format("Invalid class (%s) for row: %s",row.getCellAsString(0).substring(0,1),row.getCellAsString(0)));
                }
                classifierName = row.getCellAsString(0).substring(2);

                switch (row.getCellAsString(1).toUpperCase()) {
                    case NUMERIC_TYPE_NAME : typeName = NUMERIC_TYPE_NAME; break;
                    case STRING_TYPE_NAME : typeName = STRING_TYPE_NAME; break;
                    default : throw new IllegalArgumentException(String.format("Invalid type (%s) for row: %s",row.getCellAsString(1).toUpperCase(),row.getCellAsString(0)));
                }

                fieldName = row.getCellAsString(2);
                targetGeography = row.getCellAsString(3);

                first  = false;
                continue;
            }
            String name = row.getCellAsString(0);
            if (ranges.containsKey(name))
                throw new IllegalArgumentException("Duplicated classification: "+ name);
            targetFields.put(name,row.getCellAsString(2));
            switch (typeName) {
                case NUMERIC_TYPE_NAME : ranges.put(name,buildRanges(row.getCellAsString(1))); break;
                case STRING_TYPE_NAME : classificationMap.put(row.getCellAsString(1),name); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        }
        if (typeName == null)
            throw new IllegalStateException("Shouldn't be here.");
        else if (typeName.equals(NUMERIC_TYPE_NAME))
            return new PumsNumericClassifier(classifierName,ranges,fieldName,getTargetDataSpec(targetFields,geographies.get(targetGeography)),household);
        else if (typeName.equals(STRING_TYPE_NAME))
            return new PumsStringClassifier(classifierName,classificationMap,getTargetDataSpec(targetFields,geographies.get(targetGeography)),fieldName,household);
        else
            throw new IllegalStateException("Shouldn't be here.");
    }



    private static TargetDataSpec<String> getTargetDataSpec(Map<String,String> targetMapping, final Geography<?,?> targetGeography) {
        final Map<String,String> targetMappingFinal = Collections.unmodifiableMap(targetMapping);
        return new TargetDataSpec<String>() {
            @Override
            public Map<String,String> getTargetFields() {
                return targetMappingFinal;
            }

            @Override
            public Geography<?,?> getTargetGeography() {
                return targetGeography;
            }
        };
    }

    private static List<List<Double>> buildRanges(String rangeSpecification) {
        String[] ranges = rangeSpecification.split(";");
        List<List<Double>> r = new LinkedList<>();
        for (String range : ranges)
            r.add(buildRange(range));
//        for (List<Double> l : r)
//            System.out.println("" + (l.get(0) < l.get(1)) + " " + l);
        return r;
    }

    private static List<Double> buildRange(String rangeSpecification) {
        rangeSpecification = rangeSpecification.trim();
        //format: [/(
        //        start
        //        -/+
        //        end
        //        ]/)
        boolean braced = rangeSpecification.startsWith("[") || rangeSpecification.startsWith("(");
        boolean endBraced = rangeSpecification.endsWith("]") || rangeSpecification.endsWith(")");
        //default to inclusive
        boolean exclusiveStart = rangeSpecification.startsWith("(");
        boolean exclusiveEnd = rangeSpecification.endsWith(")");

        if (braced ^ endBraced) {
            throw new IllegalStateException("Invalid range specification (unbalanced braces): " + rangeSpecification);
        } else if (braced) {
            if (rangeSpecification.length() == 2)
                throw new IllegalStateException("Invalid range specification (no range): " + rangeSpecification);
            else
                rangeSpecification = rangeSpecification.substring(1,rangeSpecification.length()-1);
        }


        double a;
        double b;
        char signA = rangeSpecification.startsWith("-") ? '-' : '+';
        char signB = rangeSpecification.indexOf("--") > 0 ? '-' : '+';
        if (signA == '-')
            rangeSpecification = rangeSpecification.substring(1);
        if (signB == '-')
            rangeSpecification = rangeSpecification.replace("--","-");
        if (rangeSpecification.contains("-")) {
            String[] range = rangeSpecification.split("-");
            if (range.length != 2)
                throw new IllegalStateException("Invalid range specification: " + rangeSpecification);
            a = Double.parseDouble(signA + range[0].trim());
            b = Double.parseDouble(signB + range[1].trim());
        } else if (rangeSpecification.endsWith("+")) {
            a = Double.parseDouble(signA + rangeSpecification.substring(0,rangeSpecification.length()-1).trim());
            b = Double.MAX_VALUE;
            exclusiveEnd = true; //has to be for "+"
        } else {
            a = Double.parseDouble(signA + rangeSpecification.trim());
            b = Math.nextUp(a);
        }
        if (exclusiveStart)
            a = Math.nextUp(a);
        if (!exclusiveEnd)
            b = Math.nextUp(b);
        return Arrays.asList(a,b);
    }
}
