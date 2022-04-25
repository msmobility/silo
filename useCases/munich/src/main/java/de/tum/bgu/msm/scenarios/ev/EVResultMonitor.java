package de.tum.bgu.msm.scenarios.ev;

import com.google.common.collect.*;
import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.carOwnership.*;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.*;

import java.io.*;
import java.util.*;

public class EVResultMonitor implements ResultsMonitor {

    private final Properties properties;
    private DataContainer dataContainer;

    private PrintWriter resultWriter;
    private final static Logger logger = Logger.getLogger(EVResultMonitor.class);

    public EVResultMonitor(ModelContainer modelContainer, DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public void setup() {
        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/ev_ownership.csv";
        try {
            resultWriter = new PrintWriter(new File(pathname));
            resultWriter.println("year,hhs,n_autos,n_cv,n_ev");
        } catch (FileNotFoundException e) {
            logger.error("Cannot write the result file: " + pathname, e);
        }
    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {

        int nAutos = 0;
        int nEVs = 0;

        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            nAutos += hh.getAutos();
            nEVs += (int) hh.getAttribute("EV").orElse(0);
        }

        int nCVs = nAutos - nEVs;

        resultWriter.println(year + "," +
                dataContainer.getHouseholdDataManager().getHouseholds().size() + "," +
                nAutos + "," +
                nCVs + "," +
                nEVs);

    }

    @Override
    public void endSimulation() {
        resultWriter.close();
    }
}
