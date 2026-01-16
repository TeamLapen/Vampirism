package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.FactionsApi;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.function.Predicate;

public interface IFactionPredicate extends Predicate<LivingEntity>, TargetingConditions.Selector {

    static IFactionPredicate.Builder builder(Holder<? extends IFaction<?>> ownFaction) {
        return FactionsApi.services().factionPredicates().builder(ownFaction);
    }

    static IFactionPredicate.Builder builder() {
        return FactionsApi.services().factionPredicates().builder();
    }

    interface Builder {

        Builder onlyPlayer();

        Builder onlyNonPlayers();

        /**
         * For disguised entity, consider the actual faction no the one it is disguised as
         */
        Builder ignoreDisguise();

        Builder allowOwnFaction();

        /**
         * Target all creatures that have a faction other than NEUTRAL
         */
        Builder notNeutral();

        Builder targetFaction(Holder<? extends IFaction<?>> targetFaction);

        Builder targetFaction(TagKey<IFaction<?>> targetFaction);

        Builder and(Predicate<Entity> other);

        IFactionPredicate build();
    }
}
