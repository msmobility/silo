package sdg;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import sdg.data.DataContainerSdg;
import sdg.reader.EventAnalysis;
import sdg.reader.TripReader;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        String outputPath = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName;
        String networkFileName = "F:\\models\\muc\\scenOutput\\test\\2011\\trafficAssignment\\mito_assignment.output_network.xml.gz";
        String eventFileName = "F:\\models\\muc\\scenOutput\\test\\2011\\trafficAssignment\\mito_assignment.output_events.xml.gz";

        DataContainerSdg dataContainer = DataBuilderSdg.getModelData(properties, null);
        DataBuilderSdg.read(properties, dataContainer,2011);

        SDGCalculator sdgCalculator = new SDGCalculator();
        sdgCalculator.setMatsimPerson(new EventAnalysis().runEventAnalysis(networkFileName, eventFileName));
        sdgCalculator.calculateSdgIndicators(dataContainer, outputPath, Properties.get().main.startYear);
    }

}
