package inputGeneration;

import data.SandboxDwellingTypes;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.io.output.DefaultHouseholdWriter;
import de.tum.bgu.msm.io.output.DefaultJobWriter;
import de.tum.bgu.msm.io.output.DefaultPersonWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.core.utils.misc.Counter;

import java.util.Random;

public class CreatePopulation {
    private final static Logger logger = Logger.getLogger(CreatePopulation.class);

    private final static String scenarioFolderRoot = "useCases/fabiland/scenario";

    private final static int sideLengthHorizonal = 1000;
    private final static int sideLengthVertical = 1000;
    private final static int centroidOffset = 5000;

    // Non-adjusted population
//    private final static int[][] householdsByZone = {
//            {300, 10, 10, 10, 10},
//            {10, 10, 300, 10, 10},
//            {10, 300, 1020, 10, 300},
//            {10, 10, 10, 10, 10},
//            {10, 10, 300, 10, 300}
//    };
//
//    private final static int[][] currentJobsByZone = {
//            {300, 10, 10, 10, 300},
//            {10, 10, 10, 10, 10},
//            {10, 300, 1020, 10, 10},
//            {10, 10, 300, 10, 10},
//            {300, 10, 10, 10, 300}
//    };
//
//    private final static int[][] jobsByZoneTotal = {
//            {300, 10, 10, 10, 300},
//            {10, 10, 10, 10, 10},
//            {10, 300, 1020, 10, 10},
//            {10, 10, 300, 10, 10},
//            {300, 10, 10, 10, 300}
//    };
    // End non-adjusted population

    // Doubled population
//    private final static int[][] householdsByZone = {
//            {600, 20, 20, 20, 20},
//            {20, 20, 600, 20, 20},
//            {20, 600, 2040, 20, 600},
//            {20, 20, 20, 20, 20},
//            {20, 20, 600, 20, 600}
//    };
//
//    private final static int[][] currentJobsByZone = {
//            {600, 20, 20, 20, 600},
//            {20, 20, 20, 20, 20},
//            {20, 600, 2040, 20, 20},
//            {20, 20, 600, 20, 20},
//            {600, 20, 20, 20, 600}
//    };
//
//    private final static int[][] jobsByZoneTotal = {
//            {600, 20, 20, 20, 600},
//            {20, 20, 20, 20, 20},
//            {20, 600, 2040, 20, 20},
//            {20, 20, 600, 20, 20},
//            {600, 20, 20, 20, 600}
//    };
    // End doubled population

    // Quadrupled population
//    private final static int[][] householdsByZone = {
//            {1200, 40, 40, 40, 40},
//            {40, 40, 1200, 40, 40},
//            {40, 1200, 4080, 40, 1200},
//            {40, 40, 40, 40, 40},
//            {40, 40, 1200, 40, 1200}
//    };
//
//    private final static int[][] currentJobsByZone = {
//            {1200, 40, 40, 40, 1200},
//            {40, 40, 40, 40, 40},
//            {40, 1200, 4080, 40, 40},
//            {40, 40, 1200, 40, 40},
//            {1200, 40, 40, 40, 1200}
//    };
//
//    private final static int[][] jobsByZoneTotal = {
//            {1200, 40, 40, 40, 1200},
//            {40, 40, 40, 40, 40},
//            {40, 1200, 4080, 40, 40},
//            {40, 40, 1200, 40, 40},
//            {1200, 40, 40, 40, 1200}
//    };
    // End Quadrupled population

    // Octupled population
    private final static int[][] householdsByZone = {
            {2400, 80, 80, 80, 80},
            {80, 80, 2400, 80, 80},
            {80, 2400, 8160, 80, 2400},
            {80, 80, 80, 80, 80},
            {80, 80, 2400, 80, 2400}
    };

    private final static int[][] currentJobsByZone = {
            {2400, 80, 80, 80, 2400},
            {80, 80, 80, 80, 80},
            {80, 2400, 8160, 80, 80},
            {80, 80, 2400, 80, 80},
            {2400, 80, 80, 80, 2400}
    };

    private final static int[][] jobsByZoneTotal = {
            {2400, 80, 80, 80, 2400},
            {80, 80, 80, 80, 80},
            {80, 2400, 8160, 80, 80},
            {80, 80, 2400, 80, 80},
            {2400, 80, 80, 80, 2400}
    };
    // End octupled population

    public static void main(String[] args) {

        Properties.initializeProperties(scenarioFolderRoot + "/base.properties");
        //Properties.initializeProperties(scenarioFolderRoot + "/unrestrictedDev.properties");
        DwellingFactory ddFactory = new DwellingFactoryImpl();
        HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        PersonFactory ppFactory = new PersonFactoryImpl();
        JobFactory jjFactory = new JobFactoryImpl();

        DwellingData dwellingData = new DwellingDataImpl();
        HouseholdData householdData = new HouseholdDataImpl();
        JobData jobData = new JobDataImpl();

        int hhId = 1;
        int jjId = 1;
        int ppId = 1;
        int ddId = 1;

        Counter counter = new Counter("Zone");
        for (int zoneId = 1; zoneId <= 25; zoneId++) {


            int row = (zoneId - 1) / 5;
            int col = (zoneId - 1) % 5;

            double x1 = col * centroidOffset - 12500;
            double y1 = 20000 - 12500 - row * centroidOffset;

            Random rnd = new Random(4711);
            int population = householdsByZone[row][col];

            Counter counter1 = new Counter("Household ");
            for (int i = 0; i < population; i++) {
                double x = x1 + ((centroidOffset - sideLengthHorizonal) / 2.) + rnd.nextInt(sideLengthHorizonal);
                double y = y1 + ((centroidOffset - sideLengthVertical) / 2.) + rnd.nextInt(sideLengthVertical);
                DwellingType type = SandboxDwellingTypes.SandboxDwellingType.SF;
                int price = 1000;
                if (i >= population / 2) {
                    type = SandboxDwellingTypes.SandboxDwellingType.MF;
                    price = 500;
                }
                Dwelling dwelling = ddFactory.createDwelling(ddId, zoneId, new Coordinate(x, y), hhId, type, 2, 4, price, 0);
                Household household = hhFactory.createHousehold(hhId, ddId, rnd.nextInt(3));
                if (rnd.nextDouble() <= 1. / 3.) {
                    int age1 = rnd.nextInt(60) + 20;
                    int age2 = age1 - rnd.nextInt(6);
                    //no negative age, maximum 18, try to have at least 18 years less than mom
                    int age3 = Math.min(18, rnd.nextInt(Math.max(1, age2 - 18)));

                    Person p1 = ppFactory.createPerson(ppId, age1, Gender.MALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 500);
                    p1.setHousehold(household);
                    ppId++;
                    Person p2 = ppFactory.createPerson(ppId, age2, Gender.FEMALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 500);
                    p2.setHousehold(household);
                    ppId++;
                    Person p3 = ppFactory.createPerson(ppId, age3, Gender.valueOf(rnd.nextInt(2) + 1), Occupation.STUDENT, PersonRole.CHILD, -1, 0);
                    p3.setHousehold(household);
                    ppId++;



                    if (rnd.nextDouble() < 2./3.) {
                        Job job1 = getJob(jjFactory, jjId, p1.getId());
                        if(job1 != null) {
                            p1.setOccupation(Occupation.EMPLOYED);
                            p1.setIncome(12*2000);
                            p1.setDriverLicense(true);
                            job1.setWorkerID(p1.getId());
                            p1.setWorkplace(job1.getId());
                            jobData.addJob(job1);
                            jjId++;
                        }
                    }
                    if (rnd.nextDouble() < 0.5) {
                        Job job2 = getJob(jjFactory, jjId, p2.getId());

                        if(job2 != null) {
                            p2.setOccupation(Occupation.EMPLOYED);
                            p2.setIncome(12*2000);
                            job2.setWorkerID(p2.getId());
                            p2.setDriverLicense(true);
                            p2.setWorkplace(job2.getId());
                            jobData.addJob(job2);
                            jjId++;
                        }
                    }

                    household.addPerson(p1);
                    household.addPerson(p2);
                    household.addPerson(p3);
                    p1.setHousehold(household);
                    p2.setHousehold(household);
                    p3.setHousehold(household);
                    householdData.addPerson(p1);
                    householdData.addPerson(p2);
                    householdData.addPerson(p3);

                } else if (rnd.nextDouble() <= 1. / 3.) {
                    int age1 = rnd.nextInt(60) + 20;
                    int age2 = age1 - rnd.nextInt(6);

                    Person p1 = ppFactory.createPerson(ppId, age1, Gender.MALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 500);
                    ppId++;
                    p1.setHousehold(household);
                    Person p2 = ppFactory.createPerson(ppId, age2, Gender.FEMALE, Occupation.UNEMPLOYED, PersonRole.MARRIED, -1, 500);
                    ppId++;
                    p2.setHousehold(household);

                    if (rnd.nextDouble() < 2./3.) {
                        Job job1 = getJob(jjFactory, jjId, p1.getId());
                        if(job1 != null) {
                            p1.setOccupation(Occupation.EMPLOYED);
                            job1.setWorkerID(p1.getId());
                            p1.setWorkplace(job1.getId());
                            p1.setDriverLicense(true);
                            jobData.addJob(job1);
                            p1.setIncome(12*2000);
                            jjId++;
                        }
                    }
                    if (rnd.nextDouble() < 0.5) {
                        Job job2 = getJob(jjFactory, jjId, p2.getId());
                        if(job2 != null) {
                            p2.setOccupation(Occupation.EMPLOYED);
                            job2.setWorkerID(p2.getId());
                            p2.setWorkplace(job2.getId());
                            p2.setDriverLicense(true);
                            jjId++;
                            jobData.addJob(job2);
                            p2.setIncome(12*2000);
                        }
                    }

                    household.addPerson(p1);
                    household.addPerson(p2);
                    p1.setHousehold(household);
                    p2.setHousehold(household);
                    householdData.addPerson(p1);
                    householdData.addPerson(p2);
                } else {
                    int age1 = rnd.nextInt(60) + 20;

                    Person p1 = ppFactory.createPerson(ppId, age1, Gender.valueOf(rnd.nextInt(2) + 1), Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 500);
                    ppId++;
                    p1.setHousehold(household);

                    if (rnd.nextDouble() < 2./3.) {
                        Job job1 = getJob(jjFactory, jjId, p1.getId());
                        if(job1 != null) {
                            p1.setOccupation(Occupation.EMPLOYED);
                            job1.setWorkerID(p1.getId());
                            p1.setWorkplace(job1.getId());
                            p1.setDriverLicense(true);
                            jjId++;
                            jobData.addJob(job1);
                            p1.setIncome(12*2000);
                        }
                    }

                    household.addPerson(p1);
                    p1.setHousehold(household);
                    householdData.addPerson(p1);
                }
                household.setDwelling(dwelling.getId());
                dwelling.setResidentID(household.getId());

                hhId++;
                ddId++;

                dwellingData.addDwelling(dwelling);
                householdData.addHousehold(household);
                counter1.incCounter();
            }
            counter.incCounter();
        }


        logger.warn("Adding vacant jobs and dwellings");
        for (int zoneId = 1; zoneId <= 25; zoneId++) {


            int row = (zoneId - 1) / 5;
            int col = (zoneId - 1) % 5;

            double x1 = col * centroidOffset - 12500;
            double y1 = 20000 - 12500 - row * centroidOffset;

            Random rnd = new Random(4712);
            //int vacantDd = 100;
            int vacantDd = 200;

            // 9-region case (in order to distributed dewllings evently over regions)
            // int vacantDd = 0;
//            List<Integer> zonesInOneZoneRegions = Arrays.asList(13);
//            List<Integer> zonesInTwoZoneRegions = Arrays.asList(3,8,11,12,14,15,18,23);
//            List<Integer> zonesInFourZoneRegions = Arrays.asList(1,2,4,5,6,7,9,10,16,17,19,20,21,22,24,25);
//            if (zonesInOneZoneRegions.contains(zoneId)) {
//                vacantDd = 400;
//            } else if (zonesInTwoZoneRegions.contains(zoneId)) {
//                vacantDd = 200;
//            } else if (zonesInFourZoneRegions.contains(zoneId)) {
//                vacantDd = 100;
//            } else {
//                throw new RuntimeException("Zone must have been contained in one of the prespecified lists.");
//            }
            // End 9-region case

            int vacantJj = (int) (0.05 * jobsByZoneTotal[row][col]);

            for (int i = 0; i < vacantDd; i++) {
                double x = x1 + ((centroidOffset - sideLengthHorizonal) / 2.) + rnd.nextInt(sideLengthHorizonal);
                double y = y1 + ((centroidOffset - sideLengthVertical) / 2.) + rnd.nextInt(sideLengthVertical);
                DwellingType type = SandboxDwellingTypes.SandboxDwellingType.MF;
                int price = 500;
                if (rnd.nextDouble() < 0.5) {
                    type = SandboxDwellingTypes.SandboxDwellingType.SF;
                    price = 1000;
                }
                Dwelling dwelling = ddFactory.createDwelling(ddId, zoneId, new Coordinate(x, y), -1, type, 2, 4, price, 0);
                dwellingData.addDwelling(dwelling);
                ddId++;

            }

            for (int i = 0; i < vacantJj; i++) {
                double x = x1 + ((centroidOffset - sideLengthHorizonal) / 2.) + rnd.nextInt(sideLengthHorizonal);
                double y = y1 + ((centroidOffset - sideLengthVertical) / 2.) + rnd.nextInt(sideLengthVertical);
                Job job = jjFactory.createJob(jjId, zoneId, new Coordinate(x, y), -1, "IND");
                jobData.addJob(job);
                jjId++;

            }
        }

//        new DefaultDwellingWriter(dwellingData.getDwellings()).writeDwellings(scenarioFolderRoot + "/microData/dd_0.csv");
//        new DefaultHouseholdWriter(householdData.getHouseholds()).writeHouseholds(scenarioFolderRoot + "/microData/hh_0.csv");
//        new DefaultPersonWriter(householdData).writePersons(scenarioFolderRoot + "/microData/pp_0.csv");
//        new DefaultJobWriter(jobData.getJobs()).writeJobs(scenarioFolderRoot + "/microData/jj_0.csv");
        new DefaultDwellingWriter(dwellingData.getDwellings()).writeDwellings(scenarioFolderRoot + "/microData/dd_8x_0.csv");
        new DefaultHouseholdWriter(householdData.getHouseholds()).writeHouseholds(scenarioFolderRoot + "/microData/hh_8x_0.csv");
        new DefaultPersonWriter(householdData).writePersons(scenarioFolderRoot + "/microData/pp_8x_0.csv");
        new DefaultJobWriter(jobData.getJobs()).writeJobs(scenarioFolderRoot + "/microData/jj_8x_0.csv");
    }

    public static Job getJob(JobFactory jjFactory, int jjId, int ppId) {
        Random rnd = new Random(jjId);
        Sampler<Integer> sampler = new Sampler<>(25, Integer.class);
        for (int i = 1; i <= 25; i++) {
            int row = (i - 1) / 5;
            int col = (i - 1) % 5;

            sampler.incrementalAdd(i, currentJobsByZone[row][col]);
        }
        int zoneId;
        try {
            zoneId = sampler.sampleObject();
            int row = (zoneId - 1) / 5;
            int col = (zoneId - 1) % 5;

            double x1 = col * centroidOffset - 12500;
            double y1 = 20000 - 12500 - row * centroidOffset;

            double x = x1 + ((centroidOffset - sideLengthHorizonal) / 2.) + rnd.nextInt(sideLengthHorizonal);
            double y = y1 + ((centroidOffset - sideLengthVertical) / 2.) + rnd.nextInt(sideLengthVertical);

            currentJobsByZone[row][col] = Math.max(0, currentJobsByZone[row][col]-1);

            return jjFactory.createJob(jjId, zoneId, new Coordinate(x, y), ppId, "IND");

        } catch (SampleException e) {
            logger.warn(e);
            return null;
        }
    }
}