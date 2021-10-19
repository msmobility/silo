package de.tum.bgu.msm.syntheticPopulationGenerator.sanFrancisco;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.io.output.OmxMatrixWriter;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateSkimFromUber {

    public static final int PEAK_HOUR = 8;
    public static final int HOUR_OF_DAY_INDEX = 2;

    public static void main(String[] args) throws IOException {

        SiloUtil.loadHdf5Lib();

        final Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures("Z:\\projects\\2019\\TraMPA" +
                "\\San Francisco\\Data\\TAZ\\censusTractsNineCountiesTaz\\censusTracts_projected_7131_uberId.shp");
        final Map<Integer, Integer> movementId2zoneId = new HashMap<>();
        features.forEach(simpleFeature -> {
            final int tractce = Integer.parseInt(String.valueOf(simpleFeature.getAttribute("TRACTCE")));
            final int uberId = Integer.parseInt(String.valueOf(simpleFeature.getAttribute("MOVEMENT_I")));
            movementId2zoneId.put(uberId, tractce);
        });


        final Set<Id> ids = movementId2zoneId.values().stream().map(key -> (Id) () -> key).collect(Collectors.toSet());
        final SkimTravelTimes skimTravelTimes = new SkimTravelTimes();
        final IndexedDoubleMatrix2D carSkim = new IndexedDoubleMatrix2D(ids, ids);

        final BufferedReader fileReader = new BufferedReader(new FileReader("Z:\\projects\\2019\\TraMPA\\" +
                "San Francisco\\Data\\TAZ\\skims\\san_francisco-censustracts-2020-1-OnlyWeekdays-HourlyAggregate.csv"));
        fileReader
                //line by line
                .lines()
                //skip header
                .skip(1)
                //map to comma separated string arrays
                .map(string -> string.split(","))
                //filter only peak hours entries
                .filter(entry -> Integer.parseInt(entry[HOUR_OF_DAY_INDEX]) == PEAK_HOUR)
                //fill skim matrix
                .forEach(strings -> {
                    final int source = Integer.parseInt(strings[0]);
                    final int destination = Integer.parseInt(strings[1]);
                    final double meanTravelTime = Double.parseDouble(strings[3]);
                    if(movementId2zoneId.containsKey(source) && movementId2zoneId.containsKey(destination)) {
                        carSkim.setIndexed(movementId2zoneId.get(source), movementId2zoneId.get(destination), meanTravelTime);
                    }
                });


        skimTravelTimes.updateSkimMatrix(carSkim, TransportMode.car);

        int dimension = ids.size();
        OmxMatrixWriter.createOmxFile("Z:\\projects\\2019\\TraMPA\\San Francisco\\Data\\TAZ\\skims\\carSkim.omx", dimension);
        skimTravelTimes.printOutCarSkim(TransportMode.car, "Z:\\projects\\2019\\TraMPA\\San Francisco\\Data\\TAZ\\skims\\carSkim.omx", "carTravelTimes");
    }
}
