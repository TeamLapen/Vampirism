package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;

public interface IEntityCrossbowArrow extends IProjectile {

    double getDamage();

    void setIgnoreHurtTimer();

    void setIsCritical(boolean b);

    void setKnockbackStrength(int k);

    void setFire(int i);

    void setDamage(double d);

    void shoot(EntityPlayer player, float rotationPitch, float rotationYaw, float f, float g, float h);
}
