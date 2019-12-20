package inputGeneration;

import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.dwelling.DwellingDataImpl;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdFactoryImpl;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.data.person.PersonFactoryImpl;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;

public class CreatePopulation {

    public static void main(String[] args) {


        DwellingFactory ddFactory = new DwellingFactoryImpl();
        HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        PersonFactory ppFactory = new PersonFactoryImpl();
        JobFactory jjFactory = new JobFactoryImpl();

        int hhId = 1;
        int jjId = 1;
        int ppId = 1;
        int ddId = 1;

        int zoneId =6;

        int row = zoneId/5;
        int col = zoneId%5-1;

        double x1 = col * 5000;
        double y1 = 20000 - row * 5000;

//        Polygon zoneGeom =
//        Envelope envelope = new Envelope(x1, x1+5000, y1, y1+5000);
//        SeededRandomPointsBuilder randomPointsBuilder = new SeededRandomPointsBuilder(new GeometryFactory(), new Random());
//        for(int i = 0; i < 500; i++) {
//            Dwelling dwelling = ddFactory.createDwelling(ddId, zoneId, randomPointsBuilder );
//            Household household = hhFactory.createHousehold(hhId);
//            hhId++;
//            ddId++;
//        }



        DwellingData dwellingData = new DwellingDataImpl();
        new DefaultDwellingWriter(dwellingData.getDwellings());
    }
}
