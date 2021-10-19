package de.tum.bgu.msm.models.demography.leaveParentalHousehold;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonType;

public class DefaultLeaveParentalHouseholdStrategy implements LeaveParentalHouseholdStrategy {

    @Override
    public double calculateLeaveParentsProbability(Person person) {
        PersonType type = person.getType();
        var alpha = 0.;
        var typeCode = type.ordinal();

        if (typeCode > 43){
            throw new Error("Undefined person type!");
        } else if (typeCode < 0){
            throw new Error("Undefined person type!");
        }

        if (typeCode == 4){
            alpha = 0.0143;
        } else if (typeCode == 5){
            alpha = 0.0398;
        } else if (typeCode == 6){
            alpha = 0.0214;
        } else if (typeCode == 7){
            alpha = 0.0064;
        } else if (typeCode == 8){
            alpha = 0.0018;
        } else if (typeCode == 9){
            alpha = 0.0006;
        } else if (typeCode == 10){
            alpha = 0.0001;
        } else if (typeCode == 26){
            alpha = 0.0202;
        } else if (typeCode == 27){
            alpha = 0.0432;
        } else if (typeCode == 28){
            alpha = 0.0192;
        } else if (typeCode == 29){
            alpha = 0.0038;
        } else if (typeCode == 30){
            alpha = 0.0006;
        } else if (typeCode == 31){
            alpha = 0.0003;
        } else if (typeCode == 32){
            alpha = 0.0001;
        }

        return alpha;
    }
}
