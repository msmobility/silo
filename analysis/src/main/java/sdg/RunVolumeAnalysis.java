package sdg;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.scenario.ScenarioUtils;
import sdg.data.AnalyzedPerson;
import sdg.data.DataContainerSdg;
import sdg.data.MyLink;
import sdg.reader.CongestionEventHandler;
import sdg.reader.EventAnalysis;
import sdg.reader.VolumeEventHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class RunVolumeAnalysis {
    private static String networkFileName = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/year.output_network.xml.gz";
    private static String eventFileName = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/year.output_events.xml.gz";
    private static String output = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/linkVolume.csv";


    public static void main(String[] args) {
        EventsManager eventsManager = new EventsManagerImpl();
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFileName);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        VolumeEventHandler volumeEventHandler = new VolumeEventHandler(scenario.getNetwork());
        eventsManager.addHandler(volumeEventHandler);
        EventsUtils.readEvents(eventsManager,eventFileName);

        try {
            writeOutVolume(volumeEventHandler.getMyLinkList(),output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeOutVolume (Map<Id<Link>, MyLink> myLinkMap, String output) throws FileNotFoundException {
        StringBuilder tt = new StringBuilder();

        //write header
        tt.append("linkId,volume");
        tt.append('\n');
        for (Id<Link> linkId : myLinkMap.keySet()) {
            tt.append(linkId);
            tt.append(',');
            tt.append(myLinkMap.get(linkId).getDailyVolume());
            tt.append('\n');
        }

        writeToFile(output,tt.toString());
    }

    public static void writeToFile(String path, String building) throws FileNotFoundException {
        PrintWriter bd = new PrintWriter(new FileOutputStream(path, true));
        bd.write(building);
        bd.close();
    }

}
