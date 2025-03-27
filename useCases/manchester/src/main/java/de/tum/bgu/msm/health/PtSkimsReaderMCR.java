package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PtSkimsReaderMCR {

    private static final Logger LOGGER = LogManager.getLogger(PtSkimsReaderMCR.class);
    private DataContainer dataContainer;

    public PtSkimsReaderMCR(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void read() {
        LOGGER.info("Reading skims");
        readPtSkims();
    }


    private void readPtSkims() {
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptAccess", Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptAccessTimeMatrix, 1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptEgress", Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptEgressTimeMatrix, 1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptTotalTravelTime", Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptTotalTravelTimeMatrix,1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptBusTimeShare", Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptBusTimeShareMatrix, 1.);
    }
}
