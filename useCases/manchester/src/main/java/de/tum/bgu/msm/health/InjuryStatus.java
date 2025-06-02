package de.tum.bgu.msm.health;

public enum InjuryStatus {
        NO_INJURY,          // Default state
        SERIOUSLY_INJURED,  // Non-fatal injury (generic or mode-specific)
        // add car, bike, walk
        KILLED_CAR,         // Fatal injury by car
        KILLED_BIKE,        // Fatal injury by bike
        KILLED_WALK         // Fatal injury while walking
}
