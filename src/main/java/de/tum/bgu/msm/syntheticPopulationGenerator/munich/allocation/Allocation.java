package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;


import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Allocation extends ModuleSynPop{

    private static final Logger logger = Logger.getLogger(Allocation.class);
    private final SiloDataContainer dataContainer;

    public Allocation(DataSetSynPop dataSetSynPop, SiloDataContainer dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
    }

    @Override
    public void run(){
        logger.info("   Started allocation model.");
        if (PropertiesSynPop.get().main.runAllocation) {
            generateJobs();
            generateHouseholdsPersonsDwellings();
            checkMarriages();
            if (PropertiesSynPop.get().main.disability){
                baseYearDisability();
            }
            if (PropertiesSynPop.get().main.runJobAllocation) {
                assignJobs();
                assignSchools();
                validateTripLengths();
            }
        } else {
            readPopulation();
        }
        logger.info("   Completed allocation model.");

    }

    public void generateHouseholdsPersonsDwellings(){
        if (PropertiesSynPop.get().main.boroughIPU){
            for (int county : dataSetSynPop.getBoroughsByCounty().keySet()){
                addBoroughsAsCities(county);
            }
        }
        new GenerateHouseholdsPersonsDwellings(dataContainer, dataSetSynPop).run();
        new GenerateVacantDwellings(dataContainer, dataSetSynPop).run();
        if (PropertiesSynPop.get().main.boroughIPU){
            for (int county : dataSetSynPop.getBoroughsByCounty().keySet()){
                removeBoroughsAsCities(county);
            }
        }
        SummarizeData.writeOutSyntheticPopulationDE(1990, dataContainer);
    }

    public void generateJobs(){
        if (PropertiesSynPop.get().main.boroughIPU){
            for (int county : dataSetSynPop.getBoroughsByCounty().keySet()){
                addBoroughsAsCities(county);
            }
        }
        new GenerateJobs(dataContainer, dataSetSynPop).run();
        if (PropertiesSynPop.get().main.boroughIPU){
            for (int county : dataSetSynPop.getBoroughsByCounty().keySet()){
                removeBoroughsAsCities(county);
            }
        }
        SummarizeData.writeOutSyntheticPopulationDE(1991, dataContainer);
    }

    public void assignJobs(){
        new AssignJobs(dataContainer, dataSetSynPop).run();
    }

    public void assignSchools(){
        new AssignSchools(dataContainer, dataSetSynPop).run();
        SummarizeData.writeOutSyntheticPopulationDE(1992, dataContainer);
    }

    public void readPopulation(){
        new ReadPopulation(dataContainer).run();
    }

    public void validateTripLengths(){
        new ValidateTripLengthDistribution(dataContainer, dataSetSynPop).run();
    }


    public void addBoroughsAsCities(int county){
        //Add to the municipality list the boroughs, because they have weights as well
        //Only if the option of running IPU with three areas is true
        ArrayList<Integer> newCities = new ArrayList<>();
        for (int city : dataSetSynPop.getMunicipalities()){
            if (!dataSetSynPop.getMunicipalitiesByCounty().get(county).contains(city)){
                newCities.add(city);
            }
        }
        newCities.addAll(dataSetSynPop.getBoroughsByCounty().get(county));
        dataSetSynPop.setMunicipalities(newCities);
    }

    public void removeBoroughsAsCities(int county){
        //Remove to the list of boroughs at the municipality, because they have weights for household generation but are not on the job allocation explicitly
        //Only if the option of running IPU with three areas is true
        ArrayList<Integer> newCities = dataSetSynPop.getMunicipalitiesByCounty().get(county);
        newCities.removeAll(dataSetSynPop.getBoroughsByCounty().get(county));
        dataSetSynPop.setMunicipalities(newCities);
    }


    private void baseYearDisability(){new BaseYearDisabilityModel(dataContainer).run();}

    private void checkMarriages(){
        new CheckMarriage(dataContainer, dataSetSynPop).run();
    }

}
