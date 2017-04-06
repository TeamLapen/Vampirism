package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

/**
 * Bat which blinds non vampires for a short time.
 */
public class EntityBlindingBat extends EntityBat {
    private boolean restrictLiveSpan;

    public EntityBlindingBat(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.world.checkNoEntityCollision(this.getEntityBoundingBox()) && this.world.collidesWithAnyBlock(this.getEntityBoundingBox()) && !this.world.containsAnyLiquid(this.getEntityBoundingBox());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (restrictLiveSpan && this.ticksExisted > Balance.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.attackEntityFrom(DamageSource.magic, 10F);
        }
        if (!this.world.isRemote) {
            List l = world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox());
            for (Object e : l) {
                if (VampirePlayer.get((EntityPlayer) e).getLevel() == 0) {
                    ((EntityPlayer) e).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, Balance.mobProps.BLINDING_BAT_EFFECT_DURATION));
                }
            }
        }
    }

    public void restrictLiveSpan() {
        this.restrictLiveSpan = true;
    }
}
