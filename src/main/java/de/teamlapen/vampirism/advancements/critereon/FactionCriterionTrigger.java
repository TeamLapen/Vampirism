package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.FactionCodec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class FactionCriterionTrigger extends SimpleCriterionTrigger<FactionCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, Holder<? extends IPlayableFaction<?>> faction, int level, int lordLevel) {
        this.trigger(player, instance -> instance.matches(faction, level, lordLevel));
    }

    public void revokeAll(ServerPlayer player) {
        this.revoke(player, instance -> true);
    }

    public void revokeLevel(ServerPlayer player, Holder<? extends IPlayableFaction<?>> faction, Type type, int newLevel) {
        this.revoke(player, instance -> instance.faction == faction && instance.type == type && instance.level > newLevel);
    }

    private void revoke(ServerPlayer player, Predicate<TriggerInstance> instancePredicate) {
//        PlayerAdvancements advancements = player.getAdvancements();
//        ((PlayerAdvancementsAccessor) advancements).getAdvancements().entrySet().stream().filter(entry -> !entry.getValue().isDone()).forEach(advancementProgressEntry -> {
//            AdvancementProgress progress = advancementProgressEntry.getValue();
//            StreamSupport.stream(progress.getCompletedCriteria().spliterator(), false).map(progress::getCriterion).filter(s -> {
//                s.revoke();
//            })
//            if(advancementProgressEntry.getValue().getCompletedCriteria()..getCriteria().values().stream().anyMatch(pair -> {
//                CriterionTriggerInstance trigger = pair.getTrigger();
//                return trigger != null && trigger.getCriterion().equals(FactionCriterionTrigger.ID) && instancePredicate.test(((TriggerInstance) trigger));
//            })) {
//                advancementProgressEntry.getValue().getCompletedCriteria().forEach(a -> advancements.revoke(advancementProgressEntry.getKey(), a));
//            }
//        });
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public enum Type implements StringRepresentable {
        LEVEL("level"),
        LORD("lord");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Type type, @Nullable Holder<? extends IPlayableFaction<?>> faction, int level) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                    StringRepresentable.fromEnum(Type::values).fieldOf("type").forGetter(TriggerInstance::type),
                    FactionCodec.playable().optionalFieldOf("faction").forGetter(p -> Optional.ofNullable(p.faction())),
                    ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(TriggerInstance::level)
            ).apply(inst, TriggerInstance::new);
        });

        public static Criterion<FactionCriterionTrigger.TriggerInstance> level(@Nullable Holder<? extends IPlayableFaction<?>> faction, int level) {
            return ModAdvancements.TRIGGER_FACTION.get().createCriterion(new TriggerInstance(Optional.empty(), Type.LEVEL, faction, level));
        }

        public static Criterion<FactionCriterionTrigger.TriggerInstance> lord(@Nullable Holder<? extends IPlayableFaction<?>> faction, int level) {
            return ModAdvancements.TRIGGER_FACTION.get().createCriterion(new TriggerInstance(Optional.empty(), Type.LORD, faction, level));
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Type type, @NotNull Optional<Holder<? extends IPlayableFaction<?>>> faction, int level) {
            this(player, type, faction.orElse(null), level);
        }

        public boolean matches(Holder<? extends IFaction<?>> faction, int level, int lordLevel) {
            if ((faction == null && this.faction == null) || Objects.equals(this.faction, faction)) {
                if (type == Type.LEVEL) {
                    return level >= this.level;
                } else if (type == Type.LORD) {
                    return lordLevel >= this.level;
                }
            }
            return false;
        }
    }
}
