package de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization;


import com.google.common.primitives.Ints;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

public class Optimization extends ModuleSynPop{

    private static final Logger logger = Logger.getLogger(Optimization.class);

    public Optimization(DataSetSynPop dataSetSynPop){
        super(dataSetSynPop);
    }

    @Override
    public void run(){

        logger.info("   Started optimization model.");
        if (PropertiesSynPop.get().main.runIPU){
            obtainWeightsIPU();
        } else if (PropertiesSynPop.get().main.runAllocation){
            readIPUresults();
        }
        logger.info("   Completed optimization model.");
    }


    private void obtainWeightsIPU(){
        if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
            if (!PropertiesSynPop.get().main.boroughIPU) {
                createWeightsAndErrorsCountyandCity();
                new IPUbyCountyAndCity(dataSetSynPop).run();
            } else {
                createWeightsAndErrorsCountyCityandBorough();
                IPUbyCountyCityAndBorough ipuWithThreeGeographicalAreas = new IPUbyCountyCityAndBorough(dataSetSynPop);
                for (int county : dataSetSynPop.getBoroughsByCounty().keySet()) {
                    ipuWithThreeGeographicalAreas.run(county);
                }
            }
        } else {
            createWeightsAndErrorsCity();
            new IPUbyCity(dataSetSynPop).run();
        }
    }


    private void readIPUresults(){
        new ReadIPU(dataSetSynPop).run();
    }


    private void createWeightsAndErrorsCity(){

        int[] microDataIds = dataSetSynPop.getFrequencyMatrix().getColumnAsInt("ID");
        dataSetSynPop.getFrequencyMatrix().buildIndex(dataSetSynPop.getFrequencyMatrix().getColumnPosition("ID"));
        dataSetSynPop.setWeights(new TableDataSet());
        dataSetSynPop.getWeights().appendColumn(microDataIds, "ID");

        TableDataSet errorsMunicipality = new TableDataSet();
        TableDataSet errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsMunicipality =  SiloUtil.initializeTableDataSet(errorsMunicipality,
                PropertiesSynPop.get().main.attributesMunicipality, dataSetSynPop.getCityIDs());
        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCityIDs());
        dataSetSynPop.setErrorsMunicipality(errorsMunicipality);
        dataSetSynPop.setErrorsSummary(errorsSummary);
    }


    private void createWeightsAndErrorsCountyandCity(){

        int[] microDataIds = dataSetSynPop.getFrequencyMatrix().getColumnAsInt("ID");
        dataSetSynPop.getFrequencyMatrix().buildIndex(dataSetSynPop.getFrequencyMatrix().getColumnPosition("ID"));
        dataSetSynPop.setWeights(new TableDataSet());
        dataSetSynPop.getWeights().appendColumn(microDataIds, "ID");

        TableDataSet errorsCounty = new TableDataSet();
        TableDataSet errorsMunicipality = new TableDataSet();
        TableDataSet errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsCounty = SiloUtil.initializeTableDataSet(errorsCounty,
                PropertiesSynPop.get().main.attributesCounty, dataSetSynPop.getCountyIDs());
        errorsMunicipality = SiloUtil.initializeTableDataSet(errorsMunicipality,
                    PropertiesSynPop.get().main.attributesMunicipality, dataSetSynPop.getCityIDs());
        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCountyIDs());
        dataSetSynPop.setErrorsCounty(errorsCounty);
        dataSetSynPop.setErrorsMunicipality(errorsMunicipality);
        dataSetSynPop.setErrorsSummary(errorsSummary);
    }

    private void createWeightsAndErrorsCountyCityandBorough(){

        int[] microDataIds = dataSetSynPop.getFrequencyMatrix().getColumnAsInt("ID");
        dataSetSynPop.getFrequencyMatrix().buildIndex(dataSetSynPop.getFrequencyMatrix().getColumnPosition("ID"));
        dataSetSynPop.setWeights(new TableDataSet());
        dataSetSynPop.getWeights().appendColumn(microDataIds, "ID");

        TableDataSet errorsCounty = new TableDataSet();
        TableDataSet errorsMunicipality = new TableDataSet();
        TableDataSet errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsCounty = SiloUtil.initializeTableDataSet(errorsCounty,
                PropertiesSynPop.get().main.attributesCounty, dataSetSynPop.getCountyIDs());
        errorsMunicipality = SiloUtil.initializeTableDataSet(errorsMunicipality,
                PropertiesSynPop.get().main.attributesMunicipality, dataSetSynPop.getCityIDs());

        TableDataSet errorsBorough = new TableDataSet();
        int[] boroughIds = Ints.toArray(dataSetSynPop.getBoroughs());
        errorsBorough = SiloUtil.initializeTableDataSet(errorsBorough, PropertiesSynPop.get().main.attributesBorough, boroughIds);
        dataSetSynPop.setErrorsBorough(errorsBorough);

        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCountyIDs());
        dataSetSynPop.setErrorsCounty(errorsCounty);
        dataSetSynPop.setErrorsMunicipality(errorsMunicipality);
        dataSetSynPop.setErrorsSummary(errorsSummary);
    }


}
