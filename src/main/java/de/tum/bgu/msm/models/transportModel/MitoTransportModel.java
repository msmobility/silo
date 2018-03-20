package de.tum.bgu.msm.models.transportModel;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.InputFeed;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Transport Model Interface for MITO
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public final class MitoTransportModel implements TransportModelI {

    private static final Logger logger = Logger.getLogger( MitoTransportModel.class );
	private final SiloModelContainer modelContainer;
	private final SiloDataContainer dataContainer;
	private final MitoModel mito;

    public MitoTransportModel(String baseDirectory, SiloDataContainer dataContainer, SiloModelContainer modelContainer) {
		String propertiesPath = Properties.get().transportModel.demandModelPropertiesPath;
        this.mito = MitoModel.standAloneModel(propertiesPath, Implementation.valueOf(Properties.get().main.implementation.name()));
        this.modelContainer = modelContainer;
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        setBaseDirectory(baseDirectory);
		this.dataContainer = dataContainer;
	}

    @Override
    public void runTransportModel(int year) {
    	MitoModel.setScenarioName (Properties.get().main.scenarioName);
    	updateData();
    	logger.info("  Running travel demand model MITO for the year " + year);
    	mito.runModel();
    }
    
    private void updateData() {
    	Map<Integer, MitoZone> zones = new HashMap<>();
		for (Zone siloZone: dataContainer.getGeoData().getZones().values()) {
			AreaType areaType = AreaType.RURAL; //TODO: put real area type in here
			MitoZone zone = new MitoZone(siloZone.getId(), siloZone.getArea(), areaType);
			zones.put(zone.getId(), zone);
		}
		dataContainer.getJobData().fillMitoZoneEmployees(zones);

		HouseholdDataManager householdData = dataContainer.getHouseholdData();
		Map<Integer, MitoHousehold> households = householdData.convertHhs(zones);
		for(Person person: dataContainer.getHouseholdData().getPersons()) {
			int hhId = person.getHh().getId();
			if(households.containsKey(hhId)) {
				MitoPerson mitoPerson = householdData.convertToMitoPp(person);
				households.get(hhId).addPerson(mitoPerson);
			} else {
				logger.warn("Person " + person.getId() + " refers to non-existing household " + hhId
						+ " and will thus NOT be considered in the transport model.");
			}
		}
		
		Map<String, TravelTimes> travelTimes = modelContainer.getAcc().getTravelTimesByMode();
        logger.info("  SILO data being sent to MITO");
        InputFeed feed = new InputFeed(zones, travelTimes, households);
        mito.feedData(feed);
    }

    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }
}