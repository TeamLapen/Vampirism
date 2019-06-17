package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Entity Ghost
 */
public class EntityGhost extends EntityVampirism implements IMob {
    public EntityGhost(World worldIn) {
        super(ModEntities.ghost, worldIn);
        ((PathNavigateGround) getNavigator()).setCanSwim(true);
        this.setSize(0.8F, 1.95F);
        this.experienceValue = 8;

    }

    /**
     * Entity becomes invisible (5 sec) after being damaged.
     */
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2) {
        if (!super.attackEntityFrom(damageSource, par2)) {
            return false;
        } else {
            if (damageSource.getTrueSource() != null && !this.equals(damageSource.getTrueSource())) {
                addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 5, 1));
            }
        }
        return true;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.1F;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.GHOST_ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(Balance.mobProps.GHOST_FOLLOW_RANGE);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.GHOST_SPEED);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.GHOST_HEALTH);
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.GHOST;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(7, new EntityAIWander(this, 0.9F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 16));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
    }

    /**
     * Ghost do not make any step sounds
     */
    @Override
    protected void playStepSound(BlockPos pos, IBlockState blockIn) {
        return;
    }
}
