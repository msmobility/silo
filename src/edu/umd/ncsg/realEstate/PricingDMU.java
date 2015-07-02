package edu.umd.ncsg.realEstate;

import org.apache.log4j.Logger;

import java.util.HashMap;

import com.pb.common.calculator.IndexValues;
import edu.umd.ncsg.data.Person;

/**
 * Simulates renovation and deterioration of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 Januar 2010 in Rhede
 **/

public class PricingDMU {

    protected transient Logger logger = Logger.getLogger(PricingDMU.class);
    protected HashMap<String, Integer> methodIndexMap;

    // uec variables
    private IndexValues dmuIndex;
    private int token;


    public PricingDMU() {
		dmuIndex = new IndexValues();
	}


    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }


    public void setToken (int token) {
        this.token = token;        
    }

    // DMU methods - define one of these for every @var in the mode choice control file.
	public int getToken() {
		return token;
	}

}