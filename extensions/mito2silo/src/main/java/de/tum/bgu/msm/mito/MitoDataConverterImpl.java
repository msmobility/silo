package de.tum.bgu.msm.mito;

import de.tum.bgu.msm.container.DataContainer;

import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.jobTypes.JobType;
import de.tum.bgu.msm.data.jobTypes.MunichJobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;
import java.util.Map;

/**
 * A very basic default implementation for mito data conversion
 * Caveats:
 *
 *  (1) All zones are considered as CORE_CITY in area type
 *  (2) No schools
 *  (3) Job types of the Munich classification mito are used
 */
public class MitoDataConverterImpl implements MitoDataConverter {

    private final Logger logger = LogManager.getLogger(MitoDataConverterImpl.class);

    @Override
    public DataSet convertData(DataContainer dataContainer) {
        logger.warn("Using default implementation of mito data conversion. Please Note:" +
                "\t 1) all zones are considered as CORE-CITY" +
                "\2 2) no schools (no predefined education destinations)" +
                "\t 3) job types need to match default mito values:" + Arrays.toString(MunichJobType.values()));
        DataSet dataSet = new DataSetImpl();
        convertZones(dataSet, dataContainer);
        fillMitoZoneEmployees(dataSet, dataContainer);
        convertHhs(dataSet, dataContainer);
        return dataSet;
    }



    private void convertZones(DataSet dataSet, DataContainer dataContainer) {
        for (Zone siloZone : dataContainer.getGeoData().getZones().values()) {
            MitoZone zone = new MitoZone(siloZone.getZoneId(), AreaTypes.SGType.CORE_CITY);
            zone.setGeometry((Geometry) siloZone.getZoneFeature().getDefaultGeometry());
            dataSet.addZone(zone);
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
                    (int) siloHousehold.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count(),Boolean.TRUE);
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
                    MitoPerson mitoPerson = convertToMitoPp(person, household, dataSet, dataContainer);
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


    private MitoPerson convertToMitoPp(Person person, MitoHousehold household, DataSet dataSet, DataContainer dataContainer) {
        final MitoGender mitoGender = MitoGender.valueOf(person.getGender().name());
        final MitoOccupationStatus mitoOccupationStatus = MitoOccupationStatus.valueOf(person.getOccupation().getCode());

        MitoOccupation mitoOccupation = null;
        String jobType = null;
        switch (mitoOccupationStatus) {
            case WORKER:
                if (person.getJobId() > 0) {
                    Job job = dataContainer.getJobDataManager().getJobFromId(person.getJobId());
                    jobType = job.getType();
                    MitoZone zone = dataSet.getZones().get(job.getZoneId());
                    final Coordinate coordinate;
                    if (job instanceof MicroLocation) {
                        coordinate = job.getCoordinate();
                    } else {
                        coordinate = zone.getRandomCoord(SiloUtil.getRandomObject());
                    }
                    mitoOccupation = new MitoJob(zone, coordinate, job.getId());
                }
                break;
            case UNEMPLOYED:
                break;
            case STUDENT:
                break;
        }

        return new MitoPersonImpl(
                person.getId(),
                household,
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
                //TODO: shouldn't use MunichJobType, need to implement JobTypeImpl in MITO
                de.tum.bgu.msm.data.jobTypes.JobType mitoJobType = MunichJobType.valueOf(type);
                zone.addEmployeeForType(mitoJobType);
            } catch (IllegalArgumentException e) {
                logger.warn("Job type " + type + " not defined for MITO implementation: Munich");
            }
        }
    }
}
