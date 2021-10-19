package de.tum.bgu.msm.models.demography.divorce;

import de.tum.bgu.msm.data.person.Person;

public class DefaultDivorceStrategy implements DivorceStrategy {

    public DefaultDivorceStrategy() {
    }

    @Override
    public double calculateDivorceProbability(Person per) {
        var alpha = 0.;

        int personType = per.getType().ordinal();

        if (personType == 4){
            alpha = 0.0071;
        } else if (personType == 5){
            alpha = 0.0193;
        } else if (personType == 6){
            alpha = 0.0199;
        } else if (personType == 7){
            alpha = 0.0185;
        } else if (personType == 8){
            alpha = 0.0180;
        } else if (personType == 9){
            alpha = 0.0164;
        } else if (personType == 10){
            alpha = 0.0154;
        } else if (personType == 11){
            alpha = 0.0125;
        } else if (personType == 12){
            alpha = 0.0078;
        } else if (personType == 13){
            alpha = 0.0041;
        } else if (personType == 14){
            alpha = 0.0024;
        } else if (personType == 15){
            alpha = 0.0011;
        } else if (personType >= 16 && personType <= 21){
            alpha = 0.0004;
        } else if (personType == 26){
            alpha = 0.0066;
        } else if (personType == 27){
            alpha = 0.0214;
        } else if (personType == 28){
            alpha = 0.0204;
        } else if (personType == 29){
            alpha = 0.0184;
        } else if (personType == 30){
            alpha = 0.0176;
        } else if (personType == 31){
            alpha = 0.0156;
        } else if (personType == 32){
            alpha = 0.0142;
        } else if (personType == 33){
            alpha = 0.0100;
        } else if (personType == 34){
            alpha = 0.0053;
        } else if (personType == 35){
            alpha = 0.0026;
        } else if (personType == 36){
            alpha = 0.0015;
        } else if (personType == 37){
            alpha = 0.0006;
        } else if (personType >= 38 && personType <= 43){
            alpha = 0.0002;
        }
        return alpha;
    }
}
