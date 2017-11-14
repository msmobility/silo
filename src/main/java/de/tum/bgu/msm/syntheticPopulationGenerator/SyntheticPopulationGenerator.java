package de.tum.bgu.msm.syntheticPopulationGenerator;


import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.capeTown.SyntheticPopCT;
import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopDe;
import de.tum.bgu.msm.syntheticPopulationGenerator.munichCity.SyntheticPopMuc;
import org.apache.log4j.Logger;


import java.util.ResourceBundle;

public class SyntheticPopulationGenerator {

    static Logger logger = Logger.getLogger(SyntheticPopulationGenerator.class);
    //public enum Implementation {MUC, MSTM, CAPE_TOWN, MSP, MUC_CITY};
    private final ResourceBundle rb;
    protected static final String PROPERTIES_IMPLEMENTATION_SYNTHETIC_POPULATION     = "syn.pop.implementation";

    public SyntheticPopulationGenerator( ResourceBundle rb) {
        this.rb = rb;// set up counter for any issues during initial setup
    }

    public void run(){

        SyntheticPopI syntheticPop;
        String implementation = ResourceUtil.getProperty(rb, PROPERTIES_IMPLEMENTATION_SYNTHETIC_POPULATION);

        if (implementation.equals("MUC")){
            syntheticPop = new SyntheticPopDe(rb);
        } else if (implementation.equals("CAPE_TOWN")){
            syntheticPop = new SyntheticPopCT(rb);
        } else if (implementation.equals("MUC_CITY")){
            syntheticPop = new SyntheticPopMuc(rb);
        } else {
            syntheticPop = new SyntheticPopUs(rb);
        }

        syntheticPop.runSP();

    }




}
