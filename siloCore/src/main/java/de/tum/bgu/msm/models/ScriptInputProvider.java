package de.tum.bgu.msm.models;

import java.io.InputStream;

public final class ScriptInputProvider {

    private ScriptInputProvider() {
    }

    private static InputStream divorceProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DivorceProbabilityCalc");
    private static InputStream driversLicenseProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DriversLicenseCalc");
    private static InputStream demolitionScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DemolitionCalc");
    private static InputStream pricingScriptInput
            = ScriptInputProvider.class.getResourceAsStream("PricingCalc");
    private static InputStream renovationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("RenovationCalc");
    private static InputStream movesScriptInput  = ScriptInputProvider.class.getResourceAsStream("MovesOrNotCalc");

    private static InputStream leaveParentalHouseholdScriptInput
            = ScriptInputProvider.class.getResourceAsStream("LeaveParentHhCalc");

    public static InputStream getDriversLicenseProbabilityScriptInput() {
        return driversLicenseProbabilityScriptInput;
    }

    /**
     * TODO
     */
    private static InputStream deathProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DeathProbabilityCalc");

    /**
     * TODO
     */
    private static InputStream marriageProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("MarriageProbabilityCalc");

    public static InputStream getDeathProbabilityScriptInput() {
        return deathProbabilityScriptInput;
    }

    public static InputStream getMarriageProbabilityScriptInput() {
        return marriageProbabilityScriptInput;
    }


    public static InputStream getDivorceProbabilityScriptInput() {
        return divorceProbabilityScriptInput;
    }

    public static InputStream getLeaveParentalHouseholdScriptInput() {
        return leaveParentalHouseholdScriptInput;
    }

    public static InputStream getDemolitionScriptInput() {
        return demolitionScriptInput;
    }

    public static InputStream getPricingScriptInput() {
        return pricingScriptInput;
    }

    public static InputStream getRenovationScriptInput() {
        return renovationScriptInput;
    }

    public static InputStream getMovesScriptInput() {
        return movesScriptInput;
    }

}
