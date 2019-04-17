package de.tum.bgu.msm.models.demography.leaveParentalHousehold;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultLeaveParentalHouseholdStrategy extends JavaScriptCalculator<Double> implements LeaveParentalHouseholdStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getLeaveParentalHouseholdScriptInput());

    public DefaultLeaveParentalHouseholdStrategy() {
        super(reader);
    }

    @Override
    public double calculateLeaveParentsProbability(Person person) {
        return super.calculate("calculateLeaveParentsProbability", person.getType());
    }
}
