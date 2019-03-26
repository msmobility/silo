package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 * Edited on 05 October 2018 in Munich by Qin Zhang, TUM
 **/
public class MucEducationModelImpl extends AbstractModel implements EducationModel {

    private static final Logger logger = Logger.getLogger(MucEducationModelImpl.class);
    private static final int MIN_PRIMARY_AGE = 6;
    private static final int MIN_SECONDARY_AGE = 10;
    private static final int MIN_TERTIARY_AGE = 18;
    private static final int MAX_EDUCATION_AGE = 24;

    public MucEducationModelImpl(SiloDataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public Collection<EducationEvent> prepareYear(int year) {
        //TODO: Realschuln and Gymnasien
        //TODO: Hard code age and probability or set in the properties?
        final List<EducationEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            Occupation occupation = person.getOccupation();
            switch (occupation){
                case TODDLER:
                    if (person.getAge()>= MIN_PRIMARY_AGE){
                        events.add(new EducationEvent(person.getId()));
                    }
                    break;
                case STUDENT:
                    School oldSchool;
                    if(person.getSchoolId()> 0){
                        oldSchool = dataContainer.getSchoolData().getSchoolFromId(person.getSchoolId());
                    }else{
                        logger.warn("person id " + person.getId()+" has no school." + " Age: " +person.getAge()+" Occupation: "+ person.getOccupation().name());
                        continue;
                    }

                    if (person.getAge() > MIN_SECONDARY_AGE && oldSchool.getType() == 1) {
                        events.add(new EducationEvent(person.getId()));
                    }else if (person.getAge() > MIN_TERTIARY_AGE && oldSchool.getType() == 2) {
                        events.add(new EducationEvent(person.getId()));
                    }else if (person.getAge() > MAX_EDUCATION_AGE && oldSchool.getType() == 3){
                        events.add(new EducationEvent(person.getId()));
                    }
                    break;
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        Person pp = dataContainer.getHouseholdData().getPersonFromId(event.getPersonId());
        if (pp != null) {
            Occupation occupation = pp.getOccupation();
            School newSchool;
            switch (occupation) {
                case TODDLER:
                    newSchool = findSchool(pp, 1);
                    break;
                case STUDENT:
                    int currentSchoolType = dataContainer.getSchoolData().getSchoolFromId(pp.getSchoolId()).getType();
                    if (currentSchoolType == 3) {
                        return leaveSchoolToWork(pp);
                    } else {
                        newSchool = findSchool(pp, currentSchoolType + 1);
                    }
                    break;
                default:
                    logger.warn("person id " + pp.getId() + " couldn't handle update education event, because occupation doesn't fit anymore." +
                            " Age: " + pp.getAge() + " Occupation: " + pp.getOccupation().name());
                    return false;
            }

            if (newSchool != null) {
                return updateEducation(pp, newSchool);
            } else {
                logger.warn("person id " + pp.getId() + " cannot find a new school." +
                        " Age: " + pp.getAge() + " Current school id: " +
                        pp.getSchoolId() + " Home zone id: " +
                        dataContainer.getRealEstateData().getDwelling(pp.getHousehold().getDwellingId()).getZoneId());
            }

        }
        return false;
    }

    @Override
    public void finishYear(int year) {}

    boolean updateEducation(Person person, School school) {

        person.setSchoolId(school.getId());
        person.setOccupation(Occupation.STUDENT);
        school.setOccupancy(school.getOccupancy()-1);

        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school. New school id " + school.getId());
        }
        return true;
    }

    boolean leaveSchoolToWork(Person person) {

        person.setOccupation(Occupation.UNEMPLOYED);
        School school = dataContainer.getSchoolData().getSchoolFromId(person.getSchoolId());
        school.setOccupancy(school.getOccupancy() + 1);
        person.setSchoolId(-1);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " leaved from school to job market. ");
        }
        return true;
    }

    public School findSchool(Person person, int schoolType) {
        return dataContainer.getSchoolData().getClosestSchool(person, schoolType);
    }
}
