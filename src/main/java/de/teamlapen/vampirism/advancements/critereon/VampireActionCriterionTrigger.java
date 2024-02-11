package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Collection of several vampire related triggers
 */
public class VampireActionCriterionTrigger extends SimpleCriterionTrigger<VampireActionCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, Action action) {
        this.trigger(player, (instance) -> instance.matches(action));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public enum Action implements StringRepresentable {
        SNIPED_IN_BAT("sniped_in_bat"),
        POISONOUS_BITE("poisonous_bite"),
        PERFORM_RITUAL_INFUSION("perform_ritual_infusion"),
        BAT("bat"),
        SUCK_BLOOD("suck_blood"),
        NONE("none"),
        KILL_FROZEN_HUNTER("kill_frozen_hunter"),
        RESURRECT("resurrect");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Action action) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                StringRepresentable.fromEnum(Action::values).fieldOf("action").forGetter(TriggerInstance::action)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<VampireActionCriterionTrigger.TriggerInstance> of(@NotNull Action action) {
            return ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().createCriterion(new TriggerInstance(Optional.empty(), action));
        }


        boolean matches(Action action) {
            return this.action == action;
        }
    }
}
