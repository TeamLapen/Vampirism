package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.FactionsApi;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.function.Predicate;

/**
 * Faction predicate interface that is used to test the faction of the target entity against certain faction conditions
 */
public interface IFactionPredicate extends Predicate<LivingEntity>, TargetingConditions.Selector {

    static IFactionPredicate.Builder builder(Holder<? extends IFaction<?>> ownFaction) {
        return FactionsApi.services().factionPredicates().builder(ownFaction);
    }

    static IFactionPredicate.Builder builder() {
        return FactionsApi.services().factionPredicates().builder();
    }

    interface Builder {

        /**
         * Only target players.
         * <p>
         * The default is all entities
         */
        Builder onlyPlayer();

        /**
         * Only target non-players.
         * <p>
         * The default is all entities
         */
        Builder onlyNonPlayers();

        /**
         * For a disguised entity, consider the actual faction not the one it is disguised as
         */
        Builder ignoreDisguise();

        /**
         * Allow the own faction to be targeted
         * <p>
         * The default is false
         */
        Builder allowOwnFaction();

        /**
         * Target all creatures that have a faction other than NEUTRAL
         */
        Builder notNeutral();

        /**
         * Target a specific faction
         */
        Builder targetFaction(Holder<? extends IFaction<?>> targetFaction);

        /**
         * Target a faction tag
         */
        Builder targetFaction(TagKey<IFaction<?>> targetFaction);

        /**
         * Add a predicate to the faction predicate
         */
        Builder and(Predicate<Entity> other);

        IFactionPredicate build();
    }
}
