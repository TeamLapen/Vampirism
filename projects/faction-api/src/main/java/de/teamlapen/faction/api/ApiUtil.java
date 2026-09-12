package de.teamlapen.faction.api;

import com.google.common.base.Suppliers;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
interface ApiUtil {

    @SuppressWarnings("unchecked")
    static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> retrieveFaction(Identifier key) {
        return DeferredFaction.createFaction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.FACTION, key));
    }

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> retrieveDataComponent(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, key));
    }

    static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> retrieveAttachmentType(Identifier key) {
        return DeferredHolder.create(ResourceKey.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, key));
    }

    @SuppressWarnings("unchecked")
    static <T> Supplier<Registry<T>> retrieveRegistry(ResourceKey<Registry<T>> key) {
        return Suppliers.memoize(() -> (Registry<T>) BuiltInRegistries.REGISTRY.getValueOrThrow((ResourceKey)key));
    }

    static <T> ResourceKey<Registry<T>> registryKey(Identifier id) {
        return ResourceKey.createRegistryKey(id);
    }
}
