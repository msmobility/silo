package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.simulator.UpdateListener;

import java.util.Collection;

public interface SchoolData extends UpdateListener {
    void addSchool(School ss);

    School getSchoolFromId(int id);

    Collection<School> getSchools();

    School getClosestSchool(Person person, int schoolType);

    void removeSchool(int id);
}
