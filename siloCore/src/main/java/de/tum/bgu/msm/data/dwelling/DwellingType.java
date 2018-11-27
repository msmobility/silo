package de.tum.bgu.msm.data.dwelling;

/**
 * Dwelling types that are distinguished in the model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 21 March 2011 in Santa Fe (which is J.S. Bach's 326th birthday)
 **/

public enum DwellingType {

    /**
     * single-family house detached
     */
    SFD,
    /**
     * single-family house attached or townhouse
     */
    SFA,
    /**
     * duplexes and buildings 2-4 units (not including those that fit Attached or Townhouse definition)
     */
    MF234,
    /**
     * Multi-family houses with 5+ units
     */
    MF5plus,
    /**
     * mobile home
     */
    MH
}
