package de.tum.bgu.msm.data.mito;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.jobTypes.munich.MunichJobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.mito.MitoDataConverter;
import de.tum.bgu.msm.schools.DataContainerWithSchoolsImpl;
import de.tum.bgu.msm.schools.School;
import de.tum.bgu.msm.schools.SchoolImpl;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.Map;

public class MitoDataConverterMuc implements MitoDataConverter {

    private final static Logger logger = Logger.getLogger(MitoDataConverterMuc.class);

    @Override
    public DataSet convertData(DataContainer dataContainer) {
        DataSet dataSet = new DataSet();
        convertZones(dataSet, dataContainer);
        fillMitoZoneEmployees(dataSet, dataContainer);
        convertSchools(dataSet, dataContainer);
        convertHhs(dataSet, dataContainer);
        return dataSet;
    }

    private void convertZones(DataSet dataSet, DataContainer dataContainer) {
        for (Zone siloZone : dataContainer.getGeoData().getZones().values()) {
            MitoZone zone = new MitoZone(siloZone.getZoneId(), ((ZoneMuc) siloZone).getAreaTypeSG());
            zone.setGeometry((Geometry) siloZone.getZoneFeature().getDefaultGeometry());
            dataSet.addZone(zone);
        }
    }

    private void convertSchools(DataSet dataSet, DataContainer dataContainer) {
        Map<Integer, MitoZone> zones = dataSet.getZones();
        for (School school : ((DataContainerWithSchoolsImpl) dataContainer).getSchoolData().getSchools()) {
            MitoZone zone = zones.get(school.getZoneId());
            Coordinate coordinate;
            if (school instanceof SchoolImpl) {
                coordinate = ((SchoolImpl) school).getCoordinate();
            } else {
                coordinate = zone.getRandomCoord(SiloUtil.getRandomObject());
            }
            MitoSchool mitoSchool = new MitoSchool(zones.get(school.getZoneId()), coordinate, school.getId());
            mitoSchool.setStartTime_min((int) (school.getStartTimeInSeconds() / 60.));
            mitoSchool.setEndTime_min((int) ((school.getStartTimeInSeconds() + school.getStudyTimeInSeconds()) / 60.));
            zone.addSchoolEnrollment(school.getOccupancy());
            dataSet.addSchool(mitoSchool);
        }
    }

    private void convertHhs(DataSet dataSet, DataContainer dataContainer) {
        Map<Integer, MitoZone> zones = dataSet.getZones();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        int householdsSkipped = 0;
        int randomCoordCounter = 0;
        for (Household siloHousehold : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zoneId = -1;
            Dwelling dwelling = realEstateDataManager.getDwelling(siloHousehold.getDwellingId());
            if (dwelling != null) {
                zoneId = dwelling.getZoneId();

            }
            MitoZone zone = zones.get(zoneId);

            MitoHousehold household = new MitoHousehold(
                    siloHousehold.getId(),
                    HouseholdUtil.getAnnualHhIncome(siloHousehold) / 12,
                    (int) siloHousehold.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count());
            household.setHomeZone(zone);

            Coordinate coordinate;
            if (dwelling.getCoordinate() != null) {
                coordinate = dwelling.getCoordinate();
            } else {
                randomCoordCounter++;
                coordinate = zone.getRandomCoord(SiloUtil.getRandomObject());
            }

            //todo if there are housholds without adults they cannot be processed
            if (siloHousehold.getPersons().values().stream().anyMatch(p -> p.getAge() >= 18)) {
                household.setHomeLocation(coordinate);
                zone.addHousehold();
                dataSet.addHousehold(household);
                for (Person person : siloHousehold.getPersons().values()) {
                    MitoPerson mitoPerson = convertToMitoPp((PersonMuc) person, dataSet, dataContainer);
                    household.addPerson(mitoPerson);
                    dataSet.addPerson(mitoPerson);
                }
            } else {
                householdsSkipped++;
            }
        }
        logger.warn("There are " + randomCoordCounter + " households that were assigned a random coord inside their zone (" +
                randomCoordCounter / dataContainer.getHouseholdDataManager().getHouseholds().size() * 100 + "%)");
        logger.warn("There are " + householdsSkipped + " households without adults that CANNOT be processed in MITO (" +
                householdsSkipped / dataContainer.getHouseholdDataManager().getHouseholds().size() * 100 + "%)");
    }


    private MitoPerson convertToMitoPp(PersonMuc person, DataSet dataSet, DataContainer dataContainer) {
        final MitoGender mitoGender = MitoGender.valueOf(person.getGender().name());
        final MitoOccupationStatus mitoOccupationStatus = MitoOccupationStatus.valueOf(person.getOccupation().getCode());

        MitoOccupation mitoOccupation = null;
        switch (mitoOccupationStatus) {
            case WORKER:
                if (person.getJobId() > 0) {
                    JobMuc job = (JobMuc) dataContainer.getJobDataManager().getJobFromId(person.getJobId());
                    MitoZone zone = dataSet.getZones().get(job.getZoneId());
                    final Coordinate coordinate;
                    if (job instanceof MicroLocation) {
                        coordinate = job.getCoordinate();
                    } else {
                        coordinate = zone.getRandomCoord(SiloUtil.getRandomObject());
                    }
                    mitoOccupation = new MitoJob(zone, coordinate, job.getId());
                    mitoOccupation.setStartTime_min((int) (job.getStartTimeInSeconds().get() / 60.));
                    mitoOccupation.setEndTime_min((int) ((job.getStartTimeInSeconds().get() + job.getWorkingTimeInSeconds().get()) / 60.));
                }
                break;
            case UNEMPLOYED:
                break;
            case STUDENT:
                if (person.getSchoolId() > 0) {
                    mitoOccupation = dataSet.getSchools().get(person.getSchoolId());
                }
                break;
        }

        return new MitoPerson(
                person.getId(),
                mitoOccupationStatus,
                mitoOccupation,
                person.getAge(),
                mitoGender,
                person.hasDriverLicense());
    }

    private void fillMitoZoneEmployees(DataSet dataSet, DataContainer dataContainer) {

        final Map<Integer, MitoZone> zones = dataSet.getZones();

        for (Job jj : dataContainer.getJobDataManager().getJobs()) {
            final MitoZone zone = zones.get(jj.getZoneId());
            final String type = jj.getType().toUpperCase();
            try {
                de.tum.bgu.msm.data.jobTypes.JobType mitoJobType = MunichJobType.valueOf(type);
                zone.addEmployeeForType(mitoJobType);
            } catch (IllegalArgumentException e) {
                logger.warn("Job type " + type + " not defined for MITO implementation: Munich");
            }
        }
    }
}