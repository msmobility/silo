package de.tum.bgu.msm.models;

import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.demography.education.EducationModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 * Edited on 05 October 2018 in Munich by Qin Zhang, TUM
 **/
public class EducationModelMuc extends AbstractModel implements EducationModel {

    private static final Logger logger = Logger.getLogger(EducationModelMuc.class);
    private static final int MIN_PRIMARY_AGE = 6;
    private static final int MIN_SECONDARY_AGE = 10;
    private static final int MIN_TERTIARY_AGE = 18;
    private static final int MAX_EDUCATION_AGE = 24;

    public EducationModelMuc(DataContainerWithSchools dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    @Override
    public Collection<EducationEvent> getEventsForCurrentYear(int year) {
        //TODO: Realschuln and Gymnasien
        //TODO: Hard code age and probability or set in the properties?
        final List<EducationEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdDataManager().getPersons()) {
            Occupation occupation = person.getOccupation();
            switch (occupation){
                case TODDLER:
                    if (person.getAge()>= MIN_PRIMARY_AGE){
                        events.add(new EducationEvent(person.getId()));
                    }
                    break;
                case STUDENT:
                    int oldSchoolType = -1;
                    if(((PersonMuc)person).getSchoolId()> 0){
                        oldSchoolType = ((DataContainerWithSchoolsImpl)dataContainer).getSchoolData().getSchoolFromId(((PersonMuc)person).getSchoolId()).getType();
                    }else if(((PersonMuc)person).getSchoolId()== -2){
                        oldSchoolType = SchoolDataImpl.guessSchoolType((PersonWithSchool) person);
                    }else{
                        logger.warn("person id " + person.getId()+" has no school." + " Age: " +person.getAge()+" Occupation: "+ person.getOccupation().name());
                        continue;
                    }

                    if (person.getAge() > MIN_SECONDARY_AGE && oldSchoolType == 1) {
                        events.add(new EducationEvent(person.getId()));
                    }else if (person.getAge() > MIN_TERTIARY_AGE && oldSchoolType == 2) {
                        events.add(new EducationEvent(person.getId()));
                    }else if (person.getAge() > MAX_EDUCATION_AGE && oldSchoolType == 3){
                        events.add(new EducationEvent(person.getId()));
                    }
                    break;
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        PersonMuc pp = (PersonMuc) dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (pp != null) {
            Occupation occupation = pp.getOccupation();
            School newSchool;
            switch (occupation) {
                case TODDLER:
                    newSchool = findSchool(pp, 1);
                    break;
                case STUDENT:
                    int currentSchoolType = -1;
                    if(pp.getSchoolId()== -2) {
                        currentSchoolType = pp.getSchoolType();
                    }else{
                        currentSchoolType = ((DataContainerWithSchoolsImpl)dataContainer).getSchoolData().getSchoolFromId(pp.getSchoolId()).getType();
                    }

                    if (currentSchoolType == 3) {
                        return leaveSchoolToWork(pp);
                    } else {
                        newSchool = findSchool(pp, currentSchoolType + 1);
                    }
                    break;
                default:
                    //logger.warn("person id " + pp.getId() + " couldn't handle update education event, because occupation doesn't fit anymore." +
                    //        " Age: " + pp.getAge() + " Occupation: " + pp.getOccupation().name());
                    return false;
            }

            if (newSchool != null) {
                return updateEducation(pp, newSchool);
            } else {
                logger.warn("person id " + pp.getId() + " cannot find a new school." +
                        " Age: " + pp.getAge() + " Current school id: " +
                        pp.getSchoolId() + " Home zone id: " +
                        dataContainer.getRealEstateDataManager().getDwelling(pp.getHousehold().getDwellingId()).getZoneId());
            }

        }
        return false;
    }

    boolean updateEducation(PersonMuc person, School school) {

        person.setSchoolId(school.getId());
        person.setOccupation(Occupation.STUDENT);
        school.setOccupancy(school.getOccupancy()-1);

        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school. New school id " + school.getId());
        }
        return true;
    }

    boolean leaveSchoolToWork(PersonMuc person) {

        person.setOccupation(Occupation.UNEMPLOYED);

        if(person.getSchoolId()== -2) {
            person.setSchoolId(-1);
            person.setSchoolType(-1);
        }else{
            School school = ((DataContainerWithSchoolsImpl)dataContainer).getSchoolData().getSchoolFromId(person.getSchoolId());
            school.setOccupancy(school.getOccupancy() + 1);
            person.setSchoolId(-1);
        }

        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " leaved from school to job market. ");
        }
        return true;
    }

    public School findSchool(Person person, int schoolType) {
        return ((DataContainerWithSchoolsImpl)dataContainer).getSchoolData().getClosestSchool(person, schoolType);
    }
}
