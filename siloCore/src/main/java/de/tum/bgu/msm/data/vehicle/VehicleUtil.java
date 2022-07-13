package de.tum.bgu.msm.data.vehicle;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.utils.SiloUtil;

public class VehicleUtil {

    public static int getVehicleAgeInBaseYear() {
        return SiloUtil.getRandomObject().nextInt(10);
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
