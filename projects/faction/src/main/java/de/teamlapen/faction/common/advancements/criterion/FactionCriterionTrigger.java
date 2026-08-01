package de.teamlapen.faction.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FactionCriterionTrigger extends SimpleCriterionTrigger<FactionCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, Holder<? extends IPlayableFaction<?>> faction, int level) {
        this.trigger(player, instance -> instance.matches(faction, level));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @Nullable Holder<? extends IPlayableFaction<?>> faction, int level) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                    ModCodecs.playableFaction().optionalFieldOf("faction").forGetter(p -> Optional.ofNullable(p.faction())),
                    ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(TriggerInstance::level)
            ).apply(inst, TriggerInstance::new);
        });

        public static Criterion<FactionCriterionTrigger.TriggerInstance> level(@Nullable Holder<? extends IPlayableFaction<?>> faction, int level) {
            return FactionAdvancements.TRIGGER_FACTION.get().createCriterion(new TriggerInstance(Optional.empty(), faction, level));
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Optional<Holder<? extends IPlayableFaction<?>>> faction, int level) {
            this(player, faction.orElse(null), level);
        }

        public boolean matches(Holder<? extends IFaction<?>> faction, int level) {
            if ((faction == null && this.faction == null) || Objects.equals(this.faction, faction)) {
                return level >= this.level;
            }
            return false;
        }
    }
}
