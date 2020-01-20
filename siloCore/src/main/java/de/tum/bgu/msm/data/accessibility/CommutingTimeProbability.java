package de.tum.bgu.msm.data.accessibility;

import de.tum.bgu.msm.models.ModelUpdateListener;

public interface CommutingTimeProbability extends ModelUpdateListener {
    float getCommutingTimeProbability(int minutes, String mode);
}
