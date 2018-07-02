package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.munich.MunichZone;
import de.tum.bgu.msm.models.transportModel.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesSynPop;

import java.util.List;
import java.util.concurrent.Callable;

public class AddJobsDefinition extends EmploymentChangeDefinition implements Callable {

    public final List<Integer> ids;
    public final GeoData geoData;

    public AddJobsDefinition(int zone, int change, String jobType, SiloDataContainer dataContainer) {
        super(zone, change, jobType, dataContainer);
        this.ids = jobDataManager.getNextJobIds(change);
        this.geoData = dataContainer.getGeoData();
    }

    @Override
    public Object call() throws Exception {

        for (int i = 0; i < changes; i++) {
            int id = ids.get(i);
            synchronized (Job.class) {
                jobDataManager.createJob(id, zone, -1, jobType);
                if(Properties.get().main.implementation == Implementation.MUNICH) {
                    if(Properties.get().main.runDwellingMicrolocation) {
                        jobDataManager.getJobFromId(id).setCoord(SiloMatsimUtils.getRandomCoordinateInGeometry(((MunichZone) geoData.getZones().get(zone)).getZoneFeature()));
                    }
                }
            }
            if (id == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Job " + id + " of type " + jobType +
                        " was newly created in zone " + zone + " based on exogenous forecast.");
            }
        }
        return null;
    }
}
