package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataContainerHealth extends DataContainer {
    void writePersonExposureData(int year);

    void writePersonRelativeRiskData(int year);

    Map<Id<Link>, LinkInfo> getLinkInfo();

    void setLinkInfo(Map<Id<Link>, LinkInfo> linkInfo);

    Map<String, ActivityLocation> getActivityLocations();

    void setActivityLocations(Map<String, ActivityLocation> activityLocations);

    Set<Pollutant> getPollutantSet();

    void setPollutantSet(Set<Pollutant> pollutantSet);

    EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> getAvgSpeeds();

    void setAvgSpeeds(EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> avgSpeeds);

    EnumMap<Diseases, Map<String, Double>> getHealthTransitionData();

    void setHealthTransitionData(EnumMap<Diseases, Map<String, Double>> healthTransitionData);

    EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> getDoseResponseData();

    void setDoseResponseData(EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> doseResponseData);

    Map<Integer, Map<Integer, List<String>>> getHealthDiseaseTrackerRemovedPerson();

    void reset();

    String createTransitionLookupIndex(int age, Gender gender, String location);
}
