package de.teamlapen.factions.api.registries.actions;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeferredActionRegister<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends DeferredRegister<IAction<T>> {

    @SuppressWarnings({"unchecked", "RedundantCast"})
    protected DeferredActionRegister(String namespace) {
        super((ResourceKey<? extends Registry<IAction<T>>>) (Object) FactionRegistries.Keys.ACTION, namespace);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> DeferredActionRegister<T> create(String namespace) {
        return new DeferredActionRegister<>(namespace);
    }

    /**
     * @deprecated Use {@link #registerAction(String, Function)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    @Override
    public <I extends IAction<T>> DeferredAction<T, IAction<T>, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (DeferredAction<T, @NotNull IAction<T>, I>) super.register(name, func);
    }

    @SuppressWarnings("unchecked")
    public <L extends IAction<T>, I extends L> DeferredAction<T, L, I> registerAction(String name, Function<ResourceLocation, ? extends I> func) {
        return (DeferredAction<T, L, I>) super.register(name, func);
    }

    /**
     * @deprecated Use {@link #registerAction(String, Supplier)} instead
     */
    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public <I extends IAction<T>> DeferredAction<T, IAction<T>, I> register(String name, Supplier<? extends I> sup) {
        return (DeferredAction<T, @NotNull IAction<T>, I>) super.register(name, sup);
    }

    @SuppressWarnings("unchecked")
    public <L extends IAction<T>, I extends L> DeferredAction<T, L, I> registerAction(String name, Supplier<? extends I> sup) {
        return (DeferredAction<T, L, I>) super.register(name, sup);
    }

    @SuppressWarnings("unchecked")
    public <I extends IAction<?>> DeferredAction<?, IAction<?>, I> registerUnspecified(String name, Supplier<? extends I> sup) {
        return (DeferredAction<?, @NotNull IAction<?>, I>) (Object) register(name, (Supplier<? extends IAction<T>>) sup);
    }

    @Override
    protected <I extends IAction<T>> DeferredAction<T, IAction<T>, I> createHolder(ResourceKey<? extends Registry<IAction<T>>> registryKey, ResourceLocation key) {
        return DeferredAction.createAction(ResourceKey.create(registryKey, key));
    }

}
