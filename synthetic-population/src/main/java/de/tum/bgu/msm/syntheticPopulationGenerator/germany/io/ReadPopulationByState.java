package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdFactoryMuc;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import javax.swing.table.TableRowSorter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadPopulationByState {

    private static final Logger logger = Logger.getLogger(ReadPopulationByState.class);
    private final DataContainer dataContainer;
    private final String state;
    private String folder;


    public ReadPopulationByState(DataContainer dataContainer, String state){
        this.dataContainer = dataContainer;
        this.state = state;
    }

    public void run(){
        logger.info("   Running module: read population");
        folder = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles + "/" + state + "/";
        readHouseholdData(Properties.get().main.startYear);
        readPersonData(Properties.get().main.startYear);
        readDwellingData(Properties.get().main.startYear, state);
    }


    private void readHouseholdData(int year) {
        logger.info("Reading household micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactory householdFactory = householdData.getHouseholdFactory();
        String fileName =  folder + PropertiesSynPop.get().main.householdsFileName + "_" + year + ".csv";
        HouseholdReaderMucMito hhReader = new HouseholdReaderMucMito(householdData, (HouseholdFactoryMuc) householdFactory);
        hhReader.readData(fileName);
    }


    private void readPersonData(int year) {
        logger.info("Reading person micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        String fileName = folder + PropertiesSynPop.get().main.personsFileName + "_" + year + ".csv";
        PersonReaderMucMito ppReader = new PersonReaderMucMito(householdData);
        ppReader.readData(fileName);
    }


    private void readDwellingData(int year, String state) {
        logger.info("Reading dwelling micro data from ascii file");

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        String fileName = folder + PropertiesSynPop.get().main.dwellingsFileName + "_" + year + ".csv";
        DwellingReaderMucMito ddReader = new DwellingReaderMucMito(realEstate);
        boolean changeCoordinates = true;
        if (changeCoordinates) {
            ddReader.readDatawithStateChangeCoordinates(fileName, state);
        } else {
            ddReader.readData(fileName);
        }
    }


}
