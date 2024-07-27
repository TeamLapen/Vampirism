package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IFactionRegistry {

    @Nullable
    Holder<? extends IFaction<?>> getFaction(Entity entity);

    @Nullable
    Holder<? extends IFaction<?>> getFaction(Player entity);

    boolean isEntityOfFaction(Entity entity, Holder<? extends IFaction<?>> faction);

    boolean isEntityOfFaction(Player entity, Holder<? extends IFaction<?>> faction);

    /**
     * @return all factions form the {@link de.teamlapen.vampirism.api.VampirismRegistries#FACTION} registry
     */
    @Unmodifiable
    Collection<Holder<? extends IFaction<?>>> factions();

    /**
     * @return all playable factions form the {@link de.teamlapen.vampirism.api.VampirismRegistries#FACTION} registry
     */
    @Unmodifiable
    Collection<Holder<? extends IPlayableFaction<?>>> playableFactions();

    /**
     * Get a cached or create a predicate which selects all other faction entities
     *
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     */
    Predicate<LivingEntity> getPredicate(Holder<? extends IFaction<?>> thisFaction, boolean ignoreDisguise);

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
    Predicate<LivingEntity> getPredicate(Holder<? extends IFaction<?>> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable Holder<? extends IFaction<?>> otherFaction);

    /**
     * @return a map of all minion entries from {@link de.teamlapen.vampirism.api.VampirismRegistries#MINION} registry, grouped by faction
     */
    @Unmodifiable
    Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> getFactionMinionEntries();

}
