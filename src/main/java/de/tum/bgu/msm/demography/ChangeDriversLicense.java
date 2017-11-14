package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates if someone obtains a drivers license
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class ChangeDriversLicense {
    static Logger logger = Logger.getLogger(ChangeDriversLicense.class);
    private ChangeDriversLicenseJSCalculator calculator;
    private CreateDriversLicenseJSCalculator calculatorCreate;
    private double[] changeDriversLicenseProbability;
    private double[] createDriversLicenseProbability;

    public ChangeDriversLicense() {
        // constructor
        setupChangeDriversLicense();
        setupCreateDriversLicense();
    }

    private void setupCreateDriversLicense() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateDriversLicenseCalc"));
        calculatorCreate = new CreateDriversLicenseJSCalculator(reader, false);

        // initialize results for each alternative
        PersonType[] types = PersonType.values();
        createDriversLicenseProbability = new double[types.length];

        //apply the calculator to each alternative
        for (int i = 0; i < types.length; i++) {
            // set calculator bindings
            calculatorCreate.setPersonType(types[i]);
            //calculate
            try {
                createDriversLicenseProbability[i] = calculatorCreate.calculate();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }

    }

    private void setupChangeDriversLicense(){
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ChangeDriversLicenseCalc"));
        calculator = new ChangeDriversLicenseJSCalculator(reader, false);

        // initialize results for each alternative
        PersonType[] types = PersonType.values();
        changeDriversLicenseProbability = new double[types.length];

        //apply the calculator to each alternative
        for (int i = 0; i < types.length; i++) {
            // set calculator bindings
            calculator.setPersonType(types[i]);
            //calculate
            try {
                changeDriversLicenseProbability[i] = calculator.calculate();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean changeDriversLicense (int perId, SiloDataContainer dataContainer) {
        // check if person obtains a drivers license

        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away
        if (pp.hasDriverLicense()) return false;
        if (pp.getAge() < 18) return  false;
        if (SiloUtil.getRandomNumberAsDouble() < changeDriversLicenseProbability[pp.getType().ordinal()]) {
            pp.setDriverLicense(true);
            EventManager.countEvent(EventTypes.checkDriversLicense);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId +
                    " obtained a drivers license.");
            return true;
        }
        return false;
    }


    public boolean createDriversLicense(int perId){
        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away

        if (SiloUtil.getRandomNumberAsDouble() < createDriversLicenseProbability[pp.getType().ordinal()]) {
            pp.setDriverLicense(true);
            EventManager.countEvent(EventTypes.checkDriversLicense);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId +
                    " obtained a drivers license.");
            return true;
        }
        return false;
    }

}
