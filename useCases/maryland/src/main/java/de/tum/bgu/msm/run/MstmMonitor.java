package de.tum.bgu.msm.run;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

public class MstmMonitor implements ResultsMonitor {

    private final static Logger logger = Logger.getLogger(MstmMonitor.class);

    /**
     * For compatibility with this legacy code, taken from configurable property
     * mstm.income.brackets     = 20000,40000,60000,100000
     */
    private final static int[] MSTM_INCOME_BRACKETS = {20000, 40000, 60000, 100000};

    private final DataContainer dataContainer;
    private final Properties properties;

    public MstmMonitor(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    @Override
    public void setup() {

    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter, List<MicroEvent> events) {

    }

    @Override
    public void endSimulation() {
        writeOutSocioEconomicDataForMstm(properties.main.endYear);
    }

    //taken from 2017 code
    // https://github.com/msmobility/silo/blob/e2e103425080e5e3b9388b9b4ed1923d4617a7da/src/main/java/de/tum/bgu/msm/transportModel/MstmTransportModel.java#L185-L298
    //nkuehnel, mar '20
    private void writeOutSocioEconomicDataForMstm(int year) {
        // write out file with socio-economic data for MSTM transportation model

        String fileName = (properties.main.baseDirectory + "/scenOutput/" + properties.main.scenarioName + "/" +
                "mstm/Activities" + "_" + year + ".csv");
        logger.info("  Summarizing socio-economic data for MSTM to file " + fileName);
        // summarize micro data
        GeoData geoData = dataContainer.getGeoData();

        Map<Integer, List<Household>> hhByZone = dataContainer.getHouseholdDataManager().getHouseholds().stream().collect(Collectors.groupingBy(hh -> dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId()));
        Map<Integer, Map<String, List<Job>>> jobsByTypeByZone = dataContainer.getJobDataManager().getJobs().stream().collect(Collectors.groupingBy(Location::getZoneId, Collectors.groupingBy(Job::getType)));

        TableDataSet enrollment = SiloUtil.readCSVfile(properties.main.baseDirectory + "/input/assumptions/Activities_2000.csv");
        enrollment.buildIndex(enrollment.getColumnPosition("SMZ_N"));

        // write file for MSTM
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        if (pw == null) {
            return;
        }
        pw.println("SMZ_N,SQMI,HH" + year + ",ENR" + year + ",RE" + year + ",OFF" + year + ",OTH" + year + ",TOT" + year);
        // SMZ_N: zone (int)
        // ACRES: size of zone in acres
        // HH: number of households in zone
        // ENR: school enrollment in zone (at school location)
        // RE: Retail employment in zone
        // OFF: Office employment in zone
        // OTH: Other employment in zone
        // TOT: Total employment in zone
        for (Zone zone : geoData.getZones().values()) {
            int zoneId = zone.getZoneId();
            int totalEmployment = jobsByTypeByZone.getOrDefault(zoneId, new HashMap<>()).values().stream().mapToInt(list -> list == null ? 0: list.size()).sum();

            pw.println(zone.getZoneId() + "," + zone.getArea_sqmi() + "," + hhByZone.getOrDefault(zoneId, EMPTY_LIST).size() + ","
                    + enrollment.getIndexedValueAt(zoneId, "ENR") + ","
                    + jobsByTypeByZone.getOrDefault(zoneId, new HashMap<>()).getOrDefault("RET",EMPTY_LIST).size() + "," +
                    jobsByTypeByZone.getOrDefault(zoneId, new HashMap<>()).getOrDefault("OFF", EMPTY_LIST).size() + ","
                    + jobsByTypeByZone.getOrDefault(zoneId, new HashMap<>()).getOrDefault("OTH", EMPTY_LIST).size() + ","
                    + totalEmployment);
        }
        pw.close();

        String fileNameWrk = (properties.main.baseDirectory + "/scenOutput/" + properties.main.scenarioName + "/mstm/HH_By_WRKS_INC" + "_" + year + ".csv");
        logger.info("  Summarizing households by number of workers for MSTM to file " + fileNameWrk);

        PrintWriter pwWrk = SiloUtil.openFileForSequentialWriting(fileNameWrk, false);
        if (pwWrk == null) {
            throw new RuntimeException("Error writing mstm output file!");
        }

        Map<Integer, Map<Integer, Map<Integer, List<Household>>>> householdsByIncomeByWorkersByZone = dataContainer.getHouseholdDataManager().getHouseholds().stream().collect(Collectors.groupingBy(hh ->
                        dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId(),
                Collectors.groupingBy(hh -> Math.min(3, HouseholdUtil.getNumberOfWorkers(hh)), Collectors.groupingBy(hh -> {
                    int annualHhIncome = HouseholdUtil.getAnnualHhIncome(hh);
                    int index = 1;
                    for (int i = 0; i < MSTM_INCOME_BRACKETS.length; i++) {
                        if (annualHhIncome > MSTM_INCOME_BRACKETS[i]) {
                            index = i + 2;
                        }
                    }
                    return index;
                }))));
        pwWrk.println("SMZ,WKR0_IQ1,WKR0_IQ2,WKR0_IQ3,WKR0_IQ4,WKR0_IQ5,WKR1_IQ1,WKR1_IQ2,WKR1_IQ3,WKR1_IQ4,WKR1_IQ5," +
                "WKR2_IQ1,WKR2_IQ2,WKR2_IQ3,WKR2_IQ4,WKR2_IQ5,WKR3_IQ1,WKR3_IQ2,WKR3_IQ3,WKR3_IQ4,WKR3_IQ5,Total");
        // SMZ: zone (int)
        // WKR0_IQ1: number of households with zero workers in income group 1 in zone
        // WKR0_IQ2: number of households with zero workers in income group 2 in zone
        // Etc.
        // Total: Total number of households in zone
        for (Zone zone : geoData.getZones().values()) {
            pwWrk.print(zone.getZoneId());
            int total = 0;
            for (int wrk = 0; wrk <= 3; wrk++) {
                for (int inc = 1; inc <= 5; inc++) {
                    int size = householdsByIncomeByWorkersByZone
                            .getOrDefault(zone.getZoneId(), new HashMap<>())
                            .getOrDefault(wrk, new HashMap<>())
                            .getOrDefault(inc, EMPTY_LIST)
                            .size();
                    pwWrk.print("," + size);
                    total += size;
                }
            }
            pwWrk.println("," + total);
        }
        pwWrk.close();

        String fileNameSize = (properties.main.baseDirectory + "/scenOutput/" + properties.main.scenarioName
                + "/mstm/HH_By_SIZ_INC_" + year + ".csv");
        logger.info("  Summarizing households by size for MSTM to file " + fileNameSize);

        PrintWriter pwSize = SiloUtil.openFileForSequentialWriting(fileNameSize, false);
        if (pwSize == null) {
            throw new RuntimeException("Error writing mstm file!");
        }

        Map<Integer, Map<Integer, Map<Integer, List<Household>>>> householdsByIncomeBySizeByZone = dataContainer.getHouseholdDataManager().getHouseholds().stream().collect(Collectors.groupingBy(hh ->
                        dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId(),
                Collectors.groupingBy(hh -> Math.min(5, hh.getHhSize()), Collectors.groupingBy(hh -> {
                    int annualHhIncome = HouseholdUtil.getAnnualHhIncome(hh);
                    int index = 1;
                    for (int i = 0; i < MSTM_INCOME_BRACKETS.length; i++) {
                        if (annualHhIncome > MSTM_INCOME_BRACKETS[i]) {
                            index = i + 2;
                        }
                    }
                    return index;
                }))));

        pwSize.println("SMZ,SIZ1_IQ1,SIZ1_IQ2,SIZ1_IQ3,SIZ1_IQ4,SIZ1_IQ5,SIZ2_IQ1,SIZ2_IQ2,SIZ2_IQ3,SIZ2_IQ4,SIZ2_IQ5," +
                "SIZ3_IQ1,SIZ3_IQ2,SIZ3_IQ3,SIZ3_IQ4,SIZ3_IQ5,SIZ4_IQ1,SIZ4_IQ2,SIZ4_IQ3,SIZ4_IQ4,SIZ4_IQ5,SIZ5_IQ1," +
                "SIZ5_IQ2,SIZ5_IQ3,SIZ5_IQ4,SIZ5_IQ5,Total");
        // SMZ: zone (int)
        // SIZ1_IQ1: number of households with household size 1 in income group 1 in zone
        // SIZ1_IQ2: number of households with household size 1 in income group 2 in zone
        // Etc.
        // Total: Total number of households in zone
        for (Zone zone : geoData.getZones().values()) {
            pwSize.print(zone.getZoneId());
            int total = 0;
            for (int size = 1; size <= 5; size++) {
                for (int inc = 1; inc <= 5; inc++) {
                    int count = householdsByIncomeBySizeByZone
                            .getOrDefault(zone.getZoneId(), new HashMap<>())
                            .getOrDefault(size, new HashMap<>())
                            .getOrDefault(inc, EMPTY_LIST)
                            .size();
                    pwSize.print("," + count);
                    total += count;
                }
            }
            pwSize.println("," + total);
        }
        pwSize.close();
    }
}
