package de.tum.bgu.msm.models;

import java.io.InputStream;

public final class ScriptInputProvider {

    private ScriptInputProvider() {
    }

    private static InputStream renovationScriptInput
            = ScriptInputProvider.class.getResourceAsStream("RenovationCalc");


    public static InputStream getRenovationScriptInput() {
        return renovationScriptInput;
    }

}
