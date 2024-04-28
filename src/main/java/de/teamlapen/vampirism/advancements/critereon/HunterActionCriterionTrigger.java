package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Collection of several hunter related triggers
 */
public class HunterActionCriterionTrigger extends SimpleCriterionTrigger<HunterActionCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, Action action) {
        this.trigger(player, (instance) -> instance.matches(action));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public enum Action implements StringRepresentable {
        STAKE("stake"),
        NONE("none");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Action action) implements SimpleCriterionTrigger.SimpleInstance  {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "player").forGetter(TriggerInstance::player),
                StringRepresentable.fromEnum(Action::values).fieldOf("action").forGetter(TriggerInstance::action)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<HunterActionCriterionTrigger.TriggerInstance> of(@NotNull Action action) {
            return ModAdvancements.TRIGGER_HUNTER_ACTION.get().createCriterion(new TriggerInstance(Optional.empty(), action));
        }

        boolean matches(Action action) {
            return this.action == action;
        }
    }
}
