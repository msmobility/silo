package de.tum.bgu.msm.data.maryland;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.data.AbstractDefaultGeoData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zonal, county and regional data used by the SILO Model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 20 April 2015 in College Park
 **/
public class GeoDataMstm extends AbstractDefaultGeoData {

    private final Map<Integer, County> counties = new HashMap<>();

    private final String COUNTY_COLUMN_NAME = "COUNTYFIPS";

    public GeoDataMstm() {
        super("ZoneId", "Region", "SMZRMZ");
    }

    @Override
    public void readData() {
        super.readData();
        readCrimeData();
    }

    private void readCrimeData() {
        String crimeFileName = Properties.get().main.baseDirectory + Properties.get().geo.countyCrimeFile;
        TableDataSet tblCrimeIndex = SiloUtil.readCSVfile(crimeFileName);
        for (int row = 1; row <= tblCrimeIndex.getRowCount(); row++) {
            int county = (int) tblCrimeIndex.getValueAt(row, "FIPS");
            double crimeRate = tblCrimeIndex.getValueAt(row, "CrimeIndicator");
            if(counties.containsKey(county)) {
                counties.get(county).setCrimeRate(crimeRate);
            } else {
                throw new RuntimeException("County " + county + " referred in crime data table does not exist!");
            }
        }

        for(Region region: regions.values()) {
            double regionalCrimeRate = 0.;
            double regionalArea = 0.;
            for(Zone zone: region.getZones()) {
                regionalCrimeRate += ((MstmZone)zone).getCounty().getCrimeRate() * zone.getArea_sqmi();
                regionalArea += zone.getArea_sqmi();
            }
            regionalCrimeRate /= regionalArea;
            ((MstmRegion) region).setCrimeRate(regionalCrimeRate);
        }
    }

    @Override
    protected void readZones() {
        String fileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalDataFile;
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        int[] zoneIds = zonalData.getColumnAsInt(zoneIdColumnName);
        int[] zoneMsa = zonalData.getColumnAsInt("msa");
        int[] puma = zonalData.getColumnAsInt("PUMA");
        int[] simplifiedPuma = zonalData.getColumnAsInt("simplifiedPUMA");
        int[] countyData = zonalData.getColumnAsInt(COUNTY_COLUMN_NAME);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");
        int[] regionData = zonalData.getColumnAsInt("Region");

        for(int i = 0; i < zoneIds.length; i++) {
            County county;
            if(counties.containsKey(countyData[i])) {
                county = counties.get(countyData[i]);
            } else {
                county = new County(countyData[i]);
                counties.put(county.getId(), county);
            }

            Region region;
            int regionId = regionData[i];
            if (regions.containsKey(regionId)) {
                region = regions.get(regionId);
            } else {
                region = new MstmRegion(regionId);
                regions.put(region.getId(), region);
            }

            Zone zone = new MstmZone(zoneIds[i], zoneMsa[i], zoneAreas[i], puma[i], simplifiedPuma[i], county, region);
            region.addZone(zone);
            zones.put(zoneIds[i], zone);
        }

        zonalData.buildIndex(zonalData.getColumnPosition("ZoneId"));
        for (Region region: regions.values()) {
            double schoolQuality = region.getZones().stream().mapToDouble(
                    zone -> zonalData.getIndexedValueAt(zone.getId(), "SchoolQualityIndex")).average().getAsDouble();

            ((MstmRegion) region).setSchoolQuality(schoolQuality);
        }
    }

    /**
     * @deprecated  As of jan'18. Use direct access method of {@link MstmZone} instead
     */
    @Deprecated
    public int getPUMAofZone(int taz) {
        return ((MstmZone)this.zones.get(taz)).getPuma();
    }
    /**
     * @deprecated  As of jan'18. Use direct access method of {@link MstmZone} instead
     */
    @Deprecated
    public int getSimplifiedPUMAofZone(int taz) {
        return ((MstmZone)this.zones.get(taz)).getSimplifiedPuma();
    }

    /**
     * @deprecated  As of jan'18. Use direct access method of {@link MstmZone} instead
     */
    @Deprecated
    public int getCountyOfZone(int taz) {
        return ((MstmZone)this.zones.get(taz)).getCounty().getId();
    }
}
