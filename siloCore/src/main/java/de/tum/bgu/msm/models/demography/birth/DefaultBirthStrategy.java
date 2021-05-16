package de.tum.bgu.msm.models.demography.birth;

public class DefaultBirthStrategy implements BirthStrategy {

    public DefaultBirthStrategy() {
    }

    @Override
    public double calculateBirthProbability(int personAge, int numberOfChildren) {
        var alpha = 0.;
        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age!"+personAge);
        }

        if (numberOfChildren == 0){
            if (personAge <= 14){
                alpha = 0.0;
            } else if (personAge == 15){
                alpha = 1.5;
            } else if (personAge == 16){
                alpha = 4.4;
            } else if (personAge == 17){
                alpha = 9.2;
            } else if (personAge == 18){
                alpha = 16.2;
            } else if (personAge == 19){
                alpha = 26.7;
            } else if (personAge == 20){
                alpha = 34.3;
            } else if (personAge == 21){
                alpha = 41.4;
            } else if (personAge == 22){
                alpha = 47.1;
            } else if (personAge == 23){
                alpha = 53.8;
            } else if (personAge == 24){
                alpha = 61.5;
            } else if (personAge == 25){
                alpha = 75.8;
            } else if (personAge == 26){
                alpha = 89.0;
            } else if (personAge == 27){
                alpha = 104.4;
            } else if (personAge == 28){
                alpha = 114.4;
            } else if (personAge == 29){
                alpha = 124.7;
            } else if (personAge == 30){
                alpha = 127.8;
            } else if (personAge == 31){
                alpha = 127.0;
            } else if (personAge == 32){
                alpha = 116.1;
            } else if (personAge == 33){
                alpha = 101.9;
            } else if (personAge == 34){
                alpha = 86.6;
            } else if (personAge == 35){
                alpha = 72.5;
            } else if (personAge == 36){
                alpha = 60.3;
            } else if (personAge == 37){
                alpha = 46.9;
            } else if (personAge == 38){
                alpha = 35.6;
            } else if (personAge == 39){
                alpha = 27.6;
            } else if (personAge == 40){
                alpha = 20.7;
            } else if (personAge == 41){
                alpha = 13.6;
            } else if (personAge == 42){
                alpha = 8.5;
            } else if (personAge == 43){
                alpha = 4.9;
            } else if (personAge == 44){
                alpha = 2.7;
            } else if (personAge == 45){
                alpha = 1.6;
            } else if (personAge == 46){
                alpha = 0.9;
            } else if (personAge == 47){
                alpha = 0.4;
            } else if (personAge == 48){
                alpha = 0.2;
            } else if (personAge == 49){
                alpha = 0.1;
            } else {
                alpha = 0.0;
            }
        } else if (numberOfChildren == 1){
            if (personAge <= 14){
                alpha = 0.0;
            } else if (personAge == 15){
                alpha = 0.02;
            } else if (personAge == 16){
                alpha = 0.2;
            } else if (personAge == 17){
                alpha = 0.7;
            } else if (personAge == 18){
                alpha = 2.4;
            } else if (personAge == 19){
                alpha = 5.5;
            } else if (personAge == 20){
                alpha = 9.5;
            } else if (personAge == 21){
                alpha = 15.2;
            } else if (personAge == 22){
                alpha = 20.5;
            } else if (personAge == 23){
                alpha = 24.0;
            } else if (personAge == 24){
                alpha = 29.4;
            } else if (personAge == 25){
                alpha = 36.1;
            } else if (personAge == 26){
                alpha = 43.5;
            } else if (personAge == 27){
                alpha = 52.1;
            } else if (personAge == 28){
                alpha = 62.6;
            } else if (personAge == 29){
                alpha = 72.9;
            } else if (personAge == 30){
                alpha = 84.2;
            } else if (personAge == 31){
                alpha = 91.9;
            } else if (personAge == 32){
                alpha = 96.9;
            } else if (personAge == 33){
                alpha = 98.5;
            } else if (personAge == 34){
                alpha = 94.0;
            } else if (personAge == 35){
                alpha = 86.3;
            } else if (personAge == 36){
                alpha = 76.0;
            } else if (personAge == 37){
                alpha = 62.2;
            } else if (personAge == 38){
                alpha = 49.0;
            } else if (personAge == 39){
                alpha = 36.9;
            } else if (personAge == 40){
                alpha = 27.1;
            } else if (personAge == 41){
                alpha = 17.7;
            } else if (personAge == 42){
                alpha = 10.5;
            } else if (personAge == 43){
                alpha = 5.8;
            } else if (personAge == 44){
                alpha = 2.8;
            } else if (personAge == 45){
                alpha = 1.6;
            } else if (personAge == 46){
                alpha = 0.7;
            } else if (personAge == 47){
                alpha = 0.3;
            } else if (personAge == 48){
                alpha = 0.2;
            } else if (personAge == 49){
                alpha = 0.1;
            } else {
                alpha = 0.0;
            }
        } else if (numberOfChildren == 2){
            if (personAge <= 14){
                alpha = 0.0;
            } else if (personAge == 15){
                alpha = 0.0;
            } else if (personAge == 16){
                alpha = 0.01;
            } else if (personAge == 17){
                alpha = 0.04;
            } else if (personAge == 18){
                alpha = 0.2;
            } else if (personAge == 19){
                alpha = 0.7;
            } else if (personAge == 20){
                alpha = 1.4;
            } else if (personAge == 21){
                alpha = 2.9;
            } else if (personAge == 22){
                alpha = 4.9;
            } else if (personAge == 23){
                alpha = 6.7;
            } else if (personAge == 24){
                alpha = 8.9;
            } else if (personAge == 25){
                alpha = 10.3;
            } else if (personAge == 26){
                alpha = 13.5;
            } else if (personAge == 27){
                alpha = 15.3;
            } else if (personAge == 28){
                alpha = 17.5;
            } else if (personAge == 29){
                alpha = 21.3;
            } else if (personAge == 30){
                alpha = 24.1;
            } else if (personAge == 31){
                alpha = 27.1;
            } else if (personAge == 32){
                alpha = 29.8;
            } else if (personAge == 33){
                alpha = 31.8;
            } else if (personAge == 34){
                alpha = 32.0;
            } else if (personAge == 35){
                alpha = 32.3;
            } else if (personAge == 36){
                alpha = 30.9;
            } else if (personAge == 37){
                alpha = 27.3;
            } else if (personAge == 38){
                alpha = 23.7;
            } else if (personAge == 39){
                alpha = 19.3;
            } else if (personAge == 40){
                alpha = 14.2;
            } else if (personAge == 41){
                alpha = 9.7;
            } else if (personAge == 42){
                alpha = 6.3;
            } else if (personAge == 43){
                alpha = 3.6;
            } else if (personAge == 44){
                alpha = 1.8;
            } else if (personAge == 45){
                alpha = 1.0;
            } else if (personAge == 46){
                alpha = 0.4;
            } else if (personAge == 47){
                alpha = 0.25;
            } else if (personAge == 48){
                alpha = 0.1;
            } else if (personAge == 49){
                alpha = 0.05;
            } else {
                alpha = 0.0;
            }
        } else {
            if (personAge <= 14){
                alpha = 0.0;
            } else if (personAge == 15){
                alpha = 0.0;
            } else if (personAge == 16){
                alpha = 0.0;
            } else if (personAge == 17){
                alpha = 0.0;
            } else if (personAge == 18){
                alpha = 0.02;
            } else if (personAge == 19){
                alpha = 0.06;
            } else if (personAge == 20){
                alpha = 0.2;
            } else if (personAge == 21){
                alpha = 0.5;
            } else if (personAge == 22){
                alpha = 1.0;
            } else if (personAge == 23){
                alpha = 2.0;
            } else if (personAge == 24){
                alpha = 2.8;
            } else if (personAge == 25){
                alpha = 4.0;
            } else if (personAge == 26){
                alpha = 5.3;
            } else if (personAge == 27){
                alpha = 6.3;
            } else if (personAge == 28){
                alpha = 7.3;
            } else if (personAge == 29){
                alpha = 9.5;
            } else if (personAge == 30){
                alpha = 10.7;
            } else if (personAge == 31){
                alpha = 12.2;
            } else if (personAge == 32){
                alpha = 13.9;
            } else if (personAge == 33){
                alpha = 14.0;
            } else if (personAge == 34){
                alpha = 14.3;
            } else if (personAge == 35){
                alpha = 15.1;
            } else if (personAge == 36){
                alpha = 15.1;
            } else if (personAge == 37){
                alpha = 14.7;
            } else if (personAge == 38){
                alpha = 13.2;
            } else if (personAge == 39){
                alpha = 11.6;
            } else if (personAge == 40){
                alpha = 9.9;
            } else if (personAge == 41){
                alpha = 7.2;
            } else if (personAge == 42){
                alpha = 5.0;
            } else if (personAge == 43){
                alpha = 3.5;
            } else if (personAge == 44){
                alpha = 2.0;
            } else if (personAge == 45){
                alpha = 0.9;
            } else if (personAge == 46){
                alpha = 0.5;
            } else if (personAge == 47){
                alpha = 0.2;
            } else if (personAge == 48){
                alpha = 0.1;
            } else if (personAge == 49){
                alpha = 0.05;
            } else {
                alpha = 0.0;
            }
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