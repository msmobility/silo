package de.tum.bgu.msm.data.person;

/**
 * Nationality of a person or a household. For households, defined if all household members belong to one nationality,
 * otherwise called foreigner
 * @author Rolf Moeckel, TUM
 * Created on 20 May 2017 near Greenland at an altitude of 35,000 feet
 **/

public enum Nationality {

    GERMAN(0),         // German national, either born or naturalized German
    OTHER(1);          // Any other nationality, including mixed households

    private final int nationalityCode;

    Nationality(int nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public int getNationalityCode() {
        return nationalityCode;
    }

}
