package de.tum.bgu.msm.scenarios.ev;

import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.carOwnership.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.*;
import org.apache.log4j.*;

import java.io.*;
import java.util.*;

/**
 * Created by matthewokrah on 26/06/2018.
 */
public class SwitchToElectricVehicleModelMuc extends AbstractModel implements ModelUpdateListener {

    private final static Logger logger = Logger.getLogger(SwitchToElectricVehicleModelMuc.class);


    /**
     * this variable stores a summary for print out purposes
     */
    private Map<String, Integer> summary = new HashMap<>();

    public SwitchToElectricVehicleModelMuc(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
        summary.clear();

    }

    @Override
    public void endYear(int year) {
        switchToEV(year);

    }

    @Override
    public void endSimulation() {

    }

    private void switchToEV(int year) {

        int event_counter = 0; // number of events change to AV
        int autos_counter = 0; //number of cars (all)
        int ev_counter = 0; //numbre of avs

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        // return HashMap<Household, ArrayOfHouseholdAttributes>. These are the households eligible for switching
        // to autonomous cars. currently income is the only household attribute used but room is left for additional
        // attributes in the future
        for (Household hh : householdDataManager.getHouseholds()) {
            int numberOfElectric = (int) hh.getAttribute("EV").orElse(0);
            ev_counter += numberOfElectric;
            autos_counter += hh.getAutos();
            if (hh.getAutos() > numberOfElectric) {
                int income = HouseholdUtil.getAnnualHhIncome(hh);
                double prob = 0;
                //todo implement a better utility equation
                if (income > 30000){
                    prob = 0.5;
                } else {
                    prob = 0.0;
                }
                if (random.nextDouble() < prob){
                    hh.setAttribute("EV", numberOfElectric + 1);
                    event_counter++;
                }
            }
        }




        int hh = dataContainer.getHouseholdDataManager().getHouseholds().size(); // number of hh
        summary.put("hh", hh);
        summary.put("autos", autos_counter);
        summary.put("avs", ev_counter);
        summary.put("events", event_counter);


        logger.info(" Simulated household switched to EV " + event_counter + " (" +
                SiloUtil.rounder((100. * event_counter / hh), 0) + "% of hh)");
    }

    public Map<String, Integer> getSummaryForThisYear() {
        return summary;
    }
}
