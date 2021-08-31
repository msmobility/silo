package de.tum.bgu.msm.models.realEstate.renovation;


public class DefaultRenovationStrategy implements RenovationStrategy {

    public double calculateRenovationProbability(int quality, int newQuality) {

        if (quality == 1) {
            if (newQuality == 1) {
                return 0.;
            } else if (newQuality == 2) {
                return 0.0;
            } else if (newQuality == 3) {
                return 0.93;
            } else if (newQuality == 4) {
                return 0.05;
            } else if (newQuality == 5) {
                return 0.02;
            }
        } else if (quality == 2) {
            if (newQuality == 1) {
                return 0.;
            } else if (newQuality == 2) {
                return 0.1;
            } else if (newQuality == 3) {
                return 0.75;
            } else if (newQuality == 4) {
                return 0.1;
            } else if (newQuality == 5) {
                return 0.05;
            }
        } else if (quality == 3) {
            if (newQuality == 1) {
                return 0.05;
            } else if (newQuality == 2) {
                return 0.1;
            } else if (newQuality == 3) {
                return 0.75;
            } else if (newQuality == 4) {
                return 0.10;
            } else if (newQuality == 5) {
                return 0.;
            }
        } else if (quality == 4) {
            if (newQuality == 1) {
                return 0.02;
            } else if (newQuality == 2) {
                return 0.05;
            } else if (newQuality == 3) {
                return 0.93;
            } else if (newQuality == 4) {
                return 0.;
            } else if (newQuality == 5) {
                return 0.;
            }
        }
        throw new RuntimeException("Undefined dwelling quality!");
    }
}
