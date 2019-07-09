package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class ItemCrossbowArrow extends VampirismItem implements IVampirismCrossbowArrow<EntityCrossbowArrow> {

    private static final String regName = "crossbow_arrow";
    private final EnumArrowType type;


    public ItemCrossbowArrow(EnumArrowType type) {
        super(regName + "_" + type.getName(), new Properties().group(VampirismMod.creativeTab));
        this.type = type;
    }






    /**
     * @param stack        Is copied by {@link EntityCrossbowArrow}
     * @param heightOffset An height offset for the position the entity is created
     * @return An arrow entity at the players position using the given itemstack
     */
    @Override
    public EntityCrossbowArrow createEntity(ItemStack stack, World world, PlayerEntity player, double heightOffset, double centerOffset, boolean rightHand) {
        EntityCrossbowArrow entity = EntityCrossbowArrow.createWithShooter(world, player, heightOffset, centerOffset, rightHand, stack);
        entity.setDamage(type.baseDamage);
        if (this.type == EnumArrowType.SPITFIRE) {
            entity.setFire(100);
        }
        return entity;
    }


    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    @Override
    public boolean isCanBeInfinite() {
        return type != EnumArrowType.VAMPIRE_KILLER && type != EnumArrowType.SPITFIRE;
    }

    /**
     * Called when the {@link EntityCrossbowArrow} hits a block
     *
     * @param arrow          The itemstack of the shot arrow
     * @param blockPos       The position of the hit block
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
        EntityCrossbowArrow entity = (EntityCrossbowArrow) arrowEntity;
        if (type == EnumArrowType.SPITFIRE) {
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -2; dy < 2; dy++) {
                    for (int dz = -1; dz < 2; dz++) {
                        BlockPos pos = blockPos.add(dx, dy, dz);
                        BlockState blockState = entity.getEntityWorld().getBlockState(pos);
                        if (blockState.getMaterial().isReplaceable()
                                && entity.getEntityWorld().getBlockState(pos.down()).isTopSolid() && (entity).getRNG().nextInt(4) != 0) {
                            entity.getEntityWorld().setBlockState(pos, ModBlocks.alchemical_fire.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the {@link EntityCrossbowArrow} hits an entity
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
                if (max < Balance.general.ARROW_VAMPIRE_KILLER_MAX_HEALTH) {
                    entity.attackEntityFrom(DamageSource.causeArrowDamage((AbstractArrowEntity) arrowEntity, shootingEntity), max);
                }
            }
        }
    }

    public EnumArrowType getType() {
        return type;
    }


    public enum EnumArrowType implements IStringSerializable {
        NORMAL("normal", 2.0, 0xFFFFFF), VAMPIRE_KILLER("vampire_killer", 0.5, 0x7A0073), SPITFIRE("spitfire", 0.5, 0xFF2211);
        public final int color;
        final String name;
        final double baseDamage;

        EnumArrowType(String name, double baseDamage, int color) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.color = color;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
