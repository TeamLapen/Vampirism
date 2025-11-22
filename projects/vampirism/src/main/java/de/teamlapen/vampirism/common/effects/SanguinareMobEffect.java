package de.teamlapen.vampirism.common.effects;

import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModParticles;
import de.teamlapen.vampirism.common.entity.player.vampire.InfectionStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
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
        int avgDuration = 20 * (player ? ModConfig.BALANCE.vpSanguinareAverageDuration.get() : BalanceMobProps.mobProps.SANGUINARE_AVG_DURATION);
        if (fasterInfection) {
            avgDuration /= 2;
        }
        int duration = (int) ((entity.getRandom().nextFloat() + 0.5F) * avgDuration);
        entity.addEffect(ModEffectInstanceHelper.createSanguinare(duration));
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
