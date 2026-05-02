package de.teamlapen.vampirism.common.advancements.critereon;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class SerumInjectedCriterionTrigger extends SimpleCriterionTrigger<SerumInjectedCriterionTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, PotionContents potionContents) {
        this.trigger(player, instance -> instance.matches(potionContents));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<TagKey<MobEffect>> effect) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<SerumInjectedCriterionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SerumInjectedCriterionTrigger.TriggerInstance::player),
                                TagKey.codec(Registries.MOB_EFFECT).optionalFieldOf("effect").forGetter(SerumInjectedCriterionTrigger.TriggerInstance::effect)
                        )
                        .apply(instance, SerumInjectedCriterionTrigger.TriggerInstance::new)
        );

        public static Criterion<SerumInjectedCriterionTrigger.TriggerInstance> injectedSerum(TagKey<MobEffect> effect) {
            return ModAdvancements.TRIGGER_SERUM_INJECTED.get()
                    .createCriterion(
                            new TriggerInstance(Optional.empty(), Optional.of(effect))
                    );
        }

        public static Criterion<SerumInjectedCriterionTrigger.TriggerInstance> injectedSerumAny() {
            return ModAdvancements.TRIGGER_SERUM_INJECTED.get()
                    .createCriterion(
                            new TriggerInstance(Optional.empty(), Optional.empty())
                    );
        }

        public boolean matches(PotionContents potionContents) {
            if (this.effect.isEmpty()) return true;
            TagKey<MobEffect> tag = this.effect.get();;
            return Streams.stream(potionContents.getAllEffects()).anyMatch(instance -> instance.getEffect().is(tag));
        }
    }
}
