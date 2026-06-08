package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TeleportBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 0xFF0b4d42;
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction hitDirection) {
        if (shootingEntity != null) {
            if (shootingEntity.level() instanceof ServerLevel level && shootingEntity.isAlive()) {
                BlockPos teleportPosition = blockPos.relative(hitDirection);
                if (shootingEntity instanceof ServerPlayer player) {
                    if (player.connection.getConnection().isConnected() && player.level() == arrowEntity.level() && !player.isSleeping()) {

                        if (player.isPassenger()) {
                            player.stopRiding();
                        }

                        player.teleportTo(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
                        player.fallDistance = 0.0F;
                        DamageHandler.hurtVanilla(level, player, DamageSources::fall, 1);
                    }
                } else {
                    shootingEntity.teleportTo(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
                    shootingEntity.fallDistance = 0.0F;
                }
            }
        }
    }

    @Override
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_teleport");
    }

    @Override
    public boolean canBeInfinite() {
        return ModConfig.balance().allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }
}
