package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IVampirismCrossbowArrow<T extends EntityArrow & IEntityCrossbowArrow> {

    public T createEntity(ItemStack stack, World world, EntityPlayer player, double heightOffset, double centerOffset, boolean rightHand);

    public boolean isCanBeInfinite(ItemStack stack);

    public boolean isBurning(ItemStack arrow);

    public void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

    public void onHitEntity(ItemStack arrow, EntityLivingBase entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

}
