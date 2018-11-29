package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.person.Person;

import java.util.Map;

public interface PersonReader {

    void readData(String path);

    void copyData(Map<Integer, Person> personMap);
}
