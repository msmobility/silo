package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.AbstractCsvReader;
import de.tum.bgu.msm.util.MitoUtil;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CoefficientsReader extends AbstractCsvReader {


    private final Map<String, Double> coefficients = new HashMap<>();
    private final String id;

    private int variableIndex;
    private int coefficientIndex;

    private final Path path;


//        public CoefficientsReader(DataSet dataSet, Id id, Path path) {
//            super(dataSet);
//            this.id = id.toString();
//            this.path = path;
//            read();
//        }

    public CoefficientsReader(DataSetSynPop dataSet, String id, Path path) {
        super(dataSet);
        this.id = id;
        this.path = path;
        read();
    }

    @Override
    protected void processHeader(String[] header) {
        variableIndex = MitoUtil.findPositionInArray("variable", header);
        coefficientIndex = MitoUtil.findPositionInArray(id, header);
    }

    @Override
    protected void processRecord(String[] record) {
        String name = record[variableIndex];
        double value = Double.parseDouble(record[coefficientIndex]);

        coefficients.put(name, value);

    }

    @Override
    public void read() {
        super.read(path, ",");
    }

    public Map<String, Double> readCoefficients(){
        read();
        return coefficients;
    };

}
