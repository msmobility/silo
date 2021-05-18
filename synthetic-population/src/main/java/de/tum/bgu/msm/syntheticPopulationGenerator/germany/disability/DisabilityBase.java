package de.tum.bgu.msm.syntheticPopulationGenerator.germany.disability;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Disability;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class DisabilityBase extends ModuleSynPop {

    private static final Logger logger = Logger.getLogger(DisabilityBase.class);
    private final DataContainer dataContainer;
    private BaseYearDisabilityJSCalculator calculator;

    public DisabilityBase(DataSetSynPop dataSetSynPop, DataContainer dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
/*        Class<? extends DisabilityBase> aClass = this.getClass();
        InputStream baseYearDisabilityCalc =
                aClass.getResourceAsStream("BaseCalc");
        Reader reader = new InputStreamReader(baseYearDisabilityCalc);*/
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("BaseCalc"));
        calculator = new de.tum.bgu.msm.syntheticPopulationGenerator.germany.disability.BaseYearDisabilityJSCalculator(reader);
    }

    @Override
    public void run(){
        if (PropertiesSynPop.get().main.runDisability) {
            logger.info("   Started disability model.");
            for (Person person : dataContainer.getHouseholdDataManager().getPersons()){
                assignDisability(person);
            }
        }
        logger.info("   Completed disability model.");

    }

    private void assignDisability(Person person){
        double disabilityProb = calculator.calculateBaseYearDisabilityProbability(person, person.getGender().getCode());
        if (SiloUtil.getRandomNumberAsDouble() < disabilityProb){
            double disabilityTypeProb = calculator.calculateDisabilityTypeProbability(person, person.getGender().getCode());
            if (SiloUtil.getRandomNumberAsDouble() < disabilityTypeProb){
                ((PersonMuc)person).setAttribute("disability",Disability.PHYSICAL);
            } else {
                ((PersonMuc)person).setAttribute("disability",Disability.MENTAL);
            }
        } else {
            ((PersonMuc)person).setAttribute("disability",Disability.WITHOUT);
        }

    }
}
