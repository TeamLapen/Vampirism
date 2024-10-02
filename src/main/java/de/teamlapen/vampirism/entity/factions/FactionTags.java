package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.event.AddFactionTagEvent;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

public class FactionTags {

    private static final Table<IFaction<?>, ResourceKey<?>, TagKey<?>> registeredTags = HashBasedTable.create();

    @ApiStatus.Internal
    public static void collectTags() {
        ModRegistries.FACTIONS.holders().forEach(FactionTags::addFaction);
    }

    private static void addFaction(Holder<IFaction<?>> faction) {
        ModLoader.postEventWithReturn(new AddFactionTagEvent(faction)).getTags().forEach((key, tag) -> registeredTags.put(faction.value(), key, tag));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<TagKey<T>> getTag(Holder<IFaction<?>> factionHolder, ResourceKey<T> key) {
        return Optional.ofNullable((TagKey<T>) registeredTags.get(factionHolder.value(), key));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<TagKey<T>> getRegistryTag(Holder<IFaction<?>> faction, ResourceKey<? extends Registry<T>> key) {
        return Optional.ofNullable((TagKey<T>) registeredTags.get(faction.value(), key));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<TagKey<T>> getTag(IFaction<?> faction, ResourceKey<T> key) {
        return Optional.ofNullable((TagKey<T>) registeredTags.get(faction, key));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<TagKey<T>> getRegistryTag(IFaction<?> faction, ResourceKey<? extends Registry<T>> key) {
        return Optional.ofNullable((TagKey<T>) registeredTags.get(faction, key));
    }
}
