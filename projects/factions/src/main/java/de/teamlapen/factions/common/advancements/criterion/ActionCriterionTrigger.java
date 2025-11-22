package de.teamlapen.factions.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.common.core.FactionAdvancements;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionCriterionTrigger extends SimpleCriterionTrigger<ActionCriterionTrigger.TriggerInstance> {

    public void trigger(ServerPlayer player, Holder<? extends IAction<?>> action) {
        this.trigger(player, (instance) -> instance.matches(action));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, Holder<? extends IAction<?>> action) implements SimpleCriterionTrigger.SimpleInstance {

        @SuppressWarnings("unchecked")
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> {
                    return inst.group(
                            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                            ModRegistries.ACTIONS.holderByNameCodec().fieldOf("action").forGetter(o -> (Holder<IAction<?>>) o.action())
                    ).apply(inst, TriggerInstance::new);
                }
        );

        public boolean matches(Holder<? extends IAction<?>> action) {
            return IAction.is(this.action, action);
        }

        public static Criterion<TriggerInstance> of(Holder<? extends IAction<?>> action) {
            return FactionAdvancements.ACTION_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), action));
        }
    }
}
