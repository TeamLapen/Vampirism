package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.common.particles.WhispersOfTheVeilParticleOptions;
import de.teamlapen.vampirism.common.tags.ModStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WhispersOfTheVeilMobEffect extends SimpleMobEffect {

    public WhispersOfTheVeilMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B0000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 5 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) return false;
        BlockPos target = level.findNearestMapStructure(ModStructureTags.VELMORRA_PORTAL, entity.blockPosition(), 100, false);
        if (target == null) return true;

        RandomSource random = entity.getRandom();
        var xPos = entity.getX() - 5 + random.nextFloat() * 10;
        var yPos = entity.getY() - 5 + random.nextFloat() * 10;
        var zPos = entity.getZ() - 5 + random.nextFloat() * 10;

        level.sendParticles(player, new WhispersOfTheVeilParticleOptions(120, target.getX(), target.getY(), target.getZ(), 0.1f),false,true, xPos,yPos,zPos, 1, 1,1,1,0.2);

        return true;
    }
}
