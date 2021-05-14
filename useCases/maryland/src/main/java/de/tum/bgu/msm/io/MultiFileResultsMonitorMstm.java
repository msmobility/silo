package de.tum.bgu.msm.io;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMstm;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class MultiFileResultsMonitorMstm extends MultiFileResultsMonitor {
    private DataContainer dataContainer;
    private Properties properties;
    private PrintWriter popByRace;
    private final Logger logger = Logger.getLogger(MultiFileResultsMonitorMstm.class);


    public MultiFileResultsMonitorMstm(DataContainer dataContainer, Properties properties) {
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


        int ppRace[] = new int[Race.values().length];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            ppRace[((PersonMstm) per).getRace().ordinal()]++;
        }
        popByRace.println("white," + ppRace[0]);
        popByRace.println("black," + ppRace[1]);
        popByRace.println("hispanic," + ppRace[2]);
        popByRace.println("other," + ppRace[3]);

    }


}
