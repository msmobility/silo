package de.tum.bgu.msm.models.autoOwnership.munich;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * Created by matthewokrah on 26/06/2018.
 */
public class SwitchToAutonomousVehicleModel {
    static Logger logger = Logger.getLogger(SwitchToAutonomousVehicleModel.class);
    private final SwitchToAutonomousVehicleJSCalculator calculator;
    private final SiloDataContainer dataContainer;

    public SwitchToAutonomousVehicleModel(SiloDataContainer dataContainer) {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SwitchToAutonomousVehicleCalc"));
        calculator = new SwitchToAutonomousVehicleJSCalculator(reader);
        this.dataContainer = dataContainer;
    }


    public int switchToAV(Map<Integer, int[]> conventionalCarsHouseholds, int year) {

        int counter = 0;
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Map.Entry<Integer, int[]> pair : conventionalCarsHouseholds.entrySet()) {
            Household hh = householdData.getHouseholdFromId(pair.getKey());
            if (hh != null) {
                int income = hh.getHhIncome()/12 ; //uses monthly income
                double[] prob = calculator.calculate(income, year);

                int action = SiloUtil.select(prob);

                if (action == 1){
                    hh.setAutonomous(hh.getAutonomous() + 1);
                    counter++;
                }
            }
        }
        return counter;
    }
}
