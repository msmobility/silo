package de.tum.bgu.msm.utils;

import cern.colt.Timer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

public class TimeTracker {

    private int currentYear;
    private final Multiset<String> currentYearRecords = HashMultiset.create();
    private final Timer timer = new Timer().start();
    private final Table<Integer, String, Integer> timeRecords = HashBasedTable.create();

    public void reset() {
        timer.reset().start();
    }

    public void record(String identifier) {
        currentYearRecords.add(identifier, (int) timer.millis());
    }

    public void recordAndReset(String identifier) {
        record(identifier);
        reset();
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void endYear() {
        for(String identifier: currentYearRecords.elementSet()) {
            timeRecords.put(currentYear, identifier, currentYearRecords.count(identifier));
        }
        currentYearRecords.clear();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("year");
        for(String identifier: timeRecords.columnKeySet()) {
            builder.append(",").append(identifier);
        }
        builder.append("\n");

        for(Integer year: timeRecords.rowKeySet()) {
            builder.append(year);
            for(String identifier: timeRecords.columnKeySet()) {
                Integer millis = timeRecords.get(year, identifier);
                if(millis != null) {
                    builder.append(",").append(millis / 60000.);
                } else {
                    builder.append(",").append(0);
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
