package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.LicenseEvent;
import de.tum.bgu.msm.models.AbstractModel;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates if someone obtains a drivers license
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class DriversLicense extends AbstractModel implements MicroEventModel<LicenseEvent> {

    private LicenseJSCalculator calculator;

    public DriversLicense(SiloDataContainer dataContainer) {
        super(dataContainer);
        setup();
    }

    private void setup() {
        final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DriverLicenseCalc"));
        calculator = new LicenseJSCalculator(reader);
    }

    @Override
    public Collection<LicenseEvent> prepareYear(int year) {
        final List<LicenseEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdData().getPersons()) {
            events.add(new LicenseEvent(person.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(LicenseEvent event) {
        Person pp = dataContainer.getHouseholdData().getPersonFromId(event.getPersonId());
        if (pp != null && pp.hasDriverLicense() && pp.getAge() < 18) {
            final double changeProb = calculator.calculateChangeDriversLicenseProbability(pp.getType());
            if (SiloUtil.getRandomNumberAsDouble() < changeProb) {
                return createLicense(pp);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
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

    boolean createLicense(Person person) {
        person.setDriverLicense(true);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " obtained a drivers license.");
        }
        return true;
    }
}
