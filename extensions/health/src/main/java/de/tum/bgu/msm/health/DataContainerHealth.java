package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.util.EnumMap;
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

    void reset();
}
