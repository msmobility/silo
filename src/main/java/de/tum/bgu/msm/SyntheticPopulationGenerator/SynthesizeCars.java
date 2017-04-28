package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Implements auto ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/04/2017 in Munich, Germany.
 */

public class SynthesizeCars {

    static Logger logger = Logger.getLogger(SynthesizeCars.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private ResourceBundle rb;

    protected static final String PROPERTIES_AutoOwnership_UEC_FILE               = "AutoOwnership.UEC.FileName";
    protected static final String PROPERTIES_AutoOwnership_UEC_DATA_SHEET         = "AutoOwnership.UEC.DataSheetNumber";
    protected static final String PROPERTIES_AutoOwnership_UEC_OWNERSHIP_UTILITY  = "AutoOwnership.UEC.Ownership.Utility";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.autoOwnership";
    private String uecFileName;
    private int dataSheetNumber;
    int numAltsAutoOwnership;
    private double[][][][][][] autoOwnerShipUtil;   // [three probabilities][license][workers][income][logdistToTransit][areaType]


    public SynthesizeCars(ResourceBundle rb) {
        // Constructor
        logger.info(" Setting up probabilities to synthesize cars");
        this.rb = rb;
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_AutoOwnership_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_AutoOwnership_UEC_DATA_SHEET);
        setupCarSynthesis();
    }


    private void setupCarSynthesis() {

        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION);
        int aoModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_AutoOwnership_UEC_OWNERSHIP_UTILITY);
        UtilityExpressionCalculator aoModelUtility = new UtilityExpressionCalculator(new File(uecFileName),
                aoModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                SynthesizeCarsDMU.class);
        SynthesizeCarsDMU synthesizeCarsDMU = new SynthesizeCarsDMU();

        // everything is available
        numAltsAutoOwnership = aoModelUtility.getNumberOfAlternatives();

        int[] aoAvail = new int[numAltsAutoOwnership + 1];
        for (int i = 1; i < aoAvail.length; i++) {
            aoAvail[i] = 1;
        }

        autoOwnerShipUtil = new double[3][8][8][13505][11][4];
        for (int lic = 0; lic < 8; lic++) {
            for (int wrk = 0; wrk < 8; wrk++) {
                for (int inc = 0; inc < 13505; inc++) {
                    for (int logDist = 0; logDist < 11; logDist++) {
                        for (int aTyp = 0; aTyp < 4; aTyp++) {
                            // set DMU attributes
                            synthesizeCarsDMU.setLicense(lic);
                            synthesizeCarsDMU.setWorkers(wrk);
                            synthesizeCarsDMU.setIncome(inc);
                            synthesizeCarsDMU.setLogDistanceToTransit(logDist);
                            synthesizeCarsDMU.setAreaType(aTyp + 1);

                            double util[] = aoModelUtility.solve(synthesizeCarsDMU.getDmuIndexValues(),
                                    synthesizeCarsDMU, aoAvail);

                            for (int i = 1; i < aoAvail.length; i++) {
                                util[i-1] = Math.exp(util[i-1]);
                            }

                            double prob0cars = 1d / (SiloUtil.getSum(util) + 1d);

                            for (int i = 1; i < aoAvail.length; i++) {
                                autoOwnerShipUtil[i-1][lic][wrk][inc][logDist][aTyp] = util[i-1] * prob0cars;
                            }

                            if (logCalculation) {
                                // log UEC values for each person type
                                aoModelUtility.logAnswersArray(traceLogger, "Car Synthesis. Lic: " + lic +
                                        ", wrk: " + wrk + ", inc: " + inc + ", logDist: " + logDist + ", aTyp: " + aTyp);

                                logger.info(lic + "," + wrk + "," + inc + "," + logDist + "," + aTyp + "," + prob0cars + "," +
                                        autoOwnerShipUtil[0][lic][wrk][inc][logDist][aTyp] + "," +
                                        autoOwnerShipUtil[1][lic][wrk][inc][logDist][aTyp] + "," +
                                        autoOwnerShipUtil[2][lic][wrk][inc][logDist][aTyp]);
                            }
                        }
                    }
                }
            }
        }
    }

    public void selectAutoOwnership (Household hh, geoDataMuc geoData) {
        // simulate number of autos for household hh (Main version)
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as distance to transit and areaType is dependent on where households are living

        int license = Math.min(hh.getHHLicenseHolders(), 7);
        int workers = Math.min(hh.getNumberOfWorkers(), 7);
        int income = Math.min(hh.getHhIncome(), 13504);
        int logDistanceToTransit = Math.min((int)(Math.log(geoData.getDistanceToTransit(hh.getHomeZone()))), 10);
        int areaType = geoData.getAreaTypeOfZone(hh.getHomeZone());

        double[] prob = new double[4];

        for (int i = 1; i < 4; i++) prob[i] =
                autoOwnerShipUtil[i-1][license][workers][income][logDistanceToTransit][areaType - 1];

        prob[0] = 1 - SiloUtil.getSum(prob);
        hh.setAutos(SiloUtil.select(prob));

        summarizeData.summarizeAutoOwnershipByMunicipality(geoData);
    }
}
