package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Event for registering tags for a faction
 * <p>
 * This can be used to register tags for a faction that are used in various places. Tags are registered either with a registry key or an arbitrary resource key.
 * In game, they can be obtained using {@link IFaction#getRegistryTag(ResourceKey)} or {@link IFaction#getTag(ResourceKey)}
 */
public class AddFactionTagEvent extends Event implements IModBusEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Holder<IFaction<?>> faction;
    private final Map<ResourceKey<?>, TagKey<?>> tags = new HashMap<>();

    public AddFactionTagEvent(Holder<IFaction<?>> faction) {
        this.faction = faction;
    }

    /**
     * The faction for which the tags are registered
     */
    public Holder<IFaction<?>> getFaction() {
        return faction;
    }

    /**
     * Adds a tag for the given key
     */
    public <T> void addTag(ResourceKey<T> key, TagKey<T> tag) {
        addTagUnsafe(key, tag);
    }

    /**
     * Adds a tag for the given registry
     */
    public <T> void addRegistryTag(ResourceKey<? extends Registry<T>> key, TagKey<T> tag) {
        addTagUnsafe(key, tag);
    }

    private void addTagUnsafe(ResourceKey<?> key, TagKey<?> tag) {
        var value = this.tags.putIfAbsent(key, tag);
        if (value != tag) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw new IllegalStateException("Could not register tag for key " + key + " for faction " + faction.getKey());
            } else {
                LOGGER.warn("Could not register tag for key {} for faction {}", key, faction.getKey());
            }
        }
    }


    public Map<ResourceKey<?>, TagKey<?>> getTags() {
        return Collections.unmodifiableMap(tags);
    }
}
