package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.IndexValues;
import org.apache.log4j.Logger;

/**
 * @author Matthew Okrah
 * Created on 28/08/2017 in Munich, Germany.
 */

public class UpdateCarOwnershipDMU {
    protected transient Logger logger = Logger.getLogger(UpdateCarOwnershipDMU.class);

    // uec variables
    private IndexValues dmuIndex;
    private int previousCars;
    private int hhSizePlus;
    private int hhSizeMinus;
    private int hhIncomePlus;
    private int hhIncomeMinus;
    private int licensePlus;
    private int changeResidence;

    public UpdateCarOwnershipDMU(){
        dmuIndex = new IndexValues();
    }

    public IndexValues getDmuIndexValues(){
        return dmuIndex;
    }


    public int getPreviousCars() {
        return previousCars;
    }

    public void setPreviousCars(int previousCars) {
        this.previousCars = previousCars;
    }

    public int getHhSizePlus() {
        return hhSizePlus;
    }

    public void setHhSizePlus(int hhSizePlus) {
        this.hhSizePlus = hhSizePlus;
    }

    public int getHhSizeMinus() {
        return hhSizeMinus;
    }

    public void setHhSizeMinus(int hhSizeMinus) {
        this.hhSizeMinus = hhSizeMinus;
    }

    public int getHhIncomePlus() {
        return hhIncomePlus;
    }

    public void setHhIncomePlus(int hhIncomePlus) {
        this.hhIncomePlus = hhIncomePlus;
    }

    public int getHhIncomeMinus() {
        return hhIncomeMinus;
    }

    public void setHhIncomeMinus(int hhIncomeMinus) {
        this.hhIncomeMinus = hhIncomeMinus;
    }

    public int getLicensePlus() {
        return licensePlus;
    }

    public void setLicensePlus(int licensePlus) {
        this.licensePlus = licensePlus;
    }

    public int getChangeResidence() {
        return changeResidence;
    }

    public void setChangeResidence(int changeResidence) {
        this.changeResidence = changeResidence;
    }

}
