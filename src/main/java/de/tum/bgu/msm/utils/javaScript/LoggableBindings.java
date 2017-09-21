package de.tum.bgu.msm.utils.javaScript;

import org.apache.log4j.Logger;

import javax.script.SimpleBindings;

public class LoggableBindings extends SimpleBindings {

    public static final Logger logger = Logger.getLogger(LoggableBindings.class);

    public void logValues() {
        logger.debug("Bound values: ");
        for(Entry<String, Object> entry: this.entrySet()) {
            logger.debug(entry.getKey() + " = " + entry.getValue());
        }
    }
}
