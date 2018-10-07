package sdg;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SDGCalculator {

    private static Logger logger = Logger.getLogger(SDGCalculator.class);

    public static void calculateSdgIndicators(SiloDataContainer siloDataContainer, String outputPath, int year){

        //load data from the container into this class



        //calculate indicators in separate classes, e.g. by indicator type and data requirement
        // AccessibilitySDGIndicators.calculateIcators(data1, data3 ,data3, fileName)


        Collection<Household> households = siloDataContainer.getHouseholdData().getHouseholds();


        Map<Integer,List<Household>> hhBySize = households.parallelStream().collect(Collectors.groupingBy(hh -> hh.getPersons().size()));
        hhBySize.entrySet().forEach(entry -> logger.info(entry.getKey() + " " + entry.getValue().stream().filter(hh -> (double) HouseholdUtil.getHhIncome(hh) < 10000).count() / (double) entry.getValue().size()));





        Map<Integer,List<Household>> hhByRegion = households.parallelStream().collect(Collectors.groupingBy(hh -> hh.getDwellingId()));
        hhBySize.entrySet().forEach(entry -> logger.info(entry.getKey() + " " + entry.getValue().stream().filter(hh -> (double) HouseholdUtil.getHhIncome(hh) < 10000).count() / (double) entry.getValue().size()));


//        logger.info(hhUnder);


    }
}
