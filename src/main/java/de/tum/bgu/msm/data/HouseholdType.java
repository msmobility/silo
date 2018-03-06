package de.tum.bgu.msm.data;

/**
 * @author Rolf Moeckel, PB Albuquerque
 * Created on Dec 2, 2009
 *
 */
public enum HouseholdType {

    size1inc1,
    size2inc1,
    size3inc1,
    size4inc1,
    size1inc2,
    size2inc2,
    size3inc2,
    size4inc2,
    size1inc3,
    size2inc3,
    size3inc3,
    size4inc3,
    size1inc4,
    size2inc4,
    size3inc4,
    size4inc4;


    public static int convertHouseholdTypeToIncomeCategory (HouseholdType ht) {
        if (ht.equals(HouseholdType.size1inc1) || ht.equals(HouseholdType.size2inc1) ||
                ht.equals(HouseholdType.size3inc1) || ht.equals(HouseholdType.size4inc1)) {
            return 1;
        } else if (ht.equals(HouseholdType.size1inc2) || ht.equals(HouseholdType.size2inc2) ||
                ht.equals(HouseholdType.size3inc2) || ht.equals(HouseholdType.size4inc2)) {
            return 2;
        } else if (ht.equals(HouseholdType.size1inc3) || ht.equals(HouseholdType.size2inc3) ||
                ht.equals(HouseholdType.size3inc3) || ht.equals(HouseholdType.size4inc3)) {
            return 3;
        } else {
            return 4;
        }
    }
}


