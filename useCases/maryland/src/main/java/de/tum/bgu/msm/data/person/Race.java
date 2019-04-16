package de.tum.bgu.msm.data.person;


import de.tum.bgu.msm.data.Id;

/**
 * Race of a person or a household. For households, defined if all household members belong to one race, otherwise called other
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 28 January 2015 in College Park, MD
 **/

public enum Race implements Id {
    white,          // white person or all household members identify themselves as white (and not hispanic)
    black,          // black person or all household members identify themselves as african-american or black (and not hispanic)
    hispanic,       // hispanic person or all household members identify themselves as hispanic or of latino origin
    other;// person with other race or various household member identify themselves as different races, or household members have other racial definition

    @Override
    public int getId() {
        return this.ordinal();
    }
}
