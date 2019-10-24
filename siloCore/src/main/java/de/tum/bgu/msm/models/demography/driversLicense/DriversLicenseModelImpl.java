package de.tum.bgu.msm.models.demography.driversLicense;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.person.LicenseEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Simulates if someone obtains a drivers license
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/

public class DriversLicenseModelImpl extends AbstractModel implements DriversLicenseModel {

    private final DriversLicenseStrategy strategy;

    public DriversLicenseModelImpl(DataContainer dataContainer, Properties properties, DriversLicenseStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
    }

    @Override
    public void setup() {}

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<LicenseEvent> getEventsForCurrentYear(int year) {
        final List<LicenseEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            events.add(new LicenseEvent(person.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(LicenseEvent event) {
        Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        //assign new licenses to adults who does not have one, no license is revoked at any time
        if (pp != null && !pp.hasDriverLicense() && pp.getAge()>= 18) {
            final double changeProb = strategy.calculateChangeDriversLicenseProbability(pp);
            if (random.nextDouble() < changeProb) {
                return createLicense(pp);
            }
        }
        return false;
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {

    }

    @Override
    public void checkLicenseCreation(int perId) {
        Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(perId);
        if (pp == null || pp.getAge() < 17) {
            return;
        }
        final double createProb = strategy.calculateCreateDriversLicenseProbability(pp);
        if (random.nextDouble() < createProb) {
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

