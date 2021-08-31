package de.tum.bgu.msm.scenarios.disabilities;

import de.tum.bgu.msm.ModelBuilderMuc;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.schools.DataContainerWithSchoolsImpl;
import de.tum.bgu.msm.events.DisabilityEvent;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.models.disability.DefaultDisabilityStrategy;
import de.tum.bgu.msm.models.disability.DisabilityImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.SchoolsWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloMucDisability {

    private final static Logger logger = Logger.getLogger(SiloMucDisability.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for the Munich Metropolitan Area");
        DataContainerWithSchoolsImpl dataContainer = DataBuilderDisability.getModelDataForMuc(properties, config);
        DataBuilderDisability.read(properties, dataContainer);
        //summarizeData(dataContainer, properties);
        ModelContainer modelContainer = ModelBuilderMuc.getModelContainerForMuc(dataContainer, properties, config);
        modelContainer.registerEventModel(DisabilityEvent.class, new DisabilityImpl(dataContainer, properties,new DefaultDisabilityStrategy(), SiloUtil.provideNewRandom()));
        ResultsMonitor resultsMonitor = new MultiFileResultsMonitorMuc(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.runModel();
        logger.info("Finished SILO.");
    }

    private static void summarizeData(DataContainerWithSchoolsImpl dataContainer, Properties properties){

        String filehh = properties.main.baseDirectory
                + properties.householdData.householdFileName
                + "_"
                + properties.main.baseYear
                + "d.csv";
        HouseholdWriter hhwriter = new HouseholdWriterMucDisability(dataContainer.getHouseholdDataManager(),dataContainer.getRealEstateDataManager());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear
                + "d.csv";
        PersonWriter ppwriter = new PersonWriterMucDisability(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear
                + "d.csv";
        DwellingWriter ddwriter = new DefaultDwellingWriter(dataContainer.getRealEstateDataManager().getDwellings());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear
                + "d.csv";
        JobWriter jjwriter = new JobWriterMuc(dataContainer.getJobDataManager());
        jjwriter.writeJobs(filejj);


        String fileee = properties.main.baseDirectory
                + "ee"
                + "_"
                + properties.main.baseYear
                + "d.csv";
        SchoolsWriter eewriter = new SchoolsWriter(dataContainer.getSchoolData());
        eewriter.writeSchools(fileee);

    }
}