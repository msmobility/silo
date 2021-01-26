package de.tum.bgu.msm.data.dwelling;

import de.tum.bgu.msm.data.dwelling.DwellingType;

import java.util.List;

public interface DwellingTypes {
    DwellingType valueOf(String tp);
    List<DwellingType> getTypes();
}
