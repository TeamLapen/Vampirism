package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredActionRegister<T extends IFactionPlayer<T>> extends DeferredRegister<IAction<T>> {

    public static <T extends IFactionPlayer<T>> DeferredActionRegister<T> create(String namespace) {
        return new DeferredActionRegister<>(namespace);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    protected DeferredActionRegister(String namespace) {
        super((ResourceKey<? extends Registry<IAction<T>>>) (Object) VampirismRegistries.Keys.ACTION, namespace);
    }


    /**
     * @deprecated Use {@link #registerAction(String, Function)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    @Override
    public <I extends IAction<T>> DeferredAction<T,IAction<T>, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (DeferredAction<T, IAction<T>, I>) super.register(name, func);
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
        return (DeferredAction<T,IAction<T>, I>) super.register(name, sup);
    }

    @SuppressWarnings("unchecked")
    public <L extends IAction<T>, I extends L> DeferredAction<T, L, I> registerAction(String name, Supplier<? extends I> sup) {
        return (DeferredAction<T, L, I>) super.register(name, sup);
    }

    @SuppressWarnings("unchecked")
    public <I extends IAction<?>> DeferredAction<?,IAction<?> ,I> registerUnspecified(String name, Supplier<? extends I> sup) {
        return (DeferredAction<?,IAction<?>, I>) (Object) register(name, (Supplier<? extends IAction<T>>) sup);
    }

    @Override
    protected <I extends IAction<T>> DeferredAction<T, IAction<T>, I> createHolder(ResourceKey<? extends Registry<IAction<T>>> registryKey, ResourceLocation key) {
        return DeferredAction.createAction(ResourceKey.create(registryKey, key));
    }

}
