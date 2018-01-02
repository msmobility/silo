package de.tum.bgu.msm.data.maryland;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.AbstractDefaultGeoData;
import de.tum.bgu.msm.properties.Properties;

/**
 * Zonal, county and regional data used by the SILO Model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 20 April 2015 in College Park
 **/
public class GeoDataMstm extends AbstractDefaultGeoData {

    private float[] zonalSchoolQuality;
    private float[] regionalSchoolQuality;
    private int[] counties;
    private float[] countyCrimeRate;
    private float[] regionalCrimeRate;

    private final String COUNTY_COLUMN_NAME = "COUNTYFIPS";

    public GeoDataMstm() {
        super("ZoneId", "Region");
    }

    @Override
    public void setInitialData() {
        super.setInitialData();
        createListOfCountyFIPSCodes();
        readSchoolQuality();
        readCrimeData();
    }

    private void readSchoolQuality() {
        String sqFileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalSchoolQualityFile;
        TableDataSet tblSchoolQualityIndex = SiloUtil.readCSVfile(sqFileName);
        zonalSchoolQuality = new float[zones.length];
        for (int row = 1; row <= tblSchoolQualityIndex.getRowCount(); row++) {
            int taz = (int) tblSchoolQualityIndex.getValueAt(row, "Zone");
            zonalSchoolQuality[zoneIndex[taz]] = tblSchoolQualityIndex.getValueAt(row, "SchoolQualityIndex");
        }
        regionalSchoolQuality = new float[SiloUtil.getHighestVal(regionList) + 1];
        for (int zone: zones) {
            int reg = getRegionOfZone(zone);
            regionalSchoolQuality[reg] += getZonalSchoolQuality(zone);
        }
        for (int region: regionList)
            regionalSchoolQuality[region] = regionalSchoolQuality[region] / regionDefinition.get(region).length;
    }

    private void readCrimeData() {
        countyCrimeRate = new float[counties.length];
        String crimeFileName = Properties.get().main.baseDirectory + Properties.get().geo.countyCrimeFile;
        TableDataSet tblCrimeIndex = SiloUtil.readCSVfile(crimeFileName);
        for (int row = 1; row <= tblCrimeIndex.getRowCount(); row++) {
            int county = (int) tblCrimeIndex.getValueAt(row, "FIPS");
            countyCrimeRate[countyIndex[county]] = tblCrimeIndex.getValueAt(row, "CrimeIndicator");
        }
        regionalCrimeRate = new float[SiloUtil.getHighestVal(regionList) + 1];
        float[] regionalArea = new float[SiloUtil.getHighestVal(regionList) + 1];
        for (int zone: zones) {
            int reg = getRegionOfZone(zone);
            int fips = getCountyOfZone(zone);
            regionalCrimeRate[reg] += countyCrimeRate[countyIndex[fips]] * getSizeOfZoneInAcres(zone);  // weight by bedrooms
            regionalArea[reg] += getSizeOfZoneInAcres(zone);
        }
        for (int region: regionList) {
            regionalCrimeRate[region] = regionalCrimeRate[region] / regionalArea[region];
        }
    }

    private void createListOfCountyFIPSCodes() {
        counties = SiloUtil.idendifyUniqueValues(zonalData.getColumnAsInt(COUNTY_COLUMN_NAME));
        countyIndex = SiloUtil.createIndexArray(counties);
    }

    public  int getCountyOfZone(int zone) {
        return (int) zonalData.getIndexedValueAt(zone, COUNTY_COLUMN_NAME);
    }

    public int getPUMAofZone (int taz) {
        return (int) zonalData.getIndexedValueAt(taz, "PUMA");
    }

    public int getSimplifiedPUMAofZone (int taz) {
        // return PUMA in which taz is located (less geographic detail, last digit is rounded to 1)
        return (int) zonalData.getIndexedValueAt(taz, "simplifiedPUMA");
    }

    public float getZonalSchoolQuality (int zone) {
        return zonalSchoolQuality[zoneIndex[zone]];
    }

    public float getRegionalSchoolQuality (int region) {
        return regionalSchoolQuality[region];
    }

    public float getCountyCrimeRate (int fips) {
        return countyCrimeRate[countyIndex[fips]];
    }

    public float getRegionalCrimeRate (int region) {
        return regionalCrimeRate[region];
    }
}
