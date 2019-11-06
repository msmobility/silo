package de.tum.bgu.msm.matsim.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.noise.NoiseReceiverPoints;
import org.matsim.contrib.noise.ReceiverPoint;

import java.util.Random;

public class NoiseModel extends AbstractModel implements ModelUpdateListener {

    private static final Logger logger = Logger.getLogger(NoiseModel.class);
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
        logger.info("Updating noise immisisons.");
        //Transport model ran at end of last year
        final NoiseReceiverPoints noiseReceiverPoints = noiseDataManager.getNoiseReceiverPoints();
        int counter65 = 0;
        int counter55 = 0;
        for (Dwelling dwelling: dataContainer.getRealEstateDataManager().getDwellings()) {
            final Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
            if(noiseReceiverPoints.containsKey(id)) {
                double lden = noiseReceiverPoints.get(id).getLden();
                ((NoiseDwelling) dwelling).setNoiseImmision(lden);
                if(lden > 55) {
                    if(lden > 65) {
                        counter65++;
                    } else {
                        counter55++;
                    }
                }
            }
        }
        int total = dataContainer.getRealEstateDataManager().getDwellings().size();
        int quiet = total - counter55 - counter65;
        logger.info("Dwellings <55dB(A) : " + quiet + " (" + ((double) quiet) / total + "%)");
        logger.info("Dwellings 55dB(A)-65dB(A) : " + counter55 + " (" + ((double) counter55) / total + "%)");
        logger.info("Dwellings >65dB(A) : " + counter65 + " (" + ((double) counter65) / total + "%)");
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {
    }
}
