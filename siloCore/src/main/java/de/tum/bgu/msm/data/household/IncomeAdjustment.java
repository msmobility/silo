package de.tum.bgu.msm.data.household;

import cern.jet.random.tdouble.Normal;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.util.concurrent.RandomizableConcurrentFunction;

public class IncomeAdjustment extends RandomizableConcurrentFunction<Void> {

    private final Person person;
    private final float meanIncomeChange;
    private final float[][][] currentIncomeDistribution;
    private final float[][][] initialIncomeDistribution;


    IncomeAdjustment(Person person, float meanIncomeChange,
                     float[][][] currentIncomeDistribution, float[][][] initialIncomeDistribution) {
        super(SiloUtil.getRandomObject().nextLong());
        this.person = person;
        this.meanIncomeChange = meanIncomeChange;
        this.currentIncomeDistribution = currentIncomeDistribution;
        this.initialIncomeDistribution = initialIncomeDistribution;
    }

    @Override
    public Void call() {
        // adjust income of person with ID per
        person.setIncome(selectNewIncome());
        return null;
    }

    private int selectNewIncome () {
        // calculate new income using a normal distribution

        float desiredShift = getDesiredShift();

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

        // normal distribution to calculate change of income
        Normal normal = new Normal(0,0, DoubleRandomEngine.makeDefault());
        for (int i = 0; i < prob.length; i++) {
            int change = lowerBound + (upperBound - lowerBound) / (prob.length-1) * i;
            if (Math.abs(change) < smallestAbsValue) {
                smallestAbsValuePos = i;
                smallestAbsValue = Math.abs(change);
            }
            normal.setState(change, meanIncomeChange);
            prob[i] = normal.pdf(desiredShift);
        }
        prob[smallestAbsValuePos] = prob[smallestAbsValuePos] * 10;   // make no change most likely
        int sel = SiloUtil.select(prob, random);
        return Math.max((person.getAnnualIncome() + lowerBound + (upperBound - lowerBound) / prob.length * sel), 0);
    }

    private float getDesiredShift() {
        int gender = person.getGender().ordinal();
        int age = Math.min(99, person.getAge());
        int occ = 0;
        if (person.getOccupation() == Occupation.EMPLOYED) {
            occ = 1;
        }
        return initialIncomeDistribution[gender][age][occ] - currentIncomeDistribution[gender][age][occ];
    }
}
