package de.tum.bgu.msm.models.demography.birthday;

import de.tum.bgu.msm.events.impls.person.VehicleBirthdayEvent;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.simulator.UpdateListener;

public interface VehicleBirthdayAndRenovationModel extends UpdateListener, EventModel<VehicleBirthdayEvent> {
}
