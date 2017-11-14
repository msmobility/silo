package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

        import com.pb.common.datafile.TableDataSet;
        import com.pb.common.matrix.Matrix;
        import de.tum.bgu.msm.data.Person;

        import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

        import java.util.*;


/**
 * Created by AnaTsui on 15/02/2017.
 * Adapted from Joe package of DomesticDestinationChoice
 */


public class EmploymentChoice {

    private ResourceBundle rb;

    public EmploymentChoice(ResourceBundle rb) {


    }


    public int selectJobType(Person person, TableDataSet probabilitiesJobTable,int[] jobTypes) {
        //given a person, select the job type. It is based on the probabilities

        double[] probabilities = new double[jobTypes.length];
        //Person and job type values
        String name = "";
        if (person.getGender() == 1) {
            name = "maleEducation";
        } else {
            name = "femaleEducation";
        }
        name = name + person.getEducationLevel();

        if (jobTypes.length == probabilitiesJobTable.getRowCount()) {
            probabilities = probabilitiesJobTable.getColumnAsDouble(name);
        } else {
            for (int job = 0; job < jobTypes.length; job++){
                probabilities[job] = probabilitiesJobTable.getValueAt(jobTypes[job],name);
            }
        }
        int selected = new EnumeratedIntegerDistribution(jobTypes,probabilities).sample();

        return selected;

    }


    public int[] selectWorkplace(Person person, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                 int[] zoneJobKeys, int lengthZoneKeys, Matrix timesMatrix, double alpha, double gamma) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination

        double[] probabilities = new double[lengthZoneKeys];
        for (int j = 0; j < lengthZoneKeys; j++){
            probabilities[j] = Math.exp(calculateUtilityZoneJobType(person, zoneJobKeys[j], timesMatrix, alpha, gamma)) * vacantJobsByZoneByType.get(zoneJobKeys[j]);
            //probability = exp(utility) * number of vacant jobs
        }

        int[] work = select(probabilities,zoneJobKeys);

        return work;
    }


    public int[] selectWorkplace2(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                 int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination

        double[] probabilities = new double[lengthZoneKeys];
        for (int j = 0; j < lengthZoneKeys; j++){
            probabilities[j] = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) * vacantJobsByZoneByType.get(zoneJobKeys[j]);
            //probability = impedance * number of vacant jobs. Impedance is calculated in advance as exp(utility)
        }

        int[] work = select(probabilities,zoneJobKeys);

        return work;
    }


    public int[] selectWorkplace3(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                  int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination


        int[] min = new int[2];
        min[0] = zoneJobKeys[0];
        min[1] = 0;
        double minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[0] / 100);
        for (int j = 1; j < lengthZoneKeys; j++) {
            if (impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) < minDist) {
                min[0] = zoneJobKeys[j];
                min[1] = j;
                minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100);
            }
        }
        return min;
    }


    //Previous version that selects the job type and workplace at the same time
    public int selectJobTypev0(Person person, TableDataSet probabilitiesJobTable, int[] jobTypes,
                               HashMap<Integer, Integer> vacantJobsByType) {
        //given a person, select the job type. It is based on the utility of that job type, multiplied by the number of jobs of that type that remain vacant


        double[] utilitiesJob = new double[jobTypes.length];
        for (int j = 0; j < jobTypes.length; j++){
            utilitiesJob[j] = calculateUtilityJobType(person,jobTypes[j],probabilitiesJobTable) * vacantJobsByType.get(jobTypes[j]);
        }
        //calculate denominator
        double probabilityJob_denominator = Arrays.stream(utilitiesJob).sum();

        //calculate probabilities
        double[] probabilitiesJob = Arrays.stream(utilitiesJob).map(u -> u/probabilityJob_denominator).toArray();

        //select the job type according to probabilities
        int selectedJobType = new EnumeratedIntegerDistribution(jobTypes,probabilitiesJob).sample();

        return selectedJobType;
    }


    private double calculateUtilityJobType(Person person, int jobTypeDE, TableDataSet probabilitiesJob) {
        //calculates utility given one person and the type of job

        //Person and job type values
        int gender = person.getGender() - 1; //at person, 1 is male and 2 is female
        int education = person.getEducationLevel();
        String name = "";
        if (gender == 0) {
            name = "maleEducation";
        } else {
            name = "femaleEducation";
        }
        name = name + education;

        //Utility for that job is proportional to the percentages on the base year
        double adequacy = probabilitiesJob.getValueAt(jobTypeDE, name);

        return adequacy;
    }


    private double[] calculateUtilitiesJobType(Person person, TableDataSet probabilitiesJob) {
        //calculates utility given one person and the type of job

        //Person and job type values
        String name = "";
        if (person.getGender() == 1) {
            name = "maleEducation";
        } else {
            name = "femaleEducation";
        }
        name = name + person.getEducationLevel();

        //Utility for that job is proportional to the percentages on the base year

        double adequacy[] = probabilitiesJob.getColumnAsDouble(name);

        return adequacy;
    }


    private double calculateUtilityZoneJobType(Person person, int zoneJob, Matrix timesMatrix, double a, double g) {
        //calculates utility given one person and the key of the Hashmap (zone + job type)

        //Person and workplace values
        int origin = person.getZone();
        int destination = Math.round(zoneJob / 100);
        double travelTime = timesMatrix.getValueAt(origin, destination);

        //Calculate utility
        double u = a * Math.exp(g * travelTime);

        return u;
    }


    public static int[] select (double[] probabilities, int[] zones, int[] type) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[3];
        for (double val: probabilities) sumProb += val;
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = zones[i];
                results[1] = type[i];
                results[2] = i;
                return results;
            }
        }
        results[0] = zones[probabilities.length - 1];
        results[1] = type[probabilities.length - 1];
        results[2] = probabilities.length - 1;
        return results;
    }


    public static int[] select (double[] probabilities, int[] keys) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[2];
        for (double val: probabilities) sumProb += val;
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = keys[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = keys[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


}
