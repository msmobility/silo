package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.data.*;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.health.disease.RelativeRisksDisease;
import de.tum.bgu.msm.health.injury.AccidentModel;
import de.tum.bgu.msm.health.injury.AccidentType;
import de.tum.bgu.msm.health.io.TripReaderMucHealth;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.MitoUtil;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.config.Config;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DiseaseModelMCR extends AbstractModel implements ModelUpdateListener {
    private static final Logger logger = Logger.getLogger(DiseaseModelMCR.class);

    public DiseaseModelMCR(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    @Override
    public void setup() { }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void endYear(int year) {
        logger.warn("Health disease model end year:" + year);
        calculateRelativeRisk();
        updateDiseaseProbability(Boolean.FALSE);
        updateHealthDiseaseStates(year);
    }

    @Override
    public void endSimulation() {
    }


    public void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            EnumMap<Diseases, Float> rr_PA = RelativeRisksDisease.calculateForPA((PersonHealth)person, (DataContainerHealth) dataContainer);
            ((PersonHealth)person).getRelativeRisksByDisease().put(HealthExposures.PHYSICAL_ACTIVITY, rr_PA);

            //EnumMap<Diseases, Float> rr_AP = RelativeRisksDisease.calculateForAP(personMRC, (DataContainerHealth) dataContainer);
            //personMRC.getRelativeRisksByDisease().put(HealthExposures.AIR_POLLUTION, rr_AP);
            //TODO: do we combine all RR to calculate all cause rr?
            //personMRC.setAllCauseRR(relativeRisks.values().stream().reduce(1.f, (a, b) -> a*b));
        }
    }

    public void updateDiseaseProbability(Boolean adjustByRelativeRisk) {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            for(Diseases diseases : Diseases.values()){
                double sickProb = 0.;
                if(adjustByRelativeRisk){
                    //TODO: how to adjust sick prob by rr?
                }else{
                    sickProb = ((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(person.getGender()).get(Math.min(person.getAge(), 100));
                }
                ((PersonHealth)person).getCurrentDiseaseProb().put(diseases, (float) sickProb);
            }
        }
    }

    public void updateHealthDiseaseStates(int year) {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonHealth personHealth = (PersonHealth) person;
            List<String> newDisease = new ArrayList<>();

            for(Diseases diseases : Diseases.values()){
                if(diseases.equals(Diseases.all_cause)){
                    continue;
                }

                if(random.nextFloat()<(personHealth.getCurrentDiseaseProb().get(diseases))){
                    if(!personHealth.getCurrentDisease().contains(diseases)){
                        personHealth.getCurrentDisease().add(diseases);
                        newDisease.add(diseases.toString());
                    }
                }
            }

            //update Disease track map
            if(newDisease.isEmpty()){
                if(year == Properties.get().main.baseYear){
                    personHealth.getHealthDiseaseTracker().put(year, Arrays.asList("health"));
                }else {
                    personHealth.getHealthDiseaseTracker().put(year, personHealth.getHealthDiseaseTracker().get(year-1));
                }
            }else {
                personHealth.getHealthDiseaseTracker().put(year, newDisease);
            }
        }
    }

}
