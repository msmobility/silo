package de.tum.bgu.msm.models.demography.driversLicense;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonType;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class DefaultDriversLicenseStrategy implements DriversLicenseStrategy{

    public DefaultDriversLicenseStrategy() {
    }

    @Override
    public double calculateChangeDriversLicenseProbability(Person person) {
        PersonType personType = person.getType();
        switch (personType.name()) {
            case "MEN_AGE_15_TO_19":
                return 0.86;
            case "MEN_AGE_20_TO_24":
                return 0.86;
            case "MEN_AGE_25_TO_29":
                return 0.86;
            case "MEN_AGE_30_TO_34":
                return 0.95;
            case "MEN_AGE_35_TO_39":
                return 0.50;
            case "MEN_AGE_40_TO_44":
                return 0.30;
            case "MEN_AGE_45_TO_49":
                return 0.20;
            case "MEN_AGE_50_TO_54":
                return 0.15;
            case "MEN_AGE_55_TO_59":
                return 0.15;
            case "MEN_AGE_60_TO_64":
                return 0.15;
            case "MEN_AGE_65_TO_69":
                return 0.05;
            case "MEN_AGE_70_TO_74":
                return 0.05;
            case "MEN_AGE_75_TO_79":
                return 0.00;
            case "MEN_AGE_80_TO_84":
                return 0.00;
            case "MEN_AGE_85_TO_89":
                return 0.00;
            case "MEN_AGE_90_TO_94":
                return 0.00;
            case "MEN_AGE_95_TO_99":
                return 0.00;
            case "MEN_AGE_100plus":
                return 0.00;
            case "WOMEN_AGE_15_TO_19":
                return 0.87;
            case "WOMEN_AGE_20_TO_24":
                return 0.87;
            case "WOMEN_AGE_25_TO_29":
                return 0.87;
            case "WOMEN_AGE_30_TO_34":
                return 0.94;
            case "WOMEN_AGE_35_TO_39":
                return 0.50;
            case "WOMEN_AGE_40_TO_44":
                return 0.30;
            case "WOMEN_AGE_45_TO_49":
                return 0.20;
            case "WOMEN_AGE_50_TO_54":
                return 0.15;
            case "WOMEN_AGE_55_TO_59":
                return 0.15;
            case "WOMEN_AGE_60_TO_64":
                return 0.15;
            case "WOMEN_AGE_65_TO_69":
                return 0.05;
            case "WOMEN_AGE_70_TO_74":
                return 0.00;
            case "WOMEN_AGE_75_TO_79":
                return 0.00;
            case "WOMEN_AGE_80_TO_84":
                return 0.00;
            case "WOMEN_AGE_85_TO_89":
                return 0.00;
            case "WOMEN_AGE_90_TO_94":
                return 0.00;
            case "WOMEN_AGE_95_TO_99":
                return 0.00;
            case "WOMEN_AGE_100_PLUS":
                return 0.00;
        }
        return 0.;
    }

    @Override
    public double calculateCreateDriversLicenseProbability(Person pp) {
        PersonType personType = pp.getType();
        switch (personType.name()) {
            case "MEN_AGE_15_TO_19":
                return 0.86;
            case "MEN_AGE_20_TO_24":
                return 0.86;
            case "MEN_AGE_25_TO_29":
                return 0.86;
            case "MEN_AGE_30_TO_34":
                return 0.95;
            case "MEN_AGE_35_TO_39":
                return 0.95;
            case "MEN_AGE_40_TO_44":
                return 0.97;
            case "MEN_AGE_45_TO_49":
                return 0.97;
            case "MEN_AGE_50_TO_54":
                return 0.96;
            case "MEN_AGE_55_TO_59":
                return 0.96;
            case "MEN_AGE_60_TO_64":
                return 0.95;
            case "MEN_AGE_65_TO_69":
                return 0.95;
            case "MEN_AGE_70_TO_74":
                return 0.95;
            case "MEN_AGE_75_TO_79":
                return 0.88;
            case "MEN_AGE_80_TO_84":
                return 0.88;
            case "MEN_AGE_85_TO_89":
                return 0.88;
            case "MEN_AGE_90_TO_94":
                return 0.88;
            case "MEN_AGE_95_TO_99":
                return 0.88;
            case "MEN_AGE_100plus":
                return 0.88;
            case "WOMEN_AGE_15_TO_19":
                return 0.87;
            case "WOMEN_AGE_20_TO_24":
                return 0.87;
            case "WOMEN_AGE_25_TO_29":
                return 0.87;
            case "WOMEN_AGE_30_TO_34":
                return 0.94;
            case "WOMEN_AGE_35_TO_39":
                return 0.94;
            case "WOMEN_AGE_40_TO_44":
                return 0.95;
            case "WOMEN_AGE_45_TO_49":
                return 0.95;
            case "WOMEN_AGE_50_TO_54":
                return 0.89;
            case "WOMEN_AGE_55_TO_59":
                return 0.89;
            case "WOMEN_AGE_60_TO_64":
                return 0.86;
            case "WOMEN_AGE_65_TO_69":
                return 0.71;
            case "WOMEN_AGE_70_TO_74":
                return 0.71;
            case "WOMEN_AGE_75_TO_79":
                return 0.44;
            case "WOMEN_AGE_80_TO_84":
                return 0.44;
            case "WOMEN_AGE_85_TO_89":
                return 0.44;
            case "WOMEN_AGE_90_TO_94":
                return 0.44;
            case "WOMEN_AGE_95_TO_99":
                return 0.44;
            case "WOMEN_AGE_100_PLUS":
                return 0.44;
        }
        return 0.;
    }
}
