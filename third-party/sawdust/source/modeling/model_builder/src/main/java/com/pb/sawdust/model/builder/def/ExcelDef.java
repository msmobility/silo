package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.model.builder.SimpleLinearUtilityBuilder;
import com.pb.sawdust.model.builder.spec.CoefficientSpec;
import com.pb.sawdust.model.builder.spec.LinearUtilitySpec;
import com.pb.sawdust.model.builder.spec.SpecUtil;
import com.pb.sawdust.model.builder.spec.VariableSpec;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The {@code ExcelDef} ...
 *
 * @author crf
 *         Started 12/29/11 11:44 AM
 */
public class ExcelDef {
    public static final String KEYWORD_IDENTIFIER = "#";

    public static enum UtilityColumn {
        VARIABLE("variable"),
        DESCRIPTION("description"),
        FORMULA("formula"),
        COEFFICIENT("__undefined__");
        private final String name;

        private UtilityColumn(String name) {
            this.name = name;
        }

        private static UtilityColumn getUtilityColumn(String columnName) {
            for (UtilityColumn column : values())
                if (normalizeString(columnName).equals(column.name) && column != COEFFICIENT)
                    return column;
            return null;
        }

        public String getColumnName() {
            return name;
        }
    }

    public static enum Keyword {
        COMMENT("#"),
        MODEL_TYPE("type"),
        MODEL_NAME("name"),
        MODEL_DESCRIPTION("description"),
        UTILITY("utility"),
        COEFFICIENT("coeff"),
        END("end");

        private final String keyword;

        private Keyword(String keyword) {
            this.keyword = KEYWORD_IDENTIFIER + keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        private static Keyword getKeywordFromKeyword(String keyword) {
            //special for comments
            if (keyword.startsWith(COMMENT.keyword))
                return COMMENT;
            for (Keyword key : values())
                if (normalizeString(key.getKeyword()).equals(keyword))
                    return key;
            return null;
        }
    }

    private static String normalizeString(String input) {
        return input.trim().toLowerCase();
    }

    private boolean isKeyword(String value) {
        return Keyword.getKeywordFromKeyword(value) != null;
    }

    private Object readCell(Cell cell, FormulaEvaluator evaluator) {
        CellValue cellValue = evaluator.evaluate(cell);
        if (cellValue == null)
            return "";

        switch (cellValue.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN :
                return ((Boolean) cellValue.getBooleanValue()).toString();
            case Cell.CELL_TYPE_NUMERIC :
                return cellValue.getNumberValue();
            case Cell.CELL_TYPE_STRING :
                return cellValue.getStringValue();
            case Cell.CELL_TYPE_BLANK :
                return "";
            case Cell.CELL_TYPE_ERROR :
                throw new DefException("Formula evaluated to error: " + cell.getStringCellValue());
            case Cell.CELL_TYPE_FORMULA : // will never happen
                throw new IllegalStateException("Shouldn't be here...");
            default :
                return null;
        }

    }

    private void processModel(Map<String,Object> modelAdditions, Map<String,Object> modelMap) {
        for (String s : modelAdditions.keySet()) {
//            if (s.equals(Keyword.UTILITY.getKeyword())) {
//                List<Map<String,Object>> t = (List<Map<String,Object>>) modelAdditions.get(s);
//                System.out.println(s);
//                for (Map<String,Object> m : t) {
//                    for (String ss : m.keySet())
//                        System.out.println("    " + ss + " : " + m.get(ss));
//                    System.out.println();
//                }
//            } else {
                System.out.println(s + " : " + modelAdditions.get(s));
//            }
        }
//        if (currentModel.containsKey(currentKeyword.getKeyword()) && !currentModel.get(currentKeyword.getKeyword()).equals(val))
//                                            throw new DefException(String.format("Values for keyword %s do not match: \"%s\" vs \"%s\"",currentKeyword,currentModel.get(currentKeyword.getKeyword()),val));

        modelMap.put((String) modelAdditions.get(Keyword.MODEL_NAME.getKeyword()),modelAdditions.get(Keyword.UTILITY.getKeyword()));
//
    }

    private int fillUtilityEntryFromRow(Row row, FormulaEvaluator evaluator, Map<String,Object> currentModel, Map<Integer,UtilityColumn> utilityMapping, Map<Integer,String> coefficientMapping) {
        //prescan row
        int maxIndex = Integer.MAX_VALUE;
        for (Cell cell : row) {
            Object value = readCell(cell,evaluator);
            Keyword currentKeyword = null;
            if (value instanceof String)
                currentKeyword = Keyword.getKeywordFromKeyword(((String) value));
            if (currentKeyword == Keyword.COMMENT || currentKeyword == Keyword.END || currentKeyword == Keyword.MODEL_NAME) {
                maxIndex = cell.getColumnIndex();
                break;
            }
        }
        Map<String,String> variable = new HashMap<>();
        CoefficientSpec coefficientSpec = SpecUtil.emptyCoefficientSpec();
        for (int index : utilityMapping.keySet()) {
            if (index >= maxIndex)
                continue;
            UtilityColumn column = utilityMapping.get(index);
            Object value = readCell(row.getCell(index),evaluator);
            if (column == UtilityColumn.COEFFICIENT) {
                if (value.equals(""))
                    value = 0.0;
                if (!(value instanceof Double))
                    throw new DefException("Coefficient value for " + coefficientMapping.get(index) + " is not number: " + value);
                coefficientSpec = SpecUtil.mergeSpecs(Arrays.asList(coefficientSpec,SpecUtil.buildCoefficientSpec(coefficientMapping.get(index),(Double) value)));
            } else {
                variable.put(utilityMapping.get(index).getColumnName(),value.toString());
            }
        }
        //skip if variable name and formula not defined before end of definition
        if (variable.containsKey(UtilityColumn.VARIABLE.getColumnName()) && variable.containsKey(UtilityColumn.FORMULA.getColumnName())) {
            VariableSpec variableSpec = SpecUtil.buildVariableSpec(variable.get(UtilityColumn.VARIABLE.getColumnName()),SpecUtil.buildFormulaSpec(variable.get(UtilityColumn.FORMULA.getColumnName())));
            LinearUtilitySpec utilitySpec = (LinearUtilitySpec) currentModel.get(Keyword.UTILITY.getKeyword());  //get current spec and replace with merged one (next line)
            currentModel.put(Keyword.UTILITY.getKeyword(),SpecUtil.mergeLinearUtilitySpecs(Arrays.asList(utilitySpec,SpecUtil.buildLinearUtilitySpec(utilitySpec.getName(),variableSpec,coefficientSpec))));
        }
        return maxIndex;
    }


    @SuppressWarnings("fallthrough") //this is intentional here
    private void readSheet(Sheet sheet, FormulaEvaluator evaluator, Map<String,Object> modelMap) {
        boolean skip = false;
        Map<String,Object> currentModel = null;
        Map<Integer,UtilityColumn> utilityMapping = null; //column index -> utility column
        Map<Integer,String> coefficientMapping = null; //column index -> coefficient name
        //List<Map<VariableSpec,CoefficientSpec>> variableList = null;
        ROW : for (Row row : sheet) {
            boolean inUtilityRow = false;
            boolean fillUtilityMapping = coefficientMapping != null && coefficientMapping.size() == 0; //coefficients exist, but don't know them yet
            int maxIndex = 0;
            if (coefficientMapping != null && !fillUtilityMapping) {
                maxIndex = fillUtilityEntryFromRow(row,evaluator,currentModel,utilityMapping,coefficientMapping);
                if (maxIndex == Integer.MAX_VALUE)
                    continue;
            }
            for (Cell cell : row) {
                if (cell.getColumnIndex() < maxIndex)
                    continue ;
                if (skip) {
                    skip = false;
                    continue;
                }
                Object value = readCell(cell,evaluator);
                Keyword currentKeyword = value instanceof String ? Keyword.getKeywordFromKeyword(((String) value)) : null;

                if (inUtilityRow && currentKeyword == Keyword.COEFFICIENT) {
                    utilityMapping.put(cell.getColumnIndex(),UtilityColumn.COEFFICIENT);
                    continue;
                }
                if (fillUtilityMapping) {
                    int columnIndex = cell.getColumnIndex();
                    if (utilityMapping.containsKey(columnIndex)) { //will be true only if a coefficient column
                        String coefficientName = value.toString();
                        if (coefficientMapping.containsValue(coefficientName))
                            throw new DefException("Repeated coefficient name: " + coefficientName);
                        coefficientMapping.put(columnIndex,coefficientName);
                    } else {
                        UtilityColumn column = UtilityColumn.getUtilityColumn(value.toString());
                        if (column != null)
                            utilityMapping.put(columnIndex,column);
                    }
                }


                if (currentKeyword == null)
                    continue ;
                switch (currentKeyword) {
                    case COMMENT :
                        continue ROW;
                    case MODEL_NAME :
                        if (currentModel != null)
                            processModel(currentModel,modelMap);
                        currentModel = new HashMap<>();
                        utilityMapping = null;
                        coefficientMapping = null;
                    case MODEL_TYPE :
                    case MODEL_DESCRIPTION :
                        if (currentModel != null)
                            currentModel.put(currentKeyword.getKeyword(),readCell(row.getCell(cell.getColumnIndex()+1),evaluator));
                        skip = true;
                        break;
                    case END :
                        if (currentModel != null)
                            processModel(currentModel,modelMap);
                        currentModel = null;
                        utilityMapping = null;
                        coefficientMapping = null;
                        break;
                    case UTILITY :
                        utilityMapping = new HashMap<>();
                        coefficientMapping = new HashMap<>();
                        currentModel.put(Keyword.UTILITY.getKeyword(),SpecUtil.emptyLinearUtilitySpec((String) currentModel.get(Keyword.MODEL_NAME.getKeyword())));
                        inUtilityRow = true;
                        break;
                }
            }
            if (inUtilityRow && utilityMapping.size() == 0) { //no coefficients specified, so this whole thing can be skipped
                utilityMapping = null;
                coefficientMapping = null;
            }
            if (fillUtilityMapping) {
                //check to make sure all necessary variables are completed
                for (UtilityColumn column : UtilityColumn.values())
                    if (!utilityMapping.containsValue(column))
                        throw new DefException("Utility missing column: " + column);
                for (int index : utilityMapping.keySet())
                    if (utilityMapping.get(index) == UtilityColumn.COEFFICIENT && !coefficientMapping.containsKey(index))
                        throw new DefException("Missing coefficient name at column index " + index); //shouldn't happen, but just in case...
            }
        }
        if (currentModel != null)
            processModel(currentModel,modelMap);
    }

    public static void main(String ... args) throws IOException,InvalidFormatException {
        InputStream inp = new FileInputStream("d:/dump/temp_test.xlsx");
        //InputStream inp = new FileInputStream("workbook.xlsx");

        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        ExcelDef def = new ExcelDef();
        Map<String,Object> modelMap = new HashMap<>();
        def.readSheet(sheet,evaluator,modelMap);
        SimpleLinearUtilityBuilder builder = new SimpleLinearUtilityBuilder();
        System.out.println(modelMap);
        System.out.println(builder.buildUtility((LinearUtilitySpec) modelMap.get("this is a name"),"value"));

    }
}
