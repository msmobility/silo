package de.tum.bgu.msm.io.output;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.events.MicroEvent;

public interface ResultsMonitor {
    void setup();

    void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter);

    void endSimulation();
}
