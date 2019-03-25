//package various;
//
//import cern.colt.matrix.tdouble.DoubleMatrix1D;
//import cern.colt.matrix.tdouble.DoubleMatrix2D;
//import cern.jet.math.tdouble.DoubleFunctions;
//import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
//import de.tum.bgu.msm.io.output.OmxMatrixWriter;
//import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
//import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.geotools.feature.simple.SimpleFeatureImpl;
//import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
//import org.matsim.api.core.v01.TransportMode;
//import org.matsim.core.utils.gis.ShapeFileReader;
//import org.matsim.core.utils.gis.ShapeFileWriter;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class SkimDiffAnalysis {
//
//    public static void main(String[] args) {
//
//        SiloUtil.loadHdf5Lib();
//
//        SkimTravelTimes skim1 = new SkimTravelTimes();
//        skim1.readSkim(TransportMode.car, "D:/traveltimes.omx", "tt", 1.0);
//
//        SkimTravelTimes skim2 = new SkimTravelTimes();
//        skim2.readSkim(TransportMode.car, "D:/traveltimes2.omx", "tt", 1.0);
//
//        IndexedDoubleMatrix2D matrix1 = skim1.getMatrixForMode(TransportMode.car);
//        IndexedDoubleMatrix2D matrix2 = skim2.getMatrixForMode(TransportMode.car);
//
//        IndexedDoubleMatrix2D diff = matrix1.assign(matrix2, (d1, d2) -> d1 - d2);
//        OmxMatrixWriter.createOmxFile("D:/diff.omx", diff.rows());
//        OmxMatrixWriter.createOmxSkimMatrix(diff, "D:/diff.omx", "tt");
//
//        double avgDiff = diff.zSum() / diff.size();
//        double stdDev = Math.sqrt(diff.assign(DoubleFunctions.chain(DoubleFunctions.square, DoubleFunctions.minus(avgDiff))).zSum() / diff.size());
//        System.out.println("Average diff: " + avgDiff + ", std dev: " + stdDev);
//
//        String zoneShapeFile = "D:/muc/input/zonesShapefile/zonesNew.shp";
//        final Collection<SimpleFeature> allFeatures = ShapeFileReader.getAllFeatures(zoneShapeFile);
//        SimpleFeatureType originalType = allFeatures.iterator().next().getFeatureType();
//
//        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
//        typeBuilder.addAll(originalType.getAttributeDescriptors());
//        typeBuilder.setName("zoneSkim");
//        typeBuilder.add("avg_to_1", Double.class);
//        typeBuilder.add("avg_from_1", Double.class);
//        typeBuilder.add("avg_to_2", Double.class);
//        typeBuilder.add("avg_from_2", Double.class);
//        typeBuilder.add("avg_to_d", Double.class);
//        typeBuilder.add("avg_from_d", Double.class);
//
//        SimpleFeatureType type = typeBuilder.buildFeatureType();
//
//        List<SimpleFeature> features = new ArrayList<>();
//        for (SimpleFeature feature : allFeatures) {
//
//            List<Object> values = new ArrayList<>(feature.getAttributes());
//            values.add(null);
//            values.add(null);
//            values.add(null);
//            values.add(null);
//            values.add(null);
//            values.add(null);
//            SimpleFeature newFeature = new SimpleFeatureImpl(values, type, feature.getIdentifier());
//            final int zoneId = Integer.parseInt(feature.getAttribute("id").toString());
//            {
//                final IndexedDoubleMatrix1D row = matrix1.viewRow(zoneId);
//                final IndexedDoubleMatrix1D col = matrix1.viewColumn(zoneId);
//                final double avgTo = row.zSum() / row.size();
//                newFeature.setAttribute("avg_to_1", avgTo);
//                final double avgFrom = col.zSum() / col.size();
//                newFeature.setAttribute("avg_from_1", avgFrom);
//            }
//
//            {
//                final IndexedDoubleMatrix1D row = matrix2.viewRow(zoneId);
//                final IndexedDoubleMatrix1D col = matrix2.viewColumn(zoneId);
//                newFeature.setAttribute("avg_to_2", row.zSum() / row.size());
//                newFeature.setAttribute("avg_from_2", col.zSum() / col.size());
//            }
//
//            {
//                final IndexedDoubleMatrix1D row = diff.viewRow(zoneId);
//                final IndexedDoubleMatrix1D col = diff.viewColumn(zoneId);
//                newFeature.setAttribute("avg_to_d", row.zSum() / row.size());
//                newFeature.setAttribute("avg_from_d", col.zSum() / col.size());
//            }
//            features.add(newFeature);
//        }
//        ShapeFileWriter.writeGeometries(features, "D:/skimAnalysis.shp");
//    }
//}
