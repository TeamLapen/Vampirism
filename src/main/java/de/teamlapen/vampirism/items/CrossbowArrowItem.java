package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class CrossbowArrowItem extends ArrowItem implements IVampirismCrossbowArrow<CrossbowArrowEntity> {

    private final EnumArrowType type;


    public CrossbowArrowItem(EnumArrowType type) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> textComponents, ITooltipFlag tooltipFlag) {
        switch (type) {
            case SPITFIRE:
                textComponents.add(new TranslationTextComponent("item.vampirism.crossbow_arrow_spitfire.tooltip").withStyle(TextFormatting.GRAY));
                break;
            case VAMPIRE_KILLER:
                textComponents.add(new TranslationTextComponent("item.vampirism.crossbow_arrow_vampire_killer.tooltip").withStyle(TextFormatting.GRAY));
                break;
            case TELEPORT:
                textComponents.add(new TranslationTextComponent("item.vampirism.crossbow_arrow_teleport.tooltip").withStyle(TextFormatting.GRAY));
                break;
        }
    }

    /**
     * @param stack        Is copied by {@link CrossbowArrowEntity}
     * @param heightOffset An height offset for the position the entity is created
     * @return An arrow entity at the players position using the given itemstack
     */
    @Deprecated
    @Override
    public CrossbowArrowEntity createEntity(ItemStack stack, World world, PlayerEntity player, double heightOffset, double centerOffset, boolean rightHand) {
        CrossbowArrowEntity entity = CrossbowArrowEntity.createWithShooter(world, player, heightOffset, centerOffset, rightHand, stack);
        entity.setBaseDamage(type.baseDamage * VampirismConfig.BALANCE.crossbowDamageMult.get());
        if (this.type == EnumArrowType.SPITFIRE) {
            entity.setSecondsOnFire(100);
        }
        return entity;
    }

    @Nonnull
    @Override
    public AbstractArrowEntity createArrow(@Nonnull World level, @Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        CrossbowArrowEntity arrowEntity = new CrossbowArrowEntity(level, entity, stack);
        arrowEntity.setEffectsFromItem(stack);
        arrowEntity.setBaseDamage(type.baseDamage * VampirismConfig.BALANCE.crossbowDamageMult.get());
        if (this.type == EnumArrowType.SPITFIRE) {
            arrowEntity.setSecondsOnFire(100);
        }
        arrowEntity.pickup = type == EnumArrowType.NORMAL ? AbstractArrowEntity.PickupStatus.ALLOWED : AbstractArrowEntity.PickupStatus.DISALLOWED;
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
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
        CrossbowArrowEntity entity = (CrossbowArrowEntity) arrowEntity;
        switch (type){
            case SPITFIRE:
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -2; dy < 2; dy++) {
                        for (int dz = -1; dz < 2; dz++) {
                            BlockPos pos = blockPos.offset(dx, dy, dz);
                            BlockState blockState = entity.getCommandSenderWorld().getBlockState(pos);
                            if (blockState.getMaterial().isReplaceable()
                                    && entity.getCommandSenderWorld().getBlockState(pos.below()).isFaceSturdy(entity.getCommandSenderWorld(), pos.below(), Direction.UP) && (entity).getRNG().nextInt(4) != 0) {
                                entity.getCommandSenderWorld().setBlockAndUpdate(pos, ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState());
                            }
                        }
                    }
                }
                break;
            case TELEPORT:
                if (!shootingEntity.level.isClientSide && shootingEntity.isAlive()) {
                    if (shootingEntity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = ((ServerPlayerEntity) shootingEntity);
                        if (player.connection.getConnection().isConnected() && player.level == entity.level && !player.isSleeping()) {

                            if (player.isPassenger()) {
                                player.stopRiding();
                            }

                            player.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            player.fallDistance = 0.0F;
                            player.hurt(DamageSource.FALL, 1);
                        }
                    } else if (shootingEntity != null) {
                        shootingEntity.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        shootingEntity.fallDistance = 0.0F;
                    }
                }
                break;
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
                    entity.hurt(DamageSource.arrow((AbstractArrowEntity) arrowEntity, shootingEntity), max);
                }
            }
        }
    }


    public enum EnumArrowType implements IStringSerializable {
        NORMAL("normal", 2.0, 0xFFFFFF),
        VAMPIRE_KILLER("vampire_killer", 0.5, 0x7A0073),
        SPITFIRE("spitfire", 0.5, 0xFF2211),
        TELEPORT("teleport", 0.5, 0x0b4d42);

        public final int color;
        final String name;
        final double baseDamage;

        EnumArrowType(String name, double baseDamage, int color) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.color = color;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
