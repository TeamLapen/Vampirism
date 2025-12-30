package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Objects;
import java.util.function.Supplier;

class APIUtil {

    @SuppressWarnings("unchecked")
    static <T> Supplier<DataComponentType<T>> supplyDataComponent(Identifier key) {
        return Suppliers.memoize(() -> Objects.requireNonNull((DataComponentType<T>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(key)));
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    static <T, Z> Supplier<DataMapType<T, Z>> supplyDataMap(ResourceKey<Registry<T>> registry, Identifier key) {
        return Suppliers.memoize(() -> Objects.requireNonNull((DataMapType<T, Z>) RegistryManager.getDataMap(registry, key)));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> Supplier<T> supplyRegistry(ResourceKey<T> key) {
        return Suppliers.memoize(() -> Objects.requireNonNull((T) BuiltInRegistries.REGISTRY.getValue((ResourceKey) key)));
    }

    static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(VIdentifier.mod(name));
    }

    @SuppressWarnings("unchecked")
    static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> factionHolder(Identifier key) {
        return DeferredFaction.createFaction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.FACTION, key));
    }
}
