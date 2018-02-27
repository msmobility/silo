package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import org.apache.log4j.Logger;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class ChangeSchoolUnivModel {
    static Logger logger = Logger.getLogger(ChangeSchoolUnivModel.class);
    private GeoData geoData;


    public ChangeSchoolUnivModel(GeoData geoData) {
        this.geoData = geoData;
    }


    public boolean updateSchoolUniv (int perId, SiloDataContainer dataContainer) {
        // check if person needs to find a new school

        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away

        // todo: Implement logical rules how students change from one school type to another or graduate from school/university
        if (pp.getAge() == 19) {
            int schoolId = 0;
            pp.setSchoolPlace(schoolId);
            pp.setEducationLevel(2);  // todo if 2 is the right code for someone who graduates from high school
            EventManager.countEvent(EventTypes.CHECK_SCHOOL_UNIV);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId +
                    " changed school. New school place (0 = left school) " + schoolId);
            return true;
        }
        return false;
    }
}
