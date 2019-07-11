package de.tum.bgu.msm.models.relocation.migration;

import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.EventModel;

public interface InOutMigration extends EventModel<MigrationEvent> {
    boolean outMigrateHh(int hhId, boolean overwriteEventRules);
}
