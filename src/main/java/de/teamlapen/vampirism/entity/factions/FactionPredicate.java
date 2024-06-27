package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FactionPredicate(Holder<? extends IFaction<?>> sourceFaction, boolean player, boolean nonPlayer, boolean neutral, boolean ignoreDisguise, Optional<Holder<? extends IFaction<?>>> targetFaction) implements Predicate<LivingEntity> {

    @Override
    public boolean apply(@Nullable LivingEntity input) {
        switch (input) {
            case null -> {
                return false;
            }
            case IFactionEntity iFactionEntity when nonPlayer -> {
                Holder<? extends IFaction<?>> other = iFactionEntity.getFaction();
                return !IFaction.is(sourceFaction, other) && (targetFaction.isEmpty() || IFaction.is(targetFaction.get(), other));
            }
            case Player player1 when player && input.isAlive() -> {
                return FactionPlayerHandler.getCurrentFactionPlayer(player1).map(fp -> {
                            Holder<? extends IFaction<?>> f = fp.getDisguise().getViewedFaction(sourceFaction, ignoreDisguise);
                            return (f != null || (IFaction.is(sourceFaction, ModFactionTags.HOSTILE_TOWARDS_NEUTRAL) && neutral)) && !sourceFaction.equals(f) && (targetFaction.isEmpty() || (f != null && IFaction.is(targetFaction.get(), f)));
                        }
                ).orElse(neutral);
            }
            default -> {
            }
        }
        return false;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Builder {

        private final Holder<? extends IFaction<?>> sourceFaction;
        private boolean player;
        private boolean nonPlayer;
        private boolean neutral;
        private boolean ignoreDisguise;
        private Optional<Holder<? extends IFaction<?>>> targetFaction = Optional.empty();

        public Builder(Holder<? extends IFaction<?>> sourceFaction) {
            this.sourceFaction = sourceFaction;
        }

        public Builder player() {
            this.player = true;
            return this;
        }

        public Builder player(boolean player) {
            this.player = player;
            return this;
        }

        public Builder nonPlayer() {
            this.nonPlayer = false;
            return this;
        }

        public Builder nonPlayer(boolean nonPlayer) {
            this.nonPlayer = nonPlayer;
            return this;
        }

        public Builder neutral() {
            this.neutral = true;
            return this;
        }

        public Builder neutral(boolean neutral) {
            this.neutral = neutral;
            return this;
        }

        public Builder ignoreDisguise() {
            this.ignoreDisguise = true;
            return this;
        }

        public Builder ignoreDisguise(boolean ignoreDisguise) {
            this.ignoreDisguise = ignoreDisguise;
            return this;
        }

        public Builder targetFaction(@Nullable Holder<? extends IFaction<?>> targetFaction) {
            this.targetFaction = Optional.ofNullable(targetFaction);
            return this;
        }

        public FactionPredicate build() {
            return new FactionPredicate(sourceFaction, player, nonPlayer, neutral, ignoreDisguise, targetFaction);
        }
    }
}
