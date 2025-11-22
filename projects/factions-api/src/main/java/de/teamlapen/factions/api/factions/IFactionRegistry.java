package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.entities.minion.IMinionEntry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IFactionRegistry {

    @NotNull
    Holder<? extends IFaction<?>> getFaction(Entity entity);

    @NotNull
    Holder<? extends IFaction<?>> getFaction(Player entity);

    boolean isEntityOfFaction(Entity entity, Holder<? extends IFaction<?>> faction);

    boolean isEntityOfFaction(Player entity, Holder<? extends IFaction<?>> faction);

    /**
     * @return all factions form the {@link de.teamlapen.factions.api.FactionRegistries#FACTION} registry
     */
    @Unmodifiable
    Collection<Holder<? extends IFaction<?>>> factions();

    /**
     * @return all playable factions form the {@link de.teamlapen.factions.api.FactionRegistries#FACTION} registry
     */
    @Unmodifiable
    Collection<Holder<? extends IPlayableFaction<?>>> playableFactions();

    /**
     * Get a cached or create a predicate which selects all other faction entities
     *
     * @param ignoreDisguise If disguised players should still be counted for their actual faction (disguised vampires will still be detected as vampires)
     */
    Predicate<LivingEntity> getPredicate(Holder<? extends IFaction<?>> thisFaction, boolean ignoreDisguise);

    default TargetingConditions.Selector getSelector(Holder<? extends IFaction<?>> thisFaction, boolean ignoreDisguise) {
        return (entity, level) -> getPredicate(thisFaction, ignoreDisguise).test(entity);
    }

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

    default TargetingConditions.Selector getSelector(Holder<? extends IFaction<?>> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable Holder<? extends IFaction<?>> otherFaction) {
        return (entity, level) -> getPredicate(thisFaction, player, mob, neutralPlayer, ignoreDisguise, otherFaction).test(entity);
    }

    /**
     * @return a map of all minion entries from {@link de.teamlapen.factions.api.FactionRegistries#MINION} registry, grouped by faction
     */
    @Unmodifiable
    Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> getFactionMinionEntries();

}
