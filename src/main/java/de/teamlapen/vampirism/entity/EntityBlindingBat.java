package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

/**
 * Bat which blinds non vampires for a short time.
 */
public class EntityBlindingBat extends EntityBat {
    public EntityBlindingBat(World worldIn) {
        super(worldIn);
    }

    private boolean restrictLiveSpan;


    @Override
    public void onUpdate() {
        super.onUpdate();
        if (restrictLiveSpan&&this.ticksExisted > Balance.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.attackEntityFrom(DamageSource.magic, 10F);
        }
        if (!this.worldObj.isRemote) {
            List l = worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox());
            for (Object e : l) {
                if (VampirePlayer.get((EntityPlayer) e).getLevel() == 0) {
                    ((EntityPlayer) e).addPotionEffect(new PotionEffect(Potion.blindness.id, Balance.mobProps.BLINDING_BAT_EFFECT_DURATION));
                }
            }
        }
    }

    public void restrictLiveSpan(){
        this.restrictLiveSpan=true;
    }

    @Override public boolean getCanSpawnHere() {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox()) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
    }
}
