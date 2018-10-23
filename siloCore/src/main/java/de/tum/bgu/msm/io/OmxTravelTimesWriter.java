package de.tum.bgu.msm.io;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.output.OmxMatrixWriter;
import de.tum.bgu.msm.utils.TravelTimeUtil;

import java.util.Collection;

public class OmxTravelTimesWriter implements TravelTimesWriter {

    private final TravelTimes travelTimes;
    private final Collection<Zone> zones;

    public OmxTravelTimesWriter(TravelTimes travelTimes, Collection<Zone> zones) {
        this.travelTimes = travelTimes;
        this.zones = zones;
    }

    @Override
    public void writeTravelTimes(String path, String name, String mode) {
        final DoubleMatrix2D peakTravelTimeMatrix = TravelTimeUtil.getPeakTravelTimeMatrix(mode, travelTimes, zones);
        OmxMatrixWriter.createOmxFile(path, zones.stream().mapToInt(Id::getId).max().getAsInt()+1);
        OmxMatrixWriter.createOmxSkimMatrix(peakTravelTimeMatrix, path, name);
    }
}
