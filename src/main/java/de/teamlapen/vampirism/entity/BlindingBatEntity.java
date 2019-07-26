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

    public static boolean spawnPredicate(EntityType<? extends BlindingBatEntity> entityType, IWorld iWorld, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        } else {
            int i = iWorld.getLight(blockPos);
            int j = 4;
            if (random.nextBoolean())
                return false;

            return i > random.nextInt(j) ? false : func_223315_a(entityType, iWorld, spawnReason, blockPos, random);
        }
    }
}
