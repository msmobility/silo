package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;

import java.util.ArrayList;
import java.util.List;

public class SampleGenerator {


    /**
     * Runs the sample generator.
     * @param args: input array of<br>
     *            0: Path to properties <br>
     *            1: Implementation identifier (e.g. "MUNICH")<br>
     *            2: sample fraction value (e.g. "0.2" for 20%)
     */
    public static void main(String[] args) {
        final Properties properties = Properties.initializeProperties(args[0]);
        DataContainer dataContainer = DataContainerImpl.loadSiloDataContainer(properties);

        double sampleFraction = Double.parseDouble(args[2]);

        List<Household> toRemove = new ArrayList<>();
        for(Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
            if(Math.random() < sampleFraction) {
                toRemove.add(household);
            }
        }

        for(Household household: toRemove) {
            for(Person person: household.getPersons().values()) {
                dataContainer.getJobDataManager().removeJob(person.getJobId());
            }
            dataContainer.getRealEstateDataManager().removeDwelling(household.getDwellingId());
            dataContainer.getHouseholdDataManager().removeHousehold(household.getId());
        }

//TODO use writers
        SummarizeData.writeOutSyntheticPopulation(dataContainer);
    }
}
