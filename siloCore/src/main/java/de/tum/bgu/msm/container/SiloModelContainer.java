package de.tum.bgu.msm.container;

import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.models.AnnualModel;
import de.tum.bgu.msm.models.EventModel;

import java.util.List;
import java.util.Map;

public interface SiloModelContainer {
    Map<Class<? extends MicroEvent>, EventModel> getEventModels();

    List<AnnualModel> getAnnualModels();
}
