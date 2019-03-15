package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 26/06/2018.
 */
public class SwitchToAutonomousVehicleModelMuc extends AbstractModel implements ModelUpdateListener {

    private final static Logger logger = Logger.getLogger(SwitchToAutonomousVehicleModelMuc.class);
    private SwitchToAutonomousVehicleJSCalculatorMuc calculator;
    private final Reader reader;

    public SwitchToAutonomousVehicleModelMuc(DataContainer dataContainer, Properties properties, InputStream inputStream) {
        super(dataContainer, properties);
        this.reader = new InputStreamReader(inputStream);
    }

    @Override
    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SwitchToAutonomousVehicleCalc"));
        calculator = new SwitchToAutonomousVehicleJSCalculatorMuc(reader);
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void endYear(int year) {
        switchToAV(year);
    }

    @Override
    public void endSimulation() {

    }

    private void switchToAV(int year) {

        int counter = 0;
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        // return HashMap<Household, ArrayOfHouseholdAttributes>. These are the households eligible for switching
        // to autonomous cars. currently income is the only household attribute used but room is left for additional
        // attributes in the future
        for (Household hh : householdDataManager.getHouseholds()) {
            if (hh.getAutos() > ((HouseholdMuc)hh).getAutonomous()) {
                int income = HouseholdUtil.getHhIncome(hh) / 12;
                double[] prob = calculator.calculate(income, year);
                int action = SiloUtil.select(prob);
                if (action == 1) {
                    ((HouseholdMuc)hh).setAutonomous(((HouseholdMuc)hh).getAutonomous() + 1);
                    counter++;
                }
            }
        }
        double hh = dataContainer.getHouseholdDataManager().getHouseholds().size();
        logger.info(" Simulated household switched to AV" + counter + " (" +
                SiloUtil.rounder((100. * counter / hh), 0) + "% of hh)");
        SummarizeData.resultFile("SwitchedToAV," + counter);
    }
}
