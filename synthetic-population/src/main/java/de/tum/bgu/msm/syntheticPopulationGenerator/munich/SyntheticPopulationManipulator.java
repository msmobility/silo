package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SyntheticPopulationManipulator {

    static Logger logger = Logger.getLogger(SyntheticPopulationManipulator.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);

        logger.info("Load silo data container");
        Map<Integer, Household> householdMap = new HashMap<>();
        Map<Integer, Person> personMap = new HashMap<>();
        SiloDataContainer siloDataContainer = SiloDataContainer.loadSiloDataContainer(properties, householdMap, personMap);

        //manipulate objects if needed

        logger.info("Write out synthetic population");
        SummarizeData.writeOutSyntheticPopulation(Properties.get().main.implementation.BASE_YEAR, siloDataContainer);


    }
}
