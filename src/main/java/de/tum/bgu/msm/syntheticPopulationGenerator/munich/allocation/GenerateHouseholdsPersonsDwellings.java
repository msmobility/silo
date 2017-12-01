package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenerateHouseholdsPersonsDwellings {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersonsDwellings.class);

    private final DataSetSynPop dataSetSynPop;
    private int previousHouseholds;
    private int previousPersons;
    private HashMap<Integer, HashMap<Integer, Integer>> ddQuality;
    private int totalHouseholds;
    private float ddTypeProbOfSFAorSFD;
    private float ddTypeProbOfMF234orMF5plus;
    private Map<Integer, Float> probTAZ;
    private Map<Integer, Float> probMicroData;
    private int persons;
    private int households;

    public GenerateHouseholdsPersonsDwellings(DataSetSynPop dataSetSynPop){this.dataSetSynPop = dataSetSynPop;}

    public void run(){

        initializeQualityMap();
        for (int municipality : dataSetSynPop.getMunicipalities()){
            initializeMunicipalityData(municipality);
            for (int draw = 0; draw < totalHouseholds; draw++){
                int hhSelected = selectMicroHouseholdWithReplacement();
                int tazSelected = selectTAZwithoutReplacement(hhSelected);
                generateHousehold(hhSelected);

            }
        }
    }


    private void generateHousehold(int hhSelected){

        int hhSize = 1;



    }


    private int selectMicroHouseholdWithReplacement() {

        int hhSelected = SiloUtil.select(probMicroData);
        if (probMicroData.get(hhSelected) > 0){
            probMicroData.put(hhSelected, probMicroData.get(hhSelected) - 1);
        } else {
            probMicroData.remove(hhSelected);
        }
        return hhSelected;
    }


    private int selectTAZwithoutReplacement(int hhSelected){

        int taz = SiloUtil.select(probTAZ);
        return taz;
    }


    public void initializeMunicipalityData(int municipality){

        totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "hhTotal");
        ddTypeProbOfSFAorSFD = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbSFAorSFD");
        ddTypeProbOfMF234orMF5plus = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbMF234orMF5plus");
        probTAZ = dataSetSynPop.getProbabilityZone().get(municipality);
        probMicroData = new HashMap<>();
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")){
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
        persons = 0;
        households = 0;
    }


    public void initializeQualityMap(){

        previousHouseholds = 0;
        previousPersons = 0;

        ddQuality = new HashMap<>();
        for (int year : PropertiesSynPop.get().main.yearBracketsDwelling){
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalities().iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                HashMap<Integer, Integer> qualities = new HashMap<>();
                for (int quality = 1; quality <= PropertiesSynPop.get().main.numberofQualityLevels; quality++){
                    qualities.put(quality, 0);
                }
                int key = year * 1000 + municipality;
                ddQuality.put(key, qualities);
            }
        }
    }

}
