package de.tum.bgu.msm.io.input;

import de.tum.bgu.msm.data.DataSet;

abstract class AbstractInputReader {

    protected final DataSet dataSet;

    AbstractInputReader(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public abstract void read();
}
