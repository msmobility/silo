package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;

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
        final IndexedDoubleMatrix2D peakTravelTimeMatrix = travelTimes.getPeakSkim(mode);
        OmxMatrixWriter.createOmxFile(path, peakTravelTimeMatrix.columns());
        OmxMatrixWriter.createOmxSkimMatrix(peakTravelTimeMatrix, path, name);
    }
}
