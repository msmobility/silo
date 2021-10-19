package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import cern.colt.map.tint.OpenIntIntHashMap;
import cern.colt.map.tobject.OpenIntObjectHashMap;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GaleShapley {

    private static final Logger logger = Logger.getLogger(GaleShapley.class);

    public static void main(String[] args) {
        SiloUtil.loadHdf5Lib();
        new GaleShapley();
    }

    private OpenIntObjectHashMap waitingLists;
    private OpenIntIntHashMap capacities;

    public GaleShapley() {

        String sector = "Trns";

        OpenIntIntHashMap worker2Index = new OpenIntIntHashMap();
        OpenIntIntHashMap zones2Index = new OpenIntIntHashMap();

        OpenIntIntHashMap index2Worker = new OpenIntIntHashMap();
        OpenIntIntHashMap index2Zones = new OpenIntIntHashMap();

        String path = "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/siloMuc.properties";
        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);
        TravelTimeUtil.updateCarSkim((SkimTravelTimes) dataContainer.getTravelTimes(), 2011, properties);

        final Collection<Person> persons = dataContainer.getHouseholdDataManager().getPersons();
        final Map<String, List<Person>> personsByJobSector = persons.stream().filter(p -> p.getJobId() > 0)
                .collect(Collectors.groupingBy(person ->
                        dataContainer.getJobDataManager().getJobFromId(person.getJobId()).getType()));
        final Map<String, Map<Integer, List<Job>>> jobsByZoneBySector = dataContainer.getJobDataManager().getJobs().stream().collect(Collectors.groupingBy(Job::getType, Collectors.groupingBy(Job::getZoneId)));

        for(String type: JobType.getJobTypes()) {
            final Map<Integer, List<Job>> jobsByZone = jobsByZoneBySector.get(type);
            final IntSummaryStatistics intSummaryStatistics = jobsByZone.keySet().stream().mapToInt(zone -> jobsByZone.get(zone).size()).summaryStatistics();
            System.out.println("Sector " + type);
            System.out.println("min " +intSummaryStatistics.getMin());
            System.out.println("max "+intSummaryStatistics.getMax());
            System.out.println("avg " +intSummaryStatistics.getAverage());
            System.out.println("sum " +intSummaryStatistics.getSum());

        }




        int[][] preferences = new int[personsByJobSector.get(sector).size()][jobsByZoneBySector.get(sector).size()];
        logger.info("Initialized preference array with " + preferences.length + " workers and " + preferences[0].length + " zones for sector " + sector);
        {
            int i = 0;
            for (Person person : personsByJobSector.get(sector)) {
                if(LongMath.isPowerOfTwo(i)) {
                    logger.info("Filling preferences, number " + i);
                }
                int j = 0;
                for (int zoneId :jobsByZoneBySector.get(sector).keySet()) {
                    Zone from = dataContainer.getGeoData().getZones().get(dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId());
                    Zone to = dataContainer.getGeoData().getZones().get(zoneId);
                    preferences[i][j] = (int) dataContainer.getTravelTimes().getTravelTime(from, to, 28800, "car");
                    zones2Index.put(zoneId, j);
                    index2Zones.put(j, zoneId);
                    j++;
                }
                worker2Index.put(person.getId(), i);
                index2Worker.put(i, person.getId());
                i++;
            }
        }

        waitingLists = new OpenIntObjectHashMap();
        capacities = new OpenIntIntHashMap();
        int capacitySum = 0;
        int noCapacity = 0;

        for (Map.Entry<Integer, List<Job>> entry: jobsByZoneBySector.get(sector).entrySet()) {
            int index = zones2Index.get(entry.getKey());
            final int capacity = entry.getValue().size();
            capacities.put(index, capacity);
            capacitySum += capacity;
            waitingLists.put(index, new ArrayList<Entry>());
            if (capacity == 0) {
                noCapacity++;
            }
        }
        logger.warn("capacity " + capacitySum + " to be allocated for " + personsByJobSector.get(sector).size() + " workers. " + noCapacity + " zones have no capacity.");


        int rejectionsNo = Integer.MAX_VALUE;
        int noOtherOptions = 0;
        int givingUP = 0;

        int iteration = 0;
        List<Entry> rejections = new ArrayList<>();
        for(int k = 0; k < preferences.length; k++) {
            rejections.add(new Entry(k, 0));
        }

        int fullZones;

        while(rejectionsNo > 0  && rejectionsNo != noOtherOptions) {

            fullZones = 0;

            if(iteration%3 == 0) {
                logger.info("Cleaning preferences..");
                for (int k = 0; k < preferences[0].length; k++) {
                    final List<Entry> list = ((List<Entry>) waitingLists.get(k));
                    if (!list.isEmpty() && list.size() >= capacities.get(k)) {
                        int min = list.get(list.size()-1).value;
                        for (Entry entry: rejections) {
                            if(preferences[entry.person][k] > min) {
                                preferences[entry.person][k] = Integer.MAX_VALUE;
                            }
                        }
                    }
                }
            }

            for (Entry entry: rejections) {
                final int i = entry.person;

                //if(cardinality(preferences[i]) == Integer.MAX_VALUE) {
                  //  noOtherOptions++;
                    //continue;
                //}

                final int[] minLocation = getMinLoation(preferences[i]);
                ((List<Entry>) waitingLists.get(minLocation[1])).add(new Entry(i, minLocation[0]));
            }


            rejections.clear();
            for (int i = 0; i < preferences[0].length; i++) {
                final List<Entry> list = ((List<Entry>) waitingLists.get(i));
                final int capacity = capacities.get(i);
                if (list.size() > capacity) {
                    list.sort(Comparator.comparingInt(o -> o.value));
                    final List<Entry> newRejections = list.subList(capacity, list.size());
                    rejections.addAll(newRejections);
                    waitingLists.put(i, list.subList(0, capacity));
                    for (Entry entry : newRejections) {
                        preferences[entry.person][i]= Integer.MAX_VALUE;
                    }
                }
            }
            rejectionsNo = rejections.size();
            iteration++;
            if(LongMath.isPowerOfTwo(iteration)) {
                logger.info("Iteration "+ iteration + ": " + rejectionsNo + " rejected, " + noOtherOptions + " no other options, "+ capacitySum + " total capacity, "+ givingUP + " gave up.");
            }
            if(iteration % 10 == 0) {
                for (int i = 0; i < preferences[0].length; i++) {
                    final List<Entry> list = ((List<Entry>) waitingLists.get(i));

                    if (!list.isEmpty() && list.size() >= capacities.get(i)) {
                        fullZones++;
                    }
                }
                logger.info(fullZones + " filled zones.");
            }
        }

//        for(int i = 0; i <preferences.rows(); i++) {
//            rejections.add(new Entry(i, 0));
//        }
        logger.info("Finished in iteration "+ iteration + ": " + rejectionsNo + " rejected, " + noOtherOptions + " no other options, "+ capacitySum + " total capacity, "+ givingUP + " gave up.");


        File file = new File(sector+"matches.csv");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("worker,zone,tt");
            writer.newLine();
            waitingLists.forEachPair((first, second) -> {
                int zoneId = index2Zones.get(first);
                List<Entry> workers = (List<Entry>) second;
                for (Entry entry: workers) {
                    int workerId = index2Worker.get(entry.person);
                    try {
                        writer.write(workerId+","+zoneId+","+entry.value);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            });
        } catch (IOException e) {


        }
    }


    private int cardinality(int[] array) {
        int counter = 0;
        for (int value : array) {
            if (value != 0) {
                counter++;
            }
        }
        return counter;
    }

    private int[] getMinLoation(int[] array) {
        int[] location = new int[2];
        Arrays.fill(location, Integer.MAX_VALUE);
        for (int i = 0; i < array.length; i++) {
            if(array[i] < location[0]) {
                location[0] = array[i];
                location[1] = i;
            }
        }
        return location;
    }


    static class Entry {
        final int person;
        final int value;

        public Entry(int person, int value){
            this.person = person;
            this.value = value;
        }

        public String toString() {
            return String.valueOf(person);
        }
    }
}
