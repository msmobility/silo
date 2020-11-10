package run;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class PerthDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES = Collections.unmodifiableList(Arrays.asList(DwellingTypePerth.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return DwellingTypePerth.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }
}
