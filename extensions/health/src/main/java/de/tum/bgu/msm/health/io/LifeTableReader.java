package de.tum.bgu.msm.health.io;

// Reads life tables in .csv format from www.destatis.de

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class LifeTableReader {

    private final static Logger logger = LogManager.getLogger(LifeTableReader.class);
    private final static NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

    private final static String SEP = ";";
    private final static int POS_QX = 2;
    private final static int LINES_TO_SKIP = 9;

    public static double[] readData(String fileName) {
        logger.info("Reading mortality rates from lifetable");
        double[] mortalityRates = new double[101];
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));

            for (int i = 1 ; i < LINES_TO_SKIP ; i++) {
                in.readLine();
            }

            for (int age = 0 ; age <= 100 ; age++) {
                String[] line = in.readLine().split(SEP);
                mortalityRates[age] = nf.parse(line[POS_QX]).doubleValue();
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading life table file: " + fileName);
        } catch (ParseException e) {
            logger.fatal(e.getMessage());
        }
        logger.info("Finished reading life table");
        return mortalityRates;
    }

}
