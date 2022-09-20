package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.MitoTrip;
import de.tum.bgu.msm.data.Mode;
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

import java.util.*;

public class HealthDataContainerImpl implements DataContainerWithSchools {
    private final static Logger logger = Logger.getLogger(HealthDataContainerImpl.class);

    private final DataContainerWithSchools delegate;
    private final Properties properties;
    private Map<Id<Link>, LinkInfo> linkInfo = new HashMap<>();
    private Set<Pollutant> pollutantSet = new HashSet<>();
    private EnumMap<Mode, EnumMap<MitoGender,Map<Integer,Double>>> avgSpeeds;

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
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        writePersonHealthData(properties.main.endYear);
    }

    private void writePersonHealthData(int year) {
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filepp = outputDirectory
                + properties.householdData.personFinalFileName
                + "_health_"
                + year
                + ".csv";
        new HealthPersonWriter(this).writePersons(filepp);
    }

    public Map<Id<Link>, LinkInfo> getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(Map<Id<Link>, LinkInfo> linkInfo) {
        this.linkInfo = linkInfo;
    }

    public Set<Pollutant> getPollutantSet() {
        return pollutantSet;
    }

    public void setPollutantSet(Set<Pollutant> pollutantSet) {
        this.pollutantSet = pollutantSet;
    }

    public EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> getAvgSpeeds() {
        return avgSpeeds;
    }

    public void setAvgSpeeds(EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> avgSpeeds) {
        this.avgSpeeds = avgSpeeds;
    }

    public void reset(){
        linkInfo.clear();
    }
}
