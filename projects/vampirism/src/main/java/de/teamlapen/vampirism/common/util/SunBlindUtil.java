package de.teamlapen.vampirism.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class SunBlindUtil {

    private static final float LOOK_THRESHOLD = 0.9f;
    private static final double RAY_MAX_DISTANCE = 128;
    private static final float RAMP_UP_SPEED = 0.5f;
    private static final float FADE_OUT_SPEED = 0.08f;

    public static float update(float intensity, float target) {
        float speed = target > intensity ? RAMP_UP_SPEED : FADE_OUT_SPEED;
        return Mth.approach(intensity, target, speed);
    }

    public static float computeTarget(Player player) {
        Level world = player.level();
        if (!Helper.hasLevelSunDamage(world, player.blockPosition())) return 0f;

        Vec3 sunDirection = sunDirection(world, player.blockPosition());
        if (sunDirection.y <= 0) return 0f;

        double lookAlignment = player.getViewVector(1f).dot(sunDirection);
        if (lookAlignment <= LOOK_THRESHOLD || !isSunVisible(world, player, sunDirection)) return 0f;

        return (float) Mth.inverseLerp(lookAlignment, LOOK_THRESHOLD, 1.0);
    }

    private static Vec3 sunDirection(Level world, BlockPos pos) {
        float sunAngleDegrees = world.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, pos);
        double sunAngleRadians = Math.toRadians(sunAngleDegrees);
        return new Vec3(-Math.sin(sunAngleRadians), Math.cos(sunAngleRadians), 0);
    }

    private static boolean isSunVisible(Level world, Player player, Vec3 sunDirection) {
        Vec3 eyePosition = player.getEyePosition();
        double distanceToSkyTop = world.getMaxY() + 1 - eyePosition.y;
        if (distanceToSkyTop <= 0) return true;

        double rayLength = Math.min(RAY_MAX_DISTANCE, distanceToSkyTop / sunDirection.y);
        Vec3 rayEnd = eyePosition.add(sunDirection.scale(rayLength));
        BlockHitResult hit = world.clip(new ClipContext(eyePosition, rayEnd, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS;
    }
}
