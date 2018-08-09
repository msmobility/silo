/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm;

import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Implements SILO for Minneapolis/St. Paul (MetCouncil)
 * @author Rolf Moeckel
 * Created on Jan 28, 2011 in Vienna, VA
 *
 */

public class SiloMsp {
    static Logger logger = Logger.getLogger(SiloMsp.class);

    /**
     * @param args Arguments fed in from command line
     */

    public static void main(String[] args) {

        ResourceBundle rb = SiloUtil.siloInitialization(args[0], Implementation.MSP);
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting SILO for Minneapolis/St. Paul");
            logger.info("Scenario: " + Properties.get().main.scenarioName);
            SyntheticPopUs sp = new SyntheticPopUs(rb);
            sp.runSP();
            SiloModel model = new SiloModel();
            model.runModel();
            logger.info("Finished SILO.");
        } catch (Exception e) {
            logger.error("Error running SILO.");
            throw new RuntimeException(e);
        } finally {
            SiloUtil.trackingFile("close");
            SummarizeData.resultFile("close");
            SummarizeData.resultFileSpatial("close");
            float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
            int hours = (int) (endTime / 60);
            int min = (int) (endTime - 60 * hours);
            logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
        }
    }
}
