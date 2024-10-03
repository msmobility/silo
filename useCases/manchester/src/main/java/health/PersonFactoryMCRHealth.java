package health;

import de.tum.bgu.msm.data.person.*;

public class PersonFactoryMCRHealth implements PersonFactory {

    @Override
    public PersonHealthMCR createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonHealthMCR(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonHealthMCR pp = new PersonHealthMCR(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonHealthMCR duplicate(Person originalPerson, int id) {
        PersonHealthMCR duplicate = new PersonHealthMCR(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setSchoolId(((PersonHealthMCR) originalPerson).getSchoolId());
        return duplicate;
    }
}
