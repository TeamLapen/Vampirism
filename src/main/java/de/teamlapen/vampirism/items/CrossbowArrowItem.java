package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class CrossbowArrowItem extends Item implements IVampirismCrossbowArrow<CrossbowArrowEntity> {
    private final EnumArrowType type;


    public CrossbowArrowItem(EnumArrowType type) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable Level world, @Nonnull List<Component> textComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (type != EnumArrowType.NORMAL) {
            textComponents.add(new TranslatableComponent(type == EnumArrowType.VAMPIRE_KILLER ? "item.vampirism.crossbow_arrow_vampire_killer.tooltip" : "item.vampirism.crossbow_arrow_spitfire.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * @param stack        Is copied by {@link CrossbowArrowEntity}
     * @param heightOffset A height offset for the position the entity is created
     * @return An arrow entity at the players position using the given itemstack
     */
    @Override
    public CrossbowArrowEntity createEntity(ItemStack stack, Level world, Player player, double heightOffset, double centerOffset, boolean rightHand) {
        CrossbowArrowEntity entity = CrossbowArrowEntity.createWithShooter(world, player, heightOffset, centerOffset, rightHand, stack);
        entity.setBaseDamage(type.baseDamage * VampirismConfig.BALANCE.crossbowDamageMult.get());
        if (this.type == EnumArrowType.SPITFIRE) {
            entity.setSecondsOnFire(100);
        }
        return entity;
    }

    public EnumArrowType getType() {
        return type;
    }

    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    @Override
    public boolean isCanBeInfinite() {
        return (type != EnumArrowType.VAMPIRE_KILLER && type != EnumArrowType.SPITFIRE) || VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
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
        if (type == EnumArrowType.SPITFIRE) {
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
                    entity.hurt(DamageSource.arrow((AbstractArrow) arrowEntity, shootingEntity), max);
                }
            }
        }
    }


    public enum EnumArrowType implements StringRepresentable {
        NORMAL("normal", 2.0, 0xFFFFFF), VAMPIRE_KILLER("vampire_killer", 0.5, 0x7A0073), SPITFIRE("spitfire", 0.5, 0xFF2211);
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

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
