package de.tum.bgu.msm.data;

/**
 * Nationality of a person or a household. For households, defined if all household members belong to one nationality,
 * otherwise called foreigner
 * @author Rolf Moeckel, TUM
 * Created on 20 May 2017 near Greenland at an altitude of 35,000 feet
 **/

public enum Nationality {
    german,         // German national, either born or naturalized German
    other           // Any other nationality, including mixed households
}
