package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SkillUnlockedCriterionTrigger extends SimpleCriterionTrigger<SkillUnlockedCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull ISkill<?> skill) {
        this.trigger(player, (instance -> instance.matches(skill)));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull ISkill<?> skill) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "player").forGetter(TriggerInstance::player),
                ModRegistries.SKILLS.byNameCodec().fieldOf("skill").forGetter(TriggerInstance::skill)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<SkillUnlockedCriterionTrigger.TriggerInstance> of(@NotNull ISkill<?> skill) {
            return ModAdvancements.TRIGGER_SKILL_UNLOCKED.get().createCriterion(new TriggerInstance(Optional.empty(), skill));
        }

        public boolean matches(@NotNull ISkill<?> skill) {
            return this.skill == skill;
        }
    }
}
