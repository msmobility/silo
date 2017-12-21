package de.tum.bgu.msm.data;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.util.concurrent.RandomizableConcurrentFunction;

public class IncomeAdjustment extends RandomizableConcurrentFunction {

    private final Person person;
    private final float meanIncomeChange;
    private final float desiredShift;


    public IncomeAdjustment(Person person, float desiredShift, float meanIncomeChange) {
        super(SiloUtil.getRandomObject().nextLong());
        this.person = person;
        this.desiredShift = desiredShift;
        this.meanIncomeChange = meanIncomeChange;
    }

    @Override
    public void execute() {
        // adjust income of person with ID per
        int newIncome = selectNewIncome();
        person.setIncome(newIncome);
    }

    private int selectNewIncome () {
        // calculate new income using a normal distribution

        double[] prob = new double[21];
        int lowerBound;
        int upperBound;
        if (Math.abs(desiredShift) < 1000) {
            lowerBound = -5000;
            upperBound = 5000;
        } else if (desiredShift > 1000) {
            lowerBound = (int) -desiredShift;
            upperBound = (int) desiredShift * 3;
        } else {
            lowerBound = (int) desiredShift * 3;
            upperBound = (int) -desiredShift;
        }
        int smallestAbsValuePos = 0;
        float smallestAbsValue = Float.MAX_VALUE;
        for (int i = 0; i < prob.length; i++) {
            int change = lowerBound + (upperBound - lowerBound) / (prob.length-1) * i;
            if (Math.abs(change) < smallestAbsValue) {
                smallestAbsValuePos = i;
                smallestAbsValue = Math.abs(change);
            }
            // normal distribution to calculate change of income
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) * Math.exp(-(Math.pow((desiredShift - change), 2) /
                    (2 * Math.pow(meanIncomeChange, 2))));
        }
        prob[smallestAbsValuePos] = prob[smallestAbsValuePos] * 10;   // make no change most likely
        int sel = SiloUtil.select(prob, random);
        return Math.max((person.getIncome() + lowerBound + (upperBound - lowerBound) / prob.length * sel), 0);
    }
}
