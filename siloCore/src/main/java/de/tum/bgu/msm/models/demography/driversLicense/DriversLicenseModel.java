package de.tum.bgu.msm.models.demography.driversLicense;

import de.tum.bgu.msm.events.impls.person.LicenseEvent;
import de.tum.bgu.msm.models.EventModel;

public interface DriversLicenseModel extends EventModel<LicenseEvent> {

    /**
     * TODO
     * @param perId
     */
    void checkLicenseCreation(int perId);
}
