package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Entity Ghost
 */
public class GhostEntity extends VampirismEntity implements IMob {
    public GhostEntity(EntityType<? extends GhostEntity> type, World worldIn) {
        super(type, worldIn);
        getNavigator().setCanSwim(true);
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
                addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 20 * 5, 1));
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 0.9F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 16));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 0, true, false, null));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, true));
    }

    /**
     * Ghost do not make any step sounds
     */
    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        return;
    }
}
