package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Bat which blinds non vampires for a short time.
 */
public class BlindingBatEntity extends BatEntity {
    private boolean restrictLiveSpan;

    public BlindingBatEntity(EntityType<? extends BlindingBatEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return worldIn.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox())) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox()) && !worldIn.containsAnyLiquid(this.getBoundingBox()); //TODO eventually dublicated check (isCollisionBoxesEmpty
    }

    @Override
    public void tick() {
        super.tick();
        if (restrictLiveSpan && this.ticksExisted > Balance.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.attackEntityFrom(DamageSource.MAGIC, 10F);
        }
        if (!this.world.isRemote) {
            List l = world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox());
            for (Object e : l) {
                if (VampirePlayer.get((PlayerEntity) e).getLevel() == 0) {
                    ((PlayerEntity) e).addPotionEffect(new EffectInstance(Effects.BLINDNESS, Balance.mobProps.BLINDING_BAT_EFFECT_DURATION));
                }
            }
        }
    }

    public void restrictLiveSpan() {
        this.restrictLiveSpan = true;
    }

    public static boolean spawnPredicate(EntityType<? extends BlindingBatEntity> p_223369_0_, IWorld p_223369_1_, SpawnReason p_223369_2_, BlockPos p_223369_3_, Random p_223369_4_) {
        if (p_223369_3_.getY() >= p_223369_1_.getSeaLevel()) {
            return false;
        } else {
            int i = p_223369_1_.getLight(p_223369_3_);
            int j = 4;
            if (p_223369_4_.nextBoolean())
                return false;

            return i > p_223369_4_.nextInt(j) ? false : func_223315_a(p_223369_0_, p_223369_1_, p_223369_2_, p_223369_3_, p_223369_4_);
        }
    }
}
