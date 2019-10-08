package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.JobImpl;
import org.matsim.api.core.v01.TransportMode;

import java.util.Comparator;

public class PersonUtils {

    private final static PersonFactory factory = new PersonFactoryImpl();

    private PersonUtils() {};

    public static PersonFactory getFactory() {
        return factory;
    }


    public static class PersonByAgeComparator implements Comparator<Person> {
        @Override
        public int compare(Person person1, Person person2) {
            return Integer.compare(person1.getAge(), person2.getAge());
        }
    }
}
