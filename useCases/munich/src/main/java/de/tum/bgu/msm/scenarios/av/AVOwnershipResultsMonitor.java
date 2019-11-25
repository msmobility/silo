package de.tum.bgu.msm.scenarios.av;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.models.carOwnership.SwitchToAutonomousVehicleModelMuc;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class AVOwnershipResultsMonitor implements ResultsMonitor {

    private SwitchToAutonomousVehicleModelMuc switchToAutonomousVehicleModelMuc = null;
    private final Properties properties;
    private PrintWriter resultWriter;
    private final static Logger logger = Logger.getLogger(SwitchToAutonomousVehicleModelMuc.class);

    public AVOwnershipResultsMonitor(ModelContainer modelContainer, Properties properties) {
        this.properties = properties;
        for (ModelUpdateListener listener : modelContainer.getModelUpdateListeners()){
            if (listener != null && listener.getClass().equals(SwitchToAutonomousVehicleModelMuc.class)) {
                switchToAutonomousVehicleModelMuc = (SwitchToAutonomousVehicleModelMuc) listener;
                break;
            }
        }

    }


    @Override
    public void setup() {
        if (switchToAutonomousVehicleModelMuc != null){
            String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/avOwnership.csv";
            try {
                resultWriter = new PrintWriter(new File(pathname));
                resultWriter.println("year,hhs,autos,avs,events");
            } catch (FileNotFoundException e) {
                logger.error("Cannot write the result file: " + pathname, e);
            }
        }

    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter) {
//        summary.put("hh", hh);
//        summary.put("autos", autos_counter);
//        summary.put("avs", av_counter);
//        summary.put("avs", event_counter);
        Map<String, Integer> summaryForThisYear = switchToAutonomousVehicleModelMuc.getSummaryForThisYear();
        resultWriter.print(year);
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("hh"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("autos"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("avs"));
        resultWriter.print(",");
        resultWriter.print(summaryForThisYear.get("avs"));
        resultWriter.println();

        resultWriter.flush();
    }

    @Override
    public void endSimulation() {

        resultWriter.close();

    }
}
