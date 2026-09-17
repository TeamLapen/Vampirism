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

    /**
     * Creates a builder that additionally targets the default target entity types of the given faction
     *
     * @see Builder#defaultTargets()
     */
    static IFactionPredicate.Builder defaultTargets(Holder<? extends IFaction<?>> ownFaction) {
        return builder(ownFaction).defaultTargets();
    }

    default Predicate<Entity> forEntity() {
        return entity -> entity instanceof LivingEntity livingEntity && IFactionPredicate.this.test(livingEntity);
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
         * Additionally target all entities whose type is in the default target tag of the own faction, regardless of their faction.
         * <p>
         * The tag is registered using {@link de.teamlapen.faction.api.event.AddFactionTagEvent.Builder#defaultTargets(net.minecraft.tags.TagKey)}.
         * Has no effect if the builder has no own faction or the faction did not register a tag.
         */
        Builder defaultTargets();

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
         * Set an additional non-faction condition the target must match.
         * <p>
         * Replaces the default {@link net.minecraft.world.entity.EntitySelector#NO_CREATIVE_OR_SPECTATOR}
         */
        Builder and(Predicate<Entity> other);

        IFactionPredicate build();
    }
}
