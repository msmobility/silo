package de.tum.bgu.msm.data.person;

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
