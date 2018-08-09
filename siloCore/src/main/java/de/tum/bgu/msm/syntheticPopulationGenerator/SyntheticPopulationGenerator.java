package de.tum.bgu.msm.syntheticPopulationGenerator;


import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.capeTown.SyntheticPopCT;
import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopDe;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

public class SyntheticPopulationGenerator {

    static Logger logger = Logger.getLogger(SyntheticPopulationGenerator.class);
    private final ResourceBundle rb;
    private final DataSetSynPop dataSetSynPop;

    public SyntheticPopulationGenerator( ResourceBundle rb) {
        this.rb = rb;// set up counter for any issues during initial setup
        this.dataSetSynPop = new DataSetSynPop();
    }

    public void run(){

        SyntheticPopI syntheticPop;
        Implementation imp = Properties.get().main.implementation;
        if (Properties.get().main.runSynPop) {
            switch (imp) {
                case MUNICH:
                    syntheticPop = new SyntheticPopDe(dataSetSynPop);
                    break;
                case MARYLAND:
                    syntheticPop = new SyntheticPopUs(rb);
                    break;
                case CAPE_TOWN:
                    syntheticPop = new SyntheticPopCT(rb);
                    break;
                case MSP:
                    syntheticPop = new SyntheticPopUs(rb);
                    break;
                default:
                    throw new RuntimeException("Synthetic population implementation not set");
            }
            syntheticPop.runSP();
        }
    }




}
