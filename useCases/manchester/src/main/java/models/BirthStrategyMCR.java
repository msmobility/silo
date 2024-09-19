package models;

import de.tum.bgu.msm.models.demography.birth.BirthStrategy;

public class BirthStrategyMCR implements BirthStrategy {

    public BirthStrategyMCR() {
    }

    @Override
    public double calculateBirthProbability(int personAge, int numberOfChildren) {
        var alpha = 0.;
        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age!"+personAge);
        }

        if (personAge <= 14){
            alpha = 0.0;
        } else if (personAge == 15){
            alpha = 13.3;
        } else if (personAge == 16){
            alpha = 13.3;
        } else if (personAge == 17){
            alpha = 13.3;
        } else if (personAge == 18){
            alpha = 13.3;
        } else if (personAge == 19){
            alpha = 13.3;
        } else if (personAge == 20){
            alpha = 52.8;
        } else if (personAge == 21){
            alpha = 52.8;
        } else if (personAge == 22){
            alpha = 52.8;
        } else if (personAge == 23){
            alpha = 52.8;
        } else if (personAge == 24){
            alpha = 52.8;
        } else if (personAge == 25){
            alpha = 92.3;
        } else if (personAge == 26){
            alpha = 92.3;
        } else if (personAge == 27){
            alpha = 92.3;
        } else if (personAge == 28){
            alpha = 92.3;
        } else if (personAge == 29){
            alpha = 92.3;
        } else if (personAge == 30){
            alpha = 105.1;
        } else if (personAge == 31){
            alpha = 105.1;
        } else if (personAge == 32){
            alpha = 105.1;
        } else if (personAge == 33){
            alpha = 105.1;
        } else if (personAge == 34){
            alpha = 105.1;
        } else if (personAge == 35){
            alpha = 62.3;
        } else if (personAge == 36){
            alpha = 62.3;
        } else if (personAge == 37){
            alpha = 62.3;
        } else if (personAge == 38){
            alpha = 62.3;
        } else if (personAge == 39){
            alpha = 62.3;
        } else if (personAge == 40){
            alpha = 15.4;
        } else if (personAge == 41){
            alpha = 15.4;
        } else if (personAge == 42){
            alpha = 15.4;
        } else if (personAge == 43){
            alpha = 15.4;
        } else if (personAge == 44){
            alpha = 15.4;
        } else if (personAge == 45){
            alpha = 0;
        } else if (personAge == 46){
            alpha = 0;
        } else if (personAge == 47){
            alpha = 0;
        } else if (personAge == 48){
            alpha = 0;
        } else if (personAge == 49){
            alpha = 0;
        } else {
            alpha = 0.0;
        }

        // Birth probabilities are provided as birth per 1,000 women
        alpha = alpha / 1000.;
        return alpha;
    }

    @Override
    public double getProbabilityForGirl() {
        return 0.4867;
    }
}