package de.tum.bgu.msm.models.transportModel;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.munich.MunichZone;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelDistances.TravelDistances;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.Input;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of Transport Model Interface for MITO
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public final class MitoTransportModel extends AbstractModel implements TransportModelI {

    private static final Logger logger = Logger.getLogger( MitoTransportModel.class );
	private MitoModel mito;
    private TravelTimes travelTimes;
    private TravelDistances travelDistancesAuto;
    private final String propertiesPath;
    private final String baseDirectory;

    public MitoTransportModel(String baseDirectory, SiloDataContainer dataContainer) {
    	super(dataContainer);
    	this.travelTimes = Objects.requireNonNull(dataContainer.getTravelTimes());
		this.propertiesPath = Objects.requireNonNull(Properties.get().transportModel.demandModelPropertiesPath);
		this.baseDirectory = Objects.requireNonNull(baseDirectory);
	}

    @Override
    public void runTransportModel(int year) {
		this.mito = MitoModel.initializeModelFromSilo(propertiesPath);
		this.mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
		setBaseDirectory(baseDirectory);
    	MitoModel.setScenarioName (Properties.get().main.scenarioName);
    	updateData(year);
    	logger.info("  Running travel demand model MITO for the year " + year);
    	mito.runModel();
		travelTimes = mito.getData().getTravelTimes();
		travelDistancesAuto = mito.getData().getTravelDistancesAuto();
    }

	private void updateData(int year) {
    	Map<Integer, MitoZone> zones = new HashMap<>();
		for (Zone siloZone: dataContainer.getGeoData().getZones().values()) {
			MitoZone zone = new MitoZone(siloZone.getZoneId(), siloZone.getArea(), ((MunichZone)siloZone).getAreaType());
			zones.put(zone.getId(), zone);
		}
		dataContainer.getJobData().fillMitoZoneEmployees(zones);
		Map<Integer, MitoHousehold> households = convertHhs(zones);
		for(Person person: dataContainer.getHouseholdData().getPersons()) {
			int hhId = person.getHh().getId();
			if(households.containsKey(hhId)) {
				MitoPerson mitoPerson = convertToMitoPp(person);
				Coordinate workplaceCoordinate = null;
				//todo need to mode the transitions between new born, student, unemployed and worker in a better way
				if (person.getWorkplace()>0) {
					//is a worker
					Job job = dataContainer.getJobData().getJobFromId(person.getWorkplace());
					if (job instanceof MicroLocation) {
						//is a worker with a microlocated job
						mitoPerson.setOccupationLocation(((MicroLocation) job).getCoordinate());
					}
				} else if (person.getSchoolLocation() instanceof MicroLocation) {
					//is a student with a microlocated school
					mitoPerson.setOccupationLocation(person.getSchoolLocation());
				}
				households.get(hhId).addPerson(mitoPerson);
			} else {
				//logger.warn("Person " + person.getZoneId() + " refers to non-existing household " + hhId
				//		+ " and will thus NOT be considered in the transport model.");
			}
		}
        logger.info("  SILO data being sent to MITO");
        Input.InputFeed feed = new Input.InputFeed(zones, travelTimes, travelDistancesAuto, households, year, dataContainer.getGeoData().getZoneFeatureMap());
        mito.feedData(feed);
    }

	private Map<Integer, MitoHousehold> convertHhs(Map<Integer, MitoZone> zones) {
		Map<Integer, MitoHousehold> thhs = new HashMap<>();
		RealEstateDataManager realEstateData = dataContainer.getRealEstateData();
		int householdsSkipped = 0;
		for (Household siloHousehold : dataContainer.getHouseholdData().getHouseholds()) {
			int zoneId = -1;
			Dwelling dwelling = realEstateData.getDwelling(siloHousehold.getDwellingId());
			if(dwelling != null) {
				zoneId = dwelling.getZoneId();

			}
			MitoZone zone = zones.get(zoneId);

			MitoHousehold household = convertToMitoHh(siloHousehold, zone);
			//set mitoHousehold's microlocation
			if (dwelling instanceof MicroLocation) {
				household.setHomeLocation(((MicroLocation) dwelling).getCoordinate());
			}
            //todo if there are housholds without adults they cannot be processed
			if (siloHousehold.getPersons().stream().filter(p -> p.getAge() >= 18).count() != 0){
                thhs.put(household.getId(), household);
            } else {
                householdsSkipped++;
            }
		}
        logger.warn("There are " + householdsSkipped + " households without adults that CANNOT be processed in MITO (" +
                householdsSkipped/dataContainer.getHouseholdData().getHouseholds().size()*100 + "%)");
		return thhs;
	}

	private MitoHousehold convertToMitoHh(Household household, MitoZone zone) {
		return new MitoHousehold(household.getId(), household.getHhIncome(), household.getAutos(), zone);
	}

	private MitoPerson convertToMitoPp(Person person) {
		final MitoGender mitoGender = MitoGender.valueOf(person.getGender().name());
		final MitoOccupation mitoOccupation = MitoOccupation.valueOf(person.getOccupation().getCode());
		final int workPlace = person.getWorkplace();
		int workzone = -1;
		if(workPlace > 0) {
			workzone = dataContainer.getJobData().getJobFromId(workPlace).getZoneId();
		}
		return new MitoPerson(person.getId(), mitoOccupation, workzone, person.getAge(), mitoGender, person.hasDriverLicense());
	}

    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }
}