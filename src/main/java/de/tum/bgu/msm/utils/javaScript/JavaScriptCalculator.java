package de.tum.bgu.msm.utils.javaScript;

import org.apache.log4j.Logger;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Skeleton class for java script calculations
 */
public abstract class JavaScriptCalculator<T> {

    private static final Logger logger = Logger.getLogger(JavaScriptCalculator.class);

    private CompiledScript compiledScript;
    protected LoggableBindings bindings = new LoggableBindings();


    protected JavaScriptCalculator(Reader reader, boolean log) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder scriptBuilder = new StringBuilder();
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                scriptBuilder.append(line + "\n");
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            logger.fatal("Error in reading script!", e);
        }
        logger.debug("Compiling script: " + scriptBuilder.toString());
        Compilable compileEngine = (Compilable) engine;
        try {
            compiledScript = compileEngine.compile(scriptBuilder.toString());
        } catch (ScriptException e) {
            logger.fatal("Error in input script!", e);
            e.printStackTrace();
        }
        bindings.put("log", log);
    }

    public T calculate() throws ScriptException {
        bindings.logValues();
        return (T) compiledScript.eval(bindings);
    }
}
