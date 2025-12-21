package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.FactionsApi;
import de.teamlapen.factions.api.util.SafeCast;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

public interface IFactionTags {

    static IFactionTags get() {
        return FactionsApi.services().factionTags();
    }

    <T> Optional<TagKey<T>> getCustom(Holder<? extends IFaction<?>> faction, ResourceKey<T> key);

    <T> Optional<TagKey<T>> get(Holder<? extends IFaction<?>> faction, ResourceKey<? extends Registry<T>> key);

    @Unmodifiable
    <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> allCustom(ResourceKey<T> key);

    @Unmodifiable
    <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> all(ResourceKey<? extends Registry<T>> key);

}
