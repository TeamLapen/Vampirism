package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;

public interface IEntityCrossbowArrow extends IProjectile {

    void shoot(EntityPlayer player, float rotationPitch, float rotationYaw, float f, float g, float h);

    public void setIgnoreHurtTimer();
}
