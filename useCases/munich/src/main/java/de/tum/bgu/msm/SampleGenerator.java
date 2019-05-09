package de.tum.bgu.msm;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.data.school.SchoolData;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.io.input.AbstractOmxReader;
import de.tum.bgu.msm.io.input.readers.SkimsReader;
import de.tum.bgu.msm.io.output.OmxMatrixWriter;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashSet;
import java.util.Set;

public class SampleGenerator {


    public static void main(String[] args) {

    }

    public static DataContainer generateSampleForZones(DataContainer data, Set<Integer> zoneIdFilter) {

        Set<Integer> removedSchools = new HashSet<>();
        final SchoolData schoolData = ((DataContainerMuc) data).getSchoolData();
        schoolData.setup();
        for(School school: schoolData.getSchools()) {
            if(!zoneIdFilter.contains(school.getZoneId())) {
                removedSchools.add(school.getId());
                schoolData.removeSchool(school.getId());
            }
        }

        for(Person pp: data.getHouseholdDataManager().getPersons()) {
            final int schoolId = ((PersonMuc) pp).getSchoolId();
            if(schoolId > 0) {
                if(removedSchools.contains(schoolId)) {
                    ((PersonMuc) pp).setSchoolId(-2);
                }
            }
        }

        for(Dwelling dwelling: data.getRealEstateDataManager().getDwellings()) {
            if(!zoneIdFilter.contains(dwelling.getZoneId())) {
                if(dwelling.getResidentId() > 0) {
                    Household household
                            = data.getHouseholdDataManager().getHouseholdFromId(dwelling.getResidentId());
                    household.setDwelling(-1);
                    for (Person person : household.getPersons().values()) {
                        if (person.getJobId() > 0) {
                            data.getJobDataManager().removeJob(person.getJobId());
                        }
                    }
                    data.getHouseholdDataManager().removeHousehold(household.getId());
                }
                data.getRealEstateDataManager().removeDwelling(dwelling.getId());
            }
        }

        for(Job job: data.getJobDataManager().getJobs()) {
            if(!zoneIdFilter.contains(job.getZoneId())) {
                if(job.getWorkerId() > 0) {
                    Person p = data.getHouseholdDataManager().getPersonFromId(job.getWorkerId());
                    p.setWorkplace(-2);
                }
                data.getJobDataManager().removeJob(job.getId());
            }
        }
        return data;
    }
}
