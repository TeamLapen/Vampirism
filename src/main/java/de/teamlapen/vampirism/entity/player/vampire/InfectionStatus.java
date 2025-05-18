package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

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
        if (duration <= 21) {
            finish();
            return false;
        } else if (duration / (float) totalTicks < 0.5f) {
            if (this.entity.getEffect(MobEffects.HUNGER) == null) {
                MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.HUNGER, MobEffectInstance.INFINITE_DURATION);
                ((EffectInstanceWithSource) mobEffectInstance).setSource(ModEffects.SANGUINARE.getId());
                this.entity.addEffect(mobEffectInstance);
            }
        }
        if (this.entity.getRandom().nextFloat() < 0.02f) {
            this.entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120));
        }
        return true;
    }

    private void finish() {
        MobEffectInstance effect = this.entity.getEffect(MobEffects.HUNGER);
        if (effect instanceof EffectInstanceWithSource withSource && withSource.getSource() == ModEffects.SANGUINARE.getId()) {
            withSource.removeEffect();
        }
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
