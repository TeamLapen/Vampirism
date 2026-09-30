package de.teamlapen.faction.api.registries.minion_tasks;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredMinionTask<TData extends IMinionData, TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TData, TState>> extends DeferredHolder<TTask, TTask> {

    protected DeferredMinionTask(ResourceKey<TTask> key) {
        super(key);
    }

    public static <TData extends IMinionData, TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TData, TState>> DeferredMinionTask<TData, TState, TTask> createTask(ResourceKey<TTask> key) {
        return new DeferredMinionTask<>(key);
    }

    @SuppressWarnings("unchecked")
    public static <TData extends IMinionData, TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TData, TState>> DeferredMinionTask<TData, TState, TTask> createTask(Identifier key) {
        return createTask((ResourceKey<TTask>) ResourceKey.create(FactionRegistries.Keys.MINION_TASK, key));
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<IMinionTask<?,?>> getRawKey() {
        return (ResourceKey<IMinionTask<?,?>>) super.getKey();
    }
}
