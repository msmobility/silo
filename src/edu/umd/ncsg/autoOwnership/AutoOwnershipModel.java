package edu.umd.ncsg.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.Accessibility;
import edu.umd.ncsg.data.Household;
import edu.umd.ncsg.data.JobDataManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Simulates number of vehicles per household
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 18 August 2014 in College Park, MD
 **/

public class AutoOwnershipModel {
    static Logger logger = Logger.getLogger(AutoOwnershipModel.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_AutoOwnership_UEC_FILE               = "AutoOwnership.UEC.FileName";
    protected static final String PROPERTIES_AutoOwnership_UEC_DATA_SHEET         = "AutoOwnership.UEC.DataSheetNumber";
    protected static final String PROPERTIES_AutoOwnership_UEC_OWNERSHIP_UTILITY  = "AutoOwnership.UEC.Ownership.Utility";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.autoOwnership";
    private ResourceBundle rb;
    private String uecFileName;
    private int dataSheetNumber;
    int numAltsAutoOwnership;
    private double[][][][][][] autoOwnerShipUtil;   // [three probabilities][hhsize][workers][income][transitAcc][density]



    public AutoOwnershipModel(ResourceBundle rb) {
        // constructor

        logger.info("  Setting up probabilities for auto-ownership model");
        this.rb = rb;
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_AutoOwnership_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_AutoOwnership_UEC_DATA_SHEET);
        setupAutoOwnershipModel();
    }


    private void setupAutoOwnershipModel () {

        boolean logCalculation  = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION);
        int aoModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_AutoOwnership_UEC_OWNERSHIP_UTILITY);
        UtilityExpressionCalculator aoModelUtility = new UtilityExpressionCalculator(new File(uecFileName),
                aoModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                AutoOwnershipDMU.class);
        AutoOwnershipDMU autoOwnershipDMU = new AutoOwnershipDMU();

        // everything is available
        numAltsAutoOwnership = aoModelUtility.getNumberOfAlternatives();

        int[] aoAvail = new int[numAltsAutoOwnership + 1];
        for (int i = 1; i < aoAvail.length; i++) {
            aoAvail[i] = 1;
        }

        autoOwnerShipUtil = new double[3][8][5][12][101][10];
        for (int hhSize = 0; hhSize < 8; hhSize++) {
            for (int wrk = 0; wrk < 5; wrk++) {
                for (int inc = 0; inc < 12; inc++) {
                    for (int transitAcc = 0; transitAcc < 101; transitAcc++) {
                        for (int dens = 0; dens < 10; dens++) {
                            // set DMU attributes
                            autoOwnershipDMU.setHhSize(hhSize + 1);
                            autoOwnershipDMU.setWorkers(wrk);
                            autoOwnershipDMU.setIncomeCategory(inc + 1);
                            autoOwnershipDMU.setTransitAccessibility(transitAcc);
                            autoOwnershipDMU.setDensityCategory(dens + 1);
                            double util[] = aoModelUtility.solve(autoOwnershipDMU.getDmuIndexValues(),
                                    autoOwnershipDMU, aoAvail);
                            for (int i = 1; i < aoAvail.length; i++) {
                                util[i-1] = Math.exp(util[i-1]);
                            }
                            double prob0cars = 1d / (SiloUtil.getSum(util) + 1d);
                            for (int i = 1; i < aoAvail.length; i++) {
                                autoOwnerShipUtil[i-1][hhSize][wrk][inc][transitAcc][dens] = util[i-1] * prob0cars;
                            }
                            if (logCalculation) {
                                // log UEC values for each person type
                                aoModelUtility.logAnswersArray(traceLogger, "Auto-ownership model. HH size: " + hhSize +
                                ", wrk: " + wrk + ", inc: " + inc + ", transitAcc: " + transitAcc + ", density: " + dens);
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


    public int simulateAutoOwnership (Household hh) {
        // simulate number of autos for household hh
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as calculating accessibilities requires to know where households are living

        double[] prob = new double[4];
        int hhSize = Math.min(hh.getHhSize(), 8);
        int workers = Math.min(hh.getNumberOfWorkers(), 4);
        int incomeCategory = getIncomeCategory(hh.getHhIncome());
        int transitAcc = (int) (Accessibility.getTransitAccessibility(hh.getHomeZone()) + 0.5);
        int density = JobDataManager.getJobDensityCategoryOfZone(hh.getHomeZone());
        for (int i = 1; i < 4; i++) prob[i] =
                autoOwnerShipUtil[i-1][hhSize-1][workers][incomeCategory-1][transitAcc][density-1];
        prob[0] = 1 - SiloUtil.getSum(prob);
        return SiloUtil.select(prob);
    }


    private int getIncomeCategory (int hhIncome) {
        // Convert income in $ into income categories of household travel survey

        int[] incomeCategories = {0,10000,15000,30000,40000,50000,60000,75000,100000,125000,150000,200000};
        for (int i = 0; i < incomeCategories.length; i++) {
            if (hhIncome < incomeCategories[i]) return i;
        }
        return incomeCategories.length;
    }
}
