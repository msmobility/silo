package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.utils.Sampler;

interface CreateCarOwnershipStrategyImpl {
    Sampler<Integer> getSampler(int license, int workers, int income, double logDistanceToTransit, int areaType);
}
