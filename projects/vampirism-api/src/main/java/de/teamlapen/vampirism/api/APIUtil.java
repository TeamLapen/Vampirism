package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.FactionExtensionType;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
class APIUtil {

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> supplyDataComponent(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, key));
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

    static TagKey<IFaction<?>> faction(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, VIdentifier.mod(name));
    }

    static TagKey<Biome> biome(String name) {
        return TagKey.create(Registries.BIOME, VIdentifier.mod(name));
    }

    static TagKey<DimensionType> dimensionType(String name) {
        return TagKey.create(Registries.DIMENSION_TYPE, VIdentifier.mod(name));
    }

    @SuppressWarnings("unchecked")
    static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> factionHolder(Identifier key) {
        return DeferredFaction.createFaction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.FACTION, key));
    }
}
