package inputGeneration;

import data.SandboxDwellingType;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdFactoryImpl;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.io.input.DefaultGeoDataReader;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import org.locationtech.jts.geom.Coordinate;

import java.util.Random;

public class CreatePopulation {

    public static void main(String[] args) {

        GeoData geoData = new DefaultGeoData();
        new DefaultGeoDataReader(geoData).readZoneCsv("input/base/input/zoneSystem.csv");
        new DefaultGeoDataReader(geoData).readZoneShapefile("input/base/input/fabiland_shp/fabiland.csv");

        DwellingFactory ddFactory = new DwellingFactoryImpl();
        HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        PersonFactory ppFactory = new PersonFactoryImpl();
        JobFactory jjFactory = new JobFactoryImpl();


        int hhId = 1;
        int jjId = 1;
        int ppId = 1;
        int ddId = 1;

        int zoneId = 6;

        int row = zoneId / 5;
        int col = zoneId % 5 - 1;

        double x1 = col * 5000;
        double y1 = 20000 - row * 5000;

        Random rnd = new Random();

        for (int i = 0; i < 500; i++) {
            double x = x1 + rnd.nextInt(5000);
            double y = y1 + rnd.nextInt(5000);
            DwellingType type = SandboxDwellingType.SF;
            int price = 1000;
            if(i >= 250) {
                type = SandboxDwellingType.MF;
                price = 500;
            }
            Dwelling dwelling = ddFactory.createDwelling(ddId, zoneId, new Coordinate(x,y), hhId, type, 2, 4, price, 0);
            Household household = hhFactory.createHousehold(hhId, ddId, rnd.nextInt(3));
            if(rnd.nextDouble() < 1./6) {
                int age1 = rnd.nextInt(60) + 20;
                int age2 = age1 - rnd.nextInt(6);
//                int age3 =
                Occupation occupation1 = Occupation.UNEMPLOYED;
                if(rnd.nextDouble() < 0.5) {
                    occupation1 = Occupation.EMPLOYED;
                }
                Occupation occupation2 = Occupation.UNEMPLOYED;
                if(rnd.nextDouble() < 0.5) {
                    occupation2 = Occupation.EMPLOYED;
                }
                Person p1 = ppFactory.createPerson(ppId,age1, Gender.MALE, occupation1, PersonRole.MARRIED, -1, 500);
                ppId++;
                Person p2 = ppFactory.createPerson(ppId,age2, Gender.FEMALE, occupation2, PersonRole.MARRIED, -1, 500);
                ppId++;
//                Person p3 = ppFactory.createPerson(ppId, age3,)
//                ppId++;
            }
//            Person pp = ppFactory.


//                    hhId++;
//            ddId++;
        }


        DwellingData dwellingData = new DwellingDataImpl();
        new DefaultDwellingWriter(dwellingData.getDwellings());
    }
}
