package de.teamlapen.faction.api.registries.minion_tasks;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredMinionTask<TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TState>> extends DeferredHolder<TTask, TTask> {

    protected DeferredMinionTask(ResourceKey<TTask> key) {
        super(key);
    }

    public static <TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TState>> DeferredMinionTask<TState, TTask> createTask(ResourceKey<TTask> key) {
        return new DeferredMinionTask<>(key);
    }

    @SuppressWarnings("unchecked")
    public static <TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TState>> DeferredMinionTask<TState, TTask> createTask(Identifier key) {
        return createTask((ResourceKey<TTask>) ResourceKey.create(FactionRegistries.Keys.MINION_TASK, key));
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<IMinionTask<?>> getRawKey() {
        return (ResourceKey<IMinionTask<?>>) super.getKey();
    }
}
