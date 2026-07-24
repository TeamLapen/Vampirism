package de.teamlapen.vampirism.common.world.entity.player.vampire;

import de.teamlapen.faction.misc.extensions.IEffectInstanceWithSource;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;
import java.util.stream.Stream;

public class InfectionStatus {

    private final LivingEntity entity;
    private int totalTicks;

    public InfectionStatus(LivingEntity entity, int totalTicks) {
        this.entity = entity;
        this.totalTicks = totalTicks;
    }

    public void init() {
        MobEffectInstance effect = this.entity.getEffect(ModEffects.SANGUINARE);
        if (effect == null) {
            this.totalTicks = 0;
        } else {
            this.totalTicks = effect.getDuration();
        }
    }

    public boolean checkStatus() {
        if (this.totalTicks == -1) {
            return false;
        }

        MobEffectInstance effect = this.entity.getEffect(ModEffects.SANGUINARE);
        if (effect == null) {
            return false;
        }

        int duration = effect.getDuration();
        float progress = duration / (float) totalTicks;
        if (duration <= 21) {
            finish();
            return false;
        } else if (progress < 0.5f) {
            addEffectUntilTheEnd(this.entity, MobEffects.HUNGER);

            if (progress < 0.25f) {
                addEffectUntilTheEnd(this.entity, ModEffects.SUN_SENSITIVITY);

                if (progress < 0.05f) {
                    addEffectUntilTheEnd(this.entity, ModEffects.EXPOSED);
                }
            }
        }
        if (this.entity.getRandom().nextFloat() < 0.02f) {
            this.entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120));
        }
        return true;
    }

    private static void addEffectUntilTheEnd(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.getEffect(effect) == null) {
            MobEffectInstance instance = new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION);
            instance.factions$addProperty(ModEffects.SANGUINARE.getId());
            entity.addEffect(instance);
        }
    }

    private void finish() {
        Stream.of(MobEffects.HUNGER, ModEffects.SUN_SENSITIVITY, ModEffects.EXPOSED).forEach(effect -> {
            MobEffectInstance instance = this.entity.getEffect(effect);
            if (instance instanceof IEffectInstanceWithSource withSource && withSource.factions$getProperties().contains(ModEffects.SANGUINARE.getId())) {
                withSource.factions$removeEffect();
            }
        });
        if (this.entity instanceof PathfinderMob) {
            ExtendedCreature.getSafe(this.entity).ifPresent(IExtendedCreatureVampirism::makeVampire);
        }
        if (this.entity instanceof Player player) {
            VampirePlayer.get(player).onSanguinareFinished();
        }
    }

    public static class Factory implements Function<IAttachmentHolder, InfectionStatus> {

        @Override
        public InfectionStatus apply(IAttachmentHolder holder) {
            if (holder instanceof LivingEntity entity) {
                return new InfectionStatus(entity, -1);
            }
            throw new IllegalArgumentException("Cannot create infection status attachment for holder " + holder.getClass() + ". Expected LivingEntity");
        }
    }
}
