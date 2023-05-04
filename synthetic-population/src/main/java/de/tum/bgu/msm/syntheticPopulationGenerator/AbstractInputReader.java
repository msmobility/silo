package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;

abstract class AbstractInputReader {

    protected final DataSetSynPop dataSet;

    AbstractInputReader(DataSetSynPop dataSet) {
        this.dataSet = dataSet;
    }

    public abstract void read();
}
