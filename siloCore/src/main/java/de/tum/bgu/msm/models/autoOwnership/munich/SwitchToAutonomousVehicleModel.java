package de.tum.bgu.msm.models.autoOwnership.munich;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.AnnualModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 26/06/2018.
 */
public class SwitchToAutonomousVehicleModel extends AbstractModel implements AnnualModel {

    private final static Logger logger = Logger.getLogger(SwitchToAutonomousVehicleModel.class);
    private SwitchToAutonomousVehicleJSCalculator calculator;

    public SwitchToAutonomousVehicleModel(SiloDataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SwitchToAutonomousVehicleCalc"));
        calculator = new SwitchToAutonomousVehicleJSCalculator(reader);
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void finishYear(int year) {
        switchToAV(year);
    }

    private void switchToAV(int year) {

        int counter = 0;
        HouseholdDataManager householdData = dataContainer.getHouseholdData();

        // return HashMap<Household, ArrayOfHouseholdAttributes>. These are the households eligible for switching
        // to autonomous cars. currently income is the only household attribute used but room is left for additional
        // attributes in the future
        for (Household hh : householdData.getHouseholds()) {
            if (hh.getAutos() > hh.getAutonomous()) {
                int income = HouseholdUtil.getHhIncome(hh) / 12;
                double[] prob = calculator.calculate(income, year);
                int action = SiloUtil.select(prob);
                if (action == 1) {
                    hh.setAutonomous(hh.getAutonomous() + 1);
                    counter++;
                }
            }
        }
        double hh = dataContainer.getHouseholdData().getHouseholds().size();
        logger.info(" Simulated household switched to AV" + counter + " (" +
                SiloUtil.rounder((100. * counter / hh), 0) + "% of hh)");
        SummarizeData.resultFile("SwitchedToAV," + counter);
    }
}
