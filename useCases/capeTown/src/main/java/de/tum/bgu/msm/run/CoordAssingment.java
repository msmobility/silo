package de.tum.bgu.msm.run;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.io.GeoDataReaderCapeTown;
import de.tum.bgu.msm.io.input.DefaultDwellingReader;
import de.tum.bgu.msm.io.input.DefaultJobReader;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.io.output.DefaultJobWriter;
import de.tum.bgu.msm.utils.SiloUtil;

public class CoordAssingment {

    public static void main(String[] args) {

        GeoData geoData = new DefaultGeoData();
        GeoDataReaderCapeTown geoDataReaderCapeTown = new GeoDataReaderCapeTown(geoData);
        geoDataReaderCapeTown.readZoneCsv("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\input\\zoneSystem.csv");
        geoDataReaderCapeTown.readZoneShapefile("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\input\\zonesShapefile\\zones.shp");

        RealEstateDataManager dataManager = new RealEstateDataManagerImpl(null, new DwellingDataImpl(), null, null, new DwellingFactoryImpl(), null);
        new DefaultDwellingReader(dataManager).readData("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\microData\\dd_2011.csv");

        for(Dwelling dwelling: dataManager.getDwellings()) {
            Zone zone = geoData.getZones().get(dwelling.getZoneId());
            dwelling.setCoordinate(zone.getRandomCoordinate(SiloUtil.getRandomObject()));
        }

        new DefaultDwellingWriter(dataManager.getDwellings()).writeDwellings("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\microData\\dd_2011_micro.csv");

        JobDataManager jobDataManager = new JobDataManagerImpl(null, new JobFactoryImpl(), new JobDataImpl(), geoData, null, null);
        new DefaultJobReader(jobDataManager).readData("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\microData\\jj_2011.csv");

        JobDataManager jobDataManagerCopy = new JobDataManagerImpl(null, new JobFactoryImpl(), new JobDataImpl(), geoData, null, null);
        for(Job job: jobDataManager.getJobs()) {
            Zone zone = geoData.getZones().get(job.getZoneId());
            jobDataManagerCopy.addJob(jobDataManagerCopy.getFactory().createJob(job.getId(), job.getZoneId(), zone.getRandomCoordinate(SiloUtil.getRandomObject()), job.getWorkerId(), job.getType()));
        }

        new DefaultJobWriter(jobDataManagerCopy.getJobs()).writeJobs("C:\\Users\\nkueh\\IdeaProjects\\silo-parent\\cape_town_fabilut\\silo\\microData\\jj_2011_micro.csv");
    }
}
