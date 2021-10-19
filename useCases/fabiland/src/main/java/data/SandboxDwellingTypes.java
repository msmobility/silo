package data;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SandboxDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES
            = Collections.unmodifiableList(Arrays.asList(SandboxDwellingType.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return SandboxDwellingType.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }

    public enum SandboxDwellingType implements DwellingType {

        SF {
            @Override
            public float getAreaPerDwelling() {
                return 0.3f;
            }

            @Override
            public float getStructuralVacancyRate() {
                return 0.05f;
            }
        }, MF {
            @Override
            public float getAreaPerDwelling() {
                return 0.1f;
            }

            @Override
            public float getStructuralVacancyRate() {
                return 0.05f;
            }
        };
    }
}
