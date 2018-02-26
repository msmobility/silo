package de.tum.bgu.msm.models.autoOwnership.maryland;

import com.pb.common.calculator.IndexValues;
import org.apache.log4j.Logger;

/**
 * Simulates auto ownership
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 18 August 2014 in Wheaton, MD
 **/


public class MarylandCarOwnershipDMU {

    protected transient Logger logger = Logger.getLogger(MarylandCarOwnershipDMU.class);

    // uec variables
    private IndexValues dmuIndex;
    private int token;
    private int hhSize;
    private int workers;
    private int incomeCategory;
    private int transitAccessibility;
    private int densityCategory;


    public MarylandCarOwnershipDMU() {
		dmuIndex = new IndexValues();
	}


    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }


    public void setHhSize (int hhSize) {
    	this.hhSize = hhSize;
    }

    public void setWorkers (int workers) {
        this.workers = workers;
    }

    public void setIncomeCategory (int incomeCategory) {
        this.incomeCategory = incomeCategory;
    }

    public void setTransitAccessibility (int accessibility) {
        this.transitAccessibility = accessibility;
    }

    public void setDensityCategory (int densityCategory) {
        this.densityCategory = densityCategory;
    }

    // DMU methods - define one of these for every @var in the control file.
    public int getHhSize() {
        return hhSize;
    }

    public int getWorkers() {
        return workers;
    }

    public int getIncomeCategory() {
        return incomeCategory;
    }

    public int getTransitAccessibility() {
        return transitAccessibility;
    }

    public int getDensityCategory() {
        return densityCategory;
    }

}