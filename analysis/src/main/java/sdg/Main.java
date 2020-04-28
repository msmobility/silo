package sdg;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import sdg.data.DataContainerSdg;
import sdg.reader.EventAnalysis;

public class Main {

    public static void main(String[] args) {


            Properties properties = SiloUtil.siloInitialization(args[0]);
            String outputPath = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName;

            DataContainerSdg dataContainer = DataBuilderSdg.getModelData(properties, null);
            DataBuilderSdg.read(properties, dataContainer, Properties.get().main.startYear);
            dataContainer.getGeoData().setup();
            dataContainer.getJobDataManager().setup();
            dataContainer.getHouseholdDataManager().setup();
            dataContainer.getRealEstateDataManager().setup();
            String networkFileName = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/matsim/" + Properties.get().main.startYear + "/" + Properties.get().main.startYear + ".output_network.xml.gz";
            String eventFileName = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/matsim/" + Properties.get().main.startYear + "/" + Properties.get().main.startYear + ".output_events.xml.gz";
            SDGCalculator sdgCalculator = new SDGCalculator();
            sdgCalculator.setMatsimPerson(new EventAnalysis().runEventAnalysis(networkFileName, eventFileName));
            sdgCalculator.calculateSdgIndicators(dataContainer, outputPath, Properties.get().main.startYear);

            DataContainerSdg dataContainerFuture = DataBuilderSdg.getModelData(properties, null);
            DataBuilderSdg.read(properties, dataContainerFuture, Properties.get().main.endYear);
            dataContainer.getGeoData().setup();
            dataContainer.getJobDataManager().setup();
            dataContainer.getHouseholdDataManager().setup();
            dataContainer.getRealEstateDataManager().setup();
            String networkFinalFileName = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/matsim/" + Properties.get().main.endYear + "/" + Properties.get().main.endYear + ".output_network.xml.gz";
            String eventFinalFileName = Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/matsim/" + Properties.get().main.endYear + "/" + Properties.get().main.endYear + ".output_events.xml.gz";
            SDGCalculator sdgCalculatorFuture = new SDGCalculator();
            sdgCalculatorFuture.setMatsimPerson(new EventAnalysis().runEventAnalysis(networkFinalFileName, eventFinalFileName));
            sdgCalculatorFuture.calculateSdgIndicators(dataContainerFuture, outputPath, Properties.get().main.endYear);

    }

}
