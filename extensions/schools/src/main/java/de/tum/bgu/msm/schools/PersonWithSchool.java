package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.data.person.Person;

public interface PersonWithSchool extends Person {
    void setSchoolType(int i);

    int getSchoolType();

    void setSchoolPlace(int schoolPlace);

    int getSchoolPlace();

    int getSchoolId();

    void setSchoolId(int schoolId);
}
