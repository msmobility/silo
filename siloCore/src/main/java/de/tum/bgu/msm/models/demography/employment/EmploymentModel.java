package de.tum.bgu.msm.models.demography.employment;

import de.tum.bgu.msm.events.impls.person.EmploymentEvent;
import de.tum.bgu.msm.models.EventModel;

public interface EmploymentModel extends EventModel<EmploymentEvent> {

    /**
     * TODO
     * @param perId
     * @return
     */
    boolean lookForJob(int perId);

    /**
     * TODO
     * @param perId
     * @return
     */
    boolean quitJob(int perId);
}
