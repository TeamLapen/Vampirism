package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TeleportBehavior extends QuarrelBehavior {

    private static final double BACK_GAP = 0.6;

    public TeleportBehavior() {
        super(QuarrelProperties.of(0xFF0B4D42).baseDamage(0).damageMultiplier(0).knockbackMultiplier(0).forcesChunkLoading().effectDescription(Component.translatable("tooltip.vampirism.quarrel_teleport")).build());
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity hitEntity, AbstractArrow arrowEntity, @Nullable Entity shootingEntity) {
        arrowEntity.discard();
        if (!canTeleport(shootingEntity, hitEntity.level())) {
            return;
        }
        float backYaw = hitEntity.yBodyRot;
        Vec3 facing = Vec3.directionFromRotation(0f, backYaw);
        Vec3 destination = findSpotBehind((ServerLevel) shootingEntity.level(), hitEntity, shootingEntity, facing);
        teleport((ServerLevel) shootingEntity.level(), shootingEntity, destination, backYaw);
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction hitDirection) {
        if (!canTeleport(shootingEntity, arrowEntity.level())) {
            return;
        }
        BlockPos teleportPosition = blockPos.relative(hitDirection);
        Vec3 destination = new Vec3(teleportPosition.getX() + 0.5, teleportPosition.getY(), teleportPosition.getZ() + 0.5);
        teleport((ServerLevel) shootingEntity.level(), shootingEntity, destination, shootingEntity.getYRot());
        if (shootingEntity instanceof LivingEntity) {
            DamageHandler.hurtVanilla((ServerLevel) shootingEntity.level(), shootingEntity, DamageSources::fall, 1);
        }
    }

    private static boolean canTeleport(@Nullable Entity shootingEntity, Level impactLevel) {
        return shootingEntity != null && shootingEntity.isAlive() && shootingEntity.level() instanceof ServerLevel && shootingEntity.level() == impactLevel && !(shootingEntity instanceof Player player && player.isSleeping());
    }

    private static Vec3 findSpotBehind(ServerLevel level, LivingEntity target, Entity shooter, Vec3 facing) {
        double reach = target.getBbWidth() / 2.0 + shooter.getBbWidth() / 2.0;
        double[] gaps = {BACK_GAP, 0.0};
        for (double gap : gaps) {
            Vec3 base = target.position().subtract(facing.scale(reach + gap));
            for (int dy = 0; dy <= 1; dy++) {
                Vec3 candidate = base.add(0, dy, 0);
                if (canFit(level, shooter, candidate)) {
                    return candidate;
                }
            }
        }
        return target.position();
    }

    private static boolean canFit(ServerLevel level, Entity shooter, Vec3 feetPosition) {
        AABB box = shooter.getDimensions(shooter.getPose()).makeBoundingBox(feetPosition);
        return level.noCollision(shooter, box) && !level.containsAnyLiquid(box);
    }

    private static void teleport(ServerLevel level, Entity shooter, Vec3 destination, float yaw) {
        Vec3 origin = shooter.position();
        float height = shooter.getBbHeight();

        if (shooter.isPassenger()) {
            shooter.stopRiding();
        }
        shooter.teleportTo(level, destination.x, destination.y, destination.z, Set.of(), yaw, 0f, true);
        shooter.setYHeadRot(yaw);
        shooter.setYBodyRot(yaw);
        shooter.resetFallDistance();

        createTeleportParticles(level, origin, height);
        createTeleportParticles(level, shooter.position(), height);
        level.playSound(null, origin.x, origin.y, origin.z, ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private static void createTeleportParticles(ServerLevel level, Vec3 feet, float height) {
        level.sendParticles(ParticleTypes.PORTAL, feet.x, feet.y + height / 2.0, feet.z, 32, 0.4, height / 2.0, 0.4, 0.5);
    }

    @Override
    public void onChargeTick(ServerLevel level, LivingEntity shooter, ItemStack crossbow, float chargeProgress) {
        if (!(shooter instanceof ServerPlayer serverPlayer)) return;

        RandomSource random = shooter.getRandom();
        double width = shooter.getBbWidth();
        double height = shooter.getBbHeight();

        int count = 1 + (int) (chargeProgress * 2);
        for (int i = 0; i < count; i++) {
            double degree = random.nextDouble() * 360;

            double sin = Math.sin(Math.toRadians(degree));
            double cos = Math.cos(Math.toRadians(degree));

            double x = shooter.getX() + sin * width * 0.7;
            double y = shooter.getY() + random.nextDouble() * height;
            double z = shooter.getZ() + cos * width * 0.7;

            double dirX = sin * 0.06;
            double dirY = 0.2;
            double dirZ = cos * 0.06;

            level.sendParticles(serverPlayer, ParticleTypes.REVERSE_PORTAL, true, true, x, y, z, 0, dirX, dirY, dirZ, 1.0);
        }
    }
}
