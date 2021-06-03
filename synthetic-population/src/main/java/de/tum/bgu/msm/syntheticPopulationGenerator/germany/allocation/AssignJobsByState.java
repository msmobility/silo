package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.matrix.RowVector;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class AssignJobsByState {

    private static final Logger logger = Logger.getLogger(AssignJobsByState.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private int assignedJobs;



    public AssignJobsByState(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }


    public void run() {
        logger.info("   Running module: job de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.AssignJobs");

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager households = dataContainer.getHouseholdDataManager();
        //TableDataSet cellsMatrix = PropertiesSynPop.get().main.cellsMatrix;
        int workersOutOfStudyArea = 0;
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            if (pp.getOccupation() == Occupation.EMPLOYED) {

                String selectedJobTypeAsString = (String) pp.getAttribute("jobType").get();
                ///todo found some empty job types
                if (selectedJobTypeAsString.equals("")) {
                    pp.setAttribute("jobType", "Serv");
                    selectedJobTypeAsString = "Serv";
                }
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int destination = -2;
                if (!dataSetSynPop.getVacantJobsByTypeAndZone().get(selectedJobTypeAsString).isEmpty()) {
                    Map<Integer, Double> probabilities = calculateDistanceProbabilityByJobType(selectedJobTypeAsString,origin);
                    destination = SiloUtil.select(probabilities);
                    int remainingJobs = dataSetSynPop.getVacantJobsByTypeAndZone().get(selectedJobTypeAsString).get(destination) - 1;
                    if (remainingJobs > 0) {
                        dataSetSynPop.getVacantJobsByTypeAndZone().get(selectedJobTypeAsString).put(destination, remainingJobs);
                    } else {
                        dataSetSynPop.getVacantJobsByTypeAndZone().get(selectedJobTypeAsString).remove(destination);
                        if (dataSetSynPop.getVacantJobsByTypeAndZone().get(selectedJobTypeAsString).isEmpty()){
                            dataSetSynPop.getVacantJobsByTypeAndZone().remove(selectedJobTypeAsString);
                        }
                    }
                } else {
                    workersOutOfStudyArea++;
                }
                pp.setAttribute("workZone", destination);
                pp.setAttribute("commuteDistance", dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination));

                if (LongMath.isPowerOfTwo(assignedJobs)) {
                    logger.info("   Assigned " + assignedJobs + " jobs.");
                }
                assignedJobs++;
            } else {
                pp.setAttribute("workZone", 0);
                pp.setAttribute("commuteDistance", 0);
            }

        }
        logger.info("   Finished job de.tum.bgu.msm.syntheticPopulationGenerator.germany.AssignJobsByState. Assigned " + assignedJobs + " jobs. "
                + workersOutOfStudyArea + " workers did not find job within 200 km.");

    }

    private Map<Integer, Double> calculateDistanceProbabilityByJobType(String jobType, int origin) {
        Map<Integer, Double> probabilityByTypeAndZone = new HashMap<>();
        TableDataSet jobsByTaz = PropertiesSynPop.get().main.jobsByTaz;
        for (int destination : dataSetSynPop.getVacantJobsByTypeAndZone().get(jobType).keySet()) {
            float distance = dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination);
            //consider only the zones that are within 200 km
            if (distance < 200) {
                boolean readFromTable = false;
                boolean useLongDistanceFormula = true;
                double impendanceDistance = 0;
                double alpha = 0.27;  // 0.6500;  0.6000; 0.5500
                double gamma =-0.020;  //-0.0300; -0.0200;-0.0050
                double alpha_ld = 20;
                double beta_ld = -0.005;
                if (readFromTable) {
                    impendanceDistance = dataSetSynPop.getDistanceUtility().getValueAt(origin, destination);
                } else if (useLongDistanceFormula) {
                    impendanceDistance = Math.exp(beta_ld * distance);
                } else {
                    impendanceDistance = Math.exp(gamma * distance);
                }
                int jobs = (int) jobsByTaz.getValueAt(destination,jobType);
                double probability = 0;
                if (useLongDistanceFormula) {
                    probability = Math.exp(alpha_ld * impendanceDistance) * jobs;
                } else {
                    probability = Math.exp(impendanceDistance * Math.pow(jobs, alpha));
                }
                probabilityByTypeAndZone.putIfAbsent(destination, probability);
            }
        }
        return probabilityByTypeAndZone;
    }


}
