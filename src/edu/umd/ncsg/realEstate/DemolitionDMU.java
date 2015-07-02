package edu.umd.ncsg.realEstate;

import org.apache.log4j.Logger;

import java.util.HashMap;

import com.pb.common.calculator.IndexValues;

/**
 * Simulates renovation and deterioration of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 Januar 2010 in Rhede
 **/

public class DemolitionDMU {

    protected transient Logger logger = Logger.getLogger(DemolitionDMU.class);

    protected HashMap<String, Integer> methodIndexMap;

    // uec variables
    private int quality;
    private int residentId;
    private IndexValues dmuIndex;


    public DemolitionDMU() {
		dmuIndex = new IndexValues();
	}

    public void setQuality (int quality) {
    	this.quality = quality;
    }

    public void setResidentId(int residentId) {
    	this.residentId = residentId;
    }

    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }

    // DMU methods - define one of these for every @var in the mode choice control file.
	public int getQuality() {
        return quality;
	}

    public int getResidentId() {
        return residentId;
    }
}
