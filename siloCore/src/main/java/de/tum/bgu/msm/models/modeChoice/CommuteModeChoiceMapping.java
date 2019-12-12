package de.tum.bgu.msm.models.modeChoice;

import cern.colt.map.tobject.OpenIntObjectHashMap;
import de.tum.bgu.msm.data.person.Person;

public class CommuteModeChoiceMapping {

    private OpenIntObjectHashMap map;

    public CommuteModeChoiceMapping(int numberOfPersons) {
        map =  new OpenIntObjectHashMap(numberOfPersons);
    }

    public void assignMode(CommuteMode mode, Person person) {
        map.put(person.getId(), mode);
    }

    public CommuteMode getMode(Person person) {
        return (CommuteMode) map.get(person.getId());
    }

    public final static class CommuteMode {

        public final String mode;
        public final double utility;

        public CommuteMode(String mode, double utility) {
            this.mode = mode;
            this.utility = utility;
        }
    }
}
