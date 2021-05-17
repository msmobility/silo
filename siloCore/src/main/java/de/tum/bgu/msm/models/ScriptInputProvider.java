package de.tum.bgu.msm.models;

import java.io.InputStream;

public final class ScriptInputProvider {

    private ScriptInputProvider() {
    }

    private static InputStream renovationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("RenovationCalc");
    private static InputStream movesScriptInput  = ScriptInputProvider.class.getResourceAsStream("MovesOrNotCalc");

    /**
     * TODO
     */
    private static InputStream marriageProbabilityScriptInput
            = ScriptInputProvider.class.getResourceAsStream("MarriageProbabilityCalc");

    public static InputStream getMarriageProbabilityScriptInput() {
        return marriageProbabilityScriptInput;
    }

    public static InputStream getRenovationScriptInput() {
        return renovationScriptInput;
    }

    public static InputStream getMovesScriptInput() {
        return movesScriptInput;
    }
}
