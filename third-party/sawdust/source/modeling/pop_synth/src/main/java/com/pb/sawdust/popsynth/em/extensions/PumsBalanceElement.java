package com.pb.sawdust.popsynth.em.extensions;

import com.pb.sawdust.data.census.pums.PumaDataGroup;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.popsynth.em.BalanceElement;
import com.pb.sawdust.tabledata.DataRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code PumsBalanceElement} ...
 *
 * @author crf
 *         Started 9/30/11 9:04 PM
 */
public class PumsBalanceElement extends BalanceElement {
    public static final String HOUSEHOLD_ROW_KEY = PumaDataType.HOUSEHOLD.name();
    public static final String PERSON_ROW_KEY_PREFIX = PumaDataType.PERSON.name();

    public PumsBalanceElement(int id, PumaDataGroup pumaData, String hhWeightColumn, String personWeightColumn) {
        this(id,buildDataRowMap(pumaData),getWeight(pumaData,hhWeightColumn,personWeightColumn));
    }

    private PumsBalanceElement(int id, Map<String,DataRow> dataRowMap, double initialWeight) {
        super(id,dataRowMap,initialWeight);
    }

    private static double getWeight(PumaDataGroup pumaData, String hhWeightColumn, String personWeightColumn) {
        double weight = pumaData.getHouseholdRow().getCellAsDouble(hhWeightColumn);
        if (weight == 0.0) {
            //assume gq and 1 person
            if (pumaData.getPersonCount() > 1)
                throw new IllegalStateException("0 HH weight and more than 1 person for Pums record");
            weight = pumaData.getPersonRows().iterator().next().getCellAsDouble(personWeightColumn);
        }
        return weight;
    }

    private static Map<String,DataRow> buildDataRowMap(PumaDataGroup pumaData) {
        Map<String,DataRow> rowMap = new HashMap<>();
        rowMap.put(HOUSEHOLD_ROW_KEY,pumaData.getHouseholdRow());
        int counter = 1;
        for (DataRow personRow : pumaData.getPersonRows())
            rowMap.put(formPersonRowKey(counter++),personRow);
        return rowMap;
    }

    public PumsBalanceElement freshCopy() {
        return new PumsBalanceElement(getId(),getElementData(),getInitialWeight());
    }

    public static String formPersonRowKey(int personNumber) {
        return PERSON_ROW_KEY_PREFIX + personNumber;
    }
}
