package de.tum.bgu.msm.data;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ZoneImpl implements Zone {

    private final int id;
    private final int msa;
    private final float area;

    private final Set<Job> jobs = new LinkedHashSet<>();
    private final Set<Dwelling> dwellings = new LinkedHashSet<>();

    private Region region;

    public ZoneImpl(int id, int msa, float area) {
        this.id = id;
        this.msa = msa;
        this.area = area;
    }

    @Override
    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    @Override
    public int getMsa() {
        return this.msa;
    }

    @Override
    public float getArea() {
        return area;
    }

    @Override
    public Set<Job> getJobs() {
        return Collections.unmodifiableSet(this.jobs);
    }

    @Override
    public boolean addJob(Job job) {
        if(job.getZone() == this.id) {
            return jobs.add(job);
        } else {
            throw new RuntimeException("Job " + job.getId() + " can't be added to Zone " + this.id
                    + ", as it already is assigned to " + job.getZone());
        }
    }

    @Override
    public boolean removeJob(Job job) {
        if(Job.getJobs().contains(job)) {
            Job.removeJob(job.getId());
        }
        return jobs.remove(job);
    }

    @Override
    public Set<Dwelling> getDwellings() {
        return Collections.unmodifiableSet(this.dwellings);
    }

    @Override
    public boolean addDwelling(Dwelling dwelling) {
        if(dwelling.getZone() == this.id) {
            return this.dwellings.add(dwelling);
        } else {
            throw new RuntimeException("Dwelling " + dwelling.getId() + " can't be added to Zone " + this.id
                    + ", as it already is assigned to " + dwelling.getZone());
        }
    }

    @Override
    public boolean removeDwelling(Dwelling dwelling) {
        if(Dwelling.getDwellings().contains(dwelling)) {
            Dwelling.removeDwelling(dwelling.getId());
        }
        return this.dwellings.remove(dwelling);
    }

    @Override
    public int getPopulation() {
        int count = 0;
        for(Dwelling dwelling: dwellings){
            if(dwelling.getResidentId() != -1)  {

                Household.getHouseholdFromId(dwelling.getResidentId()).getHhSize();
            }
        }
        return count;
    }

    @Override
    public double getZonalJobDensity() {
        return jobs.size() / area;
    }

    @Override
    public int getNumberOfJobsForType(String jt) {
        return (int) jobs.stream().filter(job -> job.getType().equals(jt)).count();
    }
}
