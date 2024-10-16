package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class HealthDiseaseTrackerWriter {

    private final static Logger logger = Logger.getLogger(HealthDiseaseTrackerWriter.class);
    protected final DataContainer dataContainer;
    private final HouseholdDataManager householdData;

    public HealthDiseaseTrackerWriter(DataContainer dataContainer) {
        this.householdData = dataContainer.getHouseholdDataManager();
        this.dataContainer = dataContainer;
    }

    public void writeHealthDiseaseTracking(String path) {

        logger.info("  Writing health disease tracking file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);

        //print header
        pwp.print("id");
        int startYear = Properties.get().main.baseYear - 1;
        int endYear = Properties.get().main.endYear;

        for(int i = startYear; i < endYear; i++) {
            pwp.print(",");
            pwp.print(i);
        }
        pwp.println();

        //print disease tracking of alive person
        for (Person pp : householdData.getPersons()) {
            pwp.print(pp.getId());
            for(int i = startYear; i < endYear; i++) {
                List<String> diseaseState = ((PersonHealth) pp).getHealthDiseaseTracker().get(i);
                if(diseaseState == null) {
                    pwp.print(",");
                    pwp.print("null");
                }else {
                    pwp.print(",");
                    pwp.print(String.join("|", diseaseState));
                }
            }
            pwp.println();
        }

        //print disease tracking of removed(died) person
        for (int ppId : ((DataContainerHealth) dataContainer).getHealthDiseaseTrackerRemovedPerson().keySet()) {
            pwp.print(ppId);
            Map<Integer, List<String>> healthDiseaseTracker =  ((DataContainerHealth) dataContainer).getHealthDiseaseTrackerRemovedPerson().get(ppId);
            for(int i = startYear; i < endYear; i++) {
                List<String> diseaseState = healthDiseaseTracker.get(i);
                if(diseaseState == null) {
                    pwp.print(",");
                    pwp.print("null");
                }else {
                    pwp.print(",");
                    pwp.print(String.join("|", diseaseState));
                }
            }
            pwp.println();
        }

        pwp.close();
    }
}
