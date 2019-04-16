package de.tum.bgu.msm.io;

import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.properties.Properties;

public class ResultsMonitorMuc extends DefaultResultsMonitor {
    public ResultsMonitorMuc(DataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public void endYear(int year, Multiset<Class<? extends MicroEvent>> eventCounter) {
        super.endYear(year, eventCounter);
    }

}
