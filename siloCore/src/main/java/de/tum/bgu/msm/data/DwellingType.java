package de.tum.bgu.msm.data;

/**
 * Dwelling types that are distinguished in the model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 21 March 2011 in Santa Fe (which is J.S. Bach's 326th birthday)
 **/

public enum DwellingType {

    SFD,         // single-family house detached
    SFA,         // single-family house attached or townhouse
    MF234,       // duplexes and buildings 2-4 units (not including those that fit Attached or Townhouse definition)
    MF5plus,     // Multi-family houses with 5+ units
    MH           // mobile home
}
