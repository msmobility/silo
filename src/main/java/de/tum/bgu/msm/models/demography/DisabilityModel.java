package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Disability;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.models.AbstractModel;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates if someone is assigned with a mental or physical disability
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 9 June 2018 in Munich
 **/

public class DisabilityModel extends AbstractModel {

    private static DisabilityJSCalculator calculator;

    public DisabilityModel(SiloDataContainer dataContainer){
        super(dataContainer);
        setupDisabilityModel();
    }


    private void setupDisabilityModel(){
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DisabilityProbabilityCalcMuc"));
        calculator = new DisabilityJSCalculator(reader);
    }


    public void chooseDisability(int perId){
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(perId);
        if(!EventRules.ruleDisability(person)){
            return;
        }
        if (!person.getDisability().equals(Disability.without)){
            return;           //Exclude persons that already have a disability
        }
        final double disabilityProb = calculator.calculateDisabilityProbability(person.getAge(), person.getGender());
        if (SiloUtil.getRandomNumberAsDouble() < disabilityProb){
        final double disabilityTypeProb = calculator.calculateDisabilityTypeProbability(person.getAge(), person.getGender());
            if (SiloUtil.getRandomNumberAsDouble() < disabilityTypeProb) {
                createDisability(person, Disability.physical);
            } else {
                createDisability(person, Disability.mental);
            }
        }
    }

    void createDisability(Person person, Disability disability){
        person.setDisability(disability);
        EventManager.countEvent(EventTypes.DISABILITY);
        if (person.getId() == SiloUtil.trackPp){
            SiloUtil.trackWriter.println("Person " + person.getId() +
                " is assigned a " + person.getDisability() + " disability.");

        }
    }

}
