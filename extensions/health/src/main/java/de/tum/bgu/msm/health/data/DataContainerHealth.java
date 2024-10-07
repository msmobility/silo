package de.tum.bgu.msm.health.data;

import cern.colt.map.tlong.OpenLongLongHashMap;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataContainerHealth {
    void writePersonHealthData(int year);

    Map<Id<Link>, LinkInfo> getLinkInfo();

    void setLinkInfo(Map<Id<Link>, LinkInfo> linkInfo);

    Set<Pollutant> getPollutantSet();

    void setPollutantSet(Set<Pollutant> pollutantSet);

    EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> getAvgSpeeds();

    void setAvgSpeeds(EnumMap<Mode, EnumMap<MitoGender, Map<Integer, Double>>> avgSpeeds);

    EnumMap<Diseases, EnumMap<Gender, Map<Integer, Double>>> getHealthTransitionData();

    void setHealthTransitionData(EnumMap<Diseases, EnumMap<Gender, Map<Integer, Double>>> healthTransitionData);

    EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> getDoseResponseData();

    void setDoseResponseData(EnumMap<HealthExposures, EnumMap<Diseases, TableDataSet>> doseResponseData);

    Map<Integer, Map<Integer, List<String>>> getHealthDiseaseTrackerRemovedPerson();

    void reset();
}
