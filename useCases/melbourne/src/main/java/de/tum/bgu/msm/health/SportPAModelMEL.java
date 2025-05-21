package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ZoneMEL;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.io.SportPAmodelCoefficientReader;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SportPAModelMEL extends AbstractModel implements ModelUpdateListener {
    private static final Logger logger = LogManager.getLogger(SportPAModelMEL.class);
    private Map<String,Map<String,Double>> coef = new HashMap<>();

    public SportPAModelMEL(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
        this.coef = new SportPAmodelCoefficientReader().readData(properties.healthData.sportPAmodel);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        logger.warn("Sport Physical Activity end year:" + year);
        updateSportPA();
    }

    @Override
    public void endSimulation() {
    }

    public void updateSportPA() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonHealthMEL personHealth = (PersonHealthMEL) person;
            //zero model
            double utility = getPredictor(person, coef.get("zero"));
            double zeroProb = Math.exp(utility)/(1+Math.exp(utility));

            if (random.nextDouble() < zeroProb) {
                personHealth.setWeeklyMarginalMetHoursSport(0.f);
                continue;
            }

            //linear model weekly hour
            double otherSport_wkhr = getPredictor(person, coef.get("linear"));
            personHealth.setWeeklyMarginalMetHoursSport((float) Math.max(0, otherSport_wkhr *3.));
        }
    }

    private double getPredictor(Person person, Map<String, Double> coef) {
        double predictor = 0.0;

        // Intercept
        predictor += coef.get("intercept");

        // gender
        if (person.getGender().equals(Gender.FEMALE)){
            predictor += coef.get("female");
        }

        // age
        if (person.getAge() < 25) {
            predictor += coef.get("age_group_under25");
        }else if (person.getAge() < 35) {
            predictor += coef.get("age_group_25_34");
        }else if (person.getAge() < 45) {
            predictor += coef.get("age_group_35_44");
        }else if (person.getAge() < 55) {
            predictor += coef.get("age_group_45_54");
        }else if (person.getAge() < 65) {
            predictor += coef.get("age_group_55_64");
        }else if (person.getAge() < 75) {
            predictor += coef.get("age_group_65_74");
        }else {
            predictor += coef.get("age_group_over75");
        }

        // occupation
        if (person.getOccupation().equals(Occupation.RETIREE)){
            predictor += coef.get("workstat_retired");
        }else if(person.getOccupation().equals(Occupation.STUDENT)){
            predictor += coef.get("workstat_student");
        }else if(person.getOccupation().equals(Occupation.UNEMPLOYED)){
            predictor += coef.get("workstat_unemployed");
        }

        // imd10
        int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
        ZoneMEL zoneMEL = (ZoneMEL) dataContainer.getGeoData().getZones().get(zoneId);

        predictor += zoneMEL.getImd10() * coef.get("imd10");

        return predictor;
    }

}
