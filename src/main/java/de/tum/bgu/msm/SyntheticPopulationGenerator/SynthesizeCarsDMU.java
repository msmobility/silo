package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.calculator.IndexValues;
import org.apache.log4j.Logger;

/**
 * @author Matthew Okrah
 * Created on 28/04/2017 in Munich, Germany.
 */

public class SynthesizeCarsDMU {
    protected transient Logger logger = Logger.getLogger(SynthesizeCarsDMU.class);

    // uec variables
    private IndexValues dmuIndex;
    private int token;
    private int license;
    private int workers;
    private int income;
    private int logDistanceToTransit;
    private int areaType;


    public SynthesizeCarsDMU() {
        dmuIndex = new IndexValues();
    }

    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }

    public void setLicense(int license) {
        this.license = license;
    }

    public void setWorkers (int workers) {
        this.workers = workers;
    }

    public void setIncome (int income) {
        this.income = income;
    }

    public void setLogDistanceToTransit(int logDistanceToTransit) {
        this.logDistanceToTransit = logDistanceToTransit;
    }

    public void setAreaType (int areaType) {
        this.areaType = areaType;
    }


    // DMU methods - define one of these for every @var in the control file.
    public int getLicense() {
        return license;
    }

    public int getWorkers() {
        return workers;
    }

    public int getIncome() {
        return income;
    }

    public int getLogDistanceToTransit() {
        return logDistanceToTransit;
    }

    public int getAreaType() {
        return areaType;
    }
}
