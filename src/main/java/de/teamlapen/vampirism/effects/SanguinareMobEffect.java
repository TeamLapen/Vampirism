package de.teamlapen.vampirism.effects;

import com.google.common.base.Preconditions;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.player.vampire.InfectionStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SanguinareMobEffect extends VampirismMobEffect {

    public SanguinareMobEffect(MobEffectCategory category, int color) {
        super(category, color, x -> ModParticles.SANGUINARE.get());
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.SANGUINARE.getId(), -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, ModEffects.SANGUINARE.getId(), -4, AttributeModifier.Operation.ADD_VALUE);
    }

    /**
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(LivingEntity entity, boolean player) {
        addRandom(entity, player, false);
    }

    public static void addRandom(LivingEntity entity, boolean player, boolean fasterInfection) {
        int avgDuration = 20 * (player ? VampirismConfig.BALANCE.vpSanguinareAverageDuration.get() : BalanceMobProps.mobProps.SANGUINARE_AVG_DURATION);
        if (fasterInfection) {
            avgDuration /= 2;
        }
        int duration = (int) ((entity.getRandom().nextFloat() + 0.5F) * avgDuration);
        MobEffectInstance effect = new SanguinareEffectInstance(duration);
        Preconditions.checkNotNull(effect);
        entity.addEffect(effect);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        entity.getData(ModAttachments.INFECTION_STATUS).init();
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!entity.isAlive()) return false;
        InfectionStatus data = entity.getData(ModAttachments.INFECTION_STATUS);
        return data.checkStatus();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
