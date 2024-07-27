package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public class BleedingEffect extends MobEffect {

    public BleedingEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int j = 5 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.isInvertedHealAndHarm()) {
            return false;
        }

        if (entityLivingBaseIn.getHealth() > 1.0F) {
            DamageHandler.hurtModded(entityLivingBaseIn, ModDamageSources::bleeding, VampirismConfig.BALANCE.bleedingEffectDamage.get().floatValue());
            if (entityLivingBaseIn.getRandom().nextInt(4) == 0) {
                if (Helper.isVampire(entityLivingBaseIn)) {
                    if (entityLivingBaseIn instanceof Player) {
                        VampirePlayer.get(((Player) entityLivingBaseIn)).useBlood(1, true);
                    } else if (entityLivingBaseIn instanceof IVampire) {
                        ((IVampire) entityLivingBaseIn).useBlood(1, true);
                    }
                } else if (entityLivingBaseIn instanceof PathfinderMob) {
                    ExtendedCreature.getSafe(entityLivingBaseIn).ifPresent(creature -> creature.setBlood(creature.getBlood() - 1));
                }
            }
        }
        return true;
    }
}
