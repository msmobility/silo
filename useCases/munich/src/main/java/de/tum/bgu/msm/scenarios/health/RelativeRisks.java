package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import org.matsim.contrib.emissions.Pollutant;

import java.util.Collection;

// Dose-response functions for health exposures (simple for now but will become more complex)
public class RelativeRisks {

    public static double walk(double mMEThrs) {
        return Math.exp(Math.log(0.9)*mMEThrs/8.75);
    }

    public static double bike(double mMEThrs) {
        return Math.exp(Math.log(0.9)*mMEThrs/8.75);
    }

    public static double no2(double microgramPerM3) {
        return 1 + 0.008 * microgramPerM3;
    }

    public static double pm25(double microgramPerM3) {
        return 1 + 0.002 * microgramPerM3 + (microgramPerM3 > 500 ? .01 : 0);
    }

    public static double accident(double risk) {
        return 1 - risk;
    }
}
