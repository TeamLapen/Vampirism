package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTask;
import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTaskRegister;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.management.*;
import net.neoforged.bus.api.IEventBus;

public class FactionMinionTasks {

    public static final DeferredMinionTaskRegister MINION_TASKS = DeferredMinionTaskRegister.create(REFERENCE.MOD_ID);

    public static final DeferredMinionTask<MinionData, IMinionTask.EmptyState, MinionTask<MinionData, IMinionTask.EmptyState>> NOTHING = MINION_TASKS.registerTask("nothing", MinionTask::new);

    public static final DeferredMinionTask<MinionData, StayTask.State, StayTask> STAY = MINION_TASKS.registerTask("stay", StayTask::new, MinionTaskProperties::markGlobal);
    public static final DeferredMinionTask<MinionData, DefendAreaTask.State, DefendAreaTask> DEFEND_AREA = MINION_TASKS.registerTask("defend_area", DefendAreaTask::new, MinionTaskProperties::markGlobal);

    public static final DeferredMinionTask<MinionData, MinionTask.EmptyState, MinionTask<MinionData, MinionTask.EmptyState>> FOLLOW_LORD = MINION_TASKS.registerTask("follow_lord", MinionTask::new, MinionTaskProperties::markGlobal);
    public static final DeferredMinionTask<MinionData, MinionTask.EmptyState, MinionTask<MinionData, MinionTask.EmptyState>> PROTECT_LORD = MINION_TASKS.registerTask("protect_lord", MinionTask::new, MinionTaskProperties::markGlobal);

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
