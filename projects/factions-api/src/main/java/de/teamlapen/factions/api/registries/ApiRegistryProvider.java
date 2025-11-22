package de.teamlapen.factions.api.registries;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.registries.factions.DeferredFaction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ApiRegistryProvider {

    @SuppressWarnings("unchecked")
    static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> retrieveFaction(ResourceLocation key) {
        return DeferredFaction.createFaction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.FACTION, key));
    }

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> retrieveDataComponent(ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, key));
    }

    static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> retrieveAttachmentType(ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, key));
    }

    static <T> RegistryProvider<T> retrieveRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryProvider<>(key);
    }

    static <T> ResourceKey<Registry<T>> registryKey(ResourceLocation id) {
        return ResourceKey.createRegistryKey(id);
    }
}
