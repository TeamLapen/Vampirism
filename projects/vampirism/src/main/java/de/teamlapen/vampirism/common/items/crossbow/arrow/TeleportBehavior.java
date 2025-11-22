package de.teamlapen.vampirism.common.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TeleportBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {

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
    public void appendHoverText(ItemStack itemStack, @Nullable Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> textComponents, TooltipFlag tooltipFlag) {
        textComponents.accept(Component.translatable("item.vampirism.crossbow_arrow_teleport.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeInfinite() {
        return ModConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }
}
