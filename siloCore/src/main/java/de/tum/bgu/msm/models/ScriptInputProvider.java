package de.tum.bgu.msm.models;

import java.io.InputStream;

public final class ScriptInputProvider {

    private ScriptInputProvider() {
    }

    private static InputStream pricingScriptInput
            = ScriptInputProvider.class.getResourceAsStream("PricingCalc");
    private static InputStream renovationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("RenovationCalc");
    private static InputStream movesScriptInput  = ScriptInputProvider.class.getResourceAsStream("MovesOrNotCalc");

    private static InputStream leaveParentalHouseholdScriptInput
            = ScriptInputProvider.class.getResourceAsStream("LeaveParentHhCalc");

    /**
     * TODO
     */
    private static InputStream marriageProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("MarriageProbabilityCalc");

    public static InputStream getMarriageProbabilityScriptInput() {
        return marriageProbabilityScriptInput;
    }

    public static InputStream getLeaveParentalHouseholdScriptInput() {
        return leaveParentalHouseholdScriptInput;
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
