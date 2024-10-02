package de.teamlapen.vampirism.effects;

import com.google.common.base.Preconditions;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class SanguinareEffect extends VampirismEffect {
    /**
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(@NotNull LivingEntity entity, boolean player) {
        addRandom(entity, player, false);
    }

    public static void addRandom(@NotNull LivingEntity entity, boolean player, boolean fasterInfection) {
        int avgDuration = 20 * (player ? VampirismConfig.BALANCE.vpSanguinareAverageDuration.get() : BalanceMobProps.mobProps.SANGUINARE_AVG_DURATION);
        if (fasterInfection) {
            avgDuration /= 2;
        }
        int duration = (int) ((entity.getRandom().nextFloat() + 0.5F) * avgDuration);
        MobEffectInstance effect = new SanguinareEffectInstance(duration);
        Preconditions.checkNotNull(effect);
        if (!VampirismConfig.BALANCE.canCancelSanguinare.get()) {
            effect.getCures().clear();
        }
        entity.addEffect(effect);

    }

    public SanguinareEffect(@NotNull MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, ModEffects.SANGUINARE.getId(), 2.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {
        cures.add(ModItems.GARLIC_CURE);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide || !entity.isAlive()) return true;
        if (entity instanceof PathfinderMob) {
            ExtendedCreature.getSafe(entity).ifPresent(IExtendedCreatureVampirism::makeVampire);
        }
        if (entity instanceof Player player) {
            VampirePlayer.get(player).onSanguinareFinished();
        }
        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == 2;
    }

}
