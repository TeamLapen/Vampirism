package de.teamlapen.faction.common.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
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

        @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
        public boolean matches(Holder<? extends IAction<?>> action) {
            return this.action.is((Holder)action);
        }

        public static Criterion<TriggerInstance> of(Holder<? extends IAction<?>> action) {
            return FactionAdvancements.ACTION_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), action));
        }
    }
}
