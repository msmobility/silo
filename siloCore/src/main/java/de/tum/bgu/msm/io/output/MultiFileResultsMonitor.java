package de.tum.bgu.msm.io.output;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.math.Quantiles;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiFileResultsMonitor implements ResultsMonitor {

    private final Logger logger = Logger.getLogger(MultiFileResultsMonitor.class);
    private PrintWriter popYearW;
    private PrintWriter hhTypeW;
    private PrintWriter hhSizeW;
    private PrintWriter hhAveSizeW;
    private PrintWriter hhAveIncomeW;
    private PrintWriter labourParticipationRateW;
    private DataContainer dataContainer;
    private Properties properties;
    private PrintWriter commutingTimeW;
    private PrintWriter carOwnW;
    private PrintWriter ddCountW;
    private PrintWriter landRegionW;
    private PrintWriter eventCountW;
    private PrintWriter ddQualW;
    private PrintWriter migrantsW;
    private PrintWriter vacantJobsRegionW;

    public MultiFileResultsMonitor(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }


    @Override
    public void setup() {

        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/siloResults/";
        try {
            File file = new File(pathname + "popYear.csv");
            //
            // file.getParentFile().mkdirs();
            popYearW = new PrintWriter(file);
            hhTypeW = new PrintWriter(new File(pathname + "hhType.csv"));
            hhSizeW = new PrintWriter(new File(pathname + "hhSize.csv"));
            hhAveSizeW = new PrintWriter(new File(pathname + "aveHhSize.csv"));
            hhAveIncomeW = new PrintWriter(new File(pathname + "hhAveIncome.csv"));
            labourParticipationRateW = new PrintWriter(new File(pathname + "labourParticipationRate.csv"));
            commutingTimeW = new PrintWriter(new File(pathname + "regionAvCommutingTime.csv"));
            carOwnW = new PrintWriter(new File(pathname + "carOwnership.csv"));
            ddQualW = new PrintWriter(new File(pathname + "dwellingQualityLevel.csv"));
            ddCountW = new PrintWriter(new File(pathname + "dwellings.csv"));
            landRegionW = new PrintWriter(new File(pathname + "regionAvailableLand.csv"));
            eventCountW = new PrintWriter(new File(pathname + "eventCounts.csv"));
            migrantsW = new PrintWriter(new File(pathname + "persMigrants.csv"));
            vacantJobsRegionW = new PrintWriter(new File(pathname + "vacantJobsRegion.csv"));
        } catch (FileNotFoundException e) {
            logger.error("Cannot write the result file: " + pathname, e);
        }
    }


    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {
        summarizePopulationByAgeAndGender(year);
        summarizeHouseholdsByType(year);
        summarizeHouseholdsBySize(year);
        summarizeAverageHouseholdSize(year);
        summarizeHouseholdIncome(year);
        summarizeLabourParticipationRates(year);
        summarizeAverageCommutingDistanceByRegion(year);
        summarizeCarOwnership(year);
        summarizeDwellingsByQuality(year);
        summarizeDwellings(year);
        summarizeAvailableLandByRegion(year);
        //summarizeHousingCostsByIncomeGroup(year);
        //summarizeJobsByRegionAndType(year);
        summarizeEventCounts(eventCounter, year);
        summarizeMigration(year, events);
        summarizeVacantJobsByRegion(year);

        popYearW.flush();
        hhTypeW.flush();
        hhSizeW.flush();
        hhAveSizeW.flush();
        hhAveIncomeW.flush();
        labourParticipationRateW.flush();
        commutingTimeW.flush();
        carOwnW.flush();
        ddCountW.flush();
        landRegionW.flush();
        eventCountW.flush();
        migrantsW.flush();
        vacantJobsRegionW.flush();
    }

    private void summarizeVacantJobsByRegion(int year) {
        if (year == properties.main.baseYear) {
            vacantJobsRegionW.println("year,region,vacants");
        }
        Map<Integer, List<Job>> vacantJobsByRegion = dataContainer.getJobDataManager().getVacantJobsByRegion();
        for (int region : vacantJobsByRegion.keySet()) {
            vacantJobsRegionW.println(year + "," + region + "," + vacantJobsByRegion.get(region).size());
        }

    }


    private void summarizePopulationByAgeAndGender(int year) {
        int pers[][] = new int[2][101];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            Gender gender = per.getGender();
            int age = Math.min(per.getAge(), 100);
            pers[gender.ordinal()][age] += 1;
        }
        if (year == properties.main.baseYear) {
            popYearW.println("year,age,men,women");
        }
        for (int i = 0; i <= 100; i++) {
            String row = year + "," + i + "," + pers[0][i] + "," + pers[1][i];
            popYearW.println(row);
        }

    }

//    private void summarizePopulationByRace(int year) {
//        int ppRace[] = new int[];
//        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
//            ppRace[per.getRace().ordinal()]++;
//        }
//        popYearW.println("ppByRace,hh");
//        popYearW.println("white," + ppRace[0]);
//        popYearW.println("black," + ppRace[1]);
//        popYearW.println("hispanic," + ppRace[2]);
//        popYearW.println("other," + ppRace[3]);
//
//    }

    private void summarizeHouseholdsByType(int year) {
        int hht[] = new int[HouseholdType.values().length + 1];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            hht[hh.getHouseholdType().ordinal()]++;
        }
        if (year == properties.main.baseYear) {
            hhTypeW.println("year,type,count");
        }

        for (HouseholdType ht : HouseholdType.values()) {
            String row = year + "," + ht + "," + hht[ht.ordinal()];
            hhTypeW.println(row);
        }
    }

//    private void summarizeHouseholdsByRace() {
//        int hhRace[] = new int[4];
//        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
//            hhRace[hh.getRace().ordinal()]++;
//        }
//        popYearW.println("hhByRace,hh");
//        popYearW.println("white," + hhRace[0]);
//        popYearW.println("black," + hhRace[1]);
//        popYearW.println("hispanic," + hhRace[2]);
//        popYearW.println("other," + hhRace[3]);
//    }

    private void summarizeHouseholdsBySize(int year) {
        int hhs[] = new int[10];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
        }

        if (year == properties.main.baseYear) {
            hhSizeW.println("year,size,count");
        }

        for (int i = 0; i < hhs.length; i++) {
            String row = String.valueOf(year);
            int thisHhSize = i + 1;
            row = row + "," + thisHhSize;
            row = row + "," + hhs[i];
            hhSizeW.println(row);
        }

    }

    private void summarizeAverageHouseholdSize(int year) {

        if (year == properties.main.baseYear) {
            hhAveSizeW.println("year,size");
        }
        String row = year + "," + dataContainer.getHouseholdDataManager().getHouseholds().stream().mapToInt(h -> h.getPersons().size()).average().getAsDouble();
        hhAveSizeW.println(row);
    }

    private void summarizeHouseholdIncome(int year) {
        List<Integer> incomes = dataContainer.getHouseholdDataManager().getHouseholds().stream().map(h -> h.getPersons().values().stream().mapToInt(p -> p.getAnnualIncome()).sum()).collect(Collectors.toList());
        double aveHHincome = incomes.stream().mapToDouble(i -> i).average().getAsDouble();
        double medianHhIncome = Quantiles.median().compute(incomes);
        if (year == properties.main.baseYear) {
            hhAveIncomeW.println("year,variable,value");
        }

        String row = year + ",average," + aveHHincome;
        String nextRow = year + ",median," + medianHhIncome;

        hhAveIncomeW.println(row);
        hhAveIncomeW.println(nextRow);
    }

    private void summarizeLabourParticipationRates(int year) {
        float[][][] labP = new float[2][2][5];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            int age = per.getAge();
            Gender gender = per.getGender();
            boolean employed = per.getJobId() > 0;
            int ageGroup = 0;
            if (age >= 65) {
                ageGroup = 4;
            } else if (age >= 50) {
                ageGroup = 3;
            } else if (age >= 30) {
                ageGroup = 2;
            } else if (age >= 18) {
                ageGroup = 1;
            }

            if (employed) {
                labP[1][gender.ordinal()][ageGroup]++;
            } else {
                labP[0][gender.ordinal()][ageGroup]++;
            }

        }
        if (year == properties.main.baseYear) {
            labourParticipationRateW.println("year,group,male,female");
        }
        String[] grp = {"<18", "18-29", "30-49", "50-64", ">=65"};

        for (int ag = 0; ag < 5; ag++) {
            Formatter f = new Formatter();
            float rateMale = labP[1][0][ag] / (labP[0][0][ag] + labP[1][0][ag]);
            float rateFemale = labP[1][1][ag] / (labP[0][1][ag] + labP[1][1][ag]);
            //f.format("%s,%f,%f", grp[ag], rateMale, rateFemale);
            labourParticipationRateW.println(year + "," + grp[ag] + "," + rateMale + "," + rateFemale);
        }
    }

    private void summarizeAverageCommutingDistanceByRegion(int year) {
        float[][] commDist = new float[2][dataContainer.getGeoData().getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt() + 1];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            boolean employed = per.getJobId() > 0;
            if (employed) {
                Zone zone = null;
                Household household = dataContainer.getHouseholdDataManager().getHouseholdFromId(per.getHousehold().getId());
                Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
                if (dwelling != null) {
                    zone = dataContainer.getGeoData().getZones().get(dwelling.getZoneId());
                }
                Zone destination = dataContainer.getGeoData().getZones().get(dataContainer.getJobDataManager().getJobFromId(per.getJobId()).getZoneId());
                double ds = dataContainer.getTravelTimes().getPeakSkim(TransportMode.car).getIndexed(zone.getZoneId(), destination.getZoneId());
                commDist[0][zone.getRegion().getId()] += ds;
                commDist[1][zone.getRegion().getId()]++;
            }
        }

        if (year == properties.main.baseYear) {
            commutingTimeW.println("year,aveCommuteDistByRegion,minutes");
        }

        for (int i : dataContainer.getGeoData().getRegions().keySet()) {
            commutingTimeW.println(year + "," + i + "," + commDist[0][i] / commDist[1][i]);
        }
    }

    private void summarizeCarOwnership(int year) {
        int[] carOwnership = new int[4];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            carOwnership[Math.min(3,(int) hh.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count())]++;
        }

        if (year == properties.main.baseYear) {
            carOwnW.println("year,carOwnershipLevel,households");
        }


        carOwnW.println(year + ",0," + carOwnership[0]);
        carOwnW.println(year + ",1," + carOwnership[1]);
        carOwnW.println(year + ",2," + carOwnership[2]);
        carOwnW.println(year + ",3," + carOwnership[3]);
    }

    private void summarizeDwellingsByQuality(int year) {

        if (year == properties.main.baseYear) {
            ddQualW.println("year,QualityLevel,Dwellings");
        }

        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++) {
            String row = year + "," + qual + "," + dataContainer.getRealEstateDataManager().getUpdatedQualityShares().getOrDefault(qual, 0.);
            ddQualW.println(row);
        }
    }


    private void summarizeDwellings(int year) {

        if (year == properties.main.baseYear) {
            ddCountW.println("year,type,count,price,vacancy");
        }

        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();

        Multiset<DwellingType> countsByDwellingType = HashMultiset.create(dwellingTypes.size());

        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            countsByDwellingType.add(dd.getType());
        }
        for (DwellingType dt : dwellingTypes) {
            double aveVac = dataContainer.getRealEstateDataManager().getAverageVacancyByDwellingType()[dwellingTypes.indexOf(dt)];
            double avePrice = dataContainer.getRealEstateDataManager().getAveragePriceByDwellingType()[dwellingTypes.indexOf(dt)];
            ddCountW.println(year + "," + dt.toString() + "," + countsByDwellingType.count(dt) + "," + avePrice + "," + aveVac);
        }
    }

    private void summarizeAvailableLandByRegion(int year) {

        if (year == properties.main.baseYear) {
            landRegionW.println("year,region,land");
        }
        GeoData geoData = dataContainer.getGeoData();
        final int highestId = geoData.getRegions().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        double[] availLand = new double[highestId + 1];
        for (int zone : geoData.getZones().keySet()) {
            availLand[geoData.getZones().get(zone).getRegion().getId()] +=
                    dataContainer.getRealEstateDataManager().getAvailableCapacityForConstruction(zone);
        }
        for (int region : geoData.getRegions().keySet()) {
            //Formatter f = new Formatter();
            //f.format("%d,%f", region, availLand[region]);
            landRegionW.println(year + "," + region + "," + availLand[region]);
        }

    }

    //todo
//    private void summarizeHousingCostsByIncomeGroup(int year) {
//        popYearW.println("Housing costs by income group");
//        String header = "Income";
//        for (int i = 0; i < 10; i++) header = header.concat(",rent_" + ((i + 1) * 250));
//        header = header.concat(",averageRent");
//        popYearW.println(header);
//        int[][] rentByIncome = new int[10][10];
//        long[] rents = new long[10];
//        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
//            int hhInc = HouseholdUtil.getAnnualHhIncome(hh);
//            int rent = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getPrice();
//            int incCat = Math.min((hhInc / 10000), 9);
//            int rentCat = Math.min((rent / 250), 9);
//            rentByIncome[incCat][rentCat]++;
//            rents[incCat] += rent;
//        }
//        for (int i = 0; i < 10; i++) {
//            String line = String.valueOf((i + 1) * 10000);
//            int countThisIncome = 0;
//            for (int r = 0; r < 10; r++) {
//                line = line.concat("," + rentByIncome[i][r]);
//                countThisIncome += rentByIncome[i][r];
//            }
//            if (countThisIncome != 0) { // new dz, avoid dividing by zero
//                // TODO check what happens by leaving this out... the error is avoided
//                line = line.concat("," + rents[i] / countThisIncome);
//            }
//            popYearW.println(line);
//        }
//    }

//    private void summarizeJobsByRegionAndType(int year) {
//        String txt = "jobByRegion";
//        for (String empType : JobType.getJobTypes()) {
//            txt += "," + empType;
//        }
//        popYearW.println(txt + ",total");
//        Map<Integer, Region> regions = dataContainer.getGeoData().getRegions();
//        final int highestId = regions.keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
//        int[][] jobsByTypeAndRegion = new int[JobType.getNumberOfJobTypes()][highestId + 1];
//        for (Job job : dataContainer.getJobDataManager().getJobs()) {
//            jobsByTypeAndRegion[JobType.getOrdinal(job.getType())][dataContainer.getGeoData().getZones().get(job.getZoneId()).getRegion().getId()]++;
//        }
//
//        for (int region : regions.keySet()) {
//            StringBuilder line = new StringBuilder(String.valueOf(region));
//            int regionSum = 0;
//            for (String empType : JobType.getJobTypes()) {
//                line.append(",").append(jobsByTypeAndRegion[JobType.getOrdinal(empType)][region]);
//                regionSum += jobsByTypeAndRegion[JobType.getOrdinal(empType)][region];
//            }
//            popYearW.println(line + "," + regionSum);
//        }
//    }


    private void summarizeEventCounts(Multiset<Class<? extends MicroEvent>> eventCounter, int year) {
        if (year == properties.main.baseYear) {
            eventCountW.println("year,event,count");
        }

        logger.info("Simulated " + eventCounter.size() + " successful events in total.");
        for (Class<? extends MicroEvent> event : eventCounter.elementSet()) {
            final int count = eventCounter.count(event);
            eventCountW.println(year + "," + event.getSimpleName() + "," + count);
            logger.info("Simulated " + event.getSimpleName() + ": " + count);
        }
    }


    private void summarizeMigration(int year, List<MicroEvent> events) {

        if (year == properties.main.baseYear) {
            migrantsW.println("year,Key,Value");
        }

        int countInmigrants = 0;
        int countOutmigrants = 0;
        for (MicroEvent event : events) {
            if (event instanceof MigrationEvent) {
                MigrationEvent.Type type = ((MigrationEvent) event).getType();
                if (type.equals(MigrationEvent.Type.IN)) {
                    countInmigrants += ((MigrationEvent) event).getHousehold().getHhSize();
                } else {
                    countOutmigrants += ((MigrationEvent) event).getHousehold().getHhSize();
                }
            }
        }

        migrantsW.println(year + ",InmigrantsPP," + countInmigrants);
        migrantsW.println(year + ",OutmigrantsPP," + countOutmigrants);


    }


    @Override
    public void endSimulation() {
        popYearW.close();
        hhTypeW.close();
        hhSizeW.close();
        hhAveSizeW.close();
        hhAveIncomeW.close();
        labourParticipationRateW.close();
        commutingTimeW.close();
        carOwnW.close();
        ddQualW.close();
        ddCountW.close();
        landRegionW.close();
        eventCountW.close();
        migrantsW.close();
        vacantJobsRegionW.close();
    }

}
