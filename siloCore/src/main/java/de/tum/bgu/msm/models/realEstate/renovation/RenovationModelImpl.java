package de.tum.bgu.msm.models.realEstate.renovation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.events.impls.realEstate.RenovationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.*;

/**
 * Simulates renovation and deterioration of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 January 2010 in Rhede
 **/
public class RenovationModelImpl extends AbstractModel implements RenovationModel {

    private final RenovationStrategy strategy;
    private double[][] renovationProbability;

    private enum DdQualityChange {
        DECREASE_2 {
            @Override
            int getChange() {
                return -2;
            }
        }, DECREASE_1 {
            @Override
            int getChange() {
                return -1;
            }
        }, UNCHANGED {
            @Override
            int getChange() {
                return 0;
            }
        }, IMPROVE_1 {
            @Override
            int getChange() {
                return 1;
            }
        }, IMPROVE_2 {
            @Override
            int getChange() {
                return 2;
            }
        };

        abstract int getChange();
    }

    public RenovationModelImpl(DataContainer dataContainer, Properties properties, RenovationStrategy strategy, Random random) {
        super(dataContainer, properties, random);
        this.strategy = strategy;
    }

    @Override
    public void setup() {
        renovationProbability = new double[properties.main.qualityLevels][5];
        for (int oldQual = 0; oldQual < properties.main.qualityLevels; oldQual++) {
            for (int alternative = 0; alternative < 5; alternative++) {
                renovationProbability[oldQual][alternative] = strategy.calculateRenovationProbability(oldQual + 1, alternative + 1);
            }
        }
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<RenovationEvent> getEventsForCurrentYear(int year) {
        final List<RenovationEvent> events = new ArrayList<>();
        for (Dwelling dwelling : dataContainer.getRealEstateDataManager().getDwellings()) {
            events.add(new RenovationEvent(dwelling.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(RenovationEvent event) {

        //check if dwelling is renovated or deteriorates
        final RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        Dwelling dd = realEstateDataManager.getDwelling(event.getDwellingId());
        if (dd != null) {
            int currentQuality = dd.getQuality();
            DdQualityChange change = SiloUtil.select(getProbabilities(currentQuality), random);
            dd.setQuality(currentQuality + change.getChange());
            return true;
        }
        return false;
    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    private Map<DdQualityChange, Double> getProbabilities(int currentQual) {
        // return probabilities to upgrade or deteriorate based on current quality of dwelling and average
        // quality of all dwellings
        Map<Integer, Double> currentShare = dataContainer.getRealEstateDataManager().getUpdatedQualityShares();
        Map<Integer, Double> initialShare = dataContainer.getRealEstateDataManager().getInitialQualShares();

        Map<DdQualityChange, Double> probs = new EnumMap<>(DdQualityChange.class);
        for (DdQualityChange change: DdQualityChange.values()) {
            int potentialNewQual = currentQual + change.getChange();
            potentialNewQual = Math.min(Math.max(1, potentialNewQual), properties.main.qualityLevels);

            double ratio = initialShare.getOrDefault(potentialNewQual, 0.01)
                    / currentShare.getOrDefault(potentialNewQual, 0.01);

            double prob = renovationProbability[currentQual - 1][change.ordinal()] * ratio;
            probs.put(change, prob);
        }
        return probs;
    }
}

