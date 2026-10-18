package de.teamlapen.faction.common.factions;

import com.google.common.base.Preconditions;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.util.SafeCast;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param viewerFaction  the faction of the entity using this predicate. Used to resolve disguises
 * @param predicate      additional non-faction conditions that must be met
 * @param ignoreDisguise whether to use the actual faction of players instead of the disguised one
 * @param targetFactions the factions that are targeted
 * @param defaultTargets entity types that are always targeted regardless of their faction
 * @param factionLookup  resolves the faction of entities that are neither players nor {@link IFactionEntity}
 */
public record FactionPredicate(@Nullable Holder<? extends IFaction<?>> viewerFaction, Predicate<? super LivingEntity> predicate, boolean ignoreDisguise, Supplier<HolderSet<IFaction<?>>> targetFactions, Supplier<Optional<TagKey<EntityType<?>>>> defaultTargets, Function<LivingEntity, Holder<? extends IFaction<?>>> factionLookup) implements IFactionPredicate {

    @Override
    public boolean test(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null || !this.predicate.test(livingEntity)) return false;

        Optional<TagKey<EntityType<?>>> defaultTargets = this.defaultTargets.get();
        if (defaultTargets.isPresent() && livingEntity.typeHolder().is(defaultTargets.get())) {
            return true;
        }

        Holder<? extends IFaction<?>> faction = switch (livingEntity) {
            case IFactionEntity factionEntity -> factionEntity.getFaction();
            case Player player -> FactionPlayerHandler.get(player).factionPlayer().getDisguise().getViewedFaction(this.viewerFaction, this.ignoreDisguise);
            default -> this.factionLookup.apply(livingEntity);
        };
        return IFaction.contains(this.targetFactions.get(), faction.getDelegate());
    }

    @Override
    public boolean test(LivingEntity entity, ServerLevel level) {
        return test(entity);
    }

    public interface FactionPredicateLookup {

        /**
         * @return a lazily resolved (and cached) set of target factions
         */
        Supplier<HolderSet<IFaction<?>>> targetFactions(TargetKey key);

        /**
         * @return a lazily resolved default target entity tag of the given faction
         */
        Supplier<Optional<TagKey<EntityType<?>>>> defaultTargets(@Nullable Holder<? extends IFaction<?>> faction);

        Holder<? extends IFaction<?>> getFallbackFaction(LivingEntity livingEntity);
    }

    /**
     * Identifies the target faction set of a predicate. Used as cache key
     *
     * @param sourceFaction   the faction of the predicate owner
     * @param targetTag       the targeted faction tag, mutually exclusive with targetFactions
     * @param targetFactions  the targeted factions, mutually exclusive with targetTag
     * @param allowOwnFaction whether the source faction may be targeted
     */
    public record TargetKey(@Nullable ResourceKey<IFaction<?>> sourceFaction, @Nullable TagKey<IFaction<?>> targetTag, List<ResourceKey<IFaction<?>>> targetFactions, boolean allowOwnFaction) {
        public TargetKey {
            targetFactions = List.copyOf(targetFactions);
        }
    }

    public static class Builder implements IFactionPredicate.Builder {

        @Nullable
        private final Holder<? extends IFaction<?>> sourceFaction;
        private final FactionPredicateLookup lookup;
        private boolean targetPlayers = true;
        private boolean targetNonPlayers = true;
        private boolean ignoreDisguise;
        private boolean allowOwnFaction;
        private boolean defaultTargets;
        @Nullable
        private TagKey<IFaction<?>> targetFaction = null;
        private final List<ResourceKey<IFaction<?>>> targetFactions = new ArrayList<>();
        private Predicate<Entity> other = EntitySelector.NO_CREATIVE_OR_SPECTATOR;

        public Builder(@Nullable Holder<? extends IFaction<?>> sourceFaction, FactionPredicateLookup lookup) {
            this.sourceFaction = sourceFaction;
            this.lookup = lookup;
        }

        @Override
        public Builder onlyPlayer() {
            this.targetPlayers = true;
            this.targetNonPlayers = false;
            return this;
        }

        @Override
        public Builder onlyNonPlayers() {
            this.targetPlayers = false;
            this.targetNonPlayers = true;
            return this;
        }

        @Override
        public Builder ignoreDisguise() {
            this.ignoreDisguise = true;
            return this;
        }

        @Override
        public Builder allowOwnFaction() {
            this.allowOwnFaction = true;
            return this;
        }

        @Override
        public Builder defaultTargets() {
            this.defaultTargets = true;
            return this;
        }

        @Override
        public Builder notNeutral() {
            return targetFaction(FactionTags.NOT_NEUTRAL);
        }

        @Override
        public Builder targetFaction(Holder<? extends IFaction<?>> targetFaction) {
            Preconditions.checkArgument(this.targetFaction == null, "Cannot use both target Holder and target TagKey at the same time!");
            this.targetFactions.add(SafeCast.cast(targetFaction.unwrapKey().orElseThrow(() -> new IllegalArgumentException("Target faction must be a registered faction"))));
            return this;
        }

        @Override
        public Builder targetFaction(TagKey<IFaction<?>> targetFaction) {
            Preconditions.checkArgument(this.targetFactions.isEmpty(), "Cannot use both target Holder and target TagKey at the same time!");
            this.targetFaction = targetFaction;
            return this;
        }

        @Override
        public Builder and(Predicate<Entity> other) {
            this.other = other;
            return this;
        }

        @Override
        public IFactionPredicate build() {
            TagKey<IFaction<?>> targetTag = this.targetFaction == null && this.targetFactions.isEmpty() ? FactionTags.ALL_FACTIONS : this.targetFaction;
            ResourceKey<IFaction<?>> source = this.sourceFaction == null ? null : SafeCast.cast(this.sourceFaction.unwrapKey().orElseThrow(() -> new IllegalArgumentException("Source faction must be a registered faction")));
            var targets = this.lookup.targetFactions(new TargetKey(source, targetTag, this.targetFactions, this.allowOwnFaction));
            var defaults = this.defaultTargets ? this.lookup.defaultTargets(this.sourceFaction) : FactionPredicate.NO_DEFAULT_TARGETS;

            // copy into locals so later modifications of the builder do not affect the predicate
            boolean players = this.targetPlayers;
            boolean nonPlayers = this.targetNonPlayers;
            Predicate<Entity> other = this.other;
            Predicate<LivingEntity> predicate = entity -> (entity instanceof Player ? players : nonPlayers) && other.test(entity);

            return new FactionPredicate(this.sourceFaction, predicate, this.ignoreDisguise, targets, defaults, this.lookup::getFallbackFaction);
        }
    }

    private static final Supplier<Optional<TagKey<EntityType<?>>>> NO_DEFAULT_TARGETS = Optional::empty;
}
