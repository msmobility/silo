package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.utils.SiloUtil;

public class CarOwnership {

    private final DataContainer dataContainer;

    public CarOwnership(DataContainer dataContainer){
        this.dataContainer = dataContainer;
    }


    public void run(){

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        for (Household hh : householdData.getHouseholds()){
            float[] probability = autoByIncomeCumProbabilities(HouseholdUtil.getAnnualHhIncome(hh));
            int autos = SiloUtil.select(probability, 1);
            hh.setAutos(autos);
        }

    }

    private float[] autoByIncomeCumProbabilities(int income){
        //TODO: calibrate to manchester data
        double FACTOR = 1.0;
        if (income <= 20000*FACTOR){
            return new float[]{0.822f, 0.993f, 0.999f, 1};
        } else if (income <= 40000*FACTOR){
            return new float[]{0.681f, 0.983f, 0.998f, 1};
        } else if (income <= 60000*FACTOR){
            return new float[]{0.453f, 0.945f, 0.995f, 1};
        } else if (income <= 80000*FACTOR){
            return new float[]{0.333f, 0.868f, 0.987f, 1};
        } else if (income <= 100000*FACTOR){
            return new float[]{0.238f, 0.751f, 0.969f, 1};
        } else if (income <= 150000*FACTOR){
            return new float[]{0.154f, 0.598f, 0.906f, 1};
        } else {
            return new float[]{0.068f, 0.323f, 0.690f, 1};
        }
    }

}