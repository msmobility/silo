package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.output.DefaultHouseholdWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolData;

public class NoiseDataContainerImpl implements DataContainerWithSchools {

    private final DataContainerWithSchools delegate;
    private final Properties properties;

    public NoiseDataContainerImpl(DataContainerWithSchools delegate,
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
        new NoiseDwellingWriter(delegate.getRealEstateDataManager()).writeDwellings(filedd);
        String fileHh = outputDirectory
                + properties.householdData.householdFinalFileName
                + "_"
                + year
                + ".csv";
        new DefaultHouseholdWriter(delegate.getHouseholdDataManager().getHouseholds()).writeHouseholds(fileHh);

        String filePp = outputDirectory
                + properties.householdData.personFinalFileName
                + "_"
                + year
                + ".csv";
        new PersonWriterMuc(delegate.getHouseholdDataManager()).writePersons(filePp);

    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        writeDwellingsWithNoise(properties.main.endYear);
    }

}
