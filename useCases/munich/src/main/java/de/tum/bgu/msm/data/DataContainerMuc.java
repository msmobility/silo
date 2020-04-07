package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.DwellingWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.DataContainerWithSchoolsImpl;
import de.tum.bgu.msm.schools.SchoolData;

import java.util.ArrayList;
import java.util.List;


public class DataContainerMuc implements DataContainerWithSchools {

    private final DataContainerWithSchools delegate;
    private Properties properties;


    public DataContainerMuc(
            GeoData geoData, RealEstateDataManager realEstateDataManager,
            JobDataManager jobDataManager, HouseholdDataManager householdDataManager,
            TravelTimes travelTimes, Accessibility accessibility,
            CommutingTimeProbability commutingTimeProbability,
            SchoolData schoolData, Properties properties) {

        delegate = new DataContainerWithSchoolsImpl(geoData, realEstateDataManager, jobDataManager, householdDataManager,
                travelTimes, accessibility, commutingTimeProbability, schoolData, properties);
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

        List<Integer> years = new ArrayList<>();
        years.add(2015);
        years.add(2020);
        years.add(2025);
        years.add(2030);
        years.add(2035);
        years.add(2040);
        years.add(2045);

        if (years.contains(year)) {
            String filepp = properties.main.baseDirectory
                    + properties.householdData.personFinalFileName
                    + "_"
                    + year
                    + ".csv";
            new PersonWriterMuc(delegate.getHouseholdDataManager()).writePersons(filepp);

            String filedd = Properties.get().main.baseDirectory
                    + properties.realEstate.dwellingsFinalFileName
                    + "_"
                    + year
                    + ".csv";
            new DwellingWriterMuc(this).writeDwellings(filedd);
        }


    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
        String filepp = properties.main.baseDirectory
                + properties.householdData.personFinalFileName
                + "_"
                + properties.main.endYear
                + ".csv";
        new PersonWriterMuc(delegate.getHouseholdDataManager()).writePersons(filepp);

        String filedd = Properties.get().main.baseDirectory
                + properties.realEstate.dwellingsFinalFileName
                + "_"
                + properties.main.endYear
                + ".csv";
        new DwellingWriterMuc(this).writeDwellings(filedd);


    }
}
