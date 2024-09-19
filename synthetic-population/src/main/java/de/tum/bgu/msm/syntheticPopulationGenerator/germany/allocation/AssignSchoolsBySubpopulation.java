package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.School;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AssignSchoolsBySubpopulation {

    private static final Logger logger = Logger.getLogger(AssignSchoolsBySubpopulation.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainerWithSchools dataContainer;
    private int assignedJobs;



    public AssignSchoolsBySubpopulation(DataContainerWithSchools dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }


    public void run() {
        logger.info("   Running module: job de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.AssignSchools");

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager households = dataContainer.getHouseholdDataManager();
        double alpha_ld = 20;
        double beta_ld = -0.01092;
        int studentsOutOfStudyArea = 0;
        int allLongerThan200 = 0;
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            if (pp.getOccupation() == Occupation.STUDENT) {

                int schoolType = Integer.parseInt(pp.getAttribute("schoolType").get().toString());
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int destination = -2;
                School school = null;
                Map<Integer, Double> probabilities = calculateDistanceProbabilityBySchoolType(schoolType, origin, alpha_ld, beta_ld);
                if (!probabilities.isEmpty()) {
/*                    if (probabilities.keySet().size() == 1 & probabilities.containsKey(origin)) {
                        if (!dataSetSynPop.getZoneSchoolTypeSchoolLocationVacancy().get(schoolType).containsKey(origin)) {
                            allLongerThan200++;
                            destination = origin;
                            logger.info(" Student in zone " + origin + " has no probability within 200 km. Person id " + pp.getId());
                        }
                    } else {*/
                        if (schoolType == 3) {
                            destination = SiloUtil.select(probabilities);
                            int selectedSchoolID = SiloUtil.select(dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().get(schoolType).get(destination));
                            school = dataContainer.getSchoolData().getSchoolFromId(selectedSchoolID);
/*                            int remainingCapacity = dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().get(schoolType).get(destination).get(selectedSchoolID) - 1;
                            dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().get(schoolType).get(destination).put(selectedSchoolID, remainingCapacity);
                            int remainingSchools = dataSetSynPop.getZoneSchoolTypeSchoolLocationVacancy().get(schoolType).get(destination) - 1;
                            if (remainingSchools > 0) {
                                dataSetSynPop.getZoneSchoolTypeSchoolLocationVacancy().get(schoolType).put(destination, remainingSchools);
                            } else {
                                dataSetSynPop.getZoneSchoolTypeSchoolLocationVacancy().get(schoolType).remove(destination);
                                if (dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().isEmpty()) {
                                    studentsOutOfStudyArea++;
                                }
                            }*/
                        } else {
                            destination = origin;
                            int selectedSchoolID = SiloUtil.select(dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().get(schoolType).get(destination));
                            school = dataContainer.getSchoolData().getSchoolFromId(selectedSchoolID);
                        }
/*                       if (assignedJobs > 65536) {
                            logger.info(" Student " + pp.getId() + " level " + schoolType + " from zone " + origin);
                        }*/

                        studentsOutOfStudyArea++;
                    } else {

                }
                //}
                pp.setAttribute("schoolPlace", destination);
                pp.setAttribute("schoolId", school.getId());
                if (destination != -2) {
                    pp.setAttribute("commuteEduDistance", dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination));
                } else {
                    pp.setAttribute("commuteEduDistance", 0);
                }


                if (LongMath.isPowerOfTwo(assignedJobs)) {
                    logger.info("   Assigned " + assignedJobs + " schools.");
                }
                assignedJobs++;
            } else {
                pp.setAttribute("schoolPlace", 0);
                pp.setAttribute("schoolId", 0);
                pp.setAttribute("commuteEduDistance", 0);
            }

        }
        logger.info("   Finished job de.tum.bgu.msm.syntheticPopulationGenerator.germany.AssignSchoolsByState. Assigned " + assignedJobs + " schools. "
                + studentsOutOfStudyArea + " workers did not find job within 200 km." + allLongerThan200 + " persons in their zone because all longer than 200 km.");

    }

    private Map<Integer, Double> calculateDistanceProbabilityBySchoolType(int schoolType, int origin, double alpha_ld, double beta_ld) {
        Map<Integer, Double> probabilityByTypeAndZone = new HashMap<>();
        TableDataSet jobsByTaz = PropertiesSynPop.get().main.jobsByTaz;
        if (schoolType == 3) {
            float minDistance = 10000;
            int zoneMinDistance = -1;
            for (int destination : dataSetSynPop.getZoneSchoolTypeSchoolLocationCapacity().get(schoolType).keySet()) {
                float distance = dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination);
                //consider only the zones that are within 200 km
                if (distance < 200) {
                    int jobs = dataSetSynPop.getSchoolCapacity().get(destination, schoolType);
                    double impendanceDistance = Math.exp(beta_ld * distance);
                    double probability = Math.exp(alpha_ld * impendanceDistance) * jobs;
                    probabilityByTypeAndZone.putIfAbsent(destination, probability);
                }
                if (distance < minDistance){
                    minDistance = distance;
                    zoneMinDistance = destination;
                }
            }
            if (probabilityByTypeAndZone.isEmpty()) {
                probabilityByTypeAndZone.put(zoneMinDistance, 1.);
            }
        } else {
            probabilityByTypeAndZone.put(origin, 1.);
        }
        return probabilityByTypeAndZone;
    }


}
