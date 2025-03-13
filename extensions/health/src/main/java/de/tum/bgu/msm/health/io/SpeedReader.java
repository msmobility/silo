package de.tum.bgu.msm.health.io;

import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;

import java.util.EnumMap;
import java.util.Map;

public interface SpeedReader {

    EnumMap<Mode, EnumMap<MitoGender, Map<Integer,Double>>> readData(String path);


}
