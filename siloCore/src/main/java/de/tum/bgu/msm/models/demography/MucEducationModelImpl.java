package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.AbstractModel;

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

    public MucEducationModelImpl(SiloDataContainer dataContainer) {
        super(dataContainer);
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        //TODO: Event handled randomly, so it might happen that birthday event always happened before
        //TODO: Realschuln and Gymnasien
        //TODO: Hard code age and probability or set in the properties?
        Person pp = dataContainer.getHouseholdData().getPersonFromId(event.getPersonId());
        if (pp != null) {
            School findSchool = null;
            if (pp.getAge() >= 6 && pp.getAge() <= 10 && pp.getSchoolType() == 0) {
                findSchool = findSchool(pp);
            }else if (pp.getAge() > 10 && pp.getSchoolType() == 1) {
                findSchool = findSchool(pp);
            }else if (pp.getAge() > 18 && pp.getSchoolType() == 2) {
                if (SiloUtil.getRandomNumberAsFloat() < 0.528){
                    findSchool = findSchool(pp);
                }else{
                    return leaveSchoolToWork(pp,pp.getSchoolType());
                }
            }else if (pp.getAge() > 24 && pp.getSchoolType() == 3){
                return  leaveSchoolToWork(pp,pp.getSchoolType());
            }

            if (findSchool != null){
                return updateEducation(pp, findSchool);
            }
        }

        return false;
    }

    //TODO: leave school to work
    boolean leaveSchoolToWork(Person person, int educationLevel) {

        person.setOccupation(Occupation.UNEMPLOYED);
        person.setSchoolType(0);
        person.setSchoolId(-1);
        person.setSchoolCoordinate(null,-1);
        //TODO: schoolType and educationLevel code needs to be aligned! 09 Oct 2018 QZ'
        person.setEducationLevel(educationLevel);

        School school = dataContainer.getSchoolData().getSchoolFromId(person.getSchoolId());
        school.setOccupancy(school.getOccupancy() + 1);

        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " leaved from school to job market. ");
        }

        return true;
    }


    @Override
    public void finishYear(int year) {}

    @Override
    public Collection<EducationEvent> prepareYear(int year) {
        //TODO:
        final List<EducationEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            events.add(new EducationEvent(person.getId()));
        }
        return events;
    }


    boolean updateEducation(Person person, School school) {

        person.setSchoolType(school.getType());
        person.setSchoolId(school.getId());
        person.setOccupation(Occupation.STUDENT);
        person.setSchoolCoordinate(((MicroLocation)school).getCoordinate(),school.getZoneId());
        school.setOccupancy(school.getOccupancy()-1);

        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school. New school id " + school.getId());
        }
        return true;
    }

    public School findSchool(Person person) {
        return dataContainer.getSchoolData().getClosestSchool(person);
    }


}
