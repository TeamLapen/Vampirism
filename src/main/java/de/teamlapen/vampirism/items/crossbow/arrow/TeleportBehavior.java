package de.teamlapen.vampirism.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior{
    @Override
    public int color() {
        return 0x0b4d42;
    }

    @Override
    public void onHitBlock(ItemStack arrow, @NotNull BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction up) {
        if (shootingEntity != null) {
            if (!shootingEntity.level().isClientSide && shootingEntity.isAlive()) {
                if (shootingEntity instanceof ServerPlayer player) {
                    if (player.connection.connection.isConnected() && player.level() == arrowEntity.level() && !player.isSleeping()) {

                        if (player.isPassenger()) {
                            player.stopRiding();
                        }

                        player.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        player.fallDistance = 0.0F;
                        DamageHandler.hurtVanilla(player, DamageSources::fall, 1);
                    }
                } else {
                    shootingEntity.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    shootingEntity.fallDistance = 0.0F;
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
        textComponents.add(Component.translatable("item.vampirism.crossbow_arrow_teleport.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeInfinite() {
        return VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }
}
