package de.tum.bgu.msm.models.transportModel;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.Input;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Transport Model Interface for MITO
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public final class MitoTransportModel extends AbstractModel implements TransportModelI {

    private static final Logger logger = Logger.getLogger( MitoTransportModel.class );
	private MitoModel mito;
    private final TravelTimes travelTimes;
    private final String propertiesPath;
    private final String baseDirectory;

    public MitoTransportModel(String baseDirectory, SiloDataContainer dataContainer, TravelTimes travelTimes) {
    	super(dataContainer);
    	this.travelTimes = travelTimes;
		this.propertiesPath = Properties.get().transportModel.demandModelPropertiesPath;
		this.baseDirectory = baseDirectory;

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

    	testChangesInTravelTimes();
    }

	private void testChangesInTravelTimes() {
    	logger.warn("TESTING TRAVEL TIMES FROM MITO MATSim");
    	int[] origins = new int[]{10,504,3400,2256};
		int[] destinations = new int[]{45,39,117,3800,4500};

		for (int origin: origins){
			for (int destination : destinations){
				logger.warn("Travel time between " + origin + " and " + destination +  " is " + travelTimes.getTravelTime(origin, destination, 8*3600, TransportMode.car));
			}
		}


	}

	private void updateData(int year) {
    	Map<Integer, MitoZone> zones = new HashMap<>();
		for (Zone siloZone: dataContainer.getGeoData().getZones().values()) {
			AreaTypes.SGType areaType = AreaTypes.SGType.RURAL; //TODO: put real area type in here
			MitoZone zone = new MitoZone(siloZone.getId(), siloZone.getArea(), areaType);
			zones.put(zone.getId(), zone);
		}
		dataContainer.getJobData().fillMitoZoneEmployees(zones);

		Map<Integer, MitoHousehold> households = convertHhs(zones);
		for(Person person: dataContainer.getHouseholdData().getPersons()) {
			int hhId = person.getHh().getId();
			if(households.containsKey(hhId)) {
				MitoPerson mitoPerson = convertToMitoPp(person);
				households.get(hhId).addPerson(mitoPerson);
			} else {
				logger.warn("Person " + person.getId() + " refers to non-existing household " + hhId
						+ " and will thus NOT be considered in the transport model.");
			}
		}

        logger.info("  SILO data being sent to MITO");
        Input.InputFeed feed = new Input.InputFeed(zones, travelTimes, households,year);
        mito.feedData(feed);
    }

	private Map<Integer, MitoHousehold> convertHhs(Map<Integer, MitoZone> zones) {
		Map<Integer, MitoHousehold> thhs = new HashMap<>();
		RealEstateDataManager realEstateData = dataContainer.getRealEstateData();
		for (Household siloHousehold : dataContainer.getHouseholdData().getHouseholds()) {
			int zoneId = -1;
			Dwelling dwelling = realEstateData.getDwelling(siloHousehold.getDwellingId());
			if(dwelling != null) {
				zoneId = dwelling.getZone();
			}
			MitoZone zone = zones.get(zoneId);
			MitoHousehold household = convertToMitoHh(siloHousehold, zone);
			thhs.put(household.getId(), household);
		}
		return thhs;
	}

	private MitoHousehold convertToMitoHh(Household household, MitoZone zone) {
		return new MitoHousehold(household.getId(), household.getHhIncome(), household.getAutos(), zone);
	}

	private MitoPerson convertToMitoPp(Person person) {
		final Gender mitoGender = Gender.valueOf(person.getGender());
		final Occupation mitoOccupation = Occupation.valueOf(person.getOccupation());
		final int workPlace = person.getWorkplace();
		int workzone = -1;
		if(workPlace > 0) {
			workzone = dataContainer.getJobData().getJobFromId(workPlace).getZone();
		}
		return new MitoPerson(person.getId(), mitoOccupation, workzone, person.getAge(), mitoGender, person.hasDriverLicense());
	}

    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }
}