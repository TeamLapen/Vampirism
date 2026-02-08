package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.FactionsApi;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

/**
 * A IFactionSpecificTags is used to manage faction-specific tags.
 * <p>
 * Tags can be defined for each faction and registered as such using the {@link de.teamlapen.faction.api.event.AddFactionTagEvent}.
 * <p>
 * You can either register a tag by a custom {@link net.minecraft.resources.ResourceKey} or register one tag per registry
 *
 * @see de.teamlapen.faction.api.event.AddFactionTagEvent
 */
public interface IFactionSpecificTags {

    static IFactionSpecificTags get() {
        return FactionsApi.services().factionTags();
    }

    /**
     * Retrieves an optional tag for the given faction using the custom resource key
     */
    <T> Optional<TagKey<T>> getCustom(Holder<? extends IFaction<?>> faction, ResourceKey<T> key);

    /**
     * Retrieves an optional tag for the given faction using the custom resource key or a fallback
     */
    <T> TagKey<T> getCustom(Holder<? extends IFaction<?>> faction, ResourceKey<T> key, TagKey<T> fallback);

    /**
     * Retrieves an optional tag for the given faction for the registry
     */
    <T> Optional<TagKey<T>> get(Holder<? extends IFaction<?>> faction, ResourceKey<? extends Registry<T>> key);

    /**
     * Retrieves an optional tag for the given faction for the registry or a fallback
     */
    <T> TagKey<T> get(Holder<? extends IFaction<?>> faction, ResourceKey<? extends Registry<T>> key, TagKey<T> fallback);

    /**
     * Returns all tags registered for the custom registry key
     */
    @Unmodifiable
    <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> allCustom(ResourceKey<T> key);

    /**
     * Returns all tags registered for the given registry.
     */
    @Unmodifiable
    <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> all(ResourceKey<? extends Registry<T>> key);

}
