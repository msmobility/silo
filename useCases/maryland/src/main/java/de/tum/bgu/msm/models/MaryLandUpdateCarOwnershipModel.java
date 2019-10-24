package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

/**
 * Simulates number of vehicles per household
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 18 August 2014 in College Park, MD
 **/
public class MaryLandUpdateCarOwnershipModel extends AbstractModel implements ModelUpdateListener {
    private static Logger logger = Logger.getLogger(MaryLandUpdateCarOwnershipModel.class);

    private final Accessibility accessibility;
    private double[][][][][][] autoOwnerShipUtil;   // [three probabilities][hhsize][workers][income][transitAcc][density]

    public MaryLandUpdateCarOwnershipModel(DataContainer dataContainer, Accessibility accessibility, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
        logger.info("  Setting up probabilities for auto-ownership model");
        this.accessibility = accessibility;
    }

    @Override
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipMstmCalc"));
        MarylandUpdateCarOwnershipJSCalculator calculator = new MarylandUpdateCarOwnershipJSCalculator(reader);

        autoOwnerShipUtil = new double[3][8][5][12][101][10];
        for (int hhSize = 0; hhSize < 8; hhSize++) {
            for (int wrk = 0; wrk < 5; wrk++) {
                for (int inc = 0; inc < 12; inc++) {
                    for (int transitAcc = 0; transitAcc < 101; transitAcc++) {
                        for (int dens = 0; dens < 10; dens++) {
                            double util[] =calculator.calculateCarOwnerShipProbabilities(hhSize +1, wrk, inc +1, transitAcc, dens +1);
                            double prob0cars = 1d / (SiloUtil.getSum(util) + 1d);
                            for (int i = 0; i < 3; i++) {
                                autoOwnerShipUtil[i][hhSize][wrk][inc][transitAcc][dens] = util[i] * prob0cars;
                            }
                            //TODO: log?
                            if (false) {
                                logger.info(hhSize + "," + wrk + "," + inc + "," + transitAcc + "," + dens + "," + prob0cars + "," +
                                        autoOwnerShipUtil[0][hhSize][wrk][inc][transitAcc][dens] + "," +
                                        autoOwnerShipUtil[1][hhSize][wrk][inc][transitAcc][dens] + "," +
                                        autoOwnerShipUtil[2][hhSize][wrk][inc][transitAcc][dens]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        updateCarOwnership();
    }

    @Override
    public void endSimulation() {

    }

    private int getIncomeCategory(int hhIncome) {
        // Convert income in $ into income categories of household travel survey

        int[] incomeCategories = {0, 10000, 15000, 30000, 40000, 50000, 60000, 75000, 100000, 125000, 150000, 200000};
        for (int i = 0; i < incomeCategories.length; i++) {
            if (hhIncome < incomeCategories[i]) return i;
        }
        return incomeCategories.length;
    }

    public void updateCarOwnership() {
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as calculating accessibilities requires to know where households are living

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household household: householdDataManager.getHouseholds()) {
            if(household == null) {
                continue;
            }
            double[] prob = new double[4];
            int hhSize = Math.min(household.getHhSize(), 8);
            int workers = Math.min(HouseholdUtil.getNumberOfWorkers(household), 4);
            int incomeCategory = getIncomeCategory(HouseholdUtil.getAnnualHhIncome(household));
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
            int transitAcc = (int) (accessibility.getTransitAccessibilityForZone(dataContainer.getGeoData().getZones().get(dwelling.getZoneId())) + 0.5);
            int density = dataContainer.getJobDataManager().getJobDensityCategoryOfZone(dwelling.getZoneId());
            for (int i = 1; i < 4; i++) {
                prob[i] = autoOwnerShipUtil[i - 1][hhSize - 1][workers][incomeCategory - 1][transitAcc][density - 1];
            }
            prob[0] = 1 - SiloUtil.getSum(prob);
            household.setAutos(SiloUtil.select(prob, random));
        }
    }
}
