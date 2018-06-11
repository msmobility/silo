package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.CheckHouseholdRelationship;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CheckMarriage {
    private static final Logger logger = Logger.getLogger(CheckMarriage.class);

    private final DataSetSynPop dataSetSynPop;
    private final SiloDataContainer dataContainer;


    public CheckMarriage(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;}

    public void run(){
        int count = 0;
        int countSingle = 0;
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()){
            if (hh.getHhSize() > 1){
                for (Person pp : hh.getPersons()) {
                    if (pp.getRole().equals(PersonRole.MARRIED)){
                        double highestUtil = Double.NEGATIVE_INFINITY;
                        double tempUtil;
                        Person selectedPartner = null;
                        for(Person partner: hh.getPersons()) {
                            if (!partner.equals(pp) && partner.getGender() != pp.getGender() && partner.getRole() == PersonRole.MARRIED) {
                                final int ageDiff = Math.abs(pp.getAge() - partner.getAge());
                                if (ageDiff == 0) {
                                    tempUtil = 2.;
                                } else  {
                                    tempUtil = 1. / ageDiff;
                                }
                                if (tempUtil > highestUtil) {
                                    highestUtil = tempUtil;
                                    selectedPartner = partner;     // find most likely partner
                                }
                            }
                        }
                        if (selectedPartner == null) {
                            boolean foundPartner = false;
                            while (!foundPartner) {
                                for (Person partner : hh.getPersons()) {
                                    if (!partner.equals(pp) && partner.getGender() != pp.getGender() && partner.getRole() == PersonRole.SINGLE) {
                                        partner.setRole(PersonRole.MARRIED);
                                        count++;
                                        foundPartner = true;
                                    }
                                }
                                if (foundPartner == false){
                                    pp.setRole(PersonRole.SINGLE);
                                    countSingle++;
                                    foundPartner = true;
                                }
                            }
                        }
                    }
                }
            }

        }
        logger.info(" " + count + " persons have been married due to inconsistency and " + countSingle + " are single due to inconsistency.");
    }

}
