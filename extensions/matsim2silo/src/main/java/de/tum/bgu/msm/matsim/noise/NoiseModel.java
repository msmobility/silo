package de.tum.bgu.msm.matsim.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.noise.NoiseReceiverPoints;
import org.matsim.contrib.noise.ReceiverPoint;

import java.util.Random;

public class NoiseModel extends AbstractModel {

    private final NoiseDataManager noiseDataManager;

    public NoiseModel(NoiseDataContainer data, Properties properties, Random random) {
        super(data, properties, random);
        this.noiseDataManager = data.getNoiseData();
    }

    @Override
    public void setup() {
        updateNoiseImmissions();
    }

    @Override
    public void prepareYear(int year) {
        if (properties.transportModel.transportModelYears.contains(year)) {
            updateNoiseImmissions();
        }
    }

    private void updateNoiseImmissions() {
        //Transport model ran at end of last year
        final NoiseReceiverPoints noiseReceiverPoints = noiseDataManager.getNoiseReceiverPoints();
        for (Dwelling dwelling: dataContainer.getRealEstateDataManager().getDwellings()) {
            final Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
            if(noiseReceiverPoints.containsKey(id)) {
                ((NoiseDwelling) dwelling).setNoiseImmision(noiseReceiverPoints.get(id).getLden());
            }
        }
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {
    }
}
