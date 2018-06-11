package de.tum.bgu.msm.data;

/**
 * Severe disability (50% or more degree of disability)
 * Author: Ana Moreno, TUM
 * Created on 09 June 2018 in Munich
 **/

public enum Disability {
    without(0),
    mental(1),
    physical(2);

    private final int disabilityCode;

    Disability(int disabilityCode) {
        this.disabilityCode = disabilityCode;
    }

    public int getDisabilityCode() {
        return disabilityCode;
    }
}


