package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ArmorRegenerationMobEffect extends MobEffect {

    public ArmorRegenerationMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && entity.isAlive()) {
            VampirePlayer.get(player).requestNaturalArmorUpdate();
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 100 == 1;
    }
}
