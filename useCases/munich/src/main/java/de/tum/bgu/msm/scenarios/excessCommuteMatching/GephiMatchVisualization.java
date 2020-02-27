package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GephiMatchVisualization {

    public static void main(String[] args) {

        String path = "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/siloMuc.properties";
        String matchBaseDirectory = "C:\\Users\\Nico\\tum\\msm-papers\\data\\thePerfectMatch";

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        final List<Match> util = readMatches(matchBaseDirectory, "Util");

        try {
            writeGephiFilesAfter(matchBaseDirectory, dataContainer, util);
            writeGephiFilesBefore(matchBaseDirectory, dataContainer);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static void writeGephiFilesBefore(String base, DataContainerWithSchools dataContainer) throws IOException {
        CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.DHDN_GK4, TransformationFactory.WGS84);

        File file = new File(base + "/nodesBefore.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("id,x,y,type");
        writer.newLine();

        File fileEdge = new File(base + "/edgesBefore.csv");
        BufferedWriter writerEdge = new BufferedWriter(new FileWriter(fileEdge));
        writerEdge.write("id,source,target");
        writerEdge.newLine();

        for(Job job: dataContainer.getJobDataManager().getJobs().stream().filter(j -> j.getType().equals("Util")).collect(Collectors
                .toSet())) {
            final int ppId = job.getWorkerId();

            if(ppId <=0) {
                continue;
            }

            final int jobId = job.getId();
            final Coordinate coordinate = dataContainer.getJobDataManager().getJobFromId(jobId).getCoordinate();
            final Coord jobCoord = CoordUtils.createCoord(coordinate);
            final Coord transform = ct.transform(jobCoord);

            final Person person = dataContainer.getHouseholdDataManager().getPersonFromId(ppId);
            final int dwellingId = person.getHousehold().getDwellingId();
            final Coordinate dwellingCoordinate = dataContainer.getRealEstateDataManager().getDwelling(dwellingId).getCoordinate();
            final Coord dwellingCoord = CoordUtils.createCoord(dwellingCoordinate);
            final Coord transform1 = ct.transform(dwellingCoord);

            writer.write(jobId +","+ transform.getX() +","+ transform.getY()+",j");
            writer.newLine();
            writer.write(dwellingId +","+ transform1.getX() +","+ transform1.getY()+",d");
            writer.newLine();

            writerEdge.write(dwellingId + "_" + jobId +","+ dwellingId + "," + jobId);
            writerEdge.newLine();
        }
        writer.flush();
        writer.close();
        writerEdge.flush();
        writerEdge.close();

    }


    private static void writeGephiFilesAfter(String base, DataContainerWithSchools dataContainer, List<Match> util) throws IOException {

        CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.DHDN_GK4, TransformationFactory.WGS84);

        File file = new File(base + "/nodes.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("id,x,y,type");
        writer.newLine();

        File fileEdge = new File(base + "/edges.csv");
        BufferedWriter writerEdge = new BufferedWriter(new FileWriter(fileEdge));
        writerEdge.write("id,source,target");
        writerEdge.newLine();

        double distanceSumBefore =  0;
        double distanceSumAfter = 0;
        double distanceDiffSum = 0;

        for(Match match: util) {

            final int jobId = match.jobId;
            final Coordinate coordinate = dataContainer.getJobDataManager().getJobFromId(jobId).getCoordinate();
            final Coord newJobCoord = CoordUtils.createCoord(coordinate);
            final Coord transform = ct.transform(newJobCoord);

            final int ppId = match.workerId;
            final Person person = dataContainer.getHouseholdDataManager().getPersonFromId(ppId);
            final int dwellingId = person.getHousehold().getDwellingId();
            final Coordinate dwellingCoordinate = dataContainer.getRealEstateDataManager().getDwelling(dwellingId).getCoordinate();
            final Coord dwellingCoord = CoordUtils.createCoord(dwellingCoordinate);
            final Coord transform1 = ct.transform(dwellingCoord);

            double distanceAfter = CoordUtils.calcEuclideanDistance(newJobCoord, dwellingCoord);

            Job jobBefore = dataContainer.getJobDataManager().getJobFromId(person.getJobId());
            final Coord oldJObCoord = CoordUtils.createCoord(jobBefore.getCoordinate());
            double distanceBefore = CoordUtils.calcEuclideanDistance(oldJObCoord, dwellingCoord);

            distanceSumBefore += distanceBefore;
            distanceSumAfter += distanceAfter;
            distanceDiffSum += (distanceAfter - distanceBefore);


            writer.write(jobId +","+ transform.getX() +","+ transform.getY()+",j");
            writer.newLine();
            writer.write(dwellingId +","+ transform1.getX() +","+ transform1.getY()+",d");
            writer.newLine();

            writerEdge.write(dwellingId + "_" + jobId +","+ dwellingId + "," + jobId);
            writerEdge.newLine();
        }
        writer.flush();
        writer.close();
        writerEdge.flush();
        writerEdge.close();

        System.out.println("Distance before " + distanceSumBefore);
        System.out.println("Distance after " + distanceSumAfter);
        System.out.println("Distance diff " + distanceDiffSum);
    }


    private static List<Match> readMatches(String base, String sector) {
        List<Match> matches = new ArrayList<>();
        File file = new File(base +"/finalMatches"+ sector+".csv");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            final String header = reader.readLine();
            String record = reader.readLine();
            while (record!= null) {
                final String[] split = record.split(",");
                int workerId = Integer.parseInt(split[0]);
                int jobId = Integer.parseInt(split[1]);
                matches.add(new Match(workerId, jobId));
                record = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    private static class Match {
        private final int workerId;
        private final int jobId;

        public Match(int workerId, int jobId) {
            this.workerId = workerId;
            this.jobId = jobId;
        }
    }
}
