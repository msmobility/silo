package de.tum.bgu.msm.models;

import java.io.InputStream;

public final class ScriptInputProvider {

    private static InputStream divorceProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DivorceProbabilityCalc");
    private static InputStream driversLicenseProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DriversLicenseCalc");
    private static InputStream constructionLocationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("ConstructionLocationCalc");
    private static InputStream constructionDemandScriptInput
            = ScriptInputProvider.class.getResourceAsStream("ConstructionDemandCalc");
    private static InputStream demolitionScriptInput
            = ScriptInputProvider.class.getResourceAsStream("DemolitionCalc");
    private static InputStream pricingScriptInput
            = ScriptInputProvider.class.getResourceAsStream("PricingCalc");
    private static InputStream renovationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("RenovationCalc");
    private static InputStream movesScriptInput
            = ScriptInputProvider.class.getResourceAsStream("MovesOrNotCalc");

    public static void setMovesScriptInput(InputStream movesScriptInput) {
        ScriptInputProvider.movesScriptInput = movesScriptInput;
    }

    public static void setRenovationScriptInput(InputStream renovationScriptInput) {
        ScriptInputProvider.renovationScriptInput = renovationScriptInput;
    }

    public static void setPricingScriptInput(InputStream pricingScriptInput) {
        ScriptInputProvider.pricingScriptInput = pricingScriptInput;
    }

    public static void setDemolitionScriptInput(InputStream demolitionScriptInput) {
        ScriptInputProvider.demolitionScriptInput = demolitionScriptInput;
    }

    public static void setConstructionDemandScriptInput(InputStream constructionDemandScriptInput) {
        ScriptInputProvider.constructionDemandScriptInput = constructionDemandScriptInput;
    }

    public static void setConstructionLocationScriptInput(InputStream constructionLocationScriptInput) {
        ScriptInputProvider.constructionLocationScriptInput = constructionLocationScriptInput;
    }

    public static void setLeaveParentalHouseholdScriptInput(InputStream leaveParentalHouseholdScriptInput) {
        ScriptInputProvider.leaveParentalHouseholdScriptInput = leaveParentalHouseholdScriptInput;
    }

    private static InputStream leaveParentalHouseholdScriptInput
            = ScriptInputProvider.class.getResourceAsStream("LeaveParentHhCalc");

    public static InputStream getDriversLicenseProbabilityScriptInput() {
        return driversLicenseProbabilityScriptInput;
    }

    public static void setDriversLicenseProbabilityScriptInput(InputStream driversLicenseProbabilityScriptInput) {
        ScriptInputProvider.driversLicenseProbabilityScriptInput = driversLicenseProbabilityScriptInput;
    }

    public static void setDivorceProbabilityScriptInput(InputStream divorceProbabilityScriptInput) {
        ScriptInputProvider.divorceProbabilityScriptInput = divorceProbabilityScriptInput;
    }

    private ScriptInputProvider(){};

    /**
     * TODO
     */
    private static InputStream birthProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("BirthProbabilityCalc");

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

    public static InputStream getBirthProbabilityScriptInput() {
        return birthProbabilityScriptInput;
    }

    public static void setBirthProbabilityScriptInput(InputStream birthProbabilityScriptInput) {
        ScriptInputProvider.birthProbabilityScriptInput = birthProbabilityScriptInput;
    }

    public static InputStream getDeathProbabilityScriptInput() {
        return deathProbabilityScriptInput;
    }

    public static void setDeathProbabilityScriptInput(InputStream deathProbabilityScriptInput) {
        ScriptInputProvider.deathProbabilityScriptInput = deathProbabilityScriptInput;
    }

    /**
     * TODO
     * @return
     */
    public static InputStream getBirthScriptInputStream() {
        return birthProbabilityScriptInput;
    }

    /**
     * TODO
     * @param stream
     */
    public static void setBirthScriptInputStream(InputStream stream) {
        birthProbabilityScriptInput = stream;
    }

    public static InputStream getMarriageProbabilityScriptInput() {
        return marriageProbabilityScriptInput;
    }

    public static void setMarriageProbabilityScriptInput(InputStream marriageProbabilityScriptInput) {
        ScriptInputProvider.marriageProbabilityScriptInput = marriageProbabilityScriptInput;
    }

    public static InputStream getDivorceProbabilityScriptInput() {
        return divorceProbabilityScriptInput;
    }

    public static InputStream getLeaveParentalHouseholdScriptInput() {
        return leaveParentalHouseholdScriptInput;
    }

    public static InputStream getConstructionLocationScriptInput() {
        return constructionLocationScriptInput;
    }

    public static InputStream getConstructionDemandScriptInput() {
        return constructionDemandScriptInput;
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
