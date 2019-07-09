package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IVampirismCrossbowArrow<T extends AbstractArrowEntity & IEntityCrossbowArrow> {

    T createEntity(ItemStack stack, World world, PlayerEntity player, double heightOffset, double centerOffset, boolean rightHand);

    boolean isCanBeInfinite();

    void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

    void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

}
