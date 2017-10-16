package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import org.apache.log4j.Logger;

/**
 * Simulates if someone obtains a drivers license
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class ChangeDriversLicense {
    static Logger logger = Logger.getLogger(ChangeDriversLicense.class);
    private GeoData geoData;


    public ChangeDriversLicense() {
        // constructor
    }


    public boolean updateDriversLicense (int perId, SiloDataContainer dataContainer) {
        // check if person obtains a drivers license

        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away
        if (pp.hasDriverLicense()) return false;

        // todo: Implement more logical rules how drivers licenses are added (e.g., by age and sex)
        if (pp.getAge() == 18) {
            pp.setDriverLicense(true);
            EventManager.countEvent(EventTypes.checkDriversLicense);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId +
                    " obtained a drivers license.");
            return true;
        }
        return false;
    }
}
