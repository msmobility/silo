package de.tum.bgu.msm.transportModel;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.tum.bgu.msm.container.SiloModelContainer;
import org.apache.log4j.Logger;

import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.MitoHousehold;
import de.tum.bgu.msm.data.MitoPerson;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.InputFeed;

/**
 * Implementation of Transport Model Interface for MITO
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 */
public class MitoTransportModel implements TransportModelI {
    private static final Logger logger = Logger.getLogger( MitoTransportModel.class );
	private final SiloModelContainer modelContainer;
	private MitoModel mito;
    private final GeoData geoData;

    public MitoTransportModel(ResourceBundle rb, String baseDirectory, GeoData geoData, SiloModelContainer modelContainer) {
        this.mito = new MitoModel(rb);
        this.geoData = geoData;
        this.modelContainer = modelContainer;
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        setBaseDirectory(baseDirectory);
    }

    @Override
    public void runTransportModel(int year) {
    	MitoModel.setScenarioName (SiloUtil.scenarioName);
    	updateData();
    	logger.info("  Running travel demand model MITO for the year " + year);
    	mito.runModel();
    }
    
    public void updateData() {
    	Map<Integer, Zone> zones = new HashMap<>();
		for (int i = 0; i < geoData.getZones().length; i++) {
			Zone zone = new Zone(geoData.getZones()[i], geoData.getSizeOfZonesInAcres()[i]);
			zone.setRetailEmpl(summarizeData.getRetailEmploymentByZone(geoData)[i]);
			zone.setOfficeEmpl(summarizeData.getOfficeEmploymentByZone(geoData)[i]);
			zone.setOtherEmpl(summarizeData.getOtherEmploymentByZone(geoData)[i]);
			zone.setTotalEmpl(summarizeData.getTotalEmploymentByZone(geoData)[i]);
			zones.put(zone.getZoneId(), zone);
		}

		Map<Integer, MitoHousehold> households = Household.convertHhs(zones);
		for(Person person: Person.getPersons()) {
			int hhId = person.getHhId();
			if(households.containsKey(hhId)) {
				MitoPerson mitoPerson = person.convertToMitoPp();
				households.get(hhId).addPerson(mitoPerson);
			} else {
				logger.warn("Person " + person.getId() + " refers to non-existing household " + hhId
						+ " and will thus NOT be considered in the transport model.");
			}
		}
		
		Map<String, TravelTimes> travelTimes = modelContainer.getAcc().getTravelTimes();
        logger.info("  SILO data being sent to MITO");
        InputFeed feed = new InputFeed(zones, travelTimes, households);
        mito.feedData(feed);
    }

    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }
}