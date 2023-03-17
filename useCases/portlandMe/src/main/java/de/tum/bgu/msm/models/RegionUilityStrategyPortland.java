package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RegionUilityStrategyPortland {

    double calculateRegionUtility(IncomeCategory incomeCategory, Race race, float price,
                                  float accessibility, float share, float schoolQuality, float crimeRate, double nicheRank) {

        double delta; //relevant share
        double alpha; //accessibility
        double beta; // school quality
        double gamma; // crime rate
        double epsilon; //niche rank

        delta = 0.;
        if (incomeCategory.equals(IncomeCategory.LOW)) {
            alpha = 0.05;
            //beta = 0.05;
            //gamma = 0.05;
            epsilon = 0.00;
            if (race.equals(Race.white)) {
                delta = 0.55;
            } else if (race.equals(Race.black)) {
                delta = 0.25;
            } else if (race.equals(Race.hispanic)) {
                delta = 0.5;
            }
        } else if (incomeCategory.equals(IncomeCategory.MEDIUM)) {
            alpha = 0.08;
            //beta = 0.1;
            //gamma = 0.1;
            epsilon = 0.03;
            if (race.equals(Race.white)) {
                delta = 0.5;
            } else if (race.equals(Race.black)) {
                delta = 0.25;
            } else if (race.equals(Race.hispanic)) {
                delta = 0.45;
            }
        } else if (incomeCategory.equals(IncomeCategory.HIGH)) {
            alpha = 0.12;
            //beta = 0.2;
            //gamma = 0.15;
            epsilon = 0.06;
            if (race.equals(Race.white)) {
                delta = 0.45;
            } else if (race.equals(Race.black)) {
                delta = 0.15;
            } else if (race.equals(Race.hispanic)) {
                delta = 0.25;
            }
        } else if (incomeCategory.equals(IncomeCategory.VERY_HIGH)) {
            alpha = 0.25;
            //beta = 0.3;
            //gamma = 0.25;
            epsilon = 0.15;
            if (race.equals(Race.white)) {
                delta = 0.25;
            } else if (race.equals(Race.black)) {
                delta = 0.1;
            } else if (race.equals(Race.hispanic)) {
                delta = 0.15;
            }
        } else {
            throw new Error("Undefined income group: " + incomeCategory);
        }

        double relevantShare;

        if (race.equals(Race.white) || race.equals(Race.black) || race.equals(Race.hispanic)) {
            relevantShare = share;
        } else if (race.equals(Race.other)) {
            relevantShare = 1.0;
        } else {
            throw new Error("Undefined race: " + race);
        }


        return (1 - alpha - delta - epsilon) * price + alpha * accessibility + delta * relevantShare + epsilon * nicheRank;

    }

}
