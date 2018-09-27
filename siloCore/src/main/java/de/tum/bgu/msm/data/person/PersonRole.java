package de.tum.bgu.msm.data.person;

/**
 * Role a person takes over in its household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/

public enum PersonRole {
    /**
     * does not live with other person aged +/- 15 years: can get married, unlikely to have a child, can leave household if household size > 1
     */
    SINGLE,
    /**
     * lives with other person aged +/- 15 years: can get divorced, more likely to have a child
     */
    MARRIED,
    /**
    * lives with at least one person aged +15 to +40: can leave parental household, unlikely to have a child, can get married
    */
    CHILD;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
