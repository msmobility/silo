package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoTrip;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolData;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.accidents.AccidentLinkInfo;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HealthDataContainerImpl implements DataContainerWithSchools {
    private final static Logger logger = Logger.getLogger(HealthDataContainerImpl.class);

    private final DataContainerWithSchools delegate;
    private final Properties properties;
    private Map<Integer, MitoTrip> mitoTrips = new HashMap<>();
    private Map<Day, Map<Id<Link>, LinkInfo>> linkInfoByDay = new HashMap<>();
    private Set<Pollutant> pollutantSet = new HashSet<>();

    public HealthDataContainerImpl(DataContainerWithSchools delegate,
                                   Properties properties) {
        this.delegate = delegate;
        this.properties = properties;
    }

    @Override
    public SchoolData getSchoolData() {
        return delegate.getSchoolData();
    }

    @Override
    public HouseholdDataManager getHouseholdDataManager() {
        return delegate.getHouseholdDataManager();
    }

    @Override
    public RealEstateDataManager getRealEstateDataManager() {
        return delegate.getRealEstateDataManager();
    }

    @Override
    public JobDataManager getJobDataManager() {
        return delegate.getJobDataManager();
    }

    @Override
    public GeoData getGeoData() {
        return delegate.getGeoData();
    }

    @Override
    public TravelTimes getTravelTimes() {
        return delegate.getTravelTimes();
    }

    @Override
    public Accessibility getAccessibility() {
        return delegate.getAccessibility();
    }

    @Override
    public CommutingTimeProbability getCommutingTimeProbability() {
        return delegate.getCommutingTimeProbability();
    }

    @Override
    public void setup() {
        delegate.setup();
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
        if (properties.transportModel.transportModelYears.contains(year) || properties.main.startYear == year) {
            writeInjuryRiskData(year);
        }
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        writeInjuryRiskData(properties.main.endYear);
    }

    private void writeInjuryRiskData(int year) {
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filepp = outputDirectory
                + properties.householdData.personFinalFileName
                + "accident_"
                + year
                + ".csv";
        new HealthPersonWriter(getHouseholdDataManager()).writePersons(filepp);
    }


    public Map<Integer, MitoTrip> getMitoTrips() {
        return mitoTrips;
    }

    public void setMitoTrips(Map<Integer, MitoTrip> mitoTrips) {
        this.mitoTrips = mitoTrips;
    }

    public Map<Day, Map<Id<Link>, LinkInfo>> getLinkInfoByDay() {
        return linkInfoByDay;
    }

    public Set<Pollutant> getPollutantSet() {
        return pollutantSet;
    }

    public void setPollutantSet(Set<Pollutant> pollutantSet) {
        this.pollutantSet = pollutantSet;
    }
}
