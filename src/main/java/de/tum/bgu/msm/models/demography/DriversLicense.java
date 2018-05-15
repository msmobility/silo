package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.models.AbstractModel;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates if someone obtains a drivers license
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class DriversLicense extends AbstractModel {

    private LicenseJSCalculator calculator;

    public DriversLicense(SiloDataContainer dataContainer) {
        super(dataContainer);
        setup();
    }

    private void setup() {
        final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DriverLicenseCalc"));
        calculator = new LicenseJSCalculator(reader);

    }

    public void checkLicenseChange(int perId) {
        Person pp = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (pp == null || pp.hasDriverLicense() || pp.getAge() < 18) {
            return;
        }
        final double changeProb = calculator.calculateChangeDriversLicenseProbability(pp.getType());
        if (SiloUtil.getRandomNumberAsDouble() < changeProb) {
            createLicense(pp);
        }
    }

    public void checkLicenseCreation(int perId) {
        Person pp = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (pp == null || pp.getAge() < 17) {
            return;
        }
        final double createProb = calculator.calculateCreateDriversLicenseProbability(pp.getType());
        if (SiloUtil.getRandomNumberAsDouble() < createProb) {
            createLicense(pp);
        }
    }

    void createLicense(Person person) {
        person.setDriverLicense(true);
        EventManager.countEvent(EventTypes.CHECK_DRIVERS_LICENSE);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " obtained a drivers license.");
        }
    }
}
