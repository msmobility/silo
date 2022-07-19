package de.tum.bgu.msm.syntheticPopulationGenerator.portland;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.io.output.OmxMatrixWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.Collection;

public class QuickSkimMatrix {


    final static Logger logger = Logger.getLogger(QuickSkimMatrix.class);
    final DataContainer dataContainer;
    private double CAR_SPEED_MS = 50 / 3.6;

    public QuickSkimMatrix(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    void createSkim(){

        final Collection<Zone> zones = dataContainer.getGeoData().getZones().values();
        IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(zones, zones);
        for (Zone origin : zones) {
            Coordinate originRandomCoordinate = origin.getRandomCoordinate(SiloUtil.getRandomObject());

            for (Zone destination : zones) {
                Coordinate destinationRandomCoordinate = destination.getRandomCoordinate(SiloUtil.getRandomObject());

                double distance = Math.sqrt(Math.pow(originRandomCoordinate.x - destinationRandomCoordinate.x, 2) +
                        Math.pow(originRandomCoordinate.y - destinationRandomCoordinate.y, 2));

                matrix.setIndexed(origin.getId(), destination.getId(), distance / CAR_SPEED_MS / 60);
            }
        }

        ((SkimTravelTimes) dataContainer.getTravelTimes()).updateSkimMatrix(matrix, "car");

        logger.info("Matrix of distances calculated");

        OmxMatrixWriter.createOmxFile(Properties.get().main.baseDirectory + "skims/quick_matrix.omx", dataContainer.getGeoData().getZones().size());
        OmxMatrixWriter.createOmxSkimMatrix(matrix, Properties.get().main.baseDirectory + "skims/quick_matrix.omx", "car_time_min");


    }


}
