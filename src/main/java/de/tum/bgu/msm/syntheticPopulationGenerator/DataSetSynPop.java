package de.tum.bgu.msm.syntheticPopulationGenerator;


import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ana Moreno on 29.11.2017. Adapted from MITO
 */


public class DataSetSynPop {

    private static final Logger logger = Logger.getLogger(DataSetSynPop.class);

    private TableDataSet microDataPersons;
    private TableDataSet microDataHouseholds;
    private TableDataSet microDataDwellings;

    private TableDataSet weights;
    private TableDataSet frequencyMatrix;

    private HashMap<Integer, ArrayList> municipalitiesByCounty;
    private HashMap<Integer, int[]> tazByMunicipality;
    private ArrayList<Integer> municipalities;
    private ArrayList<Integer> counties;
    private int[] cityIDs;
    private int[] countyIDs;

    private Matrix distanceTazToTaz;




    public TableDataSet getMicroDataPersons() {
        return microDataPersons;
    }

    public void setMicroDataPersons(TableDataSet microDataPersons) {
        this.microDataPersons = microDataPersons;
    }

    public TableDataSet getMicroDataHouseholds() {
        return microDataHouseholds;
    }

    public void setMicroDataHouseholds(TableDataSet microDataHouseholds) {
        this.microDataHouseholds = microDataHouseholds;
    }

    public TableDataSet getMicroDataDwellings() {
        return microDataDwellings;
    }

    public void setMicroDataDwellings(TableDataSet microDataDwellings) {
        this.microDataDwellings = microDataDwellings;
    }

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
}
