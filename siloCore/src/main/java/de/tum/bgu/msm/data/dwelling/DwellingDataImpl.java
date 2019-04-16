package de.tum.bgu.msm.data.dwelling;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DwellingDataImpl implements DwellingData {

    private final Map<Integer, Dwelling> dwellings = new ConcurrentHashMap<>();

    @Override
    public Dwelling getDwelling(int dwellingId) {
        return dwellings.get(dwellingId);
    }

    @Override
    public Collection<Dwelling> getDwellings() {
        return dwellings.values();
    }

    @Override
    public void removeDwelling(int id) {
        dwellings.remove(id);
    }

    @Override
    public void addDwelling(Dwelling dwelling) {
        dwellings.put(dwelling.getId(), dwelling);
    }
}
