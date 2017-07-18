package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import omx.OmxFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Implements car ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/04/2017 in Munich, Germany.
 */

public class CreateCarOwnershipModel {

    static Logger logger = Logger.getLogger(CreateCarOwnershipModel.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private ResourceBundle rb;

    protected static final String PROPERTIES_CarOwnership_UEC_FILE               = "CarOwnership.UEC.FileName";
    protected static final String PROPERTIES_CarOwnership_UEC_DATA_SHEET         = "CarOwnership.UEC.DataSheetNumber";
    protected static final String PROPERTIES_CarOwnership_UEC_CREATE_UTILITY = "CarOwnership.UEC.Create.Utility";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.carOwnership";

    private String uecFileName;
    private int dataSheetNumber;
    int numAltsCarOwnership;
    private double[][][][][][] carOwnerShipUtil;   // [three probabilities][license][workers][income][logdistToTransit][areaType]
    public geoDataI geoData;


    public CreateCarOwnershipModel(ResourceBundle rb) {
        // Constructor
        logger.info(" Setting up probabilities for car ownership model");
        this.rb = rb;
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_CarOwnership_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_CarOwnership_UEC_DATA_SHEET);
        setupCarOwnershipModel();
    }

    private void setupCarOwnershipModel() {

        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION);
        int coModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_CarOwnership_UEC_CREATE_UTILITY);
        UtilityExpressionCalculator coModelUtility = new UtilityExpressionCalculator(new File(uecFileName),
                coModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                CreateCarOwnershipDMU.class);
        CreateCarOwnershipDMU createCarOwnershipDMU = new CreateCarOwnershipDMU();

        // everything is available
        numAltsCarOwnership = coModelUtility.getNumberOfAlternatives();

        int[] coAvail = new int[numAltsCarOwnership + 1];
        for (int i = 1; i < coAvail.length; i++) {
            coAvail[i] = 1;
        }

        carOwnerShipUtil = new double[3][8][8][13505][11][4];
        for (int lic = 0; lic < 8; lic++) {
            for (int wrk = 0; wrk < 8; wrk++) {
                for (int inc = 0; inc < 13505; inc++) {
                    for (int logDist = 0; logDist < 11; logDist++) {
                        for (int aTyp = 0; aTyp < 4; aTyp++) {
                            // set DMU attributes
                            createCarOwnershipDMU.setLicense(lic);
                            createCarOwnershipDMU.setWorkers(wrk);
                            createCarOwnershipDMU.setIncome(inc);
                            createCarOwnershipDMU.setLogDistanceToTransit(logDist);
                            createCarOwnershipDMU.setAreaType(aTyp + 1);

                            double util[] = coModelUtility.solve(createCarOwnershipDMU.getDmuIndexValues(),
                                    createCarOwnershipDMU, coAvail);

                            for (int i = 1; i < coAvail.length; i++) {
                                util[i-1] = Math.exp(util[i-1]);
                            }

                            double prob0cars = 1d / (SiloUtil.getSum(util) + 1d);

                            for (int i = 1; i < coAvail.length; i++) {
                                carOwnerShipUtil[i-1][lic][wrk][inc][logDist][aTyp] = util[i-1] * prob0cars;
                            }

                            if (logCalculation) {
                                // log UEC values for each person type
                                coModelUtility.logAnswersArray(traceLogger, "Car Ownership Model. Lic: " + lic +
                                        ", wrk: " + wrk + ", inc: " + inc + ", logDist: " + logDist + ", aTyp: " + aTyp);

                                logger.info(lic + "," + wrk + "," + inc + "," + logDist + "," + aTyp + "," + prob0cars + "," +
                                        carOwnerShipUtil[0][lic][wrk][inc][logDist][aTyp] + "," +
                                        carOwnerShipUtil[1][lic][wrk][inc][logDist][aTyp] + "," +
                                        carOwnerShipUtil[2][lic][wrk][inc][logDist][aTyp]);
                            }
                        }
                    }
                }
            }
        }
    }

    public void run(boolean flagSkipCreationOfSPforDebugging) {
        // main run method
        //Read geoData
        geoData = new geoDataMuc(rb);
        geoData.setInitialData();
        for (Household hh: Household.getHouseholdArray()){
            simulateCarOwnership(hh);
        }
        //summarizeData.summarizeCarOwnershipByMunicipality(geoData);
        if (flagSkipCreationOfSPforDebugging) {
            logger.info("Finished car ownership model");
            System.exit(0);
        }
    }

    public void simulateCarOwnership(Household hh){
        // simulate number of autos for household hh
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as distance to transit and areaType is dependent on where households are living
        int license = Math.min(hh.getHHLicenseHolders(), 7);
        int workers = Math.min(hh.getNumberOfWorkers(), 7);
        int income = Math.min(hh.getHhIncome(), 13504);
        //todo. refactor distanceToTransit and areaType into this class instead of directing into geoData class [Ana and Rolf, 18.07.17]
        int logDistanceToTransit = Math.min((int) (Math.log(geoData.getDistanceToTransit(hh.getHomeZone()))), 10);
        int areaType = geoData.getAreaTypeOfZone(hh.getHomeZone());

        double[] prob = new double[4];
        for (int i = 1; i < 4; i++) {
            prob[i] = carOwnerShipUtil[i - 1][license][workers][income][logDistanceToTransit][areaType - 1];
        }
        prob[0] = 1 - SiloUtil.getSum(prob);
        hh.setAutos(SiloUtil.select(prob));
    }
}


