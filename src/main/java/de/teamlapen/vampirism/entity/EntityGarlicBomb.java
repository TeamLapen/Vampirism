package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.ModPotion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Similar to EntityPotion, but represents a garlic bomb
 */
public class EntityGarlicBomb extends EntityThrowable {


    private final double distance = 4.0D;
    private final double distanceH = 2.0D;
    private final int duration = 100;

    public EntityGarlicBomb(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public EntityGarlicBomb(World p_i1777_1_, EntityLivingBase p_i1777_2_) {
        super(p_i1777_1_, p_i1777_2_);
    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {
        if (!this.worldObj.isRemote) {


            AxisAlignedBB axisalignedbb = this.boundingBox.expand(distance, distanceH, distance);
            List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

            if (list1 != null && !list1.isEmpty()) {
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) iterator.next();
                    double d0 = this.getDistanceSqToEntity(entitylivingbase);

                    if (d0 < (distance * distance)) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entitylivingbase == p_70184_1_.entityHit) {
                                d1 = 1.0D;
                            }

                            int j = (int) (d1 * (double) duration + 0.5D);

                            if (j > 20) {
                                entitylivingbase.addPotionEffect(new PotionEffect(ModPotion.garlic.id, j, 1));
                            }

                    }
                }
            }
        }


        this.worldObj.playAuxSFX(2002, (int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ), 32660);
        this.setDead();

    }
}
