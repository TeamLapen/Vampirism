package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    IFaction<?> getFaction(Entity entity);

    /**
     * Get a faction by its name
     */
    @Nullable
    IFaction<?> getFactionByID(ResourceLocation id);

    /**
     * @return All factions after post init
     */
    IFaction<?>[] getFactions();

    /**
     * @return All playable factions after post init
     */
    IPlayableFaction<?>[] getPlayableFactions();

    /**
     * Get a cached or create a predicate which selects all other faction entities
     *
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
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
     */
    Predicate<LivingEntity> getPredicate(IFaction<?> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction<?> otherFaction);

    /**
     * Creates a faction builder.
     *
     * @param id              Faction id e.g. for level command
     * @param entityInterface Interface all entities implement
     * @param <T>             Interface all entities implement
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
    <T extends IFactionPlayer<T>> IPlayableFactionBuilder<T> createPlayableFaction(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<AttachmentType<T>> playerCapabilitySupplier);

    @Nullable
    IMinionEntry<?,?> getMinion(ResourceLocation minionId);

    @NotNull Collection<IMinionEntry<?, ?>> getMinions();

    interface IMinionEntry<T extends IFactionPlayer<T>, Z extends IMinionData> {
        ResourceLocation id();
        Supplier<Z> data();

        Supplier<EntityType<? extends IMinionEntity>> type();

        IPlayableFaction<T> faction();

        List<IMinionBuilder.IMinionCommandBuilder.ICommandEntry<Z,?>> commandArguments();
    }
}
