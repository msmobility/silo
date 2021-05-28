package de.tum.bgu.msm.models.demography.marriage;

import de.tum.bgu.msm.data.person.Person;

public class DefaultMarriageStrategy implements MarriageStrategy{

    public double calculateMarriageProbability(Person person) {
        var probability = 0.;
        var age = person.getAge();
        if(age < 0) {
            throw new RuntimeException("Negative age not allowed!");
        }
        if("MALE".equals(person.getGender().name())) {
            if(age < 15) {
                probability = 0.;
            } else if(age < 20) {
                probability = 0.0003;
            } else if(age < 25) {
                probability = 0.0089;
            } else if(age < 30) {
                probability = 0.0387;
            } else if(age < 35) {
                probability = 0.0592;
            } else if(age < 40) {
                probability = 0.0499;
            } else if(age < 45) {
                probability = 0.0324;
            } else if(age < 50) {
                probability = 0.0247;
            } else if(age < 55) {
                probability = 0.0234;
            } else if(age < 60) {
                probability = 0.0209;
            } else if(age < 65) {
                probability = 0.0174;
            } else if(age < 70) {
                probability = 0.0120;
            } else if(age < 75) {
                probability = 0.0063;
            } else {
                probability = 0.0025;
            }
        } else if("FEMALE".equals(person.getGender().name())) {
            if(age < 15) {
                probability = 0.;
            } else if(age < 20) {
                probability = 0.0018;
            } else if(age < 25) {
                probability = 0.0204;
            } else if(age < 30) {
                probability = 0.0616;
            } else if(age < 35) {
                probability = 0.0721;
            } else if(age < 40) {
                probability = 0.0497;
            } else if(age < 45) {
                probability = 0.0288;
            } else if(age < 50) {
                probability = 0.0240;
            } else if(age < 55) {
                probability = 0.0219;
            } else if(age < 60) {
                probability = 0.0149;
            } else if(age < 65) {
                probability = 0.0080;
            } else if(age < 70) {
                probability = 0.0036;
            } else if(age < 75) {
                probability = 0.0012;
            } else {
                probability = 0.0002;
            }
        } else {
            throw new RuntimeException("Undefined gender " + person.getGender());
        }

        // "/2" because each marriage event affects two persons
        return (probability / 2);
    }
}
