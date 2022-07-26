package de.tum.bgu.msm.data.vehicle;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.HashMap;
import java.util.Map;

public class VehicleUtil {

    static Map<Integer, Double> probabilities = new HashMap<>();

    public static void initializeVehicleUtils(){
        final NormalDistribution normalDistribution = new NormalDistribution(4.29, 8.15);

        for (int age =1; age < 31; age++ ){
            probabilities.put(age, normalDistribution.density(age)/0.701);
        }
    }


    public static int getVehicleAgeInBaseYear() {
        if (probabilities.isEmpty()){
            return 0;
        } else {
            return SiloUtil.select(probabilities);
        }
    }

    public static int getVehicleAgeWhenReplaced() {
       return 1;
    }



    public static int getHighestVehicleIdInHousehold(Household hh) {
        if (hh.getVehicles().isEmpty()){
            return 0;
        }

        int max = 0;
        for (Vehicle vv : hh.getVehicles()) {
            if (vv.getId() >= max){
                max = vv.getId();
            }
        }

        return max + 1;
    }
}
