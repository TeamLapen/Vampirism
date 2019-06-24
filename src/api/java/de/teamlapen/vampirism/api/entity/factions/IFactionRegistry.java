package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Faction registry.
 * Register all extended properties that extend {@link IFactionPlayer} here
 * Currently only used for managing IPlayerEventListeners.
 */
public interface IFactionRegistry {


    /**
     * @param entity Can check any entity
     * @return The faction of the given entity or null
     */
    @Nullable
    IFaction getFaction(Entity entity);

    /**
     * Get a faction by it's name
     */
    @Nullable
    IFaction getFactionByName(String name);

    /**
     * @return All factions after post init
     */
    IFaction[] getFactions();

    /**
     * @return All playable factions after post init
     */
    IPlayableFaction[] getPlayableFactions();

    /**
     * Get a cached or create a predicate which selects all other faction entities
     * For all non EntityLivingBase entities the predicate is always false
     *
     * @param thisFaction
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     * @return
     */
    Predicate<Entity> getPredicate(IFaction thisFaction, boolean ignoreDisguise);

    /**
     * Get a cached or create a predicate which selects entities from other factions.
     * For all non EntityLivingBase entities the predicate is always false
     *
     * @param thisFaction    The friendly faction
     * @param player         If players should be selected
     * @param mob            If non players should be selected
     * @param neutralPlayer  If neutral playsers should be selected
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     * @param otherFaction   If this is not null, only entities of this faction are selected.
     * @return
     */
    Predicate<Entity> getPredicate(IFaction thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, IFaction otherFaction);

    /**
     * Create and registerAdvancements a non playable faction. Has to be called during InterModEnqueueEvent
     *
     * @param name            Faction name e.g. for level command
     * @param entityInterface Interface all entities implement
     * @param color           Color e.g. for level rendering
     * @param <T>             Interface all entities implement
     * @return The created faction
     */
    @ThreadSafeAPI
    <T extends IFactionEntity> IFaction registerFaction(String name, Class<T> entityInterface, int color);

    /**
     * Create and registerAdvancements a playable faction. Has to be called during InterModEnqueueEvent
     *
     * @param name             Faction name e.g. for level command
     * @param entityInterface  Interface all entities or (the given capability for players) implement
     * @param color            Color e.g. for level rendering
     * @param playerCapability The capability which is attached to all players
     * @param key              Unique key, preferable the one used for registering the capability
     * @param highestLevel     The highest reachable player level
     * @param <T>              nterface all entities or (the given capability for players)  implement
     * @return The created faction
     */
    @ThreadSafeAPI
    <T extends IFactionPlayer> IPlayableFaction registerPlayableFaction(String name, Class<T> entityInterface, int color, ResourceLocation key, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel);
}
