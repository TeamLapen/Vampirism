package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTask;
import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTaskRegister;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.management.*;
import net.neoforged.bus.api.IEventBus;

public class FactionMinionTasks {

    public static final DeferredMinionTaskRegister MINION_TASKS = DeferredMinionTaskRegister.create(REFERENCE.MOD_ID);

    public static final DeferredMinionTask<IMinionTask.EmptyState, MinionTask<IMinionTask.EmptyState>> NOTHING = MINION_TASKS.registerTask("nothing", MinionTask::new);

    public static final DeferredMinionTask<StayTask.State, StayTask> STAY = MINION_TASKS.registerTask("stay", StayTask::new, p -> p.markGlobal().category(FactionMinionTaskCategories.MOVEMENT));
    public static final DeferredMinionTask<DefendAreaTask.State, DefendAreaTask> DEFEND_AREA = MINION_TASKS.registerTask("defend_area", DefendAreaTask::new, p -> p.markGlobal().category(FactionMinionTaskCategories.DEFEND));

    public static final DeferredMinionTask<MinionTask.EmptyState, MinionTask<MinionTask.EmptyState>> FOLLOW_LORD = MINION_TASKS.registerTask("follow_lord", MinionTask::new, p -> p.markGlobal().categoryDefault(FactionMinionTaskCategories.MOVEMENT));
    public static final DeferredMinionTask<MinionTask.EmptyState, MinionTask<MinionTask.EmptyState>> PROTECT_LORD = MINION_TASKS.registerTask("protect_lord", MinionTask::new, p -> p.markGlobal().categoryDefault(FactionMinionTaskCategories.DEFEND));

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
