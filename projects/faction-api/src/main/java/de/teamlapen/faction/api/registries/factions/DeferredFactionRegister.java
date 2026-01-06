package de.teamlapen.faction.api.registries.factions;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeferredFactionRegister extends DeferredRegister<IFaction<?>> {

    protected DeferredFactionRegister(String namespace) {
        super(FactionRegistries.Keys.FACTION, namespace);
    }

    public static <T extends IFactionEntity> DeferredFactionRegister create(String namespace) {
        return new DeferredFactionRegister(namespace);
    }

    @Deprecated
    @Override
    public <I extends IFaction<?>> DeferredHolder<IFaction<?>, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T extends IFactionEntity, I extends IFaction<T>> DeferredFaction<T, I> registerFaction(String name, Supplier<? extends I> sup) {
        return (DeferredFaction<T, I>) (Object) super.register(name, sup);
    }

    @Override
    public <I extends IFaction<?>> DeferredHolder<IFaction<?>, I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T extends IFactionEntity, I extends IFaction<T>> DeferredFaction<T, I> registerFaction(String name, Function<Identifier, ? extends I> func) {
        return (DeferredFaction<T, I>) (Object) super.register(name, func);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <I extends IFaction<?>> DeferredHolder<IFaction<?>, I> createHolder(ResourceKey<? extends Registry<IFaction<?>>> registryKey, Identifier key) {
        return (DeferredHolder<IFaction<?>, I>) (Object) DeferredFaction.createFaction(key);
    }
}
