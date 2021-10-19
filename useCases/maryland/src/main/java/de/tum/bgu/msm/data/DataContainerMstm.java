package de.tum.bgu.msm.data;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.PrintWriter;
import java.util.Arrays;

public class DataContainerMstm implements DataContainer {
    
    private final DefaultDataContainer delegate;
    private final Properties properties;
    private int[] prestoRegionByTaz;

    public DataContainerMstm(
            GeoData geodata, RealEstateDataManager realEstateManager,
            JobDataManager jobManager, HouseholdDataManagerMstm householdManager,
            TravelTimes travelTimes, Accessibility accessibility,
            CommutingTimeProbability commutingTimeProbability,
            Properties properties) {

        delegate = new DefaultDataContainer(geodata, realEstateManager, 
                jobManager, householdManager, travelTimes, accessibility, commutingTimeProbability, properties);
        this.properties = properties;
    }

    @Override
    public HouseholdDataManagerMstm getHouseholdDataManager() {
        return (HouseholdDataManagerMstm) delegate.getHouseholdDataManager();
    }

    @Override
    public RealEstateDataManager getRealEstateDataManager() {
        return delegate.getRealEstateDataManager();
    }

    @Override
    public JobDataManager getJobDataManager() {
        return delegate.getJobDataManager();
    }

    @Override
    public GeoData getGeoData() {
        return delegate.getGeoData();
    }

    @Override
    public TravelTimes getTravelTimes() {
        return delegate.getTravelTimes();
    }

    @Override
    public Accessibility getAccessibility() {
        return delegate.getAccessibility();
    }
    
    @Override
    public CommutingTimeProbability getCommutingTimeProbability() {
        return delegate.getCommutingTimeProbability();
    }

    @Override
    public void setup() {
        delegate.setup();
        if (properties.main.createPrestoSummary) {
            preparePrestoSummary();
        }
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        if (properties.main.createPrestoSummary) {
            summarizePrestoRegion(properties.main.endYear);
        }
    }

    private void preparePrestoSummary() {

        String prestoZoneFile = properties.main.baseDirectory + properties.main.prestoZoneFile;
        TableDataSet regionDefinition = SiloUtil.readCSVfile(prestoZoneFile);
        regionDefinition.buildIndex(regionDefinition.getColumnPosition("aggFips"));

        final int highestId = getGeoData().getZones().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        prestoRegionByTaz = new int[highestId + 1];
        Arrays.fill(prestoRegionByTaz, -1);
        for (Zone zone : delegate.getGeoData().getZones().values()) {
            try {
                prestoRegionByTaz[zone.getZoneId()] =
                        (int) regionDefinition.getIndexedValueAt(((MstmZone) zone).getCounty().getId(), "presto");
            } catch (Exception e) {
                prestoRegionByTaz[zone.getZoneId()] = -1;
            }
        }
    }

    private void summarizePrestoRegion(int year) {
        // summarize housing costs by income group in SILO region

        String fileName = (properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/" +
                properties.main.prestoSummaryFile + ".csv");
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, year != properties.main.baseYear);
        pw.println(year + ",Housing costs by income group");
        pw.print("Income");
        for (int i = 0; i < 10; i++) pw.print(",rent_" + ((i + 1) * 250));
        pw.println(",averageRent");
        int[][] rentByIncome = new int[10][10];
        int[] rents = new int[10];
        for (Household hh : getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            if (prestoRegionByTaz[zone] > 0) {
                int hhInc = HouseholdUtil.getAnnualHhIncome(hh);
                int rent = getRealEstateDataManager().getDwelling(hh.getDwellingId()).getPrice();
                int incCat = Math.min((hhInc / 10000), 9);
                int rentCat = Math.min((rent / 250), 9);
                rentByIncome[incCat][rentCat]++;
                rents[incCat] += rent;
            }
        }
        for (int i = 0; i < 10; i++) {
            pw.print(String.valueOf((i + 1) * 10000));
            int countThisIncome = 0;
            for (int r = 0; r < 10; r++) {
                pw.print("," + rentByIncome[i][r]);
                countThisIncome += rentByIncome[i][r];
            }
            pw.println("," + rents[i] / countThisIncome);
        }
    }
}
