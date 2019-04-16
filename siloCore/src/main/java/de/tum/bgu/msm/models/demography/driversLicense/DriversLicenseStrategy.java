package de.tum.bgu.msm.models.demography.driversLicense;

import de.tum.bgu.msm.data.person.Person;

public interface DriversLicenseStrategy {
    double calculateChangeDriversLicenseProbability(Person pp);

    double calculateCreateDriversLicenseProbability(Person pp);
}
