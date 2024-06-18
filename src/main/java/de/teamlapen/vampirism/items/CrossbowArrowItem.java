package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class CrossbowArrowItem extends ArrowItem implements IVampirismCrossbowArrow<CrossbowArrowEntity> {

    private final EnumArrowType type;


    public CrossbowArrowItem(EnumArrowType type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        switch (this.type) {
            case SPITFIRE -> components.add(Component.translatable("item.vampirism.crossbow_arrow_spitfire.tooltip").withStyle(ChatFormatting.GRAY));
            case VAMPIRE_KILLER -> components.add(Component.translatable("item.vampirism.crossbow_arrow_vampire_killer.tooltip").withStyle(ChatFormatting.GRAY));
            case TELEPORT -> components.add(Component.translatable("item.vampirism.crossbow_arrow_teleport.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }

    @NotNull
    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter, @Nullable ItemStack weapon) {
        CrossbowArrowEntity arrowEntity = new CrossbowArrowEntity(pLevel, pShooter, pStack, weapon);
        arrowEntity.setEffectsFromItem(pStack);
        arrowEntity.setBaseDamage(type.baseDamage * VampirismConfig.BALANCE.crossbowDamageMult.get());
        if (this.type == EnumArrowType.SPITFIRE) {
            arrowEntity.igniteForSeconds(100);
        }
        if (pShooter instanceof Player) {
            arrowEntity.pickup = type == EnumArrowType.NORMAL ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
        }
        return arrowEntity;
    }

    @NotNull
    public AbstractArrow createArrow(@NotNull Level level, @NotNull ItemStack stack, @NotNull Position position, @Nullable ItemStack weapon) {
        CrossbowArrowEntity arrowEntity = new CrossbowArrowEntity(level, position.x(), position.y(), position.z(), stack, weapon);
        arrowEntity.setEffectsFromItem(stack);
        arrowEntity.setBaseDamage(type.baseDamage * VampirismConfig.BALANCE.crossbowDamageMult.get());
        if (this.type == EnumArrowType.SPITFIRE) {
            arrowEntity.igniteForSeconds(100);
        }
        arrowEntity.pickup = type == EnumArrowType.NORMAL ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
        return arrowEntity;
    }

    public EnumArrowType getType() {
        return type;
    }

    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    @Override
    public boolean isCanBeInfinite() {
        return type == EnumArrowType.NORMAL || VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    /**
     * Called when the {@link CrossbowArrowEntity} hits a block
     *
     * @param arrow          The itemstack of the shot arrow
     * @param blockPos       The position of the hit block
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    @Override
    public void onHitBlock(ItemStack arrow, @NotNull BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity) {
        CrossbowArrowEntity entity = (CrossbowArrowEntity) arrowEntity;
        switch (type) {
            case SPITFIRE -> {
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -2; dy < 2; dy++) {
                        for (int dz = -1; dz < 2; dz++) {
                            BlockPos pos = blockPos.offset(dx, dy, dz);
                            BlockState blockState = entity.getCommandSenderWorld().getBlockState(pos);
                            if (blockState.canBeReplaced() && entity.getCommandSenderWorld().getBlockState(pos.below()).isFaceSturdy(entity.getCommandSenderWorld(), pos.below(), Direction.UP) && (entity).getRNG().nextInt(4) != 0) {
                                entity.getCommandSenderWorld().setBlockAndUpdate(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
            case TELEPORT -> {
                if (shootingEntity != null) {
                    if (!shootingEntity.level().isClientSide && shootingEntity.isAlive()) {
                        if (shootingEntity instanceof ServerPlayer player) {
                            if (player.connection.connection.isConnected() && player.level() == entity.level() && !player.isSleeping()) {

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
        }
    }

    /**
     * Called when the {@link CrossbowArrowEntity} hits an entity
     *
     * @param arrow          The itemstack of the shot arrow
     * @param entity         The hit entity
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
        if (type == EnumArrowType.VAMPIRE_KILLER) {
            if (entity instanceof IVampireMob) {
                float max = entity.getMaxHealth();
                if (max < VampirismConfig.BALANCE.arrowVampireKillerMaxHealth.get()) {
                    DamageHandler.hurtVanilla(entity, damageSources -> damageSources.arrow((AbstractArrow) arrowEntity, shootingEntity), max);

                }
            }
        }
    }

    public enum EnumArrowType implements StringRepresentable {
        NORMAL("normal", 2.0, 0xFFFFFFFF),
        VAMPIRE_KILLER("vampire_killer", 0.5, 0xFF7A0073),
        SPITFIRE("spitfire", 0.5, 0xFFFF2211),
        TELEPORT("teleport", 0.5, 0xFF0b4d42);

        public final int color;
        final String name;
        final double baseDamage;

        EnumArrowType(String name, double baseDamage, int color) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.color = color;
        }

        public @NotNull String getName() {
            return this.getSerializedName();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
