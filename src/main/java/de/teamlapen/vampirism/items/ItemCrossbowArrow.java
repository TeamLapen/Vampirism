package de.teamlapen.vampirism.items;


import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class ItemCrossbowArrow extends VampirismItem implements IVampirismCrossbowArrow<EntityCrossbowArrow> {

    private static final String regName = "crossbow_arrow";

    /**
     * @return The {@link EnumArrowType} of this stack
     */
    public static
    @Nonnull
    EnumArrowType getType(@Nonnull ItemStack stack) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("type")) {
                String type = stack.getTagCompound().getString("type");
                for (EnumArrowType enumType : EnumArrowType.values()) {
                    if (enumType.name.equals(type)) {
                        return enumType;
                    }
                }
            }
        }
        return EnumArrowType.NORMAL;
    }

    /**
     * Set's the {@link EnumArrowType} of the stack
     *
     * @return The same stack
     */
    public static
    @Nonnull
    ItemStack setType(@Nonnull ItemStack stack, EnumArrowType type) {
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        nbt.setString("type", type.name);
        stack.setTagCompound(nbt);
        return stack;
    }


    public ItemCrossbowArrow() {
        super(regName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        EnumArrowType type = getType(stack);
        if (type != EnumArrowType.NORMAL) {
            tooltip.add(UtilLib.translate("item.vampirism." + regName + "." + type.name + ".tooltip"));
        }
    }


    /**
     * @param stack        Is copied by {@link EntityCrossbowArrow}
     * @param heightOffset An height offset for the position the entity is created
     * @return An arrow entity at the players position using the given itemstack
     */
    @Override
    public EntityCrossbowArrow createEntity(ItemStack stack, World world, EntityPlayer player, double heightOffset, double centerOffset, boolean rightHand) {
        EntityCrossbowArrow entity = EntityCrossbowArrow.createWithShooter(world, player, heightOffset, centerOffset, rightHand, stack);
        EnumArrowType type = getType(stack);
        entity.setDamage(type.baseDamage);
        if (type == EnumArrowType.SPITFIRE) {
            entity.setFire(100);
        }
        return entity;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumArrowType type = getType(stack);
        if (type != EnumArrowType.NORMAL) {
            return UtilLib.translate("item.vampirism." + regName + "." + type.name + ".name");
        }
        return super.getItemStackDisplayName(stack);
    }

    /**
     * @param type
     * @return A stack of this item with the given tier
     */
    public ItemStack getStack(EnumArrowType type) {
        return setType(new ItemStack(this), type);
    }


    @Override
    public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (EnumArrowType type : EnumArrowType.values()) {
                items.add(setType(new ItemStack(this), type));
            }
        }
    }

    /**
     * @param arrow Arrow stack
     * @return If the arrow entity that belongs to this arrow should be burning
     */
    @Override
    public boolean isBurning(ItemStack arrow) {
        EnumArrowType type = getType(arrow);
        return type == EnumArrowType.SPITFIRE;
    }

    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    @Override
    public boolean isCanBeInfinite(ItemStack stack) {
        EnumArrowType type = getType(stack);
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
        EnumArrowType type = getType(arrow);
        if (type == EnumArrowType.SPITFIRE) {
            for (int dx = -1; dx < 2; dx++) {
                for (int dy = -2; dy < 2; dy++) {
                    for (int dz = -1; dz < 2; dz++) {
                        BlockPos pos = blockPos.add(dx, dy, dz);
                        if (((entity).getEntityWorld().getBlockState(pos).getMaterial() == Material.AIR || (entity).getEntityWorld().getBlockState(pos).getBlock().isReplaceable((entity).getEntityWorld(), pos))
                                && (entity).getEntityWorld().getBlockState(pos.down()).isFullBlock() && (entity).getRNG().nextInt(4) != 0) {
                            (entity).getEntityWorld().setBlockState(pos, ModBlocks.alchemical_fire.getDefaultState());
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
    public void onHitEntity(ItemStack arrow, EntityLivingBase entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
        EnumArrowType type = getType(arrow);
        if (type == EnumArrowType.VAMPIRE_KILLER) {
            if (entity instanceof IVampireMob) {
                float max = entity.getMaxHealth();
                if (max < Balance.general.ARROW_VAMPIRE_KILLER_MAX_HEALTH) {
                    entity.attackEntityFrom(DamageSource.causeArrowDamage((EntityArrow) arrowEntity, shootingEntity), max);
                }
            }
        }
    }

    public enum EnumArrowType {
        NORMAL("normal", 2.0, 0xFFFFFF), VAMPIRE_KILLER("vampire_killer", 0.5, 0x7A0073), SPITFIRE("spitfire", 0.5, 0xFF2211);
        public final int color;
        final String name;
        final double baseDamage;

        EnumArrowType(String name, double baseDamage, int color) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.color = color;
        }
    }
}
