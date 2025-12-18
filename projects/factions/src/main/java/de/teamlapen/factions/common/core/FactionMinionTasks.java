package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.common.factions.minions.management.DefendAreaTask;
import de.teamlapen.factions.common.factions.minions.management.NothingTask;
import de.teamlapen.factions.common.factions.minions.management.SimpleMinionTask;
import de.teamlapen.factions.common.factions.minions.management.StayTask;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionMinionTasks {

    public static final DeferredRegister<IMinionTask<?, ?>> MINION_TASKS = DeferredRegister.create(FactionRegistries.Keys.MINION_TASK, REFERENCE.MOD_ID);

    public static final DeferredHolder<IMinionTask<?,?>, StayTask> STAY = MINION_TASKS.register("stay", StayTask::new);
    public static final DeferredHolder<IMinionTask<?,?>, DefendAreaTask> DEFEND_AREA = MINION_TASKS.register("defend_area", DefendAreaTask::new);
    public static final DeferredHolder<IMinionTask<?,?>, SimpleMinionTask> FOLLOW_LORD = MINION_TASKS.register("follow_lord", () -> new SimpleMinionTask(FactionMinionTasks.FOLLOW_LORD));

    public static final DeferredHolder<IMinionTask<?,?>, NothingTask> NOTHING = MINION_TASKS.register("nothing", NothingTask::new);
    public static final DeferredHolder<IMinionTask<?,?>, SimpleMinionTask> PROTECT_LORD = MINION_TASKS.register("protect_lord", () -> new SimpleMinionTask(FactionMinionTasks.PROTECT_LORD));

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
