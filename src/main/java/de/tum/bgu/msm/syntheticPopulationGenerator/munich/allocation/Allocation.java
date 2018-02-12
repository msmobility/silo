package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;


import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Allocation extends ModuleSynPop{

    private static final Logger logger = Logger.getLogger(Allocation.class);

    public Allocation(DataSetSynPop dataSetSynPop){
        super(dataSetSynPop);
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
        new GenerateHouseholdsPersonsDwellings(dataSetSynPop).run();
        SummarizeData.writeOutSyntheticPopulationDE(1990);
    }

    public void generateJobs(){
        new GenerateJobs(dataSetSynPop).run();
        SummarizeData.writeOutSyntheticPopulationDE(1991);
    }

    public void assignJobs(){
        new AssignJobs(dataSetSynPop).run();
    }

    public void assignSchools(){
        new AssignSchools(dataSetSynPop).run();
        SummarizeData.writeOutSyntheticPopulationDE(1992);
    }

    public void readPopulation(){
        new ReadPopulation().run();
    }

    public void validateTripLengths(){
        new ValidateTripLengthDistribution(dataSetSynPop).run();
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
