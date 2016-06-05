package de.teamlapen.vampirism.items;


import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Ammo for the crossbows. Has different subtypes with different base damage/names/special effects.
 */
public class ItemCrossbowArrow extends VampirismItem {

    private static final String regName = "crossbowArrow";

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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        EnumArrowType type = getType(stack);
        if (type != EnumArrowType.NORMAL) {
            tooltip.add(UtilLib.translateToLocal("item.vampirism." + regName + "." + type.name + ".tooltip"));
        }
    }

    /**
     * @param stack        Is copied by {@link EntityCrossbowArrow}
     * @param heightOffset An height offset for the position the entity is created
     * @return An arrow entity at the players position using the given itemstack
     */
    public EntityCrossbowArrow createEntity(ItemStack stack, World world, EntityPlayer player, double heightOffset) {
        EntityCrossbowArrow entity = new EntityCrossbowArrow(world, player, heightOffset, stack);
        EnumArrowType type = getType(stack);
        entity.setDamage(type.baseDamage);
        return entity;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumArrowType type = getType(stack);
        if (type != EnumArrowType.NORMAL) {
            return UtilLib.translateToLocal("item.vampirism." + regName + "." + type.name + ".name");
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (EnumArrowType type : EnumArrowType.values()) {
            subItems.add(setType(new ItemStack(itemIn), type));
        }
    }

    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    public boolean isCanBeInfinite(ItemStack stack) {
        EnumArrowType type = getType(stack);
        return type != EnumArrowType.VAMPIRE_KILLER;
    }
    /**
     * Called when the {@link EntityCrossbowArrow} hits an entity
     *
     * @param arrow          The itemstack of the shot arrow
     * @param entity         The hit entity
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    public void onHitEntity(ItemStack arrow, EntityLivingBase entity, EntityArrow arrowEntity, Entity shootingEntity) {
        EnumArrowType type = getType(arrow);
        if (type == EnumArrowType.VAMPIRE_KILLER) {
            if (entity instanceof IVampireMob) {
                float max = entity.getMaxHealth();
                if (max < Balance.general.ARROW_VAMPIRE_KILLER_MAX_HEALTH) {
                    entity.attackEntityFrom(DamageSource.causeArrowDamage(arrowEntity, shootingEntity), max);
                }
            }
        }
    }

    private enum EnumArrowType {
        NORMAL("normal", 2.0), VAMPIRE_KILLER("vampireKiller", 0.5);
        final String name;
        final double baseDamage;

        EnumArrowType(String name, double baseDamage) {
            this.name = name;
            this.baseDamage = baseDamage;
        }
    }
}
