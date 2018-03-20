package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;


import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

import java.util.ArrayList;

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
            generateHouseholdsPersonsDwellings();
            generateJobs();
        } else {
            readPopulation();
        }
        if (PropertiesSynPop.get().main.runJobAllocation) {
            assignJobs();
            assignSchools();
            validateTripLengths();
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
        SummarizeData.writeOutSyntheticPopulationDE(1990, dataContainer);
    }

    public void generateJobs(){
        new GenerateJobs(dataContainer, dataSetSynPop).run();
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

}
