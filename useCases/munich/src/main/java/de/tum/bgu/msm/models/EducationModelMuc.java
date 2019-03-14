package de.tum.bgu.msm.models;

import de.tum.bgu.msm.DataContainerMuc;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.demography.education.EducationModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
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
public class EducationModelMuc extends AbstractModel implements EducationModel {

    private static final Logger logger = Logger.getLogger(EducationModelMuc.class);
    public EducationModelMuc(DataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public Collection<EducationEvent> getEventsForCurrentYear(int year) {
        //TODO:
        final List<EducationEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdDataManager().getPersons()) {
            //TODO: check if really every person needs a status check
            events.add(new EducationEvent(person.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        //TODO: Event handled randomly, so it might happen that birthday event always happened before
        //TODO: Realschuln and Gymnasien
        //TODO: Hard code age and probability or set in the properties?
        Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (pp != null) {
            School oldSchool = null;
            School newSchool = null;
            if(pp.getSchoolId()>0) {
                 oldSchool = ((DataContainerMuc)dataContainer).getSchoolData().getSchoolFromId(pp.getSchoolId());
            }else{
//                logger.warn("person id " + pp.getId()+" has no school" + "Age " +pp.getAge()+"Occupation "+ pp.getOccupation().name());
                return false;
            }

            if (pp.getAge() >= 6 && pp.getAge() <= 10 && oldSchool == null) {
                newSchool = findSchool(pp);
            }else if (pp.getAge() > 10 && oldSchool.getType() == 1) {
                newSchool = findSchool(pp);
            }else if (pp.getAge() > 18 && oldSchool.getType() == 2) {
                if (SiloUtil.getRandomNumberAsFloat() < 0.528){
                    newSchool = findSchool(pp);
                }else{
                    return leaveSchoolToWork(pp);
                }
            }else if (pp.getAge() > 24 && oldSchool.getType() == 3){
                return  leaveSchoolToWork(pp);
            }

            if (newSchool != null){
                return updateEducation(pp, newSchool);
            }
        }

        return false;
    }

    //TODO: leave school to work
    boolean leaveSchoolToWork(Person person) {

        person.setOccupation(Occupation.UNEMPLOYED);
        School school = ((DataContainerMuc)dataContainer).getSchoolData().getSchoolFromId(person.getSchoolId());
        school.setOccupancy(school.getOccupancy() + 1);
        person.setSchoolId(-1);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " leaved from school to job market. ");
        }
        return true;
    }


    @Override
    public void endYear(int year) {}

    @Override
    public void endSimulation() {

    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {}


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

    School findSchool(Person person) {
        int currentSchoolType = ((DataContainerMuc)dataContainer).getSchoolData().getSchoolFromId(person.getSchoolId()).getType();
        return ((DataContainerMuc)dataContainer).getSchoolData().getClosestSchool(person, currentSchoolType+1);
    }
}
