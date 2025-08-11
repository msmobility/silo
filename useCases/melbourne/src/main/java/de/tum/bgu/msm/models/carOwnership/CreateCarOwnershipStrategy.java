package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.utils.Sampler;

public interface CreateCarOwnershipStrategy {
    Sampler<Integer> getSampler(int license, int workers, int income, double logDistanceToTransit, int areaType);
}
