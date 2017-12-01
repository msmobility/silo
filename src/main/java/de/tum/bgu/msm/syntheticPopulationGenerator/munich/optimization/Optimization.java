package de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization;


import com.google.common.primitives.Ints;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

import java.util.ArrayList;

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
        } else {
            readIPUresults();
        }
        logger.info("   Completed optimization model.");
    }


    private void obtainWeightsIPU(){
        if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
            createWeightsAndErrorsCountyandCity();
            IPUbyCountyAndCity ipu = new IPUbyCountyAndCity(dataSetSynPop);
            ipu.run();
            if (PropertiesSynPop.get().main.boroughIPU){
                IPUbyCountyCityAndBorough ipuMunichCity = new IPUbyCountyCityAndBorough(dataSetSynPop);
                ipuMunichCity.run(9162);
                ipuMunichCity.refineCities(9162);
            }
        } else {
            createWeightsAndErrorsCity();
            IPUbyCity ipu = new IPUbyCity(dataSetSynPop);
            ipu.run();
        }
    }


    private void readIPUresults(){
        ReadIPU ipu = new ReadIPU(dataSetSynPop);
        ipu.run();
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
        if (PropertiesSynPop.get().main.boroughIPU) {
            TableDataSet errorsBorough = new TableDataSet();
            int[] cityIds = Ints.toArray(dataSetSynPop.getMunicipalitiesByCounty().get(9162));
            for (int city : cityIds){
                int[] boroughIds = Ints.toArray(dataSetSynPop.getBoroughsByCity().get(city));
                errorsBorough = SiloUtil.initializeTableDataSet(errorsBorough,
                        PropertiesSynPop.get().main.attributesBorough, boroughIds);
            }
            dataSetSynPop.setErrorsBorough(errorsBorough);
        }
        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCountyIDs());

        dataSetSynPop.setErrorsCounty(errorsCounty);
        dataSetSynPop.setErrorsMunicipality(errorsMunicipality);
        dataSetSynPop.setErrorsSummary(errorsSummary);
    }


}
