package de.teamlapen.faction.api.registries.minion_tasks;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.*;

@SuppressWarnings("unused")
public class DeferredMinionTaskRegister extends DeferredRegister<IMinionTask<?, ?>> {

    protected DeferredMinionTaskRegister(String namespace) {
        super(FactionRegistries.Keys.MINION_TASK, namespace);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> DeferredMinionTaskRegister create(String namespace) {
        return new DeferredMinionTaskRegister(namespace);
    }

    @Deprecated
    @Override
    public <I extends IMinionTask<?,?>> DeferredHolder<IMinionTask<?,?>, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    public <TData extends IMinionData, TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TData, TState>> DeferredMinionTask<TData, TState, TTask> registerTask(String name, Function<MinionTaskProperties, ? extends TTask> sup) {
        return registerTask(name, sup, x -> x);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <TData extends IMinionData, TState extends IMinionTask.IMinionTaskState, TTask extends IMinionTask<TData, TState>> DeferredMinionTask<TData, TState, TTask> registerTask(String name, Function<MinionTaskProperties, ? extends TTask> sup, UnaryOperator<MinionTaskProperties> properties) {
        return (DeferredMinionTask<TData, TState, TTask>) (Object) super.register(name, key -> sup.apply(properties.apply(new MinionTaskProperties().setId(ResourceKey.create(FactionRegistries.Keys.MINION_TASK, key)))));
    }

    public <TData extends IMinionData, TTask extends IMinionTask<TData, IMinionTask.EmptyState>> DeferredMinionTask<TData, IMinionTask.EmptyState, TTask> registerTask(String name, BiFunction<MinionTaskProperties, IMinionTask.EmptyState, ? extends TTask> sup) {
        return registerTask(name, sup, x -> x);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <TData extends IMinionData, TTask extends IMinionTask<TData, IMinionTask.EmptyState>> DeferredMinionTask<TData, IMinionTask.EmptyState, TTask> registerTask(String name, BiFunction<MinionTaskProperties, IMinionTask.EmptyState, ? extends TTask> sup, UnaryOperator<MinionTaskProperties> properties) {
        return (DeferredMinionTask<TData, IMinionTask.EmptyState, TTask>) (Object) super.register(name, key -> sup.apply(properties.apply(new MinionTaskProperties().setId(ResourceKey.create(FactionRegistries.Keys.MINION_TASK, key))), IMinionTask.EmptyState.INSTANCE));
    }

    @Override
    @Deprecated
    public <I extends IMinionTask<?,?>> DeferredHolder<IMinionTask<?,?>, I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <I extends IMinionTask<?, ?>> DeferredHolder<IMinionTask<?, ?>, I> createHolder(ResourceKey<? extends Registry<IMinionTask<?, ?>>> registryKey, Identifier key) {
        return (DeferredHolder<IMinionTask<?,?>, I>) (Object) DeferredMinionTask.createTask(key);
    }
}
