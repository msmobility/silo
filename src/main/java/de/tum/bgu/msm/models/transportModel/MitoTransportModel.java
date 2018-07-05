package de.tum.bgu.msm.models.transportModel;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.travelDistances.TravelDistances;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.Input;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;


import javax.measure.unit.SI;
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
    private TravelTimes travelTimes;
    private TravelDistances travelDistancesAuto;
    private final String propertiesPath;
    private final String baseDirectory;

    public MitoTransportModel(String baseDirectory, SiloDataContainer dataContainer, TravelTimes travelTimes) {
    	super(dataContainer);
    	this.travelTimes = travelTimes;
		this.propertiesPath = Properties.get().transportModel.demandModelPropertiesPath;
		this.baseDirectory = baseDirectory;
		this.travelDistancesAuto = null;

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
			//todo do we really need this area type? Later is read for mode choice at least
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

				//TODO: remove it when we implement interface
				if(Properties.get().main.implementation == Implementation.MUNICH){
					if (person.getSchoolPlace() != 0){
						mitoPerson.setOccupationCoord(person.getSchoolCoord());
					}else if(person.getWorkplace()>0){
						mitoPerson.setOccupationCoord(dataContainer.getJobData().getJobFromId(person.getWorkplace()).getCoord());
					}
				}

				households.get(hhId).addPerson(mitoPerson);
			} else {
				logger.warn("Person " + person.getId() + " refers to non-existing household " + hhId
						+ " and will thus NOT be considered in the transport model.");
			}
		}
        logger.info("  SILO data being sent to MITO");
        Input.InputFeed feed = new Input.InputFeed(zones, travelTimes, travelDistancesAuto, households, year, dataContainer.getGeoData().getZoneFeatureMap());
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
			//set mitoHousehold's microlocation
			household.setHomeCoord(dwelling.getCoord());
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