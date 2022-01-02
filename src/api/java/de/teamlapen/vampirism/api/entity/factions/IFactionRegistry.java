package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.BiFunction;
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
    IFaction getFactionByID(ResourceLocation id);

    /**
     * @return All factions after post init
     */
    IFaction[] getFactions();

    /**
     * @return All playable factions after post init
     */
    IPlayableFaction<?>[] getPlayableFactions();

    /**
     * Get a cached or create a predicate which selects all other faction entities
     *
     * @param thisFaction
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     * @return
     */
    Predicate<LivingEntity> getPredicate(IFaction<?> thisFaction, boolean ignoreDisguise);

    /**
     * Get a cached or create a predicate which selects entities from other factions.
     *
     * @param thisFaction    The friendly faction
     * @param player         If players should be selected
     * @param mob            If non players should be selected
     * @param neutralPlayer  If neutral players should be selected
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     * @param otherFaction   If this is not null, only entities of this faction are selected.
     * @return
     */
    Predicate<LivingEntity> getPredicate(IFaction<?> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction<?> otherFaction);

    /**
     * Create and registerAdvancements a non playable faction. Must be called before InterModProccessEvent e.g. during InterModEnqueueEvent
     *
     * @param id              Faction id e.g. for level command
     * @param entityInterface Interface all entities implement
     * @param color           Color e.g. for level rendering
     * @param <T>             Interface all entities implement
     * @return The created faction
     *
     * @deprecated use the faction builder ({@link #createFaction(ResourceLocation, Class)})
     */
    @Deprecated
    @ThreadSafeAPI
    <T extends IFactionEntity> IFaction registerFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral);//TODO 1.17 remove

    /**
     * Create and registerAdvancements a non playable faction. Must be called before InterModProccessEvent e.g. during InterModEnqueueEvent
     *
     * @param id                 Faction id e.g. for level command
     * @param entityInterface    Interface all entities implement
     * @param color              Color e.g. for level rendering
     * @param villageFactionData village capture related utility class (if null will gets filled with dummy)
     * @param <T>                Interface all entities implement
     * @return The created faction
     *
     * @deprecated use the faction builder ({@link #createFaction(ResourceLocation, Class)})
     */
    @Deprecated
    @ThreadSafeAPI
    <T extends IFactionEntity> IFaction registerFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, @Nullable IVillageFactionData villageFactionData);//TODO 1.17 remove

    /**
     * Use {@link IFactionRegistry#registerPlayableFaction(ResourceLocation, Class, Color, boolean, NonNullSupplier, int, int, BiFunction, IVillageFactionData)} instead
     * <p>
     * Create and registerAdvancements a playable faction. Must be called before InterModProccessEvent e.g. during InterModEnqueueEvent
     *
     * @param id                       Faction id e.g. for level command
     * @param entityInterface          Interface all entities or (the given capability for players) implement
     * @param color                    Color e.g. for level rendering
     * @param playerCapabilitySupplier The capability which is attached to all players
     * @param highestLevel             The highest reachable player level
     * @param <T>                      Interface all entities or (the given capability for players)  implement
     * @return The created faction
     *
     * @deprecated use the faction builder ({@link #createPlayableFaction(ResourceLocation, Class, NonNullSupplier)}
     */
    @Deprecated
    @ThreadSafeAPI
    <T extends IFactionPlayer<?>> IPlayableFaction<T> registerPlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel);//TODO 1.17 remove

    /**
     * Create and registerAdvancements a playable faction. Must be called before InterModProccessEvent e.g. during InterModEnqueueEvent
     *
     * @param id                       Faction id e.g. for level command
     * @param entityInterface          Interface all entities or (the given capability for players) implement
     * @param color                    Color e.g. for level rendering
     * @param playerCapabilitySupplier The capability which is attached to all players
     * @param highestLevel             The highest reachable player level
     * @param highestLordLevel         The highest reachable lord level or 0 if no lord
     * @param lordTitleFunction        Function supplying a textcomponent representing the title for a given lord level and female boolean flag
     * @param villageFactionData       village capture related utility class (if null will gets filled with dummy)
     * @param <T>                      Interface all entities or (the given capability for players)  implement
     * @return The created faction
     *
     * @deprecated use the faction builder ({@link #createPlayableFaction(ResourceLocation, Class, NonNullSupplier)}
     */
    @Deprecated
    @ThreadSafeAPI
    <T extends IFactionPlayer<?>> IPlayableFaction<T> registerPlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel, int highestLordLevel, @Nonnull BiFunction<Integer, Boolean, ITextComponent> lordTitleFunction, @Nullable IVillageFactionData villageFactionData); //TODO 1.17 remove

    /**
     * Creates a faction builder.
     *
     * @param id                       Faction id e.g. for level command
     * @param entityInterface          Interface all entities implement
     * @param <T>                      Interface all entities implement
     * @return a faction builder
     */
    @ThreadSafeAPI
    <T extends IFactionEntity> IFactionBuilder<T> createFaction(ResourceLocation id, Class<T> entityInterface);

    /**
     * Creates a playable faction builder.
     *
     * @param id                       Faction id e.g. for level command
     * @param entityInterface          Interface all entities or (the given capability for players) implement
     * @param playerCapabilitySupplier The capability which is attached to all players
     * @param <T>                      Interface all entities or (the given capability for players)  implement
     * @return a player faction builder
     */
    @ThreadSafeAPI
    <T extends IFactionPlayer<?>> IPlayableFactionBuilder<T> createPlayableFaction(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<Capability<T>> playerCapabilitySupplier);
}
