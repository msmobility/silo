package de.tum.bgu.msm.mito;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataSet;

public interface MitoDataConverter {

    DataSet convertData(DataContainer dataContainer);
}
