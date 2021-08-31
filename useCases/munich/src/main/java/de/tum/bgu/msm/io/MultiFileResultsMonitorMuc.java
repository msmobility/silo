package de.tum.bgu.msm.io;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class MultiFileResultsMonitorMuc extends MultiFileResultsMonitor {


    private DataContainer dataContainer;
    private Properties properties;
    private PrintWriter popByRace;
    private final Logger logger = Logger.getLogger(MultiFileResultsMonitorMuc.class);


    public MultiFileResultsMonitorMuc(DataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public void setup() {
        super.setup();

        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/";
        try {
            popByRace = new PrintWriter(new File(pathname + "persByRace.csv"));
        } catch (FileNotFoundException e) {
            logger.error("Cannot write the result file: " + pathname, e);
        }

    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {
        super.endYear(year, eventCounter, events);
        summarizePopulationByRace(year);
        popByRace.flush();
    }

    @Override
    public void endSimulation() {
        super.endSimulation();
        popByRace.close();

    }


    private void summarizePopulationByRace(int year) {

        if (year == properties.main.baseYear) {
            popByRace.println("ppByRace,hh");
        }

        int ppRace[] = new int[1];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            ppRace[0]++;
        }
        popByRace.println("no_race," + ppRace[0]);


    }

}
