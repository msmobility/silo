package de.tum.bgu.msm.utils;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author nkuehnel
 */
public class IssueCounter {

    Logger logger = Logger.getLogger(IssueCounter.class);

    public final Set<String> issues = new ConcurrentSkipListSet<>();

    public void trackIssue(String issue) {
        issues.add(issue);
    }

    public void endYear() {
        for(String issue: issues) {
            logger.warn(issue);
        }
        issues.clear();
    }
}
