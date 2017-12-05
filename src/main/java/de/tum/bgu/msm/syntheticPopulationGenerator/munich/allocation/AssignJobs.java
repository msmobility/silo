package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AssignJobs {

    private static final Logger logger = Logger.getLogger(AssignJobs.class);

    private final DataSetSynPop dataSetSynPop;
    protected Matrix distanceImpedance;
/*    idVacantJobsByZoneType = new HashMap<>();
    numberVacantJobsByType = new HashMap<>();
    idZonesVacantJobsByType = new HashMap<>();
    numberZonesByType = new HashMap<>();
    numberVacantJobsByZoneByType = new HashMap<>();*/

    public AssignJobs(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {

        calculateDistanceImpedance();


    }


    private void calculateDistanceImpedance(){

        distanceImpedance = new Matrix(dataSetSynPop.getDistanceTazToTaz().getRowCount(), dataSetSynPop.getDistanceTazToTaz().getColumnCount());
        for (int i = 1; i <= dataSetSynPop.getDistanceTazToTaz().getRowCount(); i ++){
            for (int j = 1; j <= dataSetSynPop.getDistanceTazToTaz().getColumnCount(); j++){
                distanceImpedance.setValueAt(i,j,(float) Math.exp(PropertiesSynPop.get().main.alphaJob *
                        Math.exp(dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j) * PropertiesSynPop.get().main.gammaJob)));
            }
        }
    }


    private void identifyVacantJobsByZoneType(){



    }
}
