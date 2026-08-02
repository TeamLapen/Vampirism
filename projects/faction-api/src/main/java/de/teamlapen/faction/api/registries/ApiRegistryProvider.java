package de.teamlapen.faction.api.registries;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ApiRegistryProvider {

    @SuppressWarnings("unchecked")
    static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> retrieveFaction(Identifier key) {
        return DeferredFaction.createFaction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.FACTION, key));
    }

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> retrieveDataComponent(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, key));
    }

    static DeferredHolder<Block, Block> retrieveBlock(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(Registries.BLOCK, key));
    }

    static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> retrieveAttachmentType(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, key));
    }

    static <T> RegistryProvider<T> retrieveRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryProvider<>(key);
    }

    static <T> ResourceKey<Registry<T>> registryKey(Identifier id) {
        return ResourceKey.createRegistryKey(id);
    }
}
