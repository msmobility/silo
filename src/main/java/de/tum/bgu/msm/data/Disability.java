package de.tum.bgu.msm.data;

/**
 * Severe disability (50% or more degree of disability)
 * Author: Ana Moreno, TUM
 * Created on 09 June 2018 in Munich
 **/

public enum Disability implements Id{
    without,
    mental,
    physical;

    @Override
    public int getId(){return this.ordinal();}
}


