package de.tum.bgu.msm.models.autoOwnership.maryland;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.UpdateCarOwnershipModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * Simulates number of vehicles per household
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 18 August 2014 in College Park, MD
 **/

public class MaryLandUpdateCarOwnershipModel extends AbstractModel implements UpdateCarOwnershipModel {
    static Logger logger = Logger.getLogger(MaryLandUpdateCarOwnershipModel.class);

    private final Accessibility accessibility;
    private double[][][][][][] autoOwnerShipUtil;   // [three probabilities][hhsize][workers][income][transitAcc][density]


    public MaryLandUpdateCarOwnershipModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        logger.info("  Setting up probabilities for auto-ownership model");
        this.accessibility = accessibility;
    }

    private int getIncomeCategory(int hhIncome) {
        // Convert income in $ into income categories of household travel survey

        int[] incomeCategories = {0, 10000, 15000, 30000, 40000, 50000, 60000, 75000, 100000, 125000, 150000, 200000};
        for (int i = 0; i < incomeCategories.length; i++) {
            if (hhIncome < incomeCategories[i]) return i;
        }
        return incomeCategories.length;
    }

    @Override
    public void initialize() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipMstmCalc"));
        MarylandUpdateCarOwnershipJSCalculator calculator = new MarylandUpdateCarOwnershipJSCalculator(reader);

        boolean logCalculation = Properties.get().demographics.logAutoOwnership;

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
                            if (logCalculation) {
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
    public int[] updateCarOwnership(Map<Integer, int[]> updatedHouseholds) {
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as calculating accessibilities requires to know where households are living

        for (int id: updatedHouseholds.keySet()) {
            Household household = dataContainer.getHouseholdData().getHouseholdFromId(id);
            if(household == null) {
                continue;
            }
            double[] prob = new double[4];
            int hhSize = Math.min(household.getHhSize(), 8);
            int workers = Math.min(household.getNumberOfWorkers(), 4);
            int incomeCategory = getIncomeCategory(household.getHhIncome());
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(household.getDwellingId());
            int transitAcc = (int) (accessibility.getTransitAccessibilityForZone(dwelling.getZoneId()) + 0.5);
            int density = dataContainer.getJobData().getJobDensityCategoryOfZone(dwelling.getZoneId());
            for (int i = 1; i < 4; i++) {
                prob[i] = autoOwnerShipUtil[i - 1][hhSize - 1][workers][incomeCategory - 1][transitAcc][density - 1];
            }
            prob[0] = 1 - SiloUtil.getSum(prob);
            household.setAutos(SiloUtil.select(prob));
        }
        return new int[]{0,0};
    }
}
