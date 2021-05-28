package de.tum.bgu.msm.io.output;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


public class HouseholdSatisfactionMonitor implements ResultsMonitor {

    private final static Logger logger = Logger.getLogger(HouseholdSatisfactionMonitor.class);
    private DataContainer dataContainer;
    private Properties properties;
    private ModelContainer modelContainer;
    private PrintWriter printWriter;
    private MovesModelImpl movesModel;
    private boolean enabled = false;

    public HouseholdSatisfactionMonitor(DataContainer dataContainer, Properties properties, ModelContainer modelContainer) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.modelContainer = modelContainer;
    }

    @Override
    public void setup() {
        try{
            movesModel = (MovesModelImpl) modelContainer.getEventModels().get(MoveEvent.class);
            enabled = true;
        } catch (ClassCastException e){
            logger.error("The implemented moves model is not a MovelsModelImpl so the household satisfaction cannot be printed out");
        }

        if (enabled) {
            String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/";
            try {
                File file = new File(pathname + "hhSatisfactionByRegion.csv");
                file.getParentFile().mkdirs();
                printWriter = new PrintWriter(file);
                printWriter.println("year,zone,region,hh_count,hh_ave_satisfaction");
            } catch (
                    FileNotFoundException e) {
                logger.error("Cannot write the result file: " + pathname, e);
            }
        }



    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {
        if (enabled){
            Map<Integer, Integer> householdsByZone = movesModel.getHouseholdsByZone();
            Map<Integer, Double> sumOfSatisfactionsByZone = movesModel.getSumOfSatisfactionsByZone();
            for (Zone zone : dataContainer.getGeoData().getZones().values()){
                int hhThisZone = householdsByZone.getOrDefault(zone.getZoneId(), 0);
                double avgSatisfactionThisZone = hhThisZone != 0 ? sumOfSatisfactionsByZone.getOrDefault(zone.getZoneId(),0.)/hhThisZone : -1;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(year).append(",").append(zone.getId()).append(",").
                        append(zone.getRegion().getId()).append(",").append(hhThisZone).
                        append(",").append(avgSatisfactionThisZone);
                printWriter.println(stringBuilder);
            }
            printWriter.flush();
        }
    }

    @Override
    public void endSimulation() {
        if (enabled){
            printWriter.close();
        }

    }
}
