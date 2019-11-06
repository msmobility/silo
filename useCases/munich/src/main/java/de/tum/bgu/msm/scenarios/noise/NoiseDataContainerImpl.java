package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.noise.NoiseDataManager;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolData;

public class NoiseDataContainerImpl implements de.tum.bgu.msm.matsim.noise.NoiseDataContainer, DataContainerWithSchools {

    private final DataContainerWithSchools delegate;
    private final NoiseDataManager noiseDataManager;
    private final Properties properties;

    public NoiseDataContainerImpl(DataContainerWithSchools delegate,
                                  NoiseDataManager noiseDataManager,
                                  Properties properties) {
        this.delegate = delegate;
        this.noiseDataManager = noiseDataManager;
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
        noiseDataManager.setup();
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
        noiseDataManager.setup();
        if (properties.transportModel.transportModelYears.contains(year)) {
            writeDwellingsWithNoise(year);
        }
    }

    private void writeDwellingsWithNoise(int year) {
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filedd = outputDirectory
                + properties.realEstate.dwellingsFinalFileName
                + "Noise_"
                + year
                + ".csv";
        new NoiseDwellingWriter(this).writeDwellings(filedd);
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
        noiseDataManager.setup();
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        noiseDataManager.endSimulation();
        writeDwellingsWithNoise(properties.main.endYear);
    }

    @Override
    public NoiseDataManager getNoiseData() {
        return noiseDataManager;
    }
}
