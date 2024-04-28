package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CuredVampireVillagerCriterionTrigger extends SimpleCriterionTrigger<CuredVampireVillagerCriterionTrigger.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull Entity vampire, @NotNull Villager villager) {
        LootContext lootcontext = EntityPredicate.createContext(player, vampire);
        LootContext lootcontext1 = EntityPredicate.createContext(player, villager);
        this.trigger(player, (instance) -> instance.matches(lootcontext, lootcontext1));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Optional<ContextAwarePredicate> vampire, @NotNull Optional<ContextAwarePredicate> villager) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "player").forGetter(TriggerInstance::player),
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "vampire").forGetter(TriggerInstance::vampire),
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "villager").forGetter(TriggerInstance::villager)
        ).apply(inst, TriggerInstance::new));

        private TriggerInstance() {
            this(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static @NotNull Criterion<CuredVampireVillagerCriterionTrigger.TriggerInstance> any() {
            return ModAdvancements.TRIGGER_CURED_VAMPIRE_VILLAGER.get().createCriterion(new TriggerInstance());
        }

        public boolean matches(@NotNull LootContext vampire, @NotNull LootContext villager) {
            if (this.vampire.map(s -> !s.matches(vampire)).orElse(false)) {
                return false;
            } else {
                return this.villager.map(s -> s.matches(villager)).orElse(true);
            }
        }
    }
}
