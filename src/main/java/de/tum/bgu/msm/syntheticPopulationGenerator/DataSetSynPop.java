package de.tum.bgu.msm.syntheticPopulationGenerator;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.data.DwellingType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ana Moreno on 29.11.2017. Adapted from MITO
 */


public class DataSetSynPop {

    private static final Logger logger = Logger.getLogger(DataSetSynPop.class);

    private TableDataSet weights;
    private TableDataSet frequencyMatrix;
    private TableDataSet errorsCounty;
    private TableDataSet errorsMunicipality;
    private TableDataSet errorsSummary;
    private TableDataSet errorsBorough;
    private Map<String, int[]> valuesByHousehold;

    private HashMap<Integer, ArrayList> municipalitiesByCounty;
    private HashMap<Integer, int[]> tazByMunicipality;
    private ArrayList<Integer> municipalities;
    private ArrayList<Integer> boroughs;
    private ArrayList<Integer> counties;
    private ArrayList<Integer> tazs;
    private int[] cityIDs;
    private int[] countyIDs;
    private int[] tazIDs;
    private Map<Integer, Map<Integer, Float>> probabilityZone;
    private Map<Integer, Map<DwellingType, Integer>> dwellingPriceByTypeAndZone;
    private Table<Integer, Integer, Integer> schoolCapacity = HashBasedTable.create();
    private Table<Integer, String, Float> tripLengthDistribution;

    private Matrix distanceTazToTaz;

    private HashMap<Integer, ArrayList> boroughsByCounty;

    private Table<Integer, String, Integer> householdTable;
    private Table<Integer, String, Integer> personTable;
    private Table<Integer, String, Integer> dwellingTable;
    private TableDataSet householdDataSet;
    private TableDataSet personDataSet;
    private TableDataSet dwellingDataSet;

    public TableDataSet getWeights() {
        return weights;
    }

    public void setWeights(TableDataSet weights) {
        this.weights = weights;
    }

    public TableDataSet getFrequencyMatrix() {
        return frequencyMatrix;
    }

    public void setFrequencyMatrix(TableDataSet frequencyMatrix) {
        this.frequencyMatrix = frequencyMatrix;
    }

    public HashMap<Integer, ArrayList> getMunicipalitiesByCounty() {
        return municipalitiesByCounty;
    }

    public void setMunicipalitiesByCounty(HashMap<Integer, ArrayList> municipalitiesByCounty) {
        this.municipalitiesByCounty = municipalitiesByCounty;
    }

    public HashMap<Integer, int[]> getTazByMunicipality() {
        return tazByMunicipality;
    }

    public void setTazByMunicipality(HashMap<Integer, int[]> tazByMunicipality) {
        this.tazByMunicipality = tazByMunicipality;
    }

    public Matrix getDistanceTazToTaz() {
        return distanceTazToTaz;
    }

    public void setDistanceTazToTaz(Matrix distanceTazToTaz) {
        this.distanceTazToTaz = distanceTazToTaz;
    }

    public int[] getCityIDs() {
        return cityIDs;
    }

    public void setCityIDs(int[] cityIDs) {
        this.cityIDs = cityIDs;
    }

    public int[] getCountyIDs() {
        return countyIDs;
    }

    public void setCountyIDs(int[] countyIDs) {
        this.countyIDs = countyIDs;
    }

    public ArrayList<Integer> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(ArrayList<Integer> municipalities) {
        this.municipalities = municipalities;
    }

    public ArrayList<Integer> getCounties() {
        return counties;
    }

    public void setCounties(ArrayList<Integer> counties) {
        this.counties = counties;
    }

    public TableDataSet getErrorsCounty() {
        return errorsCounty;
    }

    public void setErrorsCounty(TableDataSet errorsCounty) {
        this.errorsCounty = errorsCounty;
    }

    public TableDataSet getErrorsMunicipality() {
        return errorsMunicipality;
    }

    public void setErrorsMunicipality(TableDataSet errorsMunicipality) {
        this.errorsMunicipality = errorsMunicipality;
    }

    public TableDataSet getErrorsSummary() {
        return errorsSummary;
    }

    public void setErrorsSummary(TableDataSet errorsSummary) {
        this.errorsSummary = errorsSummary;
    }

    public Map<String, int[]> getValuesByHousehold() {
        return valuesByHousehold;
    }

    public void setValuesByHousehold(Map<String, int[]> valuesByHousehold) {
        this.valuesByHousehold = valuesByHousehold;
    }

    public Map<Integer, Map<Integer, Float>> getProbabilityZone() {
        return probabilityZone;
    }

    public void setProbabilityZone(Map<Integer, Map<Integer, Float>> probabilityZone) {
        this.probabilityZone = probabilityZone;
    }

    public HashMap<Integer, ArrayList> getBoroughsByCounty() {
        return boroughsByCounty;
    }

    public void setBoroughsByCounty(HashMap<Integer, ArrayList> boroughsByCounty) {
        this.boroughsByCounty = boroughsByCounty;
    }

    public TableDataSet getErrorsBorough() {
        return errorsBorough;
    }

    public void setErrorsBorough(TableDataSet errorsBorough) {
        this.errorsBorough = errorsBorough;
    }

    public ArrayList<Integer> getBoroughs() {
        return boroughs;
    }

    public void setBoroughs(ArrayList<Integer> boroughs) {
        this.boroughs = boroughs;
    }

    public Map<Integer, Map<DwellingType, Integer>> getDwellingPriceByTypeAndZone() {
        return dwellingPriceByTypeAndZone;
    }

    public void setDwellingPriceByTypeAndZone(Map<Integer, Map<DwellingType, Integer>> dwellingPriceByTypeAndZone) {
        this.dwellingPriceByTypeAndZone = dwellingPriceByTypeAndZone;
    }

    public Table<Integer, String, Integer> getHouseholdTable() {
        return householdTable;
    }

    public void setHouseholdTable(Table<Integer, String, Integer> householdTable) {
        this.householdTable = householdTable;
    }

    public Table<Integer, String, Integer> getPersonTable() {
        return personTable;
    }

    public void setPersonTable(Table<Integer, String, Integer> personTable) {
        this.personTable = personTable;
    }

    public Table<Integer, String, Integer> getDwellingTable() {
        return dwellingTable;
    }

    public void setDwellingTable(Table<Integer, String, Integer> dwellingTable) {
        this.dwellingTable = dwellingTable;
    }

    public Table<Integer, Integer, Integer> getSchoolCapacity() {
        return schoolCapacity;
    }

    public void setSchoolCapacity(Table<Integer, Integer, Integer> schoolCapacity) {
        this.schoolCapacity = schoolCapacity;
    }

    public ArrayList<Integer> getTazs() {
        return tazs;
    }

    public void setTazs(ArrayList<Integer> tazs) {
        this.tazs = tazs;
    }

    public int[] getTazIDs() {
        return tazIDs;
    }

    public void setTazIDs(int[] tazIDs) {
        this.tazIDs = tazIDs;
    }

    public Table<Integer, String, Float> getTripLengthDistribution() {
        return tripLengthDistribution;
    }

    public void setTripLengthDistribution(Table<Integer, String, Float> tripLengthDistribution) {
        this.tripLengthDistribution = tripLengthDistribution;
    }

    public TableDataSet getHouseholdDataSet() {
        return householdDataSet;
    }

    public void setHouseholdDataSet(TableDataSet householdDataSet) {
        this.householdDataSet = householdDataSet;
    }

    public TableDataSet getPersonDataSet() {
        return personDataSet;
    }

    public void setPersonDataSet(TableDataSet personDataSet) {
        this.personDataSet = personDataSet;
    }

    public TableDataSet getDwellingDataSet() {
        return dwellingDataSet;
    }

    public void setDwellingDataSet(TableDataSet dwellingDataSet) {
        this.dwellingDataSet = dwellingDataSet;
    }
}
