package sdg;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import sdg.data.AnalyzedPerson;
import sdg.data.DataContainerSdg;
import sdg.reader.EventAnalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class RunCongestionAnalysis {
    private static String networkFileName = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/year.output_network.xml.gz";
    private static String eventFileName = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/year.output_events.xml.gz";
    private static String output = "H:\\FDisk\\models\\mitoMunich\\scenOutput\\mitoMopedPaper\\mito2.0WithMoped_base_withMatsim\\2011\\trafficAssignment\\BikePed/delayTime.csv";


    public static void main(String[] args) {
        Properties properties = SiloUtil.siloInitialization(args[0]);
        DataContainerSdg dataContainer = DataBuilderSdg.getModelData(properties, null);
        DataBuilderSdg.read(properties, dataContainer, Properties.get().main.startYear);
        SDGCalculator sdgCalculator = new SDGCalculator();
        sdgCalculator.setMatsimPerson(new EventAnalysis().runEventAnalysis(networkFileName, eventFileName));
        try {
            writeOutDelayTime(sdgCalculator,output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeOutDelayTime (SDGCalculator sdgCalculator, String output) throws FileNotFoundException {
        StringBuilder tt = new StringBuilder();

        //write header
        tt.append("matsimPersonId,travelDistance,congestedTravelTime,freeFlowTravelTime,delayTime");
        tt.append('\n');
        for (AnalyzedPerson analyzedPerson : sdgCalculator.getMatsimPerson().values()) {
            tt.append(analyzedPerson.getId());
            tt.append(',');
            tt.append(analyzedPerson.getTravelDistance());
            tt.append(',');
            tt.append(analyzedPerson.getCongestedTime());
            tt.append(',');
            tt.append(analyzedPerson.getFreeFlowTime());
            tt.append(',');
            double delayTime = analyzedPerson.getCongestedTime()-analyzedPerson.getFreeFlowTime();
            tt.append(delayTime);
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
