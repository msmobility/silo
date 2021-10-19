package de.tum.bgu.msm.data.accessibility;

import org.apache.log4j.Logger;

import de.tum.bgu.msm.common.datafile.TableDataSet;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

/**
 * @author dziemke
 */
public class CommutingTimeProbabilityImpl implements CommutingTimeProbability {
	private static final Logger logger = Logger.getLogger(CommutingTimeProbabilityImpl.class);

    private float[] workTripLengthFrequencyDistribution;
    
    private final Properties properties;
    
    public CommutingTimeProbabilityImpl(Properties properties) {
        this.properties = properties;
    }
	
	@Override
    public void setup() {
        logger.info("Initializing trip length frequency distributions");
        readWorkTripLengthFrequencyDistribution();
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {
    }

	private void readWorkTripLengthFrequencyDistribution() {
		String fileName = properties.main.baseDirectory + properties.accessibility.htsWorkTLFD;
		TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
		workTripLengthFrequencyDistribution = new float[tlfd.getRowCount() + 1];
		for (int row = 1; row <= tlfd.getRowCount(); row++) {
			int tt = (int) tlfd.getValueAt(row, "TravelTime");
			if (tt > workTripLengthFrequencyDistribution.length) {
				logger.error("Inconsistent trip length frequency in " + properties.main.baseDirectory +
						properties.accessibility.htsWorkTLFD + ": " + tt + ". Provide data in 1-min increments.");
			}
			workTripLengthFrequencyDistribution[tt] = tlfd.getValueAt(row, "Utility");
		}
	}

	@Override
	public float getCommutingTimeProbability(int minutes, String mode) {
    	//this default implementation does not take into account mode. This allows compatible with e.g. more relaxed perception of commute time for AV users
		if (minutes < workTripLengthFrequencyDistribution.length) {
			return workTripLengthFrequencyDistribution[minutes];
		} else {
			return 0;
		}
	}
}