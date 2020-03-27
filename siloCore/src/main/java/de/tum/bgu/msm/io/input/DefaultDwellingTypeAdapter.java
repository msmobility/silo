package de.tum.bgu.msm.io.input;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;

public class DefaultDwellingTypeAdapter implements DwellingTypeAdapter {
    @Override
    public DwellingType valueOf(String tp) {
        return DefaultDwellingTypeImpl.valueOf(tp);
    }
}
