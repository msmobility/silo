package edu.umd.ncsg.transportModel.tripGeneration;

import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class creates one dimensional segmentations, such as: 1 to 3: 1-1.2-2.3-3, 1-2.3-3, 1-1.2-3, 1-3
 *
 * @author Rolf Moeckel
 * @version 1.0, Jul 2nd, 2014 (College Park, MD)
 * Created by IntelliJ IDEA.
 */

public class CreateOneDimensionalSegmentation {
    private static Logger logger = Logger.getLogger(HouseholdTypeExploration.class);


    public static void createSegmentations(ResourceBundle rb) {
        // create possible segmentations for one-dimensional arrays

        logger.info("Creating possible segmentations for one-dimensional arrays");
        for (int arraySize = 1; arraySize <= 12; arraySize++) {
            logger.info("  - Array length " + arraySize);
            createOneDimensionalSegmentation(rb, arraySize);
        }
    }


    private static void createOneDimensionalSegmentation(ResourceBundle rb, int dim) {
        // create segmentation definitions

        ArrayList<String> al = new ArrayList<>();
        // position 1
        int positionA = 1;
        for (int aggA = 0; aggA <= dim; aggA++) {
            if (dim >= 10) logger.info("    (now on " + aggA + ")");
            if (positionA+aggA <= dim + 1) {
                String txtA = positionA + "-" + (positionA + aggA);
                if ((positionA+aggA) == dim) al.add(txtA);
                // position 2
                int startPosB = positionA + aggA + 1;
                for (int aggB = 0; aggB <= dim; aggB++) {
                    String txtB = startPosB + "-" + (startPosB + aggB);
                    if ((startPosB+aggB) == dim) al.add(txtA + "." + txtB);
                    // position 3
                    int startPosC = startPosB + aggB + 1;
                    for (int aggC = 0; aggC <= dim; aggC++) {
                        String txtC = startPosC + "-" + (startPosC + aggC);
                        if ((startPosC+aggC) == dim)al.add (txtA + "." + txtB + "." + txtC);
                        // position 4
                        int startPosD = startPosC + aggC + 1;
                        for (int aggD = 0; aggD <= dim; aggD++) {
                            String txtD = startPosD + "-" + (startPosD + aggD);
                            if ((startPosD+aggD) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD);
                            // position 5
                            int startPosE = startPosD + aggD + 1;
                            for (int aggE = 0; aggE <= dim; aggE++) {
                                String txtE = startPosE + "-" + (startPosE + aggE);
                                if ((startPosE+aggE) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE);
                                // position 6
                                int startPosF = startPosE + aggE + 1;
                                for (int aggF = 0; aggF <= dim; aggF++) {
                                    String txtF = startPosF + "-" + (startPosF + aggF);
                                    if ((startPosF+aggF) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF);
                                    // position 7
                                    int startPosG = startPosF + aggF + 1;
                                    for (int aggG = 0; aggG <= dim; aggG++) {
                                        String txtG = startPosG + "-" + (startPosG + aggG);
                                        if ((startPosG + aggG) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF + "." + txtG);
                                        // position 8
                                        int startPosH = startPosG + aggG + 1;
                                        for (int aggH = 0; aggH <= dim; aggH++) {
                                            String txtH = startPosH + "-" + (startPosH + aggH);
                                            if ((startPosH + aggH) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF + "." + txtG + "." + txtH);
                                            // position 9
                                            int startPosI = startPosH + aggH + 1;
                                            for (int aggI = 0; aggI <= dim; aggI++) {
                                                String txtI = startPosI + "-" + (startPosI + aggI);
                                                if ((startPosI + aggI) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF + "." + txtG + "." + txtH + "." + txtI);
                                                // position 10
                                                int startPosJ = startPosI + aggI + 1;
                                                for (int aggJ = 0; aggJ <= dim; aggJ++) {
                                                    String txtJ = startPosJ + "-" + (startPosJ + aggJ);
                                                    if ((startPosJ + aggJ) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF + "." + txtG + "." + txtH + "." + txtI + "." + txtJ);
                                                    // position 11
                                                    int startPosK = startPosJ + aggJ + 1;
                                                    for (int aggK = 0; aggK <= dim; aggK++) {
                                                        String txtK = startPosK + "-" + (startPosK + aggK);
                                                        if ((startPosK + aggK) == dim) al.add(txtA + "." + txtB + "." + txtC + "." + txtD + "." + txtE + "." + txtF + "." + txtG + "." + txtH + "." + txtI + "." + txtJ + "." + txtK);
                                                        // todo: for some reason model is missing writing out the last (most detailed) classification: 1-1.2-2.3-3.4-4.5-5.6-6.7-7.8-8.9-9.10-10.11-11.12-12
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        File file = new File ("output/tripGen");
        if (!file.exists()) {
            boolean outputDirectorySuccessfullyCreated = file.mkdir();
            if (!outputDirectorySuccessfullyCreated) logger.warn("Could not create scenario directory output/tripGen/");
        }
        String fileName = rb.getString("one.dim.array.combos.files") + "_" + dim + ".csv";
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.println("count,definition");
        for (int i = 0; i < al.size(); i++) pw.println((i + 1) + "," + al.get(i));
        pw.close();
    }

}
