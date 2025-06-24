package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PtSkimsReaderMEL {

    private static final Logger LOGGER = LogManager.getLogger(PtSkimsReaderMEL.class);
    private DataContainer dataContainer;

    public PtSkimsReaderMEL(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void read() {
        LOGGER.info("Reading skims");
        readPtSkims();
    }


    private void readPtSkims() {
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptAccess", "skims/" + Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptAccessTimeMatrix, 1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptEgress", "skims/" + Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptEgressTimeMatrix, 1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptTotalTravelTime", "skims/" + Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptTotalTravelTimeMatrix,1.);
        ((SkimTravelTimes) dataContainer.getTravelTimes()).readSkim("ptBusTimeShare", "skims/" + Properties.get().healthData.ptPeakSkim, Properties.get().healthData.ptBusTimeShareMatrix, 1.);
    }
}
