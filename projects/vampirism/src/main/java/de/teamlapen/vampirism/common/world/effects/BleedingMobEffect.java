package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public class BleedingMobEffect extends MobEffect {

    public BleedingMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity.isInvertedHealAndHarm()) return false;
        if (entity.getHealth() > 1.0F) {
            DamageHandler.hurtModded(level, entity, ModDamageSources::bleeding, ModConfig.BALANCE.bleedingEffectDamage.get().floatValue());
            if (entity.getRandom().nextInt(4) == 0) {
                if (Helper.isVampire(entity)) {
                    if (entity instanceof Player) {
                        VampirePlayer.get(((Player) entity)).useBlood(1, true);
                    } else if (entity instanceof IVampire) {
                        ((IVampire) entity).useBlood(1, true);
                    }
                } else if (entity instanceof PathfinderMob) {
                    ExtendedCreature.getSafe(entity).ifPresent(creature -> creature.setBlood(creature.getBlood() - 1));
                }
            }
        }
        return true;
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
}
