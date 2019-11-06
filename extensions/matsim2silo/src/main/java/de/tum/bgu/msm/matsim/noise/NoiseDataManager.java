package de.tum.bgu.msm.matsim.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.UpdateListener;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.noise.NoiseReceiverPoint;
import org.matsim.contrib.noise.NoiseReceiverPoints;
import org.matsim.contrib.noise.ReceiverPoint;
import org.matsim.core.utils.geometry.CoordUtils;

public class NoiseDataManager implements UpdateListener {

    private final NoiseReceiverPoints noiseReceiverPoints;
    private final DwellingData dwellingData;
    private final Properties properties;

    public NoiseDataManager(DwellingData dwellingData, Properties properties) {
        this.dwellingData = dwellingData;
        this.properties = properties;
        this.noiseReceiverPoints = new NoiseReceiverPoints();
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {
        noiseReceiverPoints.clear();
    }

    public NoiseReceiverPoints getNoiseReceiverPoints() {
        NoiseReceiverPoints newNoiseReceiverPoints = new NoiseReceiverPoints();
        for(Dwelling dwelling: dwellingData.getDwellings()) {
            Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
            final NoiseReceiverPoint existing = noiseReceiverPoints.remove(id);
            if(existing == null) {
                newNoiseReceiverPoints.put(id, new NoiseReceiverPoint(id, CoordUtils.createCoord(dwelling.getCoordinate())));
            } else {
                newNoiseReceiverPoints.put(id, existing);
            }
        }
        this.noiseReceiverPoints.clear();
        this.noiseReceiverPoints.putAll(newNoiseReceiverPoints);
        return noiseReceiverPoints;
    }
}
