package de.tum.bgu.msm.data.dwelling;

import java.util.Collection;

public interface DwellingData {
    Dwelling getDwelling(int dwellingId);

    Collection<Dwelling> getDwellings();

    void removeDwelling(int id);

    void addDwelling(Dwelling dwelling);
}
