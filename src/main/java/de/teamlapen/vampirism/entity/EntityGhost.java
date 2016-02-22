package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Entity Ghost
 */
public class EntityGhost extends EntityVampirism implements IMob {
    public EntityGhost(World p_i1595_1_) {
        super(p_i1595_1_);
        ((PathNavigateGround) getNavigator()).setCanSwim(true);
        this.setSize(0.8F, 2.0F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
        this.tasks.addTask(7, new EntityAIWander(this, 0.9F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 16));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
    }

    /**
     * Entity becomes invisible (5 sec) after being damaged.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float par2) {
        if (!super.attackEntityFrom(source, par2)) {
            return false;
        } else {
            addPotionEffect(new PotionEffect(Potion.invisibility.id, 20 * 5, 1));
        }
        return true;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.1F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Balance.mobProps.GHOST_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Balance.mobProps.GHOST_FOLLOW_RANGE);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(Balance.mobProps.GHOST_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Balance.mobProps.GHOST_HEALTH);
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        //TODO drop something
    }

    /**
     * Ghost do not make any step sounds
     */
    @Override
    protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
        return;
    }

}
