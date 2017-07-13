package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.IndexValues;
import org.apache.log4j.Logger;

/**
 * Created by matthewokrah on 12/06/2017.
 */
public class UpdateCarOwnershipDMU {
    protected transient Logger logger = Logger.getLogger(UpdateCarOwnershipDMU.class);

    // uec variables
    private IndexValues dmuIndex;
    private int initialCars;
    private int difEV;


    public UpdateCarOwnershipDMU(){
        dmuIndex = new IndexValues();
    }

    public IndexValues getDmuIndexValues(){
        return dmuIndex;
    }

    public void setInitialCars(int initialCars) {
        this.initialCars = initialCars;
    }

    public void setDifEV(int difEV) {
        this.difEV = difEV;
    }

    // DMU methods - define one of these for every @var in the mode choice control file.
    public int getInitialCars() {
        return initialCars;
    }

    public int getDifEV() {
        return difEV;
    }

}
