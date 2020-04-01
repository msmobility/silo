package sdg;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUsage;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import sdg.data.AnalyzedPerson;
import sdg.data.DataContainerSdg;
import sdg.data.Trip;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;


public class SDGCalculator {

    private static Logger logger = Logger.getLogger(SDGCalculator.class);
    private Collection<Household> households = new ArrayList<>();
    private Collection<Dwelling> dwellings = new ArrayList<>();
    private Collection<Person> persons = new ArrayList<>();
    private RealEstateDataManager realEstateDataManager;
    private Map<Integer, List<Household>> hhByZone = new HashMap<>();
    private Map<Integer, List<Person>> ppByZone = new HashMap<>();
    private Map<Integer, List<Dwelling>> ddByZone = new HashMap<>();
    private Map<Integer, AnalyzedPerson> matsimPerson = new HashMap<>();
    private Map<Integer, List<Trip>> commutingTripsByZone = new HashMap<>();
    private Map<Integer, List<Trip>> schoolTripsByZone = new HashMap<>();
    private Map<Integer, Map<Mode, List<Trip>>> ttByModeByZone = new HashMap<>();

    public void setMatsimPerson(Map<Integer, AnalyzedPerson> matsimPerson) {
        this.matsimPerson = matsimPerson;
    }

    void calculateSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {
        loadDataSet(dataContainer);
        logger.info("Non Spatial Sdg Indicators");
        calculateNonSpatialSdgIndicators(dataContainer,outputPath,year);
        //logger.info("Spatial Sdg Indicators");
        //calculateSpatialSdgIndicators(dataContainer,outputPath,year);
    }

    private void loadDataSet(DataContainer dataContainer) {
        households = dataContainer.getHouseholdDataManager().getHouseholds();
        dwellings = dataContainer.getRealEstateDataManager().getDwellings();
        persons = dataContainer.getHouseholdDataManager().getPersons();
        realEstateDataManager = dataContainer.getRealEstateDataManager();
        hhByZone = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getHhZone(hh, dataContainer)));
        ppByZone = persons.stream().collect(Collectors.groupingBy(pp -> HouseholdUtil.getHhZone(pp.getHousehold(), dataContainer)));
        ddByZone = dwellings.stream().collect(Collectors.groupingBy(dd -> dd.getZoneId()));

    }

    private void calculateSpatialSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {
        //Spatial indicators
        String fileSpatialSDGIndicator = outputPath + "/spatialSDGIndicators"+"_"+year+".csv";
        PrintWriter spatialSDGIndicator = MitoUtil.openFileForSequentialWriting(fileSpatialSDGIndicator, false);

        //write header
        spatialSDGIndicator.println("year,zoneId,shareOfLowIncomeHH,avgRentIncomeRatio,avgFloorAreaPerPerson,shareOfAuto0,shareOfAuto1,shareOfAuto2,shareOfAuto3,shareOfddQuality1,shareOfddQuality2,shareOfddQuality3,vacantDwellingShare,populationDensity,avgCommutingTime,avgSchoolDistance,autoDriver,autoPassenger,bicycle,bus,train,tramOrMetro,walk");

        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            spatialSDGIndicator.print(year);
            spatialSDGIndicator.print(",");
            spatialSDGIndicator.print(zone.getZoneId());
            spatialSDGIndicator.print(",");
            if(hhByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().filter(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getAnnualHhIncome(hh)).equals(IncomeCategory.LOW)).count() / (double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getPrice() / (double)(HouseholdUtil.getAnnualHhIncome(hh) + 1)).average().getAsDouble());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / (double)hh.getHhSize()).average().getAsDouble());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 0).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 1).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 2).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() > 2).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(ddByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 1).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 2).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 3).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getUsage().equals(DwellingUsage.VACANT)).count()/(double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }
            spatialSDGIndicator.print(realEstateDataManager.getAvailableCapacityForConstruction(zone.getZoneId()));
            spatialSDGIndicator.print(",");
            if(ppByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(ppByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(commutingTripsByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(commutingTripsByZone.get(zone.getZoneId()).stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(schoolTripsByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(schoolTripsByZone.get(zone.getZoneId()).stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble());
            }else{
                spatialSDGIndicator.print(0);
            }

            if(ttByModeByZone.get(zone.getZoneId())!= null) {
                ttByModeByZone.get(zone.getZoneId()).entrySet().forEach(entry -> spatialSDGIndicator.print("," + entry.getValue().size()));
            }else{
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
            }
            spatialSDGIndicator.println();
        }

        spatialSDGIndicator.close();

    }

    private void calculateNonSpatialSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {

        String fileNonSpatialSDGIndicator = outputPath + "/nonSpatialSDGIndicators"+"_"+Properties.get().main.scenarioName+".csv";
        PrintWriter nonSpatialSDGIndicator = MitoUtil.openFileForSequentialWriting(fileNonSpatialSDGIndicator, true);

        logger.info("global indicators");
        Map<IncomeCategory, List<Household>> hhByIncomeCategory = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getAnnualHhIncome(hh))));
        hhByIncomeCategory.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",incomeCategory," + entry.getKey() + "," + entry.getValue().size()));
        hhByIncomeCategory.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",incomeCategory," + entry.getKey() + "," + entry.getValue().size()/(double)households.size()));

        Map<Integer, List<Dwelling>> ddByQuality = dwellings.stream().collect(Collectors.groupingBy(dd -> dd.getQuality()));
        ddByQuality.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",dwellingQuality," + entry.getKey() + "," + entry.getValue().size()));
        ddByQuality.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",dwellingQuality," + entry.getKey() + "," + entry.getValue().size()/(double)dwellings.size()));

        Map<Integer, List<Household>> hhByAuto = households.stream().collect(Collectors.groupingBy(hh -> hh.getAutos()));
        hhByAuto.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",carOwnership," + entry.getKey() + "," + entry.getValue().size()));
        hhByAuto.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",carOwnership," + entry.getKey() + "," + entry.getValue().size()/(double)households.size()));

        nonSpatialSDGIndicator.println(year + ",carOwnershipRate," + households.stream().mapToDouble(hh -> hh.getAutos()).average().getAsDouble());

        nonSpatialSDGIndicator.print(year + ",avgCommutingTime,");
        nonSpatialSDGIndicator.println(matsimPerson.values().stream().mapToDouble(mp->mp.getCongestedTime()).average().getAsDouble());

//        nonSpatialSDGIndicator.print(year + ",avgCoummutingDistance,");
//        nonSpatialSDGIndicator.println(persons.stream().filter(pp -> pp.getOccupation().equals(Occupation.EMPLOYED)).mapToDouble(pp->euclideanDistance(realEstateDataManager.getDwelling(pp.getHousehold().getDwellingId()).getCoordinate(),dataContainer.getJobDataManager().getJobFromId(pp.getJobId()).getCoordinate())).average().getAsDouble());

        nonSpatialSDGIndicator.println(year + ",rentIncomeRatio," + households.stream().filter(hh -> HouseholdUtil.getAnnualHhIncome(hh)!= 0).mapToDouble(hh->(realEstateDataManager.getDwelling(hh.getDwellingId()).getPrice() / (double) (HouseholdUtil.getAnnualHhIncome(hh)))).average().getAsDouble());

        nonSpatialSDGIndicator.println(year + ",avgfloorSpace," + households.stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / hh.getHhSize()).average().getAsDouble());

        nonSpatialSDGIndicator.println(year + ",avgDelayTime," + matsimPerson.values().stream().mapToDouble(pp -> pp.getCongestedTime()- pp.getFreeFlowTime()).average().getAsDouble());

        nonSpatialSDGIndicator.println(year + ",vacantDwellingRatio," + dwellings.stream().filter(dd -> dd.getUsage().equals(DwellingUsage.VACANT)).count()/(double)dwellings.size());

        nonSpatialSDGIndicator.println(year + ",populationSize," + persons.size());

        logger.info("indicators by demographic groups");
        Map<IncomeCategory, List<Household>> hhByIncome = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getAnnualHhIncome(hh))));

        nonSpatialSDGIndicator.println("year,incomeCategory,dwellingQuality,frequency");
        Map<IncomeCategory, Map<Integer, List<Household>>> hhByIncomeByDdQuality = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getAnnualHhIncome(hh)), Collectors.groupingBy(hh -> dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getQuality())));
        hhByIncomeByDdQuality.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryDwellingQuality -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryDwellingQuality.getKey() + "," + entryDwellingQuality.getValue().size())));
        nonSpatialSDGIndicator.println("year,incomeCategory,dwellingQuality,share");
        hhByIncomeByDdQuality.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryDwellingQuality -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryDwellingQuality.getKey() + "," + entryDwellingQuality.getValue().size()/(double)hhByIncome.get(entry.getKey()).size())));

        nonSpatialSDGIndicator.println("year,incomeCategory,avgfloorSpace");
        hhByIncome.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entry.getValue().stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / (double)hh.getHhSize()).average().getAsDouble()));

        Map<IncomeCategory, Map<Integer, List<Household>>> hhByIncomeByAuto = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getAnnualHhIncome(hh)),Collectors.groupingBy(hh -> hh.getAutos())));
        nonSpatialSDGIndicator.println("year,incomeCategory,avgCarOwnership");
        hhByIncome.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entry.getValue().stream().mapToDouble(hh -> hh.getAutos()).sum()/(double)entry.getValue().size()));
        nonSpatialSDGIndicator.println("year,incomeCategory,CarOwnership,frequency");
        hhByIncomeByAuto.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryIncome -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryIncome.getKey() + "," + entryIncome.getValue().size())));
        nonSpatialSDGIndicator.println("year,incomeCategory,CarOwnership,share");
        hhByIncomeByAuto.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryIncome -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryIncome.getKey() + "," + entryIncome.getValue().size()/(double)hhByIncome.get(entry.getKey()).size())));
        hhByAuto.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + ",carOwnership," + entry.getKey() + "," + entry.getValue().size()/(double)households.size()));

        nonSpatialSDGIndicator.close();
    }

    private double euclideanDistance(Coordinate home, Coordinate job) {

        return Math.sqrt((home.x-job.x)*(home.x-job.x)+(home.y-job.y)*(home.y-job.y));

    }


}