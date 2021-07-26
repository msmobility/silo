package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.SampleGenerator;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import org.locationtech.jts.geom.Coordinate;

import java.util.Random;

public class NoiseDwellingFactory implements DwellingFactory {

    private final Random random;
    private DwellingFactory delegate;
    private final Sampler<NKHedonicPricingModelState> stateSampler;

    public NoiseDwellingFactory(DwellingFactory delegate) {
        this.delegate = delegate;
        this.random = new Random(42);

        stateSampler = new Sampler<>(NKHedonicPricingModelState.values().length,
                NKHedonicPricingModelState.class, new Random(42));
        stateSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_STATE, 0.138);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.FIRST_TIME_USE_AFTER_RESTORATION_STATE, 0.08);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.WELL_KEPT_STATE, 0.291);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.MODERNIZED_STATE, 0.054);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.NEW_BUILDING_STATE, 0.243);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.RENOVATED_STATE, 0.165);
        stateSampler.incrementalAdd(NKHedonicPricingModelState.RESTORED_STATE, 0.027);
    }

    @Override
    public Dwelling createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, int year) {
        final NoiseDwellingIml noiseDwellingIml = new NoiseDwellingIml(delegate.createDwelling(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, year));
        if(type == DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus) {
            if(random.nextDouble() < 0.8) {
                noiseDwellingIml.setAttribute("parking_available", 1);
            } else {
                noiseDwellingIml.setAttribute("parking_available", 0);
            }
        } else {
            noiseDwellingIml.setAttribute("parking_available", 1);
        }

        try {
            final NKHedonicPricingModelState state = stateSampler.sampleObject();
            noiseDwellingIml.setAttribute("state", state);
        } catch (SampleException e) {
            e.printStackTrace();
        }

        return noiseDwellingIml;
    }
}
