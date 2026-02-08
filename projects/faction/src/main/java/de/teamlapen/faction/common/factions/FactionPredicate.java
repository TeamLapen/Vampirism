package de.teamlapen.faction.common.factions;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.DefaultFactions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

// TODO add default target factions based on tags. -> hunter ignore neutral by default. better compatibility between additional factions -> maybe use FRIENDLY_TOWARDS_NEUTRAL
public record FactionPredicate(@Nullable Holder<? extends IFaction<?>> viewedFaction, Predicate<LivingEntity> predicate, boolean ignoreDisguise, HolderSet<IFaction<?>> targetFaction, Function<LivingEntity, Holder<? extends IFaction<?>>> factionFallback) implements IFactionPredicate {

    @Override
    public boolean test(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null) return false;
        if (!predicate.test(livingEntity)) return false;

        return switch (livingEntity) {
            case IFactionEntity iFactionEntity -> this.targetFaction.contains(SafeCast.cast(iFactionEntity.getFaction()));
            case Player player -> {
                FactionPlayerHandler handler = FactionPlayerHandler.get(player);
                Holder<? extends IFaction<?>> faction = handler.getFaction();
                var viewedFaction = handler.getCurrentFactionPlayer().map(IFactionPlayer::getDisguise).map(x -> x.getViewedFaction(this.viewedFaction)).orElse(null);
                if (viewedFaction != null) {
                    faction = viewedFaction;
                }
                yield this.targetFaction.contains(SafeCast.cast(faction));
            }
            default -> this.targetFaction.contains(SafeCast.cast(factionFallback.apply(livingEntity)));
        };
    }

    @Override
    public boolean test(LivingEntity entity, ServerLevel level) {
        return test(entity);
    }

    public interface FactionPredicateLookup {

        @Nullable
        IFactionPredicate getExisting(Builder builder);

        void update(Builder builder, FactionPredicate predicate);

        Registry<IFaction<?>> registry();

        Holder<? extends IFaction<?>> getFallbackFaction(LivingEntity livingEntity);
    }

    public static class Builder implements IFactionPredicate.Builder {

        @Nullable
        private final Holder<? extends IFaction<?>> sourceFaction;
        private final FactionPredicateLookup lookup;
        private boolean targetPlayers = true;
        private boolean targetNonPlayers = true;
        private boolean ignoreDisguise;
        private boolean allowOwnFaction;
        @Nullable
        private TagKey<IFaction<?>> targetFaction = null;
        private final List<Holder<? extends IFaction<?>>> targetFactions = new ArrayList<>();
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
        public IFactionPredicate.Builder allowOwnFaction() {
            this.allowOwnFaction = true;
            return this;
        }

        @Override
        public IFactionPredicate.Builder notNeutral() {
            return targetFaction(FactionTags.NOT_NEUTRAL);
        }

        @Override
        public Builder targetFaction(Holder<? extends IFaction<?>> targetFaction) {
            Preconditions.checkArgument(this.targetFaction == null, "Cannot use both target Holder and target TagKey at the same time!");
            this.targetFactions.add(targetFaction);
            return this;
        }

        @Override
        public Builder targetFaction(TagKey<IFaction<?>> targetFaction) {
            Preconditions.checkArgument(targetFactions.isEmpty(), "Cannot use both target Holder and target TagKey at the same time!");
            this.targetFaction = targetFaction;
            return this;
        }

        @Override
        public IFactionPredicate.Builder and(Predicate<Entity> other) {
            this.other = other;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            Builder build = (Builder) o;
            return targetPlayers == build.targetPlayers
                    && targetNonPlayers == build.targetNonPlayers
                    && ignoreDisguise == build.ignoreDisguise
                    && allowOwnFaction == build.allowOwnFaction
                    && Objects.equals(sourceFaction, build.sourceFaction)
                    && Objects.equals(targetFaction, build.targetFaction)
                    && targetFactions.equals(build.targetFactions)
                    && other.equals(build.other);
        }

        @Override
        public int hashCode() {
            int result = sourceFaction == null ? 0 : sourceFaction.getKey().identifier().hashCode();
            result = result << 1 | (targetPlayers ? 0b1: 0b0);
            result = result << 1 | (targetNonPlayers ? 0b1: 0b0);
            result = result << 1 | (ignoreDisguise ? 0b1: 0b0);
            result = result << 1 | (allowOwnFaction ? 0b1: 0b0);
            result = 31 * result + (targetFaction == null ? 0 : Objects.hashCode(targetFaction.location()));
            result = 31 * result + targetFactions.hashCode();
            result = 31 * result + other.hashCode();
            return result;
        }

        @Override
        public IFactionPredicate build() {
            if (targetFaction == null && this.targetFactions.isEmpty()) {
                targetFaction = FactionTags.ALL_FACTIONS;
            }

            IFactionPredicate existing = this.lookup.getExisting(this);
            if (existing != null) return existing;

            Registry<IFaction<?>> iFactions = this.lookup.registry();
            HolderSet<IFaction<?>> holderSet;
            if (this.targetFaction != null) {
                holderSet = iFactions.getOrThrow(this.targetFaction);
            } else {
                holderSet = HolderSet.direct(SafeCast.<List<? extends Holder<IFaction<?>>>>cast(this.targetFactions));
            }

            if (this.sourceFaction != null) {
                List<Holder<? extends IFaction<?>>> factions = new ArrayList<>();
                if (!this.allowOwnFaction) {
                    factions.add(this.sourceFaction);
                }

                if (!IFaction.is(this.sourceFaction, FactionTags.HOSTILE_TOWARDS_NEUTRAL)) {
                    factions.add(DefaultFactions.NEUTRAL);
                }

                if (!factions.isEmpty()) {
                    holderSet = new AndHolderSet<>(holderSet, new NotHolderSet<>(iFactions, HolderSet.direct(SafeCast.<List<? extends Holder<IFaction<?>>>>cast(factions))));
                }
            }

            var pred = new Predicate<LivingEntity>() {

                @Override
                public boolean test(LivingEntity livingEntity) {
                    return (targetPlayers && livingEntity instanceof Player) || targetNonPlayers;
                }
            };

            var predicate = new FactionPredicate(this.sourceFaction, pred.and(other), this.ignoreDisguise, holderSet, this.lookup::getFallbackFaction);
            this.lookup.update(this, predicate);
            return predicate;
        }
    }

}
