//package de.tum.bgu.msm.syntheticPopulationGenerator.maryland;
//
//import cern.colt.matrix.tdouble.DoubleMatrix2D;
//import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
//import de.tum.bgu.msm.io.output.OmxMatrixWriter;
//import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
//import de.tum.bgu.msm.utils.SiloUtil;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class CreateSkimOmxFromCubeCsv {
//
//    public static void main(String[] args) throws IOException {
//
//        SiloUtil.loadHdf5Lib();
//
//        BufferedReader reader = new BufferedReader(new FileReader("T:\\2Kuehnel_Nico\\InputData\\TransitSkims\\SOV_auto_dist.csv"));
//        String record = reader.readLine();
//
//        int numberOfZones = record.split(",").length;
//
//        final IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(numberOfZones, numberOfZones);
//
//        int i = 1;
//        while((record = reader.readLine()) != null) {
//            String[] zoneValues = record.split(",");
//            for (int j = 0; j < zoneValues.length; j++) {
//                double v = Double.parseDouble(zoneValues[j]);
//                if(v == 0) {
//                    v = Double.MAX_VALUE;
//                } else {
//                    //for distances, convert miles to kilometers
//                    v *= 1.60934;
//                }
//                matrix.setIndexed(i,j, v);
//            }
//            i++;
//        }
//
//        OmxMatrixWriter.createOmxFile("T:/2Kuehnel_Nico/nmtDDSkim.omx", numberOfZones);
//
//        OmxMatrixWriter.createOmxSkimMatrix(matrix, "T:/2Kuehnel_Nico/nmtDDSkim.omx", "distanceByDistance");
//
//    }
//}
