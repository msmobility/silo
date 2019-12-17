package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.Random;

public class CreateCarOwnershipStrategyTak implements CreateCarOwnershipStrategy {

    private final static Integer[] options = {0,1,2,3};
    private final static double[] betaLicense = {3.11410, 4.65460, 5.71850};
    private final static double[] betaWorkers = {0.22840, 0.76420, 1.13260};
    private final static double[] betaIncome = {0.00070, 0.00100, 0.00120};
    private final static double[] betaDistance = {0.15230, 0.25180, 0.25930};
    private final static double[][] betasAreaType = {
            {0., 0.88210, 0.99270, 1.34420},
            {0., 1.43410, 1.60730, 2.25490},
            {0., 1.69830, 1.91330, 2.93080}
    };

    private final static double[] intercept = {-4.69730, -10.98800, -17.00200};

    private final Random random;

    public CreateCarOwnershipStrategyTak() {
        this.random = SiloUtil.getRandomObject();
    }

    @Override
    public Sampler<Integer> getSampler(int license, int workers, int income, double logDistanceToTransit, int areaType) {

        double[] probs = new double[options.length];
        double sum = 0;
        for(int i = 0; i < options.length-1; i++) {
            double utility = intercept[i] + (betaLicense[i] * license) + (betaWorkers[i] * workers) + (betaIncome[i] * income) + (betaDistance[i] * logDistanceToTransit) + betasAreaType[i][areaType/10 -1];
            double result = Math.exp(utility);
            sum += result;
            probs[i+1] = result;
        }

        double prob0cars = 1.0 / (sum + 1.0);

        sum = 0;
        for(int i = 0; i < options.length-1; i++) {
            probs[i+1] = probs[i+1] * prob0cars;
            sum += probs[i+1];
        }

        probs[0] = 1 - sum;

        return new Sampler<>(options, probs, random);
    }
}
