/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to hold job data
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Santa Fe
 **/

public class Job {

    private static final Map<Integer, Job> jobMap = new ConcurrentHashMap<>();

	private final int id;
    private final int zone;
    private int workerId;
    private final String type;

    public Job (int id, int zone, int workerId, String type) {
        this.id = id;
        this.zone = zone;
        this.workerId = workerId;
        this.type = type;
        jobMap.put(id, this);
    }

    public static void saveJobs (Job[] jjs) {
        for (Job jj: jjs) jobMap.put(jj.getId(), jj);
    }

    public static Job getJobFromId(int jobId) {
        return jobMap.get(jobId);
    }

    public static int getJobCount() {
        return jobMap.size();
    }

    public static Collection<Job> getJobs() {
            return jobMap.values();
    }

    public static Set<Integer> getJobMapIDs () {
        return jobMap.keySet();
    }

    public static void removeJob(int id) {
        jobMap.remove(id);
    }

    public int getId () {
        return id;
    }

    public int getWorkerId() {
        return workerId;
    }

    public int getZone() {
        return zone;
    }

    public String getType() {
        return type;
    }

    public void setWorkerID(int personID) {
        this.workerId = personID;
    }

    @Override
    public String toString() {
        return "Attributes of job       " + id
                + "\nLocated in zone         " + zone
                + "\nFilled by person        " + workerId
                + "\nJob type                " + type;
    }
 }
