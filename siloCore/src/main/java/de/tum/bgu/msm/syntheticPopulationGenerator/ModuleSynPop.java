package de.tum.bgu.msm.syntheticPopulationGenerator;

/**
 * Created by Ana Moreno on 29.11.2017. Adapted from MITO
 */


public abstract class ModuleSynPop {

    protected final DataSetSynPop dataSetSynPop;

    protected ModuleSynPop(DataSetSynPop dataSetSynPop){this.dataSetSynPop = dataSetSynPop;}

    public abstract void run();

}
