package de.teamlapen.faction.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MinionTaskCriterionTrigger extends SimpleCriterionTrigger<MinionTaskCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, IMinionTask<?, ?> task) {
        this.trigger(player, instance -> instance.matches(task));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull IMinionTask<?, ?> task) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ModRegistries.MINION_TASKS.byNameCodec().fieldOf("task").forGetter(TriggerInstance::task)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<MinionTaskCriterionTrigger.TriggerInstance> tasks(@NotNull IMinionTask<?, ?> task) {
            return FactionAdvancements.TRIGGER_MINION_ACTION.get().createCriterion(new TriggerInstance(Optional.empty(), task));
        }

        boolean matches(IMinionTask<?, ?> action) {
            return this.task == action;
        }

    }
}
