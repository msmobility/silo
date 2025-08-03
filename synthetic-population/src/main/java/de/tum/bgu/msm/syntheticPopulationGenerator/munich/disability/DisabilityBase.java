package de.tum.bgu.msm.syntheticPopulationGenerator.munich.disability;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Disability;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMucDisability;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class DisabilityBase extends ModuleSynPop {

    private static final Logger logger = LogManager.getLogger(Allocation.class);
    private final DataContainer dataContainer;
    private BaseYearDisabilityJSCalculator calculator;

    public DisabilityBase(DataSetSynPop dataSetSynPop, DataContainer dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
        Class<? extends DisabilityBase> aClass = this.getClass();
        InputStream baseYearDisabilityCalc =
                aClass.getResourceAsStream("BaseCalc");
        Reader reader = new InputStreamReader(baseYearDisabilityCalc);
        calculator = new BaseYearDisabilityJSCalculator(reader);
    }

    @Override
    public void run(){
        if (PropertiesSynPop.get().main.runDisability) {
            logger.info("   Started disability model.");
            for (Person person : dataContainer.getHouseholdDataManager().getPersons()){
                assignDisability(person);
            }
        }
/*        int i = 0;
        HouseholdDataManager dataManager = dataContainer.getHouseholdDataManager();
        for (Household household : dataManager.getHouseholds()){
            if (i < 4){
                dataManager.removeHousehold(household.getId());
                i++;
            } else {
                i = 0;
            }
        }*/
        logger.info("   Completed disability model.");

    }

    private void assignDisability(Person person){
        double disabilityProb = calculator.calculateBaseYearDisabilityProbability(person, person.getGender().getCode());
        if (SiloUtil.getRandomNumberAsDouble() < disabilityProb){
            double disabilityTypeProb = calculator.calculateDisabilityTypeProbability(person, person.getGender().getCode());
            if (SiloUtil.getRandomNumberAsDouble() < disabilityTypeProb){
                person.setAttribute("disability",Disability.PHYSICAL);
            } else {
                person.setAttribute("disability",Disability.MENTAL);
            }
        } else {
            person.setAttribute("disability",Disability.WITHOUT);
        }

    }
}
