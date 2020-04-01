package data;

import de.tum.bgu.msm.data.dwelling.DwellingType;

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
