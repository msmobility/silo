package sdg;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import sdg.data.AnalyzedPerson;
import sdg.reader.EventAnalysis;
import java.util.Map;

public class DelayCalculator {

    public static void main(String[] args) {

        String eventFileName = args[0];
        String networkFileName = args[1];

        Map<Id<Person>, AnalyzedPerson> integerAnalyzedPersonMap = new EventAnalysis().runEventAnalysis(networkFileName, eventFileName);

        double travelTime = 0;
        double freeFlowTravelTime = 0;
        double delay = 0;

        for (AnalyzedPerson analyzedPerson : integerAnalyzedPersonMap.values()){
            travelTime += analyzedPerson.getCongestedTime();
            freeFlowTravelTime += analyzedPerson.getFreeFlowTime();
            delay += analyzedPerson.getCongestedTime() - analyzedPerson.getFreeFlowTime();

        }

        System.out.println("Values are not scaled");
        System.out.println("Travel time under congestion = " + travelTime);
        System.out.println("Travel time under free flow = " + freeFlowTravelTime);
        System.out.println("Delay = " + delay);
        System.out.println("Size = " + integerAnalyzedPersonMap.size());


    }

}
