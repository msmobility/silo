package de.tum.bgu.msm.io.output;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.math.Quantiles;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.events.MicroEvent;
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

public class DefaultResultsMonitor implements ResultsMonitor {

    private final Logger logger = Logger.getLogger(DefaultResultsMonitor.class);
    private PrintWriter resultWriter;
    private DataContainer dataContainer;
    private Properties properties;

    public DefaultResultsMonitor(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }


    @Override
    public void setup() {

        String pathname = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/" + "resultFile.csv";
        try {
            resultWriter = new PrintWriter(new File(pathname));
        } catch (FileNotFoundException e) {
            logger.error("Cannot write the result file: " + pathname, e);
        }
    }


    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {
        resultWriter.println("Year " + year);
        summarizePopulationByAgeAndGender();
        summarizeHouseholdsByType();
        summarizeHouseholdsBySize();
        summarizeAverageHouseholdSize();
        summarizeHouseholdIncome();
        summarizeLabourParticipationRates();
        summarizeAverageCommutingDistanceByRegion();
        summarizeCarOwnership();
        summarizeDwellings();
        summarizeAvailableLandByRegion();
        summarizeHousingCostsByIncomeGroup();
        summarizeJobsByRegionAndType();
        summarizeEventCounts(eventCounter);
        resultWriter.flush();
    }


    private void summarizePopulationByAgeAndGender() {
        int pers[][] = new int[2][101];
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            Gender gender = per.getGender();
            int age = Math.min(per.getAge(), 100);
            pers[gender.ordinal()][age] += 1;
        }
        resultWriter.println("Age,Men,Women");
        for (int i = 0; i <= 100; i++) {
            String row = i + "," + pers[0][i] + "," + pers[1][i];
            resultWriter.println(row);
        }

    }

//    private void summarizePopulationByRace() {
//        int ppRace[] = new int[4];
//        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
//            ppRace[per.getRace().ordinal()]++;
//        }
//        resultWriter.println("ppByRace,hh");
//        resultWriter.println("white," + ppRace[0]);
//        resultWriter.println("black," + ppRace[1]);
//        resultWriter.println("hispanic," + ppRace[2]);
//        resultWriter.println("other," + ppRace[3]);
//
//    }

    private void summarizeHouseholdsByType() {
        int hht[] = new int[HouseholdType.values().length + 1];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            hht[hh.getHouseholdType().ordinal()]++;
        }
        resultWriter.println("hhByType,hh");
        for (HouseholdType ht : HouseholdType.values()) {
            String row = ht + "," + hht[ht.ordinal()];
            resultWriter.println(row);
        }
    }

//    private void summarizeHouseholdsByRace() {
//        int hhRace[] = new int[4];
//        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
//            hhRace[hh.getRace().ordinal()]++;
//        }
//        resultWriter.println("hhByRace,hh");
//        resultWriter.println("white," + hhRace[0]);
//        resultWriter.println("black," + hhRace[1]);
//        resultWriter.println("hispanic," + hhRace[2]);
//        resultWriter.println("other," + hhRace[3]);
//    }

    private void summarizeHouseholdsBySize() {
        int hhs[] = new int[10];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
        }

        String row = "hhBySize";
        for (int i : hhs) row = row + "," + i;
        resultWriter.println(row);
    }

    private void summarizeAverageHouseholdSize() {
        String row = "AveHHSize," + dataContainer.getHouseholdDataManager().getHouseholds().stream().mapToInt(h -> h.getPersons().size()).average().getAsDouble();
        resultWriter.println(row);
    }

    private void summarizeHouseholdIncome() {
        List<Integer> incomes = dataContainer.getHouseholdDataManager().getHouseholds().stream().map(h -> h.getPersons().values().stream().mapToInt(p -> p.getAnnualIncome()).sum()).collect(Collectors.toList());
        double aveHHincome = incomes.stream().mapToDouble(i -> i).average().getAsDouble();
        double medianHhIncome = Quantiles.median().compute(incomes);
        String row = "AveHHInc," + aveHHincome + ",MedianHHInc," + medianHhIncome;
        resultWriter.println(row);
    }

    private void summarizeLabourParticipationRates() {
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
        String[] grp = {"<18", "18-29", "30-49", "50-64", ">=65"};
        resultWriter.println("laborParticipationRateByAge,male,female");
        for (int ag = 0; ag < 5; ag++) {
            Formatter f = new Formatter();
            f.format("%s,%f,%f", grp[ag], labP[1][0][ag] / (labP[0][0][ag] + labP[1][0][ag]), labP[1][1][ag] / (labP[0][1][ag] + labP[1][1][ag]));
            resultWriter.println(f.toString());
        }
    }

    private void summarizeAverageCommutingDistanceByRegion() {
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
                try {
                    Zone destination = dataContainer.getGeoData().getZones().get(dataContainer.getJobDataManager().getJobFromId(per.getJobId()).getZoneId());
                    double ds = dataContainer.getTravelTimes().getPeakSkim(TransportMode.car).getIndexed(zone.getZoneId(), destination.getZoneId());
                    commDist[0][zone.getRegion().getId()] += ds;
                    commDist[1][zone.getRegion().getId()]++;
                } catch (NullPointerException e){
                    logger.warn("Error found since hh does not have a dd? hh: " + household.getId());
                }
            }
        }
        resultWriter.println("aveCommuteDistByRegion,minutes");
        for (int i : dataContainer.getGeoData().getRegions().keySet()) {
            resultWriter.println(i + "," + commDist[0][i] / commDist[1][i]);
        }
    }

    private void summarizeCarOwnership() {
        int[] carOwnership = new int[4];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            carOwnership[(int) hh.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count()]++;
        }
        resultWriter.println("carOwnershipLevel,households");
        resultWriter.println("0cars," + carOwnership[0]);
        resultWriter.println("1car," + carOwnership[1]);
        resultWriter.println("2cars," + carOwnership[2]);
        resultWriter.println("3+cars," + carOwnership[3]);
    }


    private void summarizeDwellings() {

        resultWriter.println("QualityLevel,Dwellings");
        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++) {
            String row = qual + "," + dataContainer.getRealEstateDataManager().getUpdatedQualityShares().getOrDefault(qual, 0.);
            resultWriter.println(row);
        }


        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();

        Multiset<DwellingType> countsByDwellingType = HashMultiset.create(dwellingTypes.size());

        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            countsByDwellingType.add(dd.getType());
        }
        for (DwellingType dt : dwellingTypes) {
            resultWriter.println("CountOfDD," + dt.toString() + "," + countsByDwellingType.count(dt));
        }
        for (DwellingType dt : dwellingTypes) {
            double avePrice = dataContainer.getRealEstateDataManager().getAveragePriceByDwellingType()[dwellingTypes.indexOf(dt)];
            resultWriter.println("AveMonthlyPrice," + dt.toString() + "," + avePrice);
        }
        for (DwellingType dt : dwellingTypes) {
            double aveVac = dataContainer.getRealEstateDataManager().getAverageVacancyByDwellingType()[dwellingTypes.indexOf(dt)];
            Formatter f = new Formatter();
            f.format("AveVacancy,%s,%f", dt.toString(), aveVac);
            resultWriter.println(f.toString());
        }

    }

    private void summarizeAvailableLandByRegion() {
        resultWriter.println("Available land for construction by region");
        GeoData geoData = dataContainer.getGeoData();
        final int highestId = geoData.getRegions().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        double[] availLand = new double[highestId + 1];
        for (int zone : geoData.getZones().keySet()) {
            availLand[geoData.getZones().get(zone).getRegion().getId()] +=
                    dataContainer.getRealEstateDataManager().getAvailableCapacityForConstruction(zone);
        }
        for (int region : geoData.getRegions().keySet()) {
            Formatter f = new Formatter();
            f.format("%d,%f", region, availLand[region]);
            resultWriter.println(f.toString());
        }

    }

    private void summarizeHousingCostsByIncomeGroup() {
        resultWriter.println("Housing costs by income group");
        String header = "Income";
        for (int i = 0; i < 10; i++) {
            header = header.concat(",rent_" + ((i + 1) * 250));
        }
        header = header.concat(",averageRent");
        resultWriter.println(header);
        int[][] rentByIncome = new int[10][10];
        long [] rents = new long[10];
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            try {
                int hhInc = HouseholdUtil.getAnnualHhIncome(hh);
                int rent = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getPrice();
                int incCat = Math.min((hhInc / 10000), 9);
                int rentCat = Math.min((rent / 250), 9);
                rentByIncome[incCat][rentCat]++;
                rents[incCat] += rent;
            } catch (NullPointerException e){
                logger.warn("A household has a null dwelling");
            }
        }
        for (int i = 0; i < 10; i++) {
            String line = String.valueOf((i + 1) * 10000);
            int countThisIncome = 0;
            for (int r = 0; r < 10; r++) {
                line = line.concat("," + rentByIncome[i][r]);
                countThisIncome += rentByIncome[i][r];
            }
            if (countThisIncome != 0) { // new dz, avoid dividing by zero
                // TODO check what happens by leaving this out... the error is avoided
                line = line.concat("," + rents[i] / countThisIncome);
            }
            resultWriter.println(line);
        }
    }

    private void summarizeJobsByRegionAndType() {
        String txt = "jobByRegion";
        for (String empType : JobType.getJobTypes()) {
            txt += "," + empType;
        }
        resultWriter.println(txt + ",total");
        Map<Integer, Region> regions = dataContainer.getGeoData().getRegions();
        final int highestId = regions.keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] jobsByTypeAndRegion = new int[JobType.getNumberOfJobTypes()][highestId + 1];
        for (Job job : dataContainer.getJobDataManager().getJobs()) {
            jobsByTypeAndRegion[JobType.getOrdinal(job.getType())][dataContainer.getGeoData().getZones().get(job.getZoneId()).getRegion().getId()]++;
        }

        for (int region : regions.keySet()) {
            StringBuilder line = new StringBuilder(String.valueOf(region));
            int regionSum = 0;
            for (String empType : JobType.getJobTypes()) {
                line.append(",").append(jobsByTypeAndRegion[JobType.getOrdinal(empType)][region]);
                regionSum += jobsByTypeAndRegion[JobType.getOrdinal(empType)][region];
            }
            resultWriter.println(line + "," + regionSum);
        }
    }


    private void summarizeEventCounts(Multiset<Class<? extends MicroEvent>> eventCounter) {
        resultWriter.println("Count of simulated events");
        logger.info("Simulated " + eventCounter.size() + " successful events in total.");
        for (Class<? extends MicroEvent> event : eventCounter.elementSet()) {
            final int count = eventCounter.count(event);
            resultWriter.println(event.getSimpleName() + "," + count);
            logger.info("Simulated " + event.getSimpleName() + ": " + count);
        }
    }


    @Override
    public void endSimulation() {
        resultWriter.close();
    }

}
