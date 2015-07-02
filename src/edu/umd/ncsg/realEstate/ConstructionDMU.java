package edu.umd.ncsg.realEstate;

import edu.umd.ncsg.data.DwellingType;
import org.apache.log4j.Logger;

import java.util.HashMap;

import com.pb.common.calculator.IndexValues;

/**
 * Simulates construction of new dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/

public class ConstructionDMU {

    protected transient Logger logger = Logger.getLogger(ConstructionDMU.class);
    protected HashMap<String, Integer> methodIndexMap;

    // uec variables
    private IndexValues dmuIndex;
    private int token;
    private int dwellingType;
    private float avePrice;
    private float zonalAccessibility;


    public ConstructionDMU() {
		dmuIndex = new IndexValues();
	}


    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }


    public void setToken (int token) {
        this.token = token;        
    }

    public void setType (DwellingType type) {
    	this.dwellingType = type.ordinal();
    }

    public void setAvePrice(float price) {
        this.avePrice = price;
    }

    public void setZonalAccessibility(float accessibility) {
        this.zonalAccessibility = accessibility;
    }

    // DMU methods - define one of these for every @var in the control file.
	public int getToken() {
		return token;
	}

    public int getDwellingType() {
        return dwellingType;
    }

    public float getAvePrice() {
        return avePrice;
    }

    public float getZonalAccessibility() {
        return zonalAccessibility;
    }

}